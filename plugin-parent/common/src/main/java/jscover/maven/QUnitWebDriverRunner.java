package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class QUnitWebDriverRunner implements WebDriverRunner {

    public void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException {
        try {
            new WebDriverWait(webClient, 10).until(ExpectedConditions.textToBePresentInElement(By.id("qunit-testresult"), "Tests completed"));
        } catch(AssertionError e) {
            throw new MojoExecutionException("Problem waiting for tests to complete", e);
        }
    }

    public void verifyTestsPassed(WebDriver webClient) throws MojoFailureException {
        int failingCount = webClient.findElements(By.className("failingAlert")).size();
        if (failingCount != 0) {
            throw new MojoFailureException("Number of failing tests: " + failingCount);
        }
    }

    public List<String> getFailures(WebDriver webClient) throws MojoFailureException {
        throw new UnsupportedOperationException();
    }
}
