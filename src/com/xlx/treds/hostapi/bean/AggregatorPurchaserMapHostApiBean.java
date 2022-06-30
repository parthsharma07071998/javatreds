package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AggregatorPurchaserMapHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AggregatorPurchaserMapHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private String APMAGGREGATOR;

	public String getAPMAGGREGATOR() {
		return this.APMAGGREGATOR;
	}

	public void setAPMAGGREGATOR(String value) {
		this.APMAGGREGATOR = value;
	}

	private String APMPURCHASER;

	public String getAPMPURCHASER() {
		return this.APMPURCHASER;
	}

	public void setAPMPURCHASER(String value) {
		this.APMPURCHASER = value;
	}

	private BigDecimal APMRECORDCREATOR;

	public BigDecimal getAPMRECORDCREATOR() {
		return this.APMRECORDCREATOR;
	}

	public void setAPMRECORDCREATOR(BigDecimal value) {
		this.APMRECORDCREATOR = value;
	}

	private Date APMRECORDCREATETIME;

	public Date getAPMRECORDCREATETIME() {
		return this.APMRECORDCREATETIME;
	}

	public void setAPMRECORDCREATETIME(Date value) {
		this.APMRECORDCREATETIME = value;
	}

	private BigDecimal APMRECORDUPDATOR;

	public BigDecimal getAPMRECORDUPDATOR() {
		return this.APMRECORDUPDATOR;
	}

	public void setAPMRECORDUPDATOR(BigDecimal value) {
		this.APMRECORDUPDATOR = value;
	}

	private Date APMRECORDUPDATETIME;

	public Date getAPMRECORDUPDATETIME() {
		return this.APMRECORDUPDATETIME;
	}

	public void setAPMRECORDUPDATETIME(Date value) {
		this.APMRECORDUPDATETIME = value;
	}

	private BigDecimal APMRECORDVERSION;

	public BigDecimal getAPMRECORDVERSION() {
		return this.APMRECORDVERSION;
	}

	public void setAPMRECORDVERSION(BigDecimal value) {
		this.APMRECORDVERSION = value;
	}

}
