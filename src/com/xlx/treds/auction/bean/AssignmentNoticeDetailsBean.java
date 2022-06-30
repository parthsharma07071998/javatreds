package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class AssignmentNoticeDetailsBean {

    private Long anId;
    private Long fuId;
    private Long inId;
    private Date fuDate;
    private Date fuDueDate;
    private BigDecimal fuFactoredAmount;
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

    public Long getInId() {
        return inId;
    }

    public void setInId(Long pInId) {
        inId = pInId;
    }

    public Date getFuDate() {
        return fuDate;
    }

    public void setFuDate(Date pFuDate) {
        fuDate = pFuDate;
    }

    public Date getFuDueDate() {
        return fuDueDate;
    }

    public void setFuDueDate(Date pFuDueDate) {
        fuDueDate = pFuDueDate;
    }

    public BigDecimal getFuFactoredAmount() {
        return fuFactoredAmount;
    }

    public void setFuFactoredAmount(BigDecimal pFuFactoredAmount) {
        fuFactoredAmount = pFuFactoredAmount;
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