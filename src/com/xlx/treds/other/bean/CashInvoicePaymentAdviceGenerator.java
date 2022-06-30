package com.xlx.treds.other.bean;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.EmailSenders;
import com.xlx.treds.AppConstants.EntityEmail;
import com.xlx.treds.AppInitializer;
import com.xlx.treds.NotificationInfo;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.CIGroupBean;
import com.xlx.treds.auction.bean.FactoredPaymentBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PaymentAdviceBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class CashInvoicePaymentAdviceGenerator {
    private static Logger logger = Logger.getLogger(CashInvoicePaymentAdviceGenerator.class);

    //
    private CompositeGenericDAO<FactoredPaymentBean> factoredPaymentBeanDAO;
    private BeanMeta paymentAdviceBeanMeta;
    private GenericDAO<CIGroupBean> ciGroupDAO;

    public CashInvoicePaymentAdviceGenerator(){
        factoredPaymentBeanDAO = new CompositeGenericDAO<FactoredPaymentBean>(FactoredPaymentBean.class);
        ciGroupDAO = new GenericDAO<CIGroupBean>(CIGroupBean.class);
        paymentAdviceBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PaymentAdviceBean.class);
    }
	public int cashInvoicePayAdvice(Connection pConnection, Date pSettlementDate) throws Exception{
		int lCount = 0;
		Map<String, Map<Long, PaymentAdviceBean>> lDataMap = getCashInvoicePayAdvice(pConnection, pSettlementDate, null);
		if(lDataMap!=null && lDataMap.size() > 0) {
	        System.out.println(lDataMap);
			lCount = sendPaymentAdviceEmail(pConnection , lDataMap);
			return lCount;
		}
		return lCount;
	}    
	
	
	public Map<String, Map<Long, PaymentAdviceBean>> getCashInvoicePayAdvice(Connection pConnection, Date pSettlementDate, String pCVNumber) throws Exception{
        //Key1=Supplier Key2=FUId
        Map<String, Map<Long, PaymentAdviceBean>> lDataMap = new HashMap<String, Map<Long,PaymentAdviceBean>>();
        //
		String lEntityCode = RegistryHelper.getInstance().getString(AppInitializer.REGISTRY_CASH_INVOICE);
        List<String> lSupplierList = new ArrayList<String>();
		StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append(" SELECT * FROM INSTRUMENTS , FACTORINGUNITS LEFT OUTER JOIN OBLIGATIONS ON FUID = OBFUID ");
        lSql.append(" LEFT OUTER JOIN OBLIGATIONSPLITS ON OBSOBID = OBID  ");
        lSql.append(" WHERE FURECORDVERSION > 0 AND INRECORDVERSION > 0 AND OBRECORDVERSION > 0 AND OBSRECORDVERSION > 0 ");
        lSql.append(" AND FUID = INFUID and fusupplier=obtxnentity ");
        //When CVNumber is specified then explicity only one invoice, else list of invoices
        if(StringUtils.isNotEmpty(pCVNumber)) {
            lSql.append(" AND INCounterRefNum = ").append(DBHelper.getInstance().formatString(pCVNumber));
        }else {
            lSql.append(" AND OBDATE = ").append(lDBHelper.formatDate(pSettlementDate));
        }
        lSql.append(" AND INAGGREGATORENTITY = ").append(lDBHelper.formatString(lEntityCode));
        //
        lSql.append(" AND FUSTATUS IN ( ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_1_Settled.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_1_Failed.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_2_Settled.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_2_Failed.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_3_Settled.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_3_Failed.getCode()));
        lSql.append(" ) ");
        //
        lSql.append(" AND OBSTATUS IN ( ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode())).append(",").append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode())).append(" ) ");
        //TODO: ONLY CASH INVOICE
        //ONLY LEG 1 SETTTLED
        //
        List<FactoredPaymentBean> lFactoredPaymentBeanList = factoredPaymentBeanDAO.findListFromSql(pConnection, lSql.toString(), -1);
        //
        if(lFactoredPaymentBeanList==null || lFactoredPaymentBeanList.size()==0) {
        	logger.info("cashInvoicePayAdvice : No data for processing setllement date " + pSettlementDate.toString());
        	return null;
        }
        //
        for (FactoredPaymentBean lBean : lFactoredPaymentBeanList){
        	InstrumentBean lInstrumentBean = lBean.getInstrumentBean();
        	FactoringUnitBean lFactoringUnitBean = lBean.getFactoringUnitBean();
        	ObligationSplitsBean lObligationSplitsBean = lBean.getObligationSplitsBean();
        	//
        	if(!lSupplierList.contains(lFactoringUnitBean.getSupplier())){
        		lSupplierList.add(lFactoringUnitBean.getSupplier());
        	}
        	//this is done when cvNumber is recived.
        	if(pSettlementDate==null && lObligationSplitsBean!=null) {
        		pSettlementDate = lObligationSplitsBean.getSettledDate();
        	}
        	//
        	PaymentAdviceBean lPaymentAdviceBean = null;
    		Map<Long, PaymentAdviceBean> lFUIdData = null;
        	//
        	if(!lDataMap.containsKey(lFactoringUnitBean.getSupplier())){
        		lFUIdData = new HashMap<Long, PaymentAdviceBean>();
        		lDataMap.put(lFactoringUnitBean.getSupplier(), lFUIdData);
        	}else{
        		lFUIdData = lDataMap.get(lFactoringUnitBean.getSupplier());
        	}
        	
        	if(lFUIdData.size() > 0){
            	PaymentAdviceBean lTmpBean = null;
            	//to find the exact fuid and get the corresponding fu bean and the utilize the same 
            	lTmpBean = lFUIdData.get(lFactoringUnitBean.getId());
        		if(lTmpBean!=null){
        			lPaymentAdviceBean = lTmpBean;
        		}else{
        			//if the fuid is not found, get the first bean from the list and copy the same
            		for(Long lFuId : lFUIdData.keySet()){
            			lTmpBean = lFUIdData.get(lFuId);
                		break;
                	}
            		lPaymentAdviceBean = new PaymentAdviceBean();
            		paymentAdviceBeanMeta.copyBean(lTmpBean, lPaymentAdviceBean );
            		lPaymentAdviceBean.setCIGroupBean(null);
            		//fuid
            		lPaymentAdviceBean.setFuId(lFactoringUnitBean.getId());
            		lPaymentAdviceBean.setPaymentReferenceNumber("");
                	lPaymentAdviceBean.setCustomerRefNo("");
                	lPaymentAdviceBean.setObligationSplitsId("");
            		lPaymentAdviceBean.setInstNetAmount(lInstrumentBean.getNetAmount());
            		lFUIdData.put(lFactoringUnitBean.getId(), lPaymentAdviceBean);
        		}
        	}
        	//
        	if(lPaymentAdviceBean == null){
        		lPaymentAdviceBean = new PaymentAdviceBean();
        		//TODO : REMOVE THE CODE BELOW
        		//vendoName=AEName, vendorAddres=CDCorCity, buyerName=AEName, buyerAddres=getAdd(CD), cinNumber=CDCinNumber  
        		lPaymentAdviceBean.setFuId(lFactoringUnitBean.getId());
        		lPaymentAdviceBean.setSettlementDate(pSettlementDate);
        		//
        		lPaymentAdviceBean.setBuyer(lFactoringUnitBean.getPurchaser());
        		//Buyer Address & Buyer Name
        		//
        		lPaymentAdviceBean.setVendorCode("");
        		lPaymentAdviceBean.setPaymentReferenceNumber("");
            	lPaymentAdviceBean.setCustomerRefNo("");
            	lPaymentAdviceBean.setObligationSplitsId("");
        		lPaymentAdviceBean.setInstNetAmount(lInstrumentBean.getNetAmount());
        		//
        		lFUIdData.put(lFactoringUnitBean.getId(), lPaymentAdviceBean);
        	}
        	if(StringUtils.isNotEmpty(lObligationSplitsBean.getPaymentRefNo())){
            	String lTmpPayRefNos =  lPaymentAdviceBean.getPaymentReferenceNumber();
            	if(StringUtils.isNotEmpty(lTmpPayRefNos)){
            		lTmpPayRefNos += ",";
            	}
            	lTmpPayRefNos += lObligationSplitsBean.getPaymentRefNo();
            	lPaymentAdviceBean.setPaymentReferenceNumber(lTmpPayRefNos);
        	}
        	if(lObligationSplitsBean.getId() != null){
            	String lTmp =  lPaymentAdviceBean.getObligationSplitsId();
            	if(StringUtils.isNotEmpty(lTmp)){
            		lTmp += ",";
            	}
            	lTmp += String.valueOf(lObligationSplitsBean.getId()+"_"+lObligationSplitsBean.getPartNumber());
            	lPaymentAdviceBean.setObligationSplitsId(lTmp);
        	}
        	//FETCH THE ADVICE DATA FROM DB AND THEN MERGE
        	CIGroupBean lCiGroupFilterBean = new CIGroupBean();
        	lCiGroupFilterBean.setFuId(lPaymentAdviceBean.getFuId());
        	CIGroupBean lCiGroupBean = ciGroupDAO.findBean(pConnection, lCiGroupFilterBean); 
        	if(lCiGroupBean != null){
        		lPaymentAdviceBean.setCustomerRefNo(lCiGroupBean.getCustomerRefNo());
        		lPaymentAdviceBean.setVendorCode(lCiGroupBean.getVendorCode());
        		lPaymentAdviceBean.setCIGroupBean(lCiGroupBean);
        		lPaymentAdviceBean.setCvNumber(lCiGroupBean.getCvNumber());
        		//TODO: FETCH FROM CIGROUPBEAN
        		//vendoName=AEName, vendorAddres=CDCorCity, buyerName=AEName, buyerAddres=getAdd(CD), cinNumber=CDCinNumber
        		lPaymentAdviceBean.setVendorName(lCiGroupBean.getVendorName());
        		lPaymentAdviceBean.setVendorAddress(lCiGroupBean.getVendorAddress());
        		//
        		lPaymentAdviceBean.setBuyer(lFactoringUnitBean.getPurchaser());
        		//Buyer Address & Buyer Name
        		lPaymentAdviceBean.setBuyerName(lCiGroupBean.getBuyerName());
        		lPaymentAdviceBean.setBuyerAddress(lCiGroupBean.getBuyerAddress());
        		lPaymentAdviceBean.setCinNumber(lCiGroupBean.getCinNumber());
        	}
        //PAYMENTADVICE AND PAYMENT DETAILS WRITE 2 FIELD GROUPS WHICH WILL CREATE THE DATA STRUCTURE BELOW
        }
        return lDataMap;
	}
	
	private int sendPaymentAdviceEmail(Connection pConnection, Map<String, Map<Long, PaymentAdviceBean>> pDataMap) {
		int lCount = 0;
		for (String lSupplier : pDataMap.keySet()){
			Map<String, Object> lDataValues = new HashMap<String, Object>();
	    	List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
	    	String lFactUnitIds = "";
	    	try {
		        List<Object[]> lObjectArrayList = CashInvoicePaymentAdviceGenerator.createPayAdvicePdf(pDataMap.get(lSupplier));
		        for (Object[] lObjArray : lObjectArrayList){
		        	byte[] lPdf = ((ByteArrayOutputStream) lObjArray[1]).toByteArray();
		    		MimeBodyPart lMimeBodyPart = new MimeBodyPart();
		    	    String lFileType = "application/pdf";
		    		lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lPdf, lFileType)));
		    		String lFileName = "CashInvoice.pdf";
		    		CIGroupBean lCIGroupBean = pDataMap.get(lSupplier).get(lObjArray[0]).getCIGroupBean();
		    		if(lCIGroupBean!=null) {
		    			lFileName = lCIGroupBean.getCvNumber()+".pdf";
		    		}
		    		lMimeBodyPart.setFileName(lFileName);
		    		if(lMimeBodyPart!=null){
		        		lAttachList.add(lMimeBodyPart);
		        	}
		    		if(StringUtils.isNotEmpty(lFactUnitIds)){
		    			lFactUnitIds +=",";
		    		}
		    		lFactUnitIds += String.valueOf(lObjArray[0]);

		        }
	    		lDataValues.put("factoringUnitId",lFactUnitIds);
	    		lDataValues.put(EmailSender.ATTACHMENTS, lAttachList);
	    		//
				Map<Long, PaymentAdviceBean> lPayMap = pDataMap.get(lSupplier);
				//TODO: the purchaser can be multiple in future, so the logic here will change and the accumulation will be on Purchaser
				String lTmpPurchaser = "";
				for(Long lFuId : lPayMap.keySet()) {
					PaymentAdviceBean lPaymentAdvBean = lPayMap.get(lFuId);
					lTmpPurchaser = lPaymentAdvBean.getBuyer();
					lDataValues.put("vendorName",lPaymentAdvBean.getVendorName());
					lDataValues.put("vendorAddress",lPaymentAdvBean.getVendorAddress());
					lDataValues.put("vendorCode",lPaymentAdvBean.getVendorCode());
					lDataValues.put("bankReferenceNo",(StringUtils.isNotEmpty(lPaymentAdvBean.getPaymentReferenceNumber())?lPaymentAdvBean.getPaymentReferenceNumber():""));
					lDataValues.put("customerReferenceNo",(StringUtils.isNotEmpty(lPaymentAdvBean.getCustomerRefNo())?lPaymentAdvBean.getCustomerRefNo():""));
//					lDataValues.put("factoringUnitId",lPaymentAdvBean.getFuId());
					lDataValues.put("obligationSplitsId",lPaymentAdvBean.getObligationSplitsId());
				}
				//
	    		List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
	    		Map<String,List<String>> lEmailMap = null; 
	    		//
			    lNotificationInfos.add(new NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_PAYMENTADVICEL1SET_1, EntityEmail.Explicit, lTmpPurchaser , EmailSenders.CC));
			    lNotificationInfos.add(new NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_PAYMENTADVICEL1SET_1, EntityEmail.AdminEmail, lSupplier , EmailSenders.TO));
	    		//
		        lEmailMap = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
		        if (lEmailMap!=null) {
		        	TredsHelper.getInstance().setEmailsToData(lEmailMap, lDataValues);
		        }
		        //
				if(lDataValues != null){
			        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_PAYMENTADVICEINFO, lDataValues);
			        lCount++;
				}	    		
	    	}catch(Exception lEx) {
	    		logger.info("Error while sending payment advice email : "+lSupplier);
	    	}
		}
		return lCount;
	}
	
	private static List<Object[]> createPayAdvicePdf(Map<Long, PaymentAdviceBean> pMap){
		List<Object[]> lObjectArrayList = new ArrayList<Object[]>();
		for(Long lFuId : pMap.keySet()){
			Object[] lObjArray = new Object[]{null, null};
			lObjArray[0] = lFuId;
			ByteArrayOutputStream lByteArrayOutputStream = createPdf(pMap.get(lFuId));
			lObjArray[1] = lByteArrayOutputStream;
			lObjectArrayList.add(lObjArray);	
		}
		return lObjectArrayList;
	}
	
	public static ByteArrayOutputStream createPdf(PaymentAdviceBean pPaymentAdviceBean){
		Document document = new Document(PageSize.A4, 36, 36, 36, 130);
		ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        BigDecimal lNetAmount = BigDecimal.ZERO;
	     try {
	           PdfWriter lPdfWriter = PdfWriter.getInstance(document, lByteArrayOutputStream);
	           document.open();
	           setHeaderContents(document,pPaymentAdviceBean);
	           setHeader(document);
	           setToContent(document,pPaymentAdviceBean);
	           setDetails(document,pPaymentAdviceBean);
	           if(pPaymentAdviceBean.getInstNetAmount() != null){
	        	   setFooter(document,pPaymentAdviceBean.getInstNetAmount());
	           }
	           setSignature(document);
	           setReasonCodeDatails(document);
	           lByteArrayOutputStream.close();
	           document.close();
	           return lByteArrayOutputStream;
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	     return null;
	}
	
	private static void setHeaderContents(Document pDocument, PaymentAdviceBean pPaymentAdviceBean ) throws Exception{
        PdfPTable lTable = new PdfPTable(1);
        lTable.setWidthPercentage(100);
        Chunk lChunk = new Chunk(pPaymentAdviceBean.getBuyerName().toUpperCase());
        lChunk.setFont(new Font(Font.TIMES_ROMAN,16, Font.BOLD));
        PdfPCell lCell = new PdfPCell(new Phrase(lChunk));
        lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        lCell.setBorder(0);
        lTable.addCell(lCell);
        
        lCell = new PdfPCell(new Phrase("Corporate Identity Number: "+pPaymentAdviceBean.getCinNumber()));
        lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        lCell.setBorder(0);
        lTable.addCell(lCell);
        
        lCell = new PdfPCell(new Phrase(pPaymentAdviceBean.getBuyerAddress()));
        lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        lCell.setBorder(0);
        lTable.addCell(lCell);
        
        lCell = new PdfPCell(new Phrase(new Chunk(new LineSeparator())));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        pDocument.add(lTable);
	}
	
	private static void setHeader(Document pDocument) throws Exception{
        Chunk lChunk = new Chunk("e-Invoice Discounting Advice");
        lChunk.setFont(new Font(Font.TIMES_ROMAN,14, Font.BOLD));
        
        PdfPCell lCell = new PdfPCell(new Phrase(lChunk));
        lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        lCell.setMinimumHeight(20f);
        lCell.setBorder(0);
        PdfPTable lTable = new PdfPTable(1);
        lTable.setWidthPercentage(100);
        lTable.addCell(lCell);
        lTable.setSpacingBefore(10f);
        pDocument.add(lTable);
	}
	
	private static void setToContent(Document pDocument, PaymentAdviceBean pPaymentAdviceBean) throws Exception{
		
		PdfPTable lTable = new PdfPTable(1);
	    lTable.setWidthPercentage(100);
        PdfPCell lCell = new PdfPCell(new Phrase(pPaymentAdviceBean.getVendorName()));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase(pPaymentAdviceBean.getVendorAddress()));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("VENDOR CODE: ".concat((pPaymentAdviceBean.getVendorCode()!=null?pPaymentAdviceBean.getVendorCode():""))));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("Transaction Ref No : ".concat(pPaymentAdviceBean.getFuId().toString())));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("CV Number: ".concat((pPaymentAdviceBean.getCvNumber()))));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("Customer Ref No : ".concat(pPaymentAdviceBean.getCustomerRefNo()!=null?pPaymentAdviceBean.getCustomerRefNo():"")));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("Factoring unit id : ".concat(pPaymentAdviceBean.getFuId().toString())));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("Obligation id : ".concat(pPaymentAdviceBean.getObligationSplitsId().toString())));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("Bank Reference No : ".concat((pPaymentAdviceBean.getPaymentReferenceNumber()!=null?pPaymentAdviceBean.getPaymentReferenceNumber():""))));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("\n"));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("Date : "+ FormatHelper.getDisplay(AppConstants.DATE_FORMAT, pPaymentAdviceBean.getSettlementDate())));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("\n"));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("Dear Sir/ Madam,"));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("In accordance with the details below the payment has been done via NACH payment reference "+pPaymentAdviceBean.getFuId().toString()+" on behalf of "+pPaymentAdviceBean.getBuyer()));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("\n"));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        pDocument.add(lTable);
	}
	
	private static BigDecimal setDetails(Document pDocument, PaymentAdviceBean pPaymentAdviceBean) throws Exception{
		BigDecimal lNetAmount = BigDecimal.ZERO;
        PdfPTable lTable = new PdfPTable(7);
        lTable.setWidthPercentage(100);
        PdfPCell lCell = new PdfPCell();
        int headerwidths[] = {10, 15, 15, 15, 15, 15, 15};
        lTable.setWidths(headerwidths);
        CIGroupBean lCiGroupBean = pPaymentAdviceBean.getCIGroupBean();
        String headers[] = lCiGroupBean.getHeaders();
//        String headers[] = { "Sr no.", "Invoice Number", "Invoice Date", "Value Accepted Payable (Rs)", "FI Document No Details" ,"Reason Code – Deduction Amount"};
        for (int i=0; i<headers.length ; i++) {
        	lCell = new PdfPCell(new Phrase(headers[i]));
        	lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            lTable.addCell(lCell);
        }
        List<List<String>> lChildList = lCiGroupBean.getData();
            for (List<String> lList : lChildList){
            	for(String lData : lList){
                	lCell = new PdfPCell(new Phrase(lData));
                	lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    lTable.addCell(lCell);
            	}
            }
        lTable.setHeaderRows(1);
        pDocument.add(lTable);
		return lNetAmount;
	}
	
	private static void setFooter(Document pDocument,BigDecimal pNetAmount) throws Exception{
        PdfPTable lTable = new PdfPTable(1);   
        lTable.setWidthPercentage(40);
        PdfPCell lCell = new PdfPCell(new Phrase("Net Amount : "+pNetAmount));
        lCell.setPaddingRight(50f);
        lTable.addCell(lCell);
        lTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        lTable.setSpacingBefore(10f);
        lTable.setSpacingAfter(10f);
        pDocument.add(lTable);
	}
	
	
	private static void setSignature(Document pDocument) throws Exception{
        PdfPTable lTable = new PdfPTable(1);   
        lTable.setWidthPercentage(100);
        PdfPCell lCell = new PdfPCell(new Phrase(new Chunk(new LineSeparator())));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lCell = new PdfPCell(new Phrase("This is Computer Generated Advice, does not require signature"));
        lCell.setBorder(0);
        lTable.addCell(lCell);
        lTable.setSpacingBefore(10f);
        lTable.setSpacingAfter(10f);
        pDocument.add(lTable);
	}
	
	private static void setReasonCodeDatails(Document pDocument) throws Exception{
		pDocument.newPage();
		PdfPTable lTable = new PdfPTable(2);
        lTable.setWidthPercentage(100);
        PdfPCell lCell = new PdfPCell();
        int headerwidths[] = {20, 80};
        lTable.setWidths(headerwidths);
        String headers[] = { "Reason Code", "Description"};
        for (int i=0; i<headers.length ; i++) {
        	lCell = new PdfPCell(new Phrase(headers[i]));
        	lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            lTable.addCell(lCell);
        }
        List<RefCodeValuesBean> lRefCodeValuesBeans = TredsHelper.getInstance().getRefCodeValues(AppConstants.RC_CASHINVOICE_REASONCODES);
        for (RefCodeValuesBean lBean : lRefCodeValuesBeans){
            	lCell = new PdfPCell(new Phrase(lBean.getValue()));
            	lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                lTable.addCell(lCell);
                lCell = new PdfPCell(new Phrase(lBean.getDesc()));
            	lCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                lTable.addCell(lCell);
        }
        lTable.setHeaderRows(1);
        pDocument.add(lTable);
        
	}
	
	
	public static void main(String...args) throws Exception {
		BeanMetaFactory.createInstance(null);
		AppInitializer lAppInitializer = new AppInitializer(null);
		lAppInitializer.loadTable(null, true);
		List<Map<String,Object>> lList = new ArrayList<Map<String,Object>>();
    	Map<String,Object> lData1 = new HashMap<String,Object>();
        lData1.put("factoringUnitId", "123456");
        lData1.put("vendorName", "ASDF");
        lData1.put("vendorAddress", "adgvsdgsvsvbvrgb");
        lData1.put("vendorCode","vendorCodeAbCD");
        lData1.put("bankReferenceNo", "12121efefefe");
        lData1.put("customerReferenceNo", "5445454efefefef");
        lData1.put("leg1SettlementDate", "25-01-2020");
    	Map<String,Object> lData = new HashMap<String,Object>();
        lData.put("invoiceNo","2020001005");
        lData.put("invoiceAmount","50000");
        lData.put("valueAcceptableAmount","30000");
        lData.put("fidNoDetails","HHDHD121212");
        lData.put("srNo","1");
        lData.put("reasonCode","A");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","2");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","3");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","4");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","5");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","6");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","7");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","8");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","9");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","10");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData = new HashMap<String,Object>();
        lData.put("invoiceNo","20200010013");
        lData.put("invoiceAmount","125000");
        lData.put("valueAcceptableAmount","41000");
        lData.put("fidNoDetails","grgwg212152");
        lData.put("srNo","11");
        lData.put("reasonCode","D");
        lList.add(lData);
        lData1.put("data", lList);
//		CreatePdf(lData1);
	}

}
