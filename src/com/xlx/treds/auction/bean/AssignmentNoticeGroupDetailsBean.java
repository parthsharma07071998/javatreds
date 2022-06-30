package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class AssignmentNoticeGroupDetailsBean {

    private Long anId;
    private Long fuId;
    private Long groupInId;
    private Long childInId;
    private BigDecimal netAmount;
    private String instNumber;
    private Date instDate;
    private BigDecimal instAmount;
    private String currency;
    private Timestamp recordCreateTime;

    public Long getAnId() {
        return anId;
    }

    public void setAnId(Long pAnId) {
        anId = pAnId;
    }

    public Long getFuId() {
        return fuId;
    }

    public void setFuId(Long pFuId) {
        fuId = pFuId;
    }

    public Long getGroupInId() {
        return groupInId;
    }

    public void setGroupInId(Long pGroupInId) {
        groupInId = pGroupInId;
    }

    public Long getChildInId() {
        return childInId;
    }

    public void setChildInId(Long pChildInId) {
        childInId = pChildInId;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal pNetAmount) {
        netAmount = pNetAmount;
    }

    public String getInstNumber() {
        return instNumber;
    }

    public void setInstNumber(String pInstNumber) {
        instNumber = pInstNumber;
    }

    public Date getInstDate() {
        return instDate;
    }

    public void setInstDate(Date pInstDate) {
        instDate = pInstDate;
    }

    public BigDecimal getInstAmount() {
        return instAmount;
    }

    public void setInstAmount(BigDecimal pInstAmount) {
        instAmount = pInstAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String pCurrency) {
        currency = pCurrency;
    }

    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

}