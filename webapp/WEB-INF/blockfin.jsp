<!DOCTYPE html>
<%@page import="com.xlx.treds.entity.bean.AppEntityBean"%>
<%@page import="com.xlx.treds.master.bean.HolidayMasterBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Blocked Financiers</title>
        <%@include file="includes1.jsp" %>
        <link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
        <link href="../css/datatables.css" rel="stylesheet"/>
    </head>
    <body class="page-body">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Blocked Financier List" />
    	<jsp:param name="desc" value="" />
    </jsp:include>

	<div class="content" id="contBlockedFinanciers">
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">Blocked Financiers</h1>
			</div>
		</div>
		<!-- frmMain -->
		<div class="xform" id="frmMain">
	    	<div class="xform box">
				<fieldset class="box-body">
					<div class="row">
						<div class="col-sm-12">
							<section class="">
							<select id="blockedFinancierList" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>
		    		<div class="box-footer">
						<div class="row">
								<div class="col-sm-12">
									<div class="btn-groupX pull-right">
										<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
										<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClosePage><span class="fa fa-close"></span> Close</button>
									</div>
								</div>
							</div>
		    		</div>
		    	</fieldset>
			</div>
   		</div>
   	</div>
   	<!-- frmMain -->

   	<%@include file="footer1.jsp" %>
	<script src="../js/jquery.bootstrap-duallistbox.js"></script>


<script type="text/javascript">
	var crudBlockedFin$ = null;
	var lFormConfig = null;
	var mainForm=null;
	var crudBlockedFin$ = null,crudBlockedFin;
	var tabIdx, tabData;
	$(document).ready(function() {
		var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class, null, AppEntityBean.FIELDGROUP_UPDATEBLOCKEDFINANCIERS).getJsonConfig()%>;
		$.each(lFormConfig.fields, function(pIndex,pValue){
			if (pValue.name=="blockedFinancierList") {
				pValue.dataSetType="RESOURCE";
				pValue.dataSetValues="appentity/financiers";
			}
		});
		var lConfig = {
				resource: "blockfin",
				modify:['dummy'],
				postSaveHandler:function(pObj) {
					alert("Financier list updated successfully",null,function() {
						location.href='home';
					})
					return false;
				}
			};
		lConfig = $.extend(lConfig, lFormConfig);		
		crudBlockedFin$ = $('#contBlockedFinanciers').xcrudwrapper(lConfig);
		crudBlockedFin=crudBlockedFin$.data('xcrudwrapper');
		mainForm=crudBlockedFin.options.mainForm;
		$('#btnClosePage').on('click',function() {
			location.href='home';
		})
		
	});

</script>
   	
    </body>
</html>