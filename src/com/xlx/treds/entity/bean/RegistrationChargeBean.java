
package com.xlx.treds.entity.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants.RegEntityType;

public class RegistrationChargeBean {
	
    public static final String FIELDGROUP_UPDATEMAKER = "updateMaker";
    public static final String FIELDGROUP_UPDATECHECKER = "updateChecker";
    public static final String FIELDGROUP_UPDATEBILLID = "updateBillId";
	
	
    public enum ChargeType implements IKeyValEnumInterface<String>{
        Registration("R","Registration"),Annual("A","Annual");
        
        private final String code;
        private final String desc;
        private ChargeType(String pCode, String pDesc) {
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
    public enum RequestType implements IKeyValEnumInterface<String>{
        Waiver("W","Waiver"),Extenstion("E","Extenstion"),Payment("P","Payment");
        
        private final String code;
        private final String desc;
        private RequestType(String pCode, String pDesc) {
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
    public enum ApprovalStatus implements IKeyValEnumInterface<String>{
        Draft("D","Draft"),Pending("P","Pending"),Approved("A","Approved"),Returned("R","Returned");
        
        private final String code;
        private final String desc;
        private ApprovalStatus(String pCode, String pDesc) {
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
    private String entityCode;
    private RegEntityType entityType;
    private ChargeType chargeType;
    private Date effectiveDate;
    private BigDecimal chargeAmount;
    private RequestType requestType;
    private Date extendedDate;
    private Date prevExtendedDate;
    private Long extensionCount;
    private Date paymentDate;
    private BigDecimal paymentAmount;
    private String paymentRefrence;
    private Long billId;
    private String billedEntityCode;
    private Long billedEntityClId;
    private String remarks;
    private String supportingDoc;
    private Long makerAuId;
    private Timestamp makerTimestamp;
    private String makerLoginId;
    private String makerName;
    private Long checkerAuId;
    private Timestamp checkerTimestamp;
    private String checkerLoginId;
    private String checkerName;
    private ApprovalStatus approvalStatus;
    private Date effectiveStartDate;
    private Date effectiveEndDate;
    private Date extendedStartDate;
    private Date extendedEndDate;
    private Date paymentStartDate;
    private Date paymentEndDate;
    private BigDecimal fromChargeAmount;
    private BigDecimal toChargeAmount;
    private Long tab;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private Date registrationDate;
    //non-database
    private Long annualFeeYear;
    //
    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String pEntityCode) {
        entityCode = pEntityCode;
    }

    public RegEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(RegEntityType pEntityType) {
        entityType = pEntityType;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType pChargeType) {
        chargeType = pChargeType;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date pEffectiveDate) {
        effectiveDate = pEffectiveDate;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal pChargeAmount) {
        chargeAmount = pChargeAmount;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType pRequestType) {
        requestType = pRequestType;
    }

    public Date getExtendedDate() {
        return extendedDate;
    }

    public void setExtendedDate(Date pExtendedDate) {
        extendedDate = pExtendedDate;
    }

    public Date getPrevExtendedDate() {
        return prevExtendedDate;
    }

    public void setPrevExtendedDate(Date pPrevExtendedDate) {
        prevExtendedDate = pPrevExtendedDate;
    }

    public Long getExtensionCount() {
        return extensionCount;
    }

    public void setExtensionCount(Long pExtensionCount) {
        extensionCount = pExtensionCount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date pPaymentDate) {
        paymentDate = pPaymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal pPaymentAmount) {
        paymentAmount = pPaymentAmount;
    }

    public String getPaymentRefrence() {
        return paymentRefrence;
    }

    public void setPaymentRefrence(String pPaymentRefrence) {
        paymentRefrence = pPaymentRefrence;
    }

    public Long getBillId() {
    	return billId;
    }
    public void setBillId(Long pBillId) {
    	billId = pBillId;
    }
    public String getBilledEntityCode() {
        return billedEntityCode;
    }

    public void setBilledEntityCode(String pBilledEntityCode) {
        billedEntityCode = pBilledEntityCode;
    }

    public Long getBilledEntityClId() {
        return billedEntityClId;
    }

    public void setBilledEntityClId(Long pBilledEntityClId) {
        billedEntityClId = pBilledEntityClId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
    }

    public String getSupportingDoc() {
        return supportingDoc;
    }

    public void setSupportingDoc(String pSupportingDoc) {
        supportingDoc = pSupportingDoc;
    }

    public Long getMakerAuId() {
        return makerAuId;
    }

    public void setMakerAuId(Long pMakerAuId) {
        makerAuId = pMakerAuId;
    }

    public Timestamp getMakerTimestamp() {
        return makerTimestamp;
    }

    public void setMakerTimestamp(Timestamp pMakerTimestamp) {
        makerTimestamp = pMakerTimestamp;
    }

    public String getMakerLoginId() {
        return makerLoginId;
    }

    public void setMakerLoginId(String pMakerLoginId) {
        makerLoginId = pMakerLoginId;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String pMakerName) {
        makerName = pMakerName;
    }

    public Long getCheckerAuId() {
        return checkerAuId;
    }

    public void setCheckerAuId(Long pCheckerAuId) {
        checkerAuId = pCheckerAuId;
    }

    public Timestamp getCheckerTimestamp() {
        return checkerTimestamp;
    }

    public void setCheckerTimestamp(Timestamp pCheckerTimestamp) {
        checkerTimestamp = pCheckerTimestamp;
    }

    public String getCheckerLoginId() {
        return checkerLoginId;
    }

    public void setCheckerLoginId(String pCheckerLoginId) {
        checkerLoginId = pCheckerLoginId;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String pCheckerName) {
        checkerName = pCheckerName;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus pApprovalStatus) {
        approvalStatus = pApprovalStatus;
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

    public Date getExtendedStartDate() {
        return extendedStartDate;
    }

    public void setExtendedStartDate(Date pExtendedStartDate) {
        extendedStartDate = pExtendedStartDate;
    }

    public Date getExtendedEndDate() {
        return extendedEndDate;
    }

    public void setExtendedEndDate(Date pExtendedEndDate) {
        extendedEndDate = pExtendedEndDate;
    }

    public Date getPaymentStartDate() {
        return paymentStartDate;
    }

    public void setPaymentStartDate(Date pPaymentStartDate) {
        paymentStartDate = pPaymentStartDate;
    }

    public Date getPaymentEndDate() {
        return paymentEndDate;
    }

    public void setPaymentEndDate(Date pPaymentEndDate) {
        paymentEndDate = pPaymentEndDate;
    }

    public BigDecimal getFromChargeAmount() {
        return fromChargeAmount;
    }

    public void setFromChargeAmount(BigDecimal pFromChargeAmount) {
        fromChargeAmount = pFromChargeAmount;
    }

    public BigDecimal getToChargeAmount() {
        return toChargeAmount;
    }

    public void setToChargeAmount(BigDecimal pToChargeAmount) {
        toChargeAmount = pToChargeAmount;
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

	@Override
	public String toString() {
		return super.toString();
	}

    public Long getTab() {
        return tab;
    }

    public void setTab(Long pTab) {
        tab = pTab;
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date pRegistrationDate) {
    	registrationDate = pRegistrationDate;
    }

	public Date getRenewalDate() {
		if(effectiveDate!=null) {
			java.util.Date lNewDate = CommonUtilities.addDays(new Date(effectiveDate.getTime()), 365);
    		return new Date(lNewDate.getTime());
		}
		return null;
	}

	public void setRenewalDate(Date renewalDate) {
	}

	public Long getAnnualFeeYear() {
		return annualFeeYear;
	}

	public void setAnnualFeeYear(Long annualFeeYear) {
		this.annualFeeYear = annualFeeYear;
	}

}