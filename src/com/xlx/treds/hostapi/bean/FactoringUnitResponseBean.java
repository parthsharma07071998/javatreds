package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FactoringUnitResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FactoringUnitResponseBean() {
		/*
		 * Do Nothing
		 */
	}

	private String messageCode;
	private String message;
	private List<FactoringUnitHostApiBean> factoringUnitList;

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

	public List<FactoringUnitHostApiBean> getFactoringUnitList() {
		return factoringUnitList;
	}

	public void setFactoringUnitList(List<FactoringUnitHostApiBean> factoringUnitList) {
		this.factoringUnitList = factoringUnitList;
	}

}
