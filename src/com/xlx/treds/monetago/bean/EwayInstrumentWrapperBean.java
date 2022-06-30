package com.xlx.treds.monetago.bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.GenericDAO;
import com.xlx.treds.AppConstants;
import com.xlx.treds.InstAuthJerseyClient;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.monetago.bo.GstnMandateBO;

public class EwayInstrumentWrapperBean {
	
	private Long instId; 
	private EwayBillDetailsBean ewayBillDetailsBean;
	private MonetagoInvoiceInfoBean invoiceInfoBean;
	private MonetagoEwaybillInfoBean ewayInfoBean;
	private InstrumentBean instrumentBean;
	private InstrumentBean instrumentBeanDB;
	private AppEntityBean sellerEntityBean;
	private AppEntityBean purchaserEntityBean;
	private CompanyDetailBean sellerCompanyDetailsBean;
	private CompanyDetailBean purchaserCompanyDetailsBean;
	
	public EwayInstrumentWrapperBean(Long pId){
		instId = pId;
		getEwayDetailsBean();
	}
	
	private void getEwayDetailsBean(){
		EwayBillDetailsBean lEwayBean = new EwayBillDetailsBean();
		GenericDAO<EwayBillDetailsBean> lEwayBillDetailsDAO = new GenericDAO<EwayBillDetailsBean>(EwayBillDetailsBean.class);
		GenericDAO<InstrumentBean> lInstDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		GstnMandateBO lGstnMandateBO = new GstnMandateBO();
		try(Connection lConn = DBHelper.getInstance().getConnection()){
			instrumentBeanDB = new InstrumentBean();
			instrumentBeanDB.setId(instId);
			instrumentBeanDB = lInstDAO.findBean(lConn, instrumentBeanDB);
			lEwayBean.setId(instrumentBeanDB.getEbdId());
			ewayBillDetailsBean = lEwayBillDetailsDAO.findBean(lConn, lEwayBean);
			invoiceInfoBean = InstAuthJerseyClient.getInstance().getGstrData(ewayBillDetailsBean.getGstrPayload());
			ewayInfoBean = InstAuthJerseyClient.getInstance().getEWayBillData(ewayBillDetailsBean.getEwayPayload());
			instrumentBean = lGstnMandateBO.getInstrumentBean(lConn, invoiceInfoBean, ewayInfoBean, ewayBillDetailsBean.getTredsPayload());
			sellerEntityBean = TredsHelper.getInstance().getAppEntityBean(instrumentBean.getSupplier());
			purchaserEntityBean = TredsHelper.getInstance().getAppEntityBean(instrumentBean.getPurchaser());
			sellerCompanyDetailsBean = TredsHelper.getInstance().getCompanyDetails(lConn, sellerEntityBean.getCdId(),false);
			purchaserCompanyDetailsBean = TredsHelper.getInstance().getCompanyDetails(lConn, purchaserEntityBean.getCdId(),false);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	 
	private String getStateDesc(String pState)
	{
		if(CommonUtilities.hasValue(pState))
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_STATE_GST, pState);
		return "";
	}
	
	private String getEwayStateDesc(String pState)
	{
		if(CommonUtilities.hasValue(pState)){
			if (pState.length()==1){
				pState = "0"+pState;
			}
			return TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_STATE_GST, pState);
		}else{
			return "";
		}
	}
	
	public String getSellerName(){
		return purchaserEntityBean.getName();
	}
	
	public String getEwayPurchaserName(){
		return sellerEntityBean.getName();
		
	}
	
	public String getPurchaserCorrAddress(){
		String lAddress = "";
		if(CommonUtilities.hasValue(purchaserCompanyDetailsBean.getCorLine1())) lAddress = purchaserCompanyDetailsBean.getCorLine1();
		if(CommonUtilities.hasValue(purchaserCompanyDetailsBean.getCorLine2())) lAddress += ", " + purchaserCompanyDetailsBean.getCorLine2();
		if(CommonUtilities.hasValue(purchaserCompanyDetailsBean.getCorLine3())) lAddress += ", " + purchaserCompanyDetailsBean.getCorLine3();
		if(CommonUtilities.hasValue(purchaserCompanyDetailsBean.getCorCity())) lAddress += ", " + purchaserCompanyDetailsBean.getCorCity();
		if(CommonUtilities.hasValue(purchaserCompanyDetailsBean.getCorZipCode())) lAddress += ", " + purchaserCompanyDetailsBean.getCorZipCode();
		if(CommonUtilities.hasValue(purchaserCompanyDetailsBean.getCorDistrict())) lAddress += ", " + purchaserCompanyDetailsBean.getCorDistrict();
		if(CommonUtilities.hasValue(purchaserCompanyDetailsBean.getCorState())) lAddress += ", " + getStateDesc(purchaserCompanyDetailsBean.getCorState());
		return lAddress;
	}
	
