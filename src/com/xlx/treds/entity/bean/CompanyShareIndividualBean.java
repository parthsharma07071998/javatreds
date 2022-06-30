package com.xlx.treds.entity.bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;
import groovy.json.JsonBuilder;

public class CompanyShareIndividualBean {
	public static final String FIELDGROUP_UPDATECOMPANYSHAREINDIVIDUAL= "updateshareindividual";
	public static final String FIELDGROUP_INSERTCOMPANYSHAREINDIVIDUAL= "insertshareindividual";

    private Long id;
    private Long cdId;
    private String salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String telephone;
    private String mobile;
    private String fax;
    private Date DOB;
    private String designation;
    private String familySalutation;
    private String familyFirstName;
    private String familyMiddleName;
    private String familyLastName;
    private String pan;
    private String companyCode;
    private Long refId;
    private String line1;
    private String line2;
    private String line3;
    private String country;
    private String state;
    private String district;
    private String city;
    private String zipCode;
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

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date pDOB) {
        DOB = pDOB;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String pDesignation) {
        designation = pDesignation;
    }

    public String getFamilySalutation() {
        return familySalutation;
    }

    public void setFamilySalutation(String pFamilySalutation) {
        familySalutation = pFamilySalutation;
    }

    public String getFamilyFirstName() {
        return familyFirstName;
    }

    public void setFamilyFirstName(String pFamilyFirstName) {
        familyFirstName = pFamilyFirstName;
    }

    public String getFamilyMiddleName() {
        return familyMiddleName;
    }

    public void setFamilyMiddleName(String pFamilyMiddleName) {
        familyMiddleName = pFamilyMiddleName;
    }

    public String getFamilyLastName() {
        return familyLastName;
    }

    public void setFamilyLastName(String pFamilyLastName) {
        familyLastName = pFamilyLastName;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pPan) {
        pan = pPan;
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