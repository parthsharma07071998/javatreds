package com.xlx.treds.entity.bean;

import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.IKeyValEnumInterface;

public class AppEntityPreferenceBean {
	public static final String FIELDGROUP_UPDATEBYENTITY = "updateByEntity";
	public static final String FIELDGROUP_UPDATEBYPLATFORM = "updateByPlatform";
	public static final String FIELDGROUP_BUYERFIELDS = "buyerFields";
	public static final String FIELDGROUP_SELLERFIELDS = "sellerFields";
	public static final String FIELDGROUP_FINANCIERFIELDS = "financierFields";
	
    public enum Idcp implements IKeyValEnumInterface<String>{
        Credit_Period("CP","Credit Period"),Due_date("DD","Due date");
        
        private final String code;
        private final String desc;
        private Idcp(String pCode, String pDesc) {
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
    public enum Upds implements IKeyValEnumInterface<String>{
        Calendar_Days("CD","Calendar Days"),Working_Days("WD","Working Days");
        
        private final String code;
        private final String desc;
        private Upds(String pCode, String pDesc) {
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

    public enum BillType implements IKeyValEnumInterface<String>{
        Daily("D","Daily"),Monthly("M","Monthly");
        
        private final String code;
        private final String desc;
        private BillType(String pCode, String pDesc) {
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
    
    private Idcp idcp;
    private Upds upds;
    private Yes ick;
    private Yes ccmod;
    private Yes skipmonetago;
    private BillType billType;
    private Yes elb;
    private Yes ecf;
    //Counter API verification
    private Yes cav;
    //Charge splitting
    private Yes acs;
    //Integration enabled
	private Yes isInt;
    private Yes elp;
    
    
    public Idcp getIdcp() {
        return idcp;
    }

    public void setIdcp(Idcp pIdcp) {
        idcp = pIdcp;
    }

    public Upds getUpds() {
        return upds;
    }

    public void setUpds(Upds pUpds) {
        upds = pUpds;
    }

	public Yes getIck() {
		return ick;
	}

	public void setIck(Yes pIck) {
		this.ick = pIck;
	}
	
	public Yes getCcmod() {
		return ccmod;
	}

	public void setCcmod(Yes pCcmod) {
		this.ccmod = pCcmod;
	}
	
	public Yes getSkipmonetago() {
		return skipmonetago;
	}

	public void setSkipmonetago(Yes pSkipmonetago) {
		this.skipmonetago = pSkipmonetago;
	}
	
	public BillType getBillType() {
	    return billType;
    }

    public void setBillType(BillType pBillType) {
        billType = pBillType;
    }
	
    public Yes getElb() {
		return elb;
	}

	public void setElb(Yes pElb) {
		this.elb = pElb;
	}
	
	public Yes getEcf() {
		return ecf;
	}

	public void setEcf(Yes pEcf) {
		this.ecf = pEcf;
	}

	public Yes getCav() {
		return cav;
	}

	public void setCav(Yes cav) {
		this.cav = cav;
	}
	
	public Yes getAcs() {
		return acs;
	}

	public void setAcs(Yes acs) {
		this.acs = acs;
	}
	
    public Yes getIsInt() {
        return isInt;
    }

    public void setIsInt(Yes pIsInt) {
        isInt = pIsInt;
    }
    
    public Yes getElp() {
        return elp;
    }

    public void setElp(Yes pElp) {
    	elp = pElp;
    }
}