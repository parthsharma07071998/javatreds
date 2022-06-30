package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;

public class PaymentFileBean {
    public static final String FIELDGROUP_RETURN = "return";
    public enum Status implements IKeyValEnumInterface<String>{
        Generated("G","Generated"),Interim_File_Uploaded("I","Interim File Uploaded"),Interim_File_Processed("P","Interim File Processed"),Return_File_Uploaded("U","Return File Uploaded"),Return_File_Processed("R","Return File Processed");
        
        private final String code;
        private final String desc;
        private Status(String pCode, String pDesc) {
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
    public enum PayfileType implements IKeyValEnumInterface<String>{
        Interim_Debit_File("D","Interim Debit File"),Full_File("F","Full File"),Interim_Credit_File("C","Interim Credit File");
        
        private final String code;
        private final String desc;
        private PayfileType(String pCode, String pDesc) {
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
    private Date date;
    private TxnType fileType;
    private String facilitator;
    private String fileName;
    private Long recordCount;
    private BigDecimal totalValue;
    private Long generatedByAuId;
    private Timestamp generatedTime;
    private Status status;
    private Long returnUploadedByAuId;
    private Timestamp returnUploadedTime;
    private Long recordVersion;
    private PayfileType payfileType;
    private Yes skipL1FileGeneration;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public TxnType getFileType() {
        return fileType;
    }

    public void setFileType(TxnType pFileType) {
        fileType = pFileType;
    }

    public String getFacilitator() {
        return facilitator;
    }

    public void setFacilitator(String pFacilitator) {
        facilitator = pFacilitator;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String pFileName) {
        fileName = pFileName;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long pRecordCount) {
        recordCount = pRecordCount;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal pTotalValue) {
        totalValue = pTotalValue;
    }

    public Long getGeneratedByAuId() {
        return generatedByAuId;
    }

    public void setGeneratedByAuId(Long pGeneratedByAuId) {
        generatedByAuId = pGeneratedByAuId;
    }

    public Timestamp getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Timestamp pGeneratedTime) {
        generatedTime = pGeneratedTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public Long getReturnUploadedByAuId() {
        return returnUploadedByAuId;
    }

    public void setReturnUploadedByAuId(Long pReturnUploadedByAuId) {
        returnUploadedByAuId = pReturnUploadedByAuId;
    }

    public Timestamp getReturnUploadedTime() {
        return returnUploadedTime;
    }

    public void setReturnUploadedTime(Timestamp pReturnUploadedTime) {
        returnUploadedTime = pReturnUploadedTime;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

    private String contents;

    public String getContents() {
        return contents;
    }

    public void setContents(String pContents) {
        contents = pContents;
    }
    
    public PayfileType getPayfileType() {
        return payfileType;
    }

    public void setPayfileType(PayfileType pPayfileType) {
        payfileType = pPayfileType;
    }
 
    public Yes getSkipL1FileGeneration() {
    	return skipL1FileGeneration;
    }
    public void setSkipL1FileGeneration(Yes pSkipL1FileGeneration) {
    	skipL1FileGeneration = pSkipL1FileGeneration;
    }
}