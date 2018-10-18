/*
 * The MIT License
 *
 * Copyright 2016 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.report.genericchart;

import hudson.plugins.Chartjs;

public class ChartPoint {

    private final String buildName;
    private final String buildNameShortened;
    private final int buildNumber;
    private final String value;
    private final String pointColor;

    public ChartPoint(String buildName, int buildNumber, String value, String pointColor) {
        this.buildName = buildName;
        this.buildNameShortened = Chartjs.getShortName(buildName, buildNumber);
        this.buildNumber = buildNumber;
        this.value = value;
        this.pointColor = pointColor;
    }

    public String getBuildName() {
        return buildName;
    }

    public String getBuildNameShortened() {
        return buildNameShortened;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getValue() {
        return value;
    }

    public String getPointColor() {
        return pointColor;
    }

}
