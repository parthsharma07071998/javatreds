package com.xlx.treds.stats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.ApprovalStatus;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.notialrt.bean.NotiAlrtBean;
import com.xlx.treds.stats.bean.StatsCacheBean;
import com.xlx.treds.stats.bean.StatsCacheBean.Type;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class PurchaserSupplierLinkCache  implements IStatsCacheGenerator {
	
	 private static Logger logger = Logger.getLogger(PurchaserSupplierLinkCache.class);
	
	GenericDAO<StatsCacheBean> statsCacheDAO = null;
	GenericDAO<NotiAlrtBean> notiAlrtDAO = null;
	private GenericDAO genericDAO;
	
	public static final String EXPIRY = "expiry";
    public static final String BEANMETAFILE = "beanMetaFile";
    public static final String BEANCLASS = "beanClass";
    public static final String PURCHASER = "purchaser";
    public static final String AVERAGETIME = "averageTime";
    
	private String statType = null;
    private int expiry = 0;
    Map<String, Object> configMap = null;
    
	public PurchaserSupplierLinkCache(String pStatType, Object pConfigMap) throws Exception{
		super();
		statType = pStatType;
		configMap = (Map<String, Object>) pConfigMap;
		setConfigs();
		getGenericDAO();
		statsCacheDAO = new GenericDAO<StatsCacheBean>(StatsCacheBean.class);
		notiAlrtDAO = new GenericDAO<NotiAlrtBean>(NotiAlrtBean.class);
	}
	
	public void setConfigs() {
		if (!configMap.isEmpty()) {
			if (configMap.containsKey(EXPIRY)) {
				expiry = ((Integer)configMap.get(EXPIRY)).intValue();
			}
		}
	}
	
	private void getGenericDAO() throws Exception {
		Class lClass = Class.forName((String)configMap.get(BEANCLASS));
		BeanMeta lBeanMeta =BeanMetaFactory.getInstance().getBeanMeta(null,(String)configMap.get(BEANMETAFILE));
    	genericDAO = new GenericDAO<>(lClass,  null,lBeanMeta );         
	}
	
	@Override
	public void generate(String pKey) {
		StatsCacheBean lStatsCacheBean = new StatsCacheBean();
		lStatsCacheBean.setKey(pKey);
		lStatsCacheBean.setType(Type.Purchaser_Supplier_Link);
		DBHelper lDBHelper = DBHelper.getInstance();
		Map<String,Object> lDefinedSettings = new HashMap<>();
		try (Connection lConnection = lDBHelper.getConnection()){
			StatsCacheBean lOldStatsCacheBean = statsCacheDAO.findBean(lConnection, lStatsCacheBean);
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT  PLWPURCHASER ");
			lSql.append(" ,round(AVG(((TRUNC(FACTCREATETIME) - TRUNC(APPROVALTIME)) *24*60) + ((TO_CHAR( FACTCREATETIME, 'HH24' )-TO_CHAR( APPROVALTIME, 'HH24' ))*60) + (TO_CHAR( FACTCREATETIME, 'MI' )-TO_CHAR( APPROVALTIME, 'MI' ))),2) AS PLWAVERAGETIME ");
			lSql.append(" from  ( select DFT.PLWSUPPLIER, DFT.PLWPURCHASER ");
			lSql.append(" , DFT.PLWSTATUS DRAFTSTATUS, MIN(DFT.PLWSTATUSUPDATETIME) DRAFTTIME ");
			lSql.append(" , APP.PLWSTATUS APPROVALSTATUS, MIN(APP.PLWSTATUSUPDATETIME) APPROVALTIME ");
			lSql.append(" , MIN(FURECORDCREATETIME) FACTCREATETIME  ");
			lSql.append(" FROM PURCHASERSUPPLIERLINKWORKFLOW DFT ");
			lSql.append(" , PURCHASERSUPPLIERLINKWORKFLOW APP  ");
			lSql.append(" , FACTORINGUNITS FACT ");
			lSql.append(" WHERE  DFT.PLWSTATUS ='DFT' ");
			lSql.append(" AND APP.PLWSTATUS = 'APP' ");
			lSql.append(" AND DFT.PLWSUPPLIER = APP.PLWSUPPLIER  ");
			lSql.append(" AND DFT.PLWPURCHASER = APP.PLWPURCHASER ");
			lSql.append(" AND FACT.FUSUPPLIER = DFT.PLWSUPPLIER ");
			lSql.append(" AND FACT.FUPURCHASER = DFT.PLWPURCHASER ");
			lSql.append(" AND DFT.PLWPURCHASER = ").append(lDBHelper.formatString(pKey));
			lSql.append(" GROUP BY DFT.PLWSUPPLIER, DFT.PLWPURCHASER, DFT.PLWSTATUS,APP.PLWSTATUS ) QUERY WHERE FACTCREATETIME>APPROVALTIME ");
			lSql.append(" GROUP BY PLWPURCHASER ");
			List<GenericBean> lList = genericDAO.findListFromSql(lConnection, lSql.toString(), -1);
			if (lList!=null) {
				for (GenericBean lGenericBean : lList) {
					lDefinedSettings.put(AVERAGETIME,lGenericBean.getProperty(AVERAGETIME));
				}
			}
			lStatsCacheBean.setValue(new JsonBuilder(lDefinedSettings).toString());
			lStatsCacheBean.setExpiry(new Timestamp(System.currentTimeMillis()+expiry));
			if (lOldStatsCacheBean==null) {
				statsCacheDAO.insert(lConnection, lStatsCacheBean);
			}else {
				lStatsCacheBean.setId(lOldStatsCacheBean.getId());
				statsCacheDAO.update(lConnection, lStatsCacheBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Object getValue(Type pType, String pKey) {
		DBHelper lDBHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		try(Connection lConnection = DBHelper.getInstance().getConnection();){
			lSql.append(" SELECT * FROM STATSCACHE WHERE");
			lSql.append(" SCTYPE = ").append(lDBHelper.formatString(pType.getCode()));
			lSql.append(" AND SCKEY = ").append(lDBHelper.formatString(pKey));
			StatsCacheBean lStatsCacheBean = statsCacheDAO.findBean(lConnection, lSql.toString());
			if (lStatsCacheBean==null || new Timestamp(System.currentTimeMillis()).compareTo(lStatsCacheBean.getExpiry()) > 1) {
				generate(pKey);
				return getValue(pType, pKey);
			}
			return lStatsCacheBean.getValue();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	public void generateAlert(Object pObject) {
		try{
			FactoringUnitBean lFactoringUnitBean =  (FactoringUnitBean) pObject;
			String lValue = (String) getValue(Type.Purchaser_Supplier_Link, lFactoringUnitBean.getPurchaser());
			validateTimeTaken(lFactoringUnitBean.getPurchaser(),lFactoringUnitBean.getSupplier(),lValue);
		}catch (Exception lEx) {
			logger.debug("Error in generateAlert PurchaserSupplierLinkCache");
			logger.debug(lEx.getStackTrace());
		}
	}
	
	public void validateTimeTaken(String pKey,String pSupplier, String pData) {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		DBHelper lDBHelper = DBHelper.getInstance();
		Map<String,Object> lMap = null;
		if(pData!= null){
			lMap = (Map<String, Object>) lJsonSlurper.parseText(pData);
			if (lMap.containsKey(AVERAGETIME)) {
			 try (Connection lConnection = DBHelper.getInstance().getConnection();){
				 StringBuilder lSql = new StringBuilder();
				 lSql.append(" SELECT  Count(*) FuId FROM FACTORINGUNITS WHERE  ");
			     lSql.append(" FUPURCHASER = ").append(lDBHelper.formatString(pKey));
			     lSql.append(" FUSUPPLIER = ").append(lDBHelper.formatString(pSupplier));
			     List<GenericBean> lList = genericDAO.findListFromSql(lConnection, lSql.toString(), -1);
			     if (!lList.isEmpty()) {
			    	 return;
			     }
				 lSql = new StringBuilder();
				 lSql.append(" SELECT ((TRUNC(sysdate) - TRUNC(min(PLWSTATUSUPDATETIME))) *24*60) + ((TO_CHAR( min(PLWSTATUSUPDATETIME), 'HH24' )-TO_CHAR(min(PLWSTATUSUPDATETIME), 'HH24' ))*60) + (TO_CHAR( PLWSTATUSUPDATETIME, 'MI' )-TO_CHAR( PLWSTATUSUPDATETIME, 'MI' )) AS INMINS FROM PURCHASERSUPPLIERLINKWORKFLOW WHERE");
				 lSql.append(" PLWSUPPLIER = ").append(DBHelper.getInstance().formatString(pSupplier));
				 lSql.append(" AND PLWPURCHASER = ").append(DBHelper.getInstance().formatString(pKey));	
				 lSql.append(" AND PLWSTATUS = " ).append(DBHelper.getInstance().formatString(ApprovalStatus.Approved.getCode()));
				 Statement lStatement = lConnection.createStatement();
				 ResultSet lResultSet = lStatement.executeQuery(lSql.toString());
				 long lInMins = 0;
				 while (lResultSet.next()){
					lInMins = lResultSet.getLong("INMINS");
				 }
				 if (lInMins>0 &&  lMap.containsKey(AVERAGETIME)) {
					if (lInMins < (new Long(lMap.get(AVERAGETIME).toString()).longValue())){
						 NotiAlrtBean lAlrtBean = new NotiAlrtBean();
						 lAlrtBean.setType(NotiAlrtBean.Type.Purchaser_Supplier_Link);
						 lAlrtBean.setKey(pKey);
						 lAlrtBean.setAlertDesc(lInMins + " exceds the average time taken i.e " +lMap.get(AVERAGETIME).toString());
						 lAlrtBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
						 notiAlrtDAO.insert(lConnection, lAlrtBean);
					}
				 }
			 }catch (Exception e) {
			 }
			}
		}
	}
   

}
