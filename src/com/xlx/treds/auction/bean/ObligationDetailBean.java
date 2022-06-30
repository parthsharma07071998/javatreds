package com.xlx.treds.auction.bean;

public class ObligationDetailBean {
    private ObligationBean obligationBean;
    private ObligationSplitsBean obligationSplitsBean;
	
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