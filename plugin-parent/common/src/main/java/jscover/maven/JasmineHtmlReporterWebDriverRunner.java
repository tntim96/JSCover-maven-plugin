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

public class JasmineHtmlReporterWebDriverRunner implements WebDriverRunner {

    public void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException {
        try {
            new WebDriverWait(webClient, 10).until(ExpectedConditions.presenceOfElementLocated(By.className("duration")));
            new WebDriverWait(webClient, 10).until(ExpectedConditions.textToBePresentInElement(By.className("duration"), "finished"));
        } catch(AssertionError e) {
            throw new MojoExecutionException("Problem waiting for tests to complete", e);
        }
    }

    public void verifyTestsPassed(WebDriver webClient) throws MojoFailureException {
        if (webClient.findElements(By.className("failingAlert")).size() != 0) {
            new MojoFailureException("Failing on test");
        }
    }

    public List<String> getFailures(WebDriver webClient) throws MojoFailureException {
        List<String> failures = new ArrayList<String>();
        List<WebElement> elements = webClient.findElements(By.className("failed"));
        StringBuilder sb = new StringBuilder();
        for (WebElement element : elements) {
            List<WebElement> descriptions = element.findElements(By.className("description"));
            if (descriptions.size() != 1)
                continue;
            for (WebElement message :element.findElements(By.className("resultMessage")))
                failures.add(format("%s - %s", descriptions.get(0).getText(), message.getText()));
        }
        return failures;
    }
}
