package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;

public class EquipmentChecklistForm {

    @SerializedName("equipment_checklist_fields")
    @Expose
    private ArrayList<EquipmentChecklistField> equipmentChecklistFields = null;
    @SerializedName("form_type")
    @Expose
    private String formType;
    @SerializedName("sub_title")
    @Expose
    private String sub_title;

    @SerializedName("barcode_enabled")
    @Expose
    private String barcode_enabled;

    @SerializedName("form_id")
    @Expose
    private Integer formId;



    public ArrayList<EquipmentChecklistField> getEquipmentChecklistFields() {
        return equipmentChecklistFields;
    }

    public void setEquipmentChecklistFields(ArrayList<EquipmentChecklistField> equipmentChecklistFields) {
        this.equipmentChecklistFields = equipmentChecklistFields;
    }

    public EquipmentChecklistForm withEquipmentChecklistFields(ArrayList<EquipmentChecklistField> equipmentChecklistFields) {
        this.equipmentChecklistFields = equipmentChecklistFields;
        return this;
    }

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

    public EquipmentChecklistForm withFormType(String formType) {
        this.formType = formType;
        return this;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public EquipmentChecklistForm withFormId(Integer formId) {
        this.formId = formId;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("equipmentChecklistFields", equipmentChecklistFields).append("formType", formType).append("formId", formId).toString();
    }

}