package com.xlx.treds.entity.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyKYCDocumentBean;
import com.xlx.treds.entity.bo.CompanyKYCDocumentBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/companykycdocument")
@Singleton
public class CompanyKYCDocumentResource {

    private CompanyKYCDocumentBO companyKYCDocumentBO;
    private BeanMeta companyKYCDocumentBeanMeta;
	private List<String> defaultListFields;
	private String defaultListDBColumns;
	
    public CompanyKYCDocumentResource() {
        super();
        companyKYCDocumentBO = new CompanyKYCDocumentBO();
        companyKYCDocumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyKYCDocumentBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","documentType","document","fileName","remarks","isProvisional"});
        defaultListDBColumns = "CKDId,CKDDocumentType,CKDDocument,CKDFileName,CKDRemarks";
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pId != null)) {
            Object[] lKey = new Object[]{pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/companykycdocument.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyKYCDocumentBean lFilterBean = new CompanyKYCDocumentBean();
        lFilterBean.setId(pId);
        lFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(lUserBean));
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(true);
       CompanyKYCDocumentBean lCompanyKYCDocumentBean = companyKYCDocumentBO.findBean(pExecutionContext, lFilterBean);
        if(lCompanyKYCDocumentBean!=null){
            if(!TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), lCompanyKYCDocumentBean.getCdId(), (AppUserBean) lUserBean))
            	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        return companyKYCDocumentBeanMeta.formatAsJson(lCompanyKYCDocumentBean);
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);

        CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        if(lMap!=null && lMap.size() >0){
        	Object lTemp = lMap.get("cdId");
        	if(lTemp!=null)lCompanyDetailBean.setId(new Long(lTemp.toString()));
        	lTemp = lMap.get("isProvisional");
        	if(lTemp!=null)lCompanyDetailBean.setIsProvisional((boolean)lTemp);
        }
        if (AppConstants.DOMAIN_REGUSER.equals(lUserBean.getDomain())) {
        	lCompanyDetailBean.setId(lUserBean.getId());
        }
        //TODO: receive extra parameter isProvisional from frontend
        return new JsonBuilder(companyKYCDocumentBO.findList( pExecutionContext, lCompanyDetailBean, lUserBean)).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyKYCDocumentBean lCompanyKYCDocumentBean = new CompanyKYCDocumentBean();
        List<ValidationFailBean> lValidationFailBeans = companyKYCDocumentBeanMeta.validateAndParse(lCompanyKYCDocumentBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if(pNew) {
        	TredsHelper.getInstance().removeUnwantedValidation(lValidationFailBeans, "id");
        }
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) || 
        		AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain())){
           JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
            if(lMap!=null && lMap.size() >0){
            	Object lTemp = lMap.get("cdId");
            	if(lTemp!=null)lCompanyKYCDocumentBean.setCdId(new Long(lTemp.toString()));
            }
        }        
        pExecutionContext.setAutoCommit(false);
        //TODO: THIS SHOULD COME FROM FRONTEND
        lCompanyKYCDocumentBean.setIsProvisional(true);
        companyKYCDocumentBO.save(pExecutionContext, lCompanyKYCDocumentBean, lUserBean, pNew);
        pExecutionContext.commitAndDispose();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyKYCDocumentBean lFilterBean = new CompanyKYCDocumentBean();
        lFilterBean.setId(pId);
        //this will always be deletion from provisional
        companyKYCDocumentBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}