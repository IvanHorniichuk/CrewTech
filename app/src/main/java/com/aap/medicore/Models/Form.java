package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class Form {

    @SerializedName("form_id")
    @Expose
    private Integer formId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String sub_title;

    @SerializedName("barcode_enabled")
    @Expose
    private String barcode_enabled;

    @SerializedName("plus")
    @Expose
    private boolean plus = false;

    public boolean isPlus() {
        return plus;
    }

    public void setPlus(boolean plus) {
        this.plus = plus;
    }

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fields")
    @Expose
    private ArrayList<Field> fields = null;

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBarcode_enabled() {
        return barcode_enabled;
    }

    public void setBarcode_enabled(String barcode_enabled) {
        this.barcode_enabled = barcode_enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("formId", formId).append("title", title).append("type", type).append("fields", fields).toString();
    }

}