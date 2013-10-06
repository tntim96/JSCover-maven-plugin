package jscover.maven;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.fail;

public class QUnitWebDriverRunner implements WebDriverRunner {
    @Override
    public void waitForTestsToComplete(WebDriver webClient) {
        new WebDriverWait(webClient, 10).until(ExpectedConditions.textToBePresentInElement(By.id("qunit-testresult"), "Tests completed"));
    }

    @Override
    public void verifyTestsPassed(WebDriver webClient) {
        int failingCount = webClient.findElements(By.className("failingAlert")).size();
        if (failingCount != 0) {
            fail("Number of failing tests: " + failingCount);
        }
    }
}
