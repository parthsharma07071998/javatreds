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

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bo.CompanyLocationBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/companylocation")
@Singleton
public class CompanyLocationResource {

    private CompanyLocationBO companyLocationBO;
    private BeanMeta companyLocationBeanMeta;
	private List<String> defaultListFields, lovFields;
    private GenericDAO<CompanyDetailBean> companyDetailDAO;
    //
    public CompanyLocationResource() {
        super();
        companyLocationBO = new CompanyLocationBO();
        companyLocationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyLocationBean.class);
        defaultListFields = null; //Arrays.asList(new String[]{"id","name","line1","line2","line3","country","state","city","zipCode"});
        lovFields = Arrays.asList(new String[]{"id","name","state","gstn"});
        companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);
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
        pRequest.getRequestDispatcher("/WEB-INF/companylocation.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setId(pId);
        lFilterBean.setCdId(TredsHelper.getInstance().getCompanyId(lUserBean));
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(pIsProvisional);
        CompanyLocationBean lCompanyLocationBean = companyLocationBO.findBean(pExecutionContext, lFilterBean);
        if(lCompanyLocationBean!=null){
            if(!TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), lCompanyLocationBean.getCdId(), (AppUserBean) lUserBean))
            	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        return companyLocationBeanMeta.formatAsJson(lCompanyLocationBean);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/regOff/{cdId}")
    public String getRegOffice(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("cdId") Long pCdId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Long lCdId = pCdId;
        if(lCdId==null||lCdId.longValue()==0)
        	lCdId = TredsHelper.getInstance().getCompanyId(lUserBean);
        //TODO: receive extra parameter isProvisional from frontend
        //TODO: CHAGE THE FUNCTION TO RECEIVED FROM PROVISIONAL OR ACTUAL
        CompanyLocationBean lCompanyLocationBean = TredsHelper.getInstance().getRegisteredOfficeLocation(pExecutionContext.getConnection(), lCdId, true);
        return companyLocationBeanMeta.formatAsJson(lCompanyLocationBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	boolean lDesciption = true;
    	boolean lSendSettlementFlag = false;
    	boolean lProvFlag = false;
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        companyLocationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        
        if(lMap!=null && lMap.size() >0){
        	//cdId is nonjson true hence this is done
        	Object lTemp = lMap.get("cdId");
        	if (lMap.get("descr")!=null) lDesciption = false;
        	if(lTemp!=null && Integer.parseInt(lTemp.toString()) > 0)lFilterBean.setCdId(new Long(lTemp.toString()));
        	lTemp = lMap.get("recordVersion");
        	if(lTemp!=null) lSendSettlementFlag = (Integer.parseInt(lTemp.toString())==1) ?Boolean.TRUE.booleanValue():Boolean.FALSE.booleanValue();
        	lFilterBean.setRecordVersion(null);
        	lTemp = lMap.get("isProvisional");
        	if(lTemp!=null)lFilterBean.setIsProvisional((boolean)lTemp);
        	lProvFlag = (boolean)lTemp;
        }
        List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
        CompanyDetailBean lCDBean = TredsHelper.getInstance().getCompanyDetails(pExecutionContext.getConnection(), lFilterBean.getCdId(), lFilterBean.getIsProvisional());
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        if(lCompanyLocationList!=null){
            for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList) {
            	if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
            		lCompanyLocationBean.setCreatorIdentity(lCDBean.getCreatorIdentity());
            	}
            	lCompanyLocationBean.setIsProvisional(lFilterBean.getIsProvisional());
                lResults.add(companyLocationBeanMeta.formatAsMap(lCompanyLocationBean, null, defaultListFields, lDesciption, true));
            }
        }
        Object lFinal  = new ArrayList<Map<String, Object>>();
        if(lSendSettlementFlag){
        	lFinal = new HashMap<String, Object>();
            ((HashMap<String, Object>) lFinal).put("data",lResults);
            if (lCDBean!=null && StringUtils.isNotEmpty(lCDBean.getCreatorIdentity())) {
            	((HashMap<String, Object>) lFinal).put("creatorIdentity",lCDBean.getCreatorIdentity());
            }
            Long lCdId = null;
            if(lFilterBean.getCdId() !=null && lFilterBean.getCdId().longValue() > 0){
            	lCdId = lFilterBean.getCdId();
            }else{
                lCdId = TredsHelper.getInstance().getCompanyId(lUserBean); 
            }
            ((HashMap<String, Object>) lFinal).put("settleEnabled", TredsHelper.getInstance().isLocationwiseSettlementEnabled(pExecutionContext.getConnection(), lCdId,(boolean)lProvFlag));
            ((HashMap<String, Object>) lFinal).put("cdId", lCdId);
        }else{
        	lFinal = lResults;
        }
        return new JsonBuilder(lFinal).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, @QueryParam("aecode") String pAECode) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        
        AppEntityBean lAppEntityBean =  null;
        if(StringUtils.isNotEmpty(pAECode)) {
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pAECode);
        }else {
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
        }
        if (lAppEntityBean == null)
            throw new CommonBusinessException("Entity details not found for the loggedin user");
        lFilterBean.setCdId(lAppEntityBean.getCdId());
       
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        String lName = null;

        //TODO: receive extra parameter isProvisional from frontend
        lFilterBean.setIsProvisional(false);
        List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findList(pExecutionContext, lFilterBean, lovFields, lUserBean);
        
        if(lCompanyLocationList!=null){
            for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList) {
                Map<String, Object> lData = new HashMap<String, Object>();
            	lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyLocationBean.getId());
                lData.put(BeanFieldMeta.JSONKEY_DESC, lCompanyLocationBean.getGstn());
                lName = TredsHelper.getInstance().getGSTStateDesc(lCompanyLocationBean.getState());
                lName += " [" + lCompanyLocationBean.getName() + "] ";
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lName);
                lResults.add(lData);
            }
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
        CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
        List<ValidationFailBean> lValidationFailBeans = companyLocationBeanMeta.validateAndParse(lCompanyLocationBean, 
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
            	if(lTemp!=null)lCompanyLocationBean.setCdId(new Long(lTemp.toString()));
            }
        }
        pExecutionContext.setAutoCommit(false);
	   	 //TODO: this should come from frontend
        lCompanyLocationBean.setIsProvisional(true);//
        companyLocationBO.save(pExecutionContext, lCompanyLocationBean, lUserBean, pNew);
        pExecutionContext.commitAndDispose();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        lFilterBean.setId(pId);
        //this will always be deletion from provisional
    	lFilterBean.setIsProvisional(true);
        companyLocationBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
   

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/settlelov")
    public String settlementLocationlov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, @QueryParam("cdId") Long pCdId, @QueryParam("clId") Long pClId , @QueryParam("isProv") boolean pIsProvisional) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        Long lCdId = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, 
                new String[]{lUserBean.getDomain()});
        if(lAppEntityBean.isPlatform() ||
                        (lAppEntityBean.isRegistringEntity() && 
                                        TredsHelper.getInstance().hasAccessOnCompany(pExecutionContext.getConnection(), pCdId, lUserBean))){
                lCdId  = pCdId;
        }else{
                lCdId=TredsHelper.getInstance().getCompanyId(lUserBean);
        }
        //
        if(lCdId != null && lCdId.longValue() > 0 ){
            String lName = null;
            lFilterBean.setCdId(lCdId);
            lFilterBean.setEnableSettlement(Yes.Yes);
            //TODO: receive extra parameter isProvisional from frontend
            lFilterBean.setIsProvisional(pIsProvisional);
            List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findList(pExecutionContext, lFilterBean, lovFields, lUserBean);
            if(lCompanyLocationList!=null){
            for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList){
                    if(pClId!=null && pClId.equals(lCompanyLocationBean.getId()))
                            continue;
                Map<String, Object> lData = new HashMap<String, Object>();
                lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyLocationBean.getId());
                lData.put(BeanFieldMeta.JSONKEY_DESC, lCompanyLocationBean.getGstn());
                lName = lCompanyLocationBean.getName();
                lName += " [" + TredsHelper.getInstance().getGSTStateDesc(lCompanyLocationBean.getState()) + "] "; 
                lData.put(BeanFieldMeta.JSONKEY_TEXT, lName);
                lResults.add(lData);  
            }
            }
        }
        return new JsonBuilder(lResults).toString();
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/settleactivelov")
    public String settlementLocationActivelov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, @QueryParam("aecode") String pAECode,@QueryParam("cbdId") Long pCbdId,@QueryParam("activeOnly") boolean pActiveOnly) throws Exception {
    	 AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
         CompanyLocationBean lFilterBean = new CompanyLocationBean();
         
         MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
         AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, 
                 new String[]{StringUtils.isBlank(pAECode)?lUserBean.getDomain():pAECode});
         if (lAppEntityBean == null)
             throw new CommonBusinessException("Entity details not found for the loggedin user");
         lFilterBean.setCdId(lAppEntityBean.getCdId());
         //
         if (pCbdId!=null){
        	 lFilterBean.setCbdId(pCbdId);
         }
         //
         List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
         String lName = null;
         //TODO: Filter to send parameter isProvisional 
         lFilterBean.setIsProvisional(false);
         List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findActiveList(pExecutionContext, lFilterBean, lovFields, lUserBean, pActiveOnly);
         if(lCompanyLocationList!=null){
         for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList) {
             Map<String, Object> lData = new HashMap<String, Object>();
         	lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyLocationBean.getId());
             lData.put(BeanFieldMeta.JSONKEY_DESC, lCompanyLocationBean.getGstn());
             lName = TredsHelper.getInstance().getGSTStateDesc(lCompanyLocationBean.getState());
             lName += " [" + lCompanyLocationBean.getName() + "] ";
             lData.put(BeanFieldMeta.JSONKEY_TEXT, lName);
             lResults.add(lData);
         }
         }
         return new JsonBuilder(lResults).toString();

    }
}