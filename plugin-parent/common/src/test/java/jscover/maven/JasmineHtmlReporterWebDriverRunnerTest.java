package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class JasmineHtmlReporterWebDriverRunnerTest extends WebDriverRunnerTest {
    private WebDriverRunner runner = new JasmineHtmlReporterWebDriverRunner();

    @Test
    public void shouldFindErrorMessages() throws MojoExecutionException {
        webDriver.get(getFilePath("../data/src/test/javascript/jasmine-html-reporter-code-fail.html"));
        List<String> failures = runner.getFailures(webDriver);
        runner.waitForTestsToComplete(webDriver);
        try {
            runner.verifyTestsPassed(webDriver);
            fail("Expected exception");
        } catch (MojoFailureException e) {
        }
        assertThat(failures.size(), equalTo(6));
        assertThat(failures, contains(new String[]{
                "Common should not add one. - Expected 2 to equal 3.",
                "Common should not add one. - Expected 4 to equal 5.",
                "Common should not add one. - Expected 1000 to equal 1001.",
                "Common should add one. - Expected 2 to equal 1.",
                "Common should add one. - Expected 4 to equal 3.",
                "Common should add one. - Expected 1002 to equal 1001."
        }));
    }
}
