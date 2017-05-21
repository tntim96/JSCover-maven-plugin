package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class QUnitWebDriverRunner extends WebDriverRunnerBase {

    public void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException {
        new WebDriverWait(webClient, timeOutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(By.id("qunit-testresult"), "tests completed"));
    }

    public void verifyTestsPassed(WebDriver webClient) throws MojoFailureException {
        int failingCount = webClient.findElements(By.className("fail")).size();
        if (failingCount != 0) {
            for (String failure : getFailures(webClient))
                log.error(failure);
            throw new MojoFailureException("Number of failing tests: " + failingCount);
        }
    }

    public List<String> getFailures(WebDriver webClient) {
        List<String> failures = new ArrayList<String>();
        List<WebElement> elements = webClient.findElements(By.className("fail"));
        for (WebElement element : elements) {
            List<WebElement> descriptions = element.findElements(By.className("test-name"));
            if (descriptions.size() != 1)
                continue;
            for (WebElement message : element.findElements(By.className("test-source"))) {
                failures.add(format("%s - %s", descriptions.get(0).getText(), getTextViaJS(webClient, message)));
            }
        }
        return failures;
    }

    private String getTextViaJS(WebDriver webClient, WebElement webElement) {
        String script = "return arguments[0].innerText";
        return (String) ((JavascriptExecutor) webClient).executeScript(script, webElement);
    }
}
