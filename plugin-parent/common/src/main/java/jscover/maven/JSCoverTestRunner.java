package jscover.maven;

import jscover.report.ConfigurationForReport;
import jscover.report.Main;
import jscover.util.IoUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

import static java.lang.String.format;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;

class JSCoverTestRunner {
    private Log log = new SystemStreamLog();
    private Main main = new Main();
    protected final WebDriver webClient;
    protected final WebDriverRunner webDriverRunner;
    private final int lineCoverageMinimum;
    private final int branchCoverageMinimum;
    private final int functionCoverageMinimum;
    protected final boolean reportLCOV;
    protected final boolean reportCoberturaXML;
    protected IoUtils ioUtils = IoUtils.getInstance();

    public JSCoverTestRunner(WebDriver webClient, WebDriverRunner webDriverRunner, int lineCoverageMinimum, int branchCoverageMinimum, int functionCoverageMinimum, boolean reportLCOV, boolean reportCoberturaXML) {
        this.webClient = webClient;
        this.webDriverRunner = webDriverRunner;
        this.reportLCOV = reportLCOV;
        this.reportCoberturaXML = reportCoberturaXML;
        this.lineCoverageMinimum = lineCoverageMinimum;
        this.branchCoverageMinimum = branchCoverageMinimum;
        this.functionCoverageMinimum = functionCoverageMinimum;
    }

    protected void generateOtherReportFormats(File dir) {
        if (reportLCOV || reportCoberturaXML) {
            ConfigurationForReport configurationForReport = new ConfigurationForReport();
            main.initialize();
            configurationForReport.setProperties(Main.properties);
            configurationForReport.setJsonDirectory(dir);
            configurationForReport.setSourceDirectory(new File(dir, jscover.Main.reportSrcSubDir));
            main.setConfig(configurationForReport);
            if (reportLCOV) {
                log.info("Generating LCOV coverage data file");
                main.generateLCovDataFile();
            }
            if (reportCoberturaXML) {
                log.info("Generating Cobertura coverage data file");
                main.saveCoberturaXml();
            }
        }
    }

    protected void verifyTotal() throws MojoFailureException {
        webClient.findElement(By.id("summaryTab")).click();
        new WebDriverWait(webClient, 1).until(textToBePresentInElementLocated(By.id("summaryTotal"), "%"));

        verifyField("Line", "summaryTotal", lineCoverageMinimum);
        verifyField("Branch", "branchSummaryTotal", branchCoverageMinimum);
        verifyField("Function", "functionSummaryTotal", functionCoverageMinimum);
    }

    private void verifyField(String coverageName, String fieldName, int percentageMin) throws MojoFailureException {
        int percentage = extractInt(webClient.findElement(By.id(fieldName)).getText());
        log.info(coverageName + " coverage minimum: " + percentageMin + " actual: " + percentage);
        if (percentage < percentageMin) {
            throw new MojoFailureException(format("%s coverage %d less than %d", coverageName, percentage, percentageMin));
        }
    }

    private int extractInt(String percentage) {
        return Integer.parseInt(percentage.replaceAll("%", ""));
    }
}
