package com.xlx.treds.auction.bean;

import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class FactoredPaymentBean {
    private FactoringUnitBean factoringUnitBean;
    private BidBean bidBean;
    private InstrumentBean instrumentBean;
    private ObligationBean obligationBean;
    private ObligationSplitsBean obligationSplitsBean;
    
    public FactoringUnitBean getFactoringUnitBean() {
        return factoringUnitBean;
    }
    public void setFactoringUnitBean(FactoringUnitBean pFactoringUnitBean) {
        factoringUnitBean = pFactoringUnitBean;
    }
    public BidBean getBidBean() {
        return bidBean;
    }
    public void setBidBean(BidBean pBidBean) {
        bidBean = pBidBean;
    }
    public InstrumentBean getInstrumentBean() {
        return instrumentBean;
    }
    public void setInstrumentBean(InstrumentBean pInstrumentBean) {
    	instrumentBean = pInstrumentBean;
    }
	
    public ObligationBean getObligationBean() {
		return obligationBean;
	}
	
    public void setObligationBean(ObligationBean pObligationBean) {
		obligationBean = pObligationBean;
	}
	
    public ObligationSplitsBean getObligationSplitsBean() {
		return obligationSplitsBean;
	}
	
    public void setObligationSplitsBean(ObligationSplitsBean pObligationSplitsBean) {
		obligationSplitsBean = pObligationSplitsBean;
	}
}
