
package com.xlx.treds.master.rest;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.xlx.treds.AppConstants.RegEntityType;
import com.xlx.treds.master.bean.RegistrationChargeMasterBean;
import com.xlx.treds.master.bo.RegistrationChargeMasterBO;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.report.ReportConvertorFactory;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.commonn.other.bean.FileDownloadBean;

@Path("/regchrgmstr")
@Singleton
public class RegistrationChargeMasterResource {

	public static final Logger logger = LoggerFactory.getLogger(RegistrationChargeMasterResource.class);

    private RegistrationChargeMasterBO registrationChargeMasterBO;
    private BeanMeta registrationChargeMasterBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public RegistrationChargeMasterResource() {
        super();
        registrationChargeMasterBO = new RegistrationChargeMasterBO();
        registrationChargeMasterBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(RegistrationChargeMasterBean.class);
        defaultListFields = Arrays.asList(new String[]{"entityType","registrationCharge","annualCharge"});
        lovFields = Arrays.asList(new String[]{"entityType","registrationCharge"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew ) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else{
            Object[] lKey = new Object[]{};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/registrationchargemaster.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrgmstr-view")
    @Path("/{entityType}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		@PathParam("entityType") RegEntityType pEntityType) throws Exception {
        RegistrationChargeMasterBean lFilterBean = new RegistrationChargeMasterBean();
        lFilterBean.setEntityType(pEntityType);
        RegistrationChargeMasterBean lRegistrationChargeMasterBean = registrationChargeMasterBO.findBean(pExecutionContext, lFilterBean);
        return registrationChargeMasterBeanMeta.formatAsJson(lRegistrationChargeMasterBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrgmstr-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        RegistrationChargeMasterBean lFilterBean = new RegistrationChargeMasterBean();
        registrationChargeMasterBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<RegistrationChargeMasterBean> lRegistrationChargeMasterList = registrationChargeMasterBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (RegistrationChargeMasterBean lRegistrationChargeMasterBean : lRegistrationChargeMasterList) {
            lResults.add(registrationChargeMasterBeanMeta.formatAsArray(lRegistrationChargeMasterBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey = "regchrgmstr-view")
    @Path("/download/{format}")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter, @PathParam("format") ReportConvertorFactory.DownloadFormat pFormat) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        RegistrationChargeMasterBean lFilterBean = new RegistrationChargeMasterBean();
        registrationChargeMasterBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<RegistrationChargeMasterBean> lRegistrationChargeMasterList = registrationChargeMasterBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        FileDownloadBean lFileDownloadBean = ReportConvertorFactory.getInstance().convertData(registrationChargeMasterBeanMeta.formatBeansAsList(lRegistrationChargeMasterList, null, 
        		lFields, true), "RegistrationChargeMaster", registrationChargeMasterBeanMeta.getFieldLabelList(null, lFields).toArray(new String[0]), pFormat);
        
        return lFileDownloadBean.getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<RegistrationChargeMasterBean> lRegistrationChargeMasterList = registrationChargeMasterBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (RegistrationChargeMasterBean lRegistrationChargeMasterBean : lRegistrationChargeMasterList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lRegistrationChargeMasterBean.getEntityType());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lRegistrationChargeMasterBean.getEntityType());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrgmstr-manage")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrgmstr-manage")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        RegistrationChargeMasterBean lRegistrationChargeMasterBean = new RegistrationChargeMasterBean();
        List<ValidationFailBean> lValidationFailBeans = registrationChargeMasterBeanMeta.validateAndParse(lRegistrationChargeMasterBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        registrationChargeMasterBO.save(pExecutionContext, lRegistrationChargeMasterBean, lUserBean, pNew);
    }


}