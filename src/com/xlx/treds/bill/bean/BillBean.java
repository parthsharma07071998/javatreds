package com.xlx.treds.bill.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.TredsHelper;

public class BillBean {

    public enum BillingType implements IKeyValEnumInterface<String> {
    	RegistrationFee("R","Registration Fee"),AnnualFee("A","Annual Fee"),TransactionCharge("T","Transaction Charge");
        private final String code;
        private final String desc;
        private BillingType(String pCode,String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String getdesc() {
            return desc;
        }
    }
    
    private Long id;
    private String billNumber;
    private Date billYearMonth;
    private Date billDate;
    private String entity;
    private String entName;
    private String entGstn;
    private String entPan;
    private String entLine1;
    private String entLine2;
    private String entLine3;
    private String entCountry;
    private String entState;
    private String entDistrict;
    private String entCity;
    private String entZipCode;
    private String entSalutation;
    private String entFirstName;
    private String entMiddleName;
    private String entLastName;
    private String entEmail;
    private String entTelephone;
    private String entMobile;
    private String entFax;
    private String tredsName;
    private String tredsGstn;
    private String tredsLine1;
    private String tredsLine2;
    private String tredsLine3;
    private String tredsCountry;
    private String tredsState;
    private String tredsDistrict;
    private String tredsCity;
    private String tredsZipCode;
    private String tredsEmail;
    private String tredsTelephone;
    private String tredsMobile;
    private String tredsFax;
    private String tredsPan;
    private String tredsCin;
    private String tredsNatureOfTrans;
    private String tredsSACCode;
    private String tredsSACDesc;
    private BigDecimal chargeAmount;
    private BigDecimal fuAmount;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
    private BigDecimal cgstSurcharge;
    private BigDecimal sgstSurcharge;
    private BigDecimal igstSurcharge;
    private BigDecimal cgstValue;
    private BigDecimal sgstValue;
    private BigDecimal igstValue;
    private BillingType billingType;
    private String billedForentity;
    private Long regBillEntityLocId; //temporary column 
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String pBillNumber) {
        billNumber = pBillNumber;
    }

    public Date getBillYearMonth() {
        return billYearMonth;
    }

    public void setBillYearMonth(Date pBillYearMonth) {
        billYearMonth = pBillYearMonth;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date pBillDate) {
        billDate = pBillDate;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String pEntity) {
        entity = pEntity;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String pEntName) {
        entName = pEntName;
    }

    public String getEntGstn() {
        return entGstn;
    }

    public void setEntGstn(String pEntGstn) {
        entGstn = pEntGstn;
    }

    public String getEntPan() {
        return entPan;
    }

    public void setEntPan(String pEntPan) {
        entPan = pEntPan;
    }

    public String getEntLine1() {
        return entLine1;
    }

    public void setEntLine1(String pEntLine1) {
        entLine1 = pEntLine1;
    }

    public String getEntLine2() {
        return entLine2;
    }

    public void setEntLine2(String pEntLine2) {
        entLine2 = pEntLine2;
    }

    public String getEntLine3() {
        return entLine3;
    }

    public void setEntLine3(String pEntLine3) {
        entLine3 = pEntLine3;
    }

    public String getEntCountry() {
        return entCountry;
    }

    public void setEntCountry(String pEntCountry) {
        entCountry = pEntCountry;
    }

    public String getEntState() {
        return entState;
    }

    public void setEntState(String pEntState) {
        entState = pEntState;
    }

    public String getEntDistrict() {
        return entDistrict;
    }

    public void setEntDistrict(String pEntDistrict) {
        entDistrict = pEntDistrict;
    }

    public String getEntCity() {
        return entCity;
    }

    public void setEntCity(String pEntCity) {
        entCity = pEntCity;
    }

    public String getEntZipCode() {
        return entZipCode;
    }

    public void setEntZipCode(String pEntZipCode) {
        entZipCode = pEntZipCode;
    }

