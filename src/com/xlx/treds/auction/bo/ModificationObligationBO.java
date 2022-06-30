package com.xlx.treds.auction.bo;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

public class ModificationObligationBO {
	private static final Logger logger = LoggerFactory.getLogger(ModificationObligationBO.class);
    private GenericDAO<ObligationBean> obligationDAO;
    private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;
    private BeanMeta obligationBeanMeta;

    public ModificationObligationBO() {
        super();
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
    }
    
   
   public void saveModifiedData(ExecutionContext pExecutionContext, Map<String, Object> pMap, AppUserBean pAppUserBean) throws Exception{
	   Connection lConnection = pExecutionContext.getConnection();
	   Map<Long,ObligationBean> lRecdObligationHash = new HashMap<Long,ObligationBean>();
	   Map<Long,ObligationBean> lObligationHash = null;
	   Map<Long,List<ObligationSplitsBean>> lObligationSplitHash = new HashMap<Long,List<ObligationSplitsBean>>();
	   ObligationBean lObligationBean = null ;
	   List<Map<String, Object>> lTableData =  (List<Map<String, Object>>) pMap.get("table");
	   Date lOriginalDate = null, lNewDate = null;
	   String lOriginalSettlor = null, lNewSettlor = null;
	   //get data from the front end
	   //fetch the old ata from the database
	   for (Map<String, Object> lBeanMap : lTableData){
		   lObligationBean = new ObligationBean();
		   obligationBeanMeta.validateAndParse(lObligationBean, lBeanMap, null, null);
		   //hashing the received data 
		   lRecdObligationHash.put(lObligationBean.getId(), lObligationBean);
		   //
		   lNewDate = lObligationBean.getDate();
		   lNewSettlor = lObligationBean.getPaymentSettlor();
		   if(lNewSettlor == null){
			   throw new CommonBusinessException("Please select a valid settlor.");
		   }
		   //
		   if (lObligationHash == null){
			   List<ObligationDetailBean>  lObligationDetailList = new ArrayList<ObligationDetailBean>();
			   lObligationDetailList = TredsHelper.getInstance().getObligationDetailBean(lConnection, lObligationBean.getFuId(), lObligationBean.getType(), null);
			   if(lObligationDetailList!=null && lObligationDetailList.size() > 0){
				   List<ObligationSplitsBean> lObligationSplits = null;
				   lObligationHash = new HashMap<Long,ObligationBean>();
				   ObligationSplitsBean lObligationSplitsBean = null;
				   for ( ObligationDetailBean lObDetailBean : lObligationDetailList ){
					   lObligationBean = lObDetailBean.getObligationBean();
					   if(!lObligationHash.containsKey(lObligationBean.getId())){
						   lObligationHash.put(lObligationBean.getId(),lObligationBean);
						   lObligationSplitHash.put(lObligationBean.getId(), new ArrayList<ObligationSplitsBean>());
					   }
					   lObligationBean = lObligationHash.get(lObligationBean.getId());
					   lObligationSplits = lObligationSplitHash.get(lObligationBean.getId());
					   lObligationSplitsBean = lObDetailBean.getObligationSplitsBean();
					   lObligationSplitsBean.setParentObligation(lObligationBean);
					   lObligationSplits.add(lObligationSplitsBean);
					   //
					   if(lOriginalDate==null){
						   lOriginalDate = lObligationBean.getDate();
					   }else if (!lOriginalDate.equals(lObligationBean.getDate())){
						   logger.info("Obligation date diffrent for same leg.");
					   }
					   if(lOriginalSettlor==null){
						   lOriginalSettlor = lObDetailBean.getObligationSplitsBean().getPaymentSettlor();
					   }else if (!lOriginalDate.equals(lObligationBean.getDate())){
						   logger.info("Obligation settlor diffrent for same leg.");
					   }
				   }
			   }
	   	   }
	   }
	   //check whether there is any change in the Settlor or Date
	   //loop throught the server hash and then update
	   if (lNewDate.before(lOriginalDate)){
		   throw new CommonBusinessException("Please select a date after "+lOriginalDate);
	   }
	   if(!lOriginalDate.equals(lNewDate)|| !lOriginalSettlor.equals(lNewSettlor) ){
		   List<ObligationSplitsBean> lObliSplits = null;
		   for(ObligationBean lObliBean : lObligationHash.values()){
			   lObliBean.setDate(lNewDate);
			   lObliBean.setRecordUpdator(pAppUserBean.getId());
			   obligationDAO.update(lConnection, lObliBean, ObligationBean.FIELDGROUP_UPDATEPREGENERATIONMODIDICATION);
			   if(!lOriginalDate.equals(lNewDate) || !lOriginalSettlor.equals(lNewSettlor)){
				   lObliSplits = lObligationSplitHash.get(lObliBean.getId());
				   for(ObligationSplitsBean lObligSplit : lObliSplits){
					   lObligSplit.setDate(lNewDate);
					   lObligSplit.setPaymentSettlor(lNewSettlor);
					   lObligSplit.setRecordUpdator(pAppUserBean.getId());
					   obligationSplitsDAO.update(lConnection, lObligSplit, ObligationSplitsBean.FIELDGROUP_UPDATEPREGENERATIONMODIDICATION);
				   }
			   }
		   }
	   }
	   //if there is difference then only save to database
	   //otherwise send a message nothing to change
   }
   
   
   private AppEntityBean getAppEntityBean(String pEntityCode) throws Exception {
       MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
       AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pEntityCode});
       return lAppEntityBean;
   }
}

