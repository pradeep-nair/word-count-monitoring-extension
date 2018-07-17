package com.appdynamics.monitors.wordcount.utils;

public enum Constants {
    DEFAULT_METRIC_PREFIX("Custom Metrics|WordCount2.1|"),
    DEFAULT_METRIC_SEPARATOR("|"),
    MONITOR_NAME("WordCountMonitor");

    private String path;

    Constants(String path){
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }
}