    public String getEntSalutation() {
        return entSalutation;
    }

    public void setEntSalutation(String pEntSalutation) {
        entSalutation = pEntSalutation;
    }

    public String getEntFirstName() {
        return entFirstName;
    }

    public void setEntFirstName(String pEntFirstName) {
        entFirstName = pEntFirstName;
    }

    public String getEntMiddleName() {
        return entMiddleName;
    }

    public void setEntMiddleName(String pEntMiddleName) {
        entMiddleName = pEntMiddleName;
    }

    public String getEntLastName() {
        return entLastName;
    }

    public void setEntLastName(String pEntLastName) {
        entLastName = pEntLastName;
    }

    public String getEntEmail() {
        return entEmail;
    }

    public void setEntEmail(String pEntEmail) {
        entEmail = pEntEmail;
    }

    public String getEntTelephone() {
        return entTelephone;
    }

    public void setEntTelephone(String pEntTelephone) {
        entTelephone = pEntTelephone;
    }

    public String getEntMobile() {
        return entMobile;
    }

    public void setEntMobile(String pEntMobile) {
        entMobile = pEntMobile;
    }

    public String getEntFax() {
        return entFax;
    }

    public void setEntFax(String pEntFax) {
        entFax = pEntFax;
    }

    public String getTredsName() {
        return tredsName;
    }

    public void setTredsName(String pTredsName) {
        tredsName = pTredsName;
    }

    public String getTredsGstn() {
        return tredsGstn;
    }

    public void setTredsGstn(String pTredsGstn) {
        tredsGstn = pTredsGstn;
    }

    public String getTredsLine1() {
        return tredsLine1;
    }

    public void setTredsLine1(String pTredsLine1) {
        tredsLine1 = pTredsLine1;
    }

    public String getTredsLine2() {
        return tredsLine2;
    }

    public void setTredsLine2(String pTredsLine2) {
        tredsLine2 = pTredsLine2;
    }

    public String getTredsLine3() {
        return tredsLine3;
    }

    public void setTredsLine3(String pTredsLine3) {
        tredsLine3 = pTredsLine3;
    }

    public String getTredsCountry() {
        return tredsCountry;
    }

    public void setTredsCountry(String pTredsCountry) {
        tredsCountry = pTredsCountry;
    }

    public String getTredsState() {
        return tredsState;
    }

    public void setTredsState(String pTredsState) {
        tredsState = pTredsState;
    }

    public String getTredsDistrict() {
        return tredsDistrict;
    }

    public void setTredsDistrict(String pTredsDistrict) {
        tredsDistrict = pTredsDistrict;
    }

    public String getTredsCity() {
        return tredsCity;
    }

    public void setTredsCity(String pTredsCity) {
        tredsCity = pTredsCity;
    }

    public String getTredsZipCode() {
        return tredsZipCode;
    }

    public void setTredsZipCode(String pTredsZipCode) {
        tredsZipCode = pTredsZipCode;
    }

    public String getTredsEmail() {
        return tredsEmail;
    }

    public void setTredsEmail(String pTredsEmail) {
        tredsEmail = pTredsEmail;
    }

    public String getTredsTelephone() {
        return tredsTelephone;
    }

    public void setTredsTelephone(String pTredsTelephone) {
        tredsTelephone = pTredsTelephone;
    }

    public String getTredsMobile() {
        return tredsMobile;
    }

    public void setTredsMobile(String pTredsMobile) {
        tredsMobile = pTredsMobile;
    }

    public String getTredsFax() {
        return tredsFax;
    }

    public void setTredsFax(String pTredsFax) {
        tredsFax = pTredsFax;
    }

    public String getTredsPan() {
        return tredsPan;
    }

    public void setTredsPan(String pTredsPan) {
        tredsPan = pTredsPan;
    }

    public String getTredsCin() {
        return tredsCin;
    }

    public void setTredsCin(String pTredsCin) {
        tredsCin = pTredsCin;
    }

