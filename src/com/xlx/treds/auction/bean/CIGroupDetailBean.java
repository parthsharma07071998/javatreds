
package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;

public class CIGroupDetailBean {

    private Long id;
    private String invoiceNumber;
    private Date invoiceDate;
    private BigDecimal amount;
    private String documentNumber;
    private String deductionReason;
    private BigDecimal dedudctedAmount;
    private String other;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String pInvoiceNumber) {
        invoiceNumber = pInvoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date pInvoiceDate) {
        invoiceDate = pInvoiceDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String pDocumentNumber) {
        documentNumber = pDocumentNumber;
    }

    public String getDeductionReason() {
        return deductionReason;
    }

    public void setDeductionReason(String pDeductionReason) {
        deductionReason = pDeductionReason;
    }

    public BigDecimal getDedudctedAmount() {
        return dedudctedAmount;
    }

    public void setDedudctedAmount(BigDecimal pDedudctedAmount) {
        dedudctedAmount = pDedudctedAmount;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String pOther) {
        other = pOther;
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
}