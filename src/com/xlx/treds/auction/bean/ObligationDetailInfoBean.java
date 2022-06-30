package com.xlx.treds.auction.bean;

import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;

public class ObligationDetailInfoBean {

	//ObligationBean, AppEntitiesBean
	//CompanyDetailsBean, PaymentFileBean, FacilitatorEntityMappingBean, CompanyBankDetails
	private ObligationBean obligationBean;
	private AppEntityBean appEntityBean;
	//
	private CompanyDetailBean companyDetailBean;
	private PaymentFileBean paymentFileBean;
	private FacilitatorEntityMappingBean facilitatorEntityMappingBean;
	private CompanyBankDetailBean companyBankDetailBean;
	
	public ObligationBean getObligationBean(){
		return obligationBean ;
	}
	public void setObligationBean(ObligationBean pObligationBean){
		obligationBean = pObligationBean;
	}
	
	public AppEntityBean getAppEntityBean(){
		return appEntityBean ;
	}
	public void setAppEntityBean(AppEntityBean pAppEntityBean){
		appEntityBean = pAppEntityBean;
	}

	public CompanyDetailBean getCompanyDetailBean(){
		return companyDetailBean ;
	}
	public void setCompanyDetailBean(CompanyDetailBean pCompanyDetailBean){
		companyDetailBean = pCompanyDetailBean;
	}
	
	public PaymentFileBean getPaymentFileBean(){
		return paymentFileBean ;
	}
	public void setPaymentFileBean(PaymentFileBean pPaymentFileBean){
		paymentFileBean = pPaymentFileBean;
	}
	
	public FacilitatorEntityMappingBean getFacilitatorEntityMappingBean(){
		return facilitatorEntityMappingBean ;
	}
	public void setFacilitatorEntityMappingBean(FacilitatorEntityMappingBean pFacilitatorEntityMappingBean){
		facilitatorEntityMappingBean = pFacilitatorEntityMappingBean;
	}
	
	public CompanyBankDetailBean getCompanyBankDetailBean(){
		return companyBankDetailBean ;
	}
	public void setCompanyBankDetailBean(CompanyBankDetailBean pCompanyBankDetailBean){
		companyBankDetailBean = pCompanyBankDetailBean;
	}

}
