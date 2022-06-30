package com.xlx.treds.monetago.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.InstAuthJerseyClient;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bo.CompanyLocationBO;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.monetago.bean.EwayBillDetailsBean;
import com.xlx.treds.monetago.bean.EwayInstrumentWrapperBean;
import com.xlx.treds.monetago.bean.GstnMandateBean;
import com.xlx.treds.monetago.bo.GstnMandateBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/gstmandate")
public class MonetagoGSTNVerificationResource {

    private GstnMandateBO gstnMandateBO;
    private CompanyLocationBO companyLocationBO;
	private List<String> defaultListFields, lovFields;
	private GenericDAO<InstrumentBean> instrumentDAO;
	
	
    public MonetagoGSTNVerificationResource() {
        super();
        gstnMandateBO = new GstnMandateBO();
        companyLocationBO =  new CompanyLocationBO(); 
        defaultListFields = Arrays.asList(new String[]{"id","billNumber","billYearMonth","billDate","entity","entName","entGstn","entPan","entLine1","entLine2","entLine3","entCountry","entState","entDistrict","entCity","entZipCode","entSalutation","entFirstName","entMiddleName","entLastName","entEmail","entTelephone","entMobile","entFax","tredsName","tredsGstn","tredsLine1","tredsLine2","tredsLine3","tredsCountry","tredsState","tredsDistrict","tredsCity","tredsZipCode","tredsEmail","tredsTelephone","tredsMobile","tredsFax","tredsPan","tredsCin","tredsNatureOfTrans","tredsSACCode","tredsSACDesc","amount","grossAmount","cgst","sgst","igst","cgstSurcharge","sgstSurcharge","igstSurcharge","cgstValue","sgstValue","igstValue"});
        lovFields = Arrays.asList(new String[]{"id","billNumber"});
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/gstmandate.jsp").forward(pRequest, pResponse);
    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/gstn")
//    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
//        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//        List<GstnMandateBean> lList = gstnMandateBO.getVerifiedGSTNList(pExecutionContext,lUserBean);
//        return new JsonBuilder(lList).toString();
//    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/gstn")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<GstnMandateBean> lList = null;
        try{
            lList = gstnMandateBO.getVerifiedGSTNList(pExecutionContext,lUserBean);
        }catch(Exception lEx){
        	throw new CommonBusinessException("Error while getting GSTN's : " + lEx.getMessage());
        }
        return new JsonBuilder(lList).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/purlov")
    public String listPurchaserMap(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = gstnMandateBO.findListForMap(pExecutionContext, lAppEntityBean);
        HashSet<String> lSet = new HashSet<String>();
        AppEntityBean lPurchaserEntityBean = null;
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
        	if(!lSet.contains(lPurchaserSupplierLinkBean.getPurchaser())){
        		 lSet.add(lPurchaserSupplierLinkBean.getPurchaser());
        		 Map<String, Object> lData = new HashMap<String, Object>();
        		 lPurchaserEntityBean = TredsHelper.getInstance().getAppEntityBean(lPurchaserSupplierLinkBean.getPurchaser());
                 lData.put(BeanFieldMeta.JSONKEY_VALUE, lPurchaserSupplierLinkBean.getPurchaser());
                 lData.put(BeanFieldMeta.JSONKEY_TEXT, lPurchaserSupplierLinkBean.getPurchaser());
                 lData.put(BeanFieldMeta.JSONKEY_DESC, lPurchaserEntityBean.getName());
                 lResults.add(lData);
        	}
        }
        return new JsonBuilder(lResults).toString();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/suplov")
    public String listSupplierMap(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = gstnMandateBO.findListForMap(pExecutionContext, lAppEntityBean);
        HashSet<String> lSet = new HashSet<String>();
        AppEntityBean lSupplierEntityBean = null;
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
        	if(!lSet.contains(lPurchaserSupplierLinkBean.getSupplier())){
        		 lSet.add(lPurchaserSupplierLinkBean.getSupplier());
        		 Map<String, Object> lData = new HashMap<String, Object>();
        		 lSupplierEntityBean = TredsHelper.getInstance().getAppEntityBean(lPurchaserSupplierLinkBean.getSupplier());
                 lData.put(BeanFieldMeta.JSONKEY_VALUE, lPurchaserSupplierLinkBean.getSupplier());
                 lData.put(BeanFieldMeta.JSONKEY_TEXT, lPurchaserSupplierLinkBean.getSupplier());
                 lData.put(BeanFieldMeta.JSONKEY_DESC, lSupplierEntityBean.getName());
                 lResults.add(lData);
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
         //get mandate list only for suppliers 
         List<GstnMandateBean> lMandateList = new ArrayList<GstnMandateBean>();
         Set<String> lSet = new HashSet<String>();
         if (lAppEntityBean.isSupplier()){
        	 GstnMandateBean lSupGstnMandateBean = new GstnMandateBean();
        	 lSupGstnMandateBean.setSupplierCode(lAppEntityBean.getCode());
        	 lMandateList = gstnMandateBO.findList(pExecutionContext, lSupGstnMandateBean, null, null);
        	 for (GstnMandateBean lBean : lMandateList){
        		 lSet.add(lBean.getGstn());
        	 }
         }
         List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
         String lName = null;
         List<CompanyLocationBean> lCompanyLocationList = companyLocationBO.findActiveList(pExecutionContext, lFilterBean, lovFields, lUserBean, pActiveOnly);
         if(lCompanyLocationList!=null){
         for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationList) {
        	 // skip only if not present in mandate list
        	 if (lAppEntityBean.isSupplier()){
        		 if (!lSet.contains(lCompanyLocationBean.getGstn())){
        			 continue;
        		 }
        	 }
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
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey="inst-addverifiedinst")
    @Path("/addInst")
    public String addInst(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String,Object> lDataHash = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
		Connection lConnection = pExecutionContext.getConnection();
		EwayBillDetailsBean lEwayBillDetailsBean = null;
		InstrumentBean lInstFilterBean = new InstrumentBean();
		lInstFilterBean.setInstNumber(lDataHash.get("instNumber").toString());
		lInstFilterBean.setEwayBillNo(lDataHash.get("eway").toString());
		InstrumentBean lInstBean = instrumentDAO.findBean(lConnection, lInstFilterBean);
		String lMonetagoMessage = "No Data Found. ";
		if (lInstBean != null){
			throw new CommonBusinessException("Invoice Addition Failed. Instrument already added please check.");
		}
		lEwayBillDetailsBean = gstnMandateBO.getInfoFromDb(lConnection,lDataHash);
		if(lEwayBillDetailsBean == null){
			try {
				if( gstnMandateBO.uploadInstrumentToMonetago(pExecutionContext,pMessage,lUserBean) ){
					lEwayBillDetailsBean = gstnMandateBO.getInfoFromDb(lConnection,lDataHash);
				}
			}catch(Exception e) {
				lMonetagoMessage += e.getMessage();
			}
		}
		if(lEwayBillDetailsBean !=null ){
	        String lLoginKey = AuthenticationHandler.getInstance().getLoginKey(pRequest);
			return gstnMandateBO.getDataFromMonetago(pExecutionContext,lEwayBillDetailsBean,lLoginKey);
		}else{
			throw new CommonBusinessException(lMonetagoMessage);
		}
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/instrumentinvoice/{id}")
    public void taxInvoiceHtml(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, @PathParam("id") Long pId) throws Exception {
        EwayInstrumentWrapperBean lBean = new EwayInstrumentWrapperBean(pId);
        pRequest.setAttribute("data",lBean);
        pRequest.getRequestDispatcher("/WEB-INF/instrumentinvoicehtml.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/addSupplier")
    public void addSupplier(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
        CompanyLocationBean lCompanyLocation = new CompanyLocationBean();
        String lGstn = lMap.get("gstn").toString();
        lCompanyLocation.setGstn(lGstn);
        ArrayList<String> lEmails = (ArrayList<String>) lMap.get("emails");
        if (lEmails.size()!=3){
        	throw new CommonBusinessException(" 3 emailIds required.");
        }
        List<CompanyLocationBean> lList = companyLocationBO.findList(pExecutionContext, lCompanyLocation, null, lUserBean);
        //
        //
        if( InstAuthJerseyClient.getInstance().registerSupplier(pExecutionContext.getConnection(),lGstn, lEmails,lUserBean) ){
        	
        }        
    }
    
    
}