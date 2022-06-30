
package com.xlx.treds.instrument.bean;

import java.math.BigDecimal;

import com.xlx.treds.AppConstants.ChargeType;

public class GstSummaryBean {
	public static final String FIELDGROUP_GSTFIELDS = "gstfields";
	
    private String entity;
    private BigDecimal charge;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
    private BigDecimal cgstSurcharge;
    private BigDecimal sgstSurcharge;
    private BigDecimal igstSurcharge;
    private BigDecimal cgstValue;
    private BigDecimal sgstValue;
    private BigDecimal igstValue;
    private ChargeType chargeType;
    private Long obId;
    private BigDecimal tds;
    private BigDecimal tdsValue;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String pEntity) {
        entity = pEntity;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal pCharge) {
    	charge = pCharge;
    }

    public BigDecimal getCgst() {
        return cgst;
    }

    public void setCgst(BigDecimal pCgst) {
        cgst = pCgst;
    }

    public BigDecimal getSgst() {
        return sgst;
    }

    public void setSgst(BigDecimal pSgst) {
        sgst = pSgst;
    }

    public BigDecimal getIgst() {
        return igst;
    }

    public void setIgst(BigDecimal pIgst) {
        igst = pIgst;
    }

    public BigDecimal getCgstSurcharge() {
        return cgstSurcharge;
    }

    public void setCgstSurcharge(BigDecimal pCgstSurcharge) {
        cgstSurcharge = pCgstSurcharge;
    }

    public BigDecimal getSgstSurcharge() {
        return sgstSurcharge;
    }

    public void setSgstSurcharge(BigDecimal pSgstSurcharge) {
        sgstSurcharge = pSgstSurcharge;
    }

    public BigDecimal getIgstSurcharge() {
        return igstSurcharge;
    }

    public void setIgstSurcharge(BigDecimal pIgstSurcharge) {
        igstSurcharge = pIgstSurcharge;
    }

    public BigDecimal getCgstValue() {
        return cgstValue;
    }

    public void setCgstValue(BigDecimal pCgstValue) {
        cgstValue = pCgstValue;
    }

    public BigDecimal getSgstValue() {
        return sgstValue;
    }

    public void setSgstValue(BigDecimal pSgstValue) {
        sgstValue = pSgstValue;
    }

    public BigDecimal getIgstValue() {
        return igstValue;
    }

    public void setIgstValue(BigDecimal pIgstValue) {
        igstValue = pIgstValue;
    }

	@Override
	public String toString() {
		return super.toString();
	}
	
	public void initialize(){
	    cgst = BigDecimal.ZERO;
	    sgst = BigDecimal.ZERO;
	    igst = BigDecimal.ZERO;
	    cgstSurcharge = BigDecimal.ZERO;
	    sgstSurcharge = BigDecimal.ZERO;
	    igstSurcharge = BigDecimal.ZERO;
	    cgstValue = BigDecimal.ZERO;
	    sgstValue = BigDecimal.ZERO;
	    igstValue = BigDecimal.ZERO;
	    charge = BigDecimal.ZERO;
	    
	}
	
	public BigDecimal getTotalCharge(){
		BigDecimal lCharges = BigDecimal.ZERO;
		lCharges = lCharges.add(cgstValue);
		lCharges = lCharges.add(sgstValue);
		lCharges = lCharges.add(igstValue);
		lCharges = lCharges.add(charge);
		if(tds != null && tdsValue.compareTo(BigDecimal.ZERO) > 0 ){
			lCharges = lCharges.subtract(tdsValue);
		}
    	return lCharges;
	}
	
    public ChargeType getChargeType() {
    	if(chargeType==null){
    		chargeType = ChargeType.Normal;
    	}
        return chargeType;
    }

    public void setChargeType(ChargeType pChargeType) {
        chargeType = pChargeType;
    }
    
    public void add(GstSummaryBean pGstSummaryBean){
    	if(pGstSummaryBean!=null){
        	this.charge = this.charge.add(pGstSummaryBean.getCharge());
        	this.cgstValue = this.cgstValue.add(pGstSummaryBean.getCgstValue());
        	this.sgstValue = this.sgstValue.add(pGstSummaryBean.getSgstValue());
        	this.igstValue = this.igstValue.add(pGstSummaryBean.getIgstValue());
        	//
        	if(BigDecimal.ZERO.equals(this.cgst)&&!BigDecimal.ZERO.equals(pGstSummaryBean.cgst)){
        		this.cgst = pGstSummaryBean.cgst;
        	}
        	if(BigDecimal.ZERO.equals(this.sgst)&&!BigDecimal.ZERO.equals(pGstSummaryBean.sgst)){
        		this.sgst = pGstSummaryBean.sgst;
        	}
        	if(BigDecimal.ZERO.equals(this.igst)&&!BigDecimal.ZERO.equals(pGstSummaryBean.igst)){
        		this.igst = pGstSummaryBean.igst;
        	}
    	}
    }
    
    public Long getObId() {
		return obId;
	}
    
	public void setObId(Long pObId) {
		obId = pObId;
	}
	
    public BigDecimal getTds() {
        return tds;
    }

    public void setTds(BigDecimal pTds) {
    	tds = pTds;
    }
    
    public BigDecimal getTdsValue() {
        return tdsValue;
    }

    public void setTdsValue(BigDecimal pTdsValue) {
        tdsValue = pTdsValue;
    }
}