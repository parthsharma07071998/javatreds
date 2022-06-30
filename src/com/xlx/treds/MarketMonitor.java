package com.xlx.treds;

import java.sql.Connection;
import java.sql.Timestamp;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.io.ManagedThread;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.auction.bo.ObligationExtensionBO;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.master.bean.ConfirmationWindowBean;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.sftp.AdapterSFTPClientHolder;
import com.xlx.treds.user.bean.AppUserBean;
// all auto cron activities like eod will be performed by this thread
public class MarketMonitor extends ManagedThread {
    private static final Logger logger = LoggerFactory.getLogger(MarketMonitor.class);
    private static MarketMonitor theInstance;
    private ConfirmationWindowBean prevConfWindowBean;
    private FactoringUnitBO factoringUnitBO;
    private ObligationExtensionBO extBO;
    private Timestamp eodTriggerTime = null;
    
    public static MarketMonitor createInstance() {
        if (theInstance == null) {
            synchronized (MarketMonitor.class) {
                if (theInstance == null) {
                    MarketMonitor lMarketMonitor = new MarketMonitor();
                    lMarketMonitor.newThread("MktMon");
                    theInstance = lMarketMonitor;
                }
            }
        }
        return theInstance;
    }
    
    public static MarketMonitor getInstance() {
        return theInstance;
    }
    
    private MarketMonitor() {
        super();
        factoringUnitBO = new FactoringUnitBO();
        extBO = new ObligationExtensionBO();
    }
    
    public boolean initThread() {
        return true;
    }
    
