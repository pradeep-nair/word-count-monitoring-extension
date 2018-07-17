package com.appdynamics.monitors.wordcount;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;

import com.appdynamics.extensions.util.AssertUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static com.appdynamics.monitors.wordcount.utils.Constants.*;

public class WordCountMonitor extends ABaseMonitor {
    private static final Logger logger = ExtensionsLoggerFactory.getLogger(WordCountMonitor.class);

    @Override
    protected String getDefaultMetricPrefix() {
        return DEFAULT_METRIC_PREFIX.getPath();
    }

    @Override
    public String getMonitorName() {
        return MONITOR_NAME.getPath();
    }

    @Override
    protected void doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider) {
        List<Map<String, String>> files = (List<Map<String, String>>) getContextConfiguration().getConfigYml().get("filesToProcess");
        for (Map<String, String> file : files) {
            logger.debug("Starting word count for file " + file.get("name"));
            AssertUtils.assertNotNull(file.get("path"), "The path to file cannot be empty");
            WordCountMonitorTask task = new WordCountMonitorTask(file, tasksExecutionServiceProvider.getMetricWriteHelper(), getContextConfiguration());
            tasksExecutionServiceProvider.submit(file.get("name"), task);
        }
    }

    @Override
    protected int getTaskCount() {
        AssertUtils.assertNotNull(getContextConfiguration().getConfigYml(), "The 'config.yml' file has not been created.");
        List<Map<String, String>> files = (List<Map<String, String>>) getContextConfiguration().getConfigYml().get("filesToProcess");
        AssertUtils.assertNotNull(files, "The 'filesToProcess' section in config.yml is not initialised");
        return files.size();
    }
}
