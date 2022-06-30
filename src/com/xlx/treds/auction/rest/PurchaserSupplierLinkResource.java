package com.xlx.treds.auction.rest;

import java.math.BigDecimal;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.auction.bean.OutsideSettlementReqBean.Status;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.ApprovalStatus;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkWorkFlowBean;
import com.xlx.treds.auction.bo.PurchaserSupplierLinkBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/pursuplnk")
public class PurchaserSupplierLinkResource {
	

    private static Logger logger = Logger.getLogger(PurchaserSupplierLinkResource.class);

    private PurchaserSupplierLinkBO purchaserSupplierLinkBO;
    private BeanMeta purchaserSupplierLinkBeanMeta;
	private List<String> defaultListFields, lovFields;
    private GenericDAO<PurchaserSupplierLinkWorkFlowBean> purchaserSupplierLinkWorkFlowDAO;
    public static final String FIELDGROUP_UPDATEPLATFORMSTATUS = "updatePlatformStatus";

	
    public PurchaserSupplierLinkResource() {
        super();
        purchaserSupplierLinkBO = new PurchaserSupplierLinkBO();
        purchaserSupplierLinkBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLinkBean.class);
        purchaserSupplierLinkWorkFlowDAO = new GenericDAO<PurchaserSupplierLinkWorkFlowBean>(PurchaserSupplierLinkWorkFlowBean.class);
        defaultListFields = Arrays.asList(new String[]{"supplier","purchaser","supplierPurchaserRef","creditPeriod","status","purchaserSupplierRef","costBearingType","platformStatus","relationFlag","platformReasonCode","relationDoc","relationEffectiveDate"});
        lovFields = Arrays.asList(new String[]{"supplier","purchaser"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("supplier") String pSupplier, @QueryParam("purchaser") String pPurchaser) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pSupplier != null) && (pPurchaser != null)) {
            Object[] lKey = new Object[]{pSupplier, pPurchaser};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/pursuplnk.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/{supplier}/{purchaser}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("supplier") String pSupplier, @PathParam("purchaser") String pPurchaser) throws Exception {
    	//TODO: determine whether it is called by financier also
/*        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
        	if(!lUserBean.getDomain().equals(pSupplier) || !lUserBean.getDomain().equals(pPurchaser))
        		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
*/    	//
        PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
        Connection lConnection = pExecutionContext.getConnection();
        lFilterBean.setSupplier(pSupplier);
        lFilterBean.setPurchaser(pPurchaser);
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkBO.findBean(pExecutionContext, lFilterBean);
        if (pSupplier.equals("TEMPLATE") && lPurchaserSupplierLinkBean==null) {
        	lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
        	lPurchaserSupplierLinkBean.setPurchaser(pPurchaser);
        	lPurchaserSupplierLinkBean.setSupplier(pSupplier);
        	lPurchaserSupplierLinkBean.setPurchaserSupplierRef(pSupplier);
        	lPurchaserSupplierLinkBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
        	lPurchaserSupplierLinkBean.setApprovalStatus(PurchaserSupplierLinkBean.ApprovalStatus.Draft);
        	lPurchaserSupplierLinkBean.setRemarks(pSupplier);
        }else {
        	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
        }
        Map<String, Object> lDetailMap = purchaserSupplierLinkBeanMeta.formatAsMap(lPurchaserSupplierLinkBean, null, null, false, false); 
        // workflow
        List<Map<String, Object>> lWorkFlowMaps = new ArrayList<Map<String,Object>>();
        DBHelper lDBHelper = DBHelper.getInstance();
        String lSql = "SELECT * FROM PurchaserSupplierLinkWorkFlow WHERE PLWSUPPLIER = " + lDBHelper.formatString(pSupplier) + " AND PLWPURCHASER = " + lDBHelper.formatString(pPurchaser) + " ORDER BY PLWSTATUSUPDATETIME DESC, PLWId DESC";
        List<PurchaserSupplierLinkWorkFlowBean> lWorkFlows = purchaserSupplierLinkWorkFlowDAO.findListFromSql(lConnection, (String)lSql, 0);
        for (PurchaserSupplierLinkWorkFlowBean lPSLinkWorkFlow : lWorkFlows) {
            lWorkFlowMaps.add(purchaserSupplierLinkWorkFlowDAO.getBeanMeta().formatAsMap(lPSLinkWorkFlow, null, null, true, true));
        }
        lDetailMap.put("workFlows", lWorkFlowMaps);
        return new JsonBuilder(lDetailMap).toString();    
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
        purchaserSupplierLinkBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = purchaserSupplierLinkBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
        	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
            lResults.add(purchaserSupplierLinkBeanMeta.formatAsArray(lPurchaserSupplierLinkBean, null, lFields, true));
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured
    @Path("/all")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
        purchaserSupplierLinkBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        if (AccessControlHelper.getInstance().hasAccess("pursuplnk-view", lUserBean) == false)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = purchaserSupplierLinkBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
        //
        // filter out records based on tab
        Long lTab = Long.valueOf(((Number)lMap.get("tab")).longValue());
        List<PurchaserSupplierLinkBean> lFilteredList = new ArrayList<PurchaserSupplierLinkBean>();
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
        	lPurchaserSupplierLinkBean.populateNonDatabaseFields();
            if (lTab.equals(lPurchaserSupplierLinkBean.getTab()))
            	lFilteredList.add(lPurchaserSupplierLinkBean);
        }
        return new FileDownloadBean("BuyerSellerLink.csv", 
        		purchaserSupplierLinkBeanMeta.formatBeansAsCsv(lFilteredList, null, lFields, true, true).getBytes(), null).getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/lov")
    public String listMap(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = purchaserSupplierLinkBO.findListForMap(pExecutionContext, lUserBean);

        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
            lResults.add(purchaserSupplierLinkBeanMeta.formatAsMap(lPurchaserSupplierLinkBean));
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/purchaser")
    public String lovPur(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return purchaserSupplierLinkBO.findListForLov(pExecutionContext, true, lUserBean,null);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/supplier")
    public String lovSup(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return purchaserSupplierLinkBO.findListForLov(pExecutionContext, false, lUserBean,null);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/supplier/{purchaser}")
    public String lovSup(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("purchaser") String pPurchaser) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierLinkBean lFilterBean = null;
        if(CommonUtilities.hasValue(pPurchaser)){
        	lFilterBean = new PurchaserSupplierLinkBean();
        	lFilterBean.setPurchaser(pPurchaser);
        }
        return purchaserSupplierLinkBO.findListForLov(pExecutionContext, false, lUserBean, lFilterBean);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
        List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(lPurchaserSupplierLinkBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        if (PurchaserSupplierLinkBean.ApprovalStatus.Submitted.equals(lPurchaserSupplierLinkBean.getStatus())){
        	Map <String,String> lDataHash = new HashMap<String, String>();
        	lDataHash.put("action", "Buyer-Seller link submission");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_BUYSELLLINKSUBMISSION, AppConstants.TEMPLATE_PREFIX_BUYSELLLINK , lDataHash);        	
        
        }
        purchaserSupplierLinkBO.save(pExecutionContext, lPurchaserSupplierLinkBean, lUserBean, pNew, false, null,false);
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    @Path("/status")
    public void updateStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Connection lConnection = pExecutionContext.getConnection();
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        String lPrimaryKey = (String) lMap.get("id");
        String[] parts = lPrimaryKey.split("/");
        String lSup = parts[0]; 
        String lPur = parts[1]; 
        
		lPurchaserSupplierLinkBean.setSupplier(lSup);
		lPurchaserSupplierLinkBean.setPurchaser(lPur);
		//"supplierPurchaserRef","sellerAutoApproveInvoice", "remarks"
		String lApprovalStatusCode = (String) lMap.get("approvalStatus");
		String lApprovalRemarks = (String) lMap.get("approvalRemarks");
		String lReference = null, lAutoApprove = null;
		//supplierPurchaserRef - sellerAutoApproveInvoice --> only if status is  it is approved
		ApprovalStatus lApprovalStatus = null;
		for(ApprovalStatus lAStatus : ApprovalStatus.values()){
			if(lAStatus.getCode().equals(lApprovalStatusCode)){
				lApprovalStatus = lAStatus;
				break;
			}
		}
		lPurchaserSupplierLinkBean.setApprovalStatus(lApprovalStatus);
		lPurchaserSupplierLinkBean.setRemarks(lApprovalRemarks);

		//these two status are returned by seller hence allowing him to save the same on returning also
		if(ApprovalStatus.Approved.equals(lApprovalStatus) ||
				ApprovalStatus.Returned.equals(lApprovalStatus)	){
			lReference= (String) lMap.get("supplierPurchaserRef");
			lAutoApprove= (String) lMap.get("sellerAutoApproveInvoice");
			lPurchaserSupplierLinkBean.setSupplierPurchaserRef(lReference);
			if(CommonAppConstants.YesNo.Yes.getCode().equals(lAutoApprove)){
				lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(CommonAppConstants.YesNo.Yes);
			}else{
				lPurchaserSupplierLinkBean.setSellerAutoApproveInvoice(CommonAppConstants.YesNo.No);
			}
			if (lMap.get("sellerTds")!=null) {
				if (CommonAppConstants.Yes.Yes.getCode().equals((String)lMap.get("sellerTds"))){
					lPurchaserSupplierLinkBean.setSellerTds(YesNo.Yes);
					lPurchaserSupplierLinkBean.setSellerTdsPercent(BigDecimal.valueOf(Long.valueOf(((String) lMap.get("sellerTdsPercent")))));
				}else {
					lPurchaserSupplierLinkBean.setSellerTds(YesNo.No);
					lPurchaserSupplierLinkBean.setSellerTdsPercent(null);
				}
			}
			String lAutoConvert= (String) lMap.get("autoConvert");
			AutoConvert lTmpAutoConvert = AutoConvert.Auto;
			if(StringUtils.isNotBlank(lAutoConvert)){
				for(AutoConvert lTmp : AutoConvert.values() ){
					if(lTmp.getCode().equals(lAutoConvert)){
						lTmpAutoConvert = lTmp;
						break;
					}						
				}
			}
			lPurchaserSupplierLinkBean.setAutoConvert(lTmpAutoConvert);
		}
		String lBeanJson = purchaserSupplierLinkBeanMeta.formatAsJson(lPurchaserSupplierLinkBean);

        List<ValidationFailBean> lValidationFailBeans = null;
        
        String lFieldGroup = null;

        if(lUserBean.getDomain().equals(lPurchaserSupplierLinkBean.getPurchaser())){
        	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATEPURCHASERAPPROVALSTATUS;
        }else if(lUserBean.getDomain().equals(lPurchaserSupplierLinkBean.getSupplier())){
        	lFieldGroup = PurchaserSupplierLinkBean.FIELDGROUP_UPDATESUPPLIERAPPROVALSTATUS;
        }
        lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(lPurchaserSupplierLinkBean, 
        		lBeanJson, lFieldGroup, null);
        
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        Map <String,String> lDataHash = new HashMap<String, String>();
        if (ApprovalStatus.Approved.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
        	lDataHash.put("action", "Buyer-Seller link Acceptance/Rejecton");
        }else if (ApprovalStatus.Returned.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
        	lDataHash.put("action", "Buyer-Seller link Acceptance/Rejecton");
        }else if (ApprovalStatus.Submitted.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
        	lDataHash.put("action", "Buyer-Seller link submission for acceptance");
        }
        if (!lDataHash.isEmpty()){
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, lConnection, AppConstants.OTP_NOTIFY_TYPE_BUYSELLLINKAPPROVAL, AppConstants.TEMPLATE_PREFIX_BUYSELLLINK, lDataHash);        	
        }
        purchaserSupplierLinkBO.updateApprovalStatus(pExecutionContext, lPurchaserSupplierLinkBean, lUserBean);
        if (ApprovalStatus.Approved.equals(lPurchaserSupplierLinkBean.getApprovalStatus())){
            Connection lAdapterConnection = DBHelper.getInstance().getConnection();
        	try{
        		IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lPurchaserSupplierLinkBean.getPurchaser());
        		if(lClientAdapter != null && lPurchaserSupplierLinkBean.getPurchaser().equals("IN0000399")){
        			//TODO: WHY HAVE WE JUST SUSPENDED THE BUYER
        			lPurchaserSupplierLinkBean.setStatus(PurchaserSupplierLinkBean.Status.Suspended_by_Buyer);
        			lPurchaserSupplierLinkBean.setApprovalStatus(ApprovalStatus.Suspended);
        			//
        			AppUserBean lPurchaserAdminUserBean =  TredsHelper.getInstance().getAdminUser(lPurchaserSupplierLinkBean.getPurchaser());
        			purchaserSupplierLinkBO.updateApprovalStatus(pExecutionContext, lPurchaserSupplierLinkBean, lPurchaserAdminUserBean);
        			lPurchaserSupplierLinkBean = purchaserSupplierLinkBO.findBean(pExecutionContext, lPurchaserSupplierLinkBean);
        			lPurchaserSupplierLinkBean.populateNonDatabaseFields();
        			ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_PURSUPLINK,lAdapterConnection);
        			lProcessInformationBean.setTredsDataForProcessing(lPurchaserSupplierLinkBean);
        			lClientAdapter.sendResponseToClient(lProcessInformationBean);
        		}
        	}catch(Exception lEx){
        		logger.info("Error : " + lEx.getMessage());
        	}finally{
        		if(lAdapterConnection!=null && !lAdapterConnection.isClosed()){
    				lAdapterConnection.close();
    			}
        	}
        }
        pExecutionContext.dispose();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    @Path("/modifyCode")
    public void updateModifyCode(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            String pMessage) throws Exception {
    	 AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	 AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
    	 PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
         JsonSlurper lJsonSlurper = new JsonSlurper();
         Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
         String lPrimaryKey = (String) lMap.get("id");
         String[] parts = lPrimaryKey.split("/");
         String lSup = parts[0]; 
         String lPur = parts[1]; 
 		 lPurchaserSupplierLinkBean.setSupplier(lSup);
 		 lPurchaserSupplierLinkBean.setPurchaser(lPur);
 		 String lModifyCode = (String)lMap.get("code");
 		 if(lAppEntityBean!= null){
 			 if (lAppEntityBean.isPurchaser())
 		 		 lPurchaserSupplierLinkBean.setPurchaserSupplierRef(lModifyCode);
 			 else if (lAppEntityBean.isSupplier())
 		 		 lPurchaserSupplierLinkBean.setSupplierPurchaserRef(lModifyCode);
 		 }
 		 purchaserSupplierLinkBO.updateModifyCode(pExecutionContext, lPurchaserSupplierLinkBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-save")
    @Path("/sendReminder")
    public void sendReminder(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            String pMessage) throws Exception {
    	 AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	 PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();
         JsonSlurper lJsonSlurper = new JsonSlurper();
         Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
         String lPrimaryKey = (String) lMap.get("id");
         String[] parts = lPrimaryKey.split("/");
         String lSup = parts[0]; 
         String lPur = parts[1]; 
 		 lPurchaserSupplierLinkBean.setSupplier(lSup);
 		 lPurchaserSupplierLinkBean.setPurchaser(lPur);
 		 purchaserSupplierLinkBO.sendReminder(pExecutionContext.getConnection(), lPurchaserSupplierLinkBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/updaterelationstatus")
    public void updateRelationStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pMessage) throws Exception {
        AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        PurchaserSupplierLinkBean lPSLBean = new PurchaserSupplierLinkBean();
        List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse( lPSLBean, lMap, FIELDGROUP_UPDATEPLATFORMSTATUS,null, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
    	purchaserSupplierLinkBO.updatePlatformStatus(pExecutionContext, lPSLBean, lUserBean);
    }
   
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/platformreasoncode")
    public String lovPlatformReasonCode(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<Map<String, Object>> lResults = new ArrayList<Map<String,Object>>();
        List<RefCodeValuesBean> lRefCodeValuesBeans = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_PLATFORM_REASON_CODE);
        for(RefCodeValuesBean lBean : lRefCodeValuesBeans){
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lBean.getDesc());
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lBean.getValue());
            lResults.add(lData);
        }        	
        return new JsonBuilder(lResults).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="pursuplnk-view")
    @Path("/chargeaccess/{purchaser}")
    public String lovPlatformReasonCode(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("purchaser") String pPurchaser) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Map<String, Object> lData = new HashMap<String, Object>();
        Map<String, Object> lOptions = new HashMap<String, Object>();
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pPurchaser);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        lOptions = new HashMap<String, Object>();
    	lOptions.put(BeanFieldMeta.JSONKEY_TEXT, AppConstants.CostBearingType.Buyer.toString());
    	lOptions.put(BeanFieldMeta.JSONKEY_VALUE,AppConstants.CostBearingType.Buyer.getCode());
    	lResults.add(lOptions);
    	lOptions = new HashMap<String, Object>();
    	lOptions.put(BeanFieldMeta.JSONKEY_TEXT, AppConstants.CostBearingType.Seller.toString());
    	lOptions.put(BeanFieldMeta.JSONKEY_VALUE, AppConstants.CostBearingType.Seller.getCode());
    	lResults.add(lOptions);
        if (lAppEntityBean.getPreferences()!=null && lAppEntityBean.getPreferences().getAcs()!=null) {
        	lOptions = new HashMap<String, Object>();
        	lOptions.put(BeanFieldMeta.JSONKEY_TEXT, AppConstants.CostBearingType.Percentage_Split.toString());
        	lOptions.put(BeanFieldMeta.JSONKEY_VALUE, AppConstants.CostBearingType.Percentage_Split.getCode());
        	lResults.add(lOptions);
        	lOptions = new HashMap<String, Object>();
        	lOptions.put(BeanFieldMeta.JSONKEY_TEXT, AppConstants.CostBearingType.Periodical_Split.toString());
        	lOptions.put(BeanFieldMeta.JSONKEY_VALUE, AppConstants.CostBearingType.Periodical_Split.getCode());
        	lResults.add(lOptions);
        }      
        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/authPurchaser")
    public String authorizePurchaser(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    	, String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
        purchaserSupplierLinkBeanMeta.validateAndParse(lFilterBean, lMap, null);
        List<PurchaserSupplierLinkBean> lPurchaserSupplierLinkList = purchaserSupplierLinkBO.findAuthorizePurchaser(pExecutionContext.getConnection(), lFilterBean, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        List<Object> lPurList = new ArrayList<Object>();
        for (PurchaserSupplierLinkBean lPurchaserSupplierLinkBean : lPurchaserSupplierLinkList) {
            Map<String, Object> lData = new HashMap<String, Object>();
        	lData.put(BeanFieldMeta.JSONKEY_VALUE, lPurchaserSupplierLinkBean.getPurchaser());
            lData.put(BeanFieldMeta.JSONKEY_DESC, lPurchaserSupplierLinkBean.getPurName());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/template/suppliers")
    public String getSuppliers(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    	, String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
        PurchaserSupplierLinkBean lFilterBean = new PurchaserSupplierLinkBean();
        purchaserSupplierLinkBeanMeta.validateAndParse(lFilterBean, lMap, null);
        return purchaserSupplierLinkBO.findSuppliers(pExecutionContext, lFilterBean, lUserBean);
    }
}