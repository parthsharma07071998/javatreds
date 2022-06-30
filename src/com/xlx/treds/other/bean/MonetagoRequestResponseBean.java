
package com.xlx.treds.other.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;

public class MonetagoRequestResponseBean {
    public enum Type implements IKeyValEnumInterface<String>{
        Register("R","Register"),Cancel("C","Cancel"),Factor("F","Factor"),RegisterBatch("RB","RegisterBatch"),CancelBatch("CB","CancelBatch"),FactorBatch("FB","FactorBatch");
        
        private final String code;
        private final String desc;
        private Type(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }
    public enum ApiResponseStatus implements IKeyValEnumInterface<String>{
        Success("S","Success"),Failed("F","Failed");
        
        private final String code;
        private final String desc;
        private ApiResponseStatus(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }

    private Long id;
    private Long inId;
    private Long groupInId;
    private Type type;
    private String sellerGSTN;
    private BigDecimal amount;
    private String buyerGSTN;
    private Timestamp exchRecvDateTime;
    private Date invoiceDate;
    private String invoiceID;
    private Long duplicateFlag;
    private String inputTxnId;
    private Long reasonCode;
    private Timestamp requestDateTime;
    private Long override;
    private String outputTxnId;
    private ApiResponseStatus apiResponseStatus;
    private Timestamp responseDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getInId() {
        return inId;
    }

    public void setInId(Long pInId) {
        inId = pInId;
    }

    public Long getGroupInId() {
        return groupInId;
    }

    public void setGroupInId(Long pGroupInId) {
        groupInId = pGroupInId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }

    public String getSellerGSTN() {
        return sellerGSTN;
    }

    public void setSellerGSTN(String pSellerGSTN) {
        sellerGSTN = pSellerGSTN;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
    }

    public String getBuyerGSTN() {
        return buyerGSTN;
    }

    public void setBuyerGSTN(String pBuyerGSTN) {
        buyerGSTN = pBuyerGSTN;
    }

    public Timestamp getExchRecvDateTime() {
        return exchRecvDateTime;
    }

    public void setExchRecvDateTime(Timestamp pExchRecvDateTime) {
        exchRecvDateTime = pExchRecvDateTime;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date pInvoiceDate) {
        invoiceDate = pInvoiceDate;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String pInvoiceID) {
        invoiceID = pInvoiceID;
    }

    public Long getDuplicateFlag() {
        return duplicateFlag;
    }

    public void setDuplicateFlag(Long pDuplicateFlag) {
        duplicateFlag = pDuplicateFlag;
    }

    public String getInputTxnId() {
        return inputTxnId;
    }

    public void setInputTxnId(String pInputTxnId) {
        inputTxnId = pInputTxnId;
    }

    public Long getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Long pReasonCode) {
        reasonCode = pReasonCode;
    }

    public Timestamp getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(Timestamp pRequestDateTime) {
        requestDateTime = pRequestDateTime;
    }

    public Long getOverride() {
        return override;
    }

    public void setOverride(Long pOverride) {
        override = pOverride;
    }

    public String getOutputTxnId() {
        return outputTxnId;
    }

    public void setOutputTxnId(String pOutputTxnId) {
        outputTxnId = pOutputTxnId;
    }

    public ApiResponseStatus getApiResponseStatus() {
        return apiResponseStatus;
    }

    public void setApiResponseStatus(ApiResponseStatus pApiResponseStatus) {
        apiResponseStatus = pApiResponseStatus;
    }

    public Timestamp getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(Timestamp pResponseDateTime) {
        responseDateTime = pResponseDateTime;
    }

	@Override
	public String toString() {
		return super.toString();
	}
}