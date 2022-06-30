package com.xlx.treds;

import com.xlx.treds.AppConstants.EntityContact;

public class OtpNotificationInfo{
	
	private EntityContact entityContact;
	private Object entity; // entityCode or UserId
	
	public OtpNotificationInfo(EntityContact pEntityContact, Object pEntity){
		entityContact = pEntityContact;
		entity = pEntity;
	}
	
	public EntityContact getEntityContact() {
		return entityContact;
	}
	public void setEntityContact(EntityContact EntityContact) {
		this.entityContact = EntityContact;
	}
	public Object getEntity() {
		return entity;
	}
	public void setEntity(Object entity) {
		this.entity = entity;
	}
}

