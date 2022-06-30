package com.xlx.treds.hostapi.bean;

import java.math.BigDecimal;
import java.sql.Date;

import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.AppConstants.AutoAcceptableBidTypes;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;


public class BuyerSellerLinkBean {

	public enum Status implements IKeyValEnumInterface<String> {
		Active("ACT", "Active"), Suspended_by_Buyer("SBB", "Suspended by Buyer"), Suspended_by_Seller("SBS",
				"Suspended by Seller"), Suspended_by_Platform("SBP", "Suspended by Platform");

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

	private String supplier;
	private String purchaser;
	private String supplierPurchaserRef;
	private Long creditPeriod;
	private Long extendedCreditPeriod;
	private String purchaserSupplierRef;
	private CostBearer period1CostBearer;
	private BigDecimal period1CostPercent;
	private CostBearer period2CostBearer;
	private BigDecimal period2CostPercent;
	private CostBearer period3CostBearer;
	private BigDecimal period3CostPercent;
	private CostBearer bidAcceptingEntityType;
	private CostBearingType costBearingType;
	
	private CostBearer preSplittingCostBearer;
	private CostBearer postSplittingCostBearer;
	private BigDecimal buyerPercent;
	private BigDecimal sellerPercent;
	private CostBearer chargeBearer;
	private YesNo settleLeg3Flag;
	private AutoAcceptBid autoAccept;
	private AutoAcceptableBidTypes autoAcceptableBidTypes;
	private AutoConvert autoConvert;
	private YesNo purchaserAutoApproveInvoice;
	private YesNo sellerAutoApproveInvoice;
	private Status status;

	private YesNo invoiceMandatory;

	private String remarks;
	private Yes inWorkFlow;
	private BigDecimal cashDiscountPercent;
	private BigDecimal haircutPercent;
	private Long tab;
	private Yes fetchSupGstn;

	private YesNo relationFlag;
	private String platformReasonCode;
	private String relationDoc;
	private Date relationEffectiveDate;
	private String platformRemarks;

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public String getSupplierPurchaserRef() {
		return supplierPurchaserRef;
	}

	public void setSupplierPurchaserRef(String supplierPurchaserRef) {
		this.supplierPurchaserRef = supplierPurchaserRef;
	}

	public Long getCreditPeriod() {
		return creditPeriod;
	}

	public void setCreditPeriod(Long creditPeriod) {
		this.creditPeriod = creditPeriod;
	}

	public Long getExtendedCreditPeriod() {
		return extendedCreditPeriod;
	}

	public void setExtendedCreditPeriod(Long extendedCreditPeriod) {
		this.extendedCreditPeriod = extendedCreditPeriod;
	}

	public String getPurchaserSupplierRef() {
		return purchaserSupplierRef;
	}

	public void setPurchaserSupplierRef(String purchaserSupplierRef) {
		this.purchaserSupplierRef = purchaserSupplierRef;
	}

	public CostBearer getPeriod1CostBearer() {
		return period1CostBearer;
	}

	public void setPeriod1CostBearer(CostBearer period1CostBearer) {
		this.period1CostBearer = period1CostBearer;
	}

	public BigDecimal getPeriod1CostPercent() {
		return period1CostPercent;
	}

	public void setPeriod1CostPercent(BigDecimal period1CostPercent) {
		this.period1CostPercent = period1CostPercent;
	}

	public CostBearer getPeriod2CostBearer() {
		return period2CostBearer;
	}

	public void setPeriod2CostBearer(CostBearer period2CostBearer) {
		this.period2CostBearer = period2CostBearer;
	}

	public BigDecimal getPeriod2CostPercent() {
		return period2CostPercent;
	}

	public void setPeriod2CostPercent(BigDecimal period2CostPercent) {
		this.period2CostPercent = period2CostPercent;
	}

	public CostBearer getPeriod3CostBearer() {
		return period3CostBearer;
	}

	public void setPeriod3CostBearer(CostBearer period3CostBearer) {
		this.period3CostBearer = period3CostBearer;
	}

	public BigDecimal getPeriod3CostPercent() {
		return period3CostPercent;
	}

	public void setPeriod3CostPercent(BigDecimal period3CostPercent) {
		this.period3CostPercent = period3CostPercent;
	}

	public CostBearer getBidAcceptingEntityType() {
		return bidAcceptingEntityType;
	}

	public void setBidAcceptingEntityType(CostBearer bidAcceptingEntityType) {
		this.bidAcceptingEntityType = bidAcceptingEntityType;
	}

	public CostBearingType getCostBearingType() {
		return costBearingType;
	}

	public void setCostBearingType(CostBearingType costBearingType) {
		this.costBearingType = costBearingType;
	}

	
	public CostBearer getPreSplittingCostBearer() {
		return preSplittingCostBearer;
	}

