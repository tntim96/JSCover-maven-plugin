JSCover-maven-plugin
====================

Project for managing JSCover Maven plugins.

Try the [snapshots on sonatype](https://oss.sonatype.org/content/repositories/snapshots/com/github/tntim96/).

This will automatically run tests JavaScript tests and collect coverage.

Currently supports:
* QUnit and Jasmine test frameworks
* WebDriver drivers (e.g. PhantomJS, Firefox, Chrome, IE, etc...)
* Coverage thresholds
* Multiple report formats (HTML, LCOV, Cobertura XML)
* Ant style test inclusion and exclusions

See the server configuration [here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/server).

See the file configuration [here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/file-system).