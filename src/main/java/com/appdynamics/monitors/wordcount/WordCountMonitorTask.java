package com.appdynamics.monitors.wordcount;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.monitors.wordcount.collector.WordCountCollector;
import org.slf4j.Logger;

import java.util.Map;

public class WordCountMonitorTask implements AMonitorTaskRunnable {
    private static final Logger logger = ExtensionsLoggerFactory.getLogger(WordCountMonitorTask.class);

    private Map<String, String> file;
    private MetricWriteHelper metricWriteHelper;
    private MonitorContextConfiguration monitorContextConfiguration;

    WordCountMonitorTask(Map<String, String> file, MetricWriteHelper metricWriteHelper,
                         MonitorContextConfiguration monitorContextConfiguration) {
        this.file = file;
        this.metricWriteHelper = metricWriteHelper;
        this.monitorContextConfiguration = monitorContextConfiguration;
    }

    @Override
    public void onTaskComplete() {
        logger.info("Completed word count task for file " + file.get("name"));
    }

    @Override
    public void run() {
        logger.debug("Created word count task for file " + file.get("name"));
        WordCountCollector collector = new WordCountCollector(file, metricWriteHelper,
                monitorContextConfiguration);
        collector.populateAndPrintMetrics();
    }
}
