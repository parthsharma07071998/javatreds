package com.xlx.treds.auction.bean;

import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class FactoredBean {
    private FactoringUnitBean factoringUnitBean;
    private BidBean bidBean;
    private InstrumentBean instrumentBean;
    
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

}
