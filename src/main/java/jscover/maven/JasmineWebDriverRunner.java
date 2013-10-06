package jscover.maven;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.fail;

public class JasmineWebDriverRunner implements WebDriverRunner {

    @Override
    public void waitForTestsToComplete(WebDriver webClient) {
        new WebDriverWait(webClient, 1).until(ExpectedConditions.presenceOfElementLocated(By.className("duration")));
        new WebDriverWait(webClient, 1).until(ExpectedConditions.textToBePresentInElement(By.className("duration"), "finished"));
    }

    @Override
    public void verifyTestsPassed(WebDriver webClient) {
        if (webClient.findElements(By.className("failingAlert")).size() != 0) {
            fail("Failing on test");
        }
    }
}
