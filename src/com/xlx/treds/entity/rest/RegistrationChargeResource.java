
package com.xlx.treds.entity.rest;

import java.sql.Connection;
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
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.report.ReportConvertorFactory;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.RegistrationChargeBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean.ApprovalStatus;
import com.xlx.treds.entity.bean.RegistrationChargeBean.RequestType;
import com.xlx.treds.entity.bo.RegistrationChargeBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/regchrg")
@Singleton
public class RegistrationChargeResource {

	public static final Logger logger = LoggerFactory.getLogger(RegistrationChargeResource.class);

    private RegistrationChargeBO registrationChargeBO;
    private BeanMeta registrationChargeBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public RegistrationChargeResource() {
        super();
        registrationChargeBO = new RegistrationChargeBO();
        registrationChargeBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(RegistrationChargeBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","entityCode","entityType","chargeType","effectiveDate","chargeAmount","requestType","extendedDate","extensionCount","paymentDate","paymentAmount","paymentRefrence","billedEntityCode","billedEntityClId","remarks","supportingDoc","makerAuId","makerTimestamp","checkerAuId","checkerTimestamp","approvalStatus"});
        lovFields = Arrays.asList(new String[]{"id","entityCode"});
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
        pRequest.getRequestDispatcher("/WEB-INF/registrationcharge.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrg-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        RegistrationChargeBean lFilterBean = new RegistrationChargeBean();
        lFilterBean.setId(pId);
        RegistrationChargeBean lRegistrationChargeBean = registrationChargeBO.findBean(pExecutionContext, lFilterBean);
        return registrationChargeBeanMeta.formatAsJson(lRegistrationChargeBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrg-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        RegistrationChargeBean lFilterBean = new RegistrationChargeBean();
        registrationChargeBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<RegistrationChargeBean> lRegistrationChargeList = registrationChargeBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (RegistrationChargeBean lRegistrationChargeBean : lRegistrationChargeList) {
            lResults.add(registrationChargeBeanMeta.formatAsArray(lRegistrationChargeBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey = "regchrg-view")
    @Path("/download/{format}")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter, @PathParam("format") ReportConvertorFactory.DownloadFormat pFormat) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        RegistrationChargeBean lFilterBean = new RegistrationChargeBean();
        registrationChargeBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<RegistrationChargeBean> lRegistrationChargeList = registrationChargeBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        FileDownloadBean lFileDownloadBean = ReportConvertorFactory.getInstance().convertData(registrationChargeBeanMeta.formatBeansAsList(lRegistrationChargeList, null, 
        		lFields, true), "RegistrationCharge", registrationChargeBeanMeta.getFieldLabelList(null, lFields).toArray(new String[0]), pFormat);
        
        return lFileDownloadBean.getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<RegistrationChargeBean> lRegistrationChargeList = registrationChargeBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (RegistrationChargeBean lRegistrationChargeBean : lRegistrationChargeList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lRegistrationChargeBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lRegistrationChargeBean.getEntityCode());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = {"regchrg-manage","regchrg-save"})
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = {"regchrg-manage","regchrg-save"})
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        RegistrationChargeBean lRegistrationChargeBean = new RegistrationChargeBean();
        List<ValidationFailBean> lValidationFailBeans = registrationChargeBeanMeta.validateAndParse(lRegistrationChargeBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        //registrationChargeBO.save(pExecutionContext, lRegistrationChargeBean, lUserBean, pNew);
        registrationChargeBO.saveMakerChanges(pExecutionContext.getConnection(), lRegistrationChargeBean, lUserBean);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = {"regchrg-manage","regchrg-save"})
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        RegistrationChargeBean lFilterBean = new RegistrationChargeBean();
        lFilterBean.setId(pId);
        registrationChargeBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrg-submit")
    @Path("/submit/{id}")
    public void submit(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        RegistrationChargeBean lFilterBean = new RegistrationChargeBean();
        lFilterBean.setId(pId);
        lFilterBean = registrationChargeBO.findBean(pExecutionContext, lFilterBean);
        lFilterBean.setApprovalStatus(ApprovalStatus.Pending);
        lFilterBean.setPrevExtendedDate(lFilterBean.getExtendedDate());
        registrationChargeBO.saveMakerChanges(pExecutionContext.getConnection(), lFilterBean, lUserBean);
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "regchrg-reext")
    @Path("/reextend/{id}")
    public void reExtend(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        registrationChargeBO.reExtend(pExecutionContext.getConnection(), pId, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = {"regchrg-approve","regchrg-return"})
    @Path("/checker/{id}/{status}")
    public void checker(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId, @PathParam("status") String pStatus) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        RegistrationChargeBean lFilterBean = new RegistrationChargeBean();
        if (pId==null || pStatus == null) {
        	// throw error
        }
        lFilterBean.setId(pId);
        lFilterBean = registrationChargeBO.findBean(pExecutionContext, lFilterBean);
        if (ApprovalStatus.Returned.getCode().equals(pStatus) ) {
        	lFilterBean.setApprovalStatus(ApprovalStatus.Returned);
        }else if (ApprovalStatus.Approved.getCode().equals(pStatus) ){
        	lFilterBean.setApprovalStatus(ApprovalStatus.Approved);
        }
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        registrationChargeBO.checkerApprovesChanges(lConnection, lFilterBean, lMemoryDBConnection, lUserBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createregchrg")
    public String createRegistrationCharge(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
    	try {
    		if(!lMap.containsKey("code")) {
    			throw new CommonBusinessException("Entity code not recived.");
    		}
            registrationChargeBO.createRegistrationCharge(pExecutionContext.getConnection(), lMap.get("code").toString(), lUserBean);
    	}catch(Exception lEx) {
    		throw new CommonBusinessException(lEx.getMessage());
    	}
    	Map<String,String> lRetMsg = new HashMap<String,String>();
    	lRetMsg.put("message", "Registration charge created on enity.");
    	return new JsonBuilder(lRetMsg).toString();
   }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createannchrg")
    public String createAnnualCharge(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        RequestType lRequestType = null;
    	try {
    		if(!lMap.containsKey("code")) {
    			throw new CommonBusinessException("Entity code not recived.");
    		}
    		if(lMap.containsKey("requestType")) {
    			lRequestType = (RequestType) TredsHelper.getInstance().getValue( RegistrationChargeBean.class, "requestType", (String) lMap.get("requestType"));
    		}
            registrationChargeBO.createAnnualFeesCharge(pExecutionContext.getConnection(), lMap.get("code").toString(), lUserBean);
    	}catch(Exception lEx) {
    		throw new CommonBusinessException(lEx.getMessage());
    	}
    	Map<String,String> lRetMsg = new HashMap<String,String>();
    	lRetMsg.put("message", "Annual charge created on enity.");
        return new JsonBuilder(lRetMsg).toString();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createchrg")
    public String createChargeFromExtension(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        RequestType lRequestType = null;
    	try {
    		if(!lMap.containsKey("code")) {
    			throw new CommonBusinessException("Entity code not recived.");
    		}
    		if(!lMap.containsKey("id")) {
    			throw new CommonBusinessException("Extension id not recived for creating charge.");
    		}
    		if(lMap.containsKey("requestType")) {
    			lRequestType = (RequestType) TredsHelper.getInstance().getValue( RegistrationChargeBean.class, "requestType", (String) lMap.get("requestType"));
    		}
            registrationChargeBO.createChargeFromExtension(pExecutionContext.getConnection(), lMap.get("code").toString(), new Long(lMap.get("id").toString()),lRequestType , lUserBean);
    	}catch(Exception lEx) {
    		throw new CommonBusinessException(lEx.getMessage());
    	}
    	Map<String,String> lRetMsg = new HashMap<String,String>();
    	lRetMsg.put("message", "Annual charge created on enity.");
        return new JsonBuilder(lRetMsg).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/history")
    public String history(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
    	Map<String,Object> lRetMsg = new HashMap<String,Object>();
    	try {
    		if(!lMap.containsKey("code")) {
    			throw new CommonBusinessException("Entity code not recived.");
    		}
            List<RegistrationChargeBean> lRegChrgBeans = registrationChargeBO.getRegistrationData(pExecutionContext.getConnection(), (String)lMap.get("code"), lUserBean);
            if(lRegChrgBeans!=null && lRegChrgBeans.size() > 0) {
                GenericDAO<RegistrationChargeBean> lRegChargeDAO = new GenericDAO<RegistrationChargeBean>(RegistrationChargeBean.class);
                List<Map<String, Object>> lRCMaps = new ArrayList<Map<String,Object>>();
            	for(RegistrationChargeBean lRCBean : lRegChrgBeans) {
            		lRCMaps.add(lRegChargeDAO.getBeanMeta().formatAsMap(lRCBean, null, null, true, true));
            	}
            	lRetMsg.put("regAnnFees", lRCMaps);
            }
    	}catch(Exception lEx) {
    		throw new CommonBusinessException(lEx.getMessage());
    	}
        return new JsonBuilder(lRetMsg).toString();
    }
}