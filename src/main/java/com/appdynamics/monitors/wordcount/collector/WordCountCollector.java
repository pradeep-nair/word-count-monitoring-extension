package com.appdynamics.monitors.wordcount.collector;

import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appdynamics.monitors.wordcount.utils.Constants.*;

public class WordCountCollector {
    private static final Logger logger = ExtensionsLoggerFactory.getLogger(WordCountCollector.class);

    private Map<String, String> file;
    private MetricWriteHelper metricWriteHelper;
    private String metricPrefix;

    public WordCountCollector(Map<String, String> file, MetricWriteHelper metricWriteHelper, MonitorContextConfiguration monitorContextConfiguration) {
        this.file = file;
        this.metricWriteHelper = metricWriteHelper;
        this.metricPrefix = monitorContextConfiguration.getMetricPrefix() + file.get("name")
                + DEFAULT_METRIC_SEPARATOR.getPath();
    }

    /**
     * Get word count for the file and display them on metric browser
     */
    public void populateAndPrintMetrics() {
        logger.info("Collecting word count for " + file.get("name"));
        try {
            List<Metric> metrics = getMetricsFromFile();
            metricWriteHelper.transformAndPrintMetrics(metrics);
        } catch (Exception e) {
            logger.error("An error was encountered when processing file " + file.get("name"), e.getMessage());
        }
    }

    /**
     * This method gets metrics and loads them to {@code List<Metric> metrics}
     * @return A {@code List<Metrics>} corresponding to the metrics to be sent to controller
     * @throws IOException encountered from method calls
     */
    public List<Metric> getMetricsFromFile() throws IOException {
        Map<String, Integer> counts = collect();
        List<Metric> metrics = new ArrayList<>();
        for (Map.Entry<String, Integer> entry: counts.entrySet()) {
            Metric metric = new Metric(entry.getKey(), String.valueOf(entry.getValue()), metricPrefix + entry.getKey());
            metrics.add(metric);
        }
        return metrics;
    }

    /**
     * This method collects the word count for the file
     *
     * @throws IOException Throws IOException encountered from {@link BufferedReader}
     */
    private Map<String, Integer> collect() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file.get("path")));
        String line;
        Map<String, Integer> counts = new HashMap<>();
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[^\\w\\s]", "");
            if (!line.isEmpty()) {
                line = line.toLowerCase();
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty())
                        counts.put(word, counts.getOrDefault(word, 0) + 1);
                }
            }
        }
        return counts;
    }
}
