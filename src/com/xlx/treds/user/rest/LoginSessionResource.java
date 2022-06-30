package com.xlx.treds.user.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.LoginSessionBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/loginsess")
public class LoginSessionResource {

    private AppUserBO appUserBO;
	private BeanMeta loginSessionBeanMeta;
	
    public LoginSessionResource() {
        super();
        appUserBO = new AppUserBO();
        loginSessionBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(LoginSessionBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/loginsession.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);

        LoginSessionBean lFilterBean = new LoginSessionBean();
        loginSessionBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        List<LoginSessionBean> lLoginSessionList = appUserBO.findLoginSessionList(pExecutionContext, lFilterBean, lFields, lUserBean);
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (LoginSessionBean lLoginSessionBean : lLoginSessionList) {
        		lResults.add(loginSessionBeanMeta.formatAsArray(lLoginSessionBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

}