<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Enumeration"%>
<%
String lUrl = (String)request.getAttribute("url");
if (lUrl == null)
    lUrl = request.getParameter("url");
lUrl = StringEscapeUtils.escapeHtml(lUrl);
%>
	<div class="content" id="contFileUpload">
		<!-- frmMain -->
    	<div class="xform box" id="frmUploadMain">
    		<div class="box-body">
    			<div class="row">
					<div class="col-sm-4"><section><label class="label">File:</label></section></div>
					<div class="col-sm-8"><section><label class="label" id="sect-filename"></label></section></div>
				</div>
    		</div>
    		<div class="box-footer">
				<div class="row">
					<div class="col-sm-12">
		    			<div style="width:0px;height:0px;overflow:hidden"><input type=file id="txtFile"/></div>
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-lg btn-default" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
						</div>
						<div class="btn-groupX pull-right hidden" id="btn-grp-sel">
			    			<button type="button" id="upl-btn-select" class="btn btn-lg btn-success"><span class="fa fa-upload"></span> Select File</button>
						</div>
						<div class="btn-groupX pull-right hidden" id="btn-grp-upl">
			    			<button type="button" id="upl-btn-upload" class="btn btn-lg btn-success"><span class="fa fa-upload"></span> Upload</button>
							<button type="button" id="upl-btn-clear" class="btn btn-lg btn-default"><span class="fa fa-remove"></span> Clear</button>
						</div>
						<div class="btn-groupX pull-right hidden" id="btn-grp-ref">
							<button type="button" id="upl-btn-refresh" class="btn btn-lg btn-success"><span class="fa fa-refresh"></span> Refresh</button>
						</div>
					</div>
				</div>
    		</div>
    	</div>
    	<!-- frmMain -->
	</div>
   	<script id="tplUploadStatus" type="text/x-handlebars-template">
		<div class="row">
			<div class="col-sm-4"><section><label class="label">File:</label></section></div>
			<div class="col-sm-8"><section><label class="label">{{fileName}}</label></section></div>
		</div>
		<div class="row">
			<div class="col-sm-4"><section><label class="label">File Id:</label></section></div>
			<div class="col-sm-8"><section><label class="label">{{id}}</label></section></div>
		</div>
		<div class="row">
			<div class="col-sm-4"><section><label class="label">Record Count:</label></section></div>
			<div class="col-sm-8"><section><label class="label">{{recordCount}}</label></section></div>
		</div>
		<div class="row">
			<div class="col-sm-4"><section><label class="label">Status:</label></section></div>
			<div class="col-sm-8"><section><label class="label">{{status}}</label></section></div>
		</div>
		{{#if successCount}}<div class="row">
			<div class="col-sm-4"><section><label class="label">Success:</label></section></div>
			<div class="col-sm-8"><section><label class="label">{{successCount}}</label></section></div>
		</div>{{/if}}
		{{#if errorCount}}<div class="row">
			<div class="col-sm-4"><section><label class="label">Error:</label></section></div>
			<div class="col-sm-8"><section><label class="label">{{errorCount}}</label></section></div>
		</div>{{/if}}
		{{#if returnFileName}}<div class="row">
			<div class="col-sm-4"><section><label class="label">Return File:</label></section></div>
			<div class="col-sm-8"><section><label class="label">{{returnFileName}}</label>
			<button type="button" class="btn btn-lg btn-default" onClick="javascript:downRtnFile()" id="upl-btn-dnrt"><span class="fa fa-download"></span> Download</button></section></div>
		</div>{{/if}}
		{{#if remarks}}<div class="text-danger">{{remarks}}</div>{{/if}}
		<div class="row">
			<div class="col-sm-12"><section><label class="label"><b>NOTE:</b> Click Refresh button to download response file and check errors if any.</label></section></div>
		</div>
	</script>
	<script type="text/javascript">
	var file$, curLoc,tplUploadStatus, fileId;
	$(document).ready(function() {
		file$ = $('#frmUploadMain #txtFile');
		tplUploadStatus = Handlebars.compile($('#tplUploadStatus').html());
		$('#frmUploadMain #upl-btn-select').on('click', function(pEvent){
			file$.trigger('click');
		});
		$('#frmUploadMain #upl-btn-clear').on('click', function(pEvent){
			file$.val(null);
			setState();
		});
		$('#frmUploadMain #txtFile').on('change', function(pEvent){
			setState();
		});
		$('#frmUploadMain #upl-btn-refresh').on('click', function(pEvent){
			var lBtn$ = $(this);
			lBtn$.prop('disabled',true);
			$.ajax({
				url:'upload/data/'+fileId,
			    type: 'GET',
			    success: function(pObj){
			    	$('#frmUploadMain .box-body').html(tplUploadStatus(pObj));
			    },
			    error: errorHandler,
			    complete: function() {
			    	lBtn$.prop('disabled',false);
			    }
			});
		});
		
		$('#frmUploadMain #upl-btn-upload').on('click', function(pEvent){
			if (file$[0].files.length==1) {
				var lData = new FormData();
				lData.append("filecontent",file$[0].files[0]);
<%
Enumeration<String> lParamNames = request.getParameterNames();
while (lParamNames.hasMoreElements()) {
    String lParamName = StringEscapeUtils.escapeHtml(lParamNames.nextElement());
    String lParamValue = StringEscapeUtils.escapeHtml(request.getParameter(lParamName));
%>				lData.append("<%=lParamName%>", "<%=lParamValue%>");
<%
}
%>
				var lBtn$ = $(this);
				lBtn$.prop('disabled',true);
				$.ajax({
					url:'upload/data/<%=lUrl%>',
					data: lData,
				    cache: false,
				    contentType: false,
				    processData: false,
				    type: 'POST',
				    success: function(pObj){
				    	fileId=pObj.id;
				    	$('#frmUploadMain .box-body').html(tplUploadStatus(pObj));
						$('#frmUploadMain #btn-grp-ref').removeClass('hidden');
						$('#frmUploadMain #btn-grp-upl').addClass('hidden');
						$('#frmUploadMain #btn-grp-sel').addClass('hidden');
				    },
				    error: errorHandler,
				    complete: function() {
				    	lBtn$.prop('disabled',false);
				    }
				});
			}
		});
		setState();
	});
	function setState() {
		var lFileName = "";
		if (file$[0].files.length > 0) {
			lFileName = file$[0].files[0].name;
			$('#frmUploadMain #btn-grp-sel').addClass('hidden');
			$('#frmUploadMain #btn-grp-upl').removeClass('hidden');
		} else {
			$('#frmUploadMain #btn-grp-sel').removeClass('hidden');
			$('#frmUploadMain #btn-grp-upl').addClass('hidden');
		}
		$('#frmUploadMain #sect-filename').html(htmlEscape(lFileName));
	}
	function downRtnFile() {
		downloadFile('upload/return/' + fileId, $('#upl-btn-dnrt'))
	}
	</script>
