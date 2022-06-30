package jscover.maven;

import static java.lang.String.format;

import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class JasmineWebDriverRunner extends WebDriverRunnerBase {
    public void waitForTestsToComplete(WebDriver webClient) {
        new WebDriverWait(webClient, Duration.ofSeconds(timeOutSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.className("jasmine-duration")));
        new WebDriverWait(webClient, Duration.ofSeconds(timeOutSeconds)).until(ExpectedConditions.textToBePresentInElementLocated(By.className("jasmine-duration"), "finished"));
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

    public List<String> getFailures(WebDriver webClient) {
        List<String> failures = new ArrayList<>();
        List<WebElement> elements = webClient.findElements(By.className("jasmine-failed"));
        for (WebElement element : elements) {
            List<WebElement> descriptions = element.findElements(By.className("jasmine-description"));
            if (descriptions.size() != 1)
                continue;
            for (WebElement message :element.findElements(By.className("jasmine-result-message")))
                failures.add(format("%s - %s", descriptions.get(0).getText(), message.getText()));
        }
        return failures;
    }
}
