package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class CheckListForms {

    @SerializedName("status")
    @Expose
    private Integer status=0;
    @SerializedName("message")
    @Expose
    private String message="";
    @SerializedName("checklist_form")
    @Expose
    private ChecklistForm checklistForm = new ChecklistForm();

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChecklistForm getChecklistForm() {
        return checklistForm;
    }

    public void setChecklistForm(ChecklistForm checklistForm) {
        this.checklistForm = checklistForm;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("message", message).append("checklistForm", checklistForm).toString();
    }

}