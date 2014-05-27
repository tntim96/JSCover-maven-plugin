package jscover.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static jscover.maven.TestType.JasmineHtmlReporter;
import static jscover.maven.TestType.QUnit;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServerMojoTest {
    private ServerMojo serverMojo = new ServerMojo();

    protected File getFilePath(String pathname) {
        if (System.getProperty("user.dir").endsWith("JSCover-maven-plugin"))
            pathname = "plugin-parent/server/" + pathname;
        return new File(pathname).getAbsoluteFile();
    }

    @Before
    public void setUp() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "documentRoot", getFilePath("../data"));
        ReflectionUtils.setVariableValueInObject(serverMojo, "testDirectory", getFilePath("../data/src/test/javascript"));
        ReflectionUtils.setVariableValueInObject(serverMojo, "reportDir", getFilePath("../data/target"));
        ReflectionUtils.setVariableValueInObject(serverMojo, "testIncludes", "jasmine-html-*pass.html");
        ReflectionUtils.setVariableValueInObject(serverMojo, "instrumentPathArgs", Arrays.asList("--no-instrument=src/main/webapp/js/vendor/", "--no-instrument=src/test", "--no-instrument=target"));
        ReflectionUtils.setVariableValueInObject(serverMojo, "testType", JasmineHtmlReporter);
        ReflectionUtils.setVariableValueInObject(serverMojo, "lineCoverageMinimum", 66);
        ReflectionUtils.setVariableValueInObject(serverMojo, "branchCoverageMinimum", 66);
        ReflectionUtils.setVariableValueInObject(serverMojo, "functionCoverageMinimum", 66);
        //ReflectionUtils.setVariableValueInObject(serverMojo, "webDriverClassName", "org.openqa.selenium.firefox.FirefoxDriver");
    }

    @Test
    public void shouldPassJasmine() throws Exception {
        serverMojo.execute();
    }

    @Test
    public void shouldPassJasmineWithoutLocalStorage() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "localStorage", false);
        serverMojo.execute();
    }

    @Test
    public void shouldFailJasmineIfLineCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "lineCoverageMinimum", 101);
        try {
            serverMojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Line coverage 100 less than 101"));
        }
    }

    @Test
    public void shouldFailJasmineIfBranchCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "branchCoverageMinimum", 101);
        try {
            serverMojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Branch coverage 100 less than 101"));
        }
    }

    @Test
    public void shouldFailJasmineIfFunctionCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "functionCoverageMinimum", 101);
        try {
            serverMojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Function coverage 100 less than 101"));
        }
    }

    @Test(expected = MojoFailureException.class)
    public void shouldFailJasmineIfTests() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "testIncludes", "jasmine-html-*fail.html");
        serverMojo.execute();
    }

    @Test
    public void shouldPassQUnit() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "testIncludes", "qunit-*pass.html");
        ReflectionUtils.setVariableValueInObject(serverMojo, "testType", QUnit);
        serverMojo.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void shouldFailQUnit() throws Exception {
        ReflectionUtils.setVariableValueInObject(serverMojo, "testIncludes", "qunit-*fail.html");
        ReflectionUtils.setVariableValueInObject(serverMojo, "testType", QUnit);
        serverMojo.execute();
    }
}