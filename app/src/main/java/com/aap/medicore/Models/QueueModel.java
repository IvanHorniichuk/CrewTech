package com.aap.medicore.Models;


public class QueueModel {

    private String id="", taskID="";
    private String json = "", state = "", title = "", message = "";
    private String numSubmitted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getState() {
        return state;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNumSubmitted(String numSubmitted) {
        this.numSubmitted = numSubmitted;
    }

    public String getNumSubmitted() {
        return numSubmitted;
    }
}
