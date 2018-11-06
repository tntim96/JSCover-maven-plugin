package jscover.maven;

import jscover.report.ConfigurationForReport;
import jscover.report.Main;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.ReflectionUtils;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@RunWith(MockitoJUnitRunner.class)
public class JSCoverTestRunnerTest {;
    @Mock private Main main;
    @Mock private WebDriver webClient;
    @Mock private WebElement webElement;
    @Mock private WebDriverRunner webDriverRunner;
    @Mock private WebElement summaryTotalWebElement;
    @Mock private WebElement branchSummaryTotalWebElement;
    @Mock private WebElement functionSummaryTotalWebElement;
    private File dir = new File(".");
    private int lineCoverageMinimum = 50;
    private int branchCoverageMinimum = 50;
    private int functionCoverageMinimum = 50;
    private boolean reportLCOV;
    private boolean reportCoberturaXML;
    private JSCoverTestRunner runner;

    @Before
    public void setUp() throws Exception {
        runner = new JSCoverTestRunner(webClient, webDriverRunner, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML);
        ReflectionUtils.setVariableValueInObject(runner, "main", main);
        given(webClient.findElement(By.id("summaryTab"))).willReturn(webElement);
        given(webClient.findElement(By.id("summaryTotal"))).willReturn(summaryTotalWebElement);
        given(webClient.findElement(By.id("branchSummaryTotal"))).willReturn(branchSummaryTotalWebElement);
        given(webClient.findElement(By.id("functionSummaryTotal"))).willReturn(functionSummaryTotalWebElement);
        given(summaryTotalWebElement.getText()).willReturn("90%");
        given(branchSummaryTotalWebElement.getText()).willReturn("90%");
        given(functionSummaryTotalWebElement.getText()).willReturn("90%");
    }

    @Test
    public void shouldPass() throws MojoFailureException {
        runner.verifyTotal();
    }

    @Test
    public void shouldFailOnLineCoverage() {
        given(summaryTotalWebElement.getText()).willReturn("10%");
        try {
            runner.verifyTotal();
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Line coverage 10 less than 50"));
        }
    }

    @Test
    public void shouldFailOnBranchCoverage() {
        given(branchSummaryTotalWebElement.getText()).willReturn("10%");
        try {
            runner.verifyTotal();
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Branch coverage 10 less than 50"));
        }
    }

    @Test
    public void shouldFailOnFunctionCoverage() {
        given(functionSummaryTotalWebElement.getText()).willReturn("10%");
        try {
            runner.verifyTotal();
        } catch(MojoFailureException e) {
            assertThat(e.getMessage(), equalTo("Function coverage 10 less than 50"));
        }
    }

    @Test
    public void shouldNotGenerateLCOVReport() throws Exception {
        runner.generateOtherReportFormats(dir);

        verify(main, times(0)).generateLCovDataFile();
        verify(main, times(0)).saveCoberturaXml();
        verify(main, times(0)).setConfig(argThat(Matchers.any(ConfigurationForReport.class)));
    }

    @Test
    public void shouldGenerateLCOVReport() throws Exception {
        ReflectionUtils.setVariableValueInObject(runner, "reportLCOV", true);
        runner.generateOtherReportFormats(dir);
        verify(main).generateLCovDataFile();
        verify(main, times(0)).saveCoberturaXml();
    }

    @Test
    public void shouldGenerateCoberturaReport() throws Exception {
        ReflectionUtils.setVariableValueInObject(runner, "reportCoberturaXML", true);
        runner.generateOtherReportFormats(dir);
        verify(main, times(0)).generateLCovDataFile();
        verify(main).saveCoberturaXml();
    }

    @Test
    public void shouldGenerateBothReports() throws Exception {
        ReflectionUtils.setVariableValueInObject(runner, "reportLCOV", true);
        ReflectionUtils.setVariableValueInObject(runner, "reportCoberturaXML", true);
        runner.generateOtherReportFormats(dir);
        verify(main).generateLCovDataFile();
        verify(main).saveCoberturaXml();
        verify(main).setConfig(argThat(new TypeSafeMatcher<ConfigurationForReport>() {
            @Override
            protected boolean matchesSafely(ConfigurationForReport configurationForReport) {
                return configurationForReport.getJsonDirectory().equals(dir)
                        && configurationForReport.getSourceDirectory().getPath().endsWith(jscover.Main.reportSrcSubDir);
            }

            public void describeTo(Description description) {
            }
        }));
    }
}