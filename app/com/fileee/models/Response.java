package com.fileee.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fileee.enums.ResponseCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response implements Serializable {
    private String id;
    private String ver;
    private String ts;
    private ResponseCode responseCode = ResponseCode.OK;
    private Map<String, Object> result = new HashMap<String, Object>();

    public Response() {

    }

    public Response(String id, String ver, String ts, ResponseCode responseCode, Map<String, Object> result) {
        this.id = id;
        this.ver = ver;
        this.ts = ts;
        this.responseCode = responseCode;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
