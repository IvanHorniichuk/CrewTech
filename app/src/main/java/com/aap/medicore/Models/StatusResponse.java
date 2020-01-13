package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class StatusResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("crew_status")
    @Expose
    private String crewStatus;
    @SerializedName("vehicle_status")
    @Expose
    private String vehicleStatus;

    public Integer getStstus() {
        return status;
    }

    public void setStstus(Integer ststus) {
        this.status = ststus;
    }

    public String getCrewStatus() {
        return crewStatus;
    }

    public void setCrewStatus(String crewStatus) {
        this.crewStatus = crewStatus;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("crewStatus", crewStatus).append("vehicleStatus", vehicleStatus).toString();
    }

}