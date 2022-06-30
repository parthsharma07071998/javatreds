package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.xlx.commonn.IKeyValEnumInterface;

public class PurchaserSupplierLimitUtilizationBean {
    public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
    public enum Status implements IKeyValEnumInterface<String>{
        Active("A","Active"),Disabled("D","Disabled");
        
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
    private String supplier;
    private String purchaser;
    private BigDecimal limit;
    private BigDecimal limitUtilized;
    private Status status;
    private String supName;
    private String purName;
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

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal pLimit) {
        limit = pLimit;
    }

    public BigDecimal getLimitUtilized() {
        return limitUtilized;
    }

    public void setLimitUtilized(BigDecimal pLimitUtilized) {
        limitUtilized = pLimitUtilized;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String pSupName) {
        supName = pSupName;
    }

    public String getPurName() {
        return purName;
    }

    public void setPurName(String pPurName) {
        purName = pPurName;
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