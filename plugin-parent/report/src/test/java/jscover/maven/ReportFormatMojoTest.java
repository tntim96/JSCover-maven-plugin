package jscover.maven;

import jscover.util.IoUtils;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReportFormatMojoTest {
    private ReportFormatMojo mojo = new ReportFormatMojo();

    private IoUtils ioUtils = IoUtils.getInstance();
    private File srcDir = getFilePath("src/main/webapp/js");
    private File reportDir = getFilePath("target/reportFormat");
    private String data = ioUtils.loadFromClassPath("/jscoverage-select-1.json");

    @Before
    public void setUp() throws IOException, IllegalAccessException {
        deleteDirectory(reportDir);
        ioUtils.copy(data, new File(reportDir, "jscoverage.json"));

        ReflectionUtils.setVariableValueInObject(mojo, "reportDir", reportDir);
        ReflectionUtils.setVariableValueInObject(mojo, "srcDir", srcDir);
    }

    protected File getFilePath(String pathname) {
        return new File(getPath(pathname)).getAbsoluteFile();
    }


    private String getPath(String pathname) {
        if (System.getProperty("user.dir").endsWith("JSCover-maven-plugin"))
            pathname = "plugin-parent/report/" + pathname;
        return pathname;
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
    public void shouldGenerateCoberturaXml() throws Exception {
        mojo.execute();
        File xmlFile = new File(reportDir, "cobertura-coverage.xml");
        assertThat(xmlFile.exists(), equalTo(true));
        assertThat(ioUtils.loadFromFileSystem(xmlFile), containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(new File(reportDir, "jscover.lcov").exists(), equalTo(false));
    }

    @Test
    public void shouldGenerateLco() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "lCOV", true);
        ReflectionUtils.setVariableValueInObject(mojo, "coberturaXML", false);
        mojo.execute();
        File xmlFile = new File(reportDir, "jscover.lcov");
        assertThat(xmlFile.exists(), equalTo(true));
        assertThat(ioUtils.loadFromFileSystem(xmlFile), containsString("SF:"));
        assertThat(ioUtils.loadFromFileSystem(xmlFile), containsString("src/main/webapp/js/script.js"));
        assertThat(new File(reportDir, "cobertura-coverage.xml").exists(), equalTo(false));
    }

}