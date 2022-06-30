package com.xlx.treds.entity.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.lang.StringUtils;

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
import com.xlx.treds.entity.bean.CompanyShareIndividualBean;
import com.xlx.treds.entity.bo.CompanyShareIndividualBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/companyshareindividual")
public class CompanyShareIndividualResource {

    private CompanyShareIndividualBO companyShareIndividualBO;
    private BeanMeta companyShareIndividualBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public CompanyShareIndividualResource() {
        super();
        companyShareIndividualBO = new CompanyShareIndividualBO();
        companyShareIndividualBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyShareIndividualBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","salutation","firstName","middleName","lastName","email","telephone","mobile","fax","DOB","designation","familySalutation","familyFirstName","familyMiddleName","familyLastName","isProvisional"});
        lovFields = Arrays.asList(new String[]{"id"});
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
        pRequest.getRequestDispatcher("/WEB-INF/companyshareindividual.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        CompanyShareIndividualBean lFilterBean = new CompanyShareIndividualBean();
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setId(pId);
        lFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(lUserBean));
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(pIsProvisional);
        CompanyShareIndividualBean lCompanyShareIndividualBean = companyShareIndividualBO.findBean(pExecutionContext, lFilterBean);
        if(lCompanyShareIndividualBean!=null){
            if(!TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), lCompanyShareIndividualBean.getCdId(), (AppUserBean) lUserBean))
            	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        return companyShareIndividualBeanMeta.formatAsJson(lCompanyShareIndividualBean);
        
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyShareIndividualBean lFilterBean = new CompanyShareIndividualBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        if(lMap!=null && lMap.size() >0){
        	Object lTemp = lMap.get("cdId");
        	if(lTemp!=null)lFilterBean.setCdId(new Long(lTemp.toString()));
        	lTemp = lMap.get("isProvisional");
        	if(lTemp!=null)lFilterBean.setIsProvisional((boolean)lTemp);
        }
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        Map<String,Object> lRtnMap = new HashMap<>();
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lFilterBean.getCdId(),lFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lRtnMap.put("creatorIdentity",lCDBean.getCreatorIdentity());
        }
        List<CompanyShareIndividualBean> lCompanyShareIndividualList = companyShareIndividualBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (CompanyShareIndividualBean lCompanyShareIndividualBean : lCompanyShareIndividualList) {
        	if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        		lCompanyShareIndividualBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        	}
        	lCompanyShareIndividualBean.setIsProvisional(lFilterBean.getIsProvisional());
            lResults.add(companyShareIndividualBeanMeta.formatAsMap(lCompanyShareIndividualBean, null, null, true, true));
        }
        lRtnMap.put("list",lResults);
        return new JsonBuilder(lRtnMap).toString();
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
         CompanyShareIndividualBean lCompanyShareIndividualBean = new CompanyShareIndividualBean();
         List<ValidationFailBean> lValidationFailBeans = companyShareIndividualBeanMeta.validateAndParse(lCompanyShareIndividualBean, 
             pMessage, pNew ? lCompanyShareIndividualBean.FIELDGROUP_INSERTCOMPANYSHAREINDIVIDUAL : lCompanyShareIndividualBean.FIELDGROUP_UPDATECOMPANYSHAREINDIVIDUAL, null);
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
             	if(lTemp!=null)lCompanyShareIndividualBean.setCdId(new Long(lTemp.toString()));
             }
         }
    	 pExecutionContext.setAutoCommit(false);
    	 //TODO: this should come from frontend
    	 lCompanyShareIndividualBean.setIsProvisional(true);//
         companyShareIndividualBO.save(pExecutionContext, lCompanyShareIndividualBean, lUserBean, pNew);
         pExecutionContext.commitAndDispose();
       
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyShareIndividualBean lFilterBean = new CompanyShareIndividualBean();
        lFilterBean.setId(pId);
        //this will always be deletion from provisional
    	lFilterBean.setIsProvisional(true);
        companyShareIndividualBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}