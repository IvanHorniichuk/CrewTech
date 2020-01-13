package com.aap.medicore.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Task {

    @SerializedName("incidenc_id")
    @Expose
    private Integer incidencId;
    @SerializedName("incident_date")
    @Expose
    private String incidentDate;
    @SerializedName("incident_time")
    @Expose
    private String incidentTime;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("affected_person_details")
    @Expose
    private String affectedPersonDetails;
    @SerializedName("harm_person")
    @Expose
    private String harmPerson;
    @SerializedName("incident_repoted_other")
    @Expose
    private Boolean incidentRepotedOther;
    @SerializedName("repoted_agency_name")
    @Expose
    private String repotedAgencyName;
    @SerializedName("incident_witness")
    @Expose
    private Boolean incidentWitness;
    @SerializedName("witnes_name")
    @Expose
    private String witnesName;

    public Integer getIncidencId() {
        return incidencId;
    }

    public void setIncidencId(Integer incidencId) {
        this.incidencId = incidencId;
    }

    public String getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(String incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentTime() {
        return incidentTime;
    }

    public void setIncidentTime(String incidentTime) {
        this.incidentTime = incidentTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAffectedPersonDetails() {
        return affectedPersonDetails;
    }

    public void setAffectedPersonDetails(String affectedPersonDetails) {
        this.affectedPersonDetails = affectedPersonDetails;
    }

    public String getHarmPerson() {
        return harmPerson;
    }

    public void setHarmPerson(String harmPerson) {
        this.harmPerson = harmPerson;
    }

    public Boolean getIncidentRepotedOther() {
        return incidentRepotedOther;
    }

    public void setIncidentRepotedOther(Boolean incidentRepotedOther) {
        this.incidentRepotedOther = incidentRepotedOther;
    }

    public String getRepotedAgencyName() {
        return repotedAgencyName;
    }

    public void setRepotedAgencyName(String repotedAgencyName) {
        this.repotedAgencyName = repotedAgencyName;
    }

    public Boolean getIncidentWitness() {
        return incidentWitness;
    }

    public void setIncidentWitness(Boolean incidentWitness) {
        this.incidentWitness = incidentWitness;
    }

    public String getWitnesName() {
        return witnesName;
    }

    public void setWitnesName(String witnesName) {
        this.witnesName = witnesName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("incidencId", incidencId).append("incidentDate", incidentDate).append("incidentTime", incidentTime).append("location", location).append("affectedPersonDetails", affectedPersonDetails).append("harmPerson", harmPerson).append("incidentRepotedOther", incidentRepotedOther).append("repotedAgencyName", repotedAgencyName).append("incidentWitness", incidentWitness).append("witnesName", witnesName).toString();
    }

}