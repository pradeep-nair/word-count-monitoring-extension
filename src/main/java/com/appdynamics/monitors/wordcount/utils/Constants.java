package com.appdynamics.monitors.wordcount.utils;

public enum Constants {
    METRIC_PREFIX("Custom Metrics|WordCount|"),
    DEFAULT_METRIC_SEPARATOR("|");

    private String path;

    Constants(String path){
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }
}
