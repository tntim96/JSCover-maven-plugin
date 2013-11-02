JSCover-maven-plugin
====================

Maven plugins for [JSCover](http://tntim96.github.com/JSCover/).

This will automatically run the JavaScript tests and generate JavaScript code coverage reports.

Currently supports:
* Server and file-mode plugins (yes, generate your coverage reports without ever running a server)
* QUnit and Jasmine test frameworks
* WebDriver drivers (e.g. PhantomJS, Firefox, Chrome, IE, etc...)
* Coverage thresholds
* Multiple report formats (JSCover HTML, LCOV, Cobertura XML)
* Ant style test inclusion and exclusions

See the server configuration
[here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/server).

See the file configuration
[here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/file-system).

## Working examples

See [JSCover-Samples](https://github.com/tntim96/JSCover-Samples).


## Snapshots

This plugin is currently available as
[snapshots on sonatype](https://oss.sonatype.org/content/repositories/snapshots/com/github/tntim96/).
