package com.xlx.treds.auction.bean;

import java.math.BigDecimal;


public class PurchaserSupplierCapRateBean {

    private Long id;
    private String entityCode;
    private String counterEntityCode;
    private BigDecimal fromHaircut;
    private BigDecimal toHaircut;
    private Long fromUsance;
    private Long toUsance;
    private BigDecimal capRate;
    private String entityName;
    private String counterEntityName;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String pEntityCode) {
        entityCode = pEntityCode;
    }

    public String getCounterEntityCode() {
        return counterEntityCode;
    }

    public void setCounterEntityCode(String pCounterEntityCode) {
        counterEntityCode = pCounterEntityCode;
    }

    public BigDecimal getFromHaircut() {
        return fromHaircut;
    }

    public void setFromHaircut(BigDecimal pFromHaircut) {
        fromHaircut = pFromHaircut;
    }

    public BigDecimal getToHaircut() {
        return toHaircut;
    }

    public void setToHaircut(BigDecimal pToHaircut) {
        toHaircut = pToHaircut;
    }

    public Long getFromUsance() {
        return fromUsance;
    }

    public void setFromUsance(Long pFromUsance) {
        fromUsance = pFromUsance;
    }

    public Long getToUsance() {
        return toUsance;
    }

    public void setToUsance(Long pToUsance) {
        toUsance = pToUsance;
    }

    public BigDecimal getCapRate() {
        return capRate;
    }

    public void setCapRate(BigDecimal pCapRate) {
        capRate = pCapRate;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String pEntityName) {
        entityName = pEntityName;
    }

    public String getCounterEntityName() {
        return counterEntityName;
    }

    public void setCounterEntityName(String pCounterEntityName) {
        counterEntityName = pCounterEntityName;
    }

}