    public String getTredsNatureOfTrans() {
        return tredsNatureOfTrans;
    }

    public void setTredsNatureOfTrans(String pTredsNatureOfTrans) {
        tredsNatureOfTrans = pTredsNatureOfTrans;
    }

    public String getTredsSACCode() {
        return tredsSACCode;
    }

    public void setTredsSACCode(String pTredsSACCode) {
        tredsSACCode = pTredsSACCode;
    }

    public String getTredsSACDesc() {
        return tredsSACDesc;
    }

    public void setTredsSACDesc(String pTredsSACDesc) {
        tredsSACDesc = pTredsSACDesc;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal pChargeAmount) {
        chargeAmount = pChargeAmount;
    }

    public BigDecimal getFuAmount() {
        return fuAmount;
    }

    public void setFuAmount(BigDecimal pFUAmount) {
        fuAmount = pFUAmount;
    }

    public BigDecimal getCgst() {
        return cgst;
    }

    public void setCgst(BigDecimal pCgst) {
        cgst = pCgst;
    }

    public BigDecimal getSgst() {
        return sgst;
    }

    public void setSgst(BigDecimal pSgst) {
        sgst = pSgst;
    }

    public BigDecimal getIgst() {
        return igst;
    }

    public void setIgst(BigDecimal pIgst) {
        igst = pIgst;
    }

    public BigDecimal getCgstSurcharge() {
        return cgstSurcharge;
    }

    public void setCgstSurcharge(BigDecimal pCgstSurcharge) {
        cgstSurcharge = pCgstSurcharge;
    }

    public BigDecimal getSgstSurcharge() {
        return sgstSurcharge;
    }

    public void setSgstSurcharge(BigDecimal pSgstSurcharge) {
        sgstSurcharge = pSgstSurcharge;
    }

    public BigDecimal getIgstSurcharge() {
        return igstSurcharge;
    }

    public void setIgstSurcharge(BigDecimal pIgstSurcharge) {
        igstSurcharge = pIgstSurcharge;
    }

    public BigDecimal getCgstValue() {
        return cgstValue;
    }

    public void setCgstValue(BigDecimal pCgstValue) {
        cgstValue = pCgstValue;
    }

    public BigDecimal getSgstValue() {
        return sgstValue;
    }

    public void setSgstValue(BigDecimal pSgstValue) {
        sgstValue = pSgstValue;
    }

    public BigDecimal getIgstValue() {
        return igstValue;
    }

    public void setIgstValue(BigDecimal pIgstValue) {
        igstValue = pIgstValue;
    }

	public BillingType getBillingType() {
		return billingType;
	}

	public void setBillingType(BillingType billingType) {
		this.billingType = billingType;
	}

	public String getBilledForentity() {
		return billedForentity;
	}

