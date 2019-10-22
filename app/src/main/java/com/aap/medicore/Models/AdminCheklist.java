package com.aap.medicore.Models;



import java.util.List;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class AdminCheklist {

    @SerializedName("forms")
    @Expose
    private List<AdminFromsData> forms = null;

    public List<AdminFromsData> getForms() {
        return forms;
    }

    public void setForms(List<AdminFromsData> forms) {
        this.forms = forms;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("forms", forms).toString();
    }}

