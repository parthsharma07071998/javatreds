package com.xlx.treds.entity.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants.CompanyApprovalStatus;
import com.xlx.treds.user.bean.AppUserBean;

public class CompanyDetailBean {
    public static final String FIELDGROUP_FINANCIER = "financier";
    public static final String FIELDGROUP_PURCHASER = "purchaser";
    public static final String FIELDGROUP_SUPPLIER = "supplier";
    public static final String FIELDGROUP_PURCHASER_MANUFACTURER = "purchaserManuf";
    public static final String FIELDGROUP_SUPPLIER_MANUFACTURER = "supplierManuf";
    public static final String FIELDGROUP_PURCHASER_SERVICE = "purchaserServ";
    public static final String FIELDGROUP_SUPPLIER_SERVICE = "supplierServ";
    public static final String FIELDGROUP_APPROVALSTATUS = "approvalStatus";
    public static final String FIELDGROUP_INSERTONBOARDING = "insertOnBoarding";
    public static final String FIELDGROUP_OUTGOINGREQUESTONBOARDING = "outgoingRequestOnBoarding";
    public static final String FIELDGROUP_INCOMINGREQUESTONBOARDING = "incomingRequestOnBoarding";
    public static final String FIELDGROUP_UPDATECOMPANY = "updateCompany";
    public static final String FIELDGROUP_UPDATECOMPANYDB = "updateCompanyDB";
    public static final String FIELDGROUP_SETTING = "setting";
    public enum CompanyFlag implements IKeyValEnumInterface<String>{
        Buyer("B","Buyer"),Seller("S","Seller"),Financier("F","Financier");
        
        private final String code;
        private final String desc;
        private CompanyFlag(String pCode, String pDesc) {
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
    private String code;
    private String companyName;
    private YesNo supplierFlag;
    private YesNo purchaserFlag;
    private YesNo financierFlag;
    private String constitution;
    private String financierCategory;
    private String companyDesc;
    private String cinNo;
    private String regWebsite;
    private String corLine1;
    private String corLine2;
    private String corLine3;
    private String corCountry;
    private String corState;
    private String corDistrict;
    private String corCity;
    private String corZipCode;
    private String corSalutation;
    private String corFirstName;
    private String corMiddleName;
    private String corLastName;
    private String corEmail;
    private String corTelephone;
    private String corMobile;
    private String corFax;
    private String finCertificateNo;
    private Date finCertificateIssueDate;
    private Date dateOfIncorporation;
    private String existenceYears;
    private BigDecimal annualMsmePurchase;
    private String msmeStatus;
    private String msmeRegType;
    private String msmeRegNo;
    private Date msmeRegDate;
    private String caName;
    private String caMemNo;
    private Date invtDateCPM;
    private BigDecimal invtCPM;
    private Date caCertDate;
    private String customer1;
    private String customer2;
    private String customer3;
    private String customer4;
    private String customer5;
    private String customer1City;
    private String customer2City;
    private String customer3City;
    private String customer4City;
    private String customer5City;
    private String turnOver1;
    private String turnOver2;
    private String turnOver3;
    private String turnOver4;
    private String turnOver5;
    private String yearsInRelation1;
    private String yearsInRelation2;
    private String yearsInRelation3;
    private String yearsInRelation4;
    private String yearsInRelation5;
    private String industry;
    private String subSegment;
    private String sector;
    private String exportOrientation;
    private String currency;
    private String pan;
    private String vat;
    private String cst;
    private String lbt;
    private String stRegNo;
    private Yes stExempted;
    private String exciseRegNo;
    private String tan;
    private BigDecimal invtPnM;
    private BigDecimal salesTo;
    private String salesYear;
    private CompanyApprovalStatus approvalStatus;
    private String registrationNo;
    private Long refId;
    private BigDecimal cashDiscountPercent;
    private Yes enableLocationwiseSettlement;
    private CompanyFlag companyFlag;
    private String udin;
    private String creatorIdentity;
    private String documentsUrl;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private String loginId;
    private String creatorLoginId;
    private List<CompanyContactBean> contacts;
    private List<CompanyLocationBean> locations;
    private List<CompanyBankDetailBean> bankDetails;
    private List<CompanyKYCDocumentBean> documents;
    private List<CompanyShareIndividualBean> shareIndividuals;
    private List<CompanyShareEntityBean> shareEntities;
    private Long tab;
    private Boolean isProvisional = Boolean.FALSE;
    private Map<String,Object> modifiedData;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String pCompanyName) {
        companyName = pCompanyName;
    }

