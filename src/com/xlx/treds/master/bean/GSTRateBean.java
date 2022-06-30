package com.xlx.treds.master.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.commonn.IKeyValEnumInterface;

public class GSTRateBean {
    public enum Status implements IKeyValEnumInterface<String>{
        Active("A","Active"),InActive("I","InActive");
        
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

    private Long id;
    private Date fromDate;
    private Date toDate;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
    private BigDecimal cgstSurcharge;
    private BigDecimal sgstSurcharge;
    private BigDecimal igstSurcharge;
    private Status status;
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

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date pFromDate) {
        fromDate = pFromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date pToDate) {
        toDate = pToDate;
    }

    public BigDecimal getCgst() {
        return cgst;
    }

    public void setCgst(BigDecimal pCgst) {
        cgst = pCgst;
    }

    public BigDecimal getSgst() {
        return sgst;
    }

    public void setSgst(BigDecimal pSgst) {
        sgst = pSgst;
    }

    public BigDecimal getIgst() {
        return igst;
    }

    public void setIgst(BigDecimal pIgst) {
        igst = pIgst;
    }

    public BigDecimal getCgstSurcharge() {
        return cgstSurcharge;
    }

    public void setCgstSurcharge(BigDecimal pCgstSurcharge) {
        cgstSurcharge = pCgstSurcharge;
    }

    public BigDecimal getSgstSurcharge() {
        return sgstSurcharge;
    }

    public void setSgstSurcharge(BigDecimal pSgstSurcharge) {
        sgstSurcharge = pSgstSurcharge;
    }

    public BigDecimal getIgstSurcharge() {
        return igstSurcharge;
    }

    public void setIgstSurcharge(BigDecimal pIgstSurcharge) {
        igstSurcharge = pIgstSurcharge;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
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