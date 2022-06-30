package com.xlx.treds.auction.rest;

import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.treds.auction.bo.DashboardBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonSlurper;

@Singleton
@Path("/dashboard")
public class DashboardResource {
	private DashboardBO dashboardBO;
    public DashboardResource() {
        super();
        dashboardBO = new DashboardBO();
    }
    

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/data")
    public String data(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<String> lList = (List<String>)lJsonSlurper.parseText(pFilter);
        //return new JsonBuilder(lResults).toString();
        return dashboardBO.getData(pExecutionContext, lUserBean, lList);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/auCal")
    public String auctionCalendar(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<String> lList = (List<String>)lJsonSlurper.parseText(pFilter);
        //return new JsonBuilder(lResults).toString();
        return dashboardBO.getAuctinCalendar(pExecutionContext, lUserBean, lList);
    }
}