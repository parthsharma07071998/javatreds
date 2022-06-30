package com.xlx.treds.master.rest;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/misreports")
public class MISReportResource {
	
    public MISReportResource() {
        super();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
//    @Secured(secKey="mnu-misreports")
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
    		 , @QueryParam("f") String pFolder , @QueryParam("q") String pQuery
    		 , @QueryParam("c") String pSeperator , @QueryParam("p") String pPipe) throws Exception {
        if (pFolder != null)
            pRequest.setAttribute("f", pFolder);
        if(pQuery != null)
        	pRequest.setAttribute("q", pQuery);
        if(pSeperator != null)
        	pRequest.setAttribute("q", pSeperator);
        if(pPipe != null)
        	pRequest.setAttribute("p", pPipe);
        pRequest.getRequestDispatcher("/WEB-INF/reports/Report.jsp").forward(pRequest, pResponse);
    }
}