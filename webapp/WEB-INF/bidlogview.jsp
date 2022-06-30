<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lId = StringEscapeUtils.escapeHtml(request.getParameter("fuid"));
String lFinEntity = StringEscapeUtils.escapeHtml(request.getParameter("financierEntity"));
String lUrl = "bidlogview?fuid="+lId;
String lDnlUrl = "bidlogviewdnl?fuid="+lId;
if(CommonUtilities.hasValue(lFinEntity)){
	lUrl+="&financierEntity="+lFinEntity;
	lDnlUrl+="&financierEntity="+lFinEntity;
}
%>

	<div class="content" id="conBidLog">
    	<!-- frmMain -->
	    	<div class="xform view" id="frmMain">
    			<fieldset>
					<div class="box-body" style="max-height:300px;overflow-y:auto">
					</div>
					<div class="modal-footer">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-info btn-lg"  data-dismiss="modal" id="btnDownloadCSV"><span class="fa fa-download"></span> Download CSV</button>
									<button type="button" class="btn btn-info-inverse btn-lg btn-close"  data-dismiss="modal" ><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
					</div>
    			</fieldset>
    	</div>
    	<!-- frmMain -->
	</div>
	<style>
	.bg-lightgray {background-color:#fafafa}
	</style>

   	<script id="tplBidLogView" type="text/x-handlebars-template">
			<h3>Factoring Unit : <%=lId%></h3>
    		<table class="table table-bordered " data-col-chooser="spnColumnChooser" id="tblData" data-scroll-y="430px">
					<thead>
{{#prov}}
					<tr>
						<th></th>
						<th colspan="4" class="text-center">APPROVED</th>
						<th colspan="6" class="bg-lightgray text-center">PROVISIONAL</th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
{{/prov}}
					<tr>
						<th data-width="100px" width="100px">User</th>
						<th data-width="100px" width="80px" class="text-right">Rate %</th>
						<th data-width="100px" width="80px" class="text-right">Retention Margin %</th>
						<th data-width="120px" width="120px">Valid Till</th>
						<th data-width="100px" width="100px">Bid Type</th>
{{#prov}}
						<th data-width="100px" width="100px" class="bg-lightgray">Action</th>
						<th data-width="100px" width="80px" class="text-right bg-lightgray">Rate %</th>
						<th data-width="100px" width="80px" class="text-right bg-lightgray">Retention Margin %</th>
						<th data-width="120px" width="120px" class="bg-lightgray">Valid Till</th>
						<th data-width="100px" width="100px" class="bg-lightgray">Bid Type</th>
						<th data-width="100px" width="100px" class="bg-lightgray">Approval Status</th>
{{/prov}}
						<th data-width="100px" width="100px">Cost Leg</th>
						<th data-width="120px" width="100px">Status</th>
						<th data-width="150px" width="200px">Time</th>
						<th data-width="70px" >Status Remarks</th>
					</tr></thead>
					<tbody>
{{#each data}}
<tr>
<td>{{lastLoginId}}</td>
<td class="text-right">{{#formatDec}}{{rate}}{{/formatDec}}</td>
<td class="text-right">{{#formatDec}}{{haircut}}{{/formatDec}}</td>
<td>{{validTill}}</td>
<td>{{bidType}}</td>
{{#if ../prov}}
<td class="bg-lightgray">{{provAction}}</td>
<td class="text-right bg-lightgray">{{#formatDec}}{{provRate}}{{/formatDec}}</td>
<td class="text-right bg-lightgray">{{#formatDec}}{{provHaircut}}{{/formatDec}}</td>
<td class="bg-lightgray">{{provValidTill}}</td>
<td class="bg-lightgray">{{provBidType}}</td>
<td class="bg-lightgray">{{appStatus}}</td>
{{/if}}
<td>{{costLeg}}</td>
<td>{{status}}</td>
<td>{{timestamp}}</td>
<td>{{statusRemarks}}</td>
</tr>			
{{/each}}

					</tbody>
				</table>	
	</script>

	<script type="text/javascript">
	var tplBidLogView;
	$(document).ready(function() {
		tplBidLogView = Handlebars.compile($('#tplBidLogView').html());
		$.ajax({
			url: '<%=lUrl%>',
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
				var lProv=false;
				$.each(pObj,function(pIdx,pVal){
					if (pVal.provAction) {
						lProv=true;
						return false;
					}
				});
 				$('#conBidLog #frmMain .box-body').html(tplBidLogView({prov:lProv, data:pObj}));
			},
			error: errorHandler,
		});
		$('#btnDownloadCSV').on('click', function() {
			downloadFile("<%=lDnlUrl%>",null,null);
		});
	});
	</script>
