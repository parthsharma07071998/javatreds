package com.xlx.treds.master.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.ExcelHelper;
import com.xlx.treds.master.bean.BankBranchDetailBean;

public class BankBranchDetailBO {
    public static final Logger logger = LoggerFactory.getLogger(BankBranchDetailBO.class);
    
    private GenericDAO<BankBranchDetailBean> bankBranchDetailDAO;
    //BANK	IFSC	MICR CODE	BRANCH	ADDRESS	CONTACT	CITY	DISTRICT	STATE
    private final int DATAINDEX_BANKNAME = 0; //this is skipped
    private final int DATAINDEX_IFSC = 1;
    private final int DATAINDEX_MICR = 1;
    private final int DATAINDEX_BRANCH = 2;
    private final int DATAINDEX_ADDRESS = 3;
    private final int DATAINDEX_CONTACT = 7;
    private final int DATAINDEX_CITY = 4;
    private final int DATAINDEX_DISTRICT = 5;
    private final int DATAINDEX_STATE = 6;
    

    public BankBranchDetailBO() {
        super();
        bankBranchDetailDAO = new GenericDAO<BankBranchDetailBean>(BankBranchDetailBean.class);
    }
    
    public BankBranchDetailBean findBean(ExecutionContext pExecutionContext, 
        BankBranchDetailBean pFilterBean) throws Exception {
        BankBranchDetailBean lBankBranchDetailBean = bankBranchDetailDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lBankBranchDetailBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lBankBranchDetailBean;
    }
    
    public List<BankBranchDetailBean> findList(ExecutionContext pExecutionContext, BankBranchDetailBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        StringBuilder lFilter = new StringBuilder();
        if (pFilterBean.getBankCode() != null)
            lFilter.append(" BBDIFSC LIKE ").append(DBHelper.getInstance().formatString(pFilterBean.getBankCode() + "%"));
        bankBranchDetailDAO.appendAsSqlFilter(lFilter, pFilterBean, false);
        String lSql = "SELECT * FROM BankBranchDetail ";
        if (lFilter.length() > 0)
            lSql += " WHERE " + lFilter.toString();
        
        return bankBranchDetailDAO.findListFromSql(pExecutionContext.getConnection(), lSql, 0);
    }
    
