package com.aap.medicore.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class vehicleData {

    @SerializedName("registrationno")
    @Expose
    private String registrationno;
    @SerializedName("registration_year")
    @Expose
    private Integer registrationYear;
    @SerializedName("manufacturing_year")
    @Expose
    private Integer manufacturingYear;
    @SerializedName("colour")
    @Expose
    private String colour;
    @SerializedName("engin_houspower")
    @Expose
    private String enginHouspower;
    @SerializedName("bed_space")
    @Expose
    private String bedSpace;

    public String getRegistrationno() {
        return registrationno;
    }

    public void setRegistrationno(String registrationno) {
        this.registrationno = registrationno;
    }

    public Integer getRegistrationYear() {
        return registrationYear;
    }

    public void setRegistrationYear(Integer registrationYear) {
        this.registrationYear = registrationYear;
    }

    public Integer getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(Integer manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getEnginHouspower() {
        return enginHouspower;
    }

    public void setEnginHouspower(String enginHouspower) {
        this.enginHouspower = enginHouspower;
    }

    public String getBedSpace() {
        return bedSpace;
    }

    public void setBedSpace(String bedSpace) {
        this.bedSpace = bedSpace;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("registrationno", registrationno).append("registrationYear", registrationYear).append("manufacturingYear", manufacturingYear).append("colour", colour).append("enginHouspower", enginHouspower).append("bedSpace", bedSpace).toString();
    }

}