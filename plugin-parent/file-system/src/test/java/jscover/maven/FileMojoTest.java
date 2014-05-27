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

public class FileMojoTest {
    private FileMojo fileMojo = new FileMojo();

    protected File getFilePath(String pathname) {
        if (System.getProperty("user.dir").endsWith("JSCover-maven-plugin"))
            pathname = "plugin-parent/file-system/" + pathname;
        return new File(pathname).getAbsoluteFile();
    }

    @Before
    public void setUp() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "srcDir", getFilePath("../data/src"));
        ReflectionUtils.setVariableValueInObject(fileMojo, "testDirectory", getFilePath("../data/src/test/javascript"));
        ReflectionUtils.setVariableValueInObject(fileMojo, "destDir", getFilePath("../data/target"));
        ReflectionUtils.setVariableValueInObject(fileMojo, "testIncludes", "jasmine-html-*pass.html");
        ReflectionUtils.setVariableValueInObject(fileMojo, "instrumentPathArgs", Arrays.asList("--no-instrument=main/webapp/js/vendor/", "--no-instrument=test"));
        ReflectionUtils.setVariableValueInObject(fileMojo, "excludeArgs", Arrays.asList("--exclude=main/java", "--exclude=main/resources", "--exclude-reg=test/java$"));
        ReflectionUtils.setVariableValueInObject(fileMojo, "testType", JasmineHtmlReporter);
        ReflectionUtils.setVariableValueInObject(fileMojo, "lineCoverageMinimum", 100);
        ReflectionUtils.setVariableValueInObject(fileMojo, "branchCoverageMinimum", 100);
        ReflectionUtils.setVariableValueInObject(fileMojo, "functionCoverageMinimum", 100);
        //ReflectionUtils.setVariableValueInObject(fileMojo, "webDriverClassName", "org.openqa.selenium.firefox.FirefoxDriver");
    }

    @Test
    public void shouldPassJasmine() throws Exception {
        fileMojo.execute();
    }

    @Test
    public void shouldPassJasmineWithoutLocalStorage() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "localStorage", false);
        fileMojo.execute();
    }

    @Test
    public void shouldFailJasmineIfLineCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "lineCoverageMinimum", 101);
        try {
            fileMojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Line coverage 100 less than 101"));
        }
    }

    @Test
    public void shouldFailJasmineIfBranchCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "branchCoverageMinimum", 101);
        try {
            fileMojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Branch coverage 100 less than 101"));
        }
    }

    @Test
    public void shouldFailJasmineIfFunctionCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "functionCoverageMinimum", 101);
        try {
            fileMojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Function coverage 100 less than 101"));
        }
    }

    @Test(expected = MojoFailureException.class)
    public void shouldFailJasmineIfTests() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "testIncludes", "jasmine-html-*fail.html");
        fileMojo.execute();
    }

    @Test
    public void shouldPassQUnit() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "testIncludes", "qunit-*pass.html");
        ReflectionUtils.setVariableValueInObject(fileMojo, "testType", QUnit);
        fileMojo.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void shouldFailQUnit() throws Exception {
        ReflectionUtils.setVariableValueInObject(fileMojo, "testIncludes", "qunit-*fail.html");
        ReflectionUtils.setVariableValueInObject(fileMojo, "testType", QUnit);
        fileMojo.execute();
    }
}