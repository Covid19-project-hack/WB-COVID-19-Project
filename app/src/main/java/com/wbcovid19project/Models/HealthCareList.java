package com.wbcovid19project.Models;

public class HealthCareList {

    String sno, district, name, beds;

    public HealthCareList() {
    }

    public HealthCareList(String sno, String district, String name, String beds) {
        this.sno = sno;
        this.district = district;
        this.name = name;
        this.beds = beds;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }
}
