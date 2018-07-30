# jenkins-report-generic-chart-column
Generic reusable plugin that will show a chart in column based on properties file.

The plugin reads properties file in your archive, specified by glob, and use one value, deffined by key, to draw a chart for both project and view. The plugin was orriginally designed to show results of benchmarks, but canbe missused for anything key-number what destire chart. Eg total and failde tests summaries. The graph is scaled, so you will never miss smallest change.

* [Properties file](#properties-file)
* [Project summary](#project-summary)
* [View summary](#view-summary)
* [Blacklist and Whitelist](#blacklist-and-whitelist)
* [Project Settings](#project-settings)
* [View Settings](#view-settings)
* [Limitations](#limitations)
* [Future work](#future-work)

## Properties file
To make plugin work, you need a [properties](https://en.wikipedia.org/wiki/.properties) file with results form your job, archived. The properties  file is eg our:
```
lastSuccessfulBuild/artifact/jbb-report/result/specjbb2015-C-20180717-00001/report-00001/specjbb2015-C-20180717-00001.raw 
```
```
# garbage
jbb2015.result.metric.max-jOPS = 22523
jbb2015.result.metric.critical-jOPS = 8902
jbb2015.result.SLA-10000-jOPS = 4774
jbb2015.result.SLA-25000-jOPS = 7442
jbb2015.result.SLA-50000-jOPS = 9643
jbb2015.result.SLA-75000-jOPS = 11833
jbb2015.result.SLA-100000-jOPS = 13791
other garbage
```
The parser is quite forgiving, and will skip garabge. Supports both : and = delimiters.

## Project summary
Hugest graphs are shown in project sumamry.  You can have as much graphs as you wish, and have detailed tooltip:
![project](https://user-images.githubusercontent.com/2904395/43015881-2747cb3a-8c51-11e8-9ccf-c6b4a0189e61.png)
Comparing individual jobs was never more simple:)

## View summary
You can include the graphs to the view:
![view](https://user-images.githubusercontent.com/2904395/43015883-278a339e-8c51-11e8-8656-5165b455d8ef.png)
Comparing individual projects was never more simple:)

You can of course mix it with other propertis or other plugins
![view](https://user-images.githubusercontent.com/2904395/43015875-21c739fc-8c51-11e8-9026-c84127628634.png)

The results in view are sortable - they are sort by last valid result shown in chart.

Comparing individual projects was never ever more simple:)

## Blacklist and Whitelist
you could noted, that the graphs are scalled.  Ifyou have run, which escapes the normality, the scale get corrupeted, and youc an easily miss regression. To fix this, you have balcklist (and whitelist). This is list of regexes,  whic filters (first) out and (second) in the (un)desired builds. It works both with custom_built_name and #build_number. Empty blacklist/whitelist means it is not used at all.

## Project Settings
Project settings and view settings are separate - with both pros and cons!

![project settings](https://user-images.githubusercontent.com/2904395/43015882-2768c9b6-8c51-11e8-819b-e3005d13fc03.png)
Most important is **Glob pattern for the report file to parse**, which lets you specify not absolute (glob) path to your properties file and of course **Key to look for in the report file** which tetls chart what value to render.  **Chart name** and **color** are  cosmetic, **blacklist** and **whitelist** were already described.  **Number of data points to show** is how many successful builds (counted from end) should be displayed.  If you are in doubts, each suspicious field have help.

## View Settings
Project settings and view settings are separate - with both pros and cons!

![view settings](https://user-images.githubusercontent.com/2904395/43015884-27a74146-8c51-11e8-9a8e-92dcfba248b8.png)
You can see that the settings of view are same - thus duplicated with all its pros and cons...

The screenshots are form older version without whitelist and with just few help buttons. This get recently improved, but we were lazy to uplaod new screenshots.

## Limitations

The limitations flows from double settings and from fact that each chart can show only only one value. The non-shared blacklist/whitelist is a negative which we are working on to improve. One line only is considered as - due toscalled graph - definitely positive.

## Future work
We wish to improve whitelist/balcklist feature, so it can be used to generate wievs comparing selected runs across jobs with some kind of neigbrhood

This plugin depends on https://github.com/judovana/jenkins-chartjs-plug
