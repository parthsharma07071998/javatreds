package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class PurchaserSupplierLimitUtilizationResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PurchaserSupplierLimitUtilizationResponseBean() {
		/*
		 * Do Nothing
		 */
	}

	private String messageCode;
	private String message;
	private List<PurchaserSupplierLimitUtilizationHostApiBean> purchaserSupplierLimitUtilizationHostApiBeans;

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

	public List<PurchaserSupplierLimitUtilizationHostApiBean> getPurchaserSupplierLimitUtilizationHostApiBeans() {
		return purchaserSupplierLimitUtilizationHostApiBeans;
	}

	public void setPurchaserSupplierLimitUtilizationHostApiBeans(
			List<PurchaserSupplierLimitUtilizationHostApiBean> purchaserSupplierLimitUtilizationHostApiBeans) {
		this.purchaserSupplierLimitUtilizationHostApiBeans = purchaserSupplierLimitUtilizationHostApiBeans;
	}

}
