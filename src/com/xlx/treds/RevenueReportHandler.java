package com.xlx.treds;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.report.DefaultHandler;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants.ChargeType;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.instrument.bean.FactoringUnitBean;

import groovy.json.JsonSlurper;



public class RevenueReportHandler extends DefaultHandler {
	
	private GenericDAO<FactoringUnitBean> factoringUnitDAO;
	
    public RevenueReportHandler(){
    	super();
    	factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
    }
       
    
    @Override
    protected List getBeanList(Connection pConnection, Map<String, Object> pFilterMap, int pRecordCount, IAppUserBean pAppUserBean) throws Exception {
    	String lReportId = getId();
        GenericBean lFilterBean = new GenericBean();
        DBHelper lDbHelper = DBHelper.getInstance();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lFilterMap = new HashMap<String, Object>();
    	if (super.getDefaultFilters() != null)
    		lFilterMap.putAll(super.getDefaultFilters());
    	if (pFilterMap != null)
    		lFilterMap.putAll(pFilterMap);
        getBeanMeta().validateAndParse(lFilterBean, lFilterMap, null);
        Date lFilterFromDate = (Date)lFilterBean.getProperty("fromDate");
        Date lFilterToDate = (Date)lFilterBean.getProperty("toDate");
    	if (lFilterFromDate == null)
        	throw new CommonBusinessException("From date is mandatory");
        if (lFilterToDate == null)
        	throw new CommonBusinessException("To date is mandatory");
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT OBFUID AS fuid ");
		lSql.append("  ,OBDATE AS futxndate ");
		lSql.append("  ,OBSETTLEDDATE AS fusettledate ");
		lSql.append("  ,FUSTATDUEDATE AS fuduedate ");
		lSql.append("  ,a.CDCOMPANYNAME AS fupurchasername ");
		lSql.append("  , FUPURGSTN AS fupurchasergstn ");
		lSql.append("  ,b.CDCOMPANYNAME AS fusuppliername ");
		lSql.append("  ,FUSUPGSTN AS fusuppliergstn ");
		lSql.append("  ,c.CDCOMPANYNAME AS fufinname ");
		lSql.append("  ,fupurchaser ");
		lSql.append("  ,fusupplier ");
		lSql.append("  ,fufinancier ");
		lSql.append(" ,FUPERIOD1CHARGEBEARER ,FUPERIOD1CHARGEPERCENT, FUPERIOD2CHARGEBEARER,FUPERIOD2CHARGEPERCENT,FUPERIOD3CHARGEBEARER,FUPERIOD3CHARGEPERCENT ");
		lSql.append(" ,(CASE WHEN OBTYPE ='L2' THEN (OBDATE-FULEG1DATE) ");
		lSql.append("  WHEN OBTYPE !='L2' THEN (FUMATURITYDATE-FULEG1DATE) ELSE NULL END) AS futenor ");
		lSql.append("  ,FUAMOUNT  ");
		lSql.append("  ,OBTXNTYPE AS fuRevenueType ");
		lSql.append("  ,FUACCEPTEDRATE AS fuRate ");
		lSql.append("  ,(CASE WHEN cb.BILID IS NOT NULL THEN 'Yes' WHEN cb.BILID IS NULL THEN 'No'ELSE '' END) AS fucbgenerated ");
		lSql.append("  ,(CASE WHEN cb.BILID IS NOT NULL THEN cb.BILBILLNUMBER ELSE '' END) AS fucbInvNo ");
		lSql.append("  ,(CASE WHEN fin.BILID IS NOT NULL THEN 'Yes' WHEN fin.BILID IS NULL THEN 'No'ELSE '' END) AS fufingenerated ");
		lSql.append("  ,(CASE WHEN fin.BILID IS NOT NULL THEN fin.BILBILLNUMBER ELSE '' END) AS fufinInvNo ");
		lSql.append("  ,(CASE WHEN ext1.BILID IS NOT NULL THEN 'Yes' WHEN ext1.BILID IS NULL THEN 'No'ELSE '' END) AS fuext1generated ");
		lSql.append("  ,(CASE WHEN ext1.BILID IS NOT NULL THEN ext1.BILBILLNUMBER ELSE '' END) AS fuext1InvNo ");
		lSql.append("  ,(CASE WHEN ext2.BILID IS NOT NULL THEN 'Yes' WHEN ext2.BILID IS NULL THEN 'No'ELSE '' END) AS fuext1generated ");
		lSql.append("  ,(CASE WHEN ext2.BILID IS NOT NULL THEN ext2.BILBILLNUMBER ELSE '' END) AS fuext1InvNo ");
		lSql.append("  , fuentitygstsummary ");
		lSql.append(" ,FUPERIOD1CHARGEBEARER ,FUPERIOD1CHARGEPERCENT, FUPERIOD2CHARGEBEARER,FUPERIOD2CHARGEPERCENT,FUPERIOD3CHARGEBEARER,FUPERIOD3CHARGEPERCENT ");
		lSql.append("  FROM OBLIGATIONS  ");
		lSql.append("  LEFT OUTER JOIN FACTORINGUNITS ON OBLIGATIONS.OBFUID=FACTORINGUNITS.FUID ");
		lSql.append("  LEFT OUTER JOIN COMPANYDETAILS a ON FACTORINGUNITS.FUPURCHASER=a.CDCODE ");
		lSql.append("  LEFT OUTER JOIN COMPANYDETAILS b ON FACTORINGUNITS.FUSUPPLIER=b.CDCODE ");
		lSql.append("  LEFT OUTER JOIN COMPANYDETAILS c ON FACTORINGUNITS.FUFinancier=c.CDCODE ");
		lSql.append("  LEFT OUTER JOIN BILLS cb ON FACTORINGUNITS.fucostBearerBillId=cb.BILID ");
		lSql.append("  LEFT OUTER JOIN BILLS fin ON FACTORINGUNITS.fufinancierBillId=fin.BILID ");
		lSql.append("  LEFT OUTER JOIN BILLS ext1 ON FACTORINGUNITS.fuext1billid=ext1.BILID ");
		lSql.append("  LEFT OUTER JOIN BILLS ext2 ON FACTORINGUNITS.fuext2billid=ext1.BILID ");
		lSql.append("  WHERE OBRECORDVERSION >0 AND OBTXNENTITY='TREDS' AND OBTXNTYPE='C'  ");
		if(lFilterFromDate!=null && lFilterToDate!=null){
    		lSql.append(" AND OBDATE between ").append(lDbHelper.formatDate(lFilterFromDate));
    		lSql.append(" AND ").append(lDbHelper.formatDate(lFilterToDate));
    	}else{
    		if (lFilterFromDate!=null){
        		lSql.append(" AND OBDATE >= ").append(lDbHelper.formatDate(lFilterFromDate));
        	}
        	if (lFilterToDate!=null){
        		lSql.append(" AND OBDATE <= ").append(lDbHelper.formatDate(lFilterToDate));
        	}
    	}
		getGenericDAO().appendAsSqlFilter(lSql, lFilterBean, false);
        if (StringUtils.isNotBlank(getOrderBy()))
            lSql.append(" ORDER BY ").append(getOrderBy());
        List<GenericBean> lList = getGenericDAO().findListFromSql(pConnection, lSql.toString(), pRecordCount);
        List<GenericBean> lRtnList = new ArrayList<>();
        GenericBean lFinBean = null;
        GenericBean lChargeBearerBean = null;
        for (GenericBean lGenericBean : lList) {
        	Map lMap = getGenericDAO().getBeanMeta().formatAsMap(lGenericBean);
        	FactoringUnitBean lFuBean = new FactoringUnitBean();
        	factoringUnitDAO.getBeanMeta().validateAndParse(lFuBean, lMap, null);
        	if (lGenericBean.getProperty("entityGstSummary")!=null) {
        		List <Map<String,Object>> lGstList =  (List<Map<String, Object>>) lJsonSlurper.parseText((String) lGenericBean.getProperty("entityGstSummary"));
        		if( lGstList!=null && !lGstList.isEmpty()) {
        			boolean lPurchaserAdded = false;
        			boolean lSupplierAdded = false;
        			for (Map<String,Object> lGstMap:lGstList) {
        				if (lGstMap.get("entity").toString().equals(lFuBean.getChargeBearerEntityCode())){
        					lChargeBearerBean = new GenericBean();
        					getGenericDAO().getBeanMeta().copyBean(lGenericBean, lChargeBearerBean);
        					lGenericBean.setProperty("costBearingEntity", lFuBean.getChargeBearerEntityCode());
        					if (lGstMap.get("entity").toString().equals(lFuBean.getSupplier())) {
        						lChargeBearerBean.setProperty("chargeEntityType", "Seller");
        					}else if (lGstMap.get("entity").toString().equals(lFuBean.getPurchaser())) {
        						lChargeBearerBean.setProperty("chargeEntityType", "Buyer");
        					}
        					lChargeBearerBean.setProperty("igst",lGstMap.get("igstValue"));
        					lChargeBearerBean.setProperty("cgst",lGstMap.get("cgstValue"));
        					lChargeBearerBean.setProperty("sgst",lGstMap.get("sgstValue"));
        					lChargeBearerBean.setProperty("charges",lGstMap.get("charge"));
        					lChargeBearerBean.setProperty("generated", lChargeBearerBean.getProperty("cbGenerated"));
        					lChargeBearerBean.setProperty("invNo", lChargeBearerBean.getProperty("cbInvNo"));
        					if (lGstMap.get("chargeType").equals(ChargeType.Extension.toString())) {
        						lChargeBearerBean.setProperty("invNo", lChargeBearerBean.getProperty("ext1InvNo"));
        					}
        					lRtnList.add(lChargeBearerBean);
        				}
        				if (CostBearingType.Percentage_Split.equals(lFuBean.getChargeBearer()) 
        						|| CostBearingType.Periodical_Split.equals(lFuBean.getChargeBearer()) )  {
        					if (!lPurchaserAdded) {
        						lChargeBearerBean = new GenericBean();
            					getGenericDAO().getBeanMeta().copyBean(lGenericBean, lChargeBearerBean);
            					lGenericBean.setProperty("costBearingEntity", lFuBean.getPurchaser());
            					lChargeBearerBean.setProperty("chargeEntityType", "Buyer");
            					lChargeBearerBean.setProperty("igst",lGstMap.get("igstValue"));
            					lChargeBearerBean.setProperty("cgst",lGstMap.get("cgstValue"));
            					lChargeBearerBean.setProperty("sgst",lGstMap.get("sgstValue"));
            					lChargeBearerBean.setProperty("charges",lGstMap.get("charge"));
            					lChargeBearerBean.setProperty("generated", lChargeBearerBean.getProperty("cbGenerated"));
            					lChargeBearerBean.setProperty("invNo", lChargeBearerBean.getProperty("cbInvNo"));
            					lRtnList.add(lChargeBearerBean);
            					lPurchaserAdded = true;
        					}
        					if (!lSupplierAdded) {
        						lChargeBearerBean = new GenericBean();
            					getGenericDAO().getBeanMeta().copyBean(lGenericBean, lChargeBearerBean);
            					lGenericBean.setProperty("costBearingEntity", lFuBean.getSupplier());
            					lChargeBearerBean.setProperty("chargeEntityType", "Seller");
            					lChargeBearerBean.setProperty("igst",lGstMap.get("igstValue"));
            					lChargeBearerBean.setProperty("cgst",lGstMap.get("cgstValue"));
            					lChargeBearerBean.setProperty("sgst",lGstMap.get("sgstValue"));
            					lChargeBearerBean.setProperty("charges",lGstMap.get("charge"));
            					lChargeBearerBean.setProperty("generated", lChargeBearerBean.getProperty("cbGenerated"));
            					lChargeBearerBean.setProperty("invNo", lChargeBearerBean.getProperty("cbInvNo"));
            					lRtnList.add(lChargeBearerBean);
            					lSupplierAdded = true;
        					}
        				}
        				if (lGstMap.get("entity").toString().equals(lGenericBean.getProperty("financier"))){
        					lFinBean = new GenericBean();
        					getGenericDAO().getBeanMeta().copyBean(lGenericBean, lFinBean);
        					lFinBean.setProperty("igst",lGstMap.get("igstValue"));
        					lFinBean.setProperty("cgst",lGstMap.get("cgstValue"));
        					lFinBean.setProperty("sgst",lGstMap.get("sgstValue"));
        					lFinBean.setProperty("charges",lGstMap.get("charge"));
        					lFinBean.setProperty("chargeEntityType", "Financier");
        					lFinBean.setProperty("generated", lFinBean.getProperty("finGenerated"));
        					lFinBean.setProperty("invNo", lFinBean.getProperty("finInvNo"));
        					lRtnList.add(lFinBean);
        				}
        			}
        		}
        	}
        }
        return lRtnList;
    }
    

  
    

}
