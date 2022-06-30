package com.xlx.treds.entity.bean;

import java.sql.Timestamp;

public class AggregatorPurchaserMapBean {

    private String aggregator;
    private String purchaser;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public String getAggregator() {
        return aggregator;
    }

    public void setAggregator(String pAggregator) {
        aggregator = pAggregator;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
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