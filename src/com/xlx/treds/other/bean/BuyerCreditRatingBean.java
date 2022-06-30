
package com.xlx.treds.other.bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants;
import com.xlx.treds.entity.bean.AppEntityBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class BuyerCreditRatingBean {
	
    public enum Status implements IKeyValEnumInterface<String>{
        Active("A","Active"),Expired("E","Expired"),Future("F","Future");
        
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
    private String buyerCode;
    private String ratingAgency;
    private List<String> filterRatingAgencyList;
    private String filterRatingAgency;
    private Date ratingDate;
    private String rating;
    private List<String> filterRatingList;
    private String filterRating;
    private Date fromDate;
    private Date toDate;
    private String ratingType;
    private Status status;
    private String remarks;
    private String pan;
    private Long financierCount;
    private Long fileId;
    private String ratingFile;
    private Long expiryDays;
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

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String pBuyerCode) {
        buyerCode = pBuyerCode;
    }

    public String getRatingAgency() {
        return ratingAgency;
    }

    public void setRatingAgency(String pRatingAgency) {
        ratingAgency = pRatingAgency;
    }
    
    public List<String> getFilterRatingAgencyList() {
        return filterRatingAgencyList;
    }

    public void setFilterRatingAgencyList(List<String> pFilterRatingAgencyList) {
        filterRatingAgencyList = pFilterRatingAgencyList;
    }

    public String getFilterRatingAgency() {
        if (filterRatingAgencyList == null){
        	return null;
        }
        else {
            return new JsonBuilder(filterRatingAgencyList).toString();
        }
    }

    public void setFilterRatingAgency(String pFilterRatingAgency) {
        if (pFilterRatingAgency == null)
        	filterRatingAgencyList = null;
        else {
        	filterRatingAgencyList = (List<String>)(new JsonSlurper().parseText(pFilterRatingAgency));
        }
    }

    public Date getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(Date pRatingDate) {
        ratingDate = pRatingDate;
    }

    public Date getExpiryDate() {
    	if(ratingDate!=null){
    		if(expiryDays!=null){
        		return CommonUtilities.addRemoveDays(ratingDate, expiryDays.intValue());
    		}
    	}
        return null;
    }

    public void setExpiryDate(Date pExpiryDate) {
    }

    public String getRating() {
        return rating;
    }
    public void setRating(String pRating) {
        rating = pRating;
    }
    
    public List<String> getFilterRatingList() {
        return filterRatingList;
    }

    public void setFilterRatingList(List<String> pFilterRatingList) {
        filterRatingList = pFilterRatingList;
    }

    public String getFilterRating() {
        if (filterRatingList == null){
        	return null;
        }
        else {
            return new JsonBuilder(filterRatingList).toString();
        }
    }

    public void setFilterRating(String pFilterRating) {
        if (pFilterRating == null)
        	filterRatingList = null;
        else {
        	filterRatingList = (List<String>)(new JsonSlurper().parseText(pFilterRating));
        }
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
    
    public String getRatingType() {
        return ratingType;
    }

    public void setRatingType(String pRatingType) {
        ratingType = pRatingType;
    }

    public Status getStatus() {
		HashMap<String, Object> lBuyerCreditSetting = new HashMap<String,Object>();
    	if(ratingDate!=null){
    		if(ratingDate.compareTo(CommonUtilities.getDate(CommonUtilities.getCurrentDate())) > 0){
    			return status.Future;
    		}
    		lBuyerCreditSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_BUYERCREDITRATING);
    		Long lExpiryDays =  (Long) lBuyerCreditSetting.get(AppConstants.ATTRIBUTE_BUYERCREDITRATING_EXPIRYDAYS);
    		if(lExpiryDays!=null){
        		Date lExpiryDate = CommonUtilities.addRemoveDays(ratingDate, lExpiryDays.intValue() );
        		if(lExpiryDate.compareTo(CommonUtilities.getDate(CommonUtilities.getCurrentDate())) < 0){
        			return status.Expired;
        		}
        		return status.Active;
    		}
    	}
        return status;
    }

    public void setStatus(Status pStatus) {
    	status = pStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
    }

    public void setToDate(Date pToDate) {
        toDate = pToDate;
    }
    
    public String getPan() {
        return pan;
    }

    public void setPan(String pPan) {
        pan = pPan;
    }

    public Long getFinancierCount() {
        return financierCount;
    }

    public void setFinancierCount(Long pFinancierCount) {
        financierCount = pFinancierCount;
    }
    
    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long pFileId) {
        fileId = pFileId;
    }

    public String getRatingFile() {
        return ratingFile;
    }

    public void setRatingFile(String pRatingFile) {
        ratingFile = pRatingFile;
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
    
    public String getPurName() {
        if (buyerCode != null) {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            try {
                AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{buyerCode});
                if (lAppEntityBean != null)
                    return lAppEntityBean.getName();
            } catch (Exception lException) {
            }
        }
        return null;
    }

    public void setPurName(String pPurName) {
        
    }
    
    public Long getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(Long pExpiryDays) {
        expiryDays = pExpiryDays;
    }
    

	@Override
	public String toString() {
		return super.toString();
	}
}