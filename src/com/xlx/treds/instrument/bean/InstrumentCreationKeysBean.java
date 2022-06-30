
package com.xlx.treds.instrument.bean;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.base.CommonConstants;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;

public class InstrumentCreationKeysBean {
	public static final String FIELDGROUP_INSTSTATUSUSEDLIST = "instStatusUsedList";
	public static final String FIELDGROUP_INSTSTATUSUNUSEDLIST = "instStatusUnUsedList";
    public enum RefType implements IKeyValEnumInterface<String>{
        SRV("SRV","SRV"),DTS("DTS","DTS");
        
        private final String code;
        private final String desc;
        private RefType(String pCode, String pDesc) {
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

    private RefType refType;
    private Date refDate;
    private String refNo;
    private String poNumber;
    private String siNumber;
    private String purchaserCode;
    private String internalVendorRefNo;
    private String supplierCode;
    private String supplierGstn;
    private Long inId;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private Status instStatus;
    private String key;

    public RefType getRefType() {
        return refType;
    }

    public void setRefType(RefType pRefType) {
        refType = pRefType;
    }

    public Date getRefDate() {
        return refDate;
    }

    public void setRefDate(Date pRefDate) {
        refDate = pRefDate;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String pRefNo) {
        refNo = pRefNo;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String pPoNumber) {
        poNumber = pPoNumber;
    }

    public String getSiNumber() {
        return siNumber;
    }

    public void setSiNumber(String pSiNumber) {
        siNumber = pSiNumber;
    }

    public String getPurchaserCode() {
        return purchaserCode;
    }

    public void setPurchaserCode(String pPurchaserCode) {
        purchaserCode = pPurchaserCode;
    }

    public String getInternalVendorRefNo() {
        return internalVendorRefNo;
    }

    public void setInternalVendorRefNo(String pInternalVendorRefNo) {
        internalVendorRefNo = pInternalVendorRefNo;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String pSupplierCode) {
        supplierCode = pSupplierCode;
    }

    public String getSupplierGstn() {
        return supplierGstn;
    }

    public void setSupplierGstn(String pSupplierGstn) {
        supplierGstn = pSupplierGstn;
    }

    public Long getInId() {
        return inId;
    }

    public void setInId(Long pInId) {
        inId = pInId;
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
    
    public String getKeyView(){
    	return "refNo-"+refNo+"-poNumber-"+poNumber+"-siNumber-"+siNumber;
    }
    
    public String getKey(){
    	return refNo+CommonConstants.KEY_SEPARATOR+poNumber+CommonConstants.KEY_SEPARATOR+siNumber;
    }
	public void setKey(String key) {
		this.key = key;
	}
    public Status getInstStatus() {
        return instStatus;
    }

    public void setInstStatus(Status pInstStatus) {
        instStatus = pInstStatus;
    }
	@Override
	public String toString() {
		return super.toString();
	}
}