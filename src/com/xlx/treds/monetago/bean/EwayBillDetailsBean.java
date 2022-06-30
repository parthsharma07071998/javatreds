package com.xlx.treds.monetago.bean;

import java.sql.Timestamp;

public class EwayBillDetailsBean {

	
	private Long id;
    private String supplier;
    private String instNumber;
    private String ewayBillNo;
    private String gstrPayload;
    private String ewayPayload;
    private String tredsPayload;
    private Timestamp recordCreateTime;
    private Long recordCreator;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public String getInstNumber() {
        return instNumber;
    }

    public void setInstNumber(String pInstNumber) {
        instNumber = pInstNumber;
    }

    public String getEwayBillNo() {
        return ewayBillNo;
    }

    public void setEwayBillNo(String pEwayBillNo) {
        ewayBillNo = pEwayBillNo;
    }

    public String getGstrPayload() {
        return gstrPayload;
    }

    public void setGstrPayload(String pGstrPayload) {
        gstrPayload = pGstrPayload;
    }

    public String getEwayPayload() {
        return ewayPayload;
    }

    public void setEwayPayload(String pEwayPayload) {
        ewayPayload = pEwayPayload;
    }

    public String getTredsPayload() {
        return tredsPayload;
    }

    public void setTredsPayload(String pTredsPayload) {
        tredsPayload = pTredsPayload;
    }
    
    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

    public Long getRecordCreator() {
        return recordCreator;
    }

    public void setRecordCreator(Long pRecordCreator) {
        recordCreator = pRecordCreator;
    }

}