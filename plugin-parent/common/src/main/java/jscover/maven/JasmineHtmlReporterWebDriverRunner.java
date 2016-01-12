package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JasmineHtmlReporterWebDriverRunner extends JasmineWebDriverRunner {
    public void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException {
        new WebDriverWait(webClient, timeOutSeconds).until(ExpectedConditions.presenceOfElementLocated(By.className("jasmine-duration")));
        new WebDriverWait(webClient, timeOutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(By.className("jasmine-duration"), "finished"));
    }

    public void verifyTestsPassed(WebDriver webClient) throws MojoFailureException {
        if (webClient.findElements(By.cssSelector(".jasmine-bar.jasmine-passed")).size() != 0) {
            return;
        }
        for (String failure : getFailures(webClient)) {
            log.error(failure);
        }
        throw new MojoFailureException("Failing on test");
    }
}
