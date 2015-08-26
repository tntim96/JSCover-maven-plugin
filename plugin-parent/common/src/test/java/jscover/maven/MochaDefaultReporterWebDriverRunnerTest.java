package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;


public class MochaDefaultReporterWebDriverRunnerTest extends WebDriverRunnerTest {
    private WebDriverRunner runner = new MochaDefaultReporterWebDriverRunner();

    @Test
    public void shouldPass() throws MojoExecutionException, MojoFailureException {
        webDriver.get(getFilePath("../data/src/test/javascript/mocha-code-pass.html"));
        runner.waitForTestsToComplete(webDriver);
        runner.verifyTestsPassed(webDriver);
    }

    @Test
    public void shouldFindErrorMessages() throws MojoExecutionException {
        webDriver.get(getFilePath("../data/src/test/javascript/mocha-code-fail.html"));
        runner.waitForTestsToComplete(webDriver);
        try {
            runner.verifyTestsPassed(webDriver);
            fail("Expected exception");
        } catch (MojoFailureException e) {
        }
        List<String> failures = runner.getFailures(webDriver);
        assertThat(failures.size(), equalTo(2));
        assertThat(failures, contains(new String[]{
                "should not add one - Error: expected 2 to equal 3",
                "should add one - Error: expected 2 to equal 1",
        }));
    }
}