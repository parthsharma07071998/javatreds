package com.xlx.treds.monitor.bean;

import java.math.BigDecimal;
import java.sql.Date;

public class SuppliersFactoredConcentrationBean {

    private String purchaser;
    private String supplier;
    private BigDecimal totalAmount;
    private BigDecimal totalFactAmount;
    private Long factUnitsCount;
    private BigDecimal purTotalAmount;
    private BigDecimal purTotalFactAmount;
    private Long purFuCount;
    private BigDecimal totalPercent;
    private BigDecimal totalFactoredPercent;
    private Date toDate;
    private Date fromDate;

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal pTotalAmount) {
        totalAmount = pTotalAmount;
    }

    public BigDecimal getTotalFactAmount() {
        return totalFactAmount;
    }

    public void setTotalFactAmount(BigDecimal pTotalFactAmount) {
        totalFactAmount = pTotalFactAmount;
    }

    public Long getFactUnitsCount() {
        return factUnitsCount;
    }

    public void setFactUnitsCount(Long pFactUnitsCount) {
        factUnitsCount = pFactUnitsCount;
    }

    public BigDecimal getPurTotalAmount() {
        return purTotalAmount;
    }

    public void setPurTotalAmount(BigDecimal pPurTotalAmount) {
        purTotalAmount = pPurTotalAmount;
    }

    public BigDecimal getPurTotalFactAmount() {
        return purTotalFactAmount;
    }

    public void setPurTotalFactAmount(BigDecimal pPurTotalFactAmount) {
        purTotalFactAmount = pPurTotalFactAmount;
    }

    public Long getPurFuCount() {
        return purFuCount;
    }

    public void setPurFuCount(Long pPurFuCount) {
    	purFuCount = pPurFuCount;
    }

    public BigDecimal getTotalPercent() {
        return totalPercent;
    }

    public void setTotalPercent(BigDecimal pTotalPercent) {
        totalPercent = pTotalPercent;
    }

    public BigDecimal getTotalFactoredPercent() {
        return totalFactoredPercent;
    }

    public void setTotalFactoredPercent(BigDecimal pTotalFactoredPercent) {
        totalFactoredPercent = pTotalFactoredPercent;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date pToDate) {
        toDate = pToDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date pFromDate) {
        fromDate = pFromDate;
    }

}