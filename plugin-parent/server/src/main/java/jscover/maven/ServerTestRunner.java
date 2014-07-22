package jscover.maven;

import jscover.server.ConfigurationForServer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;

import static java.lang.String.format;

public class ServerTestRunner extends JSCoverTestRunner {
    private Log log = new SystemStreamLog();

    private ConfigurationForServer config;

    public ServerTestRunner(WebDriver webClient, WebDriverRunner webDriverRunner, ConfigurationForServer config, int lineCoverageMinimum, int branchCoverageMinimum, int functionCoverageMinimum, boolean reportLCOV, boolean reportCoberturaXML) {
        super(webClient, webDriverRunner, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML);
        this.config = config;
    }

    public void runTests(List<File> testPages) throws MojoFailureException, MojoExecutionException {
        File jsonFile = new File(config.getReportDir() + "/jscoverage.json");
        if (jsonFile.exists())
            jsonFile.delete();

        if (config.isLocalStorage()) {
            webClient.get(String.format("http://localhost:%d/jscoverage-clear-local-storage.html", config.getPort()));
            for (File testPage : testPages)
                runTestLocalStorage(ioUtils.getRelativePath(testPage, config.getDocumentRoot()));
            webClient.get(String.format("http://localhost:%d/jscoverage.html", config.getPort()));
        } else {
            webClient.get(String.format("http://localhost:%d/jscoverage.html", config.getPort()));
            for (File testPage : testPages)
                runTestInFrames(ioUtils.getRelativePath(testPage, config.getDocumentRoot()));
        }
        saveCoverageData();
        verifyTotal();
        generateOtherReportFormats(config.getReportDir());
    }

    private void saveCoverageData() {
        new WebDriverWait(webClient, 1).until(ExpectedConditions.elementToBeClickable(By.id("storeTab")));
        webClient.findElement(By.id("storeTab")).click();

        new WebDriverWait(webClient, 1).until(ExpectedConditions.elementToBeClickable(By.id("storeButton")));
        webClient.findElement(By.id("storeButton")).click();
        new WebDriverWait(webClient, 2).until(ExpectedConditions.textToBePresentInElementLocated(By.id("storeDiv"), "Coverage data stored at"));

        webClient.get(format("http://localhost:%d/%s/jscoverage.html", config.getPort(), ioUtils.getRelativePath(config.getReportDir(), config.getDocumentRoot())));
    }

    public void runTestLocalStorage(String testPage) throws MojoFailureException, MojoExecutionException {
        log.info("Testing " + testPage);
        webClient.get(String.format("http://localhost:%d/%s", config.getPort(), testPage));
        webDriverRunner.waitForTestsToComplete(webClient);
        webDriverRunner.verifyTestsPassed(webClient);
        log.info("...passed");
    }

    public void runTestInFrames(String testPage) throws MojoFailureException, MojoExecutionException {
        log.info("Testing (without localStorage) " + testPage);
        webClient.findElement(By.id("location")).clear();
        webClient.findElement(By.id("location")).sendKeys(String.format("http://localhost:%d/%s", config.getPort(), testPage));
        webClient.findElement(By.id("openInFrameButton")).click();

        String handle = webClient.getWindowHandle();
        new WebDriverWait(webClient, 1).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("browserIframe"));
        webDriverRunner.waitForTestsToComplete(webClient);
        webDriverRunner.verifyTestsPassed(webClient);
        log.info("...passed");

        webClient.switchTo().window(handle);
    }
}
