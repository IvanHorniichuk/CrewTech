package com.aap.medicore.Models;

import android.net.Uri;

import java.io.File;

public class SelectImagesModel {

    private Uri tempUri;
    private File finalImage;
    private String name;
    private String formId;
    private String taskId;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Uri getTempUri() {
        return tempUri;
    }

    public void setTempUri(Uri tempUri) {
        this.tempUri = tempUri;
    }

    public File getFinalImage() {
        return finalImage;
    }

    public void setFinalImage(File finalImage) {
        this.finalImage = finalImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
