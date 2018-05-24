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
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class ChartModel extends AbstractDescribableImpl<ChartModel> {

    private String title;
    private String fileNameGlob;
    private String key;
    private int limit;
    private String resultsBlackList;
    private String resultsWhiteList;
    private String chartColor;
    
    @DataBoundConstructor
    public ChartModel(String title, String fileNameGlob, String key, int limit, String chartColor) {
        this.title = title;
        this.fileNameGlob = fileNameGlob;
        this.key = key;
        this.limit = limit;
        this.chartColor = chartColor;
    }

    public String getTitle() {
        return title;
    }

    @DataBoundSetter
    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileNameGlob() {
        return fileNameGlob;
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

    public String getChartColor() {
        return chartColor;
    }

    @DataBoundSetter
    public void setChartColor(String chartColor) {
        this.chartColor = chartColor;
    }

    @Override
    public Descriptor<ChartModel> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final ChartDescriptor DESCRIPTOR = new ChartDescriptor();

    public static class ChartDescriptor extends Descriptor<ChartModel> {

        @Override
        public String getDisplayName() {
            return "Chart from properties";
        }

    }

    @DataBoundSetter
    public void setResultBlackList(String resultBlackList){
        this.resultsBlackList = resultBlackList;
    }
    @DataBoundSetter
    public void setResultWhiteList(String resultWhiteList){//4
        this.resultsWhiteList = resultWhiteList;
    }

    public String getResultBlackList(){
        return resultsBlackList;
    }
    public String getResultWhiteList(){//4
        return resultsWhiteList;
    }
}
