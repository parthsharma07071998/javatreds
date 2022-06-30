package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.BillingLocationBean;

public class BillingLocationBO {
    
    private GenericDAO<BillingLocationBean> billingLocationDAO;

    public BillingLocationBO() {
        super();
        billingLocationDAO = new GenericDAO<BillingLocationBean>(BillingLocationBean.class);
    }
    
    public BillingLocationBean findBean(Connection pConnection, 
        BillingLocationBean pFilterBean) throws Exception {
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM BILLINGLOCATIONS_TBL_VW ");
    	lSql.append(" WHERE BLCODE = ").append(DBHelper.getInstance().formatString(pFilterBean.getCode()));
    	lSql.append(" AND BLID = ").append(pFilterBean.getId());
        return billingLocationDAO.findBean(pConnection, lSql.toString());
    }
    
    public List<BillingLocationBean> findList(ExecutionContext pExecutionContext, BillingLocationBean pFilterBean, 
    	List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	List<BillingLocationBean> lList = null;
    	BillingLocationBean lBean = null;
    	StringBuilder lSql = new StringBuilder();
    	DBHelper lDbHelper = DBHelper.getInstance();
    	lSql.append(" SELECT * FROM BILLINGLOCATIONS_TBL_VW ");
    	if (pFilterBean.getCode()!=null) {
    		lSql.append(" where BLCODE= ").append(lDbHelper.formatString(pFilterBean.getCode()));
    	}
    	try(Statement lStatement =  pExecutionContext.getConnection().createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString()); ){
    		if (lResultSet!=null) {
    			lList = new ArrayList<BillingLocationBean>();
    		}
			while (lResultSet.next()){
				lBean = new BillingLocationBean();
				lBean.setCode(lResultSet.getString("BLCODE"));
				lBean.setName(lResultSet.getString("BLNAME"));
				lBean.setGstn(lResultSet.getString("BLGSTN"));
				lBean.setId(lResultSet.getLong("BLID"));
				lBean.setBillLocName(lResultSet.getString("BLBILLLOCNAME"));
				lBean.setBillLocGstn(lResultSet.getString("BLBILLLOCGSTN"));
				lBean.setBillLocId(lResultSet.getLong("BLBILLLOCID"));
				lList.add(lBean);
			}
		} catch (Exception e) {
		}
    	return lList;
    }
    
    public void save(ExecutionContext pExecutionContext, BillingLocationBean pBillingLocationBean, IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        BillingLocationBean lBillingLocationBean = billingLocationDAO.findByPrimaryKey(lConnection, pBillingLocationBean);
        if (lBillingLocationBean==null) {
        	pBillingLocationBean.setRecordCreator(pUserBean.getId());
        	billingLocationDAO.insert(lConnection, pBillingLocationBean);
        }else {
        	pBillingLocationBean.setRecordUpdator(pUserBean.getId());
        	pBillingLocationBean.setRecordVersion(lBillingLocationBean.getRecordVersion());
        	if (billingLocationDAO.update(lConnection, pBillingLocationBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, BillingLocationBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        BillingLocationBean lBillingLocationBean = findBean(lConnection, pFilterBean);
        lBillingLocationBean.setRecordUpdator(pUserBean.getId());
        billingLocationDAO.delete(lConnection, lBillingLocationBean);        


        pExecutionContext.commitAndDispose();
    }
    
}
