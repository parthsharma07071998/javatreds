package com.xlx.treds.auction.bean;

import java.util.ArrayList;
import java.util.List;

import com.xlx.commonn.Excluded;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class MISFinancierReportBean {
	// Tables : FU, INST, OBLI, BID, 
    private FactoringUnitBean factoringUnitBean;
    private BidBean bidBean;
    private InstrumentBean instrumentBean;
    private ObligationBean obligationBean;
    private FinancierAuctionSettingBean financierAuctionSettingBean;
    //
    @Excluded
    private List<ObligationBean> obligations;
    //
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
    public FinancierAuctionSettingBean getFinancierAuctionSettingBean(){
    	return financierAuctionSettingBean;
    }
    public void setFinancierAuctionSettingBean(FinancierAuctionSettingBean pFinancierAuctionSettingBean){
    	financierAuctionSettingBean = pFinancierAuctionSettingBean;
    }
    
    public List<ObligationBean> getObligations(){
    	return obligations;
    }
    
    public void addObligation(ObligationBean pObligationBean){
    	if(obligations == null) obligations = new ArrayList<ObligationBean>();
    	if(pObligationBean!=null) obligations.add(pObligationBean);
    }
        
}
