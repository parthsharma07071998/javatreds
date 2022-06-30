package com.xlx.treds.other.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bo.DashboardBO;

import groovy.json.JsonBuilder;

@Path("/findashboardmonitor")
public class FinancierDashboardResource {

	
    public FinancierDashboardResource() {
        super();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/findashboardmonitor.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/getData")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	DashboardBO lDashboardBO = new DashboardBO();
    	Map<String, Object> lMap = lDashboardBO.getDataForFinancierDashboard(pExecutionContext.getConnection(), lUserBean.getDomain());
    	return new JsonBuilder(lMap).toString();
    }


}