package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class VehicleDetail {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("registrationno")
    @Expose
    private String registrationno;
    @SerializedName("vehicle_id")
    @Expose
    private Integer vehicleId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("bed_space")
    @Expose
    private Object bedSpace;
    @SerializedName("engin_houspower")
    @Expose
    private Object enginHouspower;
    @SerializedName("manufacturing_year")
    @Expose
    private Object manufacturingYear;
    @SerializedName("registration_year")
    @Expose
    private Object registrationYear;
    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegistrationno() {
        return registrationno;
    }

    public void setRegistrationno(String registrationno) {
        this.registrationno = registrationno;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getBedSpace() {
        return bedSpace;
    }

    public void setBedSpace(Object bedSpace) {
        this.bedSpace = bedSpace;
    }

    public Object getEnginHouspower() {
        return enginHouspower;
    }

    public void setEnginHouspower(Object enginHouspower) {
        this.enginHouspower = enginHouspower;
    }

    public Object getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(Object manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public Object getRegistrationYear() {
        return registrationYear;
    }

    public void setRegistrationYear(Object registrationYear) {
        this.registrationYear = registrationYear;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("userId", userId).append("registrationno", registrationno).append("vehicleId", vehicleId).append("status", status).append("bedSpace", bedSpace).append("enginHouspower", enginHouspower).append("manufacturingYear", manufacturingYear).append("registrationYear", registrationYear).append("vehicleName", vehicleName).toString();
    }

}