package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PurchaserSupplierLinkWorkFlowHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PurchaserSupplierLinkWorkFlowHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private Date PLWSTATUSUPDATETIME;

	private BigDecimal PLWID;

	public BigDecimal getPLWID() {
		return this.PLWID;
	}

	public void setPLWID(BigDecimal value) {
		this.PLWID = value;
	}

	private String PLWSUPPLIER;

	public String getPLWSUPPLIER() {
		return this.PLWSUPPLIER;
	}

	public void setPLWSUPPLIER(String value) {
		this.PLWSUPPLIER = value;
	}

	private String PLWPURCHASER;

	public String getPLWPURCHASER() {
		return this.PLWPURCHASER;
	}

	public void setPLWPURCHASER(String value) {
		this.PLWPURCHASER = value;
	}

	private String PLWSTATUS;

	public String getPLWSTATUS() {
		return this.PLWSTATUS;
	}

	public void setPLWSTATUS(String value) {
		this.PLWSTATUS = value;
	}

	private String PLWSTATUSREMARKS;

	public String getPLWSTATUSREMARKS() {
		return this.PLWSTATUSREMARKS;
	}

	public void setPLWSTATUSREMARKS(String value) {
		this.PLWSTATUSREMARKS = value;
	}

	private String PLWENTITY;

	public String getPLWENTITY() {
		return this.PLWENTITY;
	}

	public void setPLWENTITY(String value) {
		this.PLWENTITY = value;
	}

	private BigDecimal PLWAUID;

	public BigDecimal getPLWAUID() {
		return this.PLWAUID;
	}

	public void setPLWAUID(BigDecimal value) {
		this.PLWAUID = value;
	}

	public Date getPLWSTATUSUPDATETIME() {
		return PLWSTATUSUPDATETIME;
	}

	public void setPLWSTATUSUPDATETIME(Date pLWSTATUSUPDATETIME) {
		PLWSTATUSUPDATETIME = pLWSTATUSUPDATETIME;
	}

	
}
