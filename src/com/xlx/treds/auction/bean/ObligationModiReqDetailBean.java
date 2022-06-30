package com.xlx.treds.auction.bean;

public class ObligationModiReqDetailBean {
    private ObligationModificationRequestBean obligationModificationRequestBean;
    private ObligationModificationDetailBean obligationModificationDetailBean;
	
    public ObligationModificationRequestBean getObligationModificationRequestBean() {
		return obligationModificationRequestBean;
	}
	
    public void setObligationModificationRequestBean(ObligationModificationRequestBean pObligationModificationRequestBean) {
    	obligationModificationRequestBean = pObligationModificationRequestBean;
	}
	
    public ObligationModificationDetailBean getObligationModificationDetailBean() {
		return obligationModificationDetailBean;
	}
	
    public void setObligationModificationDetailBean(ObligationModificationDetailBean pObligationModificationDetailBean) {
    	obligationModificationDetailBean = pObligationModificationDetailBean;
	}
}