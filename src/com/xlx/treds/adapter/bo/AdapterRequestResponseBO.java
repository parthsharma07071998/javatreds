package com.xlx.treds.adapter.bo;

import java.sql.Connection;
import java.util.List;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;

public class AdapterRequestResponseBO {
    
    private GenericDAO<AdapterRequestResponseBean> adapterRequestResponseDAO;

    public AdapterRequestResponseBO() {
        super();
        adapterRequestResponseDAO = new GenericDAO<AdapterRequestResponseBean>(AdapterRequestResponseBean.class);
    }
    
    public AdapterRequestResponseBean findBean(ExecutionContext pExecutionContext, 
        AdapterRequestResponseBean pFilterBean) throws Exception {
        AdapterRequestResponseBean lAdapterRequestResponseBean = adapterRequestResponseDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lAdapterRequestResponseBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lAdapterRequestResponseBean;
    }
    
    public List<AdapterRequestResponseBean> findList(ExecutionContext pExecutionContext, AdapterRequestResponseBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return adapterRequestResponseDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, AdapterRequestResponseBean pAdapterRequestResponseBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        AdapterRequestResponseBean lOldAdapterRequestResponseBean = null;
        if (pNew) {

            adapterRequestResponseDAO.insert(lConnection, pAdapterRequestResponseBean);
        } else {
            lOldAdapterRequestResponseBean = findBean(pExecutionContext, pAdapterRequestResponseBean);
            

            if (adapterRequestResponseDAO.update(lConnection, pAdapterRequestResponseBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, AdapterRequestResponseBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        AdapterRequestResponseBean lAdapterRequestResponseBean = findBean(pExecutionContext, pFilterBean);
        adapterRequestResponseDAO.delete(lConnection, lAdapterRequestResponseBean);        


        pExecutionContext.commitAndDispose();
    }
    
    
    public List<AdapterRequestResponseBean> findListFromSql(ExecutionContext pExecutionContext, AdapterRequestResponseBean pFilterBean, 
            List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    		StringBuilder lSql = new StringBuilder();
    		lSql.append(" SELECT * FROM ADAPTERREQUESTRESPONSES ");
    		lSql.append(" WHERE ARRRECORDVERSION > 0");
    		lSql.append(" AND ARRTYPE =").append(DBHelper.getInstance().formatString(AdapterRequestResponseBean.Type.Out.getCode()));
    		lSql.append(" AND ARRAPIRESPONSESTATUS = ").append(DBHelper.getInstance().formatString(AdapterRequestResponseBean.ApiResponseStatus.Failed.getCode()));
    		lSql.append(" AND ARRENTITYCODE = 'IN0000399' ");
            return adapterRequestResponseDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
    }
    
    public void resendResponse(Connection pConnection, AdapterRequestResponseBean pAdapterRequestResponseBean) throws Exception {
		AdapterRequestResponseBean lAdapterRequestResponseBean = adapterRequestResponseDAO.findBean(pConnection, pAdapterRequestResponseBean);
		IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lAdapterRequestResponseBean.getEntityCode());
		if(lAdapterRequestResponseBean != null  &&  lClientAdapter!= null){
			ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(lAdapterRequestResponseBean.getProcessId(),pConnection);
			lClientAdapter.reSendResponseToClient(lProcessInformationBean, lAdapterRequestResponseBean);
		}
		
	}
}