    public void serviceThread() {
        // check confirmation window beans
        try {
        	OtherResourceCache lOtherResourceCache = OtherResourceCache.getInstance();
            ConfirmationWindowBean lConfirmationWindowBean = lOtherResourceCache.getConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
            if (prevConfWindowBean != null) {
                if (prevConfWindowBean.getStatus() == ConfirmationWindowBean.Status.Closed) {
                    // auto confirm bids after confirmation window closed
                    logger.info("MktMonitor : Confirmation window closed : " + prevConfWindowBean.getConfStartTime() + " - " + prevConfWindowBean.getConfEndTime());
                    boolean lExpireValidity = !lOtherResourceCache.isConfirmationWindowPending(OtherResourceCache.AUCTIONTYPE_NORMAL);
                    //find whether this is the first confirmation window close.
                    if(lOtherResourceCache.isFirstConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL, prevConfWindowBean)){
                        logger.info("MktMonitor : AutoConfirmBids at CutOffTime Started.");
                        factoringUnitBO.autoConfirmBids(prevConfWindowBean.getSettlementDate(),AutoAcceptBid.CutOffTime);
                        logger.info("MktMonitor : AutoConfirmBids at CutOffTime Ended.");
                        try {
                        	logger.info("MktMonitor : First Confirmation Window closed.");
                    		//this is done after ICICI request to move the files in the IN folder to the OUT folder at the end of the last confirmation window.
                        	logger.info("MktMonitor : Calling the SFTP files from IN to OUT.");
                    		SFTPHelper.getInstance().moveSFTPFilesINtoOUT();
                        	logger.info("MktMonitor : Called the SFTP files from IN to OUT.");
                    		//
                		} catch (Exception e) {
                			logger.info("MktMonitor : Error while moving SFTP files form IN to OUT. : " + e.getMessage() );
                			logger.info((e.getStackTrace()!=null)?e.getStackTrace().toString():"");
                		}
                    }
                    // lExpireValidity = !ConfirmationWindowPending
                    if(lExpireValidity){
                    	if(TredsHelper.getInstance().isAutoEODEnabled()){
                    		Long lTriggerAfterMinutes = TredsHelper.getInstance().getTriggerAutoEODAfter();
                    		if(lTriggerAfterMinutes!=null && lTriggerAfterMinutes.longValue() > 0){
                    			eodTriggerTime = new Timestamp(DateUtils.addMinutes(prevConfWindowBean.getConfEndTime(), lTriggerAfterMinutes.intValue()).getTime());
                    		}
                    	}
                    }
                }
            }
            else
            {
                if(eodTriggerTime!=null){
                	if(CommonUtilities.getCurrentDateTime().after(eodTriggerTime)){
                		EndOfDayBO lEndOfDayBO  = new EndOfDayBO();
                		AppUserBean lAdminUserBean = TredsHelper.getInstance().getAdminUser(AppConstants.DOMAIN_PLATFORM);
                		try ( Connection lConn = DBHelper.getInstance().getConnection() ){
                			lEndOfDayBO.performeEODAutomated( lConn, lAdminUserBean );
                		}catch(Exception e){
                			logger.info("MktMonitor : "+e.getMessage());
                		}
                		eodTriggerTime = null;
                		//this should be done so that when we get new confirmation window for new business date,
                		// the code would go in else, to check first confirmation window open
                		//prevConfWindowBean = null;
                	}
                }
            	//ONLY ONCE WHEN THE PROCESS IS STARTED - THEREOF AFTER EOD AND EVENTUALLY WE WILL ALWAYS HAVE A NOT NULL PREVCONFIRMATION WINDOW
				//when the VERY FIRST confirmation window has started
                if (lConfirmationWindowBean!=null && (lConfirmationWindowBean.getStatus() == ConfirmationWindowBean.Status.Open)) {
                    logger.info("MktMonitor : First Confirmation window opened : " + lConfirmationWindowBean.getConfStartTime() + " - " + lConfirmationWindowBean.getConfEndTime());
                    try(Connection lConn = DBHelper.getInstance().getConnection()){
                    	logger.info("MktMonitor : Auto Expiring Bids.");
                        factoringUnitBO.autoExpireBids(lConn,OtherResourceCache.getInstance().addDaysToDate(TredsHelper.getInstance().getBusinessDate(),-1), null, true);
                        logger.info("MktMonitor : Updating best bid after expiry.");
                        factoringUnitBO.updateBestBidAfterExpireBids(lConn, null);
                        logger.info("MktMonitor : Auto Expire Extension Request.");
                        extBO.autoExpireExtensionRequest(lConn, TredsHelper.getInstance().getBusinessDate(), null);
                    }catch (Exception lEx) {
                    	logger.error("MktMonitor : Error in Bid expiry : ",lEx.getMessage());
                    }
                	factoringUnitBO.autoConfirmBids(lConfirmationWindowBean.getSettlementDate(),AutoAcceptBid.OnRecepitOfBid);
                    
                }
                //SUBSEQUENT CONFIRMATION WINDOW HAS OPENED
                if (lConfirmationWindowBean!=null 
                		&& ConfirmationWindowBean.Status.Open.equals(lConfirmationWindowBean.getStatus())) {
                    logger.info("MktMonitor : Prev Confirmation window Pending and New Confirmation Window Open.");
                    if(lOtherResourceCache.isSecondConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL, lConfirmationWindowBean )){
                        logger.info("MktMonitor : Second and Not Last Confirmation window opened : " + lConfirmationWindowBean.getConfStartTime() + " - " + lConfirmationWindowBean.getConfEndTime());
                    	ConfirmationWindowBean lFirstConfirmationWindowBean = lOtherResourceCache.getFirstConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
                    	if(lFirstConfirmationWindowBean!=null){
                            logger.info("MktMonitor : Second Confirmation opened. Do nothing.");
                    	}
                    }
                }
            }
            prevConfWindowBean = lConfirmationWindowBean;
        } catch (Exception lException) {
            logger.error("MktMonitor : Error while getting confirmation window", lException);
        }
//        AdapterSFTPClientHolder.createInstance(AdapterSFTPClientHolder.KEY_BHELSFTPCLIENT, AdapterSFTPClientHolder.FILENAME_BHELSFTPCLIENT);
        AdapterSFTPClientHolder.createInstance(AdapterSFTPClientHolder.KEY_EDNSFTPCLIENT, AdapterSFTPClientHolder.FILENAME_EDNSFTPCLIENT);
//        AdapterSFTPClientHolder.createInstance(AdapterSFTPClientHolder.KEY_JBMSFTPCLIENT, AdapterSFTPClientHolder.FILENAME_JBMSFTPCLIENT);
//        AdapterSFTPClientHolder.createInstance(AdapterSFTPClientHolder.KEY_NEELSFTPCLIENT, AdapterSFTPClientHolder.FILENAME_NEELSFTPCLIENT);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException lException) {
            lException.printStackTrace();
        }
    }
    
    public void destroyThread() {
    }

}
