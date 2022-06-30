package com.xlx.treds.auction.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean.ApprovalStatus;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean.Level;
import com.xlx.treds.auction.bean.ObligationExtensionPenaltyBean;
import com.xlx.treds.auction.bean.TenureWiseBaseRateBean;
import com.xlx.treds.auction.bo.FinancierAuctionSettingBO;
import com.xlx.treds.entity.bean.AppEntityBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/")
public class FinancierAuctionSettingResource {

    private FinancierAuctionSettingBO financierAuctionSettingBO;
    private BeanMeta financierAuctionSettingBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public FinancierAuctionSettingResource() {
        super();
        financierAuctionSettingBO = new FinancierAuctionSettingBO();
        financierAuctionSettingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FinancierAuctionSettingBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","level","financier","purchaser","supplier","auId","loginId","baseBidRate","minBidRate","maxBidRate","minSpread","maxSpread","currency","limit","utilised","bidLimit","bidLimitUtilised","active"});
        lovFields = Arrays.asList(new String[]{"id","level"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/finaucsetself")
    public void pageSelf(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.setAttribute("level", FinancierAuctionSettingBean.Level.Financier_Self);
        pRequest.getRequestDispatcher("/WEB-INF/finaucset.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/finaucsetpur")
    public void pagePurchaser(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.setAttribute("level", FinancierAuctionSettingBean.Level.Financier_Buyer);
        pRequest.getRequestDispatcher("/WEB-INF/finaucset.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/finaucsetps")
    public void pagePurchaserSupplier(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.setAttribute("level", FinancierAuctionSettingBean.Level.Financier_Buyer_Seller);
        pRequest.getRequestDispatcher("/WEB-INF/finaucset.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/finaucsetuser")
    public void pageUser(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.setAttribute("level", FinancierAuctionSettingBean.Level.Financier_User);
        pRequest.getRequestDispatcher("/WEB-INF/finaucset.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/sysaucsetpur")
    public void pageBankPurchaser(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.setAttribute("level", FinancierAuctionSettingBean.Level.System_Buyer);
        pRequest.getRequestDispatcher("/WEB-INF/finaucset.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/finaucset/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        FinancierAuctionSettingBean lFilterBean = new FinancierAuctionSettingBean();
        lFilterBean.setId(pId);
        FinancierAuctionSettingBean lFinancierAuctionSettingBean = financierAuctionSettingBO.findBean(pExecutionContext, lFilterBean);
        if (AccessControlHelper.getInstance().hasAccess("finaucset-view-" + lFinancierAuctionSettingBean.getLevel().getCode(), lUserBean) == false)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        if(FinancierAuctionSettingBean.Level.Financier_Buyer.equals(lFinancierAuctionSettingBean.getLevel())){
            AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lFinancierAuctionSettingBean.getFinancier());
            Connection lConnection = pExecutionContext.getConnection();
            boolean lIsLocationSettlementEnabled = TredsHelper.getInstance().isLocationwiseSettlementEnabled(lConnection, lAppEntityBean.getCdId(),false);
            lFinancierAuctionSettingBean.setIsLocationEnabled(lIsLocationSettlementEnabled?Yes.Yes:null);
        }
        return financierAuctionSettingBeanMeta.formatAsJson(lFinancierAuctionSettingBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/finaucset/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        FinancierAuctionSettingBean lFilterBean = new FinancierAuctionSettingBean();
        financierAuctionSettingBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        if (AccessControlHelper.getInstance().hasAccess("finaucset-view-" + lFilterBean.getLevel().getCode(), lUserBean) == false)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        List<FinancierAuctionSettingBean> lFinancierAuctionSettingList = financierAuctionSettingBO.findList(pExecutionContext, lFilterBean, null, lUserBean);

        List<Object> lResults = new ArrayList<Object>();
        for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lFinancierAuctionSettingList) {
            if (lFields == null)
                lResults.add(financierAuctionSettingBeanMeta.formatAsMap(lFinancierAuctionSettingBean, null, defaultListFields, false)); // api
            else
                lResults.add(financierAuctionSettingBeanMeta.formatAsArray(lFinancierAuctionSettingBean, null, lFields, true));
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured
    @Path("/finaucset/all")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        FinancierAuctionSettingBean lFilterBean = new FinancierAuctionSettingBean();
        financierAuctionSettingBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        Long lTab = Long.valueOf(lMap.get("tab").toString());
        if (lFields == null) lFields = defaultListFields;

        if (AccessControlHelper.getInstance().hasAccess("finaucset-view-" + lFilterBean.getLevel().getCode(), lUserBean) == false)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        List<FinancierAuctionSettingBean> lFinancierAuctionSettingList = financierAuctionSettingBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<BeanFieldMeta> lMasterList = financierAuctionSettingBeanMeta.getFieldMetaList(null, lFields);
        String lExcludeGroup = null;
        switch (lFilterBean.getLevel()) {
        case Financier_Self:
            lExcludeGroup = "financierSelfX";
            break;
        case Financier_Buyer :
            lExcludeGroup = "buyerX";
            break;
        case Financier_Buyer_Seller :
            lExcludeGroup = "buyerSellerX";
            break;
        case Financier_User :
            lExcludeGroup = "userX";
            break;
        }
        Set<String> lExcludeKeys = new HashSet<String>();
        if (lExcludeGroup != null) {
            List<BeanFieldMeta> lExcludeFieldList = financierAuctionSettingBeanMeta.getFieldMetaList(lExcludeGroup, null);
            for (BeanFieldMeta lBeanFieldMeta : lExcludeFieldList) {
                lExcludeKeys.add(lBeanFieldMeta.getName());
            }
        }
        
        List<BeanFieldMeta> lFinalList = new ArrayList<BeanFieldMeta>();
        for (BeanFieldMeta lBeanFieldMeta : lMasterList) {
            if (lFields.contains(lBeanFieldMeta.getName()))
                lFinalList.add(lBeanFieldMeta);
        }
        int lCount = lFinalList.size();
        String lComma = ",";
        String lNewLine = "\r\n";
        final StringBuilder lData = new StringBuilder();
        int lRateCount = 0;
        if (lFilterBean.getLevel() == FinancierAuctionSettingBean.Level.Financier_Self) {
            for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lFinancierAuctionSettingList) {
                List<TenureWiseBaseRateBean> lBaseRateList = lFinancierAuctionSettingBean.getBaseRateList(); 
                if ((lBaseRateList != null) && (lBaseRateList.size() > 0)) {
                    if (lRateCount < lBaseRateList.size())
                        lRateCount = lBaseRateList.size();
                }
            }
        }
        List<ApprovalStatus> lListOfApprovalStatus = lFilterBean.getTabWiseStatus(lTab); 
        for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lFinancierAuctionSettingList) {
            if (lTab != null && !lListOfApprovalStatus.contains(lFinancierAuctionSettingBean.getApprovalStatus())) {
            	continue;
            }
        	//if (lFinancierAuctionSettingBean.getApprovalStatus() != FinancierAuctionSettingBean.ApprovalStatus.Approved) continue;
            lData.append(lNewLine);
            
            for (int lPtr=0;lPtr<lCount;lPtr++) {
                BeanFieldMeta lBeanFieldMeta = lFinalList.get(lPtr);
                if (lPtr > 0) lData.append(lComma);
                Object lObject = lBeanFieldMeta.getFormattedValue(lFinancierAuctionSettingBean, false);
                if (lObject != null){
                	if(StringUtils.contains(lObject.toString(), lComma)){
                        lData.append(TredsHelper.getInstance().getSanatisedObject(lObject.toString().replace(",","")));
                	}else{
                        lData.append(TredsHelper.getInstance().getSanatisedObject(lObject));
                	}
                }
            }
            List<TenureWiseBaseRateBean> lBaseRateList = lFinancierAuctionSettingBean.getBaseRateList();
            for (int lRatePtr=0;lRatePtr<lRateCount;lRatePtr++) {
                if ((lBaseRateList != null) && (lRatePtr < lBaseRateList.size()))
                    lData.append(lComma).append(lBaseRateList.get(lRatePtr).getTenure()).append(lComma).append(lBaseRateList.get(lRatePtr).getBaseRate());
                else
                    lData.append(lComma).append(lComma);
            }
        }
        final StringBuilder lHeader = new StringBuilder();
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            BeanFieldMeta lBeanFieldMeta = lFinalList.get(lPtr);
            if (lPtr > 0) lHeader.append(lComma);
            lHeader.append(lBeanFieldMeta.getLabel());
        }
        for (int lRatePtr=0;lRatePtr<lRateCount;lRatePtr++) {
            lHeader.append(lComma).append("tenure").append(lRatePtr+1).append(lComma).append("baseRate").append(lRatePtr+1);
        }
        return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
                output.write(lHeader.toString().getBytes());
                output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=\"" + lFilterBean.getLevel().toString() + " Limits.csv\"").header("Content-Type", "application/octet-stream").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/finaucset")
    @Secured
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/finaucset")
    @Secured
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        FinancierAuctionSettingBean lFinancierAuctionSettingBean = new FinancierAuctionSettingBean();
        List<ValidationFailBean> lValidationFailBeans = financierAuctionSettingBeanMeta.validateAndParse(lFinancierAuctionSettingBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        if (AccessControlHelper.getInstance().hasAccess("finaucset-save-" + lFinancierAuctionSettingBean.getLevel().getCode(), lUserBean) == false)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        Map<String, String> lDataHash = new HashMap<String,String>();
        Map<Level, String[]> lNotificationTypeAndTemplateHash = new HashMap<Level, String[]>();
        //
        if(pNew){
        	lNotificationTypeAndTemplateHash.put(Level.Financier_User, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINUSERLIMITNEW,AppConstants.TEMPLATE_PREFIX_FINUSERLIMITNEW} );
            lNotificationTypeAndTemplateHash.put(Level.Financier_Buyer, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINBUYERLIMITNEW,AppConstants.TEMPLATE_PREFIX_FINBUYERLIMITNEW});
            lNotificationTypeAndTemplateHash.put(Level.Financier_Buyer_Seller, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINBUYERSELLERLIMITNEW,AppConstants.TEMPLATE_PREFIX_FINBUYERSELLERLIMITNEW});
        }else{
        	lNotificationTypeAndTemplateHash.put(Level.Financier_Self, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINLIMITSELFMODIFY,AppConstants.TEMPLATE_PREFIX_FINLIMITSELFMODIFY});
            lNotificationTypeAndTemplateHash.put(Level.Financier_User, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINUSERLIMITMODIFY,AppConstants.TEMPLATE_PREFIX_FINUSERLIMITMODIFY});
            lNotificationTypeAndTemplateHash.put(Level.Financier_Buyer, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINBUYERLIMITMODIFY,AppConstants.TEMPLATE_PREFIX_FINBUYERLIMITMODIFY});
            lNotificationTypeAndTemplateHash.put(Level.Financier_Buyer_Seller, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINBUYERSELLERLIMITMODIFY,AppConstants.TEMPLATE_PREFIX_FINBUYERSELLERLIMITMODIFY });
        }
        //
        if(lNotificationTypeAndTemplateHash.containsKey(lFinancierAuctionSettingBean.getLevel())){
            lDataHash.put("action", lFinancierAuctionSettingBean.getTitle() + " " + (pNew?"creation":"modify"));
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), lNotificationTypeAndTemplateHash.get(lFinancierAuctionSettingBean.getLevel())[0],  lNotificationTypeAndTemplateHash.get(lFinancierAuctionSettingBean.getLevel())[1], lDataHash);        	
        }
        
        financierAuctionSettingBO.save(pExecutionContext, lFinancierAuctionSettingBean, lUserBean, pNew, false);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/finaucset/status")
    public void updateStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        FinancierAuctionSettingBean lFinancierAuctionSettingBean = new FinancierAuctionSettingBean();
        List<ValidationFailBean> lValidationFailBeans = financierAuctionSettingBeanMeta.validateAndParse(lFinancierAuctionSettingBean, 
            pMessage, FinancierAuctionSettingBean.FIELDGROUP_UPDATEAPPROVALSTATUS, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        String lSecKey;
        if ((lFinancierAuctionSettingBean.getApprovalStatus() == FinancierAuctionSettingBean.ApprovalStatus.Submitted) || 
                (lFinancierAuctionSettingBean.getApprovalStatus() == FinancierAuctionSettingBean.ApprovalStatus.Deleted))
            lSecKey = "finaucset-save-" + lFinancierAuctionSettingBean.getLevel().getCode();
        else
            lSecKey = "finaucset-status-checker-" + lFinancierAuctionSettingBean.getLevel().getCode();
        if (AccessControlHelper.getInstance().hasAccess(lSecKey, lUserBean) == false)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        
        Map<String, String> lDataHash = new HashMap<String,String>();
        Map<Level, String[]> lTemplateHash = new HashMap<Level, String[]>();
        Map<ApprovalStatus, String> lAppStatusNoun = new HashMap<ApprovalStatus, String>();
        //
        lAppStatusNoun.put(ApprovalStatus.Approved, "approval");
        lAppStatusNoun.put(ApprovalStatus.Rejected, "rejected");
        lAppStatusNoun.put(ApprovalStatus.Returned, "returned");
        //
        lTemplateHash.put(Level.Financier_Buyer, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINBUYERLIMITAPPROVAL,AppConstants.TEMPLATE_PREFIX_FINBUYERLIMITAPPROVAL});
        lTemplateHash.put(Level.Financier_Buyer_Seller, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINBUYERSELLERLIMITAPPROVAL,AppConstants.TEMPLATE_PREFIX_FINBUYERSELLERLIMITAPPROVAL});
        lTemplateHash.put(Level.Financier_User, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINUSERLIMITAPPROVAL,AppConstants.TEMPLATE_PREFIX_FINUSERLIMITAPPROVAL});
        lTemplateHash.put(Level.Financier_Self, new String[]{AppConstants.OTP_NOTIFY_TYPE_FINLIMITSELFAPPROVAL,AppConstants.TEMPLATE_PREFIX_FINLIMITSELFAPPROVAL});
        //
        if(lAppStatusNoun.containsKey(lFinancierAuctionSettingBean.getApprovalStatus())){
            if(lTemplateHash.containsKey(lFinancierAuctionSettingBean.getLevel())){
        		lDataHash.put("action", lFinancierAuctionSettingBean.getTitle() + " " + lAppStatusNoun.get(lFinancierAuctionSettingBean.getApprovalStatus()));
               	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), lTemplateHash.get(lFinancierAuctionSettingBean.getLevel())[0],  lTemplateHash.get(lFinancierAuctionSettingBean.getLevel())[0], lDataHash);        	
            }
        }
        financierAuctionSettingBO.updateApprovalStatus(pExecutionContext, lFinancierAuctionSettingBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/finaucset/purchasers")
    public String lovPurchaserCode(@Context ExecutionContext pExecutionContext, 
            @Context HttpServletRequest pRequest, @QueryParam("def") String pAddDefault) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        FinancierAuctionSettingBean lFilterBean = new FinancierAuctionSettingBean();
        lFilterBean.setLevel(FinancierAuctionSettingBean.Level.Financier_Buyer);
        lFilterBean.setApprovalStatus(FinancierAuctionSettingBean.ApprovalStatus.Approved);
        List<FinancierAuctionSettingBean> lFinancierAuctionSettingList = financierAuctionSettingBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        Map<String, Object> lData;
        if ("Y".equals(pAddDefault)) {
            lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, ObligationExtensionPenaltyBean.DEFAULT);
            lData.put(BeanFieldMeta.JSONKEY_TEXT, ObligationExtensionPenaltyBean.DEFAULT);
            lResults.add(lData);
        }
        String lDisplayText = "";
        for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lFinancierAuctionSettingList) {
            lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lFinancierAuctionSettingBean.getPurchaser());
            if(CommonUtilities.hasValue(lFinancierAuctionSettingBean.getPurchaserRef())){
            	lDisplayText = lFinancierAuctionSettingBean.getPurchaserRef() + " - ";
            }else {
                lDisplayText ="";
            }
            lDisplayText += lFinancierAuctionSettingBean.getPurName();
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lDisplayText);
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }
 

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/finaucset/utilization/{purCode}")
    public String getTotalLimit(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("purCode") String pPurchaserCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
/*        FinancierAuctionSettingBean lFilterBean = new FinancierAuctionSettingBean();
        FinancierAuctionSettingBean lFinancierAuctionSettingBean = financierAuctionSettingBO.findBean(pExecutionContext, lFilterBean);
        if (AccessControlHelper.getInstance().hasAccess("finaucset-view-" + lFinancierAuctionSettingBean.getLevel().getCode(), lUserBean) == false)
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
*/
        Map<String, Object> lData = financierAuctionSettingBO.getTotalFinancierLimit(pExecutionContext.getConnection(), lUserBean, pPurchaserCode);        	
        return new JsonBuilder(lData).toString();
    }
    
    @GET
    @Secured
    @Path("/finaucset/islocationenabled/{code}")
    public boolean isLocationEnabled(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pCode);
        if (!lAppEntityBean.isFinancier()){
        	return false;
        }
        return TredsHelper.getInstance().isLocationwiseSettlementEnabled(lConnection, lAppEntityBean.getCdId(),false);
    }

}