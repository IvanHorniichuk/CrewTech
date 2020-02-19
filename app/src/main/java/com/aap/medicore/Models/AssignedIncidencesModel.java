package com.aap.medicore.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssignedIncidencesModel {
    private int id;
    String json = "";
    private String date;
    private Date objDate;

    public Date getObjDate() {
        return objDate;
    }

    public void setObjDate(Date objDate) {
        this.objDate = objDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setDate(String date) {
        this.date = date;
        try {
            objDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        return date;
    }
}
