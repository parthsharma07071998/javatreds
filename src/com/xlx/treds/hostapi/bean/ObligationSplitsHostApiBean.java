package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ObligationSplitsHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObligationSplitsHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal OBSOBID;

	public BigDecimal getOBSOBID() {
		return this.OBSOBID;
	}

	public void setOBSOBID(BigDecimal value) {
		this.OBSOBID = value;
	}

	private BigDecimal OBSPARTNUMBER;

	public BigDecimal getOBSPARTNUMBER() {
		return this.OBSPARTNUMBER;
	}

	public void setOBSPARTNUMBER(BigDecimal value) {
		this.OBSPARTNUMBER = value;
	}

	private BigDecimal OBSAMOUNT;

	public BigDecimal getOBSAMOUNT() {
		return this.OBSAMOUNT;
	}

	public void setOBSAMOUNT(BigDecimal value) {
		this.OBSAMOUNT = value;
	}

	private String OBSSTATUS;

	public String getOBSSTATUS() {
		return this.OBSSTATUS;
	}

	public void setOBSSTATUS(String value) {
		this.OBSSTATUS = value;
	}

	private BigDecimal OBSPFID;

	public BigDecimal getOBSPFID() {
		return this.OBSPFID;
	}

	public void setOBSPFID(BigDecimal value) {
		this.OBSPFID = value;
	}

	private BigDecimal OBSFILESEQNO;

	public BigDecimal getOBSFILESEQNO() {
		return this.OBSFILESEQNO;
	}

	public void setOBSFILESEQNO(BigDecimal value) {
		this.OBSFILESEQNO = value;
	}

	private Date OBSSETTLEDDATE;

	public Date getOBSSETTLEDDATE() {
		return this.OBSSETTLEDDATE;
	}

	public void setOBSSETTLEDDATE(Date value) {
		this.OBSSETTLEDDATE = value;
	}

	private BigDecimal OBSSETTLEDAMOUNT;

	public BigDecimal getOBSSETTLEDAMOUNT() {
		return this.OBSSETTLEDAMOUNT;
	}

	public void setOBSSETTLEDAMOUNT(BigDecimal value) {
		this.OBSSETTLEDAMOUNT = value;
	}

	private String OBSPAYMENTREFNO;

	public String getOBSPAYMENTREFNO() {
		return this.OBSPAYMENTREFNO;
	}

	public void setOBSPAYMENTREFNO(String value) {
		this.OBSPAYMENTREFNO = value;
	}

	private String OBSRESPERRORCODE;

	public String getOBSRESPERRORCODE() {
		return this.OBSRESPERRORCODE;
	}

	public void setOBSRESPERRORCODE(String value) {
		this.OBSRESPERRORCODE = value;
	}

	private String OBSRESPREMARKS;

	public String getOBSRESPREMARKS() {
		return this.OBSRESPREMARKS;
	}

	public void setOBSRESPREMARKS(String value) {
		this.OBSRESPREMARKS = value;
	}

	private String OBSPAYMENTSETTLOR;

	public String getOBSPAYMENTSETTLOR() {
		return this.OBSPAYMENTSETTLOR;
	}

	public void setOBSPAYMENTSETTLOR(String value) {
		this.OBSPAYMENTSETTLOR = value;
	}

	private String OBSSETTLORPROCESSED;

	public String getOBSSETTLORPROCESSED() {
		return this.OBSSETTLORPROCESSED;
	}

	public void setOBSSETTLORPROCESSED(String value) {
		this.OBSSETTLORPROCESSED = value;
	}

	private BigDecimal OBSRECORDUPDATOR;

	public BigDecimal getOBSRECORDUPDATOR() {
		return this.OBSRECORDUPDATOR;
	}

	public void setOBSRECORDUPDATOR(BigDecimal value) {
		this.OBSRECORDUPDATOR = value;
	}

	private Date OBSRECORDUPDATETIME;

	public Date getOBSRECORDUPDATETIME() {
		return this.OBSRECORDUPDATETIME;
	}

	public void setOBSRECORDUPDATETIME(Date value) {
		this.OBSRECORDUPDATETIME = value;
	}

	private BigDecimal OBSRECORDVERSION;

	public BigDecimal getOBSRECORDVERSION() {
		return this.OBSRECORDVERSION;
	}

	public void setOBSRECORDVERSION(BigDecimal value) {
		this.OBSRECORDVERSION = value;
	}

	private BigDecimal OBSOSRID;

	public BigDecimal getOBSOSRID() {
		return this.OBSOSRID;
	}

	public void setOBSOSRID(BigDecimal value) {
		this.OBSOSRID = value;
	}
}
