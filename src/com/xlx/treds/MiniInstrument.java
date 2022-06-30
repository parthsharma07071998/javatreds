package com.xlx.treds;

import java.math.BigDecimal;
import java.util.Date;

import com.xlx.common.utilities.FormatHelper;

public class MiniInstrument{
	private String buyerGstn;
	private String invoiceId;
	private String invoiceIssueDate;
	private String amount;
	private String ewayId;
	//
//	public MiniInstrument(String pBuyerGstn, String pInvoiceId, String pInvoiceIssueDate, String pAmount, String pEwayId){
//		setBuyerGstn(pBuyerGstn);
//		setInvoiceId(pInvoiceId);
//		setInvoiceIssueDate(pInvoiceIssueDate);
//		setAmount(pAmount);
//		setEwayId(pEwayId);
//	}
	public MiniInstrument(String pBuyerGstn, String pInvoiceId, Date pInvoiceIssueDate, BigDecimal pAmount, String pEwayId){
		setBuyerGstn(pBuyerGstn);
		setInvoiceId(pInvoiceId);
		setInvoiceIssueDate(FormatHelper.getDisplay("yyyy-MM-dd",  pInvoiceIssueDate));
		setAmount(pAmount.toString());
		setEwayId(pEwayId);
	}
	public String getBuyerGstn() {
		return buyerGstn;
	}
	public void setBuyerGstn(String buyerGstn) {
		this.buyerGstn = buyerGstn;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getInvoiceIssueDate() {
		return invoiceIssueDate;
	}
	public void setInvoiceIssueDate(String invoiceIssueDate) {
		this.invoiceIssueDate = invoiceIssueDate;
	}
	public String getAmount() {
		return this.amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getEwayId() {
		return ewayId;
	}
	public void setEwayId(String ewayId) {
		this.ewayId = ewayId;
	}
}