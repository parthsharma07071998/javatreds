package com.xlx.treds.monitor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.istack.internal.ByteArrayDataSource;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.report.ExcelConvertor;
import com.xlx.commonn.report.ReportFactory;

import groovy.json.JsonSlurper;

public class BulkReportGenerator {
	private static final Logger logger = LoggerFactory.getLogger(BulkReportGenerator.class);
	private static final String FIELD_NAME = "name";
	private static final String FIELD_EMAILIDS = "emailIds";
	private static final String FIELD_EMAILTEMPLATE = "emailTemplate";
	private static final String FIELD_REPORTS = "reports";
	private static final String FIELD_ID = "id";
	private static final String FIELD_FILTER = "filter";
	
	private String name;
	private List<String> emailIds;
	private String emailTemplate;
	private List<ReportBean> reportList;
	
	public BulkReportGenerator(String pJsonFile) {
		super();
		InputStream lInputStream = null;
        try {
            logger.info("Parsing Bulk Report settings file " + pJsonFile);
            lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pJsonFile);
            JsonSlurper lJsonSlurper = new JsonSlurper();
            if (lInputStream == null) {
                logger.info("Could not load meta file : " + pJsonFile);
            }
            Map<String, Object> lConfig = (Map<String, Object>)lJsonSlurper.parse(lInputStream);
            name = (String)lConfig.get(FIELD_NAME);
            emailIds = (List<String>)lConfig.get(FIELD_EMAILIDS);
            emailTemplate = (String)lConfig.get(FIELD_EMAILTEMPLATE);
            reportList = new ArrayList<BulkReportGenerator.ReportBean>();
            List<Map<String, Object>> lReports = (List<Map<String, Object>>)lConfig.get(FIELD_REPORTS);
            reportList = new ArrayList<ReportBean>();
            for (Map<String, Object> lMap : lReports) {
            	reportList.add(new ReportBean((String)lMap.get(FIELD_ID), (Map<String,Object>)lMap.get(FIELD_FILTER)));
            }
        } catch (Exception lException) {
        	logger.error("Error while reading config file " + pJsonFile, lException);
        }
	}
	
	public FileDownloadBean generateBulkReport(Connection pConnection) throws Exception {
		ExcelConvertor lExcelConvertor = new ExcelConvertor();
		Workbook lWorkbook = new XSSFWorkbook();
		Sheet lSummarySheet = lWorkbook.createSheet("Summary");
		String[] lSummaryHeaders = new String[]{"Sr.No.", "Report Name", "Count"};
		List<Object[]> lSummaryData = new ArrayList<Object[]>();
		int lPtr = 1;
		for (ReportBean lReportBean : reportList) {
			DefaultHandler lHandler = ReportFactory.getInstance().getHandler(lReportBean.id);
			if (lHandler != null) {
				List<Object[]> lData = lHandler.getData(pConnection, lReportBean.filterMap, null);
				lExcelConvertor.addSheetToWorkbook(lWorkbook, lData, lHandler.getLabel(), 
						lHandler.getBeanMeta().getFieldLabelList(null, lHandler.getListFields()).toArray(new String[0]), null);
				lSummaryData.add(new Object[]{Integer.valueOf(lPtr++),  lHandler.getLabel(), new Integer(lData.size())});
				logger.info(lReportBean.id + " " + lData.size());
			}
		}
		lExcelConvertor.addDataTableToSheet(lWorkbook, lSummarySheet, lSummaryData, "Summary", lSummaryHeaders, null);
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        try {
            lWorkbook.write(lByteArrayOutputStream);
        } finally {
            lByteArrayOutputStream.close();
        }
        FileDownloadBean lFileDownloadBean = new FileDownloadBean(name + "." + ExcelConvertor.EXTENSION, 
        		lByteArrayOutputStream.toByteArray(), ExcelConvertor.CONTENTTYPE);
        return lFileDownloadBean;
	}
	
	public void sendEmail(FileDownloadBean pFileDownloadBean) throws Exception {
		logger.info("Sending email..");
		if (emailIds.isEmpty())
			return;
		Map<String, Object> lData = new HashMap<String, Object>();
		lData.put("date", BeanMetaFactory.getInstance().getDateFormatter().format(new Date(System.currentTimeMillis())));

    	MimeBodyPart lMimeBodyPart = new MimeBodyPart();
		lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(pFileDownloadBean.getContent(), 
				pFileDownloadBean.getContentType())));
		lMimeBodyPart.setFileName(pFileDownloadBean.getFileName());

		List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
		lAttachList.add(lMimeBodyPart);
		lData.put(EmailSender.ATTACHMENTS, lAttachList);
		lData.put(EmailSender.TO, emailIds);
    	EmailSender.getInstance().addMessage(emailTemplate, lData);
		logger.info("Email sent!");
	}
	
	public static final class ReportBean {
		private String id;
		private Map<String, Object> filterMap;
		public ReportBean(String pId, Map<String, Object> pFilterMap) {
			super();
			id = pId;
			filterMap = pFilterMap;
		}
	}

	public static void main(String[] pArgs) throws Exception {
		BeanMetaFactory.createInstance(null);
		Connection lConnection = DBHelper.getInstance().getConnection();
		BulkReportGenerator lBulkReportGenerator = new BulkReportGenerator("BulkReportConfig.json"); 
		FileDownloadBean lFileDownloadBean = lBulkReportGenerator.generateBulkReport(lConnection);
		lBulkReportGenerator.sendEmail(lFileDownloadBean);
		FileUtils.writeByteArrayToFile(new File("d:\\temp\\"+lFileDownloadBean.getFileName()), lFileDownloadBean.getContent());
		lConnection.close();
	}
	
}
