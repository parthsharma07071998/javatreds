package com.xlx.treds.entity.bean;

import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.master.bean.BankBranchDetailBean;

public class CompanyLocationDetailsBean {

	private CompanyLocationBean companyLocationBean;
	private CompanyBankDetailBean companyBankDetailBean;
	private BankBranchDetailBean bankBranchDetailBean;
	private FacilitatorEntityMappingBean facilitatorEntityMappingBean;
	
	public CompanyLocationBean getCompanyLocationBean(){
		return companyLocationBean;
	}
	public void setCompanyLocationBean(CompanyLocationBean pCompanyLocationBean){
		companyLocationBean=pCompanyLocationBean;
	}
	
	public CompanyBankDetailBean getCompanyBankDetailBean(){
		return companyBankDetailBean;
	}
	public void setCompanyBankDetailBean(CompanyBankDetailBean pCompanyBankDetailBean){
		companyBankDetailBean=pCompanyBankDetailBean;
	}
	
	public BankBranchDetailBean getBankBranchDetailBean(){
		return bankBranchDetailBean;
	}
	public void setBankBranchDetailBean(BankBranchDetailBean pBankBranchDetailBean){
		bankBranchDetailBean=pBankBranchDetailBean;
	}
	public FacilitatorEntityMappingBean getFacilitatorEntityMappingBean() {
		return facilitatorEntityMappingBean;
	}
	public void setFacilitatorEntityMappingBean(FacilitatorEntityMappingBean pFacilitatorEntityMappingBean) {
		facilitatorEntityMappingBean = pFacilitatorEntityMappingBean;
	}
}
