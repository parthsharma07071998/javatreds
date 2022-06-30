package com.xlx.treds.entity.bean;

import java.sql.Timestamp;
import java.util.Map;

import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.IKeyValEnumInterface;

public class CompanyLocationBean {
	public static final String FIELDGROUP_UPDATECOMPANYLOCATION = "updateCompanyLocation";
	public static final Long REG_OFFICE_LOCATION_ID = new Long(-1);
    public enum LocationType implements IKeyValEnumInterface<String>{
    	RegOffice("R","Reg Office"),NonRegOffice("N","Non Reg Office");
        
        private final String code;
        private final String desc;
        private LocationType(String pCode, String pDesc) {
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
    private Long cdId;
    private String name;
    private String vat;
    private String gstn;
    private String gstScannedFileName;
    private String remarks;
    private String line1;
    private String line2;
    private String line3;
    private String country;
    private String state;
    private String district;
    private String city;
    private String zipCode;
    private String salutation;
    private Yes enableSettlement;
    private Long cbdId;
    private Long settlementCLId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String telephone;
    private String mobile;
    private String fax;
    private String settlementName;
    private String bankBranchName;
    private String bankNACHStatus;
    private LocationType locationType;
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

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String pVat) {
        vat = pVat;
    }

    public String getGstn() {
        return gstn;
    }

    public void setGstn(String pGstn) {
        gstn = pGstn;
    }

    public String getGstScannedFileName() {
        return gstScannedFileName;
    }

    public void setGstScannedFileName(String pGstScannedFileName) {
        gstScannedFileName = pGstScannedFileName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
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
	
    public Long getSettlementCLId() {
		return settlementCLId;
	}

	public void setSettlementCLId(Long pSettlementCLId) {
		settlementCLId = pSettlementCLId;
	}

	public Long getCbdId() {
		return cbdId;
	}

	public void setCbdId(Long pCbdId) {
		cbdId = pCbdId;
	}

	public Yes getEnableSettlement() {
		return enableSettlement;
	}

	public void setEnableSettlement(Yes pEnableSettlement) {
		enableSettlement = pEnableSettlement;
	}
	

    public String getSettlementName() {
        return settlementName;
    }

    public void setSettlementName(String pSettlementName) {
        settlementName = pSettlementName;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String pBankBranchName) {
        bankBranchName = pBankBranchName;
    }

    public String getBankNACHStatus() {
        return bankNACHStatus;
    }

    public void setBankNACHStatus(String pBankNACHStatus) {
    	bankNACHStatus = pBankNACHStatus;
    }
    
    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType pLocationType) {
        locationType = pLocationType;
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