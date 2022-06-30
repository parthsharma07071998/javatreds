package com.xlx.treds.monitor.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.monitor.bean.IMonitorHandler;
import com.xlx.treds.monitor.bean.MonitorFactory;
import com.xlx.treds.monitor.bean.MonitorMetaBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/monitors")
public class MonitorResource {
	
	private GenericDAO<MonitorMetaBean> monitorMetaDAO;
	
    public MonitorResource() {
        super();
        monitorMetaDAO = new GenericDAO<MonitorMetaBean>(MonitorMetaBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/monitors.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{code}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
    	return "";
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        
        List<String> lMonitorIds = (List<String>)lMap.get("ids");
        
        IMonitorHandler lMonitorHandler = MonitorFactory.getInstance().getMonitorHandler(lMonitorIds.get(0).toLowerCase());
        Map<String,Object> lData = new HashMap<String, Object>();
        lMonitorHandler.appendData(lData);
        List<Object[]> lResults = new ArrayList<Object[]>();

        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getMonitorMeta")
    public String getMonitorMeta(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        //read full monitors.json
        //loop through list and create Handlers.
        List<MonitorMetaBean> lTmpMMList = MonitorFactory.getInstance().getMonitorHandlerList(lUserBean); 
        List<Map<String, Object>> lResult = new ArrayList<Map<String, Object>>();
        Map<String, Object> lMonitorHash = null;
        for (MonitorMetaBean lMMBean : lTmpMMList ){
        	lMonitorHash = new HashMap<String,Object>();
        	lMonitorHash = monitorMetaDAO.getBeanMeta().formatAsMap(lMMBean, null, null, false);
        }
        //create a temp bean and copy only required data 
        return new JsonBuilder(lTmpMMList).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getData")
    public String getData(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
        IMonitorHandler lMonitorHandler = MonitorFactory.getInstance().getMonitorHandler(lMap.get("templateName").toString());
        Map<String,Object> lData = new HashMap<String, Object>();
        lMonitorHandler.appendData(lData);
        return new JsonBuilder(lData).toString();
    }
}