package jscover.maven;

import org.openqa.selenium.WebDriver;

public interface WebDriverRunner {
    void waitForTestsToComplete(WebDriver webClient);
    void verifyTestsPassed(WebDriver webClient);
}
