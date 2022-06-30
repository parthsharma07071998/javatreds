package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BillsRegistrationChargesHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BillsRegistrationChargesHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal RCID;

	public BigDecimal getRCID() {
		return this.RCID;
	}

	public void setRCID(BigDecimal value) {
		this.RCID = value;
	}

	private String RCENTITYCODE;

	public String getRCENTITYCODE() {
		return this.RCENTITYCODE;
	}

	public void setRCENTITYCODE(String value) {
		this.RCENTITYCODE = value;
	}

	private String RCENTITYTYPE;

	public String getRCENTITYTYPE() {
		return this.RCENTITYTYPE;
	}

	public void setRCENTITYTYPE(String value) {
		this.RCENTITYTYPE = value;
	}

	private String RCCHARGETYPE;

	public String getRCCHARGETYPE() {
		return this.RCCHARGETYPE;
	}

	public void setRCCHARGETYPE(String value) {
		this.RCCHARGETYPE = value;
	}

	private java.sql.Date RCEFFECTIVEDATE;

	public java.sql.Date getRCEFFECTIVEDATE() {
		return this.RCEFFECTIVEDATE;
	}

	public void setRCEFFECTIVEDATE(java.sql.Date value) {
		this.RCEFFECTIVEDATE = value;
	}

	private BigDecimal RCCHARGEAMOUNT;

	public BigDecimal getRCCHARGEAMOUNT() {
		return this.RCCHARGEAMOUNT;
	}

	public void setRCCHARGEAMOUNT(BigDecimal value) {
		this.RCCHARGEAMOUNT = value;
	}

	private String RCREQUESTTYPE;

	public String getRCREQUESTTYPE() {
		return this.RCREQUESTTYPE;
	}

	public void setRCREQUESTTYPE(String value) {
		this.RCREQUESTTYPE = value;
	}

	private Date RCEXTENDEDDATE;

	public Date getRCEXTENDEDDATE() {
		return this.RCEXTENDEDDATE;
	}

	public void setRCEXTENDEDDATE(Date value) {
		this.RCEXTENDEDDATE = value;
	}

	private BigDecimal RCEXTENSIONCOUNT;

	public BigDecimal getRCEXTENSIONCOUNT() {
		return this.RCEXTENSIONCOUNT;
	}

	public void setRCEXTENSIONCOUNT(BigDecimal value) {
		this.RCEXTENSIONCOUNT = value;
	}

	private Date RCPAYMENTDATE;

	public Date getRCPAYMENTDATE() {
		return this.RCPAYMENTDATE;
	}

	public void setRCPAYMENTDATE(Date value) {
		this.RCPAYMENTDATE = value;
	}

	private BigDecimal RCPAYMENTAMOUNT;

	public BigDecimal getRCPAYMENTAMOUNT() {
		return this.RCPAYMENTAMOUNT;
	}

	public void setRCPAYMENTAMOUNT(BigDecimal value) {
		this.RCPAYMENTAMOUNT = value;
	}

	private String RCPAYMENTREFRENCE;

	public String getRCPAYMENTREFRENCE() {
		return this.RCPAYMENTREFRENCE;
	}

	public void setRCPAYMENTREFRENCE(String value) {
		this.RCPAYMENTREFRENCE = value;
	}

	private String RCBILLEDENTITYCODE;

	public String getRCBILLEDENTITYCODE() {
		return this.RCBILLEDENTITYCODE;
	}

	public void setRCBILLEDENTITYCODE(String value) {
		this.RCBILLEDENTITYCODE = value;
	}

	private BigDecimal RCBILLEDENTITYCLID;

	public BigDecimal getRCBILLEDENTITYCLID() {
		return this.RCBILLEDENTITYCLID;
	}

	public void setRCBILLEDENTITYCLID(BigDecimal value) {
		this.RCBILLEDENTITYCLID = value;
	}

	private String RCREMARKS;

	public String getRCREMARKS() {
		return this.RCREMARKS;
	}

	public void setRCREMARKS(String value) {
		this.RCREMARKS = value;
	}

	private String RCSUPPORTINGDOC;

	public String getRCSUPPORTINGDOC() {
		return this.RCSUPPORTINGDOC;
	}

	public void setRCSUPPORTINGDOC(String value) {
		this.RCSUPPORTINGDOC = value;
	}

	private BigDecimal RCMAKERAUID;

	public BigDecimal getRCMAKERAUID() {
		return this.RCMAKERAUID;
	}

	public void setRCMAKERAUID(BigDecimal value) {
		this.RCMAKERAUID = value;
	}

	private Date RCMAKERTIMESTAMP;

	public Date getRCMAKERTIMESTAMP() {
		return this.RCMAKERTIMESTAMP;
	}

	public void setRCMAKERTIMESTAMP(Date value) {
		this.RCMAKERTIMESTAMP = value;
	}

	private BigDecimal RCCHECKERAUID;

	public BigDecimal getRCCHECKERAUID() {
		return this.RCCHECKERAUID;
	}

	public void setRCCHECKERAUID(BigDecimal value) {
		this.RCCHECKERAUID = value;
	}

	private Date RCCHECKERTIMESTAMP;

	public Date getRCCHECKERTIMESTAMP() {
		return this.RCCHECKERTIMESTAMP;
	}

	public void setRCCHECKERTIMESTAMP(Date value) {
		this.RCCHECKERTIMESTAMP = value;
	}

	private String RCAPPROVALSTATUS;

	public String getRCAPPROVALSTATUS() {
		return this.RCAPPROVALSTATUS;
	}

	public void setRCAPPROVALSTATUS(String value) {
		this.RCAPPROVALSTATUS = value;
	}

	private BigDecimal RCRECORDCREATOR;

	public BigDecimal getRCRECORDCREATOR() {
		return this.RCRECORDCREATOR;
	}

	public void setRCRECORDCREATOR(BigDecimal value) {
		this.RCRECORDCREATOR = value;
	}

	private Date RCRECORDCREATETIME;

	public Date getRCRECORDCREATETIME() {
		return this.RCRECORDCREATETIME;
	}

	public void setRCRECORDCREATETIME(Date value) {
		this.RCRECORDCREATETIME = value;
	}

	private BigDecimal RCRECORDUPDATOR;

	public BigDecimal getRCRECORDUPDATOR() {
		return this.RCRECORDUPDATOR;
	}

	public void setRCRECORDUPDATOR(BigDecimal value) {
		this.RCRECORDUPDATOR = value;
	}

	private Date RCRECORDUPDATETIME;

	public Date getRCRECORDUPDATETIME() {
		return this.RCRECORDUPDATETIME;
	}

	public void setRCRECORDUPDATETIME(Date value) {
		this.RCRECORDUPDATETIME = value;
	}

	private BigDecimal RCRECORDVERSION;

	public BigDecimal getRCRECORDVERSION() {
		return this.RCRECORDVERSION;
	}

	public void setRCRECORDVERSION(BigDecimal value) {
		this.RCRECORDVERSION = value;
	}

	private Date RCPREVEXTENDEDDATE;

	public Date getRCPREVEXTENDEDDATE() {
		return this.RCPREVEXTENDEDDATE;
	}

	public void setRCPREVEXTENDEDDATE(Date value) {
		this.RCPREVEXTENDEDDATE = value;
	}

	private BigDecimal RCBILLID;

	public BigDecimal getRCBILLID() {
		return this.RCBILLID;
	}

	public void setRCBILLID(BigDecimal value) {
		this.RCBILLID = value;
	}
}
