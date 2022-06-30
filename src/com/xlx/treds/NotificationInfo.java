package com.xlx.treds;

import java.util.List;

import com.xlx.treds.AppConstants.EmailSenders;
import com.xlx.treds.AppConstants.EntityEmail;

public class NotificationInfo{
	
	private String notificationType;
	private EmailSenders emailSenders;
	private EntityEmail entityEmail;
	private Object entity; // entityCode or UserId
	private Long locationId;
	private List<String> emails;

	public NotificationInfo(String pNotificationType, EntityEmail pEntityEmail, Object pEntity, EmailSenders pEmailSenders){
		notificationType = pNotificationType;
		entityEmail = pEntityEmail;
		entity = pEntity;
		emailSenders = pEmailSenders;
		locationId = null;
	}
	public NotificationInfo(String pNotificationType, EntityEmail pEntityEmail, Object pEntity, EmailSenders pEmailSenders, Long pLocationId){
		notificationType = pNotificationType;
		entityEmail = pEntityEmail;
		entity = pEntity;
		emailSenders = pEmailSenders;
		locationId = pLocationId;
	}
	public NotificationInfo(String pNotificationType, EntityEmail pEntityEmail, Object pEntity, EmailSenders pEmailSenders, List<String> pEmails){
		notificationType = pNotificationType;
		entityEmail = pEntityEmail;
		entity = pEntity;
		emailSenders = pEmailSenders;
		emails = pEmails;
	}
	
	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	public EmailSenders getEmailSenders() {
		return emailSenders;
	}
	public void setEmailSenders(EmailSenders emailSenders) {
		this.emailSenders = emailSenders;
	}
	public EntityEmail getEntityEmail() {
		return entityEmail;
	}
	public void setEntityEmail(EntityEmail entityEmail) {
		this.entityEmail = entityEmail;
	}
	public Object getEntity() {
		return entity;
	}
	public void setEntity(Object entity) {
		this.entity = entity;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	public List<String> getEmails() {
		return emails;
	}
	public void setEmails(List<String> emails) {
		this.emails = emails;
	}
}

