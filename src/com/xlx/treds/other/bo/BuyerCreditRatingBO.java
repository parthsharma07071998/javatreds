
package com.xlx.treds.other.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.other.bean.BuyerCreditRatingBean;
import com.xlx.treds.other.bean.BuyerCreditRatingBean.Status;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;

public class BuyerCreditRatingBO {

	public static final Logger logger = LoggerFactory.getLogger(BuyerCreditRatingBO.class);
    
    private GenericDAO<BuyerCreditRatingBean> buyerCreditRatingDAO;
    public static final Pattern PATTERN_PAN = Pattern.compile("[A-Z]{5}\\d{4}[A-Z]{1}");

    public BuyerCreditRatingBO() {
        super();
        buyerCreditRatingDAO = new GenericDAO<BuyerCreditRatingBean>(BuyerCreditRatingBean.class);
    }
    
    public BuyerCreditRatingBean findBean(ExecutionContext pExecutionContext, 
        BuyerCreditRatingBean pFilterBean) throws Exception {
        BuyerCreditRatingBean lBuyerCreditRatingBean = buyerCreditRatingDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lBuyerCreditRatingBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lBuyerCreditRatingBean;
    }
    
    public List<BuyerCreditRatingBean> findList(ExecutionContext pExecutionContext, BuyerCreditRatingBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	HashMap<String, Object> lBuyerCreditSetting = new HashMap<String,Object>();
    	lBuyerCreditSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_BUYERCREDITRATING);
		Long lExpiryDays =  (Long) lBuyerCreditSetting.get(AppConstants.ATTRIBUTE_BUYERCREDITRATING_EXPIRYDAYS);
    	List<String> lTmp = new ArrayList<String>();
    	String lFieldsStr = buyerCreditRatingDAO.getDBColumnNameCsv(null,Arrays.asList("recordUpdator","filterRatingAgency","filterRating"));
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT ").append(lFieldsStr).append(" , nvl(FINCOUNTONPURLIMIT_VW.FINCOUNT, 0) BCRRECORDUPDATOR FROM BUYERCREDITRATINGS JOIN APPENTITIES ON (AECODE = BCRBUYERCODE) LEFT OUTER JOIN FINCOUNTONPURLIMIT_VW ");
        lSql.append(" ON ( BCRBUYERCODE = FASPURCHASER ) ");
        lSql.append(" WHERE 1=1 ");
        if(StringUtils.isNotEmpty(pFilterBean.getPan())){
        	lSql.append(" AND AEPAN = ").append(DBHelper.getInstance().formatString(pFilterBean.getPan()));
        }
		if(pFilterBean.getStatus()!=null){
			if(pFilterBean.getStatus().equals(Status.Future)){
				lSql.append(" AND BCRRATINGDATE > ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate())));
			}else if(pFilterBean.getStatus().equals(Status.Expired)){
				lSql.append(" AND BCRRATINGDATE + ").append(lExpiryDays).append(" < ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate())));
			}else{
				lSql.append(" AND BCRRATINGDATE <= ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate())));
				lSql.append(" AND BCRRATINGDATE + ").append(lExpiryDays).append(" > ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate())));
			}
		}
		if(pFilterBean.getFilterRatingAgencyList()!= null){
			lSql.append(" AND BCRRATINGAGENCY IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(pFilterBean.getFilterRatingAgencyList())).append(" ) ");		
		}
		if(pFilterBean.getFilterRatingList()!=null){
			lSql.append(" AND BCRRATING IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(pFilterBean.getFilterRatingList())).append(" ) ");
		}
		pFilterBean.setFilterRatingAgencyList(null);
		pFilterBean.setFilterRatingList(null);
		buyerCreditRatingDAO.appendAsSqlFilter(lSql, pFilterBean, false);
		List<BuyerCreditRatingBean> lList =  buyerCreditRatingDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        // filter out blocked buyers for financier list
        List<BuyerCreditRatingBean> lFilteredList = new ArrayList<BuyerCreditRatingBean>();
        for (BuyerCreditRatingBean lBuyerCreditRatingBean : lList) {
        	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lBuyerCreditRatingBean.getBuyerCode());
        	lBuyerCreditRatingBean.setFinancierCount(lBuyerCreditRatingBean.getRecordUpdator());
        	lBuyerCreditRatingBean.setPan(lAppEntityBean.getPan());
            lFilteredList.add(lBuyerCreditRatingBean);
        }
        return lFilteredList;
    }
    
    public void save(ExecutionContext pExecutionContext, BuyerCreditRatingBean pBuyerCreditRatingBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        BuyerCreditRatingBean lOldBuyerCreditRatingBean = null;
        if (pNew) {
        	pBuyerCreditRatingBean.setRecordCreator(pUserBean.getId());
            buyerCreditRatingDAO.insert(lConnection, pBuyerCreditRatingBean,BeanMeta.FIELDGROUP_INSERT);
        } else {
            lOldBuyerCreditRatingBean = findBean(pExecutionContext, pBuyerCreditRatingBean);
            if (buyerCreditRatingDAO.update(lConnection, pBuyerCreditRatingBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, BuyerCreditRatingBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        BuyerCreditRatingBean lBuyerCreditRatingBean = findBean(pExecutionContext, pFilterBean);
        buyerCreditRatingDAO.delete(lConnection, lBuyerCreditRatingBean);        


        pExecutionContext.commitAndDispose();
    }

	public List<BuyerCreditRatingBean> findValidRatings(Connection pConnection,
			Map<String, Object> pMap, IAppUserBean lUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM BuyerCreditRatings ");
		lSql.append(" WHERE  BCRBuyerCode = ").append(DBHelper.getInstance().formatString(pMap.get("buyerCode").toString()));
		lSql.append(" AND BCRRecordVersion > 0 ");
		lSql.append(" AND BCRRATINGDATE < ").append(DBHelper.getInstance().formatDate(CommonUtilities.getCurrentDate()));
		lSql.append(" AND BCRRATINGDATE + 365 > ").append(DBHelper.getInstance().formatDate(CommonUtilities.getCurrentDate()));
		List<BuyerCreditRatingBean> lBeanList = buyerCreditRatingDAO.findListFromSql(pConnection, lSql.toString(), -1);
		return lBeanList;
	}
	
	public List<BuyerCreditRatingBean> findNearByRatings(Connection pConnection,
			Map<String, Object> pMap, IAppUserBean lUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM BuyerCreditRatings ");
		lSql.append(" WHERE  BCRRecordVersion > 0 ");
		lSql.append(" AND BCRRATINGDATE < ").append(DBHelper.getInstance().formatDate(CommonUtilities.getCurrentDate()));
		lSql.append(" AND BCRRATINGDATE + 365 > ").append(DBHelper.getInstance().formatDate(CommonUtilities.getCurrentDate()));
		lSql.append(" AND  ( ( BCRRATINGDATE + 365 ) - sysdate ) <= 15  ");
		List<BuyerCreditRatingBean> lBeanList = buyerCreditRatingDAO.findListFromSql(pConnection, lSql.toString(), -1);
		return lBeanList;
	}
	
}
