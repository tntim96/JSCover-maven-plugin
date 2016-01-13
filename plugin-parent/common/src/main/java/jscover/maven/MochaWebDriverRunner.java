package jscover.maven;

import static java.lang.String.format;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;


public class MochaWebDriverRunner extends WebDriverRunnerBase {

  public void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException {
    new WebDriverWait(webClient, timeOutSeconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("duration")));
  }


  public void verifyTestsPassed(WebDriver webClient) throws MojoFailureException {
    if (webClient.findElements(By.className("fail")).size() != 0) {
      for (String failure : getFailures(webClient))
        log.error(failure);
      throw new MojoFailureException("Failing on test");
    }
  }

  public List<String> getFailures(WebDriver webClient) {
    List<String> failures = new ArrayList<String>();
    List<WebElement> elements = webClient.findElements(By.className("fail"));
    for (WebElement element : elements) {
      List<WebElement> descriptions = element.findElements(By.tagName("h2"));
      String testName = descriptions.get(0).getText();
      for (WebElement message :element.findElements(By.className("error")))
        failures.add(format("%s - %s", testName.substring(0, testName.length()-2), message.getText().substring(0, message.getText().indexOf("\n"))));
    }
    return failures;
  }

}
