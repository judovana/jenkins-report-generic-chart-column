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
import hudson.model.Result;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class GenericChartColumn extends ListViewColumn {

    private String fileNameGlob;
    private String key;
    private int limit;
    private String columnCaption;
    private String chartColor;

    @DataBoundConstructor
    public GenericChartColumn(String fileNameGlob, String key, int limit, String columnCaption, String chartColor) {
        this.fileNameGlob = fileNameGlob;
        this.key = key;
        this.limit = limit;
        this.columnCaption = columnCaption;
        this.chartColor = chartColor;
    }

    public List<ReportPoint> getReportPoints(Job<?, ?> job) {
        List<ReportPoint> list = new ArrayList<>();

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + fileNameGlob);
        for (Run run : job.getBuilds()) {
            if (run.getResult() == null || run.getResult().isWorseThan(Result.UNSTABLE)) {
                continue;
            }
            try {
                Optional<ReportPoint> optPoint = Files.walk(run.getRootDir().toPath())
                        .filter(p -> matcher.matches(p.getFileName()))
                        .map(p -> lines(p)
                                .filter(this::validateLine)
                                .findFirst().get())
                        .map(s -> new ReportPoint(
                                String.valueOf(run.getNumber()),
                                Integer.parseInt(s.substring(s.indexOf('=') + 1).trim())))
                        .findFirst();
                if (optPoint.isPresent()) {
                    list.add(optPoint.get());
                }
            } catch (Exception ignore) {
            }
            if (list.size() == limit) {
                break;
            }
        }

        Collections.reverse(list);
        return list;
    }

    private boolean validateLine(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        int index = str.indexOf('=');
        if (index <= 0) {
            return false;
        }
        if (!str.substring(0, index).trim().equals(key.trim())) {
            return false;
        }
        try {
            Integer.parseInt(str.substring(index + 1).trim());
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    private Stream<String> lines(Path path) {
        try {
            return Files.lines(path);
        } catch (Exception ignore) {
        }
        return Stream.empty();
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

}
