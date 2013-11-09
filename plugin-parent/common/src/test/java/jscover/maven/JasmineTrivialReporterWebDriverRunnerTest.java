package jscover.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class JasmineTrivialReporterWebDriverRunnerTest {
    private WebDriverRunner runner = new JasmineTrivialReporterWebDriverRunner();

    @Test
    public void shouldDetectFindErrorMessages() throws MojoFailureException {
        WebDriver webDriver = new PhantomJSDriver();
        webDriver.get("file:///" + new File("plugin-parent/common/data/jasmine-trivial-fail.html").getAbsolutePath().replaceAll("\\\\", "/"));
        List<String> failures = runner.getFailures(webDriver);
//        for (String failure : failures) {
//            System.out.println("failure = " + failure);
//        }
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