    public String getType() {
        if ((supplierFlag==null) || (purchaserFlag==null) || (financierFlag==null)) {
            return null;
        } else {
            StringBuilder lType = new StringBuilder();
            lType.append(supplierFlag.getCode());
            lType.append(purchaserFlag.getCode());
            lType.append(financierFlag.getCode());
            return lType.toString();
        }
    }

    public void setType(String pType) {
        if ((pType != null) && (pType.length() == 3)) {
            supplierFlag = pType.substring(0, 1).equals(CommonAppConstants.YesNo.Yes.getCode())?CommonAppConstants.YesNo.Yes:CommonAppConstants.YesNo.No;
            purchaserFlag = pType.substring(1, 2).equals(CommonAppConstants.YesNo.Yes.getCode())?CommonAppConstants.YesNo.Yes:CommonAppConstants.YesNo.No;
            financierFlag = pType.substring(2, 3).equals(CommonAppConstants.YesNo.Yes.getCode())?CommonAppConstants.YesNo.Yes:CommonAppConstants.YesNo.No;
        } else {
            supplierFlag = null;
            purchaserFlag = null;
            financierFlag = null;
        }
    }

    public YesNo getSupplierFlag() {
        return supplierFlag;
    }

    public void setSupplierFlag(YesNo pSupplierFlag) {
        supplierFlag = pSupplierFlag;
    }

    public YesNo getPurchaserFlag() {
        return purchaserFlag;
    }

    public void setPurchaserFlag(YesNo pPurchaserFlag) {
        purchaserFlag = pPurchaserFlag;
    }

    public YesNo getFinancierFlag() {
        return financierFlag;
    }

    public void setFinancierFlag(YesNo pFinancierFlag) {
        financierFlag = pFinancierFlag;
    }

    public String getConstitution() {
        return constitution;
    }

    public void setConstitution(String pConstitution) {
        constitution = pConstitution;
    }

    public String getFinancierCategory() {
        return financierCategory;
    }

    public void setFinancierCategory(String pFinancierCategory) {
        financierCategory = pFinancierCategory;
    }

    public String getCompanyDesc() {
        return companyDesc;
    }

    public void setCompanyDesc(String pCompanyDesc) {
        companyDesc = pCompanyDesc;
    }

    public String getCinNo() {
        return cinNo;
    }

    public void setCinNo(String pCinNo) {
        cinNo = pCinNo;
    }

    public String getRegWebsite() {
        return regWebsite;
    }

    public void setRegWebsite(String pRegWebsite) {
        regWebsite = pRegWebsite;
    }

    public String getCorLine1() {
        return corLine1;
    }

    public void setCorLine1(String pCorLine1) {
        corLine1 = pCorLine1;
    }

    public String getCorLine2() {
        return corLine2;
    }

    public void setCorLine2(String pCorLine2) {
        corLine2 = pCorLine2;
    }

    public String getCorLine3() {
        return corLine3;
    }

    public void setCorLine3(String pCorLine3) {
        corLine3 = pCorLine3;
    }

    public String getCorCountry() {
        return corCountry;
    }

    public void setCorCountry(String pCorCountry) {
        corCountry = pCorCountry;
    }

    public String getCorState() {
        return corState;
    }

    public void setCorState(String pCorState) {
        corState = pCorState;
    }

    public String getCorDistrict() {
        return corDistrict;
    }

    public void setCorDistrict(String pCorDistrict) {
        corDistrict = pCorDistrict;
    }

    public String getCorCity() {
        return corCity;
    }

    public void setCorCity(String pCorCity) {
        corCity = pCorCity;
    }

    public String getCorZipCode() {
        return corZipCode;
    }

    public void setCorZipCode(String pCorZipCode) {
        corZipCode = pCorZipCode;
    }

    public String getCorSalutation() {
        return corSalutation;
    }

    public void setCorSalutation(String pCorSalutation) {
        corSalutation = pCorSalutation;
    }

    public String getCorFirstName() {
        return corFirstName;
    }

    public void setCorFirstName(String pCorFirstName) {
        corFirstName = pCorFirstName;
    }

    public String getCorMiddleName() {
        return corMiddleName;
    }

    public void setCorMiddleName(String pCorMiddleName) {
        corMiddleName = pCorMiddleName;
    }

    public String getCorLastName() {
        return corLastName;
    }

    public void setCorLastName(String pCorLastName) {
        corLastName = pCorLastName;
    }

    public String getCorEmail() {
        return corEmail;
    }

