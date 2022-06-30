package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class BillFactoringInstrumentHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BillFactoringInstrumentHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal instrumentId;
	private BigDecimal netAmount;

	public BigDecimal getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(BigDecimal instrumentId) {
		this.instrumentId = instrumentId;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

}
