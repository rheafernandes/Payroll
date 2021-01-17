package com.fileee.models.mongo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {
    private String name;
    private String payType;
    @JsonProperty("_id")
    private long id;
    private WorkLog workLog;

    @Transient
    public static final String SEQUENCE_NAME = "staff_sequence";

    public Staff() {
    }

    public Staff(String name, String payType) {
        this.name = name;
        this.payType = payType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkLog getWorkLog() {
        return workLog;
    }

    public void setWorkLog(WorkLog workLog) {
        this.workLog = workLog;
    }
}
