package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Option {

    @SerializedName("field_id")
    @Expose
    private Integer fieldId;
    @SerializedName("option_id")
    @Expose
    private Integer optionId;
    @SerializedName("label")
    @Expose
    private String label;

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("fieldId", fieldId).append("optionId", optionId).append("label", label).toString();
    }

}