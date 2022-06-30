package com.xlx.treds.user.bean;

import java.sql.Timestamp;
import java.util.Date;


public interface IAgreementAcceptanceBean {
	Long getId();
	String getEntity();
	void setEntity(String pEntity);
	Date getRevisionDate();
	void setRevisionDate(Date pRevisionDate);
	String getVersion();
	void setVersion(String pVersion);
	Long getKeyId();
	Long getRecordCreator();
	void setRecordCreator(Long pRecordCreator);
	Timestamp getRecordCreateTime();
	Long getRecordUpdator();
	Timestamp getRecordUpdateTime();
	Long getRecordVersion();
}
