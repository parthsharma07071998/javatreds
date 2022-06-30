package com.xlx.treds.auction.bean;

import org.apache.commons.lang.StringUtils;

import com.xlx.treds.AppConstants;

public class PaymentInterfaceFactory {

	public static IPaymentInterface getPaymentInterface(String pFacilitator){
		IPaymentInterface lPaymentInterface = null;
		if(StringUtils.isNotEmpty(pFacilitator)){
			if(AppConstants.FACILITATOR_NPCI.equals(pFacilitator)){
				lPaymentInterface = new NPCIPaymentInterace();
			}else if(AppConstants.FACILITATOR_DIRECT.equals(pFacilitator)){
				lPaymentInterface = new DirectPaymentInterface();
			}
		}
		return lPaymentInterface;
	}
	
}
