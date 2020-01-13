package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class EquipmentChecklistResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("equipment_checklist_form")
    @Expose
    private EquipmentChecklistForm equipmentChecklistForm;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EquipmentChecklistResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public EquipmentChecklistResponse withStatus(Integer status) {
        this.status = status;
        return this;
    }

    public EquipmentChecklistForm getEquipmentChecklistForm() {
        return equipmentChecklistForm;
    }

    public void setEquipmentChecklistForm(EquipmentChecklistForm equipmentChecklistForm) {
        this.equipmentChecklistForm = equipmentChecklistForm;
    }

    public EquipmentChecklistResponse withEquipmentChecklistForm(EquipmentChecklistForm equipmentChecklistForm) {
        this.equipmentChecklistForm = equipmentChecklistForm;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("message", message).append("status", status).append("equipmentChecklistForm", equipmentChecklistForm).toString();
    }

}