	public void setBilledForentity(String billedForentity) {
		this.billedForentity = billedForentity;
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

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

    public String getTredsAddress() {
    	StringBuilder lFullAddress = new  StringBuilder();
     	if(CommonUtilities.hasValue(getTredsLine1())) {
     		lFullAddress.append(getTredsLine1().trim());
     	}
     	if(CommonUtilities.hasValue(getTredsLine2())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getTredsLine2().trim());
     	}
     	if(CommonUtilities.hasValue(getTredsLine3())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getTredsLine3().trim());
     	}
     	if(CommonUtilities.hasValue(getTredsCity())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getTredsCity().trim());
         	if(CommonUtilities.hasValue(getTredsZipCode())) {
         		if(lFullAddress.length() > 0) lFullAddress.append("-");
         		lFullAddress.append(getTredsZipCode().trim());
         	}
     	}
     	if(CommonUtilities.hasValue(getTredsDistrict())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getTredsDistrict().trim());
     	}
     	if(CommonUtilities.hasValue(getTredsStateDescription())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getTredsStateDescription().trim());
     	}
     	if(CommonUtilities.hasValue(getTredsCountry())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getTredsCountry().trim());
     	}
     	return lFullAddress.toString();
    }

    public void setTredsAddress(String pTredsAddress) {
    }

    public String getEntAddress() {
    	StringBuilder lFullAddress = new  StringBuilder();
	  	if(CommonUtilities.hasValue(getEntLine1())) {
	 		lFullAddress.append(getEntLine1().trim());
	 	}
	 	if(CommonUtilities.hasValue(getEntLine2())) {
	 		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
	 		lFullAddress.append(getEntLine2().trim());
	 	}
	 	if(CommonUtilities.hasValue(getEntLine3())) {
	 		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
	 		lFullAddress.append(getEntLine3().trim());
	 	}
     	if(CommonUtilities.hasValue(getEntCity())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getEntCity().trim());
         	if(CommonUtilities.hasValue(getEntZipCode())) {
         		if(lFullAddress.length() > 0) lFullAddress.append("-");
         		lFullAddress.append(getEntZipCode().trim());
         	}
     	}
     	if(CommonUtilities.hasValue(getEntDistrict())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getEntDistrict().trim());
     	}
     	if(CommonUtilities.hasValue(getEntStateDescription())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getEntStateDescription().trim());
     	}
     	if(CommonUtilities.hasValue(getEntCountry())) {
     		if(lFullAddress.length() > 0) lFullAddress.append(CommonConstants.COMMA);
     		lFullAddress.append(getEntCountry().trim());
     	}
     	return lFullAddress.toString();
    }

    public void setEntAddress(String pEntAddress) {
    }

    public String getTredsStateDescription() {
        return TredsHelper.getInstance().getGSTStateDesc(getTredsState());
    }

    public String getEntStateDescription() {
        return TredsHelper.getInstance().getGSTStateDesc(getEntState());
    }

    public String getEntAdminFullName() {
        StringBuilder lFullName = new  StringBuilder();
    	if(CommonUtilities.hasValue(getEntSalutation())) lFullName.append(getEntSalutation().trim());
    	if(CommonUtilities.hasValue(getEntFirstName())){
    		if(lFullName.length() > 0) lFullName.append(" ");
    		lFullName.append(getEntFirstName());
    	}
    	if(CommonUtilities.hasValue(getEntMiddleName())){
    		if(lFullName.length() > 0) lFullName.append(" ");
    		lFullName.append(getEntMiddleName());
    	}
    	if(CommonUtilities.hasValue(getEntLastName())){
    		if(lFullName.length() > 0) lFullName.append(" ");
    		lFullName.append(getEntLastName());
    	}
    	return lFullName.toString();
    }

    public void setEntAdminFullName(String pEntAdminFullName) {
    }

    public BigDecimal getTotalValue() {
    	BigDecimal lTotal=BigDecimal.ZERO;
    	if(getChargeAmount()!=null) lTotal = lTotal.add(getChargeAmount());
    	if(getIgstValue() != null) lTotal = lTotal.add(getIgstValue());
    	if(getCgstValue() != null) lTotal = lTotal.add(getCgstValue());
    	if(getSgstValue() != null) lTotal = lTotal.add(getSgstValue());
        return lTotal;
    }

    public void setTotalValue(BigDecimal pTotalGstValue) {
    }

    public BigDecimal getTotalGstValue() {
    	BigDecimal lTotal=BigDecimal.ZERO;
    	if(getIgstValue() != null) lTotal = lTotal.add(getIgstValue());
    	if(getCgstValue() != null) lTotal = lTotal.add(getCgstValue());
    	if(getSgstValue() != null) lTotal = lTotal.add(getSgstValue());
        return lTotal;
    }

    public void setTotalGstValue(BigDecimal pTotalGstValue) {
    }

	public Long getRegBillEntityLocId() {
		return regBillEntityLocId;
	}

	public void setRegBillEntityLocId(Long regBillEntityLocId) {
		this.regBillEntityLocId = regBillEntityLocId;
	}

}