    public void save(ExecutionContext pExecutionContext, BankBranchDetailBean pBankBranchDetailBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        BankBranchDetailBean lOldBankBranchDetailBean = null;
        if (pNew) {

            pBankBranchDetailBean.setRecordCreator(pUserBean.getId());
            bankBranchDetailDAO.insert(lConnection, pBankBranchDetailBean);
        } else {
            lOldBankBranchDetailBean = findBean(pExecutionContext, pBankBranchDetailBean);
            bankBranchDetailDAO.getBeanMeta().copyBean(pBankBranchDetailBean, lOldBankBranchDetailBean,"update", null);
            lOldBankBranchDetailBean.setRecordUpdator(pUserBean.getId());
            if (bankBranchDetailDAO.update(lConnection, lOldBankBranchDetailBean) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, BankBranchDetailBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        BankBranchDetailBean lBankBranchDetailBean = findBean(pExecutionContext, pFilterBean);
        lBankBranchDetailBean.setRecordUpdator(pUserBean.getId());
        bankBranchDetailDAO.delete(lConnection, lBankBranchDetailBean);        


        pExecutionContext.commitAndDispose();
    }
    
    public void uploadBranchFile(Connection pConnection, byte[] pData)
    {
		Connection lConnection = null;
		int lSheetIndex = 0, lRowIndex = 0;
    	try
		{
    		Workbook lWorkbook = ExcelHelper.getWorkbook(pData);
    		java.util.Iterator<Sheet> lSheets = lWorkbook.sheetIterator() ;
    		Sheet lSheet  = null;
    		java.util.Iterator<Row> lRows = null;
    		Row lRow = null;
    		BankBranchDetailBean lBBDBean = null;
    		HashMap<String, BankBranchDetailBean> lBBDBeans = new HashMap<String, BankBranchDetailBean>();
    		String lIFSC = null;
    		Long lCreator = new Long(1);
    		int lInsertCount = 0, lUpdateCount=0, lCount=0, lDuplicate=0, lIndex=0;
			String[] lRowData = null;
    		//
    		lConnection = pConnection;
    		List<BankBranchDetailBean> lDbBeans = bankBranchDetailDAO.findList(lConnection, new BankBranchDetailBean(),  new ArrayList<String>());
    		if(lDbBeans!= null)
    		{
    			for(int lPtr=0; lPtr < lDbBeans.size(); lPtr++)
    			{
    				lBBDBean = lDbBeans.get(lPtr);
    				lBBDBeans.put(lBBDBean.getIfsc(), lBBDBean);
    				lBBDBean.setRecordUpdator(lCreator);
    				lBBDBean.setStatus(BankBranchDetailBean.Status.Deleted);
    			}
    		}
    		//
    		while(lSheets.hasNext())
			{
        		lSheet = lSheets.next();
    			lRows = lSheet.rowIterator();
    			//lRow = lRows.next();//skip first row header
    			logger.info("**********************************");
    			logger.info(lSheet.getSheetName());
    			logger.info("**********************************");
    			lCount=0;
    			lDuplicate=0;
    			lIndex=0;
    			int lColCount = 0;
    			lRowIndex = 0;
    			lSheetIndex++;
    			while(lRows.hasNext())
    			{
    				lRow = (Row)lRows.next();
    				lRowIndex++;
    	            if (lRow.getRowNum() == 0) // header row. count columns
    	            {
        	            Iterator<Cell> lCells = lRow.cellIterator();
    	                while (lCells.hasNext())
    	                {
    	                    lCells.next();
    	                    lColCount++;
    	                }
    	                logger.debug("Column count : " + lColCount);
    	                continue;
    	            }
    	            // data rows
    	           	lRowData =  ExcelHelper.getRowData(lRow, lColCount);
    	            if(lRowData == null) continue;
    				lIFSC = lRowData[DATAINDEX_IFSC];
    				if(lIFSC == null) continue;
    				lBBDBean = lBBDBeans.get(lIFSC);
    				if(lBBDBean==null)
    				{
    					lBBDBean = new BankBranchDetailBean();
        				lBBDBean.setIfsc(lIFSC);
        				lBBDBeans.put(lIFSC, lBBDBean);
        				lCount++;
    				}
    				else
    				{
    					logger.info("Duplicate :: Row Index : " + lIndex + " : IFSC : " + lIFSC);
    					lDuplicate++;
    				}
    				lIndex++;
    				lBBDBean.setStatus(BankBranchDetailBean.Status.Active);
    				//lBBDBean.setMicrcode(lRowData[DATAINDEX_MICR]);
    				lBBDBean.setMicrcode(null);
    				lBBDBean.setBranchname(lRowData[DATAINDEX_BRANCH]);
    				lBBDBean.setAddress(lRowData[DATAINDEX_ADDRESS]);
    				lBBDBean.setContact(lRowData[DATAINDEX_CONTACT]);
    				lBBDBean.setCity(lRowData[DATAINDEX_CITY]);
    				lBBDBean.setDistrict(lRowData[DATAINDEX_DISTRICT]);
    				lBBDBean.setState(lRowData[DATAINDEX_STATE]);
    			}
    			logger.info("**********************************");
    			logger.info("Summary :: " + lSheet.getSheetName() + " : Count : " + lCount + " : Duplicate : " +lDuplicate + " : Total : "+ (lCount+lDuplicate));
    			logger.info("**********************************");
			}
    		
    		lIndex=0;
    		for(String lIFSCKey : lBBDBeans.keySet())
    		{
    			lIndex++;
    			lBBDBean= lBBDBeans.get(lIFSCKey);
    			if(lBBDBean.getRecordCreator()!=null)
    			{
    				try
    				{
        				bankBranchDetailDAO.update(lConnection, lBBDBean);
        				lUpdateCount++;
    				}
    				catch (Exception e)
    				{
    					logger.info("Exception in Update : " +  e.getMessage()+" : "+lBBDBean.toString());
    				}
    			}
    			else
    			{
    				try
    				{
        				lBBDBean.setRecordCreator(lCreator);
        				bankBranchDetailDAO.insert(lConnection, lBBDBean);
        				lInsertCount++;    					
    				}
    				catch (Exception e)
    				{
    					logger.info("Exception in Insert : " +  e.getMessage()+" : "+lBBDBean.toString());
    				}
    			}
    		}
			logger.info("**********************************");
			logger.info("Summary :: " + " Inserted : "+lInsertCount + " : Updated : "+ lUpdateCount);
			logger.info("**********************************");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info("ERROR IN IFSC : SheetIndex : " + lSheetIndex + " : RowIndex : " + lRowIndex + " :: "  + e.getMessage());
		}
    	finally
    	{
    		if (lConnection!=null)
    		{
				try
				{
					lConnection.close();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
    		}
    		lConnection = null;
    	}
    }
    
    public void uploadBranchFile(Connection pConnection, String pFilePath)
    {
    	byte[] lData = null;
    	String lFileName = null;
    	File lFile = null;
    	//
    	lFileName = pFilePath;
    	lFile = new File(lFileName);
    	lData=readContentIntoByteArray(lFile);
    	lFile = null;

    	this.uploadBranchFile(pConnection, lData);
    }
    
    private static byte[] readContentIntoByteArray(File file)
    {
      FileInputStream fileInputStream = null;
      byte[] bFile = new byte[(int) file.length()];
      try
      {
         //convert file into array of bytes
         fileInputStream = new FileInputStream(file);
         fileInputStream.read(bFile);
         fileInputStream.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return bFile;
    }
    
    
    public static void main(String[] args) throws IOException
    {
    	byte[] lData = null;
    	BankBranchDetailBO lBankBranchDetailBO = null;
    	String lFileName = null;
    	File lFile = null;
    	Connection lConnection = null;
    	//
    	try
		{
        	lFileName = "C:\\Users\\prasad\\Downloads\\68774_Old.xls";
    		args = new String[] { lFileName };
        	if(args.length > 0){
        		lFileName = args[0];
        		
                Map<String, Object> lConfig = new HashMap<String, Object>();
                lConfig.put(BeanMetaFactory.KEY_DATE_FORMAT, "dd-MMM-yyyy");
                lConfig.put(BeanMetaFactory.KEY_DECIMAL_FORMAT, "##,##,##,##,##,##,##,###.00");
                BeanMetaFactory.createInstance(lConfig);
                
            	lConnection = DBHelper.getInstance().getConnection();
            	lBankBranchDetailBO = new BankBranchDetailBO();
            	lBankBranchDetailBO.uploadBranchFile(lConnection, lFileName);
        	}
        	lFile = new File(lFileName);
        	lData=readContentIntoByteArray(lFile);
        	lFile = null;

		}
		catch (Exception e)
		{
			logger.info(e.getMessage());
			e.printStackTrace();
		}finally{
            if (lConnection != null) {
                try {
                    lConnection.close();
                } catch (Exception lException) {
					logger.error("BankBranchDetailBO : Error while closing database connection", lException);
                }
            }

		}
    }
    
}
