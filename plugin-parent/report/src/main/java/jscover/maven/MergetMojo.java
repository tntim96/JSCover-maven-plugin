package jscover.maven;


import jscover.report.ConfigurationForReport;
import jscover.report.Main;
import jscover.report.ReportFormat;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "merge", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST, threadSafe = true)
public class MergetMojo extends AbstractMojo {
    @Parameter
    protected final List<String> mergeDirStrings = new ArrayList<String>();
    @Parameter
    private File destDir = new File("dest");

    public void execute() throws MojoExecutionException, MojoFailureException {
        Main main = new Main();
        main.initialize();
        ConfigurationForReport config = getConfigurationForReport();
        main.setConfig(config);
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
