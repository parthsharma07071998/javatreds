package com.xlx.treds.entity.bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

public class CompanyShareEntityBean {
	public static final String FIELDGROUP_UPDATECOMPANYSHAREENTITY = "updateCompanyShareEntity";

    private Long id;
    private Long cdId;
    private String companyName;
    private String benificiaryOwner;
    private String constitution;
    private String companyDesc;
    private String line1;
    private String line2;
    private String line3;
    private String country;
    private String state;
    private String district;
    private String city;
    private String zipCode;
    private String salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String designation;
    private String email;
    private String telephone;
    private String mobile;
    private String fax;
    private String regNo;
    private Date dateOfIncorporation;
    private String industry;
    private String subSegment;
    private String pan;
    private String kmpPan;
    private String companyCode;
    private Long refId;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private String loginId;
    private String creatorLoginId;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String pCompanyName) {
        companyName = pCompanyName;
    }

    public String getBenificiaryOwner() {
        return benificiaryOwner;
    }

    public void setBenificiaryOwner(String pBenificiaryOwner) {
        benificiaryOwner = pBenificiaryOwner;
    }

    public String getConstitution() {
        return constitution;
    }

    public void setConstitution(String pConstitution) {
        constitution = pConstitution;
    }

    public String getCompanyDesc() {
        return companyDesc;
    }

    public void setCompanyDesc(String pCompanyDesc) {
        companyDesc = pCompanyDesc;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String pLine1) {
        line1 = pLine1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String pLine2) {
        line2 = pLine2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String pLine3) {
        line3 = pLine3;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String pCountry) {
        country = pCountry;
    }

    public String getState() {
        return state;
    }

    public void setState(String pState) {
        state = pState;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String pDistrict) {
        district = pDistrict;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String pCity) {
        city = pCity;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String pZipCode) {
        zipCode = pZipCode;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String pSalutation) {
        salutation = pSalutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String pFirstName) {
        firstName = pFirstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String pMiddleName) {
        middleName = pMiddleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String pLastName) {
        lastName = pLastName;
    }
    
    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String pDesignation) {
        designation = pDesignation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String pEmail) {
        email = pEmail;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String pTelephone) {
        telephone = pTelephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String pMobile) {
        mobile = pMobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String pFax) {
        fax = pFax;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String pRegNo) {
        regNo = pRegNo;
    }

    public Date getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(Date pDateOfIncorporation) {
        dateOfIncorporation = pDateOfIncorporation;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String pIndustry) {
        industry = pIndustry;
    }

    public String getSubSegment() {
        return subSegment;
    }

    public void setSubSegment(String pSubSegment) {
        subSegment = pSubSegment;
    }
    
    public String getPan() {
        return pan;
    }

    public void setPan(String pPan) {
        pan = pPan;
    }
    
    public String getKmpPan() {
        return kmpPan;
    }

    public void setKmpPan(String pKmpPan) {
        kmpPan = pKmpPan;
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

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String pLoginId) {
        loginId = pLoginId;
    }

    public String getCreatorLoginId() {
        return creatorLoginId;
    }

    public void setCreatorLoginId(String pCreatorLoginId) {
        creatorLoginId = pCreatorLoginId;
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