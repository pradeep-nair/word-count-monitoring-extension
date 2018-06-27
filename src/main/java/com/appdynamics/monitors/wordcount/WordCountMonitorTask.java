package com.appdynamics.monitors.wordcount;

import com.appdynamics.monitors.wordcount.utils.Constants;
import com.singularity.ee.agent.systemagent.api.MetricWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCountMonitorTask { //implements Runnable {

    private String metricPrefix;
    private List<Map<String, String>> filesToProcess;
    private static final String DEFAULT_METRIC_SEPARATOR = Constants.DEFAULT_METRIC_SEPARATOR.getPath();

    /**
     *
     * @param metricPrefix
     * @param filesToProcess
     */
    WordCountMonitorTask(String metricPrefix, List<Map<String, String>> filesToProcess){
        this.metricPrefix = metricPrefix;
        this.filesToProcess = filesToProcess;
    }

//    @Override
//    public void run() {
//        try {
//            Map<String, Integer> resultMap = populate();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * This method populates word count for all the files and also prints the metrics
     */
    public void populateAndPrint(WordCountMonitor mon){
        try {
            Map<String, Map<String, Integer>> resultMap = new HashMap<>();
            for (Map<String, String> file : filesToProcess) {
                Map<String, Integer> countMap = populate(file.get("path"));
                resultMap.put(file.get("name"), countMap);
            }
            printMetrics(mon, resultMap);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Method to do word count for a file
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static Map<String, Integer> populate(String path) throws FileNotFoundException, IOException {
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
     * This method prints the word count metrics for each file
     *
     * @param resultMap
     */
    private void printMetrics(WordCountMonitor mon, Map<String, Map<String, Integer>> resultMap){
        for (Map.Entry<String, Map<String, Integer>> entry: resultMap.entrySet()){
            String file = entry.getKey();
            for(Map.Entry<String, Integer> counts: entry.getValue().entrySet()){
                printMetric(mon, file, counts.getKey(), counts.getValue());
            }
        }
    }

    /**
     * Returns metrics to AppDynamics Controller
     * @param file  file name
     * @param key   metric name - the word in the file
     * @param value count for the key
     */
    private void printMetric(WordCountMonitor mon, String file, String key, Integer value){
        String aggregation = MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION;
        String timeRollup = MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT;
        String cluster = MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE;
        String metricPath = metricPrefix + file + DEFAULT_METRIC_SEPARATOR + key;
        MetricWriter metricWriter = mon.getMetricWriter(metricPath, aggregation, timeRollup, cluster);
        metricWriter.printMetric(String.valueOf(value));
    }
}
