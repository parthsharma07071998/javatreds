package com.xlx.treds.master.rest;

import java.io.File;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.RestrictedLoginRequired;
import com.xlx.treds.AppConstants;

@Singleton
@Path("/")
public class ApplicationResource {
	
    public ApplicationResource() {
        super();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/staticlink")
    @RestrictedLoginRequired(resourceGroup=AppConstants.RESOURCEGROUP_CLICKWRAPAGREEMENT)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
    	RegistryHelper lRegistryHelper = RegistryHelper.getInstance();
    	File lBaseFolder = new File(lRegistryHelper.getString(AppConstants.REGISTRY_STATICDOCUMENTS));
    	File lFile = new File(lRegistryHelper.getString(AppConstants.REGISTRY_STATICDOCUMENTS)+pRequest.getParameter("link"));
        if (!lFile.getCanonicalPath().startsWith(lBaseFolder.getCanonicalPath()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        pRequest.getRequestDispatcher("/WEB-INF/staticlink.jsp").forward(pRequest, pResponse);
    }
    
}