package com.xlx.treds;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.treds.user.bean.AppUserBean;

public class ApiLogger {
	private static Logger logger = Logger.getLogger(ApiLogger.class);

	public static int logApiRequestResponse(boolean pIsRequest, HttpServletRequest pRequest, String pMessage, String pResourceName, int pRequestId) {
	   	AppUserBean lUserBean=null;
	   	int lRequestId = (pIsRequest?(new Random()).nextInt(1000):pRequestId);
		try {
			lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if (logger.isDebugEnabled()){
    		logger.debug("Api"+(pIsRequest?"Request":"Response") + ":"+ lRequestId+":"+pResourceName + ":"+pRequest.getMethod()+":" + pRequest.getRequestURL().toString() + ":"  +(lUserBean!=null?lUserBean.getDomain():"UNKNOWUSER") + ":Message:" + (pMessage!=null?pMessage:"NODATARECEIVED"));
    	}
    	return lRequestId;
 	}

}
