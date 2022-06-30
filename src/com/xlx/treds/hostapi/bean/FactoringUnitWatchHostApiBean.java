package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FactoringUnitWatchHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FactoringUnitWatchHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal FUWFUID;
	private BigDecimal FUWAUID;

	public BigDecimal getFUWFUID() {
		return FUWFUID;
	}

	public void setFUWFUID(BigDecimal fUWFUID) {
		FUWFUID = fUWFUID;
	}

	public BigDecimal getFUWAUID() {
		return FUWAUID;
	}

	public void setFUWAUID(BigDecimal fUWAUID) {
		FUWAUID = fUWAUID;
	}

}
