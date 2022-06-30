package com.xlx.treds.other.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.bill.bean.BillBean;
import com.xlx.treds.other.bean.FactoredReportBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/factoredreport")
public class FactoredReportResource {

    private BeanMeta factoredReportBeanMeta;
	private List<String> defaultListFields, lovFields;
	
	private static final int COMPRESSION_LEVEL = 1;
	
    public FactoredReportResource() {
        super();
        factoredReportBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoredReportBean.class);
        defaultListFields = Arrays.asList(new String[]{"fuId","fuStatus","inInstNumber","inInstDate","inGoodsAcceptanceDate","l2Date","inInstImage","inNetAmount","inDeductions","fuAamount","inSalesCategory","inFuAcceptDateTime","l1Date","purchaser","supplier"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew ) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/factoredreport.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured (secKey = "report-factoredReport")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        FactoredReportBean lFilterBean = new FactoredReportBean();
        factoredReportBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<FactoredReportBean> lFactoredReportList = (List<FactoredReportBean>) TredsHelper.getInstance().getFactoredReportFinancier(pExecutionContext.getConnection(), lFilterBean, false ,lFields);
        return new JsonBuilder(lFactoredReportList).toString();
    }
    
    @POST
    @Produces("application/zip")
    @Path("/download")
    public Response taxInvoiceZip(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            ,String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Map<String, byte[]> lFilesHash = new HashMap<String,byte[]>();
        FactoredReportBean lFilterBean = new FactoredReportBean();
        factoredReportBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<FactoredReportBean> lFactoredReportList = (List<FactoredReportBean>) TredsHelper.getInstance().getFactoredReportFinancier(pExecutionContext.getConnection(), lFilterBean, true ,lFields);
        TredsHelper.getInstance().getFactoredReportExcel(lFactoredReportList,lFilesHash);
        return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
  				ZipOutputStream zipout = new ZipOutputStream(output);
  				zipout.setComment("Created by jsp File Browser v. ");
  				zipout.setLevel(COMPRESSION_LEVEL);
  				if (!lFilesHash.isEmpty()) {
  					for(String lKey : lFilesHash.keySet()) {
  	  					try {
  	  						if (lFilesHash.get(lKey)!=null) {
  	  							TredsHelper.getInstance().addZipToFile(zipout, lKey, lFilesHash.get(lKey));
  	  						}
  						} catch (Exception e) {
  							e.printStackTrace();
  						}
  	  				}
  				}
  				zipout.closeEntry();
  				zipout.finish();
  				output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=FactoredReport.zip").header("Content-Type", "application/zip").build();
    }


}