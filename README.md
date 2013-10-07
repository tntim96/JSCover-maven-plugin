JSCover-maven-plugin
================================

Soon to be published, this will automatically run tests JavaScript tests and collect coverage.

Currently supports:
* QUnit and Jasmine test frameworks
* WebDriver drivers (e.g. PhantomJS, Firefox, Chrome, IE, etc...)
* Coverage thresholds
* Multiple report formats (HTML, LCOV, Cobertura XML)
* Ant style test inclusion and exclusions

Run with `mvn jscover:server`

```XML
            <plugin>
                <groupId>com.github.tntim96</groupId>
                <artifactId>jscover-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <testDirectory>./src/test/javascript/qunit/test</testDirectory>
                    <testIncludes>index.html</testIncludes>
                    <testType>QUnit</testType>
                    <lineCoverageMinimum>82</lineCoverageMinimum>
                    <branchCoverageMinimum>48</branchCoverageMinimum>
                    <functionCoverageMinimum>70</functionCoverageMinimum>
                    <instrumentPathArgs>
                        <arg>--no-instrument=target</arg>
                    </instrumentPathArgs>
                    <reportCoberturaXML>true</reportCoberturaXML>
                    <reportLCOV>true</reportLCOV>
                    <!--<webDriverClassName>org.openqa.selenium.firefox.FirefoxDriver</webDriverClassName>-->
                    <!--<webDriverClassName>org.openqa.selenium.htmlunit.HtmlUnitDriver</webDriverClassName>-->
<!--
                    <webDriverClassName>org.openqa.selenium.chrome.ChromeDriver</webDriverClassName>
                    <systemProperties>
                        <property>
                            <name>webdriver.chrome.driver</name>
                            <value>C:/java/chromedriver_win32_2.0/chromedriver.exe</value>
                        </property>
                    </systemProperties>
-->
                </configuration>
            </plugin>
```