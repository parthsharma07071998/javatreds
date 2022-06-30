package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;

public class ObligationModificationDetailBean {

    private Long id;
    private Long omrId;
    private Long obId;
    private Long partNumber;
    private TxnType txnType;
    private BigDecimal origAmount;
    private Date origDate;
    private Status origStatus;
    private BigDecimal revisedAmount;
    private Date revisedDate;
    private Status revisedStatus;
    private String paymentSettlor;
    private String origPaymentSettlor;
    private String remarks;
    private String paymentRefNo;
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

    public Long getOmrId() {
        return omrId;
    }

    public void setOmrId(Long pOmrId) {
        omrId = pOmrId;
    }

    public Long getObId() {
        return obId;
    }

    public void setObId(Long pObId) {
        obId = pObId;
    }

    public Long getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Long pPartNumber) {
        partNumber = pPartNumber;
    }

    public TxnType getTxnType() {
        return txnType;
    }

    public void setTxnType(TxnType pTxnType) {
        txnType = pTxnType;
    }

    public BigDecimal getOrigAmount() {
        return origAmount;
    }

    public void setOrigAmount(BigDecimal pOrigAmount) {
        origAmount = pOrigAmount;
    }

    public Date getOrigDate() {
        return origDate;
    }

    public void setOrigDate(Date pOrigDate) {
        origDate = pOrigDate;
    }

    public Status getOrigStatus() {
        return origStatus;
    }

    public void setOrigStatus(Status pOrigStatus) {
        origStatus = pOrigStatus;
    }

    public BigDecimal getRevisedAmount() {
        return revisedAmount;
    }

    public void setRevisedAmount(BigDecimal pRevisedAmount) {
        revisedAmount = pRevisedAmount;
    }

    public Date getRevisedDate() {
        return revisedDate;
    }

    public void setRevisedDate(Date pRevisedDate) {
        revisedDate = pRevisedDate;
    }

    public Status getRevisedStatus() {
        return revisedStatus;
    }

    public void setRevisedStatus(Status pRevisedStatus) {
        revisedStatus = pRevisedStatus;
    }

    public String getPaymentSettlor() {
        return paymentSettlor;
    }

    public void setPaymentSettlor(String pPaymentSettlor) {
        paymentSettlor = pPaymentSettlor;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
    }

    public String getPaymentRefNo() {
        return paymentRefNo;
    }

    public void setPaymentRefNo(String pPaymentRefNo) {
        paymentRefNo = pPaymentRefNo;
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

    public String getOrigPaymentSettlor() {
        return origPaymentSettlor;
    }

    public void setOrigPaymentSettlor(String pOrigPaymentSettlor) {
    	origPaymentSettlor = pOrigPaymentSettlor;
    }
    
    public boolean isNotModified(){
    	if(origAmount.equals(revisedAmount) 
    			&& origDate.equals(revisedDate) 
    				&& origStatus.equals(revisedStatus) 
    					&& origPaymentSettlor.equals(paymentSettlor) ){
    		return true;
    	}
    	return false;
    }
}