package com.xlx.treds.entity.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.commonn.CommonAppConstants.Yes;

public class MemberwisePlanBean {

    private String code;
    private Date effectiveStartDate;
    private Date effectiveEndDate;
    private Long acpId;
    private String acpName;
    private String cdName;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private String type;
    private Yes financierBearShare;
    private BigDecimal financierShare;
    private BigDecimal totalShare;
    


    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date pEffectiveStartDate) {
        effectiveStartDate = pEffectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date pEffectiveEndDate) {
        effectiveEndDate = pEffectiveEndDate;
    }

    public Long getAcpId() {
        return acpId;
    }

    public void setAcpId(Long pAcpId) {
        acpId = pAcpId;
    }

    public String getAcpName() {
        return acpName;
    }

    public void setAcpName(String pAcpName) {
        acpName = pAcpName;
    }
    public String getType() {
      return type;
    }
    public void setType(String pType) {
       type = pType;
    }

    public String getCdName() {
        return cdName;
    }

    public void setCdName(String pCdName) {
        cdName = pCdName;
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
    
    public Yes getFinancierBearShare() {
    return financierBearShare;
	}
	
	public void setFinancierBearShare(Yes pFinancierBearShare) {
	    financierBearShare = pFinancierBearShare;
	}
	
	public BigDecimal getFinancierShare() {
	    return financierShare;
	}
	
	public void setFinancierShare(BigDecimal pFinancierShare) {
	    financierShare = pFinancierShare;
	}
	
	public BigDecimal getSellerShare() {
		if(financierShare!=null && totalShare!=null){
			return totalShare.subtract(financierShare);
		}
		return null;
	}
	
	public void setSellerShare(BigDecimal pSellerShare) {
	}
	
    
    public BigDecimal getTotalShare() {
        return totalShare;
    }

    public void setTotalShare(BigDecimal pTotalShare) {
        totalShare = pTotalShare;
    }

}