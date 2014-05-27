package jscover.maven;

import jscover.filesystem.ConfigurationForFS;
import jscover.report.ConfigurationForReport;
import jscover.report.Main;
import jscover.util.IoUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;

import static java.lang.String.format;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;

public class FileTestRunner {
    protected WebDriver webClient;
    private ConfigurationForFS config;
    private WebDriverRunner webDriverRunner;
    private int lineCoverageMinimum;
    private int branchCoverageMinimum;
    private int functionCoverageMinimum;
    private final boolean reportLCOV;
    private final boolean reportCoberturaXML;
    private IoUtils ioUtils = IoUtils.getInstance();

    public FileTestRunner(WebDriver webClient, WebDriverRunner webDriverRunner, ConfigurationForFS config, int lineCoverageMinimum, int branchCoverageMinimum, int functionCoverageMinimum, boolean reportLCOV, boolean reportCoberturaXML) {
        this.webClient = webClient;
        this.webDriverRunner = webDriverRunner;
        this.config = config;
        this.lineCoverageMinimum = lineCoverageMinimum;
        this.branchCoverageMinimum = branchCoverageMinimum;
        this.functionCoverageMinimum = functionCoverageMinimum;
        this.reportLCOV = reportLCOV;
        this.reportCoberturaXML = reportCoberturaXML;
    }

    public void runTests(List<File> testPages) throws MojoFailureException, MojoExecutionException {
        File jsonFile = new File(config.getDestDir() + "/jscoverage.json");
        if (jsonFile.exists())
            jsonFile.delete();
        if (config.isLocalStorage()) {
            for (File testPage : testPages)
                runTestLocalStorage(ioUtils.getRelativePath(testPage, config.getDestDir()));
            String json = (String)((JavascriptExecutor) webClient).executeScript("return jscoverage_serializeCoverageToJSON();");
            ioUtils.copy(json, new File(config.getDestDir(), "jscoverage.json"));
            String js = ioUtils.loadFromFileSystem(new File(config.getDestDir(), "jscoverage.js"));
            ioUtils.copy(js + "\njscoverage_isReport = true;", new File(config.getDestDir(), "jscoverage.js"));
            webClient.get("file:///" + new File(config.getDestDir(), "jscoverage.html").getAbsolutePath().replaceAll("\\\\","/"));
        } else {
            webClient.get("file:///" + new File(config.getDestDir(), "jscoverage.html").getAbsolutePath().replaceAll("\\\\","/"));
            for (File testPage : testPages) {
                runTestInFrames(ioUtils.getRelativePath(testPage, config.getDestDir()));
            }
            saveCoverageData();
        }
        verifyTotal();
        generateOtherReportFormats();
    }

    private void saveCoverageData() {
        String handle = webClient.getWindowHandle();
        new WebDriverWait(webClient, 1).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("browserIframe"));
        String json = (String) ((JavascriptExecutor) webClient).executeScript("return jscoverage_serializeCoverageToJSON();");
        ioUtils.copy(json, new File(config.getDestDir(), "jscoverage.json"));
        File jscoverageJS = new File(config.getDestDir(), "jscoverage.js");
        String js = ioUtils.toString(jscoverageJS);
        ioUtils.copy(js + "\njscoverage_isReport = true;", jscoverageJS);
        webClient.switchTo().window(handle);
    }

    private void generateOtherReportFormats() throws MojoExecutionException {
        try {
            if (reportLCOV || reportCoberturaXML) {
                ConfigurationForReport configurationForReport = new ConfigurationForReport();
                Main main = new Main();
                main.initialize();
                configurationForReport.setProperties(Main.properties);
                configurationForReport.setJsonDirectory(config.getDestDir());
                configurationForReport.setSourceDirectory(new File(config.getDestDir(), jscover.Main.reportSrcSubDir));
                main.setConfig(configurationForReport);
                if (reportLCOV) {
                    main.generateLCovDataFile();
                }
                if (reportCoberturaXML) {
                    main.saveCoberturaXml();
                }
            }
        } catch (Throwable t) {
            throw new MojoExecutionException("Error generating other report formats", t);
        }
    }

    public void runTestLocalStorage(String testPage) throws MojoFailureException, MojoExecutionException {
        webClient.get("file:///" + new File(config.getDestDir(), testPage).getAbsolutePath().replaceAll("\\\\","/"));
        webDriverRunner.waitForTestsToComplete(webClient);
        webDriverRunner.verifyTestsPassed(webClient);
    }

    public void runTestInFrames(String testPage) throws MojoFailureException, MojoExecutionException {
        webClient.findElement(By.id("location")).clear();
        webClient.findElement(By.id("location")).sendKeys(testPage);
        webClient.findElement(By.id("openInFrameButton")).click();

        String handle = webClient.getWindowHandle();
        new WebDriverWait(webClient, 1).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("browserIframe"));
        webDriverRunner.waitForTestsToComplete(webClient);
        webDriverRunner.verifyTestsPassed(webClient);

        webClient.switchTo().window(handle);
    }

    protected void verifyTotal() throws MojoFailureException {
        webClient.findElement(By.id("summaryTab")).click();
        new WebDriverWait(webClient, 1).until(textToBePresentInElementLocated(By.id("summaryTotal"), "%"));

        verifyField("Line", "summaryTotal", lineCoverageMinimum);
        verifyField("Branch", "branchSummaryTotal", branchCoverageMinimum);
        verifyField("Function", "functionSummaryTotal", functionCoverageMinimum);
    }

    private void verifyField(String coverageName, String fieldName, int percentageMin) throws MojoFailureException {
        int percentage = extractInt(webClient.findElement(By.id(fieldName)).getText());
        if (percentage < percentageMin) {
            throw new MojoFailureException(format("%s coverage %d less than %d", coverageName, percentage, percentageMin));
        }
    }

    private int extractInt(String percentage) {
        return Integer.parseInt(percentage.replaceAll("%", ""));
    }
}
