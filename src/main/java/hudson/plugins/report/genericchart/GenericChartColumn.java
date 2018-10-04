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

import hudson.Extension;
import hudson.model.Job;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import java.util.List;
import java.util.UUID;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class GenericChartColumn extends ListViewColumn {

    private String fileNameGlob;
    private String key;
    private int limit;
    private String columnCaption;
    private String chartColor;
    private String resultsBlackList;
    private String resultsWhiteList;
    private int rangeAroundWlist;

    @DataBoundConstructor
    public GenericChartColumn(String fileNameGlob, String key, int limit, String columnCaption, String chartColor, int rangeAroundWlist) {
        this.fileNameGlob = fileNameGlob;
        this.key = key;
        this.limit = limit;
        this.columnCaption = columnCaption;
        this.chartColor = chartColor;
        this.rangeAroundWlist = rangeAroundWlist;
    }

    public List<ChartPoint> getReportPoints(Job<?, ?> job) {
        ChartModel model = new ChartModel(key, fileNameGlob, key, limit, chartColor, rangeAroundWlist);
        model.setResultBlackList(resultsBlackList);
        model.setResultWhiteList(resultsWhiteList);
        return new PropertiesParser().getReportPointsWithBlacklist(job, model).getPoints();
    }

    public String getLatestResult(Job<?, ?> job) {
        List<ChartPoint> results = getReportPoints(job);
        if (!results.isEmpty()) {
            return results.get(results.size() - 1).getValue();
        } else {
            return "0";
        }
    }

    public String getFileNameGlob() {
        return fileNameGlob;
    }

    public String generateChartName() {
        return UUID.randomUUID().toString();
    }

    @DataBoundSetter
    public void setFileNameGlob(String fileNameGlob) {
        this.fileNameGlob = fileNameGlob;
    }

    public String getKey() {
        return key;
    }

    @DataBoundSetter
    public void setKey(String key) {
        this.key = key;
    }

    public int getLimit() {
        return limit;
    }

    @DataBoundSetter
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String getColumnCaption() {
        return columnCaption;
    }

    @DataBoundSetter
    public void setColumnCaption(String columnCaption) {
        this.columnCaption = columnCaption;
    }

    public String getChartColor() {
        return chartColor;
    }

    @DataBoundSetter
    public void setChartColor(String chartColor) {
        this.chartColor = chartColor;
    }

    @Extension
    public static final GenericChartColumnDescriptor DESCRIPTOR = new GenericChartColumnDescriptor();

    public static class GenericChartColumnDescriptor extends ListViewColumnDescriptor {

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Chart";
        }

    }

    @DataBoundSetter
    public void setResultBlackList(String resultBlackList) {
        this.resultsBlackList = resultBlackList;
    }

    public String getResultBlackList() {
        return resultsBlackList;
    }

    @DataBoundSetter
    public void setResultWhiteList(String resultWhiteList) {
        this.resultsWhiteList = resultWhiteList;
    }

    public String getResultWhiteList() {
        return resultsWhiteList;
    }

    public int getRangeAroundWlist() {
        return rangeAroundWlist;
    }

    @DataBoundSetter
    public void setRangeAroundWlist(int rangeAroundWlist) {
        this.rangeAroundWlist = rangeAroundWlist;
    }

}
