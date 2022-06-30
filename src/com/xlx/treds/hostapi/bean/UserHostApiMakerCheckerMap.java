package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserHostApiMakerCheckerMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserHostApiMakerCheckerMap() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal MCMID;

	public BigDecimal getMCMID() {
		return this.MCMID;
	}

	public void setMCMID(BigDecimal value) {
		this.MCMID = value;
	}

	private String MCMCHECKERTYPE;

	public String getMCMCHECKERTYPE() {
		return this.MCMCHECKERTYPE;
	}

	public void setMCMCHECKERTYPE(String value) {
		this.MCMCHECKERTYPE = value;
	}

	private BigDecimal MCMMAKERID;

	public BigDecimal getMCMMAKERID() {
		return this.MCMMAKERID;
	}

	public void setMCMMAKERID(BigDecimal value) {
		this.MCMMAKERID = value;
	}

	private BigDecimal MCMCHECKERID;

	public BigDecimal getMCMCHECKERID() {
		return this.MCMCHECKERID;
	}

	public void setMCMCHECKERID(BigDecimal value) {
		this.MCMCHECKERID = value;
	}

	private BigDecimal MCMRECORDCREATOR;

	public BigDecimal getMCMRECORDCREATOR() {
		return this.MCMRECORDCREATOR;
	}

	public void setMCMRECORDCREATOR(BigDecimal value) {
		this.MCMRECORDCREATOR = value;
	}

	private Date MCMRECORDCREATETIME;

	public Date getMCMRECORDCREATETIME() {
		return this.MCMRECORDCREATETIME;
	}

	public void setMCMRECORDCREATETIME(Date value) {
		this.MCMRECORDCREATETIME = value;
	}

	private BigDecimal MCMRECORDUPDATOR;

	public BigDecimal getMCMRECORDUPDATOR() {
		return this.MCMRECORDUPDATOR;
	}

	public void setMCMRECORDUPDATOR(BigDecimal value) {
		this.MCMRECORDUPDATOR = value;
	}

	private Date MCMRECORDUPDATETIME;

	public Date getMCMRECORDUPDATETIME() {
		return this.MCMRECORDUPDATETIME;
	}

	public void setMCMRECORDUPDATETIME(Date value) {
		this.MCMRECORDUPDATETIME = value;
	}

	private BigDecimal MCMRECORDVERSION;

	public BigDecimal getMCMRECORDVERSION() {
		return this.MCMRECORDVERSION;
	}

	public void setMCMRECORDVERSION(BigDecimal value) {
		this.MCMRECORDVERSION = value;
	}

}
