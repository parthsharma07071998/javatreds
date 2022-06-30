package com.xlx.treds.entity.bean;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.MailerType;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class EntityNotificationSettingBean {

	public static final String FIELDGROUP_RMMAIL = "mailRm";
	
    private String code;
    private String notificationType;
    private List<String> emailList;
    private MailerType mailerType;
    private YesNo mailRm;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String pNotificationType) {
        notificationType = pNotificationType;
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

    public MailerType getMailerType() {
        return mailerType;
    }

    public void setMailerType(MailerType pMailerType) {
        mailerType = pMailerType;
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
    
    public boolean notifyExplicitMailIds(){
    	return (AppConstants.MailerType.Both.equals(getMailerType()) || AppConstants.MailerType.Explicit.equals(getMailerType()));
    }

    public boolean notifyImplicitMailIds(){
    	return (AppConstants.MailerType.Both.equals(getMailerType()) || AppConstants.MailerType.Implicit.equals(getMailerType()));
    }

	public YesNo getMailRm() {
		return mailRm;
	}

	public void setMailRm(YesNo pMailRm) {
		mailRm = pMailRm;
	}

}