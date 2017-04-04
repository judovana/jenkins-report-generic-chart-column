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

public class ChartPoint {

    private final String buildName;
    private final String buildNameShortened;
    private final int buildNumber;
    private final String value;

    public ChartPoint(String buildName, int buildNumber, String value) {
        this.buildName = buildName;
        if (buildName.length() < 40) {
            buildNameShortened = buildName;
        } else {
            //needs buildName.length() < 60
            //buildNameShortened = buildName.substring(0, 15) + "..."+buildName.substring(buildName.length()/2-7, buildName.length()/2+7)+"..." + buildName.substring(buildName.length() - 15, buildName.length());
            //may make unclear what build it actually is
            String bns = "";
            int nonDigits = 0;
            for (int x = 0; x < buildName.length(); x++) {
                if (!Character.isDigit(buildName.charAt(x))) {
                    nonDigits++;
                    if (nonDigits % 2 == 0 ) {
                        //add every second nondigit
                        bns = bns + buildName.charAt(x);
                    }
                } else {
                    //add all digits
                    bns = bns + buildName.charAt(x);
                    //reset counter, so next char will be not included
                    nonDigits = 0;
                }
            }
            buildNameShortened = bns;

        }
        this.buildNumber = buildNumber;
        this.value = value;
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

}
