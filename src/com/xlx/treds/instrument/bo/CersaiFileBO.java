package com.xlx.treds.instrument.bo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bo.FileUploadHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.instrument.bean.CersaiFileBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.user.bean.AppUserBean;

public class CersaiFileBO {
    private static final Logger logger = LoggerFactory.getLogger(CersaiFileBO.class);
    private static int INSTRUMENTS_PER_FILE = 999;
    private GenericDAO<CersaiFileBean> cersaiFileDAO;

    public CersaiFileBO() {
        super();
        cersaiFileDAO = new GenericDAO<CersaiFileBean>(CersaiFileBean.class);
    }
    
    public CersaiFileBean findBean(ExecutionContext pExecutionContext, 
        CersaiFileBean pFilterBean) throws Exception {
        CersaiFileBean lCersaiFileBean = cersaiFileDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lCersaiFileBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lCersaiFileBean;
    }
    
    public List<CersaiFileBean> findList(ExecutionContext pExecutionContext, CersaiFileBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())) {
            pFilterBean.setFinancier(pUserBean.getDomain());
        }
        return cersaiFileDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    // this function is not used .
    public void save(ExecutionContext pExecutionContext, CersaiFileBean pCersaiFileBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        CersaiFileBean lOldCersaiFileBean = null;
        if (pNew) {

            cersaiFileDAO.insert(lConnection, pCersaiFileBean);
        } else {
            lOldCersaiFileBean = findBean(pExecutionContext, pCersaiFileBean);
            

            if (cersaiFileDAO.update(lConnection, pCersaiFileBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, CersaiFileBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        CersaiFileBean lCersaiFileBean = findBean(pExecutionContext, pFilterBean);
        cersaiFileDAO.delete(lConnection, lCersaiFileBean);        


        pExecutionContext.commitAndDispose();
    }

    public Map<Integer,Integer> getCersaiFileInstrumentCounts(Connection pConnection, String pFinancierCode, Date pLeg1SettlementDate){
    	Map<Integer,Integer> lFileIdwiseInstrumentCount = new HashMap<Integer, Integer>();
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT CersaiFileId ");
    	lSql.append(" ,  Sum(PendingInsts) PendingInsts ");
    	lSql.append(" FROM (  ");

    	lSql.append(" SELECT InCersaiFileId CersaiFileId , Count(InId) PendingInsts ");
    	lSql.append(" FROM OBLIGATIONS, FACTORINGUNITS, INSTRUMENTS ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 AND FURECORDVERSION > 0 AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID AND FUID = INFUID ");
    	lSql.append(" AND INGROUPFLAG IS NULL ");
    	//lSql.append(" AND InCersaiFileId IS NULL ");
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
    	lSql.append(" AND OBSTATUS = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Success.getCode()));
    	lSql.append(" AND OBTXNENTITY = ").append(DBHelper.getInstance().formatString(pFinancierCode));
    	lSql.append(" AND FUSTATUS = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Leg_1_Settled.getCode()));
    	lSql.append(" AND OBDate = ").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate));
    	lSql.append(" AND to_char(OBDate,'DD-MM-YYYY') = to_char(").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate)).append(",'DD-MM-YYYY')  ");
    	lSql.append(" GROUP BY  InCersaiFileId ");
    	//
    	lSql.append(" UNION ");
    	//
    	lSql.append(" SELECT InCersaiFileId CersaiFileId , Count(InId) PendingInsts ");
    	lSql.append(" FROM INSTRUMENTS ");
    	lSql.append(" WHERE INRECORDVERSION > 0 ");
    	lSql.append(" AND INGROUPFLAG IS NULL ");
    	//lSql.append(" AND InCersaiFileId IS NULL ");
    	lSql.append(" AND INGROUPINID IS NOT NULL ");
    	lSql.append(" AND INGROUPINID IN ");
    	//
    	lSql.append(" ( ");
    	lSql.append(" SELECT INID InstrumentId  FROM OBLIGATIONS, FACTORINGUNITS, INSTRUMENTS ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 AND FURECORDVERSION > 0 AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID AND FUID = INFUID ");
    	lSql.append(" AND INGROUPFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode()));
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
    	lSql.append(" AND OBSTATUS = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Success.getCode()));
    	lSql.append(" AND OBTXNENTITY = ").append(DBHelper.getInstance().formatString(pFinancierCode));
    	lSql.append(" AND FUSTATUS = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Leg_1_Settled.getCode()));
    	lSql.append(" AND OBDate = ").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate));
    	lSql.append(" AND to_char(OBDate,'DD-MM-YYYY') = to_char(").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate)).append(",'DD-MM-YYYY')  ");
    	lSql.append(" ) ");
    	lSql.append(" GROUP BY InCersaiFileId ");
    	//
    	lSql.append(" ) ");
    	lSql.append(" GROUP BY  CersaiFileId ");

    	
		Statement lStatement;
		int lPendingInstCount;
		int lCersaiFileId = 0;
		try {
			lStatement = pConnection.createStatement();
			logger.info(lSql.toString());
    		ResultSet lResultSet  = lStatement.executeQuery(lSql.toString());
    		while(lResultSet.next()){
    			if(lResultSet.getString("CersaiFileId") == null ){
    				lCersaiFileId = 0;
    			}else{
    				lCersaiFileId = lResultSet.getInt("CersaiFileId");
    			}
    			lPendingInstCount = lResultSet.getInt("PendingInsts");
    			lFileIdwiseInstrumentCount.put(lCersaiFileId, lPendingInstCount);
    		}
		} catch (Exception e) {
			logger.info("Error getPendingInstForCersaiFiles : " + e.getMessage());
		}
    	return lFileIdwiseInstrumentCount;
    }
    
    private Map<String,List<Long>> getCersaiFileInstruments(Connection pConnection, String pFinancierCode, Date pLeg1SettlementDate){
    	Map<String, List<Long>> lRtnMap = new HashMap<String, List<Long>>();
    	List<Long> lInstIdList = new ArrayList<Long>();
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT INID InstrumentId , INPURCHASER Purchaser, INSUPPLIER Supplier  FROM OBLIGATIONS, FACTORINGUNITS, INSTRUMENTS ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 AND FURECORDVERSION > 0 AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID AND FUID = INFUID ");
    	lSql.append(" AND INGROUPFLAG IS NULL ");
    	lSql.append(" AND InCersaiFileId IS NULL ");
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
    	lSql.append(" AND OBSTATUS = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Success.getCode()));
    	lSql.append(" AND OBTXNENTITY = ").append(DBHelper.getInstance().formatString(pFinancierCode));
    	lSql.append(" AND FUSTATUS = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Leg_1_Settled.getCode()));
    	lSql.append(" AND OBDate = ").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate));
    	lSql.append(" AND to_char(OBDate,'DD-MM-YYYY') = to_char(").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate)).append(",'DD-MM-YYYY')  ");
    	//
    	lSql.append(" UNION ");
    	//
    	lSql.append(" SELECT INID InstrumentId , INPURCHASER Purchaser, INSUPPLIER Supplier FROM INSTRUMENTS ");
    	lSql.append(" WHERE INRECORDVERSION > 0 ");
    	lSql.append(" AND INGROUPFLAG IS NULL ");
    	lSql.append(" AND InCersaiFileId IS NULL ");
    	lSql.append(" AND INGROUPINID IS NOT NULL ");
    	lSql.append(" AND INGROUPINID IN ");
    	//
    	lSql.append(" ( ");
    	lSql.append(" SELECT INID InstrumentId  FROM OBLIGATIONS, FACTORINGUNITS, INSTRUMENTS ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 AND FURECORDVERSION > 0 AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID AND FUID = INFUID ");
    	lSql.append(" AND INGROUPFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode()));
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
    	lSql.append(" AND OBSTATUS = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Success.getCode()));
    	lSql.append(" AND OBTXNENTITY = ").append(DBHelper.getInstance().formatString(pFinancierCode));
    	lSql.append(" AND FUSTATUS = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Leg_1_Settled.getCode()));
    	lSql.append(" AND OBDate = ").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate));
    	lSql.append(" AND to_char(OBDate,'DD-MM-YYYY') = to_char(").append(DBHelper.getInstance().formatDate(pLeg1SettlementDate)).append(",'DD-MM-YYYY')  ");
    	lSql.append(" ) ");
    	//
    	
		Statement lStatement;
		Long lInstId;
		String lPurSupKey=null;
		
		try {
			lStatement = pConnection.createStatement();
			logger.info(lSql.toString());
    		ResultSet lResultSet  = lStatement.executeQuery(lSql.toString());
    		while(lResultSet.next()){
    			lInstId = lResultSet.getLong("InstrumentId");
    			lPurSupKey = lResultSet.getString("Purchaser")+CommonConstants.KEY_SEPARATOR+lResultSet.getString("Supplier");
    			if (!lRtnMap.containsKey(lPurSupKey)) {
    				lRtnMap.put(lPurSupKey,new ArrayList<>());
				}
    			lInstIdList = lRtnMap.get(lPurSupKey);
    			lInstIdList.add(lInstId);
    		}
		} catch (Exception e) {
			logger.info("Error getPendingInstForCersaiFiles : " + e.getMessage());
		}
    	return lRtnMap;
    }
    
    public List<String> updateInstruments(Connection pConnection, AppUserBean pAppUserBean, Date pLeg1SettlementDate) throws CommonBusinessException{
    	String lFinancierCode = pAppUserBean.getDomain();
    	List<String> lReturnCersaiFileNames = new ArrayList<String>();
    	Map<String,List<Long>> lMap = getCersaiFileInstruments(pConnection,lFinancierCode,pLeg1SettlementDate);
    	for (List<Long> lInstIdList:lMap.values()) {
        	StringBuilder lSql = new StringBuilder();
        	int lUpdateCount = 0;
        	Long lCfId = null;
        	Long lSerialnumber = null;
        	if(lInstIdList!=null && !lInstIdList.isEmpty()){
        		List<List<Long>> lPerFileList = new ArrayList<List<Long>>();
        		List<Long> lTmpList = null;
        		for(int lPtr=0; lPtr < lInstIdList.size(); lPtr++){
        			if(lPtr==0 || (lPtr%INSTRUMENTS_PER_FILE)==0){
        				lTmpList = new ArrayList<Long>();
        				lPerFileList.add(lTmpList);
        			}
        			lTmpList.add(lInstIdList.get(lPtr));
        		}
        		int lLoopCount = 0;
    			Statement lStatement = null;
        		for(List<Long> lFileList : lPerFileList){
        			if(lLoopCount==0){
            			lSql.append(" SELECT MAX(CFSERIALNUMBER) SerialNo FROM CERSAIFILES WHERE CFRECORDVERSION>0 AND CFDATE = ");
            			lSql.append(DBHelper.getInstance().formatDate(pLeg1SettlementDate));
            			ResultSet lResultSet  = null;
                		try {
                			lStatement = pConnection.createStatement();
                			logger.info(lSql.toString());
                			lResultSet = lStatement.executeQuery(lSql.toString());
                    		while(lResultSet.next()){
                    			lSerialnumber = lResultSet.getLong("SerialNo")+1;
                    		}
                		} catch (Exception e) {
                			logger.info("Error while fetching Serial No : " + e.getMessage());
                			throw new CommonBusinessException("Error while generating file. Please contact TReDS admin.");
                		}finally {
            				try{
            					if(lStatement!=null){
            						lStatement.close();
            					}
            					if(lResultSet!=null){
            						lResultSet.close();
            					}
            				}catch(Exception ex1){
                    			logger.info("Error while closing " + ex1.getMessage());
            				}
            			}
        			}else{
        				lSerialnumber = new Long(lSerialnumber+1);
        			}
        			lLoopCount++;
            		//
            		try {
            			lCfId = DBHelper.getInstance().getUniqueNumber(pConnection, "Instruments.InCersaiFileId");
            		} catch (SQLException e1) {
            			logger.info("Error while getting Unique Number for Instruments.InCersaiFileId " +  e1.getMessage());
            			throw new CommonBusinessException("Error while generating file. Please contact TReDS admin.");
            		}
            		//
            		lSql = new StringBuilder();
        			lSql.append(" UPDATE INSTRUMENTS SET InCersaiFileId =  ");
            		lSql.append(lCfId);
            		lSql.append(" WHERE INRECORDVERSION>0 AND INID IN ( ");
            		lSql.append(TredsHelper.getInstance().getCSVIdsForInQuery(lFileList));
            		lSql.append(" ) ");
            		try {
        				lStatement = pConnection.createStatement();
                        lUpdateCount = lStatement.executeUpdate(lSql.toString());
        			} catch (SQLException e) {
        				logger.info("Error while updating the instruments with CersaiFileId. " + e.getMessage());
            			throw new CommonBusinessException("Error while generating file. Please contact TReDS admin.");
        			}finally {
        				if(lStatement!=null){
        					try{
        						lStatement.close();
        					}catch(Exception ex1){
                    			logger.info("Error while closing " + ex1.getMessage());
        					}
        				}
        			}
            		if (lUpdateCount>0){
            			CersaiFileBean lCfBean = new CersaiFileBean();
                    	lCfBean.setId(lCfId);
                    	lCfBean.setDate(pLeg1SettlementDate);
                    	lCfBean.setRecordCount(new Long(lUpdateCount));
                    	lCfBean.setFinancier(lFinancierCode);
                    	lCfBean.setFileName(getFileName(pLeg1SettlementDate, lSerialnumber));
                    	lCfBean.setSerialNumber(lSerialnumber);
                    	lCfBean.setGeneratedByAuId(pAppUserBean.getId());
                    	lCfBean.setGeneratedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                    	lReturnCersaiFileNames.add(getFileName(pLeg1SettlementDate, lSerialnumber));
    					try {
    						//creating and storing the file for future reference
    	                	CersaiFileGenerator cersaiFileGenerator = new CersaiFileGenerator();
    	                    String lFileData = cersaiFileGenerator.cersaiDownload(pConnection,  lCfBean, pAppUserBean);
    	                    String lStorageFileName = FileUploadHelper.saveFile(lCfBean.getFileName(), lFileData.getBytes(), null, "CERSAIFILES");
    	                    //
    	                    lCfBean.setStorageFileName(lStorageFileName);
        					cersaiFileDAO.insert(pConnection, lCfBean, CersaiFileBean.FIELDGROUP_INSERT);
    					} catch (Exception e1) {
    						// TODO Auto-generated catch block
        					logger.info("Error while generating file data for " + ((lCfBean!=null&&lCfBean.getFileName()!=null)?lCfBean.getFileName():"") + ". " +  e1.getMessage());
                			throw new CommonBusinessException("Error while generating file. Please contact TReDS admin.");
    					}
            		}
        		}
        	}
    	}
		return lReturnCersaiFileNames;
    }
    
    private String getFileName(Date pFactoringDate, Long pSerialNumber){
    	  String lFileName = "";
          //filename
          lFileName = "cerupload_";
          lFileName += "fctr_";
          lFileName += StringUtils.leftPad(String.valueOf(pSerialNumber), 3,"0");
          lFileName += "_";
          System.out.println(FormatHelper.getDisplay(AppConstants.DATE_FORMAT, pFactoringDate));
          lFileName += FormatHelper.getDisplay(AppConstants.DATE_FORMAT, pFactoringDate);
          //
          return lFileName;
    }
    
}
