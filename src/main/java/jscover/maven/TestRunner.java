package jscover.maven;

import jscover.report.ConfigurationForReport;
import jscover.report.Main;
import jscover.report.ReportFormat;
import jscover.server.ConfigurationForServer;
import jscover.util.IoUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static jscover.report.ReportFormat.COBERTURAXML;
import static jscover.report.ReportFormat.LCOV;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement;

public class TestRunner {

    protected WebDriver webClient;
    private ConfigurationForServer config;
    private WebDriverRunner webDriverRunner;
    private int lineCoverageMinimum;
    private int branchCoverageMinimum;
    private int functionCoverageMinimum;
    private final boolean reportLCOV;
    private final boolean reportCoberturaXML;
    private IoUtils ioUtils;

    public TestRunner(WebDriver webClient, WebDriverRunner webDriverRunner, ConfigurationForServer config, int lineCoverageMinimum, int branchCoverageMinimum, int functionCoverageMinimum, boolean reportLCOV, boolean reportCoberturaXML) {
        this.webClient = webClient;
        this.webDriverRunner = webDriverRunner;
        this.config = config;
        this.lineCoverageMinimum = lineCoverageMinimum;
        this.branchCoverageMinimum = branchCoverageMinimum;
        this.functionCoverageMinimum = functionCoverageMinimum;
        this.reportLCOV = reportLCOV;
        this.reportCoberturaXML = reportCoberturaXML;
    }

    public void runTests(List<File> testPages) throws Exception {
        File jsonFile = new File(config.getReportDir() + "/jscoverage.json");
        if (jsonFile.exists())
            jsonFile.delete();
        try {
            webClient.get(String.format("http://localhost:%d/jscoverage.html", config.getPort()));
            for (File testPage : testPages) {
                ioUtils = IoUtils.getInstance();
                runTest(ioUtils.getRelativePath(testPage, config.getDocumentRoot()));
            }
            saveCoverageData();
            verifyTotal(webClient, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum);
            generateOtherReportFormats();
        } finally {
            stopWebClient();
        }
    }

    private void saveCoverageData() {
        new WebDriverWait(webClient, 1).until(ExpectedConditions.elementToBeClickable(By.id("storeTab")));
        webClient.findElement(By.id("storeTab")).click();

        new WebDriverWait(webClient, 1).until(ExpectedConditions.elementToBeClickable(By.id("storeButton")));
        webClient.findElement(By.id("storeButton")).click();
        new WebDriverWait(webClient, 2).until(ExpectedConditions.textToBePresentInElement(By.id("storeDiv"), "Coverage data stored at"));

        webClient.get(format("http://localhost:%d/%s/jscoverage.html", config.getPort(), ioUtils.getRelativePath(config.getReportDir(), config.getDocumentRoot())));
    }

    private void generateOtherReportFormats() throws IOException {
        if (reportLCOV || reportCoberturaXML) {
            ConfigurationForReport configurationForReport = new ConfigurationForReport();
            Main main = new Main();
            main.initialize();
            configurationForReport.setProperties(Main.properties);
            configurationForReport.setJsonDirectory(config.getReportDir());
            configurationForReport.setSourceDirectory(new File(config.getReportDir(), jscover.Main.reportSrcSubDir));
            main.setConfig(configurationForReport);
            if (reportLCOV) {
                main.generateLCovDataFile();
            }
            if (reportCoberturaXML) {
                main.saveCoberturaXml();
            }
        }
    }

    public void runTest(String testPage) throws Exception {
        webClient.findElement(By.id("location")).clear();
        webClient.findElement(By.id("location")).sendKeys(String.format("http://localhost:%d/%s", config.getPort(), testPage));
        webClient.findElement(By.id("openInFrameButton")).click();

        String handle = webClient.getWindowHandle();
        new WebDriverWait(webClient, 1).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("browserIframe"));
        webDriverRunner.waitForTestsToComplete(webClient);
        webDriverRunner.verifyTestsPassed(webClient);

        webClient.switchTo().window(handle);
    }

    protected void verifyTotal(WebDriver webClient, int percentage, int branchPercentage, int functionPercentage) throws IOException {
        webClient.findElement(By.id("summaryTab")).click();

        verifyTotals(webClient, percentage, branchPercentage, functionPercentage);
    }

    protected void verifyTotals(WebDriver webClient, int percentage, int branchPercentage, int functionPercentage) {
        new WebDriverWait(webClient, 1).until(textToBePresentInElement(By.id("summaryTotal"), "%"));
        assertEquals(percentage + "%", webClient.findElement(By.id("summaryTotal")).getText());
        assertEquals(branchPercentage + "%", webClient.findElement(By.id("branchSummaryTotal")).getText());
        assertEquals(functionPercentage + "%", webClient.findElement(By.id("functionSummaryTotal")).getText());
    }

    public void stopWebClient() {
        try {
            webClient.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            webClient.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
