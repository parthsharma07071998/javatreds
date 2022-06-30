package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;

public class ObligationExtensionBean {
    public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
    public static final String FIELDGROUP_APPROVED = "approved";
    public static final String FIELDGROUP_PLACEBID = "placeBid";
    public static final String FIELDGROUP_UPDATESTATUSSUBMIT = "updateStatusSubmit";
    public static final String FIELDGROUP_UPDATESTATUSAPPROVE = "updateStatusApprove";
    public enum Status implements IKeyValEnumInterface<String>{
        Pending("P","Pending"),ForApproval("F","ForApproval"),Approved("A","Approved")
        ,Rejected("R","Rejected"),Expired("E","Expired"),BidApproval("B","BidApproval")
        ,BidReturned("X","BidReturned"),Returned("Y","Returned");
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

    private Long obId;
    private Long creditObId;
    private String purchaser;
    private String financier;
    private Date oldDate;
    private String currency;
    private BigDecimal oldAmount;
    private Date newDate;
    private BigDecimal interestRate;
    private BigDecimal interest;
    private BigDecimal penalty;
    private BigDecimal penaltyRate;
    private BigDecimal newAmount;
    private Status status;
    private String remarks;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private ObligationExtensionPenaltyBean penaltySetting;
    private BigDecimal extendedBidRate;
    private BigDecimal tredsCharge;
    private Long tenor;
    public Long tab;
    private BigDecimal originalInterest;
    private BigDecimal newInterest;
    private YesNo penaltyRateApplied;
    private Date submitDate;
    private Date approveDate;
    private Date chargeDate;
    private Yes upfrontCharge;
    

    public Long getObId() {
        return obId;
    }

    public void setObId(Long pObId) {
        obId = pObId;
    }

    public Long getCreditObId() {
        return creditObId;
    }

    public void setCreditObId(Long pCreditObId) {
        creditObId = pCreditObId;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public String getFinancier() {
        return financier;
    }

    public void setFinancier(String pFinancier) {
        financier = pFinancier;
    }

    public String getPurchaserName() {
        try {
            if (StringUtils.isNotBlank(purchaser)) {
                AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(purchaser);
                if (lAppEntityBean != null)
                    return lAppEntityBean.getName();
            }
        } catch (Exception lException) {
        }
        return null;
    }

    public void setPurchaserName(String pPurchaserName) {
    }

    public String getFinancierName() {
        try {
            if (StringUtils.isNotBlank(financier)) {
                AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(financier);
                if (lAppEntityBean != null)
                    return lAppEntityBean.getName();
            }
        } catch (Exception lException) {
        }
        return null;
    }

    public void setFinancierName(String pFinancierName) {
    }

    public Date getOldDate() {
        return oldDate;
    }

    public void setOldDate(Date pOldDate) {
        oldDate = pOldDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String pCurrency) {
        currency = pCurrency;
    }

    public BigDecimal getOldAmount() {
        return oldAmount;
    }

    public void setOldAmount(BigDecimal pOldAmount) {
        oldAmount = pOldAmount;
    }

    public Date getNewDate() {
        return newDate;
    }

    public void setNewDate(Date pNewDate) {
        newDate = pNewDate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal pInterestRate) {
        interestRate = pInterestRate;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal pInterest) {
        interest = pInterest;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public void setPenalty(BigDecimal pPenalty) {
        penalty = pPenalty;
    }

    public BigDecimal getPenaltyRate() {
        return penaltyRate;
    }

    public void setPenaltyRate(BigDecimal pPenaltyRate) {
        penaltyRate = pPenaltyRate;
    }

    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(BigDecimal pNewAmount) {
        newAmount = pNewAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
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

    public ObligationExtensionPenaltyBean getPenaltySetting() {
        return penaltySetting;
    }

    public void setPenaltySetting(ObligationExtensionPenaltyBean pPenaltySetting) {
        penaltySetting = pPenaltySetting;
    }
    
    public Long getTab() {
        return tab;
    }

    public void setTab(Long pTab) {
        tab = pTab;
    }
    
    public BigDecimal getExtendedBidRate() {
    	if (extendedBidRate==null) {
    		extendedBidRate = BigDecimal.ZERO;
    		if (penaltyRate!=null) extendedBidRate = extendedBidRate.add(penaltyRate);
    		if (interestRate!=null) extendedBidRate = extendedBidRate.add(interestRate);
    	}
        return extendedBidRate;
    }

    public void setExtendedBidRate(BigDecimal pExtendedBidRate) {
    	extendedBidRate = pExtendedBidRate;
    }
    
    public BigDecimal getTredsCharge() {
        return tredsCharge;
    }

    public void setTredsCharge(BigDecimal pTredsCharge) {
    	tredsCharge = pTredsCharge;
    }
    
    public Long getTenor() {
    	if (newDate!=null && oldDate!=null) {
    		 return OtherResourceCache.getInstance().getDiffInDays(newDate, oldDate);
    	}
		return null;
    }

    public void setTenor() {
        
    }
    
    public BigDecimal getOriginalInterest() {
        return originalInterest;
    }

    public void setOriginalInterest(BigDecimal pOriginalInterest) {
    	originalInterest = pOriginalInterest;
    }
    
    public BigDecimal getNewInterest() {
        return newAmount.subtract(oldAmount);
    }

    public void setNewInterest(BigDecimal pNewInterest) {
    	newInterest = pNewInterest;
    }
    
    public YesNo getPenaltyRateApplied() {
        return penaltyRateApplied;
    }

    public void setPenaltyRateApplied(YesNo pPenaltyRateApplied) {
        penaltyRateApplied = pPenaltyRateApplied;
    }
    
    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date pSubmitDate) {
        submitDate = pSubmitDate;
    }

    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date pApproveDate) {
        approveDate = pApproveDate;
    }

    public Date getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(Date pChargeDate) {
        chargeDate = pChargeDate;
    }

    public Yes getUpfrontCharge() {
        return upfrontCharge;
    }

    public void setUpfrontCharge(Yes pUpfrontCharge) {
        upfrontCharge = pUpfrontCharge;
    }
    
}