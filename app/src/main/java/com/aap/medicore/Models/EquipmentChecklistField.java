package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class EquipmentChecklistField {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("equipment_checklist_options")
    @Expose
    private ArrayList<EquipmentChecklistOption> equipmentChecklistOptions = null;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public EquipmentChecklistField withLabel(String label) {
        this.label = label;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EquipmentChecklistField withType(String type) {
        this.type = type;
        return this;
    }

    public ArrayList<EquipmentChecklistOption> getEquipmentChecklistOptions() {
        return equipmentChecklistOptions;
    }

    public void setEquipmentChecklistOptions(ArrayList<EquipmentChecklistOption> equipmentChecklistOptions) {
        this.equipmentChecklistOptions = equipmentChecklistOptions;
    }

    public EquipmentChecklistField withEquipmentChecklistOptions(ArrayList<EquipmentChecklistOption> equipmentChecklistOptions) {
        this.equipmentChecklistOptions = equipmentChecklistOptions;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public EquipmentChecklistField withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public EquipmentChecklistField withRequired(String required) {
        this.required = required;
        return this;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public EquipmentChecklistField withFieldId(Integer fieldId) {
        this.fieldId = fieldId;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipmentChecklistField withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("label", label).append("type", type).append("equipmentChecklistOptions", equipmentChecklistOptions).append("placeholder", placeholder).append("required", required).append("fieldId", fieldId).append("name", name).toString();
    }

}