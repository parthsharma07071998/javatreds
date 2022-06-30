package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ObligationModificationDetailsHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObligationModificationDetailsHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal OMDID;

	public BigDecimal getOMDID() {
		return this.OMDID;
	}

	public void setOMDID(BigDecimal value) {
		this.OMDID = value;
	}

	private BigDecimal OMDOMRID;

	public BigDecimal getOMDOMRID() {
		return this.OMDOMRID;
	}

	public void setOMDOMRID(BigDecimal value) {
		this.OMDOMRID = value;
	}

	private BigDecimal OMDOBID;

	public BigDecimal getOMDOBID() {
		return this.OMDOBID;
	}

	public void setOMDOBID(BigDecimal value) {
		this.OMDOBID = value;
	}

	private BigDecimal OMDPARTNUMBER;

	public BigDecimal getOMDPARTNUMBER() {
		return this.OMDPARTNUMBER;
	}

	public void setOMDPARTNUMBER(BigDecimal value) {
		this.OMDPARTNUMBER = value;
	}

	private String OMDTXNTYPE;

	public String getOMDTXNTYPE() {
		return this.OMDTXNTYPE;
	}

	public void setOMDTXNTYPE(String value) {
		this.OMDTXNTYPE = value;
	}

	private BigDecimal OMDORIGAMOUNT;

	public BigDecimal getOMDORIGAMOUNT() {
		return this.OMDORIGAMOUNT;
	}

	public void setOMDORIGAMOUNT(BigDecimal value) {
		this.OMDORIGAMOUNT = value;
	}

	private Date OMDORIGDATE;

	public Date getOMDORIGDATE() {
		return this.OMDORIGDATE;
	}

	public void setOMDORIGDATE(Date value) {
		this.OMDORIGDATE = value;
	}

	private String OMDORIGSTATUS;

	public String getOMDORIGSTATUS() {
		return this.OMDORIGSTATUS;
	}

	public void setOMDORIGSTATUS(String value) {
		this.OMDORIGSTATUS = value;
	}

	private BigDecimal OMDREVISEDAMOUNT;

	public BigDecimal getOMDREVISEDAMOUNT() {
		return this.OMDREVISEDAMOUNT;
	}

	public void setOMDREVISEDAMOUNT(BigDecimal value) {
		this.OMDREVISEDAMOUNT = value;
	}

	private Date OMDREVISEDDATE;

	public Date getOMDREVISEDDATE() {
		return this.OMDREVISEDDATE;
	}

	public void setOMDREVISEDDATE(Date value) {
		this.OMDREVISEDDATE = value;
	}

	private String OMDREVISEDSTATUS;

	public String getOMDREVISEDSTATUS() {
		return this.OMDREVISEDSTATUS;
	}

	public void setOMDREVISEDSTATUS(String value) {
		this.OMDREVISEDSTATUS = value;
	}

	private String OMDPAYMENTSETTLOR;

	public String getOMDPAYMENTSETTLOR() {
		return this.OMDPAYMENTSETTLOR;
	}

	public void setOMDPAYMENTSETTLOR(String value) {
		this.OMDPAYMENTSETTLOR = value;
	}

	private String OMDREMARKS;

	public String getOMDREMARKS() {
		return this.OMDREMARKS;
	}

	public void setOMDREMARKS(String value) {
		this.OMDREMARKS = value;
	}

	private String OMDPAYMENTREFNO;

	public String getOMDPAYMENTREFNO() {
		return this.OMDPAYMENTREFNO;
	}

	public void setOMDPAYMENTREFNO(String value) {
		this.OMDPAYMENTREFNO = value;
	}

	private BigDecimal OMDRECORDCREATOR;

	public BigDecimal getOMDRECORDCREATOR() {
		return this.OMDRECORDCREATOR;
	}

	public void setOMDRECORDCREATOR(BigDecimal value) {
		this.OMDRECORDCREATOR = value;
	}

	private Date OMDRECORDCREATETIME;

	public Date getOMDRECORDCREATETIME() {
		return this.OMDRECORDCREATETIME;
	}

	public void setOMDRECORDCREATETIME(Date value) {
		this.OMDRECORDCREATETIME = value;
	}

	private BigDecimal OMDRECORDUPDATOR;

	public BigDecimal getOMDRECORDUPDATOR() {
		return this.OMDRECORDUPDATOR;
	}

	public void setOMDRECORDUPDATOR(BigDecimal value) {
		this.OMDRECORDUPDATOR = value;
	}

	private Date OMDRECORDUPDATETIME;

	public Date getOMDRECORDUPDATETIME() {
		return this.OMDRECORDUPDATETIME;
	}

	public void setOMDRECORDUPDATETIME(Date value) {
		this.OMDRECORDUPDATETIME = value;
	}

	private BigDecimal OMDRECORDVERSION;

	public BigDecimal getOMDRECORDVERSION() {
		return this.OMDRECORDVERSION;
	}

	public void setOMDRECORDVERSION(BigDecimal value) {
		this.OMDRECORDVERSION = value;
	}

}