    public void setCorEmail(String pCorEmail) {
        corEmail = pCorEmail;
    }

    public String getCorTelephone() {
        return corTelephone;
    }

    public void setCorTelephone(String pCorTelephone) {
        corTelephone = pCorTelephone;
    }

    public String getCorMobile() {
        return corMobile;
    }

    public void setCorMobile(String pCorMobile) {
        corMobile = pCorMobile;
    }

    public String getCorFax() {
        return corFax;
    }

    public void setCorFax(String pCorFax) {
        corFax = pCorFax;
    }

    public String getFinCertificateNo() {
        return finCertificateNo;
    }

    public void setFinCertificateNo(String pFinCertificateNo) {
        finCertificateNo = pFinCertificateNo;
    }

    public Date getFinCertificateIssueDate() {
        return finCertificateIssueDate;
    }

    public void setFinCertificateIssueDate(Date pFinCertificateIssueDate) {
        finCertificateIssueDate = pFinCertificateIssueDate;
    }

    public Date getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(Date pDateOfIncorporation) {
        dateOfIncorporation = pDateOfIncorporation;
    }

    public String getExistenceYears() {
        return existenceYears;
    }

    public void setExistenceYears(String pExistenceYears) {
        existenceYears = pExistenceYears;
    }

    public BigDecimal getAnnualMsmePurchase() {
        return annualMsmePurchase;
    }

    public void setAnnualMsmePurchase(BigDecimal pAnnualMsmePurchase) {
        annualMsmePurchase = pAnnualMsmePurchase;
    }

    public String getMsmeStatus() {
        return msmeStatus;
    }

    public void setMsmeStatus(String pMsmeStatus) {
        msmeStatus = pMsmeStatus;
    }

    public String getMsmeRegType() {
        return msmeRegType;
    }

    public void setMsmeRegType(String pMsmeRegType) {
        msmeRegType = pMsmeRegType;
    }

    public String getMsmeRegNo() {
        return msmeRegNo;
    }

    public void setMsmeRegNo(String pMsmeRegNo) {
        msmeRegNo = pMsmeRegNo;
    }

    public Date getMsmeRegDate() {
        return msmeRegDate;
    }

    public void setMsmeRegDate(Date pMsmeRegDate) {
        msmeRegDate = pMsmeRegDate;
    }

    public String getCaName() {
        return caName;
    }

    public void setCaName(String pCaName) {
        caName = pCaName;
    }

    public String getCaMemNo() {
        return caMemNo;
    }

    public void setCaMemNo(String pCaMemNo) {
        caMemNo = pCaMemNo;
    }

    public Date getInvtDateCPM() {
        return invtDateCPM;
    }

    public void setInvtDateCPM(Date pInvtDateCPM) {
        invtDateCPM = pInvtDateCPM;
    }

    public BigDecimal getInvtCPM() {
        return invtCPM;
    }

    public void setInvtCPM(BigDecimal pInvtCPM) {
        invtCPM = pInvtCPM;
    }

    public Date getCaCertDate() {
        return caCertDate;
    }

    public void setCaCertDate(Date pCaCertDate) {
        caCertDate = pCaCertDate;
    }

    public String getCustomer1() {
        return customer1;
    }

    public void setCustomer1(String pCustomer1) {
        customer1 = pCustomer1;
    }

    public String getCustomer2() {
        return customer2;
    }

    public void setCustomer2(String pCustomer2) {
        customer2 = pCustomer2;
    }

    public String getCustomer3() {
        return customer3;
    }

    public void setCustomer3(String pCustomer3) {
        customer3 = pCustomer3;
    }

    public String getCustomer4() {
        return customer4;
    }

    public void setCustomer4(String pCustomer4) {
        customer4 = pCustomer4;
    }

    public String getCustomer5() {
        return customer5;
    }

    public void setCustomer5(String pCustomer5) {
        customer5 = pCustomer5;
    }

    public String getCustomer1City() {
        return customer1City;
    }

    public void setCustomer1City(String pCustomer1City) {
        customer1City = pCustomer1City;
    }

    public String getCustomer2City() {
        return customer2City;
    }

    public void setCustomer2City(String pCustomer2City) {
        customer2City = pCustomer2City;
    }

    public String getCustomer3City() {
        return customer3City;
    }

