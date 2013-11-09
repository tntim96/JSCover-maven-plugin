package jscover.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class QUnitWebDriverRunnerTest {
    private WebDriverRunner runner = new QUnitWebDriverRunner();

    @Test
    public void shouldDetectFindErrorMessages() throws MojoFailureException {
        WebDriver webDriver = new PhantomJSDriver();
        webDriver.get("file:///" + new File("plugin-parent/common/data/qunit-fail.html").getAbsolutePath().replaceAll("\\\\", "/"));
        List<String> failures = runner.getFailures(webDriver);
//        for (String failure : failures) {
//            System.out.println("failure = " + failure);
//        }
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
