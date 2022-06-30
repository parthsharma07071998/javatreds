package com.xlx.treds.auction.bean;

import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class ObligationSettlementBankDetailsBean {
	
	private CompanyLocationBean companyLocationBean;
	private CompanyBankDetailBean companyBankDetailBean;
	private ObligationBean obligationBean;
	private ObligationSplitsBean obligationSplitBean;
	private FactoringUnitBean factoringUnitBean;
	private InstrumentBean instrumentBean;
	
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
	
	public ObligationBean getObligationBean(){
		return obligationBean;
	}
	public void setObligationBean(ObligationBean pobligationBean){
		obligationBean=pobligationBean;
	}

	public ObligationSplitsBean getObligationSplitBean() {
		return obligationSplitBean;
	}
	public void setObligationSplitBean(ObligationSplitsBean pObligationSplitBean) {
		obligationSplitBean = pObligationSplitBean;
	}
	
	public boolean isParentObligation(){
		return false;
	}
	
	public IObligation getObligationParentBean(){
		return obligationBean;
	}
	public IObligation getActualObligationBean(){
		if(obligationSplitBean!=null && obligationSplitBean.getId()!=null){
			return obligationSplitBean;
		}
		return obligationBean;
	}
	
	public FactoringUnitBean getFactoringUnitBean() {
		return factoringUnitBean;
	}
	public void setFactoringUnitBean(FactoringUnitBean pFactoringUnitBean) {
		factoringUnitBean = pFactoringUnitBean;
	}
	
	public InstrumentBean getInstrumentBean() {
		return instrumentBean;
	}
	public void setInstrumentBean(InstrumentBean pInstrumentBean) {
		instrumentBean = pInstrumentBean;
	}
	
}
