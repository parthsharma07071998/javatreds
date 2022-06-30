package com.xlx.treds.entity.bean;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xlx.treds.AppConstants.MessageType;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class AlertNotificationSettingBean {

    private String notificationType;
    private String mobile;
    private List<String> mobileList;
    private MessageType smsMessageType;
    private String email;
    private List<String> emailList;
    private MessageType emailMessageType;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String pNotificationType) {
        notificationType = pNotificationType;
    }

    public String getMobile() {
  	   if (mobileList == null)
            return null;
        else 
            return new JsonBuilder(mobileList).toString();
     }

     public void setMobile(String pMobile) {
     	if (StringUtils.isNotBlank(pMobile))
     		mobileList = (List<String>)new JsonSlurper().parseText(pMobile);
         else
         	mobileList = null;
     }

     public List<String> getMobileList() {
         return mobileList;
     }

     public void setMobileList(List<String> pMobileList) {
         mobileList = pMobileList;
     }

     public String getEmail() {
   	   if (emailList == null)
            return null;
        else 
            return new JsonBuilder(emailList).toString();
     }

     public void setEmail(String pEmail) {
     	if (StringUtils.isNotBlank(pEmail))
     		emailList = (List<String>)new JsonSlurper().parseText(pEmail);
         else
         	emailList = null;
     }

     public List<String> getEmailList() {
         return emailList;
     }

     public void setEmailList(List<String> pEmailList) {
         emailList = pEmailList;
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