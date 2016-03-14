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
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PropertiesParser {

    public List<ChartPoint> getReportPoints(Job<?, ?> job, ChartModel chart) {
        List<ChartPoint> list = new ArrayList<>();

        Predicate<String> lineValidator = str -> {
            if (str == null || str.trim().isEmpty()) {
                return false;
            }
            int index = str.indexOf('=');
            if (index <= 0) {
                return false;
            }
            if (!str.substring(0, index).trim().equals(chart.getKey().trim())) {
                return false;
            }
            try {
                Integer.parseInt(str.substring(index + 1).trim());
                return true;
            } catch (Exception ignore) {
            }
            return false;
        };

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + chart.getFileNameGlob());
        for (Run run : job.getBuilds()) {
            if (run.getResult() == null || run.getResult().isWorseThan(Result.UNSTABLE)) {
                continue;
            }
            try (Stream<Path> filesStream = Files.walk(run.getRootDir().toPath()).sequential()) {
                Optional<ChartPoint> optPoint = filesStream
                        .filter(p -> matcher.matches(p.getFileName()))
                        .map(p -> pathToLine(p, lineValidator))
                        .filter(o -> o.isPresent())
                        .map(o -> o.get())
                        .map(s -> new ChartPoint(
                                run.getDisplayName(),
                                run.getNumber(),
                                Integer.parseInt(s.substring(s.indexOf('=') + 1).trim())))
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
        return list;
    }

    private Optional<String> pathToLine(Path path, Predicate<String> lineValidator) {
        try (Stream<String> stream = Files.lines(path)) {
            return stream.filter(lineValidator).findFirst();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
