package com.xlx.treds.auction.bo;

import java.sql.Connection;
import java.util.List;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationSplitsBean;

public class ObligationSplitsBO {
    
    private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;

    public ObligationSplitsBO() {
        super();
        obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
    }
    
    public ObligationSplitsBean findBean(ExecutionContext pExecutionContext, 
        ObligationSplitsBean pFilterBean) throws Exception {
        ObligationSplitsBean lObligationSplitsBean = obligationSplitsDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lObligationSplitsBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lObligationSplitsBean;
    }
    
    public List<ObligationSplitsBean> findList(ExecutionContext pExecutionContext, ObligationSplitsBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return obligationSplitsDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    //NOT IN USE
    public void save(ExecutionContext pExecutionContext, ObligationSplitsBean pObligationSplitsBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        ObligationSplitsBean lOldObligationSplitsBean = null;
        if (pNew) {

            obligationSplitsDAO.insert(lConnection, pObligationSplitsBean);
        } else {
            lOldObligationSplitsBean = findBean(pExecutionContext, pObligationSplitsBean);
            

            pObligationSplitsBean.setRecordUpdator(pUserBean.getId());
            if (obligationSplitsDAO.update(lConnection, pObligationSplitsBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    //NOT IN USE
    public void delete(ExecutionContext pExecutionContext, ObligationSplitsBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        ObligationSplitsBean lObligationSplitsBean = findBean(pExecutionContext, pFilterBean);
        lObligationSplitsBean.setRecordUpdator(pUserBean.getId());
        obligationSplitsDAO.delete(lConnection, lObligationSplitsBean);        


        pExecutionContext.commitAndDispose();
    }

	public List<ObligationSplitsBean> findListForModification(ExecutionContext pExecutionContext, ObligationSplitsBean pFilterBean, List<String> pFields, IAppUserBean pUserBean) throws Exception {
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT OBLIGATIONSPLITS.* ");
		lSql.append(" ,OBTXNENTITY \"OBSTransactionEntity\" ");
		lSql.append(" ,OBTYPE \"OBSLegType\" ");
		lSql.append(" ,OBFuId \"OBSFactUntId\" ");
		lSql.append(" ,FUFINANCIER \"OBSFinancierEntity\" ");
		lSql.append(" FROM OBLIGATIONSPLITS ");
		lSql.append(" JOIN OBLIGATIONS ON (OBSOBID=OBID) ");
		lSql.append(" JOIN FACTORINGUNITS ON (OBFUID=FUID) ");
		lSql.append(" WHERE OBSRECORDVERSION > 0 ");
		lSql.append(" AND OBSRECORDVERSION > 0 ");
		lSql.append(" AND OBTXNENTITY = ").append(lDbHelper.formatString(pFilterBean.getTransactionEntity()));
		lSql.append(" AND OBTYPE= ").append(lDbHelper.formatString(pFilterBean.getLegType().getCode()));
		lSql.append(" AND OBSSTATUS = ").append(lDbHelper.formatString(pFilterBean.getStatus().getCode()));
		lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(pFilterBean.getStatus().getCode()));
		lSql.append(" AND OBSOSRID IS NULL ");
		lSql.append(" AND OBSTATUS NOT IN ( ");
		lSql.append(lDbHelper.formatString(Status.Extended.getCode()));
		lSql.append(" , ").append(lDbHelper.formatString(Status.Shifted.getCode()));
		lSql.append(" ) ");
		return obligationSplitsDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(),-1);
	}
    
}