	public void setPreSplittingCostBearer(CostBearer preSplittingCostBearer) {
		this.preSplittingCostBearer = preSplittingCostBearer;
	}

	public CostBearer getPostSplittingCostBearer() {
		return postSplittingCostBearer;
	}

	public void setPostSplittingCostBearer(CostBearer postSplittingCostBearer) {
		this.postSplittingCostBearer = postSplittingCostBearer;
	}

	public BigDecimal getBuyerPercent() {
		return buyerPercent;
	}

	public void setBuyerPercent(BigDecimal buyerPercent) {
		this.buyerPercent = buyerPercent;
	}

	public BigDecimal getSellerPercent() {
		return sellerPercent;
	}

	public void setSellerPercent(BigDecimal sellerPercent) {
		this.sellerPercent = sellerPercent;
	}

	public CostBearer getChargeBearer() {
		return chargeBearer;
	}

	public void setChargeBearer(CostBearer chargeBearer) {
		this.chargeBearer = chargeBearer;
	}

	public YesNo getSettleLeg3Flag() {
		return settleLeg3Flag;
	}

	public void setSettleLeg3Flag(YesNo settleLeg3Flag) {
		this.settleLeg3Flag = settleLeg3Flag;
	}

	public AutoAcceptBid getAutoAccept() {
		return autoAccept;
	}

	public void setAutoAccept(AutoAcceptBid autoAccept) {
		this.autoAccept = autoAccept;
	}

	public AutoAcceptableBidTypes getAutoAcceptableBidTypes() {
		return autoAcceptableBidTypes;
	}

	public void setAutoAcceptableBidTypes(AutoAcceptableBidTypes autoAcceptableBidTypes) {
		this.autoAcceptableBidTypes = autoAcceptableBidTypes;
	}

	public AutoConvert getAutoConvert() {
		return autoConvert;
	}

	public void setAutoConvert(AutoConvert autoConvert) {
		this.autoConvert = autoConvert;
	}

	public YesNo getPurchaserAutoApproveInvoice() {
		return purchaserAutoApproveInvoice;
	}

	public void setPurchaserAutoApproveInvoice(YesNo purchaserAutoApproveInvoice) {
		this.purchaserAutoApproveInvoice = purchaserAutoApproveInvoice;
	}

	public YesNo getSellerAutoApproveInvoice() {
		return sellerAutoApproveInvoice;
	}

	public void setSellerAutoApproveInvoice(YesNo sellerAutoApproveInvoice) {
		this.sellerAutoApproveInvoice = sellerAutoApproveInvoice;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	

	public YesNo getInvoiceMandatory() {
		return invoiceMandatory;
	}

	public void setInvoiceMandatory(YesNo invoiceMandatory) {
		this.invoiceMandatory = invoiceMandatory;
	}

	

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Yes getInWorkFlow() {
		return inWorkFlow;
	}

	public void setInWorkFlow(Yes inWorkFlow) {
		this.inWorkFlow = inWorkFlow;
	}

	public BigDecimal getCashDiscountPercent() {
		return cashDiscountPercent;
	}

	public void setCashDiscountPercent(BigDecimal cashDiscountPercent) {
		this.cashDiscountPercent = cashDiscountPercent;
	}

	public BigDecimal getHaircutPercent() {
		return haircutPercent;
	}

	public void setHaircutPercent(BigDecimal haircutPercent) {
		this.haircutPercent = haircutPercent;
	}

	public Long getTab() {
		return tab;
	}

	public void setTab(Long tab) {
		this.tab = tab;
	}

	public Yes getFetchSupGstn() {
		return fetchSupGstn;
	}

	public void setFetchSupGstn(Yes fetchSupGstn) {
		this.fetchSupGstn = fetchSupGstn;
	}

	

	public YesNo getRelationFlag() {
		return relationFlag;
	}

	public void setRelationFlag(YesNo relationFlag) {
		this.relationFlag = relationFlag;
	}

	public String getPlatformReasonCode() {
		return platformReasonCode;
	}

	public void setPlatformReasonCode(String platformReasonCode) {
		this.platformReasonCode = platformReasonCode;
	}

	public String getRelationDoc() {
		return relationDoc;
	}

	public void setRelationDoc(String relationDoc) {
		this.relationDoc = relationDoc;
	}

	public Date getRelationEffectiveDate() {
		return relationEffectiveDate;
	}

	public void setRelationEffectiveDate(Date relationEffectiveDate) {
		this.relationEffectiveDate = relationEffectiveDate;
	}

	public String getPlatformRemarks() {
		return platformRemarks;
	}

	public void setPlatformRemarks(String platformRemarks) {
		this.platformRemarks = platformRemarks;
	}

}
