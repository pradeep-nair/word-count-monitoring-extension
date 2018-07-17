package com.appdynamics.monitors.wordcount;

//import org.junit.Before;

import com.appdynamics.extensions.AMonitorJob;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.util.PathResolver;
import com.appdynamics.monitors.wordcount.collector.WordCountCollector;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appdynamics.monitors.wordcount.utils.Constants.*;
import static org.junit.Assert.assertEquals;

public class WordCountTest {

    @Test
    public void checkWordCount() {
        MonitorContextConfiguration monitorContextConfiguration =
                new MonitorContextConfiguration("WordCountTest", DEFAULT_METRIC_PREFIX.getPath(),
                        PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        try {
            monitorContextConfiguration.setConfigYml("src/test/resources/conf/config.yml");
            List<Map<String, String>> files = (List<Map<String, String>>) monitorContextConfiguration.getConfigYml().get("filesToProcess");
            WordCountCollector mon = new WordCountCollector(files.get(0), Mockito.mock(MetricWriteHelper.class), monitorContextConfiguration);
//            Map<String, String> args = new HashMap<>();
//            args.put("config-file", "src/test/resources/conf/config.yml");
//            args.put("metric-file", "src/test/resources/conf/metrics.xml");
            List<Metric> metrics = mon.getMetricsFromFile();
            Map<String, Integer> result = new HashMap<>();
            for (Metric m: metrics){
                result.put(m.getMetricName(), Integer.valueOf(m.getMetricValue()));
            }
            assertEquals((int) result.get("the"), 2L);
            assertEquals((int) result.get("quick"), 1L);
            assertEquals((int) result.get("brown"), 1L);
            assertEquals((int) result.get("jumps"), 1L);
            assertEquals((int) result.get("over"), 1L);
            assertEquals((int) result.get("lazy"), 1L);
            assertEquals((int) result.get("dog"), 1L);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
