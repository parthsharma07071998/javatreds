package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.MessageType;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.entity.bean.EntityOtpNotificationSettingBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class EntityOtpNotificationSettingBO {
    
    private GenericDAO<EntityOtpNotificationSettingBean> entityOtpNotificationSettingDAO;

    public EntityOtpNotificationSettingBO() {
        super();
        entityOtpNotificationSettingDAO = new GenericDAO<EntityOtpNotificationSettingBean>(EntityOtpNotificationSettingBean.class);
    }
    
    public EntityOtpNotificationSettingBean findBean(ExecutionContext pExecutionContext, 
        EntityOtpNotificationSettingBean pFilterBean) throws Exception {
        EntityOtpNotificationSettingBean lEntityOtpNotificationSettingBean = entityOtpNotificationSettingDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lEntityOtpNotificationSettingBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lEntityOtpNotificationSettingBean;
    }
   
    public EntityOtpNotificationSettingBean findBean(Connection pConnection, 
            EntityOtpNotificationSettingBean pFilterBean) {
            EntityOtpNotificationSettingBean lEntityOtpNotificationSettingBean = null;
            try
			{
				lEntityOtpNotificationSettingBean = entityOtpNotificationSettingDAO.findBean(pConnection, pFilterBean);
			}
			catch (Exception e)
			{
				lEntityOtpNotificationSettingBean = null;
				e.printStackTrace();
			}
            return lEntityOtpNotificationSettingBean;
        }
   
    public String getNotificationSettingJson(ExecutionContext pExecutionContext, IAppUserBean pUserBean , String pEntityCode) throws Exception {
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        String lDomain = null;
        if (lAppUserBean.getType() != AppUserBean.Type.Admin)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        EntityOtpNotificationSettingBean lFilterBean = new EntityOtpNotificationSettingBean();
        if (AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain())){
        	lDomain = pEntityCode;
        }else{
        	lDomain = lAppUserBean.getDomain();
        }
        lFilterBean.setCode(lDomain);
        List<EntityOtpNotificationSettingBean> lEntityOtpNotificationSettingList = entityOtpNotificationSettingDAO.findList(pExecutionContext.getConnection(), 
                lFilterBean, (String)null);
     
        
        Map<String, Map<String, Object>> lDataMap = new HashMap<String, Map<String,Object>>();
        MessageType lMessageType = MessageType.None;
        for (EntityOtpNotificationSettingBean lEntityOtpNotificationSettingBean : lEntityOtpNotificationSettingList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            //
            lData.put("mobileList", lEntityOtpNotificationSettingBean.getMobileList());
            lMessageType = lEntityOtpNotificationSettingBean.getSmsMessageType();
            if(lMessageType==null) lMessageType = MessageType.None;
            lData.put("smsMessageType", lMessageType.getCode());
            //
            lData.put("emailList", lEntityOtpNotificationSettingBean.getEmailList());
            lMessageType = lEntityOtpNotificationSettingBean.getEmailMessageType();
            if(lMessageType==null) lMessageType = MessageType.None;
            lData.put("emailMessageType", lMessageType.getCode());
            //
            lDataMap.put(lEntityOtpNotificationSettingBean.getNotificationType(), lData);
        }
        
        Map<String, Object> lResults = new HashMap<String, Object>();
        lResults.put("meta", OtherResourceCache.getInstance().getOtpNotificationSettings(lDomain));
        lResults.put("data", lDataMap);
        
        return new JsonBuilder(lResults).toString();
    }
    
    public void save(ExecutionContext pExecutionContext, EntityOtpNotificationSettingBean pEntityOtpNotificationSettingBean, IAppUserBean pUserBean, 
        boolean pNew, String pEntityCode) throws Exception {
    	String lDomain = null;
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        if (lAppUserBean.getType() != AppUserBean.Type.Admin)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        if (AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain())){
        	lDomain = pEntityCode;
        }else{
        	lDomain = lAppUserBean.getDomain();
        }
        // check duplicate mobile numbers
        List<String> lMobileNoList = pEntityOtpNotificationSettingBean.getMobileList();
        if ((lMobileNoList != null) && (lMobileNoList.size() > 0)) {
            Set<String> lMobileNoSet = new HashSet<String>();
            for (String lMobileNo : lMobileNoList) {
                if (lMobileNoSet.contains(lMobileNo))
                    throw new CommonBusinessException("Duplicate Mobile " + lMobileNo);
                else
                    lMobileNoSet.add(lMobileNo);
            }
        }
        // check duplicate mobile numbers
        List<String> lEmailList = pEntityOtpNotificationSettingBean.getEmailList();
        if ((lEmailList != null) && (lEmailList.size() > 0)) {
            Set<String> lEmailSet = new HashSet<String>();
            for (String lEmail : lEmailList) {
                if (lEmailSet.contains(lEmail))
                    throw new CommonBusinessException("Duplicate Email " + lEmail);
                else
                	lEmailSet.add(lEmail);
            }
        }
        if(MessageType.Explicit.equals(pEntityOtpNotificationSettingBean.getEmailMessageType())&& 
        		(lEmailList==null || lEmailList.isEmpty() )	){
            throw new CommonBusinessException("Please enter an email address.");
        }
        if(MessageType.Explicit.equals(pEntityOtpNotificationSettingBean.getSmsMessageType())&& 
        		(lMobileNoList==null || lMobileNoList.isEmpty() )	){
            throw new CommonBusinessException("Please enter a mobile No.");
        }
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        EntityOtpNotificationSettingBean lOldEntityOtpNotificationSettingBean = null;
        pEntityOtpNotificationSettingBean.setCode(lDomain);
        if (pNew) {
            pEntityOtpNotificationSettingBean.setRecordCreator(pUserBean.getId());
            entityOtpNotificationSettingDAO.insert(lConnection, pEntityOtpNotificationSettingBean);
        } else {
            lOldEntityOtpNotificationSettingBean = findBean(pExecutionContext, pEntityOtpNotificationSettingBean);
            pEntityOtpNotificationSettingBean.setRecordUpdator(pUserBean.getId());
            if (entityOtpNotificationSettingDAO.update(lConnection, pEntityOtpNotificationSettingBean) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
}
