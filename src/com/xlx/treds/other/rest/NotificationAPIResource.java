package com.xlx.treds.other.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.messaging.SMSSender;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.ExecutionContext;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonSlurper;

@Path("/notify")
public class NotificationAPIResource {

	public NotificationAPIResource() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@POST
	@Path("/sms")
	public void getSmsNotification(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		Map<String, Object> lDataValuesTmp = null;
		List<String> lMobileList = (List<String>) lMap.get("mobilenumbers");
		for(String lMobileNo : lMobileList) {
			lDataValuesTmp = new HashMap<String, Object>();
			lDataValuesTmp.put(SMSSender.MOBILENO, lMobileNo);
			lDataValuesTmp.put("message", lMap.get("message"));
			//logger.info("Sending otp sms to " + lMobileNo);
			SMSSender.getInstance().addMessage("OnBoardingJocata"+"SMS.json", lDataValuesTmp);
		}
	}
	
	@POST
	@Path("/email")
	public void getEmailNotification(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
	        String pFilter) throws Exception{
		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		Map<String, Object> lDataValuesTmp = null;
		List<String> lEmailList = (List<String>) lMap.get("toemails");
		List<String> lCCEmailList = (List<String>) lMap.get("ccemails");
			lDataValuesTmp = new HashMap<String, Object>();
			lDataValuesTmp.put(EmailSender.TO, lEmailList);
			lDataValuesTmp.put(EmailSender.CC, lCCEmailList);
			lDataValuesTmp.put(EmailSender.SUBJECT, lMap.get("subject"));
			lDataValuesTmp.put("messagebody", lMap.get("messagebody"));
			EmailSender.getInstance().addMessage("OnBoardingJocata"+"Email.json", (HashMap) lDataValuesTmp);
	}
}
