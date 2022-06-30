package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class OutsideSettlementDetBean {
    public enum Type implements IKeyValEnumInterface<String>{
        Direct("D","Direct"),NEFT("N","NEFT"),RTGS("R","RTGS");
        
        private final String code;
        private final String desc;
        private Type(String pCode, String pDesc) {
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
    private Long osrId;
    private String paymentRefNo;
    private String accName;
    private String bankName;
    private String ifsc;
    private String branchName;
    private String accountNo;
    private BigDecimal amount;
    private Date date;
    private Type type;
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

    public Long getOsrId() {
        return osrId;
    }

    public void setOsrId(Long pOsrId) {
        osrId = pOsrId;
    }

    public String getPaymentRefNo() {
        return paymentRefNo;
    }

    public void setPaymentRefNo(String pPaymentRefNo) {
        paymentRefNo = pPaymentRefNo;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String pAccName) {
        accName = pAccName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String pBankName) {
        bankName = pBankName;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String pIfsc) {
        ifsc = pIfsc;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String pBranchName) {
        branchName = pBranchName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String pAccountNo) {
        accountNo = pAccountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
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