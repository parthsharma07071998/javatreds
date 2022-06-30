package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ObligationHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObligationHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private List<ObligationExtensionHostApiBean> obligationExtensionList;

	private List<ObligationDetailsHostApiBean> obligationDetailsList;

	private List<ObligationSplitsHostApiBean> obligationSplitsList;

	private List<ObligationModificationDetailsHostApiBean> modificationDetailsList;

	public List<ObligationModificationDetailsHostApiBean> getModificationDetailsList() {
		return modificationDetailsList;
	}

	public void setModificationDetailsList(List<ObligationModificationDetailsHostApiBean> modificationDetailsList) {
		this.modificationDetailsList = modificationDetailsList;
	}

	public List<ObligationSplitsHostApiBean> getObligationSplitsList() {
		return obligationSplitsList;
	}

	public void setObligationSplitsList(List<ObligationSplitsHostApiBean> obligationSplitsList) {
		this.obligationSplitsList = obligationSplitsList;
	}

	public List<ObligationDetailsHostApiBean> getObligationDetailsList() {
		return obligationDetailsList;
	}

	public void setObligationDetailsList(List<ObligationDetailsHostApiBean> obligationDetailsList) {
		this.obligationDetailsList = obligationDetailsList;
	}

	public List<ObligationExtensionHostApiBean> getObligationExtensionList() {
		return obligationExtensionList;
	}

	public void setObligationExtensionList(List<ObligationExtensionHostApiBean> obligationExtensionList) {
		this.obligationExtensionList = obligationExtensionList;
	}

	private BigDecimal OBID;

	public BigDecimal getOBID() {
		return this.OBID;
	}

	public void setOBID(BigDecimal value) {
		this.OBID = value;
	}

	private BigDecimal OBFUID;

	public BigDecimal getOBFUID() {
		return this.OBFUID;
	}

	public void setOBFUID(BigDecimal value) {
		this.OBFUID = value;
	}

	private BigDecimal OBBDID;

	public BigDecimal getOBBDID() {
		return this.OBBDID;
	}

	public void setOBBDID(BigDecimal value) {
		this.OBBDID = value;
	}

	private String OBTXNENTITY;

	public String getOBTXNENTITY() {
		return this.OBTXNENTITY;
	}

	public void setOBTXNENTITY(String value) {
		this.OBTXNENTITY = value;
	}

	private String OBTXNTYPE;

	public String getOBTXNTYPE() {
		return this.OBTXNTYPE;
	}

	public void setOBTXNTYPE(String value) {
		this.OBTXNTYPE = value;
	}

	private Date OBDATE;

	public Date getOBDATE() {
		return this.OBDATE;
	}

	public void setOBDATE(Date value) {
		this.OBDATE = value;
	}

	private Date OBORIGINALDATE;

	public Date getOBORIGINALDATE() {
		return this.OBORIGINALDATE;
	}

	public void setOBORIGINALDATE(Date value) {
		this.OBORIGINALDATE = value;
	}

	private String OBCURRENCY;

	public String getOBCURRENCY() {
		return this.OBCURRENCY;
	}

	public void setOBCURRENCY(String value) {
		this.OBCURRENCY = value;
	}

	private BigDecimal OBAMOUNT;

	public BigDecimal getOBAMOUNT() {
		return this.OBAMOUNT;
	}

	public void setOBAMOUNT(BigDecimal value) {
		this.OBAMOUNT = value;
	}

	private BigDecimal OBORIGINALAMOUNT;

	public BigDecimal getOBORIGINALAMOUNT() {
		return this.OBORIGINALAMOUNT;
	}

	public void setOBORIGINALAMOUNT(BigDecimal value) {
		this.OBORIGINALAMOUNT = value;
	}

	private String OBTYPE;

	public String getOBTYPE() {
		return this.OBTYPE;
	}

	public void setOBTYPE(String value) {
		this.OBTYPE = value;
	}

	private String OBNARRATION;

	public String getOBNARRATION() {
		return this.OBNARRATION;
	}

	public void setOBNARRATION(String value) {
		this.OBNARRATION = value;
	}

	private String OBSTATUS;

	public String getOBSTATUS() {
		return this.OBSTATUS;
	}

	public void setOBSTATUS(String value) {
		this.OBSTATUS = value;
	}

	private BigDecimal OBPFID;

	public BigDecimal getOBPFID() {
		return this.OBPFID;
	}

	public void setOBPFID(BigDecimal value) {
		this.OBPFID = value;
	}

	private BigDecimal OBFILESEQNO;

	public BigDecimal getOBFILESEQNO() {
		return this.OBFILESEQNO;
	}

	public void setOBFILESEQNO(BigDecimal value) {
		this.OBFILESEQNO = value;
	}

	private String OBPAYDETAIL1;

	public String getOBPAYDETAIL1() {
		return this.OBPAYDETAIL1;
	}

	public void setOBPAYDETAIL1(String value) {
		this.OBPAYDETAIL1 = value;
	}

	private String OBPAYDETAIL2;

	public String getOBPAYDETAIL2() {
		return this.OBPAYDETAIL2;
	}

	public void setOBPAYDETAIL2(String value) {
		this.OBPAYDETAIL2 = value;
	}

	private String OBPAYDETAIL3;

	public String getOBPAYDETAIL3() {
		return this.OBPAYDETAIL3;
	}

	public void setOBPAYDETAIL3(String value) {
		this.OBPAYDETAIL3 = value;
	}

	private String OBPAYDETAIL4;

	public String getOBPAYDETAIL4() {
		return this.OBPAYDETAIL4;
	}

	public void setOBPAYDETAIL4(String value) {
		this.OBPAYDETAIL4 = value;
	}

	private Date OBSETTLEDDATE;

	public Date getOBSETTLEDDATE() {
		return this.OBSETTLEDDATE;
	}

	public void setOBSETTLEDDATE(Date value) {
		this.OBSETTLEDDATE = value;
	}

	private BigDecimal OBSETTLEDAMOUNT;

	public BigDecimal getOBSETTLEDAMOUNT() {
		return this.OBSETTLEDAMOUNT;
	}

	public void setOBSETTLEDAMOUNT(BigDecimal value) {
		this.OBSETTLEDAMOUNT = value;
	}

	private String OBPAYMENTREFNO;

	public String getOBPAYMENTREFNO() {
		return this.OBPAYMENTREFNO;
	}

	public void setOBPAYMENTREFNO(String value) {
		this.OBPAYMENTREFNO = value;
	}

	private String OBRESPERRORCODE;

	public String getOBRESPERRORCODE() {
		return this.OBRESPERRORCODE;
	}

	public void setOBRESPERRORCODE(String value) {
		this.OBRESPERRORCODE = value;
	}

	private String OBRESPREMARKS;

	public String getOBRESPREMARKS() {
		return this.OBRESPREMARKS;
	}

	public void setOBRESPREMARKS(String value) {
		this.OBRESPREMARKS = value;
	}

	private BigDecimal OBOLDOBLIGATIONID;

	public BigDecimal getOBOLDOBLIGATIONID() {
		return this.OBOLDOBLIGATIONID;
	}

	public void setOBOLDOBLIGATIONID(BigDecimal value) {
		this.OBOLDOBLIGATIONID = value;
	}

	private String OBSALESCATEGORY;

	public String getOBSALESCATEGORY() {
		return this.OBSALESCATEGORY;
	}

	public void setOBSALESCATEGORY(String value) {
		this.OBSALESCATEGORY = value;
	}

	private BigDecimal OBBILLID;

	public BigDecimal getOBBILLID() {
		return this.OBBILLID;
	}

	public void setOBBILLID(BigDecimal value) {
		this.OBBILLID = value;
	}

	private BigDecimal OBSETTLEMENTCLID;

	public BigDecimal getOBSETTLEMENTCLID() {
		return this.OBSETTLEMENTCLID;
	}

	public void setOBSETTLEMENTCLID(BigDecimal value) {
		this.OBSETTLEMENTCLID = value;
	}

	private BigDecimal OBEXTENDEDDAYS;

	public BigDecimal getOBEXTENDEDDAYS() {
		return this.OBEXTENDEDDAYS;
	}

	public void setOBEXTENDEDDAYS(BigDecimal value) {
		this.OBEXTENDEDDAYS = value;
	}

	private BigDecimal OBRECORDCREATOR;

	public BigDecimal getOBRECORDCREATOR() {
		return this.OBRECORDCREATOR;
	}

	public void setOBRECORDCREATOR(BigDecimal value) {
		this.OBRECORDCREATOR = value;
	}

	private Date OBRECORDCREATETIME;

	public Date getOBRECORDCREATETIME() {
		return this.OBRECORDCREATETIME;
	}

	public void setOBRECORDCREATETIME(Date value) {
		this.OBRECORDCREATETIME = value;
	}

	private BigDecimal OBRECORDUPDATOR;

	public BigDecimal getOBRECORDUPDATOR() {
		return this.OBRECORDUPDATOR;
	}

	public void setOBRECORDUPDATOR(BigDecimal value) {
		this.OBRECORDUPDATOR = value;
	}

	private Date OBRECORDUPDATETIME;

	public Date getOBRECORDUPDATETIME() {
		return this.OBRECORDUPDATETIME;
	}

	public void setOBRECORDUPDATETIME(Date value) {
		this.OBRECORDUPDATETIME = value;
	}

	private BigDecimal OBRECORDVERSION;

	public BigDecimal getOBRECORDVERSION() {
		return this.OBRECORDVERSION;
	}

	public void setOBRECORDVERSION(BigDecimal value) {
		this.OBRECORDVERSION = value;
	}

	private String OBISUPFRONTOBLIG;

	public String getOBISUPFRONTOBLIG() {
		return this.OBISUPFRONTOBLIG;
	}

	public void setOBISUPFRONTOBLIG(String value) {
		this.OBISUPFRONTOBLIG = value;
	}

	private String OBISUPFRONT;

	public String getOBISUPFRONT() {
		return OBISUPFRONT;
	}

	public void setOBISUPFRONT(String oBISUPFRONT) {
		OBISUPFRONT = oBISUPFRONT;
	}

}
