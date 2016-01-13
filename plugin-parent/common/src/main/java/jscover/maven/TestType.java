package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;

public enum TestType {
    QUnit, JasmineTrivialReporter, Jasmine, Mocha, Custom;

    public WebDriverRunner getWebDriverRunner() throws MojoExecutionException {
        if (this == QUnit) {
            return new QUnitWebDriverRunner();
        } else if (this == JasmineTrivialReporter) {
            return new JasmineTrivialReporterWebDriverRunner();
        } else if (this == Jasmine) {
            return new JasmineDefaultReporterWebDriverRunner();
        } else if (this == Mocha) {
            return new MochaDefaultReporterWebDriverRunner();
        }
        throw new MojoExecutionException("Please provide a custom test type class that implements "+WebDriverRunner.class.getName());
    }
}
