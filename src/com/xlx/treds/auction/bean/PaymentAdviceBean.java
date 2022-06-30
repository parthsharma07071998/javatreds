
package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;

public class PaymentAdviceBean {

    private Long id;
    private Long fuId;
    private Date settlementDate;
    private String vendorName;
    private String vendorAddress;
    private String vendorCode;
    private String buyer;
    private String buyerAddress;
    private String buyerName;
    private String cinNumber;
    private String paymentReferenceNumber;
    private String customerRefNo;
    private BigDecimal instNetAmount;
    private String obligationSplitsId;
    private String cvNumber;
    private Long recordVersion;

    private CIGroupBean ciGroupBean = null;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getFuId() {
        return fuId;
    }

    public void setFuId(Long pFuId) {
        fuId = pFuId;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date pSettlementDate) {
        settlementDate = pSettlementDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String pVendorName) {
        vendorName = pVendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String pVendorAddress) {
        vendorAddress = pVendorAddress;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String pVendorCode) {
        vendorCode = pVendorCode;
    }
    
    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String pBuyer) {
        buyer = pBuyer;
    }
    
    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String pBuyerAddress) {
        buyerAddress = pBuyerAddress;
    }
    
    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String pBuyerName) {
        buyerName = pBuyerName;
    }
    
    public String getCinNumber() {
        return cinNumber;
    }

    public void setCinNumber(String pCinNumber) {
    	cinNumber = pCinNumber;
    }
    
    public String getPaymentReferenceNumber() {
        return paymentReferenceNumber;
    }

    public void setPaymentReferenceNumber(String pPaymentReferenceNumber) {
        paymentReferenceNumber = pPaymentReferenceNumber;
    }
    
    public String getCustomerRefNo() {
        return customerRefNo;
    }

    public void setCustomerRefNo(String pCustomerRefNo) {
        customerRefNo = pCustomerRefNo;
    }
    
    public BigDecimal getInstNetAmount() {
        return instNetAmount;
    }

    public void setInstNetAmount(BigDecimal pInstNetAmount) {
        instNetAmount = pInstNetAmount;
    }
    
    public String getObligationSplitsId(){
    	return obligationSplitsId;
    }
    
    public void setObligationSplitsId(String pObligationSplitsId){
    	obligationSplitsId = pObligationSplitsId;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

	@Override
	public String toString() {
		return super.toString();
	}

	public CIGroupBean getCIGroupBean() {
		return ciGroupBean;
	}

	public void setCIGroupBean(CIGroupBean pCiGroupBean) {
		ciGroupBean = pCiGroupBean;
	}

	public String getCvNumber() {
		return cvNumber;
	}

	public void setCvNumber(String pCvNumber) {
		cvNumber = pCvNumber;
	}
}