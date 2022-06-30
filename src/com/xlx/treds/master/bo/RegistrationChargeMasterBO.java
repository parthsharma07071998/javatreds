
package com.xlx.treds.master.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.treds.master.bean.RegistrationChargeMasterBean;

import oracle.jdbc.proxy.annotation.Pre;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;

public class RegistrationChargeMasterBO {

	public static final Logger logger = LoggerFactory.getLogger(RegistrationChargeMasterBO.class);
    
    private GenericDAO<RegistrationChargeMasterBean> registrationChargeMasterDAO;

    public RegistrationChargeMasterBO() {
        super();
        registrationChargeMasterDAO = new GenericDAO<RegistrationChargeMasterBean>(RegistrationChargeMasterBean.class);
    }
    
    public RegistrationChargeMasterBean findBean(ExecutionContext pExecutionContext, 
        RegistrationChargeMasterBean pFilterBean) throws Exception {
        RegistrationChargeMasterBean lRegistrationChargeMasterBean = registrationChargeMasterDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lRegistrationChargeMasterBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lRegistrationChargeMasterBean;
    }
    
    public List<RegistrationChargeMasterBean> findList(ExecutionContext pExecutionContext, RegistrationChargeMasterBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return registrationChargeMasterDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, RegistrationChargeMasterBean pRegistrationChargeMasterBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        RegistrationChargeMasterBean lOldRegistrationChargeMasterBean = null;
        if (pNew) {
        	lOldRegistrationChargeMasterBean = registrationChargeMasterDAO.findByPrimaryKey(pExecutionContext.getConnection(), pRegistrationChargeMasterBean);
        	if(lOldRegistrationChargeMasterBean!=null) {
        		throw new CommonBusinessException("Registration charges for "+pRegistrationChargeMasterBean.getEntityType().toString() + " already exits.");
        	}
            pRegistrationChargeMasterBean.setRecordCreator(pUserBean.getId());
            registrationChargeMasterDAO.insert(lConnection, pRegistrationChargeMasterBean);
            registrationChargeMasterDAO.insertAudit(lConnection, pRegistrationChargeMasterBean, AuditAction.Insert, pUserBean.getId());
        } else {
        	lOldRegistrationChargeMasterBean = findBean(pExecutionContext, pRegistrationChargeMasterBean);

            registrationChargeMasterDAO.getBeanMeta().copyBean(pRegistrationChargeMasterBean, lOldRegistrationChargeMasterBean, BeanMeta.FIELDGROUP_UPDATE, null);
            lOldRegistrationChargeMasterBean.setRecordUpdator(pUserBean.getId());
            
            if (registrationChargeMasterDAO.update(lConnection, lOldRegistrationChargeMasterBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            registrationChargeMasterDAO.insertAudit(lConnection, lOldRegistrationChargeMasterBean, AuditAction.Update, pUserBean.getId());
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, RegistrationChargeMasterBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        RegistrationChargeMasterBean lRegistrationChargeMasterBean = findBean(pExecutionContext, pFilterBean);
        lRegistrationChargeMasterBean.setRecordUpdator(pUserBean.getId());
        registrationChargeMasterDAO.delete(lConnection, lRegistrationChargeMasterBean);        
        registrationChargeMasterDAO.insertAudit(lConnection, lRegistrationChargeMasterBean, AuditAction.Delete, pUserBean.getId());
        pExecutionContext.commitAndDispose();
    }
    
}
