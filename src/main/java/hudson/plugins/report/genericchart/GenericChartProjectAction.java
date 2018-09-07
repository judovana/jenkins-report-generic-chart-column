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

import hudson.model.Action;
import hudson.model.Job;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericChartProjectAction implements Action {

    private final Job<?, ?> job;
    private final List<ChartModel> charts;

    public GenericChartProjectAction(Job<?, ?> job, List<ChartModel> charts) {
        this.job = job;
        this.charts = charts;
    }

    public List<ReportChart> getCharts() {
        if (charts == null || charts.isEmpty()) {
            return new ArrayList<>();
        }
        PropertiesParser parser = new PropertiesParser();
        List<ReportChart> list = charts.stream()
                .sequential()
                .map(m -> ReportChart.createReportChart(m, parser, job))
                .filter(r -> r.getPoints() != null && r.getPoints().size() > 0)
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

}
