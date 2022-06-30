package com.xlx.treds.instrument.bean;

import java.math.BigDecimal;
import java.sql.Date;

import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.treds.AppConstants.CostBearer;

public interface ILegInterest {
	
	BigDecimal getPurchaserLeg1Interest();
    void setPurchaserLeg1Interest(BigDecimal pPurchaserLeg1Interest);

    BigDecimal getSupplierLeg1Interest() ;
    void setSupplierLeg1Interest(BigDecimal pSupplierLeg1Interest);

    BigDecimal getPurchaserLeg2Interest();
    void setPurchaserLeg2Interest(BigDecimal pPurchaserLeg2Interest) ;

    BigDecimal getLeg2ExtensionInterest() ;
    void setLeg2ExtensionInterest(BigDecimal pLeg2ExtensionInterest) ;
	
	BigDecimal getAmount();
    void setAmount(BigDecimal pAmount);

	BigDecimal getNetAmount();
    void setNetAmount(BigDecimal pNetAmount);
    
    BigDecimal getFactoredAmount();
    void setFactoredAmount(BigDecimal pFactoredAmount);
    
    Date getMaturityDate();
    void setMaturityDate(Date pMaturityDate);
    
	Date getStatDueDate();
    void setStatDueDate(Date pStatDueDate);

    Yes getEnableExtension();
    void setEnableExtension(Yes pEnableExtension);
    
	Date getExtendedDueDate();
    void setExtendedDueDate(Date pExtendedDueDate);

    CostBearer getPeriod1CostBearer();
    void setPeriod1CostBearer(CostBearer pPeriod1CostBearer);
    
    CostBearer getPeriod2CostBearer();
    void setPeriod2CostBearer(CostBearer pPeriod2CostBearer);

    CostBearer getPeriod3CostBearer();
    void setPeriod3CostBearer(CostBearer pPeriod3CostBearer);

    BigDecimal getPeriod1CostPercent();
    void setPeriod1CostPercent(BigDecimal pPeriod1CostPercent);
   
    BigDecimal getPeriod2CostPercent();
    void setPeriod2CostPercent(BigDecimal pPeriod2CostPercent);

    BigDecimal getPeriod3CostPercent();
    void setPeriod3CostPercent(BigDecimal pPeriod3CostPercent);
    
    String getPurchaser();
    void setPurchaser(String pPurchaser);

    String getSupplier();
    void setSupplier(String pSupplier);
	
    
    CostBearer getPeriod1ChargeBearer();
    void setPeriod1ChargeBearer(CostBearer pPeriod1ChargeBearer);
    
    CostBearer getPeriod2ChargeBearer();
    void setPeriod2ChargeBearer(CostBearer pPeriod2ChargeBearer);

    CostBearer getPeriod3ChargeBearer();
    void setPeriod3ChargeBearer(CostBearer pPeriod3ChargeBearer);

    BigDecimal getPeriod1ChargePercent();
    void setPeriod1ChargePercent(BigDecimal pPeriod1ChargePercent);
   
    BigDecimal getPeriod2ChargePercent();
    void setPeriod2ChargePercent(BigDecimal pPeriod2ChargePercent);

    BigDecimal getPeriod3ChargePercent();
    void setPeriod3ChargePercent(BigDecimal pPeriod3ChargePercent);
    
    

}
