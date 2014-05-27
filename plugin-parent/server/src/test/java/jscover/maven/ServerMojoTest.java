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
    private ServerMojo mojo = new ServerMojo();

    protected File getFilePath(String pathname) {
        if (System.getProperty("user.dir").endsWith("JSCover-maven-plugin"))
            pathname = "plugin-parent/server/" + pathname;
        return new File(pathname).getAbsoluteFile();
    }

    @Before
    public void setUp() throws Exception {
        deleteDirectory(getFilePath("../data/target"));
        ReflectionUtils.setVariableValueInObject(mojo, "documentRoot", getFilePath("../data"));
        ReflectionUtils.setVariableValueInObject(mojo, "testDirectory", getFilePath("../data/src/test/javascript"));
        ReflectionUtils.setVariableValueInObject(mojo, "reportDir", getFilePath("../data/target"));
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "jasmine-html-*pass.html");
        ReflectionUtils.setVariableValueInObject(mojo, "instrumentPathArgs", Arrays.asList("--no-instrument=src/main/webapp/js/vendor/", "--no-instrument=src/test", "--no-instrument=target"));
        ReflectionUtils.setVariableValueInObject(mojo, "testType", JasmineHtmlReporter);
        ReflectionUtils.setVariableValueInObject(mojo, "lineCoverageMinimum", 66);
        ReflectionUtils.setVariableValueInObject(mojo, "branchCoverageMinimum", 66);
        ReflectionUtils.setVariableValueInObject(mojo, "functionCoverageMinimum", 66);
        ReflectionUtils.setVariableValueInObject(mojo, "webDriverClassName", "org.openqa.selenium.chrome.ChromeDriver");
        //ReflectionUtils.setVariableValueInObject(mojo, "webDriverClassName", "org.openqa.selenium.firefox.FirefoxDriver");
    }

    private void deleteDirectory(File dir) {
        for (File file : dir.listFiles())
            if (file.isFile())
                file.delete();
            else
                deleteDirectory(file);
    }

    @Test
    public void shouldPassJasmine() throws Exception {
        mojo.execute();
        assertThat(new File(getFilePath("../data/target"), "jscoverage.json").exists(), equalTo(true));
        assertThat(new File(getFilePath("../data/target"), "jscover.lcov").exists(), equalTo(false));
        assertThat(new File(getFilePath("../data/target"), "cobertura.xml").exists(), equalTo(false));
    }

    @Test
    public void shouldPassJasmineWithoutLocalStorage() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "localStorage", false);
        mojo.execute();
    }

    @Test
    public void shouldGenerateLCOVReport() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "reportLCOV", true);
        mojo.execute();

        assertThat(new File(getFilePath("../data/target"), "jscover.lcov").exists(), equalTo(true));
    }

    @Test
    public void shouldGenerateCoberturaXML() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "reportCoberturaXML", true);
        mojo.execute();

        assertThat(new File(getFilePath("../data/target"), "cobertura-coverage.xml").exists(), equalTo(true));
    }

    @Test
    public void shouldFailJasmineIfLineCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "lineCoverageMinimum", 101);
        try {
            mojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Line coverage 100 less than 101"));
        }
    }

    @Test
    public void shouldFailJasmineIfBranchCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "branchCoverageMinimum", 101);
        try {
            mojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Branch coverage 100 less than 101"));
        }
    }

    @Test
    public void shouldFailJasmineIfFunctionCoverageTooLow() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "functionCoverageMinimum", 101);
        try {
            mojo.execute();
            fail("Should have thrown exception");
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Function coverage 100 less than 101"));
        }
    }

    @Test(expected = MojoFailureException.class)
    public void shouldFailJasmineIfTests() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "jasmine-html-*fail.html");
        mojo.execute();
    }

    @Test
    public void shouldPassQUnit() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "qunit-*pass.html");
        ReflectionUtils.setVariableValueInObject(mojo, "testType", QUnit);
        mojo.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void shouldFailQUnit() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "qunit-*fail.html");
        ReflectionUtils.setVariableValueInObject(mojo, "testType", QUnit);
        mojo.execute();
    }
}