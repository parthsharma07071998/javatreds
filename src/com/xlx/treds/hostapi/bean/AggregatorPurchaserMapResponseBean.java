package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.util.List;

public class AggregatorPurchaserMapResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AggregatorPurchaserMapResponseBean() {
		/*
		 * Do Nothing
		 */
	}

	private String messageCode;
	private String message;
	List<AggregatorPurchaserMapHostApiBean> aggregatorPurchaserMap;

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

	public List<AggregatorPurchaserMapHostApiBean> getAggregatorPurchaserMap() {
		return aggregatorPurchaserMap;
	}

	public void setAggregatorPurchaserMap(List<AggregatorPurchaserMapHostApiBean> aggregatorPurchaserMap) {
		this.aggregatorPurchaserMap = aggregatorPurchaserMap;
	}

}