    public void setCustomer3City(String pCustomer3City) {
        customer3City = pCustomer3City;
    }
    public String getCustomer4City() {
        return customer4City;
    }
    public void setCustomer4City(String pCustomer4City) {
        customer4City = pCustomer4City;
    }
    public String getCustomer5City() {
        return customer5City;
    }
    public void setCustomer5City(String pCustomer5City) {
        customer5City = pCustomer5City;
    }
    public String getTurnOver1() {
        return turnOver1;
    }
    public void setTurnOver1(String pTurnOver1) {
        turnOver1 = pTurnOver1;
    }
    public String getTurnOver2() {
        return turnOver2;
    }
    public void setTurnOver2(String pTurnOver2) {
        turnOver2 = pTurnOver2;
    }
    public String getTurnOver3() {
        return turnOver3;
    }
    public void setTurnOver3(String pTurnOver3) {
        turnOver3 = pTurnOver3;
    }
    public String getTurnOver4() {
        return turnOver4;
    }
    public void setTurnOver4(String pTurnOver4) {
        turnOver4 = pTurnOver4;
    }
    public String getTurnOver5() {
        return turnOver5;
    }
    public void setTurnOver5(String pTurnOver5) {
        turnOver5 = pTurnOver5;
    }
    public String getYearsInRelation1() {
        return yearsInRelation1;
    }
    public void setYearsInRelation1(String pYearsInRelation1) {
        yearsInRelation1 = pYearsInRelation1;
    }
    public String getYearsInRelation2() {
        return yearsInRelation2;
    }
    public void setYearsInRelation2(String pYearsInRelation2) {
        yearsInRelation2 = pYearsInRelation2;
    }
    public String getYearsInRelation3() {
        return yearsInRelation3;
    }
    public void setYearsInRelation3(String pYearsInRelation3) {
        yearsInRelation3 = pYearsInRelation3;
    }
    public String getYearsInRelation4() {
        return yearsInRelation4;
    }
    public void setYearsInRelation4(String pYearsInRelation4) {
        yearsInRelation4 = pYearsInRelation4;
    }
    public String getYearsInRelation5() {
        return yearsInRelation5;
    }
    public void setYearsInRelation5(String pYearsInRelation5) {
        yearsInRelation5 = pYearsInRelation5;
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

    public String getSector() {
        return sector;
    }

    public void setSector(String pSector) {
        sector = pSector;
    }

    public String getExportOrientation() {
        return exportOrientation;
    }

    public void setExportOrientation(String pExportOrientation) {
        exportOrientation = pExportOrientation;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String pCurrency) {
        currency = pCurrency;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pPan) {
        pan = pPan;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String pVat) {
        vat = pVat;
    }

    public String getCst() {
        return cst;
    }

    public void setCst(String pCst) {
        cst = pCst;
    }

    public String getLbt() {
        return lbt;
    }

    public void setLbt(String pLbt) {
        lbt = pLbt;
    }

    public String getStRegNo() {
        return stRegNo;
    }

    public void setStRegNo(String pStRegNo) {
        stRegNo = pStRegNo;
    }

    public Yes getStExempted() {
        return stExempted;
    }

    public void setStExempted(Yes pStExempted) {
        stExempted = pStExempted;
    }

    public String getExciseRegNo() {
        return exciseRegNo;
    }

    public void setExciseRegNo(String pExciseRegNo) {
        exciseRegNo = pExciseRegNo;
    }

    public String getTan() {
        return tan;
    }

    public void setTan(String pTan) {
        tan = pTan;
    }

    public BigDecimal getInvtPnM() {
        return invtPnM;
    }

    public void setInvtPnM(BigDecimal pInvtPnM) {
        invtPnM = pInvtPnM;
    }

    public BigDecimal getSalesTo() {
        return salesTo;
    }

    public void setSalesTo(BigDecimal pSalesTo) {
        salesTo = pSalesTo;
    }

    public String getSalesYear() {
        return salesYear;
    }

    public void setSalesYear(String pSalesYear) {
        salesYear = pSalesYear;
    }

    public CompanyApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(CompanyApprovalStatus pApprovalStatus) {
        approvalStatus = pApprovalStatus;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String pRegistrationNo) {
        registrationNo = pRegistrationNo;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long pRefId) {
        refId = pRefId;
    }

    public BigDecimal getCashDiscountPercent() {
        return cashDiscountPercent;
    }

    public void setCashDiscountPercent(BigDecimal pCashDiscountPercent) {
        cashDiscountPercent = pCashDiscountPercent;
    }

    public Yes getEnableLocationwiseSettlement() {
        return enableLocationwiseSettlement;
    }

    public void setEnableLocationwiseSettlement(Yes pEnableLocationwiseSettlement) {
        enableLocationwiseSettlement = pEnableLocationwiseSettlement;
    }

    public CompanyFlag getCompanyFlag() {
    	if(purchaserFlag.equals(YesNo.Yes)){
    		return CompanyDetailBean.CompanyFlag.Buyer;
    }
    	if(supplierFlag.equals(YesNo.Yes)){
    		return CompanyDetailBean.CompanyFlag.Seller;
		}
    	if(financierFlag.equals(YesNo.Yes)){
    		return CompanyDetailBean.CompanyFlag.Financier;
		}
        return null;
    }
    public void setCompanyFlag(CompanyFlag pCompanyFlag) {
		purchaserFlag = null;
		supplierFlag = null;
		financierFlag = null;
    	if(pCompanyFlag.equals(CompanyDetailBean.CompanyFlag.Buyer)){
			purchaserFlag = YesNo.Yes;
			supplierFlag = YesNo.No;
			financierFlag = YesNo.No;
    }
    	if(pCompanyFlag.equals(CompanyDetailBean.CompanyFlag.Seller)){
    		purchaserFlag = YesNo.No;
			supplierFlag = YesNo.Yes;
			financierFlag = YesNo.No;
		}
    	if(pCompanyFlag.equals(CompanyDetailBean.CompanyFlag.Financier)){
    		purchaserFlag = YesNo.No;
			supplierFlag = YesNo.No;
			financierFlag = YesNo.Yes;
		}
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
    
    public String getUdin() {
        return udin;
    }
    
    public void setUdin(String pUdin) {
        udin = pUdin;
    }
    
    public String getCreatorIdentity() {
        return creatorIdentity;
    }

    public void setCreatorIdentity(String pCreatorIdentity) {
        creatorIdentity = pCreatorIdentity;
    }
    
    public String getDocumentsUrl() {
        return documentsUrl;
    }

    public void setDocumentsUrl(String pDocumentsUrl) {
        documentsUrl = pDocumentsUrl;
    }
    
    public String getLoginId()
    {
		if (getId() != null) {
			MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppUserBean.ENTITY_NAME);
			try {
				AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(AppUserBean.f_Id, new Long[] { getId() });
				if (lAppUserBean != null)
					return lAppUserBean.getLoginId();
			} catch (MemoryDBException e) {
			}
		}
    	return null;
    }
    
    public void setLoginId(String pLoginId)
    {
    }
    public String getCreatorLoginId()
    {
		if (getRecordCreator() != null) {
			MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppUserBean.ENTITY_NAME);
			try {
				AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(AppUserBean.f_Id, new Long[] { getRecordCreator() });
				if (lAppUserBean != null)
					return lAppUserBean.getDomain() + " [" +lAppUserBean.getLoginId() + "]";
			} catch (MemoryDBException e) {
			}
		}
    	return null;
    }
    public void setCreatorLoginId(String pCreatorLoginId)
    {    	
    }
    public String getSalesCategory(){
    	String lSalesCategory = null;
    	if(this.getSector() != null && this.getMsmeStatus()!=null){
    		lSalesCategory = this.getSector() + CommonConstants.KEY_SEPARATOR + this.getMsmeStatus();
    	}
    	return lSalesCategory;
    }
    public void setSalesCategory(String pSalesCategory){
    }

