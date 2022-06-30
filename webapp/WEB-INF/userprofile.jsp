<!DOCTYPE html>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.xlx.treds.entity.bean.CompanyDetailBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | User Profile</title>
<%@include file="includes1.jsp"%>

<%
boolean lRegHome = false;
String lHeaderFile =  "header1.jsp";
String lHome = request.getParameter("home");
if("reghome".equals(lHome)){
	lRegHome = true;
	lHeaderFile =  "regheader1.jsp";
}
%>
       <style>
        .xform section {
        	margin:0px;
        }
        .xform .label, .xform section.view {
        	padding-top: 0px;
        	padding-bottom: 0px;
        	height:20px;
        }
        </style>
	</head>
	
	
    <body class="page-body">

	<jsp:include page= "<%=lHeaderFile%>">
    	<jsp:param name="title" value="User Profile" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content">
    	<!-- frmMain -->
		<div id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">User Profile</h1>
				</div>
			</div>
		    <div class="xform view box">
		    	<fieldset>
		    	<div>
					<div id="divData">
					</div>
				</div>
		    		<div class="modal-footer" style="display:none">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" data-dismiss="modal"><span class="fa fa-close"></span> Close</button>
								</div>
							</div>
						</div>
		    		</div>
		    	</fieldset>
	    	</div>
	    </div>
    	<!-- frmMain -->
	</div>
	
  	<%@include file="footer1.jsp" %>

	
	
   	<script id="tplUserView" type="text/x-handlebars-template">
		<div class="content" id="conUserProfile">

			<div class="tab-pane panel panel-default my-panel">
            	<div class="form-header">USER DETAILS</div>
				<form role="form" class="form-horizontal">

				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Company Code</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{domain}}</section></div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Login ID</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{loginId}}</section></div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Name</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{salutation}} {{firstName}} {{middleName}} {{lastName}}</section></div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Mobile</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{mobile}}</section></div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Email</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{email}}</section></div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Telephone</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{telephone}}</section></div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Roles</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{#each rmIdList}}{{.}}, {{/each}}</section></div>
				</div>
				</div>

{{#if checkers}}
				<div class="col-md-6">
				<div class="row">
					<div class="col-sm-4"><section><label class="control-label">Checkers</label></section></div><div class="col-sm-8"><section class="control-label label-small">{{#each checkers}}{{.}} {{/each}}</section></div>
				</div>
				</div>
{{/if}}

				<div class="col-md-12">
					<div class="panel-body bg_white">
						<div class="pull-right">
							<button type="button" onClick="javascript:showRemote('user?lgn=true&CHGPASS=Y','modal-md')" class="btn btn-info btn-lg"><span class="fa fa-key"></span> Change Password</button>
							<button type="button" onClick="javascript:showRemote('security','modal-md')" class="btn btn-info btn-lg"><span class="fa fa-user-secret"></span> Change 2FA</button>
						</div>
					</div>
				</div>

		</form>
		</div>
		</div>
	</script>

	<script type="text/javascript">
	var tplUserView;
	$(document).ready(function() {
		Handlebars.registerHelper('imageName', function(options) {
			var lImg = options.fn(this);
			var lPos=lImg.indexOf('.');
			return lPos>0?lImg.substring(lPos+1):lImg;
		});
		tplUserView = Handlebars.compile($('#tplUserView').html());
		$.ajax({
			url: 'user/profile',
			type: 'GET',
			success: function( pObj, pStatus, pXhr) {
 				$('#divData').html(tplUserView(pObj));
			},
			error: errorHandler,
		});
	});
	</script>
    </body>
</html>