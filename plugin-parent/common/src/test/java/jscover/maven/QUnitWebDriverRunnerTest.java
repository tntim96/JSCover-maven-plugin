package jscover.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class QUnitWebDriverRunnerTest extends WebDriverRunnerTest {
    private WebDriverRunner runner = new QUnitWebDriverRunner();

    @Test
    public void shouldFindErrorMessages() throws MojoFailureException {
        webDriver.get(getFilePath("data/qunit-fail.html"));
        List<String> failures = runner.getFailures(webDriver);
//        for (String failure : failures) {
//            System.out.println("failure = " + failure);
//        }
        try {
            runner.verifyTestsPassed(webDriver);
            fail("Expected exception");
        } catch (MojoFailureException e) {
        }
        assertThat(failures.size(), equalTo(6));

        assertThat(failures.get(0), containsString("should not add one - Source:"));
        assertThat(failures.get(0), containsString("spec/qunit-code-fail-spec.js:3"));
        assertThat(failures.get(1), containsString("should not add one - Source:"));
        assertThat(failures.get(1), containsString("spec/qunit-code-fail-spec.js:4"));
        assertThat(failures.get(2), containsString("should not add one - Source:"));
        assertThat(failures.get(2), containsString("spec/qunit-code-fail-spec.js:5"));

        assertThat(failures.get(3), containsString("should add one - Source:"));
        assertThat(failures.get(3), containsString("spec/qunit-code-fail-spec.js:9"));
        assertThat(failures.get(4), containsString("should add one - Source:"));
        assertThat(failures.get(4), containsString("spec/qunit-code-fail-spec.js:10"));
        assertThat(failures.get(5), containsString("should add one - Source:"));
        assertThat(failures.get(5), containsString("spec/qunit-code-fail-spec.js:11"));
    }
}
