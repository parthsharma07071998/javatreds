package com.xlx.treds.auction.bean;

import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class ObliFUInstDetailBean {

	private ObligationBean obligationBean;
	private FactoringUnitBean factoringUnitBean;
	private InstrumentBean instrumentBean;

	public ObligationBean getObligationBean(){
		return obligationBean ;
	}
	public void setObligationBean(ObligationBean pObligationBean){
		obligationBean = pObligationBean;
	}
	
	public FactoringUnitBean getFactoringUnitBean(){
		return factoringUnitBean;
	}
	public void setFactoringUnitBean(FactoringUnitBean pFactoringUntiBean){
		factoringUnitBean = pFactoringUntiBean;
	}
	
	public InstrumentBean getInstrumentBean(){
		return instrumentBean;
	}
	public void setInstrumentBean(InstrumentBean pInstrumentBean){
		instrumentBean = pInstrumentBean;
	}
}
