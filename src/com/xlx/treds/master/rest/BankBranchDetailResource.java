package com.xlx.treds.master.rest;

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
import com.xlx.treds.master.bean.BankBranchDetailBean;
import com.xlx.treds.master.bo.BankBranchDetailBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/bbdtl")
public class BankBranchDetailResource {

    private BankBranchDetailBO bankBranchDetailBO;
    private BeanMeta bankBranchDetailBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public BankBranchDetailResource() {
        super();
        bankBranchDetailBO = new BankBranchDetailBO();
        bankBranchDetailBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BankBranchDetailBean.class);
        defaultListFields = Arrays.asList(new String[]{"ifsc","micrcode","branchname","address","contact","city","district","state","status"});
        lovFields = Arrays.asList(new String[]{"ifsc","micrcode"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("ifsc") String pIfsc) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pIfsc != null)) {
            Object[] lKey = new Object[]{pIfsc};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/bbdtl.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{ifsc}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("ifsc") String pIfsc) throws Exception {
    	/*
    	BankBranchDetailBO lBBDBo  = new BankBranchDetailBO();
    	lBBDBo.uploadBranchFile();
    	return "";
        */
        BankBranchDetailBean lFilterBean = new BankBranchDetailBean();
        lFilterBean.setIfsc(pIfsc);
        BankBranchDetailBean lBankBranchDetailBean = bankBranchDetailBO.findBean(pExecutionContext, lFilterBean);
        return bankBranchDetailBeanMeta.formatAsJson(lBankBranchDetailBean);
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
        BankBranchDetailBean lFilterBean = new BankBranchDetailBean();
        bankBranchDetailBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if(lMap==null || (lMap.size()==1 && lFields!=null))
        	throw new CommonBusinessException("Please provide atleast one filter.");
        if (lFields == null) lFields = defaultListFields;
        List<BankBranchDetailBean> lBankBranchDetailList = bankBranchDetailBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (BankBranchDetailBean lBankBranchDetailBean : lBankBranchDetailList) {
            lResults.add(bankBranchDetailBeanMeta.formatAsArray(lBankBranchDetailBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<BankBranchDetailBean> lBankBranchDetailList = bankBranchDetailBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (BankBranchDetailBean lBankBranchDetailBean : lBankBranchDetailList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lBankBranchDetailBean.getIfsc());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lBankBranchDetailBean.getMicrcode());
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
        BankBranchDetailBean lBankBranchDetailBean = new BankBranchDetailBean();
        List<ValidationFailBean> lValidationFailBeans = bankBranchDetailBeanMeta.validateAndParse(lBankBranchDetailBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        bankBranchDetailBO.save(pExecutionContext, lBankBranchDetailBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{ifsc}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("ifsc") String pIfsc) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        BankBranchDetailBean lFilterBean = new BankBranchDetailBean();
        lFilterBean.setIfsc(pIfsc);
        bankBranchDetailBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/upload")
    public String uploadBranchIFSC(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	BankBranchDetailBO lBBDBo  = new BankBranchDetailBO();
    	lBBDBo.uploadBranchFile(pExecutionContext.getConnection(), pRequest.getParameter("filepath"));
    	return "";
    }


}