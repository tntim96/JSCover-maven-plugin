package jscover.maven;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public abstract class JasmineWebDriverRunner implements WebDriverRunner {
    public List<String> getFailures(WebDriver webClient) {
        List<String> failures = new ArrayList<String>();
        List<WebElement> elements = webClient.findElements(By.className("failed"));
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
