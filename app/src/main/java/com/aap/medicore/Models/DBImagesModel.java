package com.aap.medicore.Models;

import android.net.Uri;

public class DBImagesModel {

    private Uri tempUri;
    private int id;
    private String incidenceId;

    public Uri getTempUri() {
        return tempUri;
    }

    public void setTempUri(Uri tempUri) {
        this.tempUri = tempUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIncidenceId() {
        return incidenceId;
    }

    public void setIncidenceId(String incidenceId) {
        this.incidenceId = incidenceId;
    }
}
