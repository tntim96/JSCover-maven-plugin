package jscover.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class Jasmine2DefaultReporterWebDriverRunner extends JasmineHtmlReporterWebDriverRunner implements WebDriverRunner {
    private Log log = new SystemStreamLog();

    public void verifyTestsPassed(WebDriver webClient) throws MojoFailureException {
        if (webClient.findElements(By.className("failed")).size() != 0) {
            for (String failure : getFailures(webClient))
                log.error(failure);
            throw new MojoFailureException("Failing on test");
        }
    }

    @Override
    public List<String> getFailures(WebDriver webClient) {
        List<String> failures = new ArrayList<String>();
        List<WebElement> elements = webClient.findElements(By.className("failed"));
        for (WebElement element : elements) {
            List<WebElement> descriptions = element.findElements(By.className("description"));
            if (descriptions.size() != 1)
                continue;
            for (WebElement message :element.findElements(By.className("result-message")))
                failures.add(format("%s - %s", descriptions.get(0).getText(), message.getText()));
        }
        return failures;
    }
}
