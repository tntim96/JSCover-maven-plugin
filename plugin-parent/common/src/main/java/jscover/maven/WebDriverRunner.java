package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.WebDriver;

public interface WebDriverRunner {
    void waitForTestsToComplete(WebDriver webClient) throws MojoExecutionException;
    void verifyTestsPassed(WebDriver webClient) throws MojoFailureException;
}
