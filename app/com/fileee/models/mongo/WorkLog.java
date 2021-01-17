package com.fileee.models.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkLog {
    Map<String, Float> workLog;
    double rate;

    public WorkLog() { }

    public Map<String, Float> getWorkLog() {
        return workLog;
    }

    public void setWorkLog(Map<String, Float> workLog) {
        this.workLog = workLog;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
