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

        int getSurrounding();
    }

    List<String> getBlacklisted(Job<?, ?> job, final ChartModel chart) {
        return getList(job, chart, new ListProvider() {
            @Override
            public String getList() {
                return chart.getResultBlackList();
            }

            @Override
            public int getSurrounding() {
                return 0;
            }

        });

    }

    List<String> getWhitelisted(Job<?, ?> job, ChartModel chart) {
        return getList(job, chart, new ListProvider() {
            @Override
            public String getList() {
                return chart.getResultWhiteList();
            }

            @Override
            public int getSurrounding() {
                return chart.getRangeAroundWlist();
            }
        });

    }

    /*
    Counting white list size without surroundings which is needed in title over the graph
     */
    List<String> getWhiteListWithoutSurroundings(Job<?, ?> job, ChartModel chart) {
        return getList(job, chart, new ListProvider() {
            @Override
            public String getList() {
                return chart.getResultWhiteList();
            }

            @Override
            public int getSurrounding() {
                return 0;
            }
        });

    }

    private List<String> getList(Job<?, ?> job, ChartModel chart, ListProvider provider) {
        if (provider.getList() == null || provider.getList().trim().isEmpty()) {
            return Collections.emptyList();
        }
        int limit = chart.getLimit();
        Run[] builds = job.getBuilds().toArray(new Run[0]);
        List<String> result = new ArrayList<>(limit);
        for (int i = 0; i < builds.length; i++) {
            Run run = builds[i];
            if (run.getResult() == null || run.getResult().isWorseThan(Result.UNSTABLE)) {
                continue;
            }
            String[] items = provider.getList().split("\\s+");
            for (String item : items) {
                if (run.getDisplayName().matches(item)) {
                    int numberOfFailedBuilds = 0;
                    for (int j = 0; j <= provider.getSurrounding() + numberOfFailedBuilds; j++) {
                        if (addNotFailedBuild(i + j, result, builds)) {
                            numberOfFailedBuilds++;
                        }
                    }
                    numberOfFailedBuilds = 0;
                    for (int j = -1; j >= -(provider.getSurrounding() + numberOfFailedBuilds); j--) {
                        if (addNotFailedBuild(i + j, result, builds)) {
                            numberOfFailedBuilds++;
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean addNotFailedBuild(int position, List<String> result, Run[] builds) {
        if (position >= 0 && position < builds.length) {
            boolean crashed = builds[position].getResult() == null || builds[position].getResult().isWorseThan(Result.UNSTABLE);
            if (crashed) {
                return true;
            }
            /*Preventing duplicates in whitelist. Not because of the graph, there is
            already chunk of code preventing from showing duplicity in the graph.
            (The final list are recreated again with help of these lists)
            Its because lenght of whitelist which is shown over the graph.*/
            if (!result.contains(builds[position].getDisplayName())) {
                result.add(builds[position].getDisplayName());
            }
        }
        return false;
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
        List<String> whiteListWithoutSurroundings = getWhiteListWithoutSurroundings(job, chart);
        List<String> pointsInRangeOfwhitelisted = new ArrayList<>(whitelisted);
        int whiteListSizeWithoutSurroundings = whiteListWithoutSurroundings.toArray().length;
        pointsInRangeOfwhitelisted.removeAll(whiteListWithoutSurroundings);
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
                                extractValue(s),
                                chart.getPointColor(pointsInRangeOfwhitelisted.contains(run.getDisplayName()))))
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

        return new ChartPointsWithBlacklist(list, blacklisted, whitelisted, whiteListSizeWithoutSurroundings);
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
