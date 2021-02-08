package com.genedev.retinalclassifierfull.classes;

/**
 * Created by Gene on 4/29/2018.
 */

public class HistoryHelper{
    String action;
    String date;
    String time;
    public String id;
    private String pushId;
    public HistoryHelper() {

    }

    public HistoryHelper(String action,String date, String time) {
        this.action = action;
        this.date = date;
        this.time = time;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getActionTime() {
        return time;
    }
    public void setActionTime(String time) {
        this.time = time;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
    public void setId(String id) {
        this.id = id;
    }
}