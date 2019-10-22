package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class VehicleChecklistForm {

    @SerializedName("form_type")
    @Expose
    private String formType;
    @SerializedName("vehicle-checklist-fields")
    @Expose
    private ArrayList<VehicleChecklistField> vehicleChecklistFields = null;
    @SerializedName("form_id")
    @Expose
    private Integer formId;

    @SerializedName("sub_title")
    @Expose
    private String sub_title;

    @SerializedName("barcode_enabled")
    @Expose
    private String barcode_enabled;


    public String getBarcode_enabled() {
        return barcode_enabled;
    }

    public void setBarcode_enabled(String barcode_enabled) {
        this.barcode_enabled = barcode_enabled;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public VehicleChecklistForm withFormType(String formType) {
        this.formType = formType;
        return this;
    }

    public ArrayList<VehicleChecklistField> getVehicleChecklistFields() {
        return vehicleChecklistFields;
    }

    public void setVehicleChecklistFields(ArrayList<VehicleChecklistField> vehicleChecklistFields) {
        this.vehicleChecklistFields = vehicleChecklistFields;
    }

    public VehicleChecklistForm withVehicleChecklistFields(ArrayList<VehicleChecklistField> vehicleChecklistFields) {
        this.vehicleChecklistFields = vehicleChecklistFields;
        return this;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public VehicleChecklistForm withFormId(Integer formId) {
        this.formId = formId;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("formType", formType).append("vehicleChecklistFields", vehicleChecklistFields).append("formId", formId).toString();
    }

}