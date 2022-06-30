package com.xlx.treds.entity.bean;

import java.sql.Timestamp;
import java.util.Map;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.IKeyValEnumInterface;
import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class CompanyBankDetailBean {
    public enum AccType implements IKeyValEnumInterface<String>{
        Term_Loan("TL","Term Loan"),Cash_Credit("CC","Cash Credit"),Overdraft("OD","Overdraft"),Current_Account("CA","Current Account"),Others("OT","Others");
        
        private final String code;
        private final String desc;
        private AccType(String pCode, String pDesc) {
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
    public enum BankingType implements IKeyValEnumInterface<String>{
        Sole("S","Sole"),Multiple("M","Multiple"),Consortium("C","Consortium");
        
        private final String code;
        private final String desc;
        private BankingType(String pCode, String pDesc) {
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
    private String bank;
    private String line1;
    private String line2;
    private String line3;
    private String country;
    private String state;
    private String district;
    private String city;
    private String zipCode;
    private AccType accType;
    private String accNo;
    private String ifsc;
    private String salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String telephone;
    private String mobile;
    private String fax;
    private Yes defaultAccount;
    private Yes leadBank;
    private BankingType bankingType;
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

    public String getBank() {
        return bank;
    }

    public void setBank(String pBank) {
        bank = pBank;
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

    public AccType getAccType() {
        return accType;
    }

    public void setAccType(AccType pAccType) {
        accType = pAccType;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String pAccNo) {
        accNo = pAccNo;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String pIfsc) {
        ifsc = pIfsc;
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

    public Yes getDefaultAccount() {
        return defaultAccount;
    }

    public void setDefaultAccount(Yes pDefaultAccount) {
        defaultAccount = pDefaultAccount;
    }

    public Yes getLeadBank() {
        return leadBank;
    }

    public void setLeadBank(Yes pLeadBank) {
        leadBank = pLeadBank;
    }

    public BankingType getBankingType() {
        return bankingType;
    }

    public void setBankingType(BankingType pBankingType) {
        bankingType = pBankingType;
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
	public String getModifiedDataStr() {
		if(modifiedData!=null) {
			return new JsonBuilder(modifiedData).toString();
		}
		return null;
	}
	public void setModifiedDataStr(String pModifiedDataStr) {
	}
}