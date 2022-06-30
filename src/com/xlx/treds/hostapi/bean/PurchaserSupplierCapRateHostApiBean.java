package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PurchaserSupplierCapRateHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PurchaserSupplierCapRateHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal PSLID;

	public BigDecimal getPSLID() {
		return this.PSLID;
	}

	public void setPSLID(BigDecimal value) {
		this.PSLID = value;
	}

	private String PSLENTITYCODE;

	public String getPSLENTITYCODE() {
		return this.PSLENTITYCODE;
	}

	public void setPSLENTITYCODE(String value) {
		this.PSLENTITYCODE = value;
	}

	private String PSLCOUNTERENTITYCODE;

	public String getPSLCOUNTERENTITYCODE() {
		return this.PSLCOUNTERENTITYCODE;
	}

	public void setPSLCOUNTERENTITYCODE(String value) {
		this.PSLCOUNTERENTITYCODE = value;
	}

	private BigDecimal PSLFROMHAIRCUT;

	public BigDecimal getPSLFROMHAIRCUT() {
		return this.PSLFROMHAIRCUT;
	}

	public void setPSLFROMHAIRCUT(BigDecimal value) {
		this.PSLFROMHAIRCUT = value;
	}

	private BigDecimal PSLTOHAIRCUT;

	public BigDecimal getPSLTOHAIRCUT() {
		return this.PSLTOHAIRCUT;
	}

	public void setPSLTOHAIRCUT(BigDecimal value) {
		this.PSLTOHAIRCUT = value;
	}

	private BigDecimal PSLFROMUSANCE;

	public BigDecimal getPSLFROMUSANCE() {
		return this.PSLFROMUSANCE;
	}

	public void setPSLFROMUSANCE(BigDecimal value) {
		this.PSLFROMUSANCE = value;
	}

	private BigDecimal PSLTOUSANCE;

	public BigDecimal getPSLTOUSANCE() {
		return this.PSLTOUSANCE;
	}

	public void setPSLTOUSANCE(BigDecimal value) {
		this.PSLTOUSANCE = value;
	}

	private BigDecimal PSLCAPRATE;

	public BigDecimal getPSLCAPRATE() {
		return this.PSLCAPRATE;
	}

	public void setPSLCAPRATE(BigDecimal value) {
		this.PSLCAPRATE = value;
	}

}
