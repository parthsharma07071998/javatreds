<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.master.bean.BankBranchDetailBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html lang="en">
  <head>
	<title>TREDS | IFSC Codes</title>
    <%@include file="includes1.jsp" %>
     <link href="../css/datatables.css" rel="stylesheet"/>
	</head>
<body class="skin-blue">
    <jsp:include page="header1.jsp">
    	<jsp:param name="title" value="IFSC Codes" />
    	<jsp:param name="desc" value="" />
    </jsp:include>
	<div class="content" id="contBankBranchDetail">	
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">IFSC Codes</h1>
			</div>
		</div>			
		<!-- frmSearch -->
		<div id="frmSearch">
			<div class="xform tab-pane panel panel-default no-margin collapse in" id=divFilter>
				<fieldset class="form-horizontal">
					<div class="row">
						<div class="col-sm-2"><section><label for="bankCode" class="label">Bank:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="bankCode"><option value="">Select Bank</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
						<div class="col-sm-2"><section><label for="ifsc" class="label">IFSC:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="ifsc" placeholder="IFSC">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="micrcode" class="label">MICR Code:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="micrcode" placeholder="MICR Code">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="branchname" class="label">Branch Name:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="branchname" placeholder="Branch Name">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="city" class="label">City:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="city" placeholder="City">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="contact" class="label">District:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="district" placeholder="District">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2"><section><label for="state" class="label">State:</label></section></div>
						<div class="col-sm-2">
							<section class="input">
							<input type="text" id="state" placeholder="State">
							<b class="tooltip tooltip-top-right"></b></section>
						</div>
						<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
						<div class="col-sm-2">
							<section class="select">
							<select id="status"><option value="">Select Status</option></select>
							<b class="tooltip tooltip-top-right"></b><i></i></section>
						</div>
					</div>
					<div class="panel-body bg_white">
						<div class="row">
							<div class="col-sm-12">
								<div class="btn-groupX pull-right">
									<button type="button" class="btn btn-enter btn-info btn-lg" id=btnSearch><span class="fa fa-search"></span> Search</button>
									<button type="button" class="btn btn-info-inverse btn-lg" id=btnFilterClr> Clear Filter</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="filter-block clearfix">
				<div class="">
					<a class="left_links" href="javascript:;" data-toggle="collapse" data-target="#divFilter">Filter</a>
					<a href="javascript:;" class="right_links" id=btnView><span class="fa fa-eye"></span> View</a>
					<a href="javascript:;" class="right_links" id=btnRemove><span class="glyphicon glyphicon-remove"></span> Remove</a>
					<a href="javascript:;" class="right_links" id=btnModify><span class="glyphicon glyphicon-pencil"></span> Modify</a>
					<a href="javascript:;" class="right_links" id=btnNew><span class="glyphicon glyphicon-plus"></span> New</a>
					<a></a>
				</div>
			</div>
			<div class="tab-pane panel panel-default">
				<fieldset>
					<div class="row">
						<div class="col-sm-12">
							<table class="table table-bordered" id="tblData">
								<thead><tr>
									<th data-width="100px" data-name="ifsc">IFSC</th>
									<th data-width="150px" data-name="bankCode">Bank</th>
									<th data-width="100px" data-name="micrcode">MICR Code</th>
									<th data-width="200px" data-name="branchname">Branch Name</th>
									<th data-width="300px" data-name="address">Address</th>
									<th data-width="100px" data-name="contact">Contact</th>
									<th data-width="100px" data-name="city">City</th>
									<th data-width="100px" data-name="district">District</th>
									<th data-width="100px" data-name="state">State</th>
									<th data-width="100px" data-name="status">Status</th>
								</tr></thead>
							</table>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<!-- frmSearch -->
		<div class="xform box" style="display:none" id="frmMain">
			<fieldset>
				<div class="row">
					<div class="col-sm-2"><section><label for="ifsc" class="label">IFSC:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="ifsc" placeholder="IFSC">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="micrcode" class="label">MICR Code:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="micrcode" placeholder="MICR Code">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="branchname" class="label">Branch Name:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="branchname" placeholder="Branch Name">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="address" class="label">Address:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="address" placeholder="Address">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="contact" class="label">Contact:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="contact" placeholder="Contact">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="city" class="label">City:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="city" placeholder="City">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="district" class="label">District:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="district" placeholder="District">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="state" class="label">State:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="state" placeholder="State">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="status" class="label">Status:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="status"><option value="">Select Status</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
	    		<div class="box-footer">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-groupX pull-right">
								<button type="button" class="btn btn-info btn-lg btn-enter" id=btnSave><span class="fa fa-save"></span> Save</button>
						<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
								<button type="button" class="btn btn-info-inverse btn-lg btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
							</div>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
	</div>

   	<%@include file="footer1.jsp" %>
   	<script src="../js/datatables.js"></script>
	<script type="text/javascript">
		var crudBankBranchDetail$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(BankBranchDetailBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "bbdtl",
					autoRefresh: false,
<%
if (lNewBool) {
%>					new: true,
<%
} else if (lModify != null) {
%>					modify: <%=lModify%>,
<%
}
%>			};
			lConfig = $.extend(lConfig, lFormConfig);
			crudBankBranchDetail$ = $('#contBankBranchDetail').xcrudwrapper(lConfig);
		});
		
	</script>

</body>
</html>