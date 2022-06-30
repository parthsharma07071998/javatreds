package com.xlx.treds.entity.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.CompanyApprovalStatus;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyWorkFlowBean;
import com.xlx.treds.entity.bean.RegistrationWrapperBean;
import com.xlx.treds.entity.bo.CompanyDetailBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/")
@Singleton
public class CompanyDetailResource {

	private static final Logger logger = LoggerFactory.getLogger(CompanyDetailResource.class);
			
    private BeanMeta companyDetailBeanMeta;
    private BeanMeta companyWorkFlowBeanMeta;
    private List<String> defaultListFields;
    private String defaultListDBColumns;

    public CompanyDetailResource() {
        super();
        companyDetailBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyDetailBean.class);
        companyWorkFlowBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CompanyWorkFlowBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","companyName","constitution","type","approvalStatus","isProvisional"});
        defaultListDBColumns = "CDId,CDCompanyName,CDConstitution,CDApprovalStatus,CDSupplierFlag,CDPurchaserFlag,CDFinancierFlag";
    }
    
    private CompanyDetailBO getCompanyDetailBO() {
    	return new CompanyDetailBO();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/company")
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        ) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/company.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        lFilterBean.setId(pId);
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        CompanyDetailBean lCompanyDetailBean = getCompanyDetailBO().findBean(pExecutionContext, lFilterBean, lUserBean, false);
        return companyDetailBeanMeta.formatAsJson(lCompanyDetailBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company/{id}/{isProvisional}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId, @PathParam("isProvisional") boolean pIsProvisional) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        lFilterBean.setId(pId);
        //TODO: Receive isProvisional from frontend - is is being passed to the BOs findBean function which determines from where to get the bean
        lFilterBean.setIsProvisional(pIsProvisional);
        CompanyDetailBean lCompanyDetailBean = getCompanyDetailBO().findBean(pExecutionContext, lFilterBean, lUserBean, false);
        return companyDetailBeanMeta.formatAsJson(lCompanyDetailBean);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/companyview")
    public void pageCompanyView(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
    		, @QueryParam("viewKYC") Boolean pViewKYC
    		, @QueryParam("id") Long pId
    		, @QueryParam("entityCode") String pEntityCode
        ) throws Exception {
        if ((pViewKYC != null) && (pViewKYC.booleanValue()))
            pRequest.getRequestDispatcher("/WEB-INF/companykycdocumentview.jsp"+(pId!=null?"?id="+pId.toString():"")).forward(pRequest, pResponse);
        else
        	pRequest.getRequestDispatcher("/WEB-INF/companyview.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/companyview")
    public String view(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @QueryParam("id") Long pId, @QueryParam("final") String pFinal, @QueryParam("entityCode") String pEntityCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Boolean lFinal = "Y".equalsIgnoreCase(pFinal);
        return getCompanyDetailBO().getCompanyJson(pExecutionContext, pId, pEntityCode, lFinal, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company/industry")
    public String industry(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        return OtherResourceCache.getInstance().getIndustryJson();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company/subsegment/{industry}")
    public String subSegment(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
            @PathParam("industry") String pIndustryCode) throws Exception {
        return OtherResourceCache.getInstance().getSubSegmentJson(pIndustryCode);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
        List<ValidationFailBean> lValidationFailBeans = companyDetailBeanMeta.validateAndParse(lCompanyDetailBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        //if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
        //    throw new CommonValidationException(lValidationFailBeans);
        logger.info("pMessage : "+pMessage);
        logger.info("lCompanyDetailBean : "+lCompanyDetailBean.toString());
        logger.info("lCompanyDetailBean : "+companyDetailBeanMeta.formatAsMap(lCompanyDetailBean));
        getCompanyDetailBO().save(pExecutionContext, lCompanyDetailBean, lUserBean, lUserBean, pNew);
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company/validate")
    public String validate(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Long lCompanyId = null;
        if (AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) ||
            	AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain())){
        	String lTemp = pRequest.getParameter("entityId");
        	if(CommonUtilities.hasValue(lTemp)) lCompanyId = new Long(lTemp);
        }else if (AppConstants.DOMAIN_REGUSER.equals(lUserBean.getDomain())){
        	lCompanyId = lUserBean.getId();
        }
        return new JsonBuilder(getCompanyDetailBO().validateAndSubmit(pExecutionContext, lCompanyId, lUserBean, false)).toPrettyString();
    }
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/company/submit")
    public String submit(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Long lCompanyId = null;
        if (AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) ||
            	AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain())){
        	String lTemp = pRequest.getParameter("entityId");
        	if(CommonUtilities.hasValue(lTemp)) lCompanyId = new Long(lTemp);
        }else if (AppConstants.DOMAIN_REGUSER.equals(lUserBean.getDomain())){
        	lCompanyId = lUserBean.getId();
        }
        return new JsonBuilder(getCompanyDetailBO().validateAndSubmit(pExecutionContext, lCompanyId, lUserBean, true)).toPrettyString();
    }
    
    // Admin commands
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="company-view")
    @Path("/company/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if (!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) &&
        	!AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain()))
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        companyDetailBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        if(lMap.containsKey("type")) lFilterBean.setType((String) lMap.get("type"));

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<CompanyDetailBean> lCompanyDetailList = getCompanyDetailBO().findList(pExecutionContext, lFilterBean, null, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (CompanyDetailBean lCompanyDetailBean : lCompanyDetailList) {
            lResults.add(companyDetailBeanMeta.formatAsArray(lCompanyDetailBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="company-approve")
    @Path("/company/status/a")
    public void updateStatusApprove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pMessage) throws Exception {
        updateStatus(pExecutionContext, pRequest, pMessage, CompanyApprovalStatus.Approved);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="company-reject")
    @Path("/company/status/r")
    public void updateStatusReject(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pMessage) throws Exception {
        updateStatus(pExecutionContext, pRequest, pMessage, CompanyApprovalStatus.Rejected);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="company-return")
    @Path("/company/status/b")
    public void updateStatusReturn(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pMessage) throws Exception {
        updateStatus(pExecutionContext, pRequest, pMessage, CompanyApprovalStatus.Returned);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="company-add")
    @Path("/company/startmod")
    public void markInModification(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pMessage) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CompanyDetailBean lCompanyDetailBean = new CompanyDetailBean();
        List<ValidationFailBean> lValidationFailBeans = companyDetailBeanMeta.validateAndParse(lCompanyDetailBean, pMessage, null, null);
        getCompanyDetailBO().startModification(pExecutionContext, lCompanyDetailBean,lUserBean);
    }
    
    private void updateStatus(ExecutionContext pExecutionContext, HttpServletRequest pRequest, 
            String pMessage, CompanyApprovalStatus pStatus) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if (!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) &&
            	!AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain())){
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        CompanyWorkFlowBean lCompanyWorkFlowBean = new CompanyWorkFlowBean();
        lCompanyWorkFlowBean.setApprovalStatus(pStatus);
        List<ValidationFailBean> lValidationFailBeans = companyWorkFlowBeanMeta.validateAndParse(lCompanyWorkFlowBean, 
            pMessage, BeanMeta.FIELDGROUP_INSERT, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        getCompanyDetailBO().updateStatus(pExecutionContext, lCompanyWorkFlowBean, lUserBean, true);
    }
        
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Secured
    @Path("/printhtml/{id}/{provFlag}")
    public void printHtml(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, @PathParam("id") Long pId, @PathParam("provFlag") Boolean pProvFlag) throws Exception {
    	HashMap<String, Object> lAttributes = null;
    	RegistrationWrapperBean lRegistrationWrapperBean = null;
    	IAppUserBean lUserBean = null;
    	//
        lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if ( (!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && !AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain()) )
        		&& (!lUserBean.getId().equals(pId)))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	lRegistrationWrapperBean = new RegistrationWrapperBean(pExecutionContext, lUserBean, pId, pProvFlag);
    	lAttributes = new HashMap<String, Object>();
		lAttributes.put(RegistrationWrapperBean.ENTITY_NAME, lRegistrationWrapperBean);
		pRequest.setAttribute("Attribs", lAttributes);
		pRequest.getRequestDispatcher("/WEB-INF/tredsregform.jsp").forward(pRequest, pResponse);
    }
    
    @GET
    @Produces("application/x-pdf")
    @Path("/printpdf/{id}/{provFlag}")
    @Secured
    public Response printPdf(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId, @PathParam("provFlag") Boolean pProvFlag) throws Exception {
        IAppUserBean lUserBean = null;
        lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if ( (!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) && !AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain()) )
        		&& (!lUserBean.getId().equals(pId)))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        Connection lConnection = pExecutionContext.getConnection();
    	try 
    	{
    	    ITextRenderer renderer = new ITextRenderer();
    	    //
    	    String lUrl = TredsHelper.getInstance().getApplicationURL();
    	    lUrl += "printhtml/";
    	    lUrl += pId;
    	    lUrl += "/"+pProvFlag;
    	    lUrl += "?loginKey=" + AuthenticationHandler.getInstance().getLoginKey(pRequest);
    	    renderer.setDocument(lUrl);
    	    //renderer.setDocumentFromString(getCompanyDetailsHtmlForPdf(pExecutionContext, pRequest, pId));
    	    renderer.layout();
    	    renderer.createPDF(lByteArrayOutputStream);
    	    lByteArrayOutputStream.close();
    	    final byte[] lPdf = lByteArrayOutputStream.toByteArray();
    	    String lFileName =  TredsHelper.getInstance().getRegistrationNo(lConnection,pId);
    	    if(!CommonUtilities.hasValue(lFileName)) 
    	    	lFileName = "RegistrationForm.pdf";
    	    else
    	    	lFileName+=".pdf";
            return Response.ok().header("content-disposition", "attachment; filename="+ lFileName ).entity(new StreamingOutput(){
                @Override
                public void write(OutputStream output)
                   throws IOException, WebApplicationException {
                   output.write(lPdf);
                   output.flush();
                }
            }).build();
    	} 
    	catch (Exception ex) 
    	{
    		ex.printStackTrace();
    	}finally{
    		if(lConnection!=null){
    			try{
        			lConnection.close();
    			}catch(Exception lEx){
    				logger.info("Error while closing connection in CompanyDetailResource. : "+ lEx.getMessage());
    			}
    		}
    	}
        //
        return null;
    }

    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/company/address/reg")
    public String getCompanyRegisterdOffDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Long lCompanyId = null;
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain()) ){
        	String lTemp = pRequest.getParameter("entityId");
        	if(CommonUtilities.hasValue(lTemp)){
        		lCompanyId = new Long(lTemp);
        	}
        }else if (AppConstants.DOMAIN_REGUSER.equals(lUserBean.getDomain())){
        	lCompanyId = lUserBean.getId();
        }
        return getCompanyDetailBO().getAddressAsJson(pExecutionContext, lCompanyId, lUserBean, false, true);
    }  
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/company/address/cor")
    public String getCompanyCorrespondanceOffDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Long lCompanyId = null;
        if(AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()) ||
        		AppConstants.DOMAIN_REGENTITY.equals(lUserBean.getDomain()) ){
        	String lTemp = pRequest.getParameter("entityId");
        	if(CommonUtilities.hasValue(lTemp)){
        		lCompanyId = new Long(lTemp);
        	}
        }else if (AppConstants.DOMAIN_REGUSER.equals(lUserBean.getDomain())){
        	lCompanyId = lUserBean.getId();
        }
        return getCompanyDetailBO().getAddressAsJson(pExecutionContext, lCompanyId, lUserBean, true, true);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey="company-add")
    @Path("/updatecompanyname")
    public void updateCompanyName(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pMessage) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurpur = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurpur.parseText(pMessage);
        getCompanyDetailBO().updateCompanyName(pExecutionContext, lMap, lUserBean);
    
    }
    
    @POST
    @Secured(secKey="company-add")
    @Path("/company/changeappmod")
    public void changeAppModStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pMessage) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurpur = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurpur.parseText(pMessage);
        CompanyDetailBean lFilterBean = new CompanyDetailBean();
        companyDetailBeanMeta.validateAndParse(lFilterBean, lMap, null);
        getCompanyDetailBO().changeAppModStatus(pExecutionContext, lFilterBean, lUserBean);
    }
}