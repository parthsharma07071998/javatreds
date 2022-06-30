package com.xlx.treds.user.bean;

import java.sql.Timestamp;

public class AddressBean {

    private Long id;
    private String line1;
    private String line2;
    private String line3;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String pLine1) {
        line1 = pLine1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String pLine2) {
        line2 = pLine2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String pLine3) {
        line3 = pLine3;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String pCountry) {
        country = pCountry;
    }

    public String getState() {
        return state;
    }

    public void setState(String pState) {
        state = pState;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String pCity) {
        city = pCity;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String pZipCode) {
        zipCode = pZipCode;
    }

    public Long getRecordCreator() {
        return recordCreator;
    }

    public void setRecordCreator(Long pRecordCreator) {
        recordCreator = pRecordCreator;
    }

    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

    public Long getRecordUpdator() {
        return recordUpdator;
    }

    public void setRecordUpdator(Long pRecordUpdator) {
        recordUpdator = pRecordUpdator;
    }

    public Timestamp getRecordUpdateTime() {
        return recordUpdateTime;
    }

    public void setRecordUpdateTime(Timestamp pRecordUpdateTime) {
        recordUpdateTime = pRecordUpdateTime;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

}