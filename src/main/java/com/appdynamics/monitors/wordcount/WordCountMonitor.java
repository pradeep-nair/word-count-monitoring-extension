package com.appdynamics.monitors.wordcount;

import com.appdynamics.monitors.wordcount.utils.Constants;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCountMonitor extends AManagedMonitor{

    private String metricPrefix;
    private static final String DEFAULT_METRIC_SEPARATOR = Constants.DEFAULT_METRIC_SEPARATOR.getPath();

    /**
     * Entry point for all other methods
     *
     * @param map
     * @param taskExecutionContext
     * @return
     * @throws TaskExecutionException
     */
    @Override
    public TaskOutput execute(Map<String, String> map, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        Yaml yml = new Yaml();
        Map<String, ?> config = null;
        try (BufferedReader ip = new BufferedReader(new FileReader(map.get("config-file")))) {
            config = yml.load(ip);
            metricPrefix = (String) config.get("metricPrefix");
            List<Map<String, String>> filesToProcess = (List<Map<String, String>>) config.get("filesToProcess");
            Map<String, Map<String, Integer>> resultMap = new HashMap<>();
            for (Map<String, String> file : filesToProcess) {
                Map<String, Integer> countMap = populate(file.get("path"));
                resultMap.put(file.get("name"), countMap);
            }
            printMetrics(resultMap);

        } catch (IOException iex) {
            iex.printStackTrace();
        }
        return null;
    }

    /**
     * This method populates the word count for each file
     *
     * @param path  Path to file location
     * @return Returns a {@link Map} of word {@link String} as key and count {@link Integer}
     *         of the word as value
     * @throws IOException
     */
    protected static Map<String, Integer> populate(String path) throws IOException {
        Map<String, Integer> countMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[^\\w\\s]", "");
            if (!line.isEmpty()) {
                line = line.toLowerCase();
                String[] words = line.split("\\s+");
                // iterate thru words
                for (String word : words) {
                    if (!word.isEmpty())
                        countMap.put(word, countMap.getOrDefault(word, 0) + 1);
                }
            }
        }
        return countMap;
    }

    /**
     * Helper method to return metrics to the controller
     *
     * @param resultMap A {@link Map} of fileName {@link String} as key and map {@link Map}
     *                  of the word count stats as value
     */
    private void printMetrics(Map<String, Map<String, Integer>> resultMap){
        for (Map.Entry<String, Map<String, Integer>> entry: resultMap.entrySet()){
            String file = entry.getKey();
            for(Map.Entry<String, Integer> counts: entry.getValue().entrySet()){
                printMetric(file, counts.getKey(), counts.getValue());
            }
        }
    }

    /**
     * Returns metrics to AppDynamics Controller
     *
     * @param file  file name
     * @param key   metric name - the word in the file
     * @param value count for the key
     */
    private void printMetric(String file, String key, Integer value){
        String aggregation = MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION;
        String timeRollup = MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT;
        String cluster = MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE;
        String metricPath = metricPrefix + file + DEFAULT_METRIC_SEPARATOR + key;
        MetricWriter metricWriter = getMetricWriter(metricPath, aggregation, timeRollup, cluster);
        metricWriter.printMetric(String.valueOf(value));
    }
}
