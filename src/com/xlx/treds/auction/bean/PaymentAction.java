package com.xlx.treds.auction.bean;

public class PaymentAction {
	
	//common actions fields (+Status) which will help to form Where clause of query
	private ObligationBean.Type leg;
	private ObligationBean.TxnType txnType;
	//specific field of obligation which will help in where clause of query
	private String factoringUnitId; // this will used in the IN Clause
	private String excludeObligationId; //this will be use in NOT IN Clause
	// new status to be change to
	private ObligationBean.Status newStatus;
	
	public PaymentAction(ObligationBean.Type pLeg, ObligationBean.TxnType pTxnType){
		
	}
	
	public void setActionFor(String pFactoringUnitId, String pExcludeObligationId){
		
	}
}
