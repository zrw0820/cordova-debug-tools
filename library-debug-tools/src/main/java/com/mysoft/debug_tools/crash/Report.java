package com.mysoft.debug_tools.crash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Report implements Serializable {
    public String header;
    public String trace;

    public String log() {
        JSONObject log = new JSONObject();
        try {
            log.put("header", header);
            log.put("trace", trace);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return log.toString();
    }

    public static Report jsonToReport(JSONObject log) {
        try {
            Report report = new Report();
            report.header = log.getString("header");
            report.trace = log.getString("trace");
            return report;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}