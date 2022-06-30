package com.xlx.treds.master.rest;

import java.io.IOException;
import java.io.OutputStream;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.xlx.common.base.CommonConstants;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.master.bean.HolidayMasterBean;
import com.xlx.treds.master.bo.HolidayMasterBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/holiday")
public class HolidayMasterResource {

    private HolidayMasterBO holidayMasterBO;
    private BeanMeta holidayMasterBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public HolidayMasterResource() {
        super();
        holidayMasterBO = new HolidayMasterBO();
        holidayMasterBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","date","desc","type"});
        lovFields = Arrays.asList(new String[]{"id","date"});
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
        pRequest.getRequestDispatcher("/WEB-INF/holiday.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="holiday-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        HolidayMasterBean lFilterBean = new HolidayMasterBean();
        lFilterBean.setId(pId);
        HolidayMasterBean lHolidayMasterBean = holidayMasterBO.findBean(pExecutionContext, lFilterBean);
        return holidayMasterBeanMeta.formatAsJson(lHolidayMasterBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="holiday-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        HolidayMasterBean lFilterBean = new HolidayMasterBean();
        holidayMasterBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<HolidayMasterBean> lHolidayMasterList = holidayMasterBO.findListFromSql(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (HolidayMasterBean lHolidayMasterBean : lHolidayMasterList) {
            lResults.add(holidayMasterBeanMeta.formatAsArray(lHolidayMasterBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Secured(secKey="holiday-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);

        HolidayMasterBean lFilterBean = new HolidayMasterBean();
        holidayMasterBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;

    	final StringBuilder lData = new StringBuilder();
    	List<BeanFieldMeta> lFieldList =  holidayMasterBeanMeta.getFieldListFromNames(lFields);

    	//for displaying header
    	int lType = -1, lCounter=0;
    	if(lFieldList!=null){
    		for(BeanFieldMeta lBeanFieldMeta : lFieldList){
    			if(lData.length() > 0) lData.append(CommonConstants.COMMA);
    			lData.append(lBeanFieldMeta.getLabel());
    			if(lBeanFieldMeta.getName().equals("type")) lType = lCounter;
    			lCounter++;
    		}
            lData.append("\n");
    	}
        List<HolidayMasterBean> lHolidayMasterList = holidayMasterBO.findListFromSql(pExecutionContext, lFilterBean, lFields, lUserBean);
        for (HolidayMasterBean lHolidayMasterBean : lHolidayMasterList) {
        	final Object[] lRow = holidayMasterBeanMeta.formatAsArray(lHolidayMasterBean, null, lFields, true);
            if(lRow != null)
            {
            	for(int lPtr=0; lPtr < lRow.length; lPtr++){
            		if(lPtr>0) lData.append(CommonConstants.COMMA);
            		if(lRow[lPtr]!=null&&lRow[lPtr]!="null"){
                		if(lPtr==lType)
                			lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[lPtr].toString().replaceAll(CommonConstants.COMMA, CommonConstants.COLUMN_SEPARATOR)));
                		else
                			lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[lPtr]));
            		}
            	}
                lData.append("\n");
            }
        }
        
    	return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=\"holidayMaster.csv\"").header("Content-Type", "application/octet-stream").build();
    }
    
    

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="holiday-view")
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<HolidayMasterBean> lHolidayMasterList = holidayMasterBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (HolidayMasterBean lHolidayMasterBean : lHolidayMasterList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lHolidayMasterBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lHolidayMasterBean.getDate());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="holiday-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="holiday-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        HolidayMasterBean lHolidayMasterBean = new HolidayMasterBean();
        List<ValidationFailBean> lValidationFailBeans = holidayMasterBeanMeta.validateAndParse(lHolidayMasterBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        holidayMasterBO.save(pExecutionContext, lHolidayMasterBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="holiday-delete")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        HolidayMasterBean lFilterBean = new HolidayMasterBean();
        lFilterBean.setId(pId);
        holidayMasterBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}