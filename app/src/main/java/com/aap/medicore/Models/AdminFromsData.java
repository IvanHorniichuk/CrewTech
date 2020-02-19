package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class AdminFromsData {

    @SerializedName("form_id")
    @Expose
    private Integer formId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("sub_title")
    @Expose
    private String subTitle;

    @SerializedName("fields")
    @Expose
    private String fields;

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("formId", formId).append("title", title).toString();
    }

}