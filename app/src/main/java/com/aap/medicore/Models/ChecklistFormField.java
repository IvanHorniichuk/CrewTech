package com.aap.medicore.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class ChecklistFormField {

    @SerializedName("field_id")
    @Expose
    private Integer fieldId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("placeholder")
    @Expose
    private String placeholder;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("required")
    @Expose
    private String required;
    @SerializedName("barcode")
    @Expose
    private boolean barcode;
    @SerializedName("options")
    @Expose
    private List<ChecklistFormOption> options = null;

    public Integer getFieldId() {
        return fieldId;

    }

    public boolean isBarcode() {
        return barcode;
    }

    public void setBarcode(boolean barcode) {
        this.barcode = barcode;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public List<ChecklistFormOption> getOptions() {
        return options;
    }

    public void setOptions(List<ChecklistFormOption> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("fieldId", fieldId).append("type", type).append("label", label).append("placeholder", placeholder).append("name", name).append("required", required).append("options", options).toString();
    }

}