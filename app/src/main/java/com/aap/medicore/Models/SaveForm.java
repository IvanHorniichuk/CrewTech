package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class SaveForm {

    @SerializedName("form_id")
    @Expose
    private String formId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fields")
    @Expose
    private ArrayList<SaveField> fields = null;
    @SerializedName("images")
    @Expose
    private ArrayList<String> images = null;

    private String sub_title;

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<SaveField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<SaveField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("formId", formId).append("title", title).append("type", type).append("fields", fields).toString();
    }

}