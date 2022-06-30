package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstrumentResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InstrumentResponseBean() {
		/*
		 * Do Nothing
		 */
	}

	private String messageCode;
	private String message;
	private List<InstrumentHostApiBean> instrumentBean;

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

	public List<InstrumentHostApiBean> getInstrumentBean() {
		return instrumentBean;
	}

	public void setInstrumentBean(List<InstrumentHostApiBean> instrumentBean) {
		this.instrumentBean = instrumentBean;
	}

}