	public String getSellerCorrAddress(){
		String lAddress = "";
		if(CommonUtilities.hasValue(sellerCompanyDetailsBean.getCorLine1())) lAddress = sellerCompanyDetailsBean.getCorLine1();
		if(CommonUtilities.hasValue(sellerCompanyDetailsBean.getCorLine2())) lAddress += ", " + sellerCompanyDetailsBean.getCorLine2();
		if(CommonUtilities.hasValue(sellerCompanyDetailsBean.getCorLine3())) lAddress += ", " + sellerCompanyDetailsBean.getCorLine3();
		if(CommonUtilities.hasValue(sellerCompanyDetailsBean.getCorCity())) lAddress += ", " + sellerCompanyDetailsBean.getCorCity();
		if(CommonUtilities.hasValue(sellerCompanyDetailsBean.getCorZipCode())) lAddress += ", " + sellerCompanyDetailsBean.getCorZipCode();
		if(CommonUtilities.hasValue(sellerCompanyDetailsBean.getCorDistrict())) lAddress += ", " + sellerCompanyDetailsBean.getCorDistrict();
		if(CommonUtilities.hasValue(sellerCompanyDetailsBean.getCorState())) lAddress += ", " + getStateDesc(sellerCompanyDetailsBean.getCorState());
		return lAddress;
	}

	public String getConsignerDetPurchaserName(){
		return ewayInfoBean.getToTrdName();
	}
	
	public String getConsignerDetSellerName(){
		return ewayInfoBean.getFromTrdName();
	}
	
	public String getSellerGstn(){
		return ewayInfoBean.getFromGstin();
	}
	
	public String getPurchaserGstn(){
		return ewayInfoBean.getToGstin();
	}
	
	public String getPurchaserEwayAddress(){
		String lAddress = "";
		if(CommonUtilities.hasValue(ewayInfoBean.getToAddr1())) lAddress = ewayInfoBean.getToAddr1();
		if(CommonUtilities.hasValue(ewayInfoBean.getToAddr2())) lAddress += ", " + ewayInfoBean.getToAddr2();
		if(CommonUtilities.hasValue(ewayInfoBean.getToPlace())) lAddress += ", " + ewayInfoBean.getToPlace();
		if(CommonUtilities.hasValue(ewayInfoBean.getToPincode().toString())) lAddress += ", " + ewayInfoBean.getToPincode().toString();
		if(CommonUtilities.hasValue(ewayInfoBean.getToStateCode().toString())) lAddress += ", " + getEwayStateDesc(ewayInfoBean.getToStateCode().toString());
		return lAddress;
	}
	
	public String getSellerEwayAddress(){
		String lAddress = "";
		if(CommonUtilities.hasValue(ewayInfoBean.getFromAddr1())) lAddress = ewayInfoBean.getFromAddr1();
		if(CommonUtilities.hasValue(ewayInfoBean.getFromAddr2())) lAddress += ", " + ewayInfoBean.getFromAddr2();
		if(CommonUtilities.hasValue(ewayInfoBean.getFromPlace())) lAddress += ", " + ewayInfoBean.getFromPlace();
		if(CommonUtilities.hasValue(ewayInfoBean.getFromPincode().toString())) lAddress += ", " + ewayInfoBean.getFromPincode().toString();
		if(CommonUtilities.hasValue(ewayInfoBean.getFromStateCode().toString())) lAddress += ", " + getEwayStateDesc(ewayInfoBean.getFromStateCode().toString());
		return lAddress;
	}
	
	public String getEwayBillNo(){
		return ewayInfoBean.getEwbNo().toString();
	}
	
	public Long getInstrumentId(){
		return instrumentBean.getId();
	}
	
	public Timestamp getInstrumentCreateDate(){
		return instrumentBean.getRecordCreateTime();
	}
	
	public String getInstrumentInvoiceNumber(){
		return instrumentBean.getInstNumber();
	}
	
	public Date getInstrumentInvoiceDate(){
		return instrumentBean.getInstDate();
	}
	
	public BigDecimal getInstrumentAmount(){
		return instrumentBean.getAmount();
	}
	
