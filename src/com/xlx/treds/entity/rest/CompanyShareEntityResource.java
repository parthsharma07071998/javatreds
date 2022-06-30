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
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyShareEntityBean;
import com.xlx.treds.entity.bo.CompanyShareEntityBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/companyshareentity")
public class CompanyShareEntityResource {

    private CompanyShareEntityBO companyShareEntityBO;
    private BeanMeta companyShareEntityBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public CompanyShareEntityResource() {
        super();
        companyShareEntityBO = new CompanyShareEntityBO();
        companyShareEntityBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyShareEntityBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","companyName","constitution","companyDesc","line1","line2","line3","country","state","district","city","zipCode","salutation","firstName","middleName","lastName","email","telephone","mobile","fax","regNo","dateOfIncorporation","industry","subSegment","isProvisional"});
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
        pRequest.getRequestDispatcher("/WEB-INF/companyshareentity.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        CompanyShareEntityBean lFilterBean = new CompanyShareEntityBean();
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setId(pId);
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(pIsProvisional);
        CompanyShareEntityBean lCompanyShareEntityBean = companyShareEntityBO.findBean(pExecutionContext, lFilterBean);
        if(lCompanyShareEntityBean!=null){
            if(!TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), lCompanyShareEntityBean.getCdId(), (AppUserBean) lUserBean))
            	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        return companyShareEntityBeanMeta.formatAsJson(lCompanyShareEntityBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyShareEntityBean lFilterBean = new CompanyShareEntityBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        if(lMap!=null && lMap.size() >0){
        	Object lTemp = lMap.get("cdId");
        	if(lTemp!=null)lFilterBean.setCdId(new Long(lTemp.toString()));
        	lTemp = lMap.get("isProvisional");
        	if(lTemp!=null)lFilterBean.setIsProvisional((boolean)lTemp);
        }
        Map<String,Object> lRtnMap = new HashMap<>();
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lFilterBean.getCdId(),lFilterBean.getIsProvisional());
        if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        	lRtnMap.put("creatorIdentity",lCDBean.getCreatorIdentity());
        }
        List<CompanyShareEntityBean> lCompanyShareEntityList = companyShareEntityBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (CompanyShareEntityBean lCompanyShareEntityBean : lCompanyShareEntityList) {
        	if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
        		lCompanyShareEntityBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
        	}
        	lCompanyShareEntityBean.setIsProvisional(lFilterBean.getIsProvisional());
            lResults.add(companyShareEntityBeanMeta.formatAsMap(lCompanyShareEntityBean, null, null, true, true));
        }
        lRtnMap.put("list",lResults);
        return new JsonBuilder(lRtnMap).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        //TODO: receive extra parameter isProvisional from frontend
        CompanyShareEntityBean lFilterBean = new CompanyShareEntityBean();
        lFilterBean.setIsProvisional(false);
        List<CompanyShareEntityBean> lCompanyShareEntityList = companyShareEntityBO.findList(pExecutionContext, lFilterBean, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (CompanyShareEntityBean lCompanyShareEntityBean : lCompanyShareEntityList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyShareEntityBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lCompanyShareEntityBean.getCdId());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
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
        CompanyShareEntityBean lCompanyShareEntityBean = new CompanyShareEntityBean();
        List<ValidationFailBean> lValidationFailBeans = companyShareEntityBeanMeta.validateAndParse(lCompanyShareEntityBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT: BeanMeta.FIELDGROUP_UPDATE, null);
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
            	if(lTemp!=null)lCompanyShareEntityBean.setCdId(new Long(lTemp.toString()));
            }
        }
        pExecutionContext.setAutoCommit(false);
        //TODO: THIS SHOULD COME FROM FRONTEND
        lCompanyShareEntityBean.setIsProvisional(true);
        companyShareEntityBO.save(pExecutionContext, lCompanyShareEntityBean, lUserBean, pNew);
        pExecutionContext.commitAndDispose();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyShareEntityBean lFilterBean = new CompanyShareEntityBean();
        lFilterBean.setId(pId);
        //this will always be deletion from provisional
    	lFilterBean.setIsProvisional(true);
        companyShareEntityBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @GET
    //@Produces(MediaType.APPLICATION_JSON)
    @Path("/getConstitution")
    public String getConstitutionDesc(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
    		@QueryParam("loginId") String pLoginId , @QueryParam("id") Long pId , @QueryParam("domain") String pDomain) throws Exception {
    	Long lId = pId;
    	if(lId == null){
    		AppUserBean lAppUserBean = TredsHelper.getInstance().getAppUserBean(pDomain, pLoginId);
    		lId = lAppUserBean.getId();
    	}
        //TODO: receive extra parameter isProvisional from frontend
    	CompanyDetailBean lBean = TredsHelper.getInstance().getCompanyDetailsForShareEntity(pExecutionContext.getConnection(), lId);
    	if(lBean.getConstitution() != null)
			return new JsonBuilder(TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_CONSTITUTION, lBean.getConstitution())).toString();
		return  new JsonBuilder("").toString();
    }

}