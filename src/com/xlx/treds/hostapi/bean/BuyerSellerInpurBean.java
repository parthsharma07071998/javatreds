package com.xlx.treds.hostapi.bean;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class BuyerSellerInpurBean {

	private String buyerCode;
	private String sellerCode;

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	@Override
	public String toString() {
		return "BuyerSellerInpurBean [buyerCode=" + buyerCode + ", sellerCode=" + sellerCode + "]";
	}

}
