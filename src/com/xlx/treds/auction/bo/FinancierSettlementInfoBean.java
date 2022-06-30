package com.xlx.treds.auction.bo;

import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;

public class FinancierSettlementInfoBean {
	// Obligations, FactoringUnits , FinancierAuctionSettings
	//
	private ObligationBean obligationBean = null;
	private FactoringUnitBean factoringUnitBean = null;
	private FinancierAuctionSettingBean financierAuctionSettingBean = null;
	//
	public ObligationBean getObligationBean(){
		return obligationBean ;
	}
	public void setObligationBean(ObligationBean pObligationBean){
		obligationBean = pObligationBean;
	}

	public FactoringUnitBean getFactoringUnitBean(){
		return factoringUnitBean;
	}
	public void setFactoringUnitBean(FactoringUnitBean pFactoringUnitBean){
		factoringUnitBean = pFactoringUnitBean;
	}

	public FinancierAuctionSettingBean getFinancierAuctionSettingBean(){
		return financierAuctionSettingBean;
	}
	public void setFinancierAuctionSettingBean(FinancierAuctionSettingBean pFinancierAuctionSettingBean){
		financierAuctionSettingBean = pFinancierAuctionSettingBean;
	}
}
