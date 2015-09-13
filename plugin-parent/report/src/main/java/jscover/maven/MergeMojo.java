package jscover.maven;


import jscover.report.ConfigurationForReport;
import jscover.report.Main;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "merge", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST, threadSafe = true)
public class MergeMojo extends AbstractMojo {
    @Parameter(required = true)
    protected final List<String> mergeDirStrings = new ArrayList<String>();
    @Parameter(required = true)
    private File destDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Main main = new Main();
        ConfigurationForReport config = getConfigurationForReport();
        main.setConfig(config);
        main.initialize();
        main.mergeReports();
    }

    private ConfigurationForReport getConfigurationForReport() {
        ConfigurationForReport config = new ConfigurationForReport();
        config.setMergeDestDir(destDir);
        List<File> mergeDirs = new ArrayList<File>();
        for (String mergeDirString : mergeDirStrings) {
            mergeDirs.add(new File(mergeDirString));
        }
        config.setMergeDirs(mergeDirs);
        return config;
    }
}
