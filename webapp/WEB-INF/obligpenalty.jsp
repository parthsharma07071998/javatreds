<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.auction.bean.ObligationExtensionPenaltyBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html lang="en">
	<head>
		<title>TREDS - Obligation Extension Penalties</title>
		<%@include file="includes1.jsp" %>
        <link href="../css/datatables.css" rel="stylesheet"/>
	</head>
	<body class="page-body">
	
	<jsp:include page="header1.jsp">
    	<jsp:param name="title" value="Obligation Extension Penalty" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
	
	<div class="content" id="contObligationExtensionPenalty">
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Obligation Extension Penalty</h1>
				</div>
			</div>
			<div class="xform tab-pane panel panel-default no-margin" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="purchaser"><option value="">Select Buyer</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="allowExtension" class="label">Allow Extension:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="allowExtension"><option value="">Select Allow Extension</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch><span class="fa fa-search"></span> Search</button>
									<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr>Clear Filter</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links secure" data-seckey="obligpenalty-save" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links secure" data-seckey="obligpenalty-save" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered " id="tblData">
								<thead><tr>
									<th data-visible="false" data-name="financier"></th>
									<th data-visible="false" data-name="purchaser"></th>
									<th data-width="150px" data-name="purchaserName">Buyer</th>
									<th data-visible="false" data-name="purchaser"></th>
									<th data-width="100px" data-name="maxExtension">Maximum Extension (days)</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		<!-- frmMain -->
		<div style="display:none" id="frmMain">
			<div class="page-title">
				<div class="title-env">
					<h1 class="title">Obligation Extension Penalty</h1>
				</div>
			</div>
    		<div class="xform box">
				<fieldset>
					<div class="row">
						<div class="col-sm-2"><section><label for="purchaser" class="label">Buyer:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="purchaser"><option value="">Select Buyer</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="allowExtension" class="label">Allow Extension:</label></section></div>
						<div class="col-sm-4">
							<section class="select">
							<select id="allowExtension"><option value="">Select Allow Extension</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
							<section class="view"></section>
						</div>
						<div class="col-sm-2"><section><label for="maxExtension" class="label">Maximum Extension (days):</label></section></div>
						<div class="col-sm-4">
							<section class="input">
							<input type="text" id="maxExtension" placeholder="Maximum Extension (days)">
							<b class="tooltip tooltip-top-right"></b></section>
							<section class="view"></section>
						</div>
					</div>


					<fieldset id="penaltyList">
						<div style="display:none" id="penaltyList-frmSearch">
							<div class="row">
								<div class="col-sm-12">
									<div class="btn-group nonView">
										<button type="button" class="btn btn-default" id="penaltyList-btnNew"><span class="fa fa-plus"></span> Add</button>
										<button type="button" class="btn btn-default" id="penaltyList-btnModify"><span class="fa fa-pencil"></span> Modify</button>
										<button type="button" class="btn btn-default" id="penaltyList-btnRemove"><span class="fa fa-minus"></span> Remove</button>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-12">
									
									<table class="table table-striped table-bordered table-condensed" id="penaltyList-tblData" width="300px" data-scroll-y="60px">
										<thead><tr>
											<th data-width="100px" data-name="uptoDays" data-data="uptoDays">Upto Days</th>
											<th data-width="100px" data-name="rate" data-data="rate">Penalty Rate %</th>
										</tr></thead>
									</table>
								</div>
							</div>
						</div>
						<div class="modal fade" tabindex=-1><div class="modal-dialog"><div class="modal-content"><div class="modal-body">
						<div id="penaltyList-frmMain" class="xform">
							<header>Penalty Rate</header>
								<fieldset>
									<div class="row">
										<div class="col-sm-4"><section><label for="penaltyList-uptoDays" class="label">Upto Days:</label></section></div>
										<div class="col-sm-8">
											<section class="input">
											<input type="text" id="penaltyList-uptoDays" placeholder="Upto Days">
											<b class="tooltip tooltip-top-right"></b></section>
											<section class="view"></section>
										</div>
									</div>
									<div class="row">
										<div class="col-sm-4"><section><label for="penaltyList-rate" class="label">Penalty Rate %:</label></section></div>
										<div class="col-sm-8">
											<section class="input">
											<input type="text" id="penaltyList-rate" placeholder="Penalty Rate %">
											<b class="tooltip tooltip-top-right"></b></section>
											<section class="view"></section>
										</div>
									</div>
								</fieldset>
							<footer>
								<div class="btn-group pull-right">
									<button type="button" class="btn btn-primary" id="penaltyList-btnSave"><span class="fa fa-save"></span> Ok</button>
									<button type="button" class="btn btn-close" id="penaltyList-btnClose"><span class="fa fa-close"></span> Cancel</button>
								</div>
							</footer>
						</div>
						</div></div></div></div>
					</fieldset>



					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSave><span class="fa fa-save"></span> Save</button>
									<button type="button" class="btn btn-close btn-info-inverse btn-lg" id=btnClose><span class="fa fa-close"></span> Close</button>
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
	<script src="../js/datatables.js"></script>
	
	<script type="text/javascript">
		var crudObligationExtensionPenalty$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(ObligationExtensionPenaltyBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "obligpenalty",
					autoRefresh: true,
					keyFields:["financier","purchaser"],
					postModifyHandler: function(pObj){
						mainForm.alterField(['maxExtension'], true, false);
						return true;
					},
					postNewHandler: function() {
						mainForm.alterField(['maxExtension'], true, false);
						return true;
					},
			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudObligationExtensionPenalty$ = $('#contObligationExtensionPenalty').xcrudwrapper(lConfig);
			crudObligationExtensionPenalty = crudObligationExtensionPenalty$.data('xcrudwrapper');
			mainForm = crudObligationExtensionPenalty.options.mainForm;
		});
		
	</script>	
	</body>
</html>
