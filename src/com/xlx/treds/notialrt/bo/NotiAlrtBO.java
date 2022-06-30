package com.xlx.treds.notialrt.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xlx.treds.notialrt.bean.NotiAlrtBean;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;

public class NotiAlrtBO {
    
    private GenericDAO<NotiAlrtBean> notiAlrtDAO;

    public NotiAlrtBO() {
        super();
        notiAlrtDAO = new GenericDAO<NotiAlrtBean>(NotiAlrtBean.class);
    }
    
    public NotiAlrtBean findBean(ExecutionContext pExecutionContext, 
        NotiAlrtBean pFilterBean) throws Exception {
        NotiAlrtBean lNotiAlrtBean = notiAlrtDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lNotiAlrtBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lNotiAlrtBean;
    }
    
    public List<NotiAlrtBean> findList(ExecutionContext pExecutionContext, NotiAlrtBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return notiAlrtDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, NotiAlrtBean pNotiAlrtBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        NotiAlrtBean lOldNotiAlrtBean = null;
        if (pNew) {
            notiAlrtDAO.insert(lConnection, pNotiAlrtBean);
        } else {
            lOldNotiAlrtBean = findBean(pExecutionContext, pNotiAlrtBean);
            if (notiAlrtDAO.update(lConnection, pNotiAlrtBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, NotiAlrtBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        NotiAlrtBean lNotiAlrtBean = findBean(pExecutionContext, pFilterBean);
        notiAlrtDAO.delete(lConnection, lNotiAlrtBean);        


        pExecutionContext.commitAndDispose();
    }
    
}
