
package com.xlx.treds.instrument.rest;

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

import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.instrument.bean.InstrumentCreationKeysBean;
import com.xlx.treds.instrument.bean.MemberLocationForInstKeysBean;
import com.xlx.treds.instrument.bo.InstrumentCreationKeysBO;
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

@Path("/instrumentcreationkeys")
@Singleton
public class InstrumentCreationKeysResource {

	public static final Logger logger = LoggerFactory.getLogger(InstrumentCreationKeysResource.class);

    private InstrumentCreationKeysBO instrumentCreationKeysBO;
    private BeanMeta instrumentCreationKeysBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public InstrumentCreationKeysResource() {
        super();
        instrumentCreationKeysBO = new InstrumentCreationKeysBO();
        instrumentCreationKeysBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentCreationKeysBean.class);
        defaultListFields = Arrays.asList(new String[]{"refType","refDate","refNo","poNumber","slNumber","purchaserCode","internalVendorRefNo","supplierCode","supplierGstn","inId"});
        lovFields = Arrays.asList(new String[]{"refType","refDate"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("refNo") String pRefNo, @QueryParam("poNumber") String pPoNumber, @QueryParam("slNumber") String pSlNumber, @QueryParam("supplierCode") String pSupplierCode) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pRefNo != null) && (pPoNumber != null) && (pSlNumber != null) && (pSupplierCode != null)) {
            Object[] lKey = new Object[]{pRefNo, pPoNumber, pSlNumber, pSupplierCode};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/instrumentcreationkeys.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "instrumentcreationkeys-view")
    @Path("/{refNo}/{poNumber}/{slNumber}/{supplierCode}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("refNo") String pRefNo, @PathParam("poNumber") String pPoNumber, @PathParam("siNumber") String pSiNumber, @PathParam("supplierCode") String pSupplierCode) throws Exception {
        InstrumentCreationKeysBean lFilterBean = new InstrumentCreationKeysBean();
        lFilterBean.setRefNo(pRefNo);
        lFilterBean.setPoNumber(pPoNumber);
        lFilterBean.setSiNumber(pSiNumber);
        lFilterBean.setSupplierCode(pSupplierCode);
        InstrumentCreationKeysBean lInstrumentCreationKeysBean = instrumentCreationKeysBO.findBean(pExecutionContext, lFilterBean);
        return instrumentCreationKeysBeanMeta.formatAsJson(lInstrumentCreationKeysBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "instrumentcreationkeys-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentCreationKeysBean lFilterBean = new InstrumentCreationKeysBean();
        instrumentCreationKeysBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentCreationKeysBean> lInstrumentCreationKeysList = instrumentCreationKeysBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (InstrumentCreationKeysBean lInstrumentCreationKeysBean : lInstrumentCreationKeysList) {
            lResults.add(instrumentCreationKeysBeanMeta.formatAsArray(lInstrumentCreationKeysBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey = "instrumentcreationkeys-view")
    @Path("/download/{format}")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter, @PathParam("format") ReportConvertorFactory.DownloadFormat pFormat) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        InstrumentCreationKeysBean lFilterBean = new InstrumentCreationKeysBean();
        instrumentCreationKeysBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<InstrumentCreationKeysBean> lInstrumentCreationKeysList = instrumentCreationKeysBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        FileDownloadBean lFileDownloadBean = ReportConvertorFactory.getInstance().convertData(instrumentCreationKeysBeanMeta.formatBeansAsList(lInstrumentCreationKeysList, null, 
        		lFields, true), "InstrumentCreationKeys", instrumentCreationKeysBeanMeta.getFieldLabelList(null, lFields).toArray(new String[0]), pFormat);
        
        return lFileDownloadBean.getResponseForSendFile();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/lov")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    	String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<InstrumentCreationKeysBean> lInstrumentCreationKeysList = null;
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
        InstrumentCreationKeysBean lInstCreationKeysBean = new InstrumentCreationKeysBean();
        instrumentCreationKeysBeanMeta.validateAndParse(lInstCreationKeysBean, lMap, null, null);
        Long lSupClid = null;
        if (lMap.containsKey("supClId") && lMap.get("supClId")!=null) {
        	lSupClid = new Long(lMap.get("supClId").toString());
        }
        lInstrumentCreationKeysList = instrumentCreationKeysBO.getInstCreationList(pExecutionContext.getConnection(), lInstCreationKeysBean.getPurchaserCode(), lInstCreationKeysBean.getSupplierCode(), lInstCreationKeysBean.getInId(),lSupClid);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (InstrumentCreationKeysBean lInstrumentCreationKeysBean : lInstrumentCreationKeysList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_TEXT,lInstrumentCreationKeysBean.getKeyView() );
            lData.put(BeanFieldMeta.JSONKEY_VALUE,lInstrumentCreationKeysBean.getKey() );
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Secured
    @Path("/hasaccess")
    public boolean hasAccessToLocation(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    	String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
        if (TredsHelper.getInstance().supportsInstrumentKeys(lMap.get("purchaserCode").toString())) {
        	return TredsHelper.getInstance().locationSupportsInstrumentKeys(pExecutionContext.getConnection(),
            		lMap.get("purchaserCode").toString(), new Long(lMap.get("purClId").toString()), null);
        }
        return false;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "instrumentcreationkeys-manage")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "instrumentcreationkeys-manage")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        InstrumentCreationKeysBean lInstrumentCreationKeysBean = new InstrumentCreationKeysBean();
        List<ValidationFailBean> lValidationFailBeans = instrumentCreationKeysBeanMeta.validateAndParse(lInstrumentCreationKeysBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        instrumentCreationKeysBO.save(pExecutionContext, lInstrumentCreationKeysBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "instrumentcreationkeys-manage")
    @Path("/{refNo}/{poNumber}/{siNumber}/{supplierCode}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("refNo") String pRefNo, @PathParam("poNumber") String pPoNumber, @PathParam("siNumber") String pSiNumber, @PathParam("supplierCode") String pSupplierCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        InstrumentCreationKeysBean lFilterBean = new InstrumentCreationKeysBean();
        lFilterBean.setRefNo(pRefNo);
        lFilterBean.setPoNumber(pPoNumber);
        lFilterBean.setSiNumber(pSiNumber);
        lFilterBean.setSupplierCode(pSupplierCode);
        instrumentCreationKeysBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}