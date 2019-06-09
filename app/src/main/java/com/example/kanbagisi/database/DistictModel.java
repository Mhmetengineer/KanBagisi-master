package com.example.kanbagisi.database;

public class DistictModel {
    String district;
    int districtID;

    public DistictModel() {
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }

    public DistictModel( int districtID,String district) {
        this.district = district;
        this.districtID = districtID;
    }
}
