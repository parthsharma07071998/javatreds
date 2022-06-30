package com.xlx.treds.master.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.user.bean.RoleMasterBean;
import com.xlx.treds.AppConstants.MailerType;

import groovy.json.JsonSlurper;

public class NotificationMasterBean {

    private String notificationType;
    private String category;
    private String description;
    private String groupDescription;
    private YesNo supplier;
    private YesNo purchaser;
    private YesNo financier;
    private MailerType malierType;
    private Long sequence;
    private String mailerInfo;
    private String extraSettings;
    private Yes mandatory;
    private Long recordVersion;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String pNotificationType) {
        notificationType = pNotificationType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String pCategory) {
        category = pCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String pGroupDescription) {
        groupDescription = pGroupDescription;
    }
    
    public YesNo getSupplier() {
        return supplier;
    }

    public void setSupplier(YesNo pSupplier) {
        supplier = pSupplier;
    }

    public YesNo getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(YesNo pPurchaser) {
        purchaser = pPurchaser;
    }

    public YesNo getFinancier() {
        return financier;
    }

    public void setFinancier(YesNo pFinancier) {
        financier = pFinancier;
    }

    public MailerType getMalierType() {
        return malierType;
    }

    public void setMalierType(MailerType pMalierType) {
        malierType = pMalierType;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long pSequence) {
        sequence = pSequence;
    }

    public String getMailerInfo() {
        return mailerInfo;
    }

    public void setMailerInfo(String pMailerInfo) {
        mailerInfo = pMailerInfo;
    }
    
    public String getExtraSettings() {
        return extraSettings;
    }
    public void setExtraSettings(String pExtraSettings) {
        extraSettings = pExtraSettings;
    }
    
    public Yes getMandatory() {
        return mandatory;
    }

    public void setMandatory(Yes pMandatory) {
        mandatory = pMandatory;
    }
    
    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }
    
    public List<String> getExtraSettingDesc() throws Exception{
    	if (StringUtils.isNotBlank(extraSettings)){
    		return getRoleList();
    	}
		return null;
    }
    
    public void setExtraSettingDesc(){
    }
    
    public List<String> getRoleList() throws Exception{
    	List<Integer> lIdList = (List<Integer>)(new JsonSlurper().parseText(extraSettings));
    	MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(RoleMasterBean.ENTITY_NAME);
    	Vector lAllRoles = null;
    	List<String> lList = new ArrayList<>();
    	RoleMasterBean lBean = null;
		lAllRoles  =  lMemoryTable.selectAllRows(lMemoryTable.getPrimaryKeyIndex());
		int lCount = (lAllRoles.size()>0?lAllRoles.size():0);
		for(int i=0 ; i<lCount; i++){
			lBean = (RoleMasterBean) lAllRoles.get(i);
			if (lIdList.contains(lBean.getId().intValue())){
				lList.add(lBean.getName());
			}
		}
    	return lList;
    }
    
    public List<Long> getRoleIdList() throws Exception{
    	List<Long> lIdList = null;
    	if(CommonUtilities.hasValue(extraSettings)){
        	List<Integer> lTmpList =	(List<Integer>)(new JsonSlurper().parseText(extraSettings));
        	if(lTmpList!=null && !lTmpList.isEmpty()){
        		lIdList = new ArrayList<Long>();
        		for(Integer lId : lTmpList){
        			lIdList.add(new Long(lId));
        		}
        	}
    	}
    	return lIdList;
    }
    
}