package com.xlx.treds.instrument.bean;

import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.FactoringUnitWatchBean;

public class FactoringUnitBidBean {
    private FactoringUnitBean factoringUnitBean;
    private InstrumentBean instrumentBean;
    private BidBean bidBean;
    private FactoringUnitWatchBean factoringUnitWatchBean;
    
    public FactoringUnitBean getFactoringUnitBean() {
        return factoringUnitBean;
    }
    public void setFactoringUnitBean(FactoringUnitBean pFactoringUnitBean) {
        factoringUnitBean = pFactoringUnitBean;
    }
    public InstrumentBean getInstrumentBean(){
    	return instrumentBean;
    }
    public void setInstrumentBean(InstrumentBean pInstrumentBean){
    	instrumentBean = pInstrumentBean;
    }
    public BidBean getBidBean() {
        return bidBean;
    }
    public void setBidBean(BidBean pBidBean) {
        bidBean = pBidBean;
    }
    public FactoringUnitWatchBean getFactoringUnitWatchBean() {
        return factoringUnitWatchBean;
    }
    public void setFactoringUnitWatchBean(
            FactoringUnitWatchBean pFactoringUnitWatchBean) {
        factoringUnitWatchBean = pFactoringUnitWatchBean;
    }
    
    
}
