/*
 * The MIT License
 *
 * Copyright 2016 root.
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

import java.util.List;

public class ChartPointsWithBlacklist {

    private final List<ChartPoint> points;
    private final List<String> blacklisted;
    private final List<String> whitelisted;
    private final int whiteListSizeWithoutSurroundings;

    public ChartPointsWithBlacklist(List<ChartPoint> points, List<String> blacklisted, List<String> whitelisted, int whiteListSizeWithoutSurroundings) {
        this.blacklisted = blacklisted;
        this.points = points;
        this.whitelisted = whitelisted;
        this.whiteListSizeWithoutSurroundings = whiteListSizeWithoutSurroundings;
    }

    public List<ChartPoint> getPoints() {
        return points;
    }

    public List<String> getBlacklist() {
        return blacklisted;
    }

    public List<String> getWhitelist() {
        return whitelisted;
    }

    public int getWhiteListSizeWithoutSurroundings() {
        return whiteListSizeWithoutSurroundings;
    }

}
