package com.xlx.treds.other.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.auction.bo.ObligationModUploader;

public class FactoredReportBean {
	
	private static final int FUID = 0;
	private static final int PURCHASER = 1;
	private static final int SUPPLIER = 2;
	private static final int STATUS = 3;
	private static final int INVOICE_NUMBER = 4;
	private static final int INVOICE_DATE = 5;
	private static final int GOODS_ACCEPTANCE_DATE = 6;
	private static final int LEG2DATE = 7;
	private static final int INVOICE = 8;
	private static final int NETAMOUNT = 9;
	private static final int DEDUCTIONS_AND_TDS = 10;
	private static final int FACTOREDAMOUNT = 11;
	private static final int SALESCATEGORY = 12;
	private static final int FACTOREDDATETIME = 13;
	private static final int LEG1DATE = 14;
	
	
	
    public enum FuStatus implements IKeyValEnumInterface<String>{
        Ready_For_Auction("RDY","Ready For Auction"),Active("ACT","Active"),Factored("FACT","Factored"),Expired("EXP","Expired"),Leg_3_Generated("LEG3","Leg 3 Generated"),Withdrawn("WTHDRN","Withdrawn"),Suspended("SUSP","Suspended"),Leg_1_Settled("L1SET","Leg 1 Settled"),Leg_1_Failed("L1FAIL","Leg 1 Failed"),Leg_2_Settled("L2SET","Leg 2 Settled"),Leg_2_Failed("L2FAIL","Leg 2 Failed");
        private final String code;
        private final String desc;
        private FuStatus(String pCode, String pDesc) {
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

    private String fuId;
    private FuStatus fuStatus;
    private String inInstNumber;
    private Date inInstDate;
    private Date inGoodsAcceptanceDate;
    private Date l2Date;
    private String inInstImage;
    private BigDecimal inNetAmount;
    private BigDecimal inTdsAmount;
    private BigDecimal inAdjAmount;
    private BigDecimal inCashDiscountValue;
    private BigDecimal inDeductions;
    private BigDecimal fuAamount;
    private String inSalesCategory;
    private Timestamp inFuAcceptDateTime;
    private Date l1Date;
    private Date toDate;
    private Date fromDate;
    private String fuPurchaser;
    private String fuSupplier;
    private String purchaser;
    private String supplier;
    private String groupFlag;
    private Object [] rowData = new Object[15];
    
    public String getFuId() {
        return fuId;
    }

    public void setFuId(String pFuId) {
        fuId = pFuId;
    }

    public FuStatus getFuStatus() {
        return fuStatus;
    }

    public void setFuStatus(FuStatus pFuStatus) {
        fuStatus = pFuStatus;
    }

    public String getInInstNumber() {
        return inInstNumber;
    }

    public void setInInstNumber(String pInInstNumber) {
        inInstNumber = pInInstNumber;
    }

    public Date getInInstDate() {
        return inInstDate;
    }

    public void setInInstDate(Date pInInstDate) {
        inInstDate = pInInstDate;
    }

    public Date getInGoodsAcceptanceDate() {
        return inGoodsAcceptanceDate;
    }

    public void setInGoodsAcceptanceDate(Date pInGoodsAcceptanceDate) {
        inGoodsAcceptanceDate = pInGoodsAcceptanceDate;
    }

    public Date getL2Date() {
        return l2Date;
    }

    public void setL2Date(Date pL2Date) {
        l2Date = pL2Date;
    }

    public String getInInstImage() {
        return inInstImage;
    }

    public void setInInstImage(String pInInstImage) {
        inInstImage = pInInstImage;
    }

    public BigDecimal getInNetAmount() {
        return inNetAmount;
    }

    public void setInNetAmount(BigDecimal pInNetAmount) {
        inNetAmount = pInNetAmount;
    }

    public BigDecimal getInTdsAmount() {
        return inTdsAmount;
    }

    public void setInTdsAmount(BigDecimal pInTdsAmount) {
        inTdsAmount = pInTdsAmount;
    }

    public BigDecimal getInAdjAmount() {
        return inAdjAmount;
    }

    public void setInAdjAmount(BigDecimal pInAdjAmount) {
        inAdjAmount = pInAdjAmount;
    }

    public BigDecimal getInCashDiscountValue() {
        return inCashDiscountValue;
    }

    public void setInCashDiscountValue(BigDecimal pInCashDiscountValue) {
        inCashDiscountValue = pInCashDiscountValue;
    }

    public BigDecimal getInDeductions() {
    	BigDecimal lDeductions = BigDecimal.ZERO;
    	lDeductions = lDeductions.add(inAdjAmount);
    	lDeductions = lDeductions.add(inTdsAmount);
    	lDeductions = lDeductions.add(inCashDiscountValue);
        return lDeductions;
    }

    public void setInDeductions(BigDecimal pInDeductions) {
    }

    public BigDecimal getFuAamount() {
        return fuAamount;
    }

    public void setFuAamount(BigDecimal pFuAamount) {
        fuAamount = pFuAamount;
    }

    public String getInSalesCategory() {
        return inSalesCategory;
    }

    public void setInSalesCategory(String pInSalesCategory) {
        inSalesCategory = pInSalesCategory;
    }

    public Timestamp getInFuAcceptDateTime() {
        return inFuAcceptDateTime;
    }

    public void setInFuAcceptDateTime(Timestamp pInFuAcceptDateTime) {
        inFuAcceptDateTime = pInFuAcceptDateTime;
    }

    public Date getL1Date() {
        return l1Date;
    }

    public void setL1Date(Date pL1Date) {
        l1Date = pL1Date;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date pToDate) {
        toDate = pToDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date pFromDate) {
        fromDate = pFromDate;
    }

    public String getFuPurchaser() {
        return fuPurchaser;
    }

    public void setFuPurchaser(String pFuPurchaser) {
        fuPurchaser = pFuPurchaser;
    }

    public String getFuSupplier() {
        return fuSupplier;
    }

    public void setFuSupplier(String pFuSupplier) {
        fuSupplier = pFuSupplier;
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

	public String getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(String pGroupFlag) {
		groupFlag = pGroupFlag;
	}

	public Object[] getObjectArrayForExcell() {
		rowData[FUID] = fuId;
		rowData[PURCHASER] = purchaser;
		rowData[SUPPLIER] = supplier;
		rowData[STATUS] = fuStatus.toString();
		rowData[INVOICE_NUMBER] = inInstNumber;
		rowData[INVOICE_DATE] = inInstDate;
		rowData[GOODS_ACCEPTANCE_DATE] = inGoodsAcceptanceDate;
		rowData[LEG2DATE] = l2Date;
		rowData[INVOICE] = inInstImage ;
		rowData[NETAMOUNT] = inNetAmount;
		rowData[DEDUCTIONS_AND_TDS] = getInDeductions();
		rowData[FACTOREDAMOUNT] = fuAamount;
		rowData[SALESCATEGORY] = inSalesCategory;
		rowData[FACTOREDDATETIME] = inFuAcceptDateTime;
		rowData[LEG1DATE] = l1Date;
		return rowData;
	}

	public static Object[] getHeaders() {
		Object[] lRowData = new Object[15];
		lRowData[FUID] = "Factoring Unit Id";
		lRowData[PURCHASER] = "Purchaser";
		lRowData[SUPPLIER] = "Supplier";
		lRowData[STATUS] = "Status";
		lRowData[INVOICE_NUMBER] = "Invoice Number";
		lRowData[INVOICE_DATE] = "Invoice Date";
		lRowData[GOODS_ACCEPTANCE_DATE] = "Goods Acceptance Date";
		lRowData[LEG2DATE] = "Leg 2 Date";
		lRowData[INVOICE] = "Invoice" ;
		lRowData[NETAMOUNT] = "Net Amount";
		lRowData[DEDUCTIONS_AND_TDS] = "Deductions & TDS";
		lRowData[FACTOREDAMOUNT] = "Factored Amont";
		lRowData[SALESCATEGORY] = "Sales Category";
		lRowData[FACTOREDDATETIME] = "Factored Date Time";
		lRowData[LEG1DATE] = "Leg 1 Date";
		return lRowData;
	}
}