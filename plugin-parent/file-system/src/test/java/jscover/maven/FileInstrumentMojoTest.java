package jscover.maven;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FileInstrumentMojoTest {
    private FileInstrumentMojo mojo = new FileInstrumentMojo();

    protected File getFilePath(String pathname) {
        if (System.getProperty("user.dir").endsWith("JSCover-maven-plugin"))
            pathname = "plugin-parent/file-system/" + pathname;
        return new File(pathname).getAbsoluteFile();
    }

    @Before
    public void setUp() throws Exception {
        deleteDirectory(getFilePath("../data/target"));
        ReflectionUtils.setVariableValueInObject(mojo, "srcDir", getFilePath("../data/src"));
        ReflectionUtils.setVariableValueInObject(mojo, "testDirectory", getFilePath("../data/src/test/javascript"));
        ReflectionUtils.setVariableValueInObject(mojo, "reportDir", getFilePath("../data/target"));
        ReflectionUtils.setVariableValueInObject(mojo, "instrumentPathArgs", Arrays.asList("--no-instrument=main/webapp/js/vendor/", "--no-instrument=test"));
        ReflectionUtils.setVariableValueInObject(mojo, "excludeArgs", Arrays.asList("--exclude=main/java", "--exclude=main/resources", "--exclude-reg=test/java$"));
    }

    private void deleteDirectory(File dir) {
        if (!dir.exists())
            return;
        for (File file : dir.listFiles())
            if (file.isFile())
                file.delete();
            else
                deleteDirectory(file);
        dir.delete();
    }

    @Test
    public void shouldInstrumentSpecifiedFiles() throws Exception {
        mojo.execute();
        assertThat(new File(getFilePath("../data/target"), "jscoverage.json").exists(), equalTo(false));
        assertThat(new File(getFilePath("../data/target"), "jscoverage.js").exists(), equalTo(true));

        verifyFileInstrumentation(new File(getFilePath("../data/target/main/webapp/js"), "code.js"), true);
        verifyFileInstrumentation(new File(getFilePath("../data/target/main/webapp/js/vendor"), "jquery-2.1.4.min.js"), false);
        verifyFileInstrumentation(new File(getFilePath("../data/target/test/javascript/lib/PhantomJS"), "run-jscover-jasmine.js"), false);
        verifyFileInstrumentation(new File(getFilePath("../data/target/test/javascript/spec"), "jasmine2-code-pass-spec.js"), false);
    }

    private void verifyFileInstrumentation(File file, boolean instrumented) throws IOException {
        assertThat(file.exists(), equalTo(true));
        String code = IOUtils.toString(file.toURI(), "UTF-8");
        assertThat(code.contains("this._$jscoverage = {};"), equalTo(instrumented));
    }

    @Test
    public void shouldFailIfTestDirectoryNotSubDirectory() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "testDirectory", getFilePath("../data/target"));
        try {
            mojo.execute();
            fail("Should have thrown exception");
        } catch (MojoExecutionException e) {
            String regex = "'testDirectory' '.*/data/target' should be a sub-directory of 'srcDir' '.*/data/src'";
            if (File.separatorChar == '\\')
                regex = regex.replaceAll("/", "\\\\\\\\");
            Pattern pattern = Pattern.compile(regex);
            assertThat(String.format("Message was: '%s'", e.getMessage()), pattern.matcher(e.getMessage()).matches(), is(true));
        }
    }

    @Test
    public void shouldFailIfExcludePathArgumentInvalid() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "excludeArgs", Arrays.asList("exclude=main"));
        try {
            mojo.execute();
            fail("Should have thrown exception");
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), equalTo("Invalid exclude argument 'exclude=main'"));
        }
    }
}