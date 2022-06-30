package com.xlx.treds.other.rest;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

@Path("/static")
public class StaticDocumentResouce {

    public StaticDocumentResouce() {
        super();
    }

    @GET
    @Produces("application/x-pdf")
    @Secured
    @Path("/defaultmechanism")
    public Response getDefaultMechanism(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
		AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if (lUserBean==null) {
			throw new CommonBusinessException("Access Denied");
		}
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
		if (lAppEntityBean==null) {
			throw new CommonBusinessException("Access Denied");
		}
		if (! ( lAppEntityBean.isFinancier() || lAppEntityBean.isPlatform() ) ) {
			throw new CommonBusinessException("Access Denied");
		}
		String lFileName = "DefaultMechanism.pdf";
		return TredsHelper.getInstance().sendFileContents(lFileName, getContents(lFileName));
    }
    
    @GET
    @Produces("application/x-pdf")
    @Secured
    @Path("/businessrules")
    public Response getBusinessRules1(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if (lUserBean==null) {
			throw new CommonBusinessException("Access Denied");
		}
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
		if (lAppEntityBean==null) {
			throw new CommonBusinessException("Access Denied");
		}
		if (!(lAppEntityBean.isFinancier() 
				|| lAppEntityBean.isSupplier() 
					|| lAppEntityBean.isPurchaser()
						|| lAppEntityBean.isPlatform())) {
			throw new CommonBusinessException("Access Denied");
		}
    	String lFileName = "businessrules.pdf";
		return TredsHelper.getInstance().sendFileContents(lFileName, getContents(lFileName));
    }
    
    @GET
    @Produces("application/x-pdf")
    @Secured
    @Path("/grievanceredressal")
    public Response getGrievanceRedressal(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if (lUserBean==null) {
			throw new CommonBusinessException("Access Denied");
		}
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
		if (lAppEntityBean==null) {
			throw new CommonBusinessException("Access Denied");
		}
		if (!(lAppEntityBean.isFinancier() || lAppEntityBean.isSupplier() || lAppEntityBean.isPurchaser())) {
			throw new CommonBusinessException("Access Denied");
		}
    	String lFileName = "grievanceredressal.pdf";
		return TredsHelper.getInstance().sendFileContents(lFileName, getContents(lFileName));
    }

    private byte[] getContents(String pFileName) throws Exception {
    	  File lFile = new File(getStaticDocumentPath()+pFileName);
	  	  byte[] lBytesArray = new byte[(int) lFile.length()]; 
	  	  FileInputStream lFileInputStream = new FileInputStream(lFile);
	  	  lFileInputStream.read(lBytesArray); //read file into bytes[]
	  	  lFileInputStream.close();
	  	  return lBytesArray;
    }
    
    private String getStaticDocumentPath(){
		RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
		return lRegistryHelper.getString(AppConstants.REGISTRY_STATICDOCUMENTS);
    }
    

}