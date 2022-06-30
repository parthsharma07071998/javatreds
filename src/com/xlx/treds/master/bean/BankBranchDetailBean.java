package com.xlx.treds.master.bean;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.IKeyValEnumInterface;

public class BankBranchDetailBean {
    public enum Status implements IKeyValEnumInterface<String>{
        Active("A","Active"),Suspended("S","Suspended"),Closed("C","Closed"),Deleted("D","Deleted");
        
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

    private String ifsc;
    private String micrcode;
    private String branchname;
    private String address;
    private String contact;
    private String city;
    private String district;
    private String state;
    private Status status;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private String bankCode;

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String pIfsc) {
        ifsc = pIfsc;
        bankCode = (StringUtils.isNotBlank(ifsc) && (ifsc.length() >= 4))?ifsc.substring(0, 4):null;
    }

    public String getMicrcode() {
        return micrcode;
    }

    public void setMicrcode(String pMicrcode) {
        micrcode = pMicrcode;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String pBranchname) {
        branchname = pBranchname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String pAddress) {
        address = pAddress;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String pContact) {
        contact = pContact;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String pCity) {
        city = pCity;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String pDistrict) {
        district = pDistrict;
    }

    public String getState() {
        return state;
    }

    public void setState(String pState) {
        state = pState;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
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
    
    public String getBankCode() {
        return bankCode; 
    }
    
    public void setBankCode(String pBankCode) {
        bankCode = pBankCode;
    }
}