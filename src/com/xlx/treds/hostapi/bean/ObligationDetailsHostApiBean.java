package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ObligationDetailsHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObligationDetailsHostApiBean() {
		/*
		 * DO Nothing
		 */
	}

	private BigDecimal OBDID;

	public BigDecimal getOBDID() {
		return this.OBDID;
	}

	public void setOBDID(BigDecimal value) {
		this.OBDID = value;
	}

	private BigDecimal OBDFUID;

	public BigDecimal getOBDFUID() {
		return this.OBDFUID;
	}

	public void setOBDFUID(BigDecimal value) {
		this.OBDFUID = value;
	}

	private java.sql.Date OBDDATE;

	public java.sql.Date getOBDDATE() {
		return this.OBDDATE;
	}

	public void setOBDDATE(java.sql.Date value) {
		this.OBDDATE = value;
	}

	private String OBDTYPE;

	public String getOBDTYPE() {
		return this.OBDTYPE;
	}

	public void setOBDTYPE(String value) {
		this.OBDTYPE = value;
	}

	private String OBDDEBITENTITY;

	public String getOBDDEBITENTITY() {
		return this.OBDDEBITENTITY;
	}

	public void setOBDDEBITENTITY(String value) {
		this.OBDDEBITENTITY = value;
	}

	private String OBDCREDITENTITY;

	public String getOBDCREDITENTITY() {
		return this.OBDCREDITENTITY;
	}

	public void setOBDCREDITENTITY(String value) {
		this.OBDCREDITENTITY = value;
	}

	private String OBDCURRENCY;

	public String getOBDCURRENCY() {
		return this.OBDCURRENCY;
	}

	public void setOBDCURRENCY(String value) {
		this.OBDCURRENCY = value;
	}

	private BigDecimal OBDAMOUNT;

	public BigDecimal getOBDAMOUNT() {
		return this.OBDAMOUNT;
	}

	public void setOBDAMOUNT(BigDecimal value) {
		this.OBDAMOUNT = value;
	}

	private String OBDREASONCODE;

	public String getOBDREASONCODE() {
		return this.OBDREASONCODE;
	}

	public void setOBDREASONCODE(String value) {
		this.OBDREASONCODE = value;
	}

}
