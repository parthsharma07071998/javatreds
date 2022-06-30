package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlTransient;


@XmlTransient
public class InstrumentWorkFlowHostApiBean implements Serializable {

	public InstrumentWorkFlowHostApiBean() {
		/*
		 * Do Nothing
		 */

	}

	private Long IWFID;
	private Long IWFINID;
	private String IWFSTATUS;
	private String IWFSTATUSREMARKS;
	private String IWFENTITY;
	private Long IWFAUID;
	// private String loginId;
	// private String name;
	private Timestamp IWFSTATUSUPDATETIME;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Long getIWFID() {
		return IWFID;
	}

	public void setIWFID(Long iWFID) {
		IWFID = iWFID;
	}

	public Long getIWFINID() {
		return IWFINID;
	}

	public void setIWFINID(Long iWFINID) {
		IWFINID = iWFINID;
	}

	public String getIWFSTATUS() {
		return IWFSTATUS;
	}

	public void setIWFSTATUS(String iWFSTATUS) {
		IWFSTATUS = iWFSTATUS;
	}

	public String getIWFSTATUSREMARKS() {
		return IWFSTATUSREMARKS;
	}

	public void setIWFSTATUSREMARKS(String iWFSTATUSREMARKS) {
		IWFSTATUSREMARKS = iWFSTATUSREMARKS;
	}

	public String getIWFENTITY() {
		return IWFENTITY;
	}

	public void setIWFENTITY(String iWFENTITY) {
		IWFENTITY = iWFENTITY;
	}

	public Long getIWFAUID() {
		return IWFAUID;
	}

	public void setIWFAUID(Long iWFAUID) {
		IWFAUID = iWFAUID;
	}

	public Timestamp getIWFSTATUSUPDATETIME() {
		return IWFSTATUSUPDATETIME;
	}

	public void setIWFSTATUSUPDATETIME(Timestamp iWFSTATUSUPDATETIME) {
		IWFSTATUSUPDATETIME = iWFSTATUSUPDATETIME;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
