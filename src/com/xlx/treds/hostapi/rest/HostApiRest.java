package com.xlx.treds.hostapi.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.PostProcessMonitor;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.hostapi.bean.BuyerSellerInpurBean;
import com.xlx.treds.hostapi.bean.InstrumentResponseBean;
import com.xlx.treds.hostapi.bo.HostApiBo;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/hostApi")
public class HostApiRest {

	private static Logger logger = Logger.getLogger(HostApiRest.class);

	private HostApiBo hostApiBo;

	public HostApiRest() {
		super();
		hostApiBo = new HostApiBo();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/buyerSellerLink1") /// {supplier}/{purchaser}
	public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pFilter)
			throws Exception {
		logger.info("inside hostapi rest");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("buyercode - " + lMap.get("buyerCode"));
		logger.info("sellercode - " + lMap.get("sellerCode"));

		return hostApiBo.getPurchaserSupplierBO(pExecutionContext, lMap);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey="pursuplnk-save")
	@Path("/bulkuploadBSLinkInitiate")
	public String insertMultipleBSLink(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pMessage) throws Exception {

		return hostApiBo.bulkUploadBSLinkBo(pExecutionContext, pRequest, pMessage);

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/instrument") /// {supplier}/{purchaser}
	public String getInstrumentRest(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		logger.info("inside instrument rest");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("buyercode - " + lMap.get("buyerCode"));
		logger.info("sellercode - " + lMap.get("sellerCode"));
		logger.info("instrumentId - " + lMap.get("id"));

		return hostApiBo.getInstrumentBo(pExecutionContext, lMap);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/userDetails") /// {supplier}/{purchaser}
	public String getAppUserRest(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		logger.info("inside user rest");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("domain - " + lMap.get("domain"));
		logger.info("loginId - " + lMap.get("loginId"));

		return hostApiBo.getUserBo(pExecutionContext, lMap);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/aggregatorPurchaserMap") /// {supplier}/{purchaser}
	public String getAggregatorPurchaserMap(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {
		logger.info("inside user rest");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("purchaser - " + lMap.get("purchaser"));
		logger.info("aggregator - " + lMap.get("aggregator"));

		return hostApiBo.getAggregatorPurchaserBo(pExecutionContext, lMap);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/factoringUnit") /// {supplier}/{purchaser}
	public String getFactgoringUnitRest(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {
		logger.info("inside user rest");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("buyerCode - " + lMap.get("buyerCode"));
		logger.info("sellerCode - " + lMap.get("sellerCode"));
		logger.info("factoringUnitId - " + lMap.get("factoringUnitId"));

		return hostApiBo.getFactoringUnitBo(pExecutionContext, lMap);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/createInstrument")
	// @Secured(secKey="inst-save")
	public String createInstrument(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pMessage) throws Exception {

		String lResponse = null;
		try {
			lResponse = hostApiBo.saveInstrumentBo(pExecutionContext, pRequest, pMessage);
		} catch (Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}
		return lResponse;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/instCntrApprove")
	public String updateStatusApprove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		String lResponse = null;
		try {
			lResponse = hostApiBo.approveInstrumentBo(pExecutionContext, pRequest, pFilter);
		} catch (Exception e) {
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}
		return lResponse;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/instCntrChkrApprove")
	public String updateStatusApproveChecker(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {
		String lResponse = null;
		try {
			lResponse = hostApiBo.approveInstrumentCheckerBo(pExecutionContext, pRequest, pFilter);
		} catch (Exception e) {
			e.printStackTrace();
			lResponse = e.getMessage();
			logger.debug(e.getStackTrace());
			throw e;
		}
		return lResponse;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/obligations") /// {supplier}/{purchaser}
	public String getObligationRest(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		logger.info("inside getObligationRest");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("obligationId - " + lMap.get("obligationId"));
		logger.info("factoringUnitId - " + lMap.get("factoringUnitId"));

		return hostApiBo.getObligationBo(pExecutionContext, lMap);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	// @Secured(secKey="instchk-approve")
	@Path("/initChkApprove")
	public String updateStatusInitChecker(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {
		return hostApiBo.approveInitChecker(pExecutionContext, pRequest, pFilter);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "inst-status")
	@Path("/initApprove")
	public String initApprove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {

		return hostApiBo.updateStatusInitApprove(pExecutionContext, pRequest, pFilter);

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "inst-status")
	@Path("/bidAccept")
	public String bidAccept(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {

		return hostApiBo.bidAcceptBO(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Factored);

	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "oblig-view")
	@Path("/settlementMis")
	public Object list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {

		return hostApiBo.getSettlementMISBo(pExecutionContext, pRequest, pFilter);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-save")
	@Path("/bsLinkStatusUpdate")
	public String updateStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pMessage) throws Exception {

		return hostApiBo.updateStatusBSLinkBo(pExecutionContext, pRequest, pMessage);

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/purchaserSupplierLimit") /// {supplier}/{purchaser}
	public String purSuppLimitUtilization(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {
		logger.info("inside purSuppLimitUtilization");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("buyercode - " + lMap.get("buyerCode"));
		logger.info("sellercode - " + lMap.get("sellerCode"));

		return hostApiBo.getPurSuppLimitUtil(pExecutionContext, lMap);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/billDetails") /// {supplier}/{purchaser}
	public String billDetailRest(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		logger.info("inside billDetailRest");
		logger.info("pFilter -- " + pFilter);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		logger.info("lmap - " + lMap);
		logger.info("entityCode - " + lMap.get("entityCode"));

		return hostApiBo.getBIllDetailsBo(pExecutionContext, lMap);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "factunitsp-withdraw")
	@Path("/factoringUnitWithdraw")
	public String withdrawFactoringUnit(@Context ExecutionContext pExecutionContext,
			@Context HttpServletRequest pRequest, String pFilter) throws Exception {

		return hostApiBo.withDrawFactoringUnit(pExecutionContext, pRequest, pFilter);

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// @Secured(secKey = "pursuplnk-view")
	@Path("/buyerSellerLink2/{supplier}/{purchaser}") /// {supplier}/{purchaser}
	public String getSecond(@Context ExecutionContext pExecutionContext, @PathParam("supplier") String supplier,
			@PathParam("purchaser") String purchaser) throws Exception {
		logger.info("inside hostapi rest");
		return hostApiBo.getSecond(pExecutionContext, supplier, purchaser);
	}

}