    public List<CompanyContactBean> getContacts() {
        return contacts;
    }

    public void setContacts(List<CompanyContactBean> pContacts) {
        contacts = pContacts;
    }

    public List<CompanyLocationBean> getLocations() {
        return locations;
    }

    public void setLocations(List<CompanyLocationBean> pLocations) {
        locations = pLocations;
    }

    public List<CompanyBankDetailBean> getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(List<CompanyBankDetailBean> pBankDetails) {
        bankDetails = pBankDetails;
    }

    public List<CompanyKYCDocumentBean> getDocuments() {
        return documents;
    }

    public void setDocuments(List<CompanyKYCDocumentBean> pDocuments) {
        documents = pDocuments;
    }

    public List<CompanyShareIndividualBean> getShareIndividuals() {
        return shareIndividuals;
    }

    public void setShareIndividuals(List<CompanyShareIndividualBean> pShareIndividuals) {
        shareIndividuals = pShareIndividuals;
    }

    public List<CompanyShareEntityBean> getShareEntities() {
        return shareEntities;
    }

    public void setShareEntities(List<CompanyShareEntityBean> pShareEntities) {
        shareEntities = pShareEntities;
    }
    
    public Long getTab() {
        return tab;
    }

    public void setTab(Long pTab) {
        tab = pTab;
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
