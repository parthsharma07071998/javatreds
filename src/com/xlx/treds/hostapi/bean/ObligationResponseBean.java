package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ObligationResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObligationResponseBean() {
		/*
		 * Do Nothing
		 */
	}

	private String messageCode;
	private String message;
	private List<ObligationHostApiBean> obligationHostApiBeans;

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

	public List<ObligationHostApiBean> getObligationHostApiBeans() {
		return obligationHostApiBeans;
	}

	public void setObligationHostApiBeans(List<ObligationHostApiBean> obligationHostApiBeans) {
		this.obligationHostApiBeans = obligationHostApiBeans;
	}

}
