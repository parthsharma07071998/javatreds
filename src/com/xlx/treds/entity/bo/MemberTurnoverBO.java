package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.util.List;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.MemberTurnoverBean;

public class MemberTurnoverBO {
    
    private GenericDAO<MemberTurnoverBean> memberTurnoverDAO;

    public MemberTurnoverBO() {
        super();
        memberTurnoverDAO = new GenericDAO<MemberTurnoverBean>(MemberTurnoverBean.class);
    }
    
    public MemberTurnoverBean findBean(ExecutionContext pExecutionContext, 
        MemberTurnoverBean pFilterBean) throws Exception {
        MemberTurnoverBean lMemberTurnoverBean = memberTurnoverDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lMemberTurnoverBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lMemberTurnoverBean;
    }
    
    public List<MemberTurnoverBean> findList(ExecutionContext pExecutionContext, MemberTurnoverBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return memberTurnoverDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, MemberTurnoverBean pMemberTurnoverBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        MemberTurnoverBean lOldMemberTurnoverBean = null;
        if (pNew) {

            pMemberTurnoverBean.setRecordCreator(pUserBean.getId());
            memberTurnoverDAO.insert(lConnection, pMemberTurnoverBean);
        } else {
            lOldMemberTurnoverBean = findBean(pExecutionContext, pMemberTurnoverBean);
            

            pMemberTurnoverBean.setRecordUpdator(pUserBean.getId());
            if (memberTurnoverDAO.update(lConnection, pMemberTurnoverBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, MemberTurnoverBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        MemberTurnoverBean lMemberTurnoverBean = findBean(pExecutionContext, pFilterBean);
        lMemberTurnoverBean.setRecordUpdator(pUserBean.getId());
        memberTurnoverDAO.delete(lConnection, lMemberTurnoverBean);        


        pExecutionContext.commitAndDispose();
    }
    
}
