package jscover.maven;

import com.google.javascript.jscomp.CompilerOptions;
import jscover.ConfigurationCommon;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static jscover.ConfigurationCommon.*;
import static jscover.maven.TestType.Custom;
import static jscover.maven.TestType.QUnit;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class JSCoverMojoTest {
    private JSCoverMojo mojo = new DummyMojo();

    protected File getFilePath(String pathname) {
        if (System.getProperty("user.dir").endsWith("JSCover-maven-plugin"))
            pathname = "plugin-parent/server/" + pathname;
        return new File(pathname).getAbsoluteFile();
    }

    @Test
    public void shouldFindTestFiles() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testDirectory", getFilePath("../data/src/test/javascript"));
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "jasmine-code-*pass.html");
        List<File> files = mojo.getTestFiles();
        for (File file : files) {
            System.out.println("file = " + file);
        }
        assertThat(files.size(), equalTo(1));
        assertThat(files, hasItem(getFilePath("../data/src/test/javascript/jasmine-code-pass.html")));
    }

    @Test
    public void shouldNotFindTestFilesBadInclude() throws IllegalAccessException {
        ReflectionUtils.setVariableValueInObject(mojo, "testDirectory", getFilePath("../data/src/test/javascript"));
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "not-there-*pass.html");
        try {
            mojo.getTestFiles();
            fail("Should throw exception");
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), startsWith("No tests found in "));
            String suffix = "JSCover-maven-plugin/plugin-parent/common/../data/src/test/javascript. Includes:not-there-*pass.html, Excludes:null";
            assertThat(e.getMessage(), endsWith(StringUtils.replaceChars(suffix, "/", File.separator)));
        }
    }

    @Test
    public void shouldNotFindTestFilesBadDirectory() throws IllegalAccessException {
        ReflectionUtils.setVariableValueInObject(mojo, "testDirectory", getFilePath("../data/src/test/not-there"));
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "jasmine-html-*pass.html");
        try {
            mojo.getTestFiles();
            fail("Should throw exception");
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), equalTo("Problem finding test pages"));
        }
    }

    @Test
    public void shouldSetSystemProperties() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("a", "b");
        ReflectionUtils.setVariableValueInObject(mojo, "systemProperties", properties);
        mojo.setSystemProperties();
        assertThat(System.getProperty("a"), equalTo("b"));
    }

    @Test
    public void shouldSetCommonConfigurationDefaults() throws Exception {
        ConfigurationCommon config = new ConfigurationCommon();

        mojo.setCommonConfiguration(config);

        assertThat(config.getECMAVersion(), equalTo(CompilerOptions.LanguageMode.ECMASCRIPT_NEXT));
        assertThat(config.isIncludeBranch(), equalTo(true));
        assertThat(config.isIncludeFunction(), equalTo(true));
        assertThat(config.isLocalStorage(), equalTo(true));
        assertThat(config.isDetectCoalesce(), equalTo(false));
    }

    @Test
    public void shouldSetCommonConfiguration() throws Exception {
        ConfigurationCommon config = new ConfigurationCommon();
        ReflectionUtils.setVariableValueInObject(mojo, "ECMAVersion", CompilerOptions.LanguageMode.ECMASCRIPT_NEXT.name());
        ReflectionUtils.setVariableValueInObject(mojo, "includeBranch", false);
        ReflectionUtils.setVariableValueInObject(mojo, "includeFunction", false);
        ReflectionUtils.setVariableValueInObject(mojo, "localStorage", false);
        ReflectionUtils.setVariableValueInObject(mojo, "detectCoalesce", true);

        mojo.setCommonConfiguration(config);

        assertThat(config.getECMAVersion(), equalTo(CompilerOptions.LanguageMode.ECMASCRIPT_NEXT));
        assertThat(config.isIncludeBranch(), equalTo(false));
        assertThat(config.isIncludeFunction(), equalTo(false));
        assertThat(config.isLocalStorage(), equalTo(false));
        assertThat(config.isDetectCoalesce(), equalTo(true));
        assertThat(config.skipInstrumentation("include.js"), equalTo(false));
        assertThat(config.skipInstrumentation("exclude/file.js"), equalTo(false));
        assertThat(config.skipInstrumentation("exclude-reg/file.js"), equalTo(false));
    }

    @Test
    public void shouldInterpretCommonConfigurationExcludePathsCorrectly() throws Exception {
        ConfigurationCommon config = new ConfigurationCommon();
        List<String> instrumentPathArgs = new ArrayList<String>();
        instrumentPathArgs.add(NO_INSTRUMENT_PREFIX + "/exclude/");
        instrumentPathArgs.add(NO_INSTRUMENT_REG_PREFIX + "/exclude-.*/.*.js");
        ReflectionUtils.setVariableValueInObject(mojo, "instrumentPathArgs", instrumentPathArgs);

        mojo.setCommonConfiguration(config);

        assertThat(config.skipInstrumentation("include.js"), equalTo(false));
        assertThat(config.skipInstrumentation("exclude/file.js"), equalTo(true));
        assertThat(config.skipInstrumentation("exclude-reg/file.js"), equalTo(true));
    }

    @Test
    public void shouldInterpretCommonConfigurationIncludeOnlyPathsCorrectly() throws Exception {
        ConfigurationCommon config = new ConfigurationCommon();
        List<String> instrumentPathArgs = new ArrayList<String>();
        instrumentPathArgs.add(ONLY_INSTRUMENT_REG_PREFIX + "/include-.*/*.js");
        ReflectionUtils.setVariableValueInObject(mojo, "instrumentPathArgs", instrumentPathArgs);

        mojo.setCommonConfiguration(config);

        assertThat(config.skipInstrumentation("include-reg/file.js"), equalTo(false));
        assertThat(config.skipInstrumentation("exclude/file.js"), equalTo(true));
        assertThat(config.skipInstrumentation("exclude-reg/file.js"), equalTo(true));
    }

    @Test
    public void shouldDetectIncorrectCommonConfigurationPath() throws Exception {
        ConfigurationCommon config = new ConfigurationCommon();
        List<String> instrumentPathArgs = new ArrayList<String>();
        instrumentPathArgs.add("bad-option");
        ReflectionUtils.setVariableValueInObject(mojo, "instrumentPathArgs", instrumentPathArgs);

        try {
            mojo.setCommonConfiguration(config);
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), equalTo("Invalid instrument path option 'bad-option'"));
        }
    }

    @Test
    public void shouldProcessBuiltInTestType() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testType", QUnit);
        assertThat((QUnitWebDriverRunner) mojo.getWebDriverRunner(), isA(QUnitWebDriverRunner.class));
    }

    @Test
    public void shouldSetTimeoutForJasmine() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "timeOutSeconds", 123);
        JasmineWebDriverRunner webDriverRunner = (JasmineWebDriverRunner) mojo.getWebDriverRunner();
        int timeOutSeconds = ReflectionUtils.getFieldByNameIncludingSuperclasses("timeOutSeconds", JasmineWebDriverRunner.class).getInt(webDriverRunner);
        assertThat(timeOutSeconds, equalTo(123));
    }

    @Test
    public void shouldSetTimeoutForQUnit() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testType", QUnit);
        ReflectionUtils.setVariableValueInObject(mojo, "timeOutSeconds", 123);
        QUnitWebDriverRunner webDriverRunner = (QUnitWebDriverRunner) mojo.getWebDriverRunner();
        Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses("timeOutSeconds", QUnitWebDriverRunner.class);
        field.setAccessible(true);
        int timeOutSeconds = field.getInt(webDriverRunner);
        assertThat(timeOutSeconds, equalTo(123));
    }

    @Test
    public void shouldProcessCustomTestType() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testType", Custom);
        ReflectionUtils.setVariableValueInObject(mojo, "testRunnerClassName", "jscover.maven.QUnitWebDriverRunner");
        assertThat((QUnitWebDriverRunner) mojo.getWebDriverRunner(), isA(QUnitWebDriverRunner.class));
    }

    @Test
    public void shouldFailCustomTestTypeIfNotSpecified() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "jasmine-*pass.html");
        ReflectionUtils.setVariableValueInObject(mojo, "testType", Custom);
        try {
            mojo.getWebDriverRunner();
            fail("Should throw exception");
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), equalTo("Please provide a custom test type class that implements jscover.maven.WebDriverRunner"));
        }
    }

    @Test
    public void shouldFailCustomTestTypeIfWrongType() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testIncludes", "jasmine-*fail.html");
        ReflectionUtils.setVariableValueInObject(mojo, "testType", Custom);
        ReflectionUtils.setVariableValueInObject(mojo, "testRunnerClassName", "java.lang.String");
        try {
            mojo.getWebDriverRunner();
            fail("Should throw exception");
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), equalTo("java.lang.String cannot be cast to jscover.maven.WebDriverRunner"));
        }
    }

    @Test
    public void shouldSetWebClientProxy() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "httpProxy", "localhost:3128");

        Object capability = mojo.getDesiredCapabilities().getCapability(CapabilityType.PROXY);
        System.out.println("capability = " + capability);
        assertThat(capability, notNullValue());
        assertThat(((Proxy) capability).getHttpProxy(), equalTo("localhost:3128"));
    }
}
