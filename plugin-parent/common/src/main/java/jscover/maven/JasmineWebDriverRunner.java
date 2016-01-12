package jscover.maven;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public abstract class JasmineWebDriverRunner extends WebDriverRunnerBase {
    public List<String> getFailures(WebDriver webClient) {
        List<String> failures = new ArrayList<String>();
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