	public BigDecimal getInstrumentNetAmount(){
		return instrumentBean.getNetAmount();
	}
	
	public BigDecimal getInstrumentTdsAmount(){
		return instrumentBean.getTdsAmount();
	}
	
	public BigDecimal getInstrumentDeductionAmount(){
		return instrumentBean.getCashDiscountValue().add(instrumentBean.getAdjAmount());
	}
	
	public Long getCreditPeriod(){
		return instrumentBean.getCreditPeriod();
	}
	
	public Long getExtendedCreditPeriod(){
		return instrumentBean.getExtendedCreditPeriod();
	}
	
	public Date getStatutoryDate(){
		return instrumentBean.getStatDueDate();
	}
	
	public Date getDueDate(){
		return instrumentBean.getInstDueDate();
	}
	
	public Timestamp getExpiryDate(){
		return instrumentBean.getFactorMaxEndDateTime();
	}
	
	public String getSupplierState(){
		return getEwayStateDesc(ewayInfoBean.getFromStateCode().toString());
	}
	
	public String getSupplierStateCode(){
		return ewayInfoBean.getFromStateCode().toString();
	}
	
	public String getPurchaserStateCode(){
		return ewayInfoBean.getToStateCode().toString();
	}
	
	public String getPurchaserState(){
		return getEwayStateDesc(ewayInfoBean.getToStateCode().toString());
	}
	
	public String getDocNo(){
		return ewayInfoBean.getDocNo();
	}
	
	public Date getDocDate(){
		return  ewayInfoBean.getDocDate();
	}
	
	public Timestamp getEwayBillValidFrom(){
		return ewayInfoBean.getEwayBillDate();
	}
	
	public Timestamp getEwayBillValidUpto(){
		return ewayInfoBean.getValidUpto();
	}
	
	public String getVeichleNo(){
		return  ewayInfoBean.getVehicleListDetails().get(0).getVehicleNo();
	}
	
	public List<MonetagoEwaybillItemListBean> getItems(){
		return ewayInfoBean.getItemList();
	}
	
	public List<MonetagoEwaybillVehicleListDetailsBean> getVehicleDetails(){
		return ewayInfoBean.getVehicleListDetails();
	}
		
	public BigDecimal getCgstValue(){
		return ewayInfoBean.getCgstValue();
	}
	
	public BigDecimal getIgstValue(){
		return ewayInfoBean.getIgstValue();
	}
	
	public BigDecimal getSgstValue(){
		return ewayInfoBean.getSgstValue();
	}
	
	public BigDecimal getGstTotal(){
		BigDecimal lAmt = new BigDecimal(0);
		if (ewayInfoBean.getSgstValue()!=null){
			lAmt = lAmt.add(ewayInfoBean.getSgstValue());
		}
		if (ewayInfoBean.getIgstValue()!=null){
			lAmt = lAmt.add(ewayInfoBean.getIgstValue());		
		}
		if (ewayInfoBean.getCgstValue()!=null){
			lAmt = lAmt.add(ewayInfoBean.getCgstValue());
		}
		return lAmt;
	}
	
	public BigDecimal getTotalInvoiceValue(){
		return ewayInfoBean.getTotInvValue();
	}
	
	public BigDecimal getTotalTaxableValue(){
		return ewayInfoBean.getTotalValue();
	}
	
	public BigDecimal getCessValue(){
		return ewayInfoBean.getCessValue();
	}
	
	public BigDecimal getCessNonAdvolValue(){
		return ewayInfoBean.getCessNonAdvolValue();
	}
	
	public Timestamp getInvoiceFetchTime(){
		return ewayBillDetailsBean.getRecordCreateTime();
	}
	
	public Date getInvoiceDate(){
		return instrumentBeanDB.getInstDate();
	}
	
	public String getPoNumber(){
		return instrumentBeanDB.getPoNumber();
	}
	
	public Date getPoDate(){
		return instrumentBeanDB.getPoDate();
	}
	
	public BigDecimal getRoundOff(){
		BigDecimal lAmt = BigDecimal.ZERO;
		lAmt = lAmt.add(getTotalTaxableValue());
		lAmt = lAmt.add(getCgstValue());
		lAmt = lAmt.add(getSgstValue());
		lAmt = lAmt.add(getIgstValue());
		lAmt = lAmt.add(getCessValue());
		lAmt = lAmt.add(getCessNonAdvolValue());
		lAmt = getTotalInvoiceValue().subtract(lAmt);
		return lAmt;
	}
}