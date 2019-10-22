package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class VehicleChecklistField {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("placeholder")
    @Expose
    private String placeholder;
    @SerializedName("required")
    @Expose
    private String required;
    @SerializedName("field_id")
    @Expose
    private Integer fieldId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("vehicle-checklist_options")
    @Expose
    private ArrayList<VehicleChecklistOption> vehicleChecklistOptions = null;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public VehicleChecklistField withLabel(String label) {
        this.label = label;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VehicleChecklistField withType(String type) {
        this.type = type;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public VehicleChecklistField withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public VehicleChecklistField withRequired(String required) {
        this.required = required;
        return this;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public VehicleChecklistField withFieldId(Integer fieldId) {
        this.fieldId = fieldId;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VehicleChecklistField withName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<VehicleChecklistOption> getVehicleChecklistOptions() {
        return vehicleChecklistOptions;
    }

    public void setVehicleChecklistOptions(ArrayList<VehicleChecklistOption> vehicleChecklistOptions) {
        this.vehicleChecklistOptions = vehicleChecklistOptions;
    }

    public VehicleChecklistField withVehicleChecklistOptions(ArrayList<VehicleChecklistOption> vehicleChecklistOptions) {
        this.vehicleChecklistOptions = vehicleChecklistOptions;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("label", label).append("type", type).append("placeholder", placeholder).append("required", required).append("fieldId", fieldId).append("name", name).append("vehicleChecklistOptions", vehicleChecklistOptions).toString();
    }

}