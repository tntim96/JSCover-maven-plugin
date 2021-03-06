package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

public class QUnitWebDriverRunnerTest extends WebDriverRunnerTest {
    private WebDriverRunner runner = new QUnitWebDriverRunner();

    @Test
    public void shouldPass() throws MojoExecutionException, MojoFailureException {
        webDriver.get(getFilePath("../data/src/test/javascript/qunit-code-pass.html"));
        runner.waitForTestsToComplete(webDriver);
        runner.verifyTestsPassed(webDriver);
    }

    @Test
    public void shouldFindErrorMessages() throws MojoExecutionException {
        webDriver.get(getFilePath("../data/src/test/javascript/qunit-code-fail.html"));
        runner.waitForTestsToComplete(webDriver);
        try {
            runner.verifyTestsPassed(webDriver);
            fail("Expected exception");
        } catch (MojoFailureException e) {
        }
        List<String> failures = runner.getFailures(webDriver);
        assertThat(failures.size(), equalTo(6));

        assertThat(failures.get(0), containsString("should not add one - Source:"));
        assertThat(failures.get(0), containsString("spec/qunit-code-fail-spec.js:2"));
        assertThat(failures.get(1), containsString("should not add one - Source:"));
        assertThat(failures.get(1), containsString("spec/qunit-code-fail-spec.js:3"));
        assertThat(failures.get(2), containsString("should not add one - Source:"));
        assertThat(failures.get(2), containsString("spec/qunit-code-fail-spec.js:4"));

        assertThat(failures.get(3), containsString("should add one - Source:"));
        assertThat(failures.get(3), containsString("spec/qunit-code-fail-spec.js:8"));
        assertThat(failures.get(4), containsString("should add one - Source:"));
        assertThat(failures.get(4), containsString("spec/qunit-code-fail-spec.js:9"));
        assertThat(failures.get(5), containsString("should add one - Source:"));
        assertThat(failures.get(5), containsString("spec/qunit-code-fail-spec.js:10"));
    }
}
