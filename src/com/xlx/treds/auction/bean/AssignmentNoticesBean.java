package com.xlx.treds.auction.bean;

import java.sql.Date;

import com.xlx.common.base.CommonConstants;
import com.xlx.commonn.IKeyValEnumInterface;

public class AssignmentNoticesBean {
    public enum SupAccType implements IKeyValEnumInterface<String>{
        Term_Loan("TL","Term Loan"),Cash_Credit("CC","Cash Credit"),Overdraft("OD","Overdraft"),Current_Account("CA","Current Account"),Other("OT","Other");
        
        private final String code;
        private final String desc;
        private SupAccType(String pCode, String pDesc) {
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
    private String purchaser;
    private String supplier;
    private String financier;
    private Date businessDate;
    private String finAccNo;
    private String finIfsc;
    private String finBankName;
    private String finBranchName;
    private String supName;
    private String purName;
    private String finName;
    private String supLine1;
    private String supLine2;
    private String supLine3;
    private String supCountry;
    private String supState;
    private String supDistrict;
    private String supCity;
    private String supZipCode;
    private String purLine1;
    private String purLine2;
    private String purLine3;
    private String purCountry;
    private String purState;
    private String purDistrict;
    private String purCity;
    private String purZipCode;
    private String finLine1;
    private String finLine2;
    private String finLine3;
    private String finCountry;
    private String finState;
    private String finDistrict;
    private String finCity;
    private String finZipCode;
    private String supBankName;
    private SupAccType supAccType;
    private String supBankEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public String getFinancier() {
        return financier;
    }

    public void setFinancier(String pFinancier) {
        financier = pFinancier;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date pBusinessDate) {
        businessDate = pBusinessDate;
    }

    public String getFinAccNo() {
        return finAccNo;
    }

    public void setFinAccNo(String pFinAccNo) {
        finAccNo = pFinAccNo;
    }

    public String getFinIfsc() {
        return finIfsc;
    }

    public void setFinIfsc(String pFinIfsc) {
        finIfsc = pFinIfsc;
    }

    public String getFinBankName() {
        return finBankName;
    }

    public void setFinBankName(String pFinBankName) {
        finBankName = pFinBankName;
    }

    public String getFinBranchName() {
        return finBranchName;
    }

    public void setFinBranchName(String pFinBranchName) {
        finBranchName = pFinBranchName;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String pSupName) {
        supName = pSupName;
    }

    public String getPurName() {
        return purName;
    }

    public void setPurName(String pPurName) {
        purName = pPurName;
    }

    public String getFinName() {
        return finName;
    }

    public void setFinName(String pFinName) {
        finName = pFinName;
    }

    public String getSupLine1() {
        return supLine1;
    }

    public void setSupLine1(String pSupLine1) {
        supLine1 = pSupLine1;
    }

    public String getSupLine2() {
        return supLine2;
    }

    public void setSupLine2(String pSupLine2) {
        supLine2 = pSupLine2;
    }

    public String getSupLine3() {
        return supLine3;
    }

    public void setSupLine3(String pSupLine3) {
        supLine3 = pSupLine3;
    }

    public String getSupCountry() {
        return supCountry;
    }

    public void setSupCountry(String pSupCountry) {
        supCountry = pSupCountry;
    }

    public String getSupState() {
        return supState;
    }

    public void setSupState(String pSupState) {
        supState = pSupState;
    }

    public String getSupDistrict() {
        return supDistrict;
    }

    public void setSupDistrict(String pSupDistrict) {
        supDistrict = pSupDistrict;
    }

    public String getSupCity() {
        return supCity;
    }

    public void setSupCity(String pSupCity) {
        supCity = pSupCity;
    }

    public String getSupZipCode() {
        return supZipCode;
    }

    public void setSupZipCode(String pSupZipCode) {
        supZipCode = pSupZipCode;
    }

    public String getPurLine1() {
        return purLine1;
    }

    public void setPurLine1(String pPurLine1) {
        purLine1 = pPurLine1;
    }

    public String getPurLine2() {
        return purLine2;
    }

    public void setPurLine2(String pPurLine2) {
        purLine2 = pPurLine2;
    }

    public String getPurLine3() {
        return purLine3;
    }

    public void setPurLine3(String pPurLine3) {
        purLine3 = pPurLine3;
    }

    public String getPurCountry() {
        return purCountry;
    }

    public void setPurCountry(String pPurCountry) {
        purCountry = pPurCountry;
    }

    public String getPurState() {
        return purState;
    }

    public void setPurState(String pPurState) {
        purState = pPurState;
    }

    public String getPurDistrict() {
        return purDistrict;
    }

    public void setPurDistrict(String pPurDistrict) {
        purDistrict = pPurDistrict;
    }

    public String getPurCity() {
        return purCity;
    }

    public void setPurCity(String pPurCity) {
        purCity = pPurCity;
    }

    public String getPurZipCode() {
        return purZipCode;
    }

    public void setPurZipCode(String pPurZipCode) {
        purZipCode = pPurZipCode;
    }

    public String getFinLine1() {
        return finLine1;
    }

    public void setFinLine1(String pFinLine1) {
        finLine1 = pFinLine1;
    }

    public String getFinLine2() {
        return finLine2;
    }

    public void setFinLine2(String pFinLine2) {
        finLine2 = pFinLine2;
    }

    public String getFinLine3() {
        return finLine3;
    }

    public void setFinLine3(String pFinLine3) {
        finLine3 = pFinLine3;
    }

    public String getFinCountry() {
        return finCountry;
    }

    public void setFinCountry(String pFinCountry) {
        finCountry = pFinCountry;
    }

    public String getFinState() {
        return finState;
    }

    public void setFinState(String pFinState) {
        finState = pFinState;
    }

    public String getFinDistrict() {
        return finDistrict;
    }

    public void setFinDistrict(String pFinDistrict) {
        finDistrict = pFinDistrict;
    }

    public String getFinCity() {
        return finCity;
    }

    public void setFinCity(String pFinCity) {
        finCity = pFinCity;
    }

    public String getFinZipCode() {
        return finZipCode;
    }

    public void setFinZipCode(String pFinZipCode) {
        finZipCode = pFinZipCode;
    }
    public String getSupBankName(){
    	return supBankName;
    }

    public void setSupBankName(String pSupBankName) {
        supBankName = pSupBankName;
    }

    public SupAccType getSupAccType() {
        return supAccType;
    }

    public void setSupAccType(SupAccType pSupAccType) {
        supAccType = pSupAccType;
    }

    public String getSupBankEmail() {
        return supBankEmail;
    }

    public void setSupBankEmail(String pSupBankEmail) {
        supBankEmail = pSupBankEmail;
    }

	public String getKey(){
		String lKey = "";
		lKey = this.getBusinessDate().toString();
		lKey += CommonConstants.KEY_SEPARATOR + this.getPurchaser();
		lKey += CommonConstants.KEY_SEPARATOR + this.getSupplier();
		lKey += CommonConstants.KEY_SEPARATOR + this.getFinancier();
		return lKey;
	}
    
}