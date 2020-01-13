package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AdminForms {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("vehicle-checklist-form")
    @Expose
    private AdminCheklist vehicleChecklistForm;

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

    public AdminCheklist getVehicleChecklistForm() {
        return vehicleChecklistForm;
    }

    public void setVehicleChecklistForm(AdminCheklist vehicleChecklistForm) {
        this.vehicleChecklistForm = vehicleChecklistForm;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("message", message).append("vehicleChecklistForm", vehicleChecklistForm).toString();
    }

}