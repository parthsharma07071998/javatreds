package com.xlx.treds.instrument.bean;

import java.math.BigDecimal;
import java.sql.Date;

public class MonetagoRequiredFieldsBean {
	private Long id;
	private String supGstn;
	private String purGstn;
	private String instNumber;
	private Date instDate;
	private BigDecimal amount;
	private String ledgerId;
	private String message;
	private String error;
	
	public Long getId() {
		return id;
	}
	public void setId(Long pId) {
		id = pId;
	}
	
	public String getSupGstn() {
		return supGstn;
	}
	public void setSupGstn(String pSupGstn) {
		supGstn = pSupGstn;
	}
	
	public String getPurGstn() {
		return purGstn;
	}
	public void setPurGstn(String pPurGstn) {
		purGstn = pPurGstn;
	}
	
	public Date getInstDate() {
		return instDate;
	}
	public void setInstDate(Date pInstDate) {
		instDate = pInstDate;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal pAmount) {
		amount = pAmount;
	}
	
	public String getLedgerId() {
		return ledgerId;
	}
	public void setLedgerId(String pLedgerId) {
		ledgerId = pLedgerId;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String pMessage) {
		message = pMessage;
	}
	
	public String getError() {
		return error;
	}
	public void setError(String pError) {
		error = pError;
	}
	
	public String getInstNumber() {
		return instNumber;
	}
	public void setInstNumber(String pInstNumber) {
		instNumber = pInstNumber;
	}

}
