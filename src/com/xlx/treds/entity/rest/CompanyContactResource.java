package com.xlx.treds.entity.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyContactBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bo.CompanyContactBO;
import com.xlx.treds.entity.bo.CompanyDetailBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/companycontact")
@Singleton
public class CompanyContactResource {

    private CompanyContactBO companyContactBO;
    private BeanMeta companyContactBeanMeta;
	private List<String> defaultListFields;
	private String defaultListDBColumns;
	
    public CompanyContactResource() {
        super();
        companyContactBO = new CompanyContactBO();
        companyContactBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyContactBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","cdId","salutation","firstName","middleName","lastName","designation","email","telephone","mobile","fax","admin","isProvisional"});
        defaultListDBColumns = "CCId,CCCdId,CCSalutation,CCFirstName,CCMiddleName,CCLastName,CCDesignation,CCEmail,CCTelephone,CCMobile,CCFax,CCAdmin";
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
        pRequest.getRequestDispatcher("/WEB-INF/companycontact.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        CompanyContactBean lFilterBean = new CompanyContactBean();
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setId(pId);
        lFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(lUserBean));
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(pIsProvisional);
        CompanyContactBean lCompanyContactBean = companyContactBO.findBean(pExecutionContext, lFilterBean);
        //check entity after getting the contact through primary key - the CDID returned for REGENTITY will always be null
		//hence checking the contact creator after fetching
        if(lCompanyContactBean!=null){
            if(!TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), lCompanyContactBean.getCdId(), (AppUserBean) lUserBean))
            	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        return companyContactBeanMeta.formatAsJson(lCompanyContactBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyContactBean lFilterBean = new CompanyContactBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        if(lMap!=null && lMap.size() >0){
        	Object lTemp = lMap.get("cdId");
        	if(lTemp!=null)lFilterBean.setCdId(new Long(lTemp.toString()));
        	lTemp = lMap.get("isProvisional");
        	if(lTemp!=null)lFilterBean.setIsProvisional((boolean)lTemp);
        }
        Map<String,Object> lRtnMap = new HashMap<>();
        //TODO: receive extra parameter isProvisional from frontend
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lFilterBean.getCdId(), lFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lRtnMap.put("creatorIdentity",lCDBean.getCreatorIdentity());
        }
        List<CompanyContactBean> lCompanyContactList = companyContactBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (CompanyContactBean lCompanyContactBean : lCompanyContactList) {
        	if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        		lCompanyContactBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        	}
    		lCompanyContactBean.setIsProvisional(lFilterBean.getIsProvisional());
            lResults.add(companyContactBeanMeta.formatAsMap(lCompanyContactBean, null, null, true, true));
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
        CompanyContactBean lCompanyContactBean = new CompanyContactBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        companyContactBeanMeta.validateAndParse(lCompanyContactBean, lMap, null, null);
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) || 
        		AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain())){
            if(lMap!=null && lMap.size() >0){
            	Object lTemp = lMap.get("cdId");
            	if(lTemp!=null)lCompanyContactBean.setCdId(new Long(lTemp.toString()));
            }
        }
        pExecutionContext.setAutoCommit(false);
        //TODO: THIS SHOULD COME FROM FRONTEND
        lCompanyContactBean.setIsProvisional(true);
        companyContactBO.save(pExecutionContext, lCompanyContactBean, lUserBean, pNew,lMap);
        pExecutionContext.commit();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyContactBean lFilterBean = new CompanyContactBean();
    	lFilterBean.setId(pId);
        //this will always be deletion from provisional
    	lFilterBean.setIsProvisional(true);
        companyContactBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/designation")
    public String subSegment(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	String lConstitutionCode = null;
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyDetailBO lCompanyDetailBO = new CompanyDetailBO();
        CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
        lCompanyDetailBean.setId(lUserBean.getId());//first use as filter 
        //TODO: receive extra parameter isProvisional from frontend
        lCompanyDetailBean = lCompanyDetailBO.findBean(pExecutionContext, lCompanyDetailBean, lUserBean, true);
    	if(lCompanyDetailBean != null)
    		lConstitutionCode = lCompanyDetailBean.getConstitution();
    	else
    		throw new CommonBusinessException("Company Details not found.");
        return OtherResourceCache.getInstance().getDesignationJson(lConstitutionCode);
    }
    
}