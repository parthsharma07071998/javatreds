package com.xlx.treds.other.bo;

import java.sql.Connection;
import java.util.List;

import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.other.bean.GEMInvoiceBean;

public class GEMInvoiceBO {
    
    private GenericDAO<GEMInvoiceBean> gEMInvoiceDAO;

    public GEMInvoiceBO() {
        super();
        gEMInvoiceDAO = new GenericDAO<GEMInvoiceBean>(GEMInvoiceBean.class);
    }
    
    public GEMInvoiceBean findBean(ExecutionContext pExecutionContext, 
        GEMInvoiceBean pFilterBean) throws Exception {
        GEMInvoiceBean lGEMInvoiceBean = gEMInvoiceDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lGEMInvoiceBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lGEMInvoiceBean;
    }
    
    public List<GEMInvoiceBean> findList(ExecutionContext pExecutionContext, GEMInvoiceBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT GEMID,GEMARRID,NVL(GEMSUPPLIER,S.CDCODE) GEMSUPPLIER, ");
    	lSql.append(" GEMSUPGSTN,GEMSUPPAN,nvl(GEMPURCHASER,P.CDCODE) GEMPURCHASER,GEMPURGSTN, ");
    	lSql.append(" GEMPURPAN,GEMGOODSACCEPTDATE,GEMPODATE,GEMPONUMBER,GEMINSTNUMBER,GEMINSTDATE, ");
    	lSql.append(" GEMINSTDUEDATE,GEMAMOUNT,GEMADJAMOUNT,GEMCREDITPERIOD,GEMSTATUS,GEMCREATOR,GEMCREATETIME ");
    	lSql.append(" FROM GEMINVOICES ");
    	lSql.append(" LEFT OUTER JOIN COMPANYDETAILS P ON (GEMPURPAN=P.CDPAN) ");
    	lSql.append(" LEFT OUTER JOIN COMpanydetails S on (GEMSUPPAN=S.CDPAN) ");
    	lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS PL ON (GEMPURGSTN=PL.CLGSTN) ");
    	lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS SL ON (GEMSUPGSTN=SL.CLGSTN) ");
    	lSql.append(" WHERE (PL.CLCDID IS NULL OR P.CDID IS NULL OR PL.CLCDID=P.CDID ) ");
    	lSql.append(" AND ( SL.CLCDID IS NULL OR S.CDID IS NULL OR SL.CLCDID=S.CDID ) ");
    	gEMInvoiceDAO.appendAsSqlFilter(lSql, pFilterBean, false);
        return gEMInvoiceDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
    }
    
    public void save(ExecutionContext pExecutionContext, GEMInvoiceBean pGEMInvoiceBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        GEMInvoiceBean lOldGEMInvoiceBean = null;
        if (pNew) {
            gEMInvoiceDAO.insert(lConnection, pGEMInvoiceBean);
        } else {
            lOldGEMInvoiceBean = findBean(pExecutionContext, pGEMInvoiceBean);
            

            if (gEMInvoiceDAO.update(lConnection, pGEMInvoiceBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, GEMInvoiceBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        GEMInvoiceBean lGEMInvoiceBean = findBean(pExecutionContext, pFilterBean);
        gEMInvoiceDAO.delete(lConnection, lGEMInvoiceBean);        


        pExecutionContext.commitAndDispose();
    }
    
}
