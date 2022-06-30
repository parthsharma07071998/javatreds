package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ObligationExtensionHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObligationExtensionHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal OEOBID;

	public BigDecimal getOEOBID() {
		return this.OEOBID;
	}

	public void setOEOBID(BigDecimal value) {
		this.OEOBID = value;
	}

	private BigDecimal OECREDITOBID;

	public BigDecimal getOECREDITOBID() {
		return this.OECREDITOBID;
	}

	public void setOECREDITOBID(BigDecimal value) {
		this.OECREDITOBID = value;
	}

	private String OEPURCHASER;

	public String getOEPURCHASER() {
		return this.OEPURCHASER;
	}

	public void setOEPURCHASER(String value) {
		this.OEPURCHASER = value;
	}

	private String OEFINANCIER;

	public String getOEFINANCIER() {
		return this.OEFINANCIER;
	}

	public void setOEFINANCIER(String value) {
		this.OEFINANCIER = value;
	}

	private Date OEOLDDATE;

	public Date getOEOLDDATE() {
		return this.OEOLDDATE;
	}

	public void setOEOLDDATE(Date value) {
		this.OEOLDDATE = value;
	}

	private String OECURRENCY;

	public String getOECURRENCY() {
		return this.OECURRENCY;
	}

	public void setOECURRENCY(String value) {
		this.OECURRENCY = value;
	}

	private BigDecimal OEOLDAMOUNT;

	public BigDecimal getOEOLDAMOUNT() {
		return this.OEOLDAMOUNT;
	}

	public void setOEOLDAMOUNT(BigDecimal value) {
		this.OEOLDAMOUNT = value;
	}

	private Date OENEWDATE;

	public Date getOENEWDATE() {
		return this.OENEWDATE;
	}

	public void setOENEWDATE(Date value) {
		this.OENEWDATE = value;
	}

	private BigDecimal OEINTEREST;

	public BigDecimal getOEINTEREST() {
		return this.OEINTEREST;
	}

	public void setOEINTEREST(BigDecimal value) {
		this.OEINTEREST = value;
	}

	private BigDecimal OEPENALTY;

	public BigDecimal getOEPENALTY() {
		return this.OEPENALTY;
	}

	public void setOEPENALTY(BigDecimal value) {
		this.OEPENALTY = value;
	}

	private BigDecimal OEPENALTYRATE;

	public BigDecimal getOEPENALTYRATE() {
		return this.OEPENALTYRATE;
	}

	public void setOEPENALTYRATE(BigDecimal value) {
		this.OEPENALTYRATE = value;
	}

	private BigDecimal OENEWAMOUNT;

	public BigDecimal getOENEWAMOUNT() {
		return this.OENEWAMOUNT;
	}

	public void setOENEWAMOUNT(BigDecimal value) {
		this.OENEWAMOUNT = value;
	}

	private String OESTATUS;

	public String getOESTATUS() {
		return this.OESTATUS;
	}

	public void setOESTATUS(String value) {
		this.OESTATUS = value;
	}

	private String OEREMARKS;

	public String getOEREMARKS() {
		return this.OEREMARKS;
	}

	public void setOEREMARKS(String value) {
		this.OEREMARKS = value;
	}

	private BigDecimal OERECORDCREATOR;

	public BigDecimal getOERECORDCREATOR() {
		return this.OERECORDCREATOR;
	}

	public void setOERECORDCREATOR(BigDecimal value) {
		this.OERECORDCREATOR = value;
	}

	private Date OERECORDCREATETIME;

	public Date getOERECORDCREATETIME() {
		return this.OERECORDCREATETIME;
	}

	public void setOERECORDCREATETIME(Date value) {
		this.OERECORDCREATETIME = value;
	}

	private BigDecimal OERECORDUPDATOR;

	public BigDecimal getOERECORDUPDATOR() {
		return this.OERECORDUPDATOR;
	}

	public void setOERECORDUPDATOR(BigDecimal value) {
		this.OERECORDUPDATOR = value;
	}

	private Date OERECORDUPDATETIME;

	public Date getOERECORDUPDATETIME() {
		return this.OERECORDUPDATETIME;
	}

	public void setOERECORDUPDATETIME(Date value) {
		this.OERECORDUPDATETIME = value;
	}

	private BigDecimal OEINTERESTRATE;

	public BigDecimal getOEINTERESTRATE() {
		return this.OEINTERESTRATE;
	}

	public void setOEINTERESTRATE(BigDecimal value) {
		this.OEINTERESTRATE = value;
	}

	private BigDecimal OETREDSCHARGE;

	public BigDecimal getOETREDSCHARGE() {
		return this.OETREDSCHARGE;
	}

	public void setOETREDSCHARGE(BigDecimal value) {
		this.OETREDSCHARGE = value;
	}

	private Date OECHARGEDATE;

	public Date getOECHARGEDATE() {
		return this.OECHARGEDATE;
	}

	public void setOECHARGEDATE(Date value) {
		this.OECHARGEDATE = value;
	}

	private Date OESUBMITDATE;

	public Date getOESUBMITDATE() {
		return this.OESUBMITDATE;
	}

	public void setOESUBMITDATE(Date value) {
		this.OESUBMITDATE = value;
	}

	private Date OEAPPROVEDATE;

	public Date getOEAPPROVEDATE() {
		return this.OEAPPROVEDATE;
	}

	public void setOEAPPROVEDATE(Date value) {
		this.OEAPPROVEDATE = value;
	}

	private String OEUPFRONTCHARGE;

	public String getOEUPFRONTCHARGE() {
		return OEUPFRONTCHARGE;
	}

	public void setOEUPFRONTCHARGE(String oEUPFRONTCHARGE) {
		OEUPFRONTCHARGE = oEUPFRONTCHARGE;
	}

}
