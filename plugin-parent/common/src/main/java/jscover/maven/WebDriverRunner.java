package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.WebDriver;

import java.util.List;

public interface WebDriverRunner {
    void setTimeOutSeconds(int timeOutSeconds) throws MojoExecutionException;
    void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException;
    void verifyTestsPassed(WebDriver webClient) throws MojoFailureException;
    List<String> getFailures(WebDriver webClient);
}
