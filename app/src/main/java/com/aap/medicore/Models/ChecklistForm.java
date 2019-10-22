package com.aap.medicore.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class ChecklistForm {

    @SerializedName("form_id")
    @Expose
    private Integer formId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String subTitle;
    @SerializedName("timeout")
    @Expose
    private String barcodeEnabled;
    @SerializedName("fields")
    @Expose
    private List<ChecklistFormField> fields = null;

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getBarcodeEnabled() {
        return barcodeEnabled;
    }

    public void setBarcodeEnabled(String barcodeEnabled) {
        this.barcodeEnabled = barcodeEnabled;
    }

    public List<ChecklistFormField> getFields() {
        return fields;
    }

    public void setFields(List<ChecklistFormField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("formId", formId).append("title", title).append("subTitle", subTitle).append("barcodeEnabled", barcodeEnabled).append("fields", fields).toString();
    }

}