package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

public class Jasmine2DefaultReporterWebDriverRunnerTest extends WebDriverRunnerTest {
    private WebDriverRunner runner = new Jasmine2DefaultReporterWebDriverRunner();

    @Test
    public void shouldPass() throws MojoExecutionException, MojoFailureException {
        webDriver.get(getFilePath("../data/src/test/javascript/jasmine2-code-pass.html"));
        runner.waitForTestsToComplete(webDriver);
        runner.verifyTestsPassed(webDriver);
    }

    @Test
    public void shouldFindErrorMessages() throws MojoExecutionException {
        webDriver.get(getFilePath("../data/src/test/javascript/jasmine2-code-fail.html"));
        runner.waitForTestsToComplete(webDriver);
        try {
            runner.verifyTestsPassed(webDriver);
            fail("Expected exception");
        } catch (MojoFailureException e) {
        }
        List<String> failures = runner.getFailures(webDriver);
        assertThat(failures.size(), equalTo(6));
        assertThat(failures, contains(new String[]{
                "Common should not add one - Expected 2 to equal 3.",
                "Common should not add one - Expected 4 to equal 5.",
                "Common should not add one - Expected 1000 to equal 1001.",
                "Common should add one - Expected 2 to equal 1.",
                "Common should add one - Expected 4 to equal 3.",
                "Common should add one - Expected 1002 to equal 1001."
        }));
    }
}
