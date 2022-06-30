package jscover.maven;

import jscover.util.IoUtils;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MergeMojoTest {
    private final MergeMojo mojo = new MergeMojo();

    private final IoUtils ioUtils = IoUtils.getInstance();
    private final String reportDir1 = getPath("target/report1");
    private final String reportDir2 = getPath("target/report2");
    private final String reportDir12 = "target/report12";
    private final String data1 = ioUtils.loadFromClassPath("/jscoverage-select-1.json");
    private final String data2 = ioUtils.loadFromClassPath("/jscoverage-select-3.json");

    @Before
    public void setUp() throws IOException, IllegalAccessException {
        deleteDirectory(getFilePath("target/report12").getCanonicalFile());
        ioUtils.copy(data1, new File(getFilePath(reportDir1), "jscoverage.json"));
        ioUtils.copy(data2, new File(getFilePath(reportDir2), "jscoverage.json"));

        List<String> mergeDirStrings = new ArrayList<>();
        mergeDirStrings.add(reportDir1);
        mergeDirStrings.add(reportDir2);
        ReflectionUtils.setVariableValueInObject(mojo, "mergeDirStrings", mergeDirStrings);
        ReflectionUtils.setVariableValueInObject(mojo, "destDir", getFilePath(reportDir12));
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
    public void shouldMergeReports() {
        mojo.execute();
        File jsonFile = new File(getFilePath(reportDir12), "jscoverage.json");
        assertThat(jsonFile.exists(), equalTo(true));
        String mergedData = "{\"/script.js\":{\"lineData\":[null,2,1,null,2,0,null,2,2,2,1,null,1,0,null,1,1,null,0,0,null,2,2,2,2,2]}}";
        assertThat(ioUtils.loadFromFileSystem(jsonFile), equalTo(mergedData));
        assertThat(new File(getFilePath(reportDir12), "jscover.lcov").exists(), equalTo(false));
    }

}