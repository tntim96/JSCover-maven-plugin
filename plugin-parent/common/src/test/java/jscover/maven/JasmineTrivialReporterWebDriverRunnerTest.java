package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class JasmineTrivialReporterWebDriverRunnerTest extends WebDriverRunnerTest {
    private WebDriverRunner runner = new JasmineTrivialReporterWebDriverRunner();

    @Test
    public void shouldPass() throws MojoExecutionException, MojoFailureException {
        webDriver.get(getFilePath("../data/src/test/javascript/jasmine-trivial-reporter-code-pass.html"));
        runner.waitForTestsToComplete(webDriver);
        runner.verifyTestsPassed(webDriver);
    }

    @Test
    public void shouldFindErrorMessages() throws MojoExecutionException {
        webDriver.get(getFilePath("../data/src/test/javascript/jasmine-trivial-reporter-code-fail.html"));
        List<String> failures = runner.getFailures(webDriver);
        runner.waitForTestsToComplete(webDriver);
        try {
            runner.verifyTestsPassed(webDriver);
            fail("Expected exception");
        } catch (MojoFailureException e) {
        }
        assertThat(failures.size(), equalTo(6));
        assertThat(failures, contains(new String[]{
                "should not add one - Expected 2 to equal 3.",
                "should not add one - Expected 4 to equal 5.",
                "should not add one - Expected 1000 to equal 1001.",
                "should add one - Expected 2 to equal 1.",
                "should add one - Expected 4 to equal 3.",
                "should add one - Expected 1002 to equal 1001."
        }));
    }
}
