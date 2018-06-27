package com.appdynamics.monitors.wordcount;

import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WordCountMonitor extends AManagedMonitor{

//    private static final String METRIC_PREFIX = "Custom Metrics|Files|WordCount|";
//    private static final String DEFAULT_METRIC_SEPARATOR = "|";

    @Override
    public TaskOutput execute(Map<String, String> map, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        Yaml yml = new Yaml();
        // YAML data structure
        Map<String, ?> config = null;
        // Read config.yml
        try (BufferedReader ip = new BufferedReader(new FileReader(map.get("config-file")))) {
            config = yml.load(ip);
            String metricPrefix = (String) config.get("metricPrefix");
            List<Map<String, String>> listOfFiles = (List<Map<String, String>>) config.get("filesToProcess");
            WordCountMonitorTask task = new WordCountMonitorTask(metricPrefix, listOfFiles);
            task.populateAndPrint(this);
//            for (Map<String, String> file : listOfFiles) {
//                System.out.println("Word Count for " + file.get("name"));
//                Map<String, Integer> countMap = doCount(file.get("path"));
////                System.out.println(yml.dump(countMap));
//            }
        } catch (IOException iex) {
            iex.printStackTrace();
        }
        return null;
    }
}
