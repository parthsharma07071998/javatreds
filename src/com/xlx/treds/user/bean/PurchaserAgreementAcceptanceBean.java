package com.xlx.treds.user.bean;

import java.sql.Timestamp;
import java.util.Date;

public class PurchaserAgreementAcceptanceBean implements IAgreementAcceptanceBean {

    private Long id;
    private String purchaser;
    private Date revisionDate;
    private String version;
    private Long instrumentId;
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

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date pRevisionDate) {
        revisionDate = pRevisionDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String pVersion) {
        version = pVersion;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long pInstrumentId) {
        instrumentId = pInstrumentId;
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

    public String getEntity() {
        return purchaser;
    }

    public void setEntity(String pEntity) {
        purchaser = pEntity;
    }

    public Long getKeyId() {
        return instrumentId;
    }

    public void setKeyId(Long pKeyId) {
        instrumentId = pKeyId;
    }

}