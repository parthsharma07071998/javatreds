
package com.xlx.treds.monetago.bean;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class MonetagoEwaybillItemListBean {

    private Long itemNo;
    private Long productId;
    private String productName;
    private String productDesc;
    private Long hsnCode;
    private BigDecimal quantity;
    private String qtyUnit;
    private BigDecimal cgstRate;
    private BigDecimal sgstRate;
    private BigDecimal igstRate;
    private BigDecimal cessRate;
    private BigDecimal cessNonAdvol;
    private BigDecimal taxableAmount;

    public Long getItemNo() {
        return itemNo;
    }

    public void setItemNo(Long pItemNo) {
        itemNo = pItemNo;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long pProductId) {
        productId = pProductId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String pProductName) {
        productName = pProductName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String pProductDesc) {
        productDesc = pProductDesc;
    }

    public Long getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(Long pHsnCode) {
        hsnCode = pHsnCode;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal pQuantity) {
        quantity = pQuantity;
    }

    public String getQtyUnit() {
        return qtyUnit;
    }

    public void setQtyUnit(String pQtyUnit) {
        qtyUnit = pQtyUnit;
    }

    public void setGstRate(BigDecimal pGstRate) {
    }

    public BigDecimal getCgstRate() {
        return cgstRate;
    }

    public void setCgstRate(BigDecimal pCgstRate) {
        cgstRate = pCgstRate;
    }

    public BigDecimal getSgstRate() {
        return sgstRate;
    }

    public void setSgstRate(BigDecimal pSgstRate) {
        sgstRate = pSgstRate;
    }

    public BigDecimal getIgstRate() {
        return igstRate;
    }

    public void setIgstRate(BigDecimal pIgstRate) {
        igstRate = pIgstRate;
    }

    public BigDecimal getCessRate() {
        return cessRate;
    }

    public void setCessRate(BigDecimal pCessRate) {
        cessRate = pCessRate;
    }

    public BigDecimal getCessNonAdvol() {
        return cessNonAdvol;
    }

    public void setCessNonAdvol(BigDecimal pCessNonAdvol) {
        cessNonAdvol = pCessNonAdvol;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(BigDecimal pTaxableAmount) {
        taxableAmount = pTaxableAmount;
    }

	@Override
	public String toString() {
		return super.toString();
	}
	
	public String getTotalGstRate () {
		String lGstRate = "";
	    if (igstRate != null && igstRate.compareTo(BigDecimal.ZERO) > 0) {
    		lGstRate = igstRate.toString();
	    }
	    if (cgstRate != null && cgstRate.compareTo(BigDecimal.ZERO) > 0) {
    		if (StringUtils.isNotEmpty(lGstRate)){
    			lGstRate += "/";
    		}
    		lGstRate += cgstRate.toString();
	    }
	    if (sgstRate != null && sgstRate.compareTo(BigDecimal.ZERO) > 0) {
    		if (StringUtils.isNotEmpty(lGstRate)){
    			lGstRate += "/";
    		}
    		lGstRate += sgstRate.toString();
	    }
	    return lGstRate;
	}
}