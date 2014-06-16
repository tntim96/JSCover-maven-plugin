package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;

public enum TestType {
    QUnit, JasmineHtmlReporter, JasmineTrivialReporter, Jasmine2, Custom;

    public WebDriverRunner getWebDriverRunner() throws MojoExecutionException {
        if (this == QUnit) {
            return new QUnitWebDriverRunner();
        } else if (this == JasmineHtmlReporter) {
            return new JasmineHtmlReporterWebDriverRunner();
        } else if (this == JasmineTrivialReporter) {
            return new JasmineTrivialReporterWebDriverRunner();
        } else if (this == Jasmine2) {
            return new Jasmine2DefaultReporterWebDriverRunner();
        }
        throw new MojoExecutionException("Please provide a custom test type class that implements "+WebDriverRunner.class.getName());
    }
}
