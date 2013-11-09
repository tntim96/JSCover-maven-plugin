package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class JasmineTrivialReporterWebDriverRunner extends JasmineWebDriverRunner implements WebDriverRunner {

    public void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException {
        try {
            new WebDriverWait(webClient, 10).until(ExpectedConditions.presenceOfElementLocated(By.className("finished-at")));
            new WebDriverWait(webClient, 10).until(ExpectedConditions.textToBePresentInElement(By.className("finished-at"), "Finished at "));
        } catch(AssertionError e) {
            throw new MojoExecutionException("Problem waiting for tests to complete", e);
        }
    }

    public void verifyTestsPassed(WebDriver webClient) throws MojoFailureException {
        if (webClient.findElements(By.className("failed")).size() != 0) {
            new MojoFailureException("Failing on test");
        }
    }
}
