
package com.xlx.treds.auction.bean;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants.YesNo;

public class PurchaserSupplierRelationshipHistoryBean {

    private Long id;
    private String supplier;
    private String purchaser;
    private Date startDate;
    private String relationDocName;
    private YesNo relationFlag;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date pStartDate) {
        startDate = pStartDate;
    }

    public String getRelationDocName() {
        return relationDocName;
    }

    public void setRelationDocName(String pRelationDocName) {
    	relationDocName = pRelationDocName;
    }

    public YesNo getRelationFlag() {
        return relationFlag;
    }

    public void setRelationFlag(YesNo pRelationFlag) {
        relationFlag = pRelationFlag;
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
    
	public String getRelationShipAsString(){
		StringBuilder lRetVal = new StringBuilder();
		if(YesNo.Yes.equals(relationFlag)){
			lRetVal.append(relationFlag.getCode());
		}else{
			lRetVal.append(YesNo.No.getCode());
		}
		lRetVal.append(CommonConstants.COMMA);
		if(startDate!=null){
			lRetVal.append(FormatHelper.getDisplay("DD-MM-YYYY",startDate));
		}else{
			lRetVal.append("");
		}
		lRetVal.append(CommonConstants.COMMA);
		if(StringUtils.isNotEmpty(relationDocName)){
			lRetVal.append(relationDocName);
		}
		return lRetVal.toString();
	}

}