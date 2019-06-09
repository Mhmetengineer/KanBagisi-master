package com.example.kanbagisi.user;


import java.util.ArrayList;

public class UserModel {
    String name,surname,tc,phoneNumber,adress,mail;
    int city,district,bloodGroup;
    ArrayList<SelectDateModel> selectDateArrayList = new ArrayList<SelectDateModel>();
    Boolean isBenefactor;

    public UserModel() {
    }

    public UserModel(String name, String surname, String tc, String phoneNumber, String adress, int bloodGroup, String mail, int city, int district,Boolean isBenefactor, ArrayList<SelectDateModel> selectDateArrayList) {
        this.name = name;
        this.surname = surname;
        this.tc = tc;
        this.phoneNumber = phoneNumber;
        this.adress = adress;
        this.bloodGroup = bloodGroup;
        this.mail = mail;
        this.city = city;
        this.district = district;
        this.isBenefactor = isBenefactor;
        this.selectDateArrayList = selectDateArrayList;
    }

    public Boolean getBenefactor() {
        return isBenefactor;
    }

    public void setBenefactor(Boolean benefactor) {
        isBenefactor = benefactor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(int bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public ArrayList<SelectDateModel> getSelectDateArrayList() {
        return selectDateArrayList;
    }

    public void setSelectDateArrayList(ArrayList<SelectDateModel> selectDateArrayList) {
        this.selectDateArrayList = selectDateArrayList;
    }
}
