2.0.7 / 2019-07-07
==================
  * Upgrade JSCover 2.0.7->2.0.8
  * Upgrade Jasmine 3.3.0->3.4.0

2.0.6 / 2019-01-01
==================
  * Upgrade JSCover 2.0.6->2.0.7
  * Make Chrome and Firefox run in headless mode 
  * Upgrade Selenium 3.13.0->3.141.59
  * Upgrade Jasmine 3.1.0->3.3.0
  * Upgrade QUnit 2.6.1->2.8.0
  * Upgrade Mocha 3.5.0->5.2.0

2.0.5 / 2018-05-29
==================
  * Upgrade JSCover 2.0.5->2.0.6
  * Upgrade Selenium 3.11.0->3.13.0

2.0.4 / 2018-04-26
==================
  * Upgrade JSCover 2.0.4->2.0.5
  * Upgrade Selenium 3.9.1->3.11.0
  * Upgrade Jasmine 3.0.0->3.1.0

2.0.3 / 2018-02-17
==================
  * Upgrade JSCover 2.0.3->2.0.4
  * Upgrade Selenium 3.7.1->3.9.1
  * Upgrade Jasmine 2.8.0->3.0.0
  * Upgrade QUnit 2.4.0->2.5.0
  * Upgrade maven-plugin-plugin 3.4->3.5.1

2.0.2 / 2017-11-14
==================
  * Upgrade JSCover 2.0.2->2.0.3
  * Upgrade Selenium 3.4.0->3.7.1
  * Upgrade Jasmine 2.7.0->2.8.0

2.0.1 / 2017-08-04
==================
  * Upgrade JSCover 2.0.1->2.0.2
  * Upgrade QUnit 2.3.2->2.4.0
  * Upgrade Mocha 3.4.2->3.5.0
  * Upgrade Jasmine 2.6.4->2.7.0

2.0.0 / 2017-07-23
==================
  * Upgrade JSCover 1.1.0->2.0.1
  * Upgrade Selenium 3.0.1->3.4.0
  * Upgrade phantomjsdriver 1.3.0-> ghostdriver 2.1.0
  * Upgrade QUnit 2.1.1->2.3.@
  * Upgrade Mocha 3.2.0->3.4.2
  * Upgrade Jasmine 2.5.2->2.6.4

1.1.0 / 2017-02-14
==================
  * Upgrade JSCover 1.0.25->1.1.0
  * Upgrade QUnit 2.0.1->2.1.1
  * Upgrade Mocha 3.0.2->3.2.0

1.0.20 / 2016-12-14
==================
  * Upgrade JSCover 1.0.24->1.0.25
  * Upgrade Selenium 2.53.0->3.0.1
  * Upgrade Jasmine 2.4.1->2.5.2
  * Upgrade Mocha 2.4.5->3.0.2

1.0.19 / 2016-09-25
==================
  * Upgrade phantomjsdriver 1.2.1->1.3.0, QUnit 1.23.0->2.0.1
  * Cannot specify proxy for webclient (https://github.com/tntim96/JSCover-maven-plugin/issues/17)

1.0.18 / 2016-04-05
==================
  * Set JVM level to 1.6
  * Upgrade JSCover 1.0.23->1.0.24
  * Remove Jasmine 1 support
  * Upgrade Selenium 2.48.2->2.53.0
  * Upgrade JQuery 2.1.4->2.2.2, QUnit 1.20.0->1.23.0
  * Upgrade Mocha 2.2.5->2.4.5

1.0.17 / 2016-01-04
==================
  * JSCover 1.0.23, Upgrade Jasmine 2.3.4->2.4.1

1.0.16 / 2015-09-15
==================
  * Add file-instrumentation only goal `jscover-file:instrument` (https://github.com/tntim96/JSCover-maven-plugin/issues/14)
  * Add merge goal `jscover-report:merge` (https://github.com/tntim96/JSCover-maven-plugin/issues/15)
  * Add format goal `jscover-report:format` (https://github.com/tntim96/JSCover-maven-plugin/issues/15)
  * JSCover 1.0.22

1.0.15 / 2015-08-26
==================
  * JSCover 1.0.21
  * Selenium 2.47.1
  * Add Mocha support
  * Upgrade JQuery 1.11.2->2.1.4, QUnit 1.14.0->1.18.0

1.0.14 / 2015-06-20
==================
  * JSCover 1.0.19
  * Update Jasmine 2.1.3->2.3.4
  * Selenium 2.46.0

1.0.13 / 2015-02-07
==================
  * JSCover 1.0.17

1.0.12 / 2015-01-06
==================
  * JSCover 1.0.16
  * Update PhantomJS 1.1.0->1.2.1
  * Update PlexusUtils 3.0.18->3.0.21
  * Update JUnit 4.11->4.12
  * Update Jasmine 2.0.3->2.1.3

1.0.11 / 2014-10-17
==================
  * JSCover 1.0.15
  * Use separate parameter for save report timeout

1.0.10 / 2014-10-16
==================
  * Selenium 2.43.1
  * Use existing timeout parameter for save report timeout (https://github.com/tntim96/JSCover-maven-plugin/issues/8)

1.0.9 / 2014-08-30
==================
  * JSCover 1.0.14

1.0.8 / 2014-07-24
==================
  * JSCover 1.0.13
  * Add logging
  * Improve robustness
  * Internal: Extract common test runner code

1.0.7 / 2014-07-03
==================
  * Change goals to 'jscover'
  * Replace FileMojo 'destDir' with existing 'reportDir'

1.0.6 / 2014-07-02
==================
  * Add Jasmine 2 support
  * Add custom test runner support
  * Add clear message if `testDirectory` is not a sub-directory of `srcDir` (https://github.com/tntim96/JSCover/issues/146)
  * Clear localStorage before each run if using it (issue for PhantomJS)
  * Make test timeout configurable
  * JSCover 1.0.12

1.0.5 / 2014-06-05
==================
  * JSCover 1.0.11, Selenium 2.42.2

1.0.4 / 2014-06-03
==================
  * JSCover 1.0.10, Selenium 2.42.1
  * Fix chrome for file-system mode
  * Add HTML5 local storage support and turn it on by default
  * Shutdown JSCover server cleanly
  * Internal: Add test infrastructure and Mojo tests

1.0.3 / 2014-05-23
==================
  * JSCover 1.0.9, Update dependencies

1.0.2 / 2014-05-02
==================
  * JSCover 1.0.8, Selenium 2.41.0

1.0.1 / 2014-01-22
==================
  * JSCover 1.0.7, Selenium 2.40.0

1.0.0 / 2014-01-22
==================
  * Basic working structure
