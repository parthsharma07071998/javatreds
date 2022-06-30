package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.MailerType;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.entity.bean.EntityNotificationSettingBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class EntityNotificationSettingBO {
    
    private GenericDAO<EntityNotificationSettingBean> entityNotificationSettingDAO;

    public EntityNotificationSettingBO() {
        super();
        entityNotificationSettingDAO = new GenericDAO<EntityNotificationSettingBean>(EntityNotificationSettingBean.class);
    }
    
    public EntityNotificationSettingBean findBean(ExecutionContext pExecutionContext, 
        EntityNotificationSettingBean pFilterBean) throws Exception {
        EntityNotificationSettingBean lEntityNotificationSettingBean = entityNotificationSettingDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lEntityNotificationSettingBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lEntityNotificationSettingBean;
    }
   
    public EntityNotificationSettingBean findBean(Connection pConnection, 
            EntityNotificationSettingBean pFilterBean) {
            EntityNotificationSettingBean lEntityNotificationSettingBean = null;
            try
			{
				lEntityNotificationSettingBean = entityNotificationSettingDAO.findBean(pConnection, pFilterBean);
			}
			catch (Exception e)
			{
				lEntityNotificationSettingBean = null;
				e.printStackTrace();
			}
            return lEntityNotificationSettingBean;
        }
   
    public String getNotificationSettingJson(ExecutionContext pExecutionContext, IAppUserBean pUserBean , String pEntityCode) throws Exception {
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        String lDomain = null;
        if (lAppUserBean.getType() != AppUserBean.Type.Admin)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        EntityNotificationSettingBean lFilterBean = new EntityNotificationSettingBean();
        if (AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain())){
        	lDomain = pEntityCode;
        }else{
        	lDomain = lAppUserBean.getDomain();
        }
        lFilterBean.setCode(lDomain);
        List<EntityNotificationSettingBean> lEntityNotificationSettingList = entityNotificationSettingDAO.findList(pExecutionContext.getConnection(), 
                lFilterBean, (String)null);
     
        
        Map<String, Map<String, Object>> lDataMap = new HashMap<String, Map<String,Object>>();
        MailerType lMailerType = MailerType.None;
        for (EntityNotificationSettingBean lEntityNotificationSettingBean : lEntityNotificationSettingList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put("emailList", lEntityNotificationSettingBean.getEmailList());
            lMailerType = lEntityNotificationSettingBean.getMailerType();
            if(lMailerType==null) lMailerType = MailerType.None;
            lData.put("mailerType", lMailerType.getCode());
            lDataMap.put(lEntityNotificationSettingBean.getNotificationType(), lData);
            lData.put("mailRm", lEntityNotificationSettingBean.getMailRm()!=null?lEntityNotificationSettingBean.getMailRm().getCode():CommonAppConstants.YesNo.No.getCode());
        }
        
        Map<String, Object> lResults = new HashMap<String, Object>();
        lResults.put("meta", OtherResourceCache.getInstance().getNotificationSettings(lDomain));
        lResults.put("data", lDataMap);
        if (AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain())){
        	lResults.put("isPlatform", true);
        }
        return new JsonBuilder(lResults).toString();
    }
    
    public void save(ExecutionContext pExecutionContext, EntityNotificationSettingBean pEntityNotificationSettingBean, IAppUserBean pUserBean, 
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
        // check duplicate email ids
        List<String> lEmailIdList = pEntityNotificationSettingBean.getEmailList();
        if ((lEmailIdList != null) && (lEmailIdList.size() > 0)) {
            Set<String> lEmailIdSet = new HashSet<String>();
            for (String lEmailId : lEmailIdList) {
                if (lEmailIdSet.contains(lEmailId))
                    throw new CommonBusinessException("Duplicate email id " + lEmailId);
                else
                    lEmailIdSet.add(lEmailId);
            }
        }
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        EntityNotificationSettingBean lOldEntityNotificationSettingBean = null;
        pEntityNotificationSettingBean.setCode(lDomain);
        if (pNew) {
            pEntityNotificationSettingBean.setRecordCreator(pUserBean.getId());
            entityNotificationSettingDAO.insert(lConnection, pEntityNotificationSettingBean);
        } else {
            lOldEntityNotificationSettingBean = findBean(pExecutionContext, pEntityNotificationSettingBean);
            pEntityNotificationSettingBean.setRecordUpdator(pUserBean.getId());
            if (entityNotificationSettingDAO.update(lConnection, pEntityNotificationSettingBean) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        pExecutionContext.commitAndDispose();
    }

	public void saveRm(ExecutionContext pExecutionContext, EntityNotificationSettingBean pFilterBean, EntityNotificationSettingBean pFrontEndBean, IAppUserBean pUserBean) throws Exception {
		pExecutionContext.setAutoCommit(false);
		EntityNotificationSettingBean lOldNotificationSettingBean = findBean(pExecutionContext.getConnection(), pFilterBean);
        if(lOldNotificationSettingBean==null) {
        	lOldNotificationSettingBean = pFrontEndBean;
        	lOldNotificationSettingBean.setMailerType(MailerType.None);
        }else {
        	lOldNotificationSettingBean.setMailRm(pFrontEndBean.getMailRm());
        }
        if (lOldNotificationSettingBean.getRecordVersion()==null) {
        	lOldNotificationSettingBean.setRecordCreator(pUserBean.getId());
        	entityNotificationSettingDAO.insert(pExecutionContext.getConnection(), lOldNotificationSettingBean);
        }else {
        	entityNotificationSettingDAO.update(pExecutionContext.getConnection(), lOldNotificationSettingBean, EntityNotificationSettingBean.FIELDGROUP_RMMAIL);
        }
        pExecutionContext.commitAndDispose();
	}
    
}
