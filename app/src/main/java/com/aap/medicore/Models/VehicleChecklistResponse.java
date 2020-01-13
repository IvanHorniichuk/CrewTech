package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class VehicleChecklistResponse {

    @SerializedName("vehicle-checklist-form")
    @Expose
    private VehicleChecklistForm vehicleChecklistForm;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;

    public VehicleChecklistForm getVehicleChecklistForm() {
        return vehicleChecklistForm;
    }

    public void setVehicleChecklistForm(VehicleChecklistForm vehicleChecklistForm) {
        this.vehicleChecklistForm = vehicleChecklistForm;
    }

    public VehicleChecklistResponse withVehicleChecklistForm(VehicleChecklistForm vehicleChecklistForm) {
        this.vehicleChecklistForm = vehicleChecklistForm;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VehicleChecklistResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public VehicleChecklistResponse withStatus(Integer status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("vehicleChecklistForm", vehicleChecklistForm).append("message", message).append("status", status).toString();
    }

}