<!DOCTYPE html>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.entity.bean.AppEntityPreferenceBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html>
    <head>
        <title>TREDS | Entity Preferences</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
        
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Entity Preferences" />
    	<jsp:param name="desc" value="" />
    </jsp:include>


	<div class="content" id="contAppEntityPreference">		
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Preferences</h1>
			</div>
		</div>
		<div class="xform" style="display:none" id="frmSearch">
		</div>
		<div id="frmMain">
		<div class="xform box">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="idcp" class="label">Date Calculation Priority:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="idcp"><option value="">Select Date Calculation Priority</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="upds" class="label">Usance period days settings:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="upds"><option value="">Select Usance period days settings</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
						<div class="col-sm-2"><section><label for="ick" class="label">Supports Instrument Creation Keys:</label></section></div>
						<div class="col-sm-4">
						<section>
							<label class="checkbox"><input type=checkbox id="ick" onclick=""><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
				</div>
				<div class="row">
						<div class="col-sm-2"><section><label for="ccmod" class="label">Allow Counter Checker Modification:</label></section></div>
						<div class="col-sm-4">
						<section>
							<label class="checkbox"><input type=checkbox id="ccmod" onclick=""><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
				</div>
				<div class="row">
						<div class="col-sm-2"><section><label for="skipmonetago" class="label">Skip Monetago:</label></section></div>
						<div class="col-sm-4">
						<section>
							<label class="checkbox"><input type=checkbox id="skipmonetago" onclick=""><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="billType" class="label">Billing Type:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="billType"><option value="">Select Billing Type</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
						<div class="col-sm-2"><section><label for="elb" class="label">Enable Locationwise Billing:</label></section></div>
						<div class="col-sm-4">
						<section>
							<label class="checkbox"><input type=checkbox id="elb" onclick=""><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
				</div>
				<div class="row">
						<div class="col-sm-2" hidden><section><label for="cav" class="label">Counter API verification:</label></section></div>
						<div class="col-sm-4" hidden>
						<section>
							<label class="checkbox"><input type=checkbox id="cav" onclick=""><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
				</div>
				<div class="row">
						<div class="col-sm-2"><section><label for="ecf" class="label">Enable Custom Fields:</label></section></div>
						<div class="col-sm-4">
						<section>
							<label class="checkbox"><input type=checkbox id="ecf" onclick=""><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
				</div>
				<div class="row">
						<div class="col-sm-2"><section><label for="acs" class="label">Allow Charge Splitting:</label></section></div>
						<div class="col-sm-4">
						<section>
							<label class="checkbox"><input type=checkbox id="acs" onclick=""><i></i>
							<b class="tooltip tooltip-top-left"></b>
							</label>
						</section>
						<section class="view"></section>
						</div>
				</div>
				<div class="row">
						<div class="col-sm-2"><section><label for="isInt" class="label">Is Integration:</label></section></div>
						<div class="col-sm-4">
							<section>
								<label class="checkbox"><input type=checkbox id="isInt" onclick=""><i></i>
								<b class="tooltip tooltip-top-left"></b>
								</label>
							</section>
							<section class="view"></section>
						</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="elp" class="label">Enable Location Preferences:</label></section></div>
					<div class="col-sm-4">
						<section class="inline-group">
						<label class="checkbox"><input type="checkbox" id="elp"><i></i><span></span>
						<b class="tooltip tooltip-top-right"></b></label></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="box-footer">
					<div>
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
		</div>
	</div>
   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
	var crudAppEntityPreference$,crudAppEntityPreference;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppEntityPreferenceBean.class).getJsonConfig()%>;
		var lConfig = {
				resource: "entpref",
				modify: <%=lModify%>,
				postModifyHandler:function(pObj) {
					if (pObj.fieldGroup) {
						var lMainForm = crudAppEntityPreference.options.mainForm;
						$.each(lFormConfig.fieldGroups[pObj.fieldGroup], function(pIdx,pVal) {
							var lFld$ = $("#contAppEntityPreference #" + pVal);
							lFld$.parents("div.row").show();
							lMainForm.enableDisableField(pVal, true, false);
						});
					}
					return true;
				},
				preSaveHandler:function(pObj) {
					pObj.code=<%=lModify%>[0];
					return true;
				},
				postSaveHandler:function(pObj) {
            		alert("Preferences saved successfully.", "Information", function() {
            			window.history.go(-1);
            			return false;
            		});

				},
				closeHandler:function() {
					window.history.go(-1);
				}
		};
		$("#contAppEntityPreference div.row").hide();
		var lPlatform = false;
		$.each(loginData.entityTypeList,function(pIdx,pVal){
			if (pVal == '<%=AppConstants.EntityType.Platform.getCode()%>') {
				lPlatform = true;
				return false;
			}
		});
		lFormConfig.fieldGroups.update=lPlatform?lFormConfig.fieldGroups.<%=AppEntityPreferenceBean.FIELDGROUP_UPDATEBYPLATFORM%>:lFormConfig.fieldGroups.<%=AppEntityPreferenceBean.FIELDGROUP_UPDATEBYENTITY%>
		lConfig = $.extend(lConfig, lFormConfig);
		crudAppEntityPreference$ = $('#contAppEntityPreference').xcrudwrapper(lConfig);
		crudAppEntityPreference = crudAppEntityPreference$.data('xcrudwrapper');
	});
	</script>
   	
    </body>
</html>