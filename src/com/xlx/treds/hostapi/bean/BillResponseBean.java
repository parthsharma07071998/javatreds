package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class BillResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String messageCode;
	private String message;
	private List<BillsHostApiBean> billsHostApiBeans;

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<BillsHostApiBean> getBillsHostApiBeans() {
		return billsHostApiBeans;
	}

	public void setBillsHostApiBeans(List<BillsHostApiBean> billsHostApiBeans) {
		this.billsHostApiBeans = billsHostApiBeans;
	}

}
