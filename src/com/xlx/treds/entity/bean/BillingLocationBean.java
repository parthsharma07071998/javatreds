package com.xlx.treds.entity.bean;

import java.sql.Timestamp;

public class BillingLocationBean {

    private String code;
    private Long id;
    private String name;
    private String gstn;
    private Long billLocId;
    private String billLocName;
    private String billLocGstn;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getGstn() {
        return gstn;
    }

    public void setGstn(String pGstn) {
        gstn = pGstn;
    }

    public Long getBillLocId() {
        return billLocId;
    }

    public void setBillLocId(Long pBillLocId) {
        billLocId = pBillLocId;
    }

    public String getBillLocName() {
        return billLocName;
    }

    public void setBillLocName(String pBillLocName) {
        billLocName = pBillLocName;
    }

    public String getBillLocGstn() {
        return billLocGstn;
    }

    public void setBillLocGstn(String pBillLocGstn) {
        billLocGstn = pBillLocGstn;
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