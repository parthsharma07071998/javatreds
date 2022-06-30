package com.xlx.treds.auction.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.PurchaserSupplierCapRateBean;
import com.xlx.treds.auction.bo.PurchaserSupplierCapRateBO;
import com.xlx.treds.entity.bean.AppEntityBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/caprate")
public class PurchaserSupplierCapRateResource {

    private PurchaserSupplierCapRateBO purchaserSupplierCapRateBO;
	private List<String> defaultListFields, lovFields;
	
    public PurchaserSupplierCapRateResource() {
        super();
        purchaserSupplierCapRateBO = new PurchaserSupplierCapRateBO();
        defaultListFields = Arrays.asList(new String[]{"id","entityCode","counterEntityCode","fromHaircut","toHaircut","fromUsance","toUsance","capRate"});
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
        pRequest.getRequestDispatcher("/WEB-INF/pursupcaprate.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="caprate-view")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @QueryParam("counterEntity") String pCounterEntityCode) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PurchaserSupplierCapRateBean lFilterBean = new PurchaserSupplierCapRateBean();
        if(lUserBean==null)
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{lUserBean.getDomain()});
        if(lAppEntityBean==null)
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        lFilterBean.setEntityCode(lUserBean.getDomain());
        if(lAppEntityBean.isPurchaser())
        	lFilterBean.setCounterEntityCode(lUserBean.getDomain());
        else if (lAppEntityBean.isSupplier() && !StringUtils.isEmpty(pCounterEntityCode))
        	lFilterBean.setCounterEntityCode(pCounterEntityCode);
        else
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        HashMap<String, Object> lCapRateMatrix = purchaserSupplierCapRateBO.getPurchSuppCapRateMatrix(pExecutionContext, lFilterBean, null, lUserBean);        
        return new JsonBuilder(lCapRateMatrix).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="caprate-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	Map<String, Object> lCapRateMatrix = null;
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        lCapRateMatrix = (Map<String, Object>)new JsonSlurper().parseText(pMessage);
        purchaserSupplierCapRateBO.setPurchSuppCapRateMatrix(pExecutionContext, lCapRateMatrix, lUserBean);
    }


}