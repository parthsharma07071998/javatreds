package com.xlx.treds.other.rest;

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
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.other.bean.IUploadFileBean;
import com.xlx.treds.other.bean.RegistrationFilesBean;
import com.xlx.treds.other.bean.UploadFileBean;
import com.xlx.treds.other.bo.UploadFileBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/registrationfiles")
public class RegistrationFilesResource {

    private UploadFileBO uploadFileBO;
    private BeanMeta registrationFilesBeanMeta;
	private List<String> defaultListFields, lovFields;
	private GenericDAO<UploadFileBean> uploadFileDAO;
	
    public RegistrationFilesResource() {
        super();
        uploadFileBO = new UploadFileBO();
        registrationFilesBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(RegistrationFilesBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","key","fileName","storageFileName"});
        lovFields = Arrays.asList(new String[]{"id","type"});
        uploadFileDAO = new GenericDAO<UploadFileBean>(UploadFileBean.class);
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
        pRequest.getRequestDispatcher("/WEB-INF/registrationfiles.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        RegistrationFilesBean lFilterBean = new RegistrationFilesBean();
        lFilterBean.setId(pId);
        RegistrationFilesBean lRegistrationFilesBean = (RegistrationFilesBean) uploadFileBO.findBean(pExecutionContext, lFilterBean);
        return registrationFilesBeanMeta.formatAsJson(lRegistrationFilesBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        RegistrationFilesBean lFilterBean = new RegistrationFilesBean();
        registrationFilesBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        
        if(StringUtils.contains(lFilterBean.getKey(), "null")){
        	lFilterBean.setKey(null);
        }
        lFilterBean.setType(UploadFileBean.FILETYPE_REGISTRATIONS);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields.isEmpty() || lFields.size()>0) lFields = defaultListFields;
        if (lFields == null) lFields = defaultListFields;
        List<IUploadFileBean> lRegistrationFilesList = uploadFileBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object> lResults = new ArrayList<Object>();
        RegistrationFilesBean lRegFileBean = null;
        for (IUploadFileBean lRegistrationFilesBean : lRegistrationFilesList) {
        	Map<String, Object> lTmpMap = new HashMap<String, Object>();
        	lTmpMap.putAll(registrationFilesBeanMeta.formatAsMap(lRegistrationFilesBean, null, lFields, true));
        	lRegFileBean = new RegistrationFilesBean(lRegistrationFilesBean);
        	lTmpMap.put("constitution",TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_CONSTITUTION,lRegFileBean.getConstitution()));
        	lTmpMap.put("entity",TredsHelper.getInstance().getRefCodeValue(AppConstants.RC_ENTITYTYPE,lRegFileBean.getEntityType()));
            lResults.add(lTmpMap);            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<IUploadFileBean> lRegistrationFilesList = uploadFileBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (IUploadFileBean lRegistrationFilesBean : lRegistrationFilesList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lRegistrationFilesBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lRegistrationFilesBean.getType());
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
        RegistrationFilesBean lRegistrationFilesBean = new RegistrationFilesBean();
        List<ValidationFailBean> lValidationFailBeans = registrationFilesBeanMeta.validateAndParse(lRegistrationFilesBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        if (pNew){
        	UploadFileBean lFilterBean = new UploadFileBean();
        	lFilterBean.setKey(lRegistrationFilesBean.getKey());
        	UploadFileBean lBean = uploadFileDAO.findBean(pExecutionContext.getConnection(), lFilterBean);
        	if (lBean!=null){
        		throw new CommonBusinessException("Record already exists.");
        	}
        }
        lRegistrationFilesBean.setType(UploadFileBean.FILETYPE_REGISTRATIONS);
        uploadFileBO.save(pExecutionContext, lRegistrationFilesBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        RegistrationFilesBean lFilterBean = new RegistrationFilesBean();
        lFilterBean.setId(pId);
        uploadFileBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}