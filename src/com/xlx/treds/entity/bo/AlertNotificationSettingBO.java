package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.AppConstants.MessageType;
import com.xlx.treds.entity.bean.AlertNotificationSettingBean;
import com.xlx.treds.entity.bean.EntityOtpNotificationSettingBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;

public class AlertNotificationSettingBO {
    
    private GenericDAO<AlertNotificationSettingBean> alertNotificationSettingDAO;

    public AlertNotificationSettingBO() {
        super();
        alertNotificationSettingDAO = new GenericDAO<AlertNotificationSettingBean>(AlertNotificationSettingBean.class);
    }
    
    public AlertNotificationSettingBean findBean(ExecutionContext pExecutionContext, 
        AlertNotificationSettingBean pFilterBean) throws Exception {
        AlertNotificationSettingBean lAlertNotificationSettingBean = alertNotificationSettingDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lAlertNotificationSettingBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lAlertNotificationSettingBean;
    }
    
    public List<AlertNotificationSettingBean> findList(ExecutionContext pExecutionContext, AlertNotificationSettingBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return alertNotificationSettingDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, AlertNotificationSettingBean pAlertNotificationSettingBean, IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AlertNotificationSettingBean lOldAlertNotificationSettingBean = findBean(pExecutionContext, pAlertNotificationSettingBean);
        if (lOldAlertNotificationSettingBean!=null) {
        	lOldAlertNotificationSettingBean.setEmailList(pAlertNotificationSettingBean.getEmailList());
        	lOldAlertNotificationSettingBean.setMobileList(pAlertNotificationSettingBean.getMobileList());
        	lOldAlertNotificationSettingBean.setRecordUpdator(pUserBean.getId());
             if (alertNotificationSettingDAO.update(lConnection, lOldAlertNotificationSettingBean, BeanMeta.FIELDGROUP_UPDATE) == 0) {
            	 throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
             }
        }else {
            pAlertNotificationSettingBean.setRecordCreator(pUserBean.getId());
            alertNotificationSettingDAO.insert(lConnection, pAlertNotificationSettingBean);
        } 
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, AlertNotificationSettingBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        AlertNotificationSettingBean lAlertNotificationSettingBean = findBean(pExecutionContext, pFilterBean);
        lAlertNotificationSettingBean.setRecordUpdator(pUserBean.getId());
        alertNotificationSettingDAO.delete(lConnection, lAlertNotificationSettingBean);        


        pExecutionContext.commitAndDispose();
    }
    
    
    public String getNotificationSettingJson(ExecutionContext pExecutionContext, IAppUserBean pUserBean) throws Exception {
        AppUserBean lAppUserBean = (AppUserBean)pUserBean;
        String lDomain = null;
        if (lAppUserBean.getType() != AppUserBean.Type.Admin)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        StringBuilder lSql = new StringBuilder();
        lSql.append(" SELECT * FROM AlertNotificationSettings WHERE ANSRECORDVERSION>0 ");
        List<AlertNotificationSettingBean> lNotificationSettingList = alertNotificationSettingDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        
        Map<String, Map<String, Object>> lDataMap = new HashMap<String, Map<String,Object>>();
        MessageType lMessageType = MessageType.None;
        if (!lNotificationSettingList.isEmpty()) {
        	for (AlertNotificationSettingBean lBean : lNotificationSettingList) {
                Map<String, Object> lData = new HashMap<String, Object>();
                //
                lData.put("mobileList", lBean.getMobileList());
                //
                lData.put("emailList", lBean.getEmailList());
                //
                lDataMap.put(lBean.getNotificationType(), lData);
            }
        }
        Map<String, Object> lResults = new HashMap<String, Object>();
        lResults.put("meta", OtherResourceCache.getInstance().getAlertSettings());
        lResults.put("data", lDataMap);
        
        return new JsonBuilder(lResults).toString();
    }
}
