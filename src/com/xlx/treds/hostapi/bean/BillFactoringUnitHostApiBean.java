package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class BillFactoringUnitHostApiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BillFactoringUnitHostApiBean() {
		/*
		 * Do Nothing
		 */
	}

	private BigDecimal factoringUnitId;
	private BigDecimal fuAmount;
	private BigDecimal fuExtBillId1;
	private BigDecimal fuExtBillId2;
	private List<BillsHostApiBean> billExt1;
	private List<BillsHostApiBean> billExt2;
	private List<BillFactoringInstrumentHostApiBean> billFactoringInstrumentList;

	public BigDecimal getFactoringUnitId() {
		return factoringUnitId;
	}

	public void setFactoringUnitId(BigDecimal factoringUnitId) {
		this.factoringUnitId = factoringUnitId;
	}

	public BigDecimal getFuAmount() {
		return fuAmount;
	}

	public void setFuAmount(BigDecimal fuAmount) {
		this.fuAmount = fuAmount;
	}

	public List<BillFactoringInstrumentHostApiBean> getBillFactoringInstrumentList() {
		return billFactoringInstrumentList;
	}

	public void setBillFactoringInstrumentList(List<BillFactoringInstrumentHostApiBean> billFactoringInstrumentList) {
		this.billFactoringInstrumentList = billFactoringInstrumentList;
	}

	public BigDecimal getFuExtBillId1() {
		return fuExtBillId1;
	}

	public void setFuExtBillId1(BigDecimal fuExtBillId1) {
		this.fuExtBillId1 = fuExtBillId1;
	}

	public BigDecimal getFuExtBillId2() {
		return fuExtBillId2;
	}

	public void setFuExtBillId2(BigDecimal fuExtBillId2) {
		this.fuExtBillId2 = fuExtBillId2;
	}

	public List<BillsHostApiBean> getBillExt1() {
		return billExt1;
	}

	public void setBillExt1(List<BillsHostApiBean> billExt1) {
		this.billExt1 = billExt1;
	}

	public List<BillsHostApiBean> getBillExt2() {
		return billExt2;
	}

	public void setBillExt2(List<BillsHostApiBean> billExt2) {
		this.billExt2 = billExt2;
	}

}
