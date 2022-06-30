<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.treds.AppConstants.CompanyApprovalStatus"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lEntityId = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("entityId"))); 
if(lEntityId==null)lEntityId="";
String lIsProv = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(request.getParameter("isProv"))); 
boolean lIsProvisional = false;
if(lIsProv!=null&&lIsProv=="true"){
	lIsProvisional = true;
};
%>
<html>
    <head>
        <title>TREDS | Create User ID</title>
        <%@include file="includes1.jsp" %>
        <script>
        	var publicPage = true;
        </script>
    </head>
    <body class="page-body">
    <jsp:include page="regheader1.jsp">
    	<jsp:param name="title" value="Registration Portal" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contHome">
		<h2>Welcome to Registration Portal for TREDS</h2>
		<div class="text-center state-logout">
			<button type="button" class="btn btn-primary btn-lg" id="btnLogin" style="width:160px">Login</button>
			<h3 style='display:none'>OR</h3>
			<button type="button" class="btn btn-success btn-lg" id="btnRegister" style="display:none;width:160px">Create User Id</button>
		</div>
		<div class="row  state-login">
			<div class="col-lg-6">
				<div class="panel Bar_block panel-default progress_panel">
					<div class="panel-heading no-bottom-border">
						<h3 class="panel-title">Application Progress</h3>
					</div>
					<div class="panel-body">
						<div>
							<h2><span class="fa fa-square-o" id="S0"></span> Entity Details</h2>
						</div>
						<div>
							<h2><span class="fa fa-square-o" id="S1"></span> Management Details</h2>
						</div>
						<div>
							<h2><span class="fa fa-square-o" id="S2"></span> Locations / Branches</h2>
						</div>
						<div>
							<h2><span class="fa fa-square-o" id="S3"></span> Banking Details</h2>
						</div>
						<div>
							<h2><span class="fa fa-square-o" id="S4"></span> Enclosures / Uploads</h2>
						</div>
						<div>
							<h2 class="text-upper">Total Progress</h2>
								<div class="progress progress-striped active "  style="height: 15px">
								<div class="progress-bar orange-color"><span class="sr-only"></span><div id="divProgress"></div></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-lg-6">
				<div id="divWorkFlow">
				</div>
			</div>
			<div class="col-lg-12">
				<div class="panel Bar_block panel-default progress_panel" id="divMessage">
               	Welcome to RXIL TReDS. 
               	<br>
               	<ul>
               	<li>Please fill-in application form and furnish along with Supporting Officially Valid documents such as Passport, PAN Card etc. </li>
               	<li>The Names, Addresses etc. of entity and individuals as applicable shall be consistent with such officially valid documents.</li>
               	<li>Please also distinctly fill in address of registered office and works location of entities along with District Name, PIN Code etc for easy identification. </li>
               	<li>Please fill in Valid Mobile numbers, e-mail addresses at applicable columns.</li>
               	</ul>
				</div>
			</div>
			
		</div>
		<div class="row">
			<div class="hidden" id="divDraft">
				<div class="btn-groupX pull-right" >
					<button type="button" class="btn btn-info btn-success btn-lg" id=btnValidate><h4 class="text-upper"><span class="fa fa-check-square-o"></span> Validate Application</h4></button>
					<button type="button" class="btn btn-info btn-primary btn-lg" id=btnSubmit><h4 class="text-upper"><span class="fa fa-save"></span> Submit Application</h4></button>
					<button type="button" class="btn btn-info btn-success btn-lg" id=btnRegistration><h4 class="text-upper">Continue with Registration Process <span class="fa fa-forward"></span> </h4></button>
				</div>
				<h3>
				</h3>
			</div>
		</div>
	</div>
	
   	<%@include file="footer1.jsp" %>
   	<script id="tplMessage" type="text/x-handlebars-template">
	{{#each this}}
		<b>{{title}}</b>
		<ul>
		{{#each messages}}
		<li>{{.}}</li>
		{{/each}}
		</ul>
	{{/each}}
	</script>
   	<script id="tplWorkFlow" type="text/x-handlebars-template">
		<div class="panel Bar_block panel-default progress_panel">
			<div class="panel-heading no-bottom-border">
			<h3 class="panel-title">Current Status of your application : <span class="text-primary">{{statusDesc}}</span></h3>
			{{#ifCond status '==' "S" }}<div class="pull-right" id="divDownload"><a href="javascript:downloadPdf({{id}});"  id="btnPrint"><i class="glyphicon glyphicon-download-alt"></i> Download as PDF</a></div>{{/ifCond}}
			</div>
			<div class="panel-body">
		<h4>Work flow</h4>
		<table class="table table-striped table-bordered" style="width:100%"><tbody>
		<tr>
			<th style="width:25%">Status</th>
			<th style="width:50%">Remarks</th>
			<th style="width:25%">Time stamp</th>
		</tr>
		{{#each workFlow}}
		<tr>
			<td>{{approvalStatus}}</td>
			<td>{{reason}}</td>
			<td>{{recordCreateTime}}</td>
		</tr>
		{{/each}}
		</tbody></table>
			</div>
		</div>
		<div>
		</div>
	</script>
	<script type="text/javascript">
	var tplMessage, tplWorkFlow;
	$(document).ready(function() {
		tplMessage = Handlebars.compile($('#tplMessage').html());
		tplWorkFlow = Handlebars.compile($('#tplWorkFlow').html());
		$('#btnRegister').on('click', function() {
			location.href="reguser";
		});
		$('#btnLogin').on('click', function() {
			//showRemote('reglogin','modal-sm')
			location.href="reglogin";
		});
		$('#btnValidate').on('click', function() {
			validateAndSubmit(false, true);
		});
		$('#btnSubmit').on('click', function() {
			validateAndSubmit(true, true);
		});
		$('#btnRegistration').on('click', function() {
			if("<%=lEntityId%>"===""){
				location.href="company?tab=tabGeneral&isProv=<%=lIsProvisional%>";				
			}else{
				location.href="company?tab=tabGeneral"+"&entityId=<%=lEntityId%>&isProv=<%=lIsProvisional%>";
			}
		});
		validateAndSubmit(false, false);
		if("<%=lEntityId%>"!=""){
			 //$('.dropdown-toggle').prop('disabled', true);
			 //$('#btnSubmit').prop('disabled',true);
			 //$('#btnSubmit').addClass('hidden');
		}
	});
	function validateAndSubmit(pSubmit, pVerbose) {
		if (loginData && loginData.login) {
			$.ajax( {
		        url: "company/" + (pSubmit?"submit":"validate")+"?entityId=<%=lEntityId%>&isProv=<%=lIsProvisional%>",
		        type: "GET",
		        success: function( pObj, pStatus, pXhr) {
		        	if (pObj.status === "<%=CompanyApprovalStatus.Draft.getCode()%>" || pObj.status === "<%=CompanyApprovalStatus.Returned.getCode()%>" || (pObj.status === "<%=CompanyApprovalStatus.Submitted.getCode()%>" && loginData.domain === "<%=AppConstants.DOMAIN_REGENTITY%>" ) ) {
		        		$('#divDraft').removeClass('hidden');
		        		$('#btnSubmit').prop('disabled', (pObj.score < pObj.maxScore));
		        		$('#tAndC').prop('disabled', (pObj.score < pObj.maxScore));
		        		var lProgress = pObj.score * 100 / pObj.maxScore;
		        		$('#divProgress').html(lProgress+'%');
		        		  $('.progress-bar').css('width', lProgress+'%').attr('aria-valuenow', lProgress);
		        		if (pVerbose) {
			        		if (pObj.messages && pObj.messages.length > 0) {
			        			alert(tplMessage(pObj.messages));
			        		} else {
			        			if (pSubmit){
			        				alert("Application Submitted.");
			        			}
			        			else if (lProgress==100){
			        				alert("Application is valid. You may submit it.");
			        			}
			        		}
		        		}
		        	} else {
		        		$('#divDraft').addClass('hidden');
		        		$('#divDownload').addClass('hidden');
		        		if (pObj.status === "A"){
		        			var lProgress = 100;
		        			$('#divProgress').html(lProgress+'%');		        			
			        		  $('.progress-bar').css('width', lProgress+'%').attr('aria-valuenow', lProgress);
		        		}
		        	}
		        	if(pObj.scores != null){
		        		for(var lPtr=0; lPtr < pObj.scores.length; lPtr++){
		        			if(pObj.scores[lPtr]){
			        			$('#S'+lPtr).prop('class',"fa fa-check-square-o");
		        			}
		        		}
		        	}
	        		$('#divWorkFlow').html(tplWorkFlow(pObj));
		        },
		        error: errorHandler
			});
		}
	}
	function downloadPdf(pId) {
		window.open('printpdf/'+pId+'?loginKey='+loginData.loginKey);
	}
	</script>
   	
    </body>
</html>