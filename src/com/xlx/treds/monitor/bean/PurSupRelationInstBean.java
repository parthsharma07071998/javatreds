package com.xlx.treds.monitor.bean;

import java.math.BigDecimal;
import java.sql.Date;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;

public class PurSupRelationInstBean {
    public enum Status implements IKeyValEnumInterface<String>{
        Uploaded("U","Uploaded"),Factored("F","Factored"),Leg1Settled("L1S","Leg1Settled"),Leg2Settled("L2S","Leg2Settled"),Leg3("L3","Leg3"),Leg3Settled("L3S","Leg3Settled");
        
        private final String code;
        private final String desc;
        private Status(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }

    private String purchaser;
    private String supplier;
    private Date startDate;
    private Date endDate;
    private YesNo relationFlag;
    private Status status;
    private Long count;
    private BigDecimal amount;
    private BigDecimal netAmount;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date pStartDate) {
        startDate = pStartDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date pEndDate) {
        endDate = pEndDate;
    }

    public YesNo getRelationFlag() {
        return relationFlag;
    }

    public void setRelationFlag(YesNo pRelationFlag) {
        relationFlag = pRelationFlag;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long pCount) {
        count = pCount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal pNetAmount) {
        netAmount = pNetAmount;
    }

}