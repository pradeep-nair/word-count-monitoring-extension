package com.appdynamics.monitors.wordcount;

//import org.junit.Before;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static com.appdynamics.monitors.wordcount.WordCountMonitor.populate;
import static org.junit.Assert.assertEquals;

public class WordCountTest {

    @Test
    public void checkWordCount() {
        try {
            Map<String, Integer> result = populate("/Users/pradeep.nair/Documents/test");
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
