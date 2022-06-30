
package com.xlx.treds.monetago.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class MonetagoEwaybillInfoBean {

    private Long ewbNo;
    private Timestamp ewayBillDate;
    private String genMode;
    private String userGstin;
    private String supplyType;
    private String subSupplyType;
    private String docType;
    private String docNo;
    private Date docDate;
    private String fromGstin;
    private String fromTrdName;
    private String fromAddr1;
    private String fromAddr2;
    private String fromPlace;
    private Long fromPincode;
    private Long fromStateCode;
    private String toGstin;
    private String toTrdName;
    private String toAddr1;
    private String toAddr2;
    private String toPlace;
    private Long toPincode;
    private Long toStateCode;
    private BigDecimal totalValue;
    private BigDecimal totInvValue;
    private BigDecimal cgstValue;
    private BigDecimal sgstValue;
    private BigDecimal igstValue;
    private BigDecimal cessValue;
    private String transporterId;
    private String transporterName;
    private String status;
    private Long actualDist;
    private Long noValidDays;
    private Timestamp validUpto;
    private Long extendedTimes;
    private String rejectStatus;
    private String vehicleType;
    private Long actFromStateCode;
    private Long actToStateCode;
    private Long transactionType;
    private BigDecimal otherValue;
    private BigDecimal cessNonAdvolValue;
    private List<MonetagoEwaybillItemListBean> itemList;
    private List<MonetagoEwaybillVehicleListDetailsBean> vehicleListDetails;

    public Long getEwbNo() {
        return ewbNo;
    }

    public void setEwbNo(Long pEwbNo) {
        ewbNo = pEwbNo;
    }

    public Timestamp getEwayBillDate() {
        return ewayBillDate;
    }

    public void setEwayBillDate(Timestamp pEwayBillDate) {
        ewayBillDate = pEwayBillDate;
    }

    public String getGenMode() {
        return genMode;
    }

    public void setGenMode(String pGenMode) {
        genMode = pGenMode;
    }

    public String getUserGstin() {
        return userGstin;
    }

    public void setUserGstin(String pUserGstin) {
        userGstin = pUserGstin;
    }

    public String getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(String pSupplyType) {
        supplyType = pSupplyType;
    }

    public String getSubSupplyType() {
        return subSupplyType;
    }

    public void setSubSupplyType(String pSubSupplyType) {
        subSupplyType = pSubSupplyType;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String pDocType) {
        docType = pDocType;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String pDocNo) {
        docNo = pDocNo;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date pDocDate) {
        docDate = pDocDate;
    }

    public String getFromGstin() {
        return fromGstin;
    }

    public void setFromGstin(String pFromGstin) {
        fromGstin = pFromGstin;
    }

    public String getFromTrdName() {
        return fromTrdName;
    }

    public void setFromTrdName(String pFromTrdName) {
        fromTrdName = pFromTrdName;
    }

    public String getFromAddr1() {
        return fromAddr1;
    }

    public void setFromAddr1(String pFromAddr1) {
        fromAddr1 = pFromAddr1;
    }

    public String getFromAddr2() {
        return fromAddr2;
    }

    public void setFromAddr2(String pFromAddr2) {
        fromAddr2 = pFromAddr2;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String pFromPlace) {
        fromPlace = pFromPlace;
    }

    public Long getFromPincode() {
        return fromPincode;
    }

    public void setFromPincode(Long pFromPincode) {
        fromPincode = pFromPincode;
    }

    public Long getFromStateCode() {
        return fromStateCode;
    }

    public void setFromStateCode(Long pFromStateCode) {
        fromStateCode = pFromStateCode;
    }

    public String getToGstin() {
        return toGstin;
    }

    public void setToGstin(String pToGstin) {
        toGstin = pToGstin;
    }

    public String getToTrdName() {
        return toTrdName;
    }

    public void setToTrdName(String pToTrdName) {
        toTrdName = pToTrdName;
    }

    public String getToAddr1() {
        return toAddr1;
    }

    public void setToAddr1(String pToAddr1) {
        toAddr1 = pToAddr1;
    }

    public String getToAddr2() {
        return toAddr2;
    }

    public void setToAddr2(String pToAddr2) {
        toAddr2 = pToAddr2;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String pToPlace) {
        toPlace = pToPlace;
    }

    public Long getToPincode() {
        return toPincode;
    }

    public void setToPincode(Long pToPincode) {
        toPincode = pToPincode;
    }

    public Long getToStateCode() {
        return toStateCode;
    }

    public void setToStateCode(Long pToStateCode) {
        toStateCode = pToStateCode;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal pTotalValue) {
        totalValue = pTotalValue;
    }

    public BigDecimal getTotInvValue() {
        return totInvValue;
    }

    public void setTotInvValue(BigDecimal pTotInvValue) {
        totInvValue = pTotInvValue;
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

    public BigDecimal getCessValue() {
        return cessValue;
    }

    public void setCessValue(BigDecimal pCessValue) {
        cessValue = pCessValue;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(String pTransporterId) {
        transporterId = pTransporterId;
    }

    public String getTransporterName() {
        return transporterName;
    }

    public void setTransporterName(String pTransporterName) {
        transporterName = pTransporterName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String pStatus) {
        status = pStatus;
    }

    public Long getActualDist() {
        return actualDist;
    }

    public void setActualDist(Long pActualDist) {
        actualDist = pActualDist;
    }

    public Long getNoValidDays() {
        return noValidDays;
    }

    public void setNoValidDays(Long pNoValidDays) {
        noValidDays = pNoValidDays;
    }

    public Timestamp getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(Timestamp pValidUpto) {
        validUpto = pValidUpto;
    }

    public Long getExtendedTimes() {
        return extendedTimes;
    }

    public void setExtendedTimes(Long pExtendedTimes) {
        extendedTimes = pExtendedTimes;
    }

    public String getRejectStatus() {
        return rejectStatus;
    }

    public void setRejectStatus(String pRejectStatus) {
        rejectStatus = pRejectStatus;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String pVehicleType) {
        vehicleType = pVehicleType;
    }

    public Long getActFromStateCode() {
        return actFromStateCode;
    }

    public void setActFromStateCode(Long pActFromStateCode) {
        actFromStateCode = pActFromStateCode;
    }

    public Long getActToStateCode() {
        return actToStateCode;
    }

    public void setActToStateCode(Long pActToStateCode) {
        actToStateCode = pActToStateCode;
    }

    public Long getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Long pTransactionType) {
        transactionType = pTransactionType;
    }

    public BigDecimal getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(BigDecimal pOtherValue) {
        otherValue = pOtherValue;
    }

    public BigDecimal getCessNonAdvolValue() {
        return cessNonAdvolValue;
    }

    public void setCessNonAdvolValue(BigDecimal pCessNonAdvolValue) {
        cessNonAdvolValue = pCessNonAdvolValue;
    }

    public List<MonetagoEwaybillItemListBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<MonetagoEwaybillItemListBean> pItemList) {
        itemList = pItemList;
    }

    public List<MonetagoEwaybillVehicleListDetailsBean> getVehicleListDetails() {
        return vehicleListDetails;
    }

    public void setVehicleListDetails(List<MonetagoEwaybillVehicleListDetailsBean> pVehicleListDetails) {
        vehicleListDetails = pVehicleListDetails;
    }

	@Override
	public String toString() {
		return super.toString();
	}
}