package com.xlx.treds.other.bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;

public class GEMInvoiceBean {
	private GenericDAO<CompanyLocationBean> companyLocationBeanDAO;
	
    public enum Status implements IKeyValEnumInterface<String>{
        Success("S","Success"),Pending("P","Pending"),Closed("C","Closed");
        
        private final String code;
        private final String desc;
        private Status(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }
    public GEMInvoiceBean() {
    	super();
    	companyLocationBeanDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
    }

    private Long id;
    private Long arrId;
    private String supplier;
    private String supName;
    private String supGstn;
    private String supPan;
    private String supLocation;
    private String purchaser;
    private String purName;
    private String purGstn;
    private String purPan;
    private String purLocation;
    private Date goodsAcceptDate;
    private Date poDate;
    private String poNumber;
    private String instNumber;
    private Date instDate;
    private Date instDueDate;
    private BigDecimal amount;
    private BigDecimal adjAmount;
    private Long creditPeriod;
    private Status status;
    private Long creator;
    private Timestamp createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getArrId() {
        return arrId;
    }

    public void setArrId(Long pArrId) {
        arrId = pArrId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public String getSupName() {
        try {
        	if (supplier!=null) {
        		return TredsHelper.getInstance().getAppEntityBean(supplier).getName();
        	}
		} catch (MemoryDBException e) {
			e.printStackTrace();
		}
		return "";
    }

    public void setSupName(String pSupName) {
    }

    public String getSupGstn() {
        return supGstn;
    }

    public void setSupGstn(String pSupGstn) {
        supGstn = pSupGstn;
    }

    public String getSupPan() {
        return supPan;
    }

    public void setSupPan(String pSupPan) {
        supPan = pSupPan;
    }

    public String getSupLocation() {
    	try {
        	if (supplier!=null) {
        		AppEntityBean lAeBean = TredsHelper.getInstance().getAppEntityBean(supplier);
        		if (lAeBean!=null) {
        			try(Connection lConnection = DBHelper.getInstance().getConnection()){
        				CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
        				lCompanyLocationBean.setCdId(lAeBean.getCdId());
        				lCompanyLocationBean.setGstn(supGstn);
        				lCompanyLocationBean = companyLocationBeanDAO.findBean(lConnection, lCompanyLocationBean);
        				if (lCompanyLocationBean!=null) {
        					return lCompanyLocationBean.getName();
        				}
        			}catch (Exception lEx) {
        				
        			}
        		}
        		
        	}
		} catch (MemoryDBException e) {
			e.printStackTrace();
		}
		return "";
    	
    }

    public void setSupLocation(String pSupLocation) {
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public String getPurName() {
    	try {
        	if (purchaser!=null) {
        		return TredsHelper.getInstance().getAppEntityBean(purchaser).getName();
        	}
		} catch (MemoryDBException e) {
			e.printStackTrace();
		}
		return "";
    }

    public void setPurName(String pPurName) {
    }

    public String getPurGstn() {
        return purGstn;
    }

    public void setPurGstn(String pPurGstn) {
        purGstn = pPurGstn;
    }

    public String getPurPan() {
        return purPan;
    }

    public void setPurPan(String pPurPan) {
        purPan = pPurPan;
    }

    public String getPurLocation() {
    	try {
        	if (purchaser!=null) {
        		AppEntityBean lAeBean = TredsHelper.getInstance().getAppEntityBean(purchaser);
        		if (lAeBean!=null) {
        			try(Connection lConnection = DBHelper.getInstance().getConnection()){
        				CompanyLocationBean lCompanyLocationBean = new CompanyLocationBean();
        				lCompanyLocationBean.setCdId(lAeBean.getCdId());
        				lCompanyLocationBean.setGstn(purGstn);
        				lCompanyLocationBean = companyLocationBeanDAO.findBean(lConnection, lCompanyLocationBean);
        				if (lCompanyLocationBean!=null) {
        					return lCompanyLocationBean.getName();
        				}
        			}catch (Exception lEx) {
        				
        			}
        		}
        		
        	}
		} catch (MemoryDBException e) {
			e.printStackTrace();
		}
		return "";
    }

    public void setPurLocation(String pPurLocation) {
        purLocation = pPurLocation;
    }

    public Date getGoodsAcceptDate() {
        return goodsAcceptDate;
    }

    public void setGoodsAcceptDate(Date pGoodsAcceptDate) {
        goodsAcceptDate = pGoodsAcceptDate;
    }

    public Date getPoDate() {
        return poDate;
    }

    public void setPoDate(Date pPoDate) {
        poDate = pPoDate;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String pPoNumber) {
        poNumber = pPoNumber;
    }

    public String getInstNumber() {
        return instNumber;
    }

    public void setInstNumber(String pInstNumber) {
        instNumber = pInstNumber;
    }

    public Date getInstDate() {
        return instDate;
    }

    public void setInstDate(Date pInstDate) {
        instDate = pInstDate;
    }

    public Date getInstDueDate() {
        return instDueDate;
    }

    public void setInstDueDate(Date pInstDueDate) {
        instDueDate = pInstDueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
    }

    public BigDecimal getAdjAmount() {
        return adjAmount;
    }

    public void setAdjAmount(BigDecimal pAdjAmount) {
        adjAmount = pAdjAmount;
    }

    public Long getCreditPeriod() {
        return creditPeriod;
    }

    public void setCreditPeriod(Long pCreditPeriod) {
        creditPeriod = pCreditPeriod;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long pCreator) {
        creator = pCreator;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp pCreateTime) {
        createTime = pCreateTime;
    }

}