package jscover.maven;

import jscover.filesystem.ConfigurationForFS;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;

public class FileTestRunner extends JSCoverTestRunner {
    private Log log = new SystemStreamLog();

    private ConfigurationForFS config;

    public FileTestRunner(WebDriver webClient, WebDriverRunner webDriverRunner, ConfigurationForFS config, int lineCoverageMinimum, int branchCoverageMinimum, int functionCoverageMinimum, boolean reportLCOV, boolean reportCoberturaXML) {
        super(webClient, webDriverRunner, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML);
        this.config = config;
    }

    public void runTests(List<File> testPages) throws MojoFailureException, MojoExecutionException {
        File jsonFile = new File(config.getDestDir() + "/jscoverage.json");
        if (jsonFile.exists()) {
            log.info("Deleting JSON file " + jsonFile.getAbsolutePath());
            jsonFile.delete();
        }
        if (config.isLocalStorage()) {
            String localStorageUrl = "file:///" + new File(config.getDestDir(), "jscoverage-clear-local-storage.html").getAbsolutePath().replaceAll("\\\\", "/");
            log.info("Clearing local storage: " + localStorageUrl);
            webClient.get(localStorageUrl);
            for (File testPage : testPages)
                runTestLocalStorage(ioUtils.getRelativePath(testPage, config.getDestDir()));
            saveCoverageData();
            webClient.get("file:///" + new File(config.getDestDir(), "jscoverage.html").getAbsolutePath().replaceAll("\\\\", "/"));
        } else {
            webClient.get("file:///" + new File(config.getDestDir(), "jscoverage.html").getAbsolutePath().replaceAll("\\\\", "/"));
            for (File testPage : testPages) {
                runTestInFrames(ioUtils.getRelativePath(testPage, config.getDestDir()));
            }
            String handle = webClient.getWindowHandle();
            new WebDriverWait(webClient, 1).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("browserIframe"));
            saveCoverageData();
            webClient.switchTo().window(handle);
        }
        verifyTotal();
        generateOtherReportFormats(config.getDestDir());
    }

    private void saveCoverageData() {
        String json = (String) ((JavascriptExecutor) webClient).executeScript("return jscoverage_serializeCoverageToJSON();");
        ioUtils.copy(json, new File(config.getDestDir(), "jscoverage.json"));
        File jscoverageJS = new File(config.getDestDir(), "jscoverage.js");
        String js = ioUtils.toString(jscoverageJS);
        ioUtils.copy(js + "\njscoverage_isReport = true;", jscoverageJS);
    }

    public void runTestLocalStorage(String testPage) throws MojoFailureException, MojoExecutionException {
        log.info("Testing " + testPage);
        webClient.get("file:///" + new File(config.getDestDir(), testPage).getAbsolutePath().replaceAll("\\\\", "/"));
        webDriverRunner.waitForTestsToComplete(webClient);
        webDriverRunner.verifyTestsPassed(webClient);
        log.info("...passed");
    }

    public void runTestInFrames(String testPage) throws MojoFailureException, MojoExecutionException {
        log.info("Testing (without localStorage) " + testPage);
        webClient.findElement(By.id("location")).clear();
        webClient.findElement(By.id("location")).sendKeys(testPage);
        webClient.findElement(By.id("openInFrameButton")).click();

        String handle = webClient.getWindowHandle();
        new WebDriverWait(webClient, 1).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("browserIframe"));
        webDriverRunner.waitForTestsToComplete(webClient);
        webDriverRunner.verifyTestsPassed(webClient);

        webClient.switchTo().window(handle);
        log.info("...passed");
    }
}
