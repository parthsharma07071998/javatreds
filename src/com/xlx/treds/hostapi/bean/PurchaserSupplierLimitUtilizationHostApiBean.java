package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class PurchaserSupplierLimitUtilizationHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PurchaserSupplierLimitUtilizationHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal PSLUID;

	public BigDecimal getPSLUID() {
		return this.PSLUID;
	}

	public void setPSLUID(BigDecimal value) {
		this.PSLUID = value;
	}

	private String PSLUSUPPLIER;

	public String getPSLUSUPPLIER() {
		return this.PSLUSUPPLIER;
	}

	public void setPSLUSUPPLIER(String value) {
		this.PSLUSUPPLIER = value;
	}

	private String PSLUPURCHASER;

	public String getPSLUPURCHASER() {
		return this.PSLUPURCHASER;
	}

	public void setPSLUPURCHASER(String value) {
		this.PSLUPURCHASER = value;
	}

	private BigDecimal PSLULIMIT;

	public BigDecimal getPSLULIMIT() {
		return this.PSLULIMIT;
	}

	public void setPSLULIMIT(BigDecimal value) {
		this.PSLULIMIT = value;
	}

	private BigDecimal PSLULIMITUTILIZED;

	public BigDecimal getPSLULIMITUTILIZED() {
		return this.PSLULIMITUTILIZED;
	}

	public void setPSLULIMITUTILIZED(BigDecimal value) {
		this.PSLULIMITUTILIZED = value;
	}

	private String PSLUSTATUS;

	public String getPSLUSTATUS() {
		return this.PSLUSTATUS;
	}

	public void setPSLUSTATUS(String value) {
		this.PSLUSTATUS = value;
	}

	private BigDecimal PSLURECORDCREATOR;

	public BigDecimal getPSLURECORDCREATOR() {
		return this.PSLURECORDCREATOR;
	}

	public void setPSLURECORDCREATOR(BigDecimal value) {
		this.PSLURECORDCREATOR = value;
	}

	private Date PSLURECORDCREATETIME;

	public Date getPSLURECORDCREATETIME() {
		return this.PSLURECORDCREATETIME;
	}

	public void setPSLURECORDCREATETIME(Date value) {
		this.PSLURECORDCREATETIME = value;
	}

	private BigDecimal PSLURECORDUPDATOR;

	public BigDecimal getPSLURECORDUPDATOR() {
		return this.PSLURECORDUPDATOR;
	}

	public void setPSLURECORDUPDATOR(BigDecimal value) {
		this.PSLURECORDUPDATOR = value;
	}

	private Date PSLURECORDUPDATETIME;

	public Date getPSLURECORDUPDATETIME() {
		return this.PSLURECORDUPDATETIME;
	}

	public void setPSLURECORDUPDATETIME(Date value) {
		this.PSLURECORDUPDATETIME = value;
	}

	private BigDecimal PSLURECORDVERSION;

	public BigDecimal getPSLURECORDVERSION() {
		return this.PSLURECORDVERSION;
	}

	public void setPSLURECORDVERSION(BigDecimal value) {
		this.PSLURECORDVERSION = value;
	}
}
