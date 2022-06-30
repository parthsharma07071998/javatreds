package com.xlx.treds.bill.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import org.xhtmlrenderer.pdf.ITextRenderer;

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
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.bill.bean.BillBean;
import com.xlx.treds.bill.bo.BillBO;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/bill")
public class BillResource {

    private BillBO billBO;
    private BeanMeta billBeanMeta;
	private List<String> defaultListFields, lovFields;
    private FactoringUnitBO factoringUnitBO;
	
	private static final int COMPRESSION_LEVEL = 1;
	
    public BillResource() {
        super();
        billBO = new BillBO();
        billBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BillBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","billNumber","billYearMonth","billDate","entity","entName","entGstn","entPan","entLine1","entLine2","entLine3","entCountry","entState","entDistrict","entCity","entZipCode","entSalutation","entFirstName","entMiddleName","entLastName","entEmail","entTelephone","entMobile","entFax","tredsName","tredsGstn","tredsLine1","tredsLine2","tredsLine3","tredsCountry","tredsState","tredsDistrict","tredsCity","tredsZipCode","tredsEmail","tredsTelephone","tredsMobile","tredsFax","tredsPan","tredsCin","tredsNatureOfTrans","tredsSACCode","tredsSACDesc","amount","grossAmount","cgst","sgst","igst","cgstSurcharge","sgstSurcharge","igstSurcharge","cgstValue","sgstValue","igstValue"});
        lovFields = Arrays.asList(new String[]{"id","billNumber"});
        factoringUnitBO = new FactoringUnitBO();
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
        pRequest.getRequestDispatcher("/WEB-INF/bill.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="bill-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        BillBean lFilterBean = new BillBean();
        lFilterBean.setId(pId);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
        	lFilterBean.setEntity(lUserBean.getDomain());
        BillBean lBillBean = billBO.findBean(pExecutionContext, lFilterBean);
        return billBeanMeta.formatAsJson(lBillBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="bill-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        BillBean lFilterBean = new BillBean();
        billBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
        	lFilterBean.setEntity(lUserBean.getDomain());

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<BillBean> lBillList = billBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (BillBean lBillBean : lBillList) {
            lResults.add(billBeanMeta.formatAsArray(lBillBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="bill-view")
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<BillBean> lBillList = billBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (BillBean lBillBean : lBillList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lBillBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lBillBean.getBillNumber());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="bill-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
       if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
    	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="bill-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        BillBean lBillBean = new BillBean();
        List<ValidationFailBean> lValidationFailBeans = billBeanMeta.validateAndParse(lBillBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        billBO.save(pExecutionContext, lBillBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="bill-save")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        BillBean lFilterBean = new BillBean();
        lFilterBean.setId(pId);
        billBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Secured(secKey="bill-view")
    @Path("/taxinvoice/{id}")
    public void taxInvoiceHtml(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        //if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
        //	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        BillBean lFilterBean = new BillBean();
        lFilterBean.setId(pId);
        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
        	lFilterBean.setEntity(lUserBean.getDomain());
        BillBean lBillBean = billBO.findBean(pExecutionContext, lFilterBean);
        if(lBillBean == null){
        	throw new CommonBusinessException("Bill not found for Id : " + pId);
        }
        // Fetching Factoring Unit from Bill Id
        List<Map<String, Object>> lList = factoringUnitBO.getBillWiseFactoringUnit(pExecutionContext,lBillBean.getId(), lBillBean.getEntity());
        //
        pRequest.setAttribute("data", billBeanMeta.formatAsMap(lBillBean));
        pRequest.setAttribute("factoringUnitDetails", lList);
        pRequest.getRequestDispatcher("/WEB-INF/taxinvoicehtml.jsp").forward(pRequest, pResponse);
    }

//    @GET
//    @Produces("application/x-pdf")
//    @Path("/taxinvoicepdf/{id}")
//    public Response taxInvoicePDF(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
//            , @PathParam("id") Long pId) throws Exception {
//        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
////        if(!AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain()))
////        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
//        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
//        ITextRenderer renderer = new ITextRenderer();
//        String lUrl = TredsHelper.getInstance().getApplicationURL() + "bill/taxinvoice/" + pId ;
//        lUrl += "?loginKey=" + AuthenticationHandler.getInstance().getLoginKey(pRequest);
//        //the font has been kept in the classpath
//        //renderer.getFontResolver().addFont("arial.ttf",true);
//        renderer.setDocument(lUrl);
//        renderer.layout();
//        renderer.createPDF(lByteArrayOutputStream);
//        lByteArrayOutputStream.close();
//        //this is just for the billnumber which has to be written to the file name
//        BillBean lFilterBean = new BillBean();
//        lFilterBean.setId(pId);
//        BillBean lBillBean = billBO.findBean(pExecutionContext, lFilterBean);
//        if(lBillBean == null){
//        	throw new CommonBusinessException("Bill not found for Id : " + pId);
//        }
//        //
//        String lFileName = "taxinvoice_"+lBillBean.getBillNumber()+".pdf" ;
//        return sendFileContents(new Object[]{lFileName, lByteArrayOutputStream.toByteArray()});
//    }
    
    @POST
    @Produces("application/zip")
    @Path("/taxinvoicezip")
    public Response taxInvoiceZip(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            ,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        String lFile = "Bills.zip";
        Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
        List<Integer> lIds = new ArrayList<Integer>();
        lIds = (List<Integer>) lMap.get("ids");
        Map<String, byte[]> lFiles = new HashMap<String,byte[]>();
        for (Integer lId:lIds){
            ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            String lUrl = TredsHelper.getInstance().getApplicationURL() + "bill/taxinvoice/" + lId ;
            lUrl += "?loginKey=" + AuthenticationHandler.getInstance().getLoginKey(pRequest);
            //the font has been kept in the classpath
            //renderer.getFontResolver().addFont("arial.ttf",true);
            renderer.setDocument(lUrl);
            renderer.layout();
            renderer.createPDF(lByteArrayOutputStream);
            lByteArrayOutputStream.close();
            //this is just for the billnumber which has to be written to the file name
            BillBean lFilterBean = new BillBean();
            lFilterBean.setId(lId.longValue());
            BillBean lBillBean = billBO.findBean(pExecutionContext, lFilterBean);
            if(lBillBean == null){
            	throw new CommonBusinessException("Bill not found for Id : " + lId);
            }
            //
            String lFileName = "taxinvoice_"+lBillBean.getBillNumber()+".pdf" ;
            lFiles.put(lFileName, lByteArrayOutputStream.toByteArray());
        }
  	  return Response.ok().entity(new StreamingOutput(){
          @Override
          public void write(OutputStream output)
             throws IOException, WebApplicationException {
				ZipOutputStream zipout = new ZipOutputStream(output);
				zipout.setComment("Created by jsp File Browser v. ");
				zipout.setLevel(COMPRESSION_LEVEL);
				for (String lFileName : lFiles.keySet()){
					zipout.putNextEntry(new ZipEntry(lFileName.replace("/", "_")));
					BufferedInputStream lInputStream = new BufferedInputStream(new ByteArrayInputStream(lFiles.get(lFileName)));
					byte buffer[] = new byte[0xffff];
					try{
						int b;
						while ((b = lInputStream.read(buffer)) != -1){
							zipout.write(buffer, 0, b);
						}
					}catch(Exception e){
						
					}
					lInputStream.close();
				}
				//
				zipout.closeEntry();
				zipout.finish();
				output.flush();
             //
             //
          }
      }).header("Content-Disposition", "attachment; filename="+lFile).header("Content-Type", "application/zip").build();
  }
    
    private Response sendFileContents(Object[] pFileDetails) throws Exception {
        String lFileName = (String) pFileDetails[0];
        final byte[] lData = (byte[]) pFileDetails[1];

        StreamingOutput lStreamingOutput = new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData);
               output.flush();
            }
        };
        String lContentType = Files.probeContentType(Paths.get(lFileName));
        return Response.ok(lStreamingOutput, lContentType).header("content-disposition", "attachment; filename = "+lFileName).build();
    }
    
}