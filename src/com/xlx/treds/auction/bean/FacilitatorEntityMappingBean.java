package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.xlx.commonn.CommonAppConstants.YesNo;

public class FacilitatorEntityMappingBean {

    private String facilitator;
    private String entityCode;
    private String mappingCode;
    private Long cbdId;
    private String designatedBankName;
    private String accNo;
    private String ifsc;
    private BigDecimal mandateAmount;
    private BigDecimal haircut;
    private YesNo active;
    private Date expiry;
    private List<Long> locationList;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public String getFacilitator() {
        return facilitator;
    }

    public void setFacilitator(String pFacilitator) {
        facilitator = pFacilitator;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String pEntityCode) {
        entityCode = pEntityCode;
    }

    public String getMappingCode() {
        return mappingCode;
    }

    public void setMappingCode(String pMappingCode) {
        mappingCode = pMappingCode;
    }

    public Long getCbdId() {
        return cbdId;
    }

    public void setCbdId(Long pCbdId) {
        cbdId = pCbdId;
    }

    public String getDesignatedBankName() {
        return designatedBankName;
    }

    public void setDesignatedBankName(String pDesignatedBankName) {
    	designatedBankName = pDesignatedBankName;
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

    public BigDecimal getMandateAmount() {
        return mandateAmount;
    }

    public void setMandateAmount(BigDecimal pMandateAmount) {
        mandateAmount = pMandateAmount;
    }

    public BigDecimal getHaircut() {
        return haircut;
    }

    public void setHaircut(BigDecimal pHaircut) {
        haircut = pHaircut;
    }

    public YesNo getActive() {
        return active;
    }

    public void setActive(YesNo pActive) {
        active = pActive;
    }
    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date pExpiry) {
        expiry = pExpiry;
    }

    public List<Long> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Long> pLocationList) {
        locationList = pLocationList;
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

}