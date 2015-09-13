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


@Mojo(name = "format", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST, threadSafe = true)
public class ReportFormatMojo extends AbstractMojo {
  @Parameter
  protected boolean lCOV;
  @Parameter(defaultValue = "true")
  protected boolean coberturaXML = true;
  @Parameter(readonly = true)
  private File reportDir;
  @Parameter(readonly = true)
  private File srcDir;

  public void execute() throws MojoExecutionException, MojoFailureException {
    Main main = new Main();
    ConfigurationForReport config = getConfigurationForReport();
    main.setConfig(config);
    main.initialize();
    if (coberturaXML)
      main.saveCoberturaXml();
    if (lCOV)
      main.generateLCovDataFile();
  }

  private ConfigurationForReport getConfigurationForReport() {
    ConfigurationForReport config = new ConfigurationForReport();
    config.setJsonDirectory(reportDir);
    config.setSourceDirectory(srcDir);
    return config;
  }

}
