
package com.xlx.treds.entity.rest;

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

import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.SettleBankLocationMapBean;
import com.xlx.treds.entity.bo.CompanyLocationBO;
import com.xlx.treds.entity.bo.SettleBankLocationMapBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.common.memdb.MemoryDBException;
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
import com.xlx.commonn.report.ReportConvertorFactory;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.commonn.other.bean.FileDownloadBean;

@Path("/settlebanklocmap")
@Singleton
public class SettleBankLocationMapResource {

	public static final Logger logger = LoggerFactory.getLogger(SettleBankLocationMapResource.class);

    private SettleBankLocationMapBO settleBankLocationMapBO;
    private CompanyLocationBO companyLocationBO;
    private BeanMeta settleBankLocationMapBeanMeta;
    private BeanMeta companyLocationBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public SettleBankLocationMapResource() {
        super();
        settleBankLocationMapBO = new SettleBankLocationMapBO();
        companyLocationBO = new CompanyLocationBO();
        settleBankLocationMapBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(SettleBankLocationMapBean.class);
        companyLocationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyLocationBean.class);
        defaultListFields = Arrays.asList(new String[]{"cdId","clId","enableSetLoc","settleClId","l1DCbdid","l2DCbdid","l1CCbdid","l2CCbdid"});
        lovFields = Arrays.asList(new String[]{"cdId","clId"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("cdId") Long pCdId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCdId != null)) {
            Object[] lKey = new Object[]{pCdId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/settlebanklocmap.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "settlebanklocmap-view")
    @Path("/{cdId}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("cdId") Long pCdId) throws Exception {
        SettleBankLocationMapBean lFilterBean = new SettleBankLocationMapBean();
        lFilterBean.setCdId(pCdId);
        SettleBankLocationMapBean lSettleBankLocationMapBean = settleBankLocationMapBO.findBean(pExecutionContext, lFilterBean);
        return settleBankLocationMapBeanMeta.formatAsJson(lSettleBankLocationMapBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "settlebanklocmap-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        SettleBankLocationMapBean lFilterBean = new SettleBankLocationMapBean();
        settleBankLocationMapBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<SettleBankLocationMapBean> lSettleBankLocationMapList = settleBankLocationMapBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (SettleBankLocationMapBean lSettleBankLocationMapBean : lSettleBankLocationMapList) {
            lResults.add(settleBankLocationMapBeanMeta.formatAsArray(lSettleBankLocationMapBean, null, lFields, true));          
            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
//    @Secured(secKey = "settlebanklocmap-view")
    @Path("/download/{format}")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter, @PathParam("format") ReportConvertorFactory.DownloadFormat pFormat) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        SettleBankLocationMapBean lFilterBean = new SettleBankLocationMapBean();
        settleBankLocationMapBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<SettleBankLocationMapBean> lSettleBankLocationMapList = settleBankLocationMapBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        FileDownloadBean lFileDownloadBean = ReportConvertorFactory.getInstance().convertData(settleBankLocationMapBeanMeta.formatBeansAsList(lSettleBankLocationMapList, null, 
        		lFields, true), "SettleBankLocationMap", settleBankLocationMapBeanMeta.getFieldLabelList(null, lFields).toArray(new String[0]), pFormat);
        
        return lFileDownloadBean.getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<SettleBankLocationMapBean> lSettleBankLocationMapList = settleBankLocationMapBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (SettleBankLocationMapBean lSettleBankLocationMapBean : lSettleBankLocationMapList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lSettleBankLocationMapBean.getCdId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lSettleBankLocationMapBean.getClId());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "settlebanklocmap-manage")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "settlebanklocmap-manage")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        SettleBankLocationMapBean lSettleBankLocationMapBean = new SettleBankLocationMapBean();
        List<ValidationFailBean> lValidationFailBeans = settleBankLocationMapBeanMeta.validateAndParse(lSettleBankLocationMapBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        settleBankLocationMapBO.save(pExecutionContext, lSettleBankLocationMapBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured(secKey = "settlebanklocmap-manage")
    @Path("/{cdId}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("cdId") Long pCdId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        SettleBankLocationMapBean lFilterBean = new SettleBankLocationMapBean();
        lFilterBean.setCdId(pCdId);
        settleBankLocationMapBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/companydetails")
    public String getCompanyDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception{
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	List<CompanyDetailBean> lCompanyDetailBeanList =  settleBankLocationMapBO.getCompanyDetails(pExecutionContext.getConnection(), lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        List<Object> lPurList = new ArrayList<Object>();
        for (CompanyDetailBean lCompanyDetailBean : lCompanyDetailBeanList) {
            Map<String, Object> lData = new HashMap<String, Object>();
        	lData.put(BeanFieldMeta.JSONKEY_VALUE, lCompanyDetailBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_DESC, lCompanyDetailBean.getCompanyName());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/companysettlementdetail")
    public String getCompanySettleDetail(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pMessage) throws Exception{
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
    	return settleBankLocationMapBO.getCompanySettleDetail(pExecutionContext.getConnection(),lMap, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/companylocation/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pCdId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        CompanyLocationBean lFilterBean = new CompanyLocationBean();
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lFilterBean.setCdId(pCdId);
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(pIsProvisional);
        List<CompanyLocationBean> lCompanyLocationBeanList = companyLocationBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        String lName = null;
        if(lCompanyLocationBeanList!=null){
            for (CompanyLocationBean lCompanyLocationBean : lCompanyLocationBeanList) {
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