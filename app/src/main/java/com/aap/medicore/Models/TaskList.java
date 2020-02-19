package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskList {

    @SerializedName("incidenc_id")
    @Expose
    private Integer incidencId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("job_time")
    @Expose
    private String jobDateTime;

    @SerializedName("job_date_time")
    @Expose
    private String jobDate;

    @SerializedName("mrn")
    @Expose
    private String mrn;
    @SerializedName("mobility")
    @Expose
    private String mobility;
    @SerializedName("infectious_status")
    @Expose
    private String infectiousStatus;
    @SerializedName("medical_requirments")
    @Expose
    private String medicalRequirments;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("from_facitity")
    @Expose
    private String fromFacitity;
    @SerializedName("to_facility")
    @Expose
    private String toFacility;
    @SerializedName("last_updated")
    @Expose
    private Object lastUpdated;
    @SerializedName("draw_over_image")
    @Expose
    private String drawOverImage;

    @SerializedName("notes")
    @Expose
    private String notes;


    @SerializedName("dob")
    @Expose
    private String dob;


    @SerializedName("return_facility")
    @Expose
    private String return_facility;

    @SerializedName("ref")
    @Expose
    private String ref;


    @SerializedName("form")
    @Expose
    private ArrayList<Form> form = null;

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;

    }

    public Integer getIncidencId() {
        return incidencId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getReturn_facility() {
        return return_facility;
    }

    public void setReturn_facility(String return_facility) {
        this.return_facility = return_facility;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setIncidencId(Integer incidencId) {
        this.incidencId = incidencId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobDateTime() {
        return jobDateTime;
    }

    public void setJobDateTime(String jobDateTime) {
        this.jobDateTime = jobDateTime;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getMobility() {
        return mobility;
    }

    public void setMobility(String mobility) {
        this.mobility = mobility;
    }

    public String getInfectiousStatus() {
        return infectiousStatus;
    }

    public void setInfectiousStatus(String infectiousStatus) {
        this.infectiousStatus = infectiousStatus;
    }

    public String getMedicalRequirments() {
        return medicalRequirments;
    }

    public void setMedicalRequirments(String medicalRequirments) {
        this.medicalRequirments = medicalRequirments;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getFromFacitity() {
        return fromFacitity;
    }

    public void setFromFacitity(String fromFacitity) {
        this.fromFacitity = fromFacitity;
    }

    public String getToFacility() {
        return toFacility;
    }

    public void setToFacility(String toFacility) {
        this.toFacility = toFacility;
    }

    public Object getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Object lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDrawOverImage() {
        return drawOverImage;
    }

    public void setDrawOverImage(String drawOverImage) {
        this.drawOverImage = drawOverImage;
    }

    public ArrayList<Form> getForm() {
        return form;
    }

    public void setForm(ArrayList<Form> form) {
        this.form = form;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("incidencId", incidencId).append("name", name).append("jobDateTime", jobDateTime).append("mrn", mrn).append("mobility", mobility).append("infectiousStatus", infectiousStatus).append("medicalRequirments", medicalRequirments).append("account", account).append("orderNo", orderNo).append("fromFacitity", fromFacitity).append("toFacility", toFacility).append("lastUpdated", lastUpdated).append("drawOverImage", drawOverImage).append("form", form).toString();
    }

}