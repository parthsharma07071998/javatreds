package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PurchaserSupplierLinkResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PurchaserSupplierLinkResponseBean() {
		/*
		 * Do Nothing
		 */
	}

	private String messageCode;
	private String message;
	private List<PurchaserSupplierLinkHostApiBean> purchaserSupplierLinkHostApiBeans;

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

	public List<PurchaserSupplierLinkHostApiBean> getPurchaserSupplierLinkHostApiBeans() {
		return purchaserSupplierLinkHostApiBeans;
	}

	public void setPurchaserSupplierLinkHostApiBeans(
			List<PurchaserSupplierLinkHostApiBean> purchaserSupplierLinkHostApiBeans) {
		this.purchaserSupplierLinkHostApiBeans = purchaserSupplierLinkHostApiBeans;
	}

}
