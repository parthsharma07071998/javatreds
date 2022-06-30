
package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;

import groovy.json.JsonSlurper;

public class CIGroupBean {
	

    private Long id;
    private String cvNumber;
    private Long fuId;
    private Long inId;
    private String ciGroupDetail;
    private String customerRefNo;
    private String vendorCode;
    private String vendorName;
	private String vendorAddress;
    private String buyerName;
    private String buyerAddress;
    private String cinNumber;
    private Long recordVersion;
    
    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getCvNumber() {
        return cvNumber;
    }

    public void setCvNumber(String pCvNumber) {
        cvNumber = pCvNumber;
    }

    public Long getFuId() {
        return fuId;
    }

    public void setFuId(Long pFuId) {
        fuId = pFuId;
    }

    public Long getInId() {
        return inId;
    }

    public void setInId(Long pInId) {
        inId = pInId;
    }

    public String getCiGroupDetail() {
    	return ciGroupDetail;
    }

    public void setCiGroupDetail(String pCiGroupDetail) {
        ciGroupDetail = pCiGroupDetail;
    }
    
    public String getCustomerRefNo() {
        return customerRefNo;
    }

    public void setCustomerRefNo(String pCustomerRefNo) {
        customerRefNo = pCustomerRefNo;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String pVendorCode) {
        vendorCode = pVendorCode;
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
	
	public String[] getHeaders() {
		//Srl No	Invoice Number	Invoice Date	Value Accepted Payable (Rs)	FI Document No Details	Reason Code – Deduction Amount
		List<String> lHeaders = new ArrayList<String>();
		lHeaders.add("Sr No.");
		lHeaders.add("Vendor Invoice Number");
		lHeaders.add("Invoice Date");
		lHeaders.add("Inv Amount (Rs.)");
		lHeaders.add("FI Document No Details");
		lHeaders.add("Code");
		lHeaders.add("Debit/Credit Amount(Rs.)");
		return (String[])lHeaders.toArray(new String[lHeaders.size()]);
	}
	public String[] getColumnSequence() {
		List<String> lColumns = new ArrayList<String>();
		lColumns.add("sr_no");
		lColumns.add("inv_num");
		lColumns.add("inv_date");
		lColumns.add("amount");
		lColumns.add("document_number");
		lColumns.add("deduction_reason");
		lColumns.add("dedudcted_amount");
		return (String[])lColumns.toArray(new String[lColumns.size()]);
	}
	
	public List<List<String>> getData(){
		//ciGroupDetail
		List<List<String>> lData = new ArrayList<List<String>>();
		if(StringUtils.isNotEmpty(ciGroupDetail)) {
			List<String> lTmp = null;
			JsonSlurper lJsonSlurper = new JsonSlurper();
			List<Map<String,String>> lListMap = (List<Map<String,String>>) lJsonSlurper.parseText(ciGroupDetail);
			int lRow = 1;
			String[] lColNames = getColumnSequence();
			for(Map<String,String> lMap : lListMap) {
				lTmp = new ArrayList<String>();
				lData.add(lTmp);
				//
				for(String lColName : lColNames) {
					if(lColName.toLowerCase().equals("sr_no")) {
						lTmp.add((lRow++)+"");
					}else {
						lTmp.add((lMap.containsKey(lColName)?String.valueOf(lMap.get(lColName)):""));
					}
				}
			}
		}
		return lData;
	}
	
	public BigDecimal getNetAmount(){
		BigDecimal lNetAmount = BigDecimal.ZERO;
		if(StringUtils.isNotEmpty(ciGroupDetail)) {
			JsonSlurper lJsonSlurper = new JsonSlurper();
			List<Map<String,Object>> lListMap = (List<Map<String,Object>>) lJsonSlurper.parseText(ciGroupDetail);
			for(Map<String,Object> lMap : lListMap) {
				for(String lColName : getColumnSequence()) {
					if(lColName.toLowerCase().equals("amount")) {
						if(lMap.containsKey(lColName) &&
								lMap.get(lColName)!=null){
							if(lMap.get(lColName) instanceof Integer){
								lNetAmount = lNetAmount.add(new BigDecimal(((Integer)lMap.get(lColName)).intValue() ));
							}else if(lMap.get(lColName) instanceof Double){
								lNetAmount = lNetAmount.add(new BigDecimal(((Double)lMap.get(lColName)).doubleValue() ));
							}else if(lMap.get(lColName) instanceof String){
								if( CommonUtilities.isNumericWithDecimal(lMap.get(lColName).toString().trim()) ){
									lNetAmount = lNetAmount.add(new BigDecimal(lMap.get(lColName).toString().trim()));
								}else if( CommonUtilities.isNumeric(lMap.get(lColName).toString().trim()) ){
									lNetAmount = lNetAmount.add(new BigDecimal(lMap.get(lColName).toString().trim()));
								}
							}else if(lMap.get(lColName) instanceof BigDecimal){
								lNetAmount = lNetAmount.add((BigDecimal)lMap.get(lColName));
							}
						}
					}
				}
			}
		}
		return lNetAmount;
	}
	
    public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorAddress() {
		return vendorAddress;
	}

	public void setVendorAddress(String vendorAddress) {
		this.vendorAddress = vendorAddress;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getBuyerAddress() {
		return buyerAddress;
	}

	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}

	public String getCinNumber() {
		return cinNumber;
	}

	public void setCinNumber(String cinNumber) {
		this.cinNumber = cinNumber;
	}
	
}