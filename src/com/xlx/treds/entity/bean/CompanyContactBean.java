package com.xlx.treds.entity.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.TredsHelper;

public class CompanyContactBean {
	
	
	public static final String FIELDGROUP_INSERTCOMPANYCONTACT= "insertCompanyContact"; 
//	public static final String FIELDGROUP_UPDATECOMPANYCONTACT= "updateCompanyContact"; 
	
	public enum Gender implements IKeyValEnumInterface<String>{
        Male("M","Male"),Female("F","Female");
        
        private final String code;
        private final String desc;
        private Gender(String pCode, String pDesc) {
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
	
	 public enum Status implements IKeyValEnumInterface<String>{
        Karta("K","Karta"),Trustee("T","Trustee"),NA("N","NA");
        
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
	 
	 public enum Nationality implements IKeyValEnumInterface<String>{
	        Indian("I","Indian"),Others("O","Others");
	    
	    private final String code;
	    private final String desc;
	    private Nationality(String pCode, String pDesc) {
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
	    
	 
 	public enum ResidentailStatus implements IKeyValEnumInterface<String>{
        Residential_Indian("RI","Residential Indian"),Non_Residential_Indian("NRI","Non Residential Indian"),Foreign_National("FN","Foreign National");
        
        private final String code;
        private final String desc;
        private ResidentailStatus(String pCode, String pDesc) {
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
	    
 	
 	public enum Occupation implements IKeyValEnumInterface<String>{
        Private_Sector_service("PRI","Private Sector service"),Public_sector_Service("PUB","Public sector Service"),Govt_service("GOV","Govt service"),Retired_Professional("RTD","Retired Professional"),Agriculturist("AGR","Agriculturist"),Housewife("HWF","Housewife"),Students("STU","Students"),Business_or_Entrepreneur("BUS","Business or Entrepreneur"),Others("OTR","Others");
        
        private final String code;
        private final String desc;
        private Occupation(String pCode, String pDesc) {
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
 	
 	public enum GrossIncome implements IKeyValEnumInterface<String>{
        A_Upto_5Lakhs("A","Upto 5Lakhs"),B_5_to_25_lakhs_("B","5 to 25 lakhs "),C_25lakhs_to_1crore_("C","25lakhs to 1crore "),D_1_to_5crore_("D","1 to 5crore "),E_5crore_and_above_("E","5crore and above ");
        
        private final String code;
        private final String desc;
        private GrossIncome(String pCode, String pDesc) {
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
    private Yes promoter;
    private Yes chiefPromoter;
    private String cpCat;
    private Yes cpWomenEnt;
    private Yes authPer;
    private Date authPerAuthDate;
    private Yes ultimateBeneficiary;
    private Date ultimateBeneficiaryDate;
    private Yes admin;
    private Date adminAuthDate;
    private Yes cersaiFlag;
    private String cersaiSalutation;
    private String cersaiFirstName;
    private String cersaiMiddleName;
    private String cersaiLastName;
    private Date cersaiDOB;
    private String noaEmail;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private Yes force;
    //
    private Gender gender;
    private String pan;
    private String uidId;
    private Status status;
    private String dinNo;
    private Nationality nationality;
    private String othersNationality;
    private ResidentailStatus residentailStatus;
    private String resLine1;
    private String resLine2;
    private String resLine3;
    private String resCountry;
    private String resState;
    private String resDistrict;
    private String resCity;
    private String resZipCode;
    private String nriLine1;
    private String nriLine2;
    private String nriLine3;
    private String nriCountry;
    private String nriState;
    private String nriDistrict;
    private String nriCity;
    private String nriZipCode;
    private Occupation occupation;
    private String othersOccupation;
    private GrossIncome grossIncome;
    private BigDecimal networth;
    private Date date;
    private String companyCode;
    private Long refId;
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

    public Yes getPromoter() {
        return promoter;
    }

    public void setPromoter(Yes pPromoter) {
        promoter = pPromoter;
    }

    public Yes getChiefPromoter() {
        return chiefPromoter;
    }

    public void setChiefPromoter(Yes pChiefPromoter) {
        chiefPromoter = pChiefPromoter;
    }

    public String getCpCat() {
        return cpCat;
    }

    public void setCpCat(String pCpCat) {
        cpCat = pCpCat;
    }

    public Yes getCpWomenEnt() {
        return cpWomenEnt;
    }

    public void setCpWomenEnt(Yes pCpWomenEnt) {
        cpWomenEnt = pCpWomenEnt;
    }

    public Yes getAuthPer() {
        return authPer;
    }

    public void setAuthPer(Yes pAuthPer) {
        authPer = pAuthPer;
    }

    public Date getAuthPerAuthDate() {
        return authPerAuthDate;
    }

    public void setAuthPerAuthDate(Date pAuthPerAuthDate) {
        authPerAuthDate = pAuthPerAuthDate;
    }
    public Yes getUltimateBeneficiary() {
    	if(CommonAppConstants.Yes.Yes.equals(ultimateBeneficiary)){
    		email = " ";
    		telephone = " ";
    		mobile = " ";
    		DOB = TredsHelper.getInstance().getBusinessDate();
    		cersaiFirstName = " ";
    		cersaiLastName = " ";
    		cersaiSalutation = " ";
    		
    	}
        return ultimateBeneficiary;
    }
    public void setUltimateBeneficiary(Yes pUltimateBeneficiary) {
        ultimateBeneficiary = pUltimateBeneficiary;
    }
    public Date getUltimateBeneficiaryDate() {
        return ultimateBeneficiaryDate;
    }
    public void setUltimateBeneficiaryDate(Date pUltimateBeneficiaryDate) {
        ultimateBeneficiaryDate = pUltimateBeneficiaryDate;
    }

    public Yes getAdmin() {
        return admin;
    }

    public void setAdmin(Yes pAdmin) {
        admin = pAdmin;
    }

    public Date getAdminAuthDate() {
        return adminAuthDate;
    }

    public void setAdminAuthDate(Date pAdminAuthDate) {
        adminAuthDate = pAdminAuthDate;
    }

    public Yes getCersaiFlag() {
        return cersaiFlag;
    }

    public void setCersaiFlag(Yes pCersaiFlag) {
        cersaiFlag = pCersaiFlag;
    }

    public String getCersaiSalutation() {
        return cersaiSalutation;
    }

    public void setCersaiSalutation(String pCersaiSalutation) {
        cersaiSalutation = pCersaiSalutation;
    }

    public String getCersaiFirstName() {
        return cersaiFirstName;
    }

    public void setCersaiFirstName(String pCersaiFirstName) {
        cersaiFirstName = pCersaiFirstName;
    }

    public String getCersaiMiddleName() {
        return cersaiMiddleName;
    }

    public void setCersaiMiddleName(String pCersaiMiddleName) {
        cersaiMiddleName = pCersaiMiddleName;
    }

    public String getCersaiLastName() {
        return cersaiLastName;
    }

    public void setCersaiLastName(String pCersaiLastName) {
        cersaiLastName = pCersaiLastName;
    }

    public Date getCersaiDOB() {
        return cersaiDOB;
    }

    public void setCersaiDOB(Date pCersaiDOB) {
        cersaiDOB = pCersaiDOB;
    }

    public String getNoaEmail() {
        return noaEmail;
    }

    public void setNoaEmail(String pNoaEmail) {
        noaEmail = pNoaEmail;
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

    public Yes getForce() {
		return force;
	}

	public void setForce(Yes pForce) {
		force = pForce;
	}
	
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender pGender) {
        gender = pGender;
    }
    
    public void setPan(String pPan) {
        pan = pPan;
    }

    public String getPan() {
        return pan;
    }
    
    public void setUidId(String pUidId) {
        uidId = pUidId;
    }

    public String getUidId() {
        return uidId;
    }
    
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }
    public String getDinNo() {
        return dinNo;
    }

    public void setDinNo(String pDinNo) {
        dinNo = pDinNo;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality pNationality) {
        nationality = pNationality;
    }

    public String getOthersNationality() {
        return othersNationality;
    }

    public void setOthersNationality(String pOthers) {
    	othersNationality = pOthers;
    }

    public ResidentailStatus getResidentailStatus() {
        return residentailStatus;
    }

    public void setResidentailStatus(ResidentailStatus pResidentailStatus) {
        residentailStatus = pResidentailStatus;
    }
    

    public String getResLine1() {
        return resLine1;
    }

    public void setResLine1(String pResLine1) {
        resLine1 = pResLine1;
    }

    public String getResLine2() {
        return resLine2;
    }

    public void setResLine2(String pResLine2) {
        resLine2 = pResLine2;
    }

    public String getResLine3() {
        return resLine3;
    }

    public void setResLine3(String pResLine3) {
        resLine3 = pResLine3;
    }

    public String getResCountry() {
        return resCountry;
    }

    public void setResCountry(String pResCountry) {
        resCountry = pResCountry;
    }

    public String getResState() {
        return resState;
    }

    public void setResState(String pResState) {
        resState = pResState;
    }

    public String getResDistrict() {
        return resDistrict;
    }

    public void setResDistrict(String pResDistrict) {
        resDistrict = pResDistrict;
    }

    public String getResCity() {
        return resCity;
    }

    public void setResCity(String pResCity) {
        resCity = pResCity;
    }

    public String getResZipCode() {
        return resZipCode;
    }

    public void setResZipCode(String pResZipCode) {
        resZipCode = pResZipCode;
    }

    public String getNriLine1() {
        return nriLine1;
    }

    public void setNriLine1(String pNriLine1) {
        nriLine1 = pNriLine1;
    }

    public String getNriLine2() {
        return nriLine2;
    }

    public void setNriLine2(String pNriLine2) {
        nriLine2 = pNriLine2;
    }

    public String getNriLine3() {
        return nriLine3;
    }

    public void setNriLine3(String pNriLine3) {
        nriLine3 = pNriLine3;
    }

    public String getNriCountry() {
        return nriCountry;
    }

    public void setNriCountry(String pNriCountry) {
        nriCountry = pNriCountry;
    }

    public String getNriState() {
        return nriState;
    }

    public void setNriState(String pNriState) {
        nriState = pNriState;
    }

    public String getNriDistrict() {
        return nriDistrict;
    }

    public void setNriDistrict(String pNriDistrict) {
        nriDistrict = pNriDistrict;
    }

    public String getNriCity() {
        return nriCity;
    }

    public void setNriCity(String pNriCity) {
        nriCity = pNriCity;
    }

    public String getNriZipCode() {
        return nriZipCode;
    }

    public void setNriZipCode(String pNriZipCode) {
        nriZipCode = pNriZipCode;
    }
    
    public Occupation getOccupation() {
        return occupation;
    }

    public void setOccupation(Occupation pOccupation) {
        occupation = pOccupation;
    }

    public String getOthersOccupation() {
        return othersOccupation;
    }

    public void setOthersOccupation(String pOthersOccupation) {
        othersOccupation = pOthersOccupation;
    }
    
    public GrossIncome getGrossIncome() {
        return grossIncome;
    }

    public void setGrossIncome(GrossIncome pGrossIncome) {
        grossIncome = pGrossIncome;
    }

    public BigDecimal getNetworth() {
        return networth;
    }

    public void setNetworth(BigDecimal pNetworth) {
        networth = pNetworth;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
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
    
    public String getCreatorIdentity() {
        return creatorIdentity;
    }

    public void setCreatorIdentity(String pCreatorIdentity) {
        creatorIdentity = pCreatorIdentity;
    }
    
    public String getFullName() {
		String lName = "";
		if(StringUtils.isNotEmpty(firstName)){
			lName += firstName;
		}
		if(StringUtils.isNotEmpty(middleName)){
			if(StringUtils.isNotEmpty(lName)){
				lName += " ";
			}
			lName += middleName;
		}
		if(StringUtils.isNotEmpty(lastName)){
			if(StringUtils.isNotEmpty(lName)){
				lName += " ";
			}
			lName += lastName;
		}
    	return lName;
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