package com.aap.medicore.Models;

import java.util.ArrayList;

public class TabsModel {
    private String title = "", jsonData = "", tab_id="", taskId="";
    boolean plus = false;
    private ArrayList<SelectImagesModel> selectedImages;
    private boolean submitted;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJsonData() {
        return jsonData;
    }

    public String getTab_id() {
        return tab_id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isPlus() {
        return plus;
    }

    public void setPlus(boolean plus) {
        this.plus = plus;
    }

    public void setTab_id(String tab_id) {
        this.tab_id = tab_id;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public ArrayList<SelectImagesModel> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(ArrayList<SelectImagesModel> selectedImages) {
        this.selectedImages = selectedImages;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
