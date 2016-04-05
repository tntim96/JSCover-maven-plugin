package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.fail;


public class MochaWebDriverRunnerTest extends WebDriverRunnerTest {
    private WebDriverRunner runner = new MochaWebDriverRunner();

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
        assertThat(failures, hasItem(getMatcher("should not add one - Error: expected 2 to equal 3")));
        assertThat(failures, hasItem(getMatcher("should add one - Error: expected 2 to equal 1")));
    }

    private TypeSafeMatcher<String> getMatcher(final String s) {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String s) {
                return s.contains(s);
            }

            public void describeTo(Description description) {
                description.appendDescriptionOf(new SelfDescribing() {
                    public void describeTo(Description description) {
                        description.appendText("a string containing ");
                    }
                });
                description.appendText("'" + s + "'");
            }
        };
    }
}