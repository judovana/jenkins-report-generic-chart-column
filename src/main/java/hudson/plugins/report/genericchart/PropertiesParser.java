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

import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PropertiesParser {

    private static interface ListProvider {

        String getList();
    }

    List<String> getBlacklisted(Job<?, ?> job, final ChartModel chart) {
        return getList(job, chart, new ListProvider() {
            @Override
            public String getList() {
                return chart.getResultBlackList();
            }
        });

    }

    List<String> getWhitelisted(Job<?, ?> job, ChartModel chart) {
        return getList(job, chart, new ListProvider() {
            @Override
            public String getList() {
                return chart.getResultWhiteList();
            }
        });

    }

    private List<String> getList(Job<?, ?> job, ChartModel chart, ListProvider provider) {
        int limit = chart.getLimit();
        List<String> result = new ArrayList<>(limit);
        for (Run run : job.getBuilds()) {
            if (run.getResult() == null || run.getResult().isWorseThan(Result.UNSTABLE)) {
                continue;
            }
            if (provider.getList() != null && !provider.getList().trim().isEmpty()) {
                String[] items = provider.getList().split("\\s+");
                for (String item : items) {
                    if (run.getDisplayName().matches(item)) {
                        result.add(run.getDisplayName());
                    }
                }
            }
        }
        return result;

    }

    public ChartPointsWithBlacklist getReportPointsWithBlacklist(Job<?, ?> job, ChartModel chart) {
        List<ChartPoint> list = new ArrayList<>();

        Predicate<String> lineValidator = str -> {
            if (str == null || str.trim().isEmpty()) {
                return false;
            }
            int index = getBestDelimiterIndex(str);
            if (index == Integer.MAX_VALUE) {
                return false;
            }
            if (!str.substring(0, index).trim().equals(chart.getKey().trim())) {
                return false;
            }
            try {
                Double.parseDouble(str.substring(index + 1).trim());
                return true;
            } catch (Exception ignore) {
            }
            return false;
        };

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + chart.getFileNameGlob());
        List<String> blacklisted = getBlacklisted(job, chart);
        List<String> whitelisted = getWhitelisted(job, chart);
        for (Run run : job.getBuilds()) {
            if (run.getResult() == null || run.getResult().isWorseThan(Result.UNSTABLE)) {
                continue;
            }
            if (blacklisted.contains(run.getDisplayName())) {
                continue;
            }
            if (!whitelisted.contains(run.getDisplayName()) && !whitelisted.isEmpty()) {
                continue;
            }

            try (Stream<Path> filesStream = Files.walk(run.getRootDir().toPath()).sequential()) {
                Optional<ChartPoint> optPoint = filesStream
                        .filter((p) -> matcher.matches(p.getFileName()))
                        .map((p) -> pathToLine(p, lineValidator))
                        .filter((o) -> o.isPresent())
                        .map(o -> o.get())
                        .map(s -> new ChartPoint(
                                run.getDisplayName(),
                                run.getNumber(),
                                extractValue(s)))
                        .findFirst();
                if (optPoint.isPresent()) {
                    list.add(optPoint.get());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (list.size() == chart.getLimit()) {
                break;
            }
        }

        Collections.reverse(list);

        return new ChartPointsWithBlacklist(list, blacklisted, whitelisted);
    }

    private int getBestDelimiterIndex(String str) {
        int index1 = str.indexOf('=');
        int index2 = str.indexOf(':');
        if (index1 < 0) {
            index1 = Integer.MAX_VALUE;
        }
        if (index2 < 0) {
            index2 = Integer.MAX_VALUE;
        }
        int index = Math.min(index1, index2);
        return index;
    }

    private Optional<String> pathToLine(Path path, Predicate<String> lineValidator) {
        try (Stream<String> stream = Files.lines(path)) {
            return stream.filter(lineValidator).findFirst();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    private String extractValue(String s) {
        return s.substring(getBestDelimiterIndex(s) + 1).trim();
    }
}
