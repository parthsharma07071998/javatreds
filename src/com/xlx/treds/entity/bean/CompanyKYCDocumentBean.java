package com.xlx.treds.entity.bean;

import java.sql.Timestamp;
import java.util.Map;

public class CompanyKYCDocumentBean {
	public static final String FIELDGROUP_UPDATECOMPANYDOCUMENT = "updatedocument";

    private Long id;
    private Long cdId;
    private Long docForCCId;
    private String documentType;
    private String documentCat;
    private String document;
    private String fileName;
    private String remarks;
    private String companyCode;
    private Long refId;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private String creatorIdentity;
    private Boolean isProvisional=Boolean.FALSE;
    private Map<String,Object> modifiedData;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getCdId() {
        return cdId;
    }

    public void setCdId(Long pCdId) {
        cdId = pCdId;
    }

    public Long getDocForCCId() {
        return docForCCId;
    }

    public void setDocForCCId(Long pDocForCCId) {
        docForCCId = pDocForCCId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String pDocumentType) {
        documentType = pDocumentType;
    }

    public String getDocumentCat() {
        return documentCat;
    }

    public void setDocumentCat(String pDocumentCat) {
        documentCat = pDocumentCat;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String pDocument) {
        document = pDocument;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String pFileName) {
        fileName = pFileName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String pCompanyCode) {
        companyCode = pCompanyCode;
    }
    public Long getRefId() {
        return refId;
    }
    public void setRefId(Long pRefId) {
        refId = pRefId;
    }

    public Long getRecordCreator() {
        return recordCreator;
    }

    public void setRecordCreator(Long pRecordCreator) {
        recordCreator = pRecordCreator;
    }

    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

    public Long getRecordUpdator() {
        return recordUpdator;
    }

    public void setRecordUpdator(Long pRecordUpdator) {
        recordUpdator = pRecordUpdator;
    }

    public Timestamp getRecordUpdateTime() {
        return recordUpdateTime;
    }

    public void setRecordUpdateTime(Timestamp pRecordUpdateTime) {
        recordUpdateTime = pRecordUpdateTime;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }
    
    public String getCreatorIdentity() {
        return creatorIdentity;
    }

    public void setCreatorIdentity(String pCreatorIdentity) {
        creatorIdentity = pCreatorIdentity;
    }
    
    public void setIsProvisional(Boolean pIsProvisional) {
    	isProvisional = pIsProvisional;
    }
    public Boolean getIsProvisional() {
    	if(isProvisional == null) {
    		return Boolean.FALSE;
    	}
    	return isProvisional;
    }
    
	public Map<String,Object> getModifiedData() {
		return modifiedData;
	}

	public void setModifiedData(Map<String,Object> modifiedData) {
		this.modifiedData = modifiedData;
	}
}