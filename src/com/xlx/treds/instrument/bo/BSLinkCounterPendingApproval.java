package com.xlx.treds.instrument.bo;

import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import com.xlx.common.messaging.IBulkMailSender;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.treds.auction.bean.ObliFUInstDetailBean;

public class BSLinkCounterPendingApproval implements IBulkMailSender {

	private String mLastErrorMessage = null;
	//
	private Map<String, Object> mParameters = null;
	private Date mBusinessDate = null;
	private String mLoginKey = null;
	private Connection mConnection = null;
	//
    private CompositeGenericDAO<ObliFUInstDetailBean> obligationFactoringUnitDAO;
	
	@Override
	public String getTemplate() {
		return "BSLinkCounterPendingApproval";
	}

	@Override
	public String getTemplateDescription() {
		return "Buyer Seller Link Counter Pending Approval";
	}

	@Override
	public Map<String, Object> getData() {
		return null;
	}
	
	
	@Override
	public boolean sendMail(Map<String, Object> pParameters) {
		mParameters = pParameters;
		mBusinessDate = CommonUtilities.getDate(pParameters.get("businessDate").toString(),"dd-MMM-yyyy");
		String lTemplate = CommonUtilities.getString(pParameters.get("template").toString(), true);
		if(mBusinessDate==null){
			mLastErrorMessage = "Business Date is mandatory.";
			return false;
		}
		HashMap<String, Object> lData = (HashMap<String, Object>) getData();
		if(lData!=null){
			
		}
		return true;
	}

	@Override
	public String getLastErrorMessage() {
		return mLastErrorMessage;
	}
	
}
