
package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class AddVitalForm {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("form")
    @Expose
    private Form form;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("form", form).toString();
    }

}
