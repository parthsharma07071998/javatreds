<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.test.bean.TestUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
Boolean lNew = (Boolean)request.getAttribute(CommonAppConstants.PARAM_NEW);
boolean lNewBool = (lNew != null) && lNew.booleanValue();
String lModify = (String)request.getAttribute(CommonAppConstants.PARAM_MODIFY);
%>
<html lang="en">
  <head>
	<title>TREDS | Test Users</title>
	<%@include file="includes.jsp" %>

	<link href="../css/global.css" rel="stylesheet">

	<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
	<link href="../css/bootstrap-duallistbox.css" rel="stylesheet"/>
	<link href="../css/bootstrap-multiselect.css" rel="stylesheet"/>
	<link href="../css/select2.css" rel="stylesheet"/>
	<link href="../css/datatables.css" rel="stylesheet"/>

</head>
<body class="skin-blue">
	<%@include file="header.jsp" %>
<div class="content-header"> 
   <h1>
       Test User1
       <small>User login details</small>
   </h1>
   <ol class="breadcrumb">
       <li><a href="#"><i class="fa fa-dashboard"></i> Workflow Tracker</a></li>
       <li class="active">Blank page</li>
   </ol>
</div>
<div class="content">
	<div id="contTestUser">
		<div class="xform box box-danger" style="display:none" id="frmSearch">
			<fieldset class="box-body">
				<div class="row">
					<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="name" placeholder="Name">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
					<div class="col-sm-2"><section><label for="email" class="label">Email:</label></section></div>
					<div class="col-sm-2">
						<section class="input">
						<input type="text" id="email" placeholder="Email">
						<b class="tooltip tooltip-top-right"></b></section>
					</div>
				</div>
			</fieldset>
			<fieldset class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-primary" id=btnSearch><span class="fa fa-search"></span> Search</button>
							<button type="button" class="btn btn-default" id=btnNew><span class="fa fa-plus"></span> New</button>
							<button type="button" class="btn btn-default" id=btnModify><span class="fa fa-pencil"></span> Modify</button>
							<button type="button" class="btn btn-default" id=btnView><span class="fa fa-eye"></span> View</button>
							<button type="button" class="btn btn-default" id=btnRemove><span class="fa fa-minus"></span> Remove</button>
							<button type="button" class="btn btn-default" id=btnFilter>Hide Filter</button>
						</div>
					</div>
				</div>
			</fieldset>
			<fieldset>
				<div class="row">
					<div class="col-sm-12">

						<table class="table table-striped table-bordered " id="tblData">
							<thead><tr>
								<th data-visible="false" data-name="id">Id</th>
								<th data-width="100px" data-name="name">Name</th>
								<th data-width="100px" data-name="email">Email</th>
								<th data-width="100px" data-name="married">Are you married?</th>
								<th data-width="100px" data-name="continent">Continent</th>
								<th data-width="100px" data-name="country">Country</th>
								<th data-width="100px" data-name="dob">Date of Birth</th>
								<th data-width="100px" data-name="wakeupTime">Wake up Time</th>
								<th data-width="100px" data-name="lastSeenDateTime">Last Seen At</th>
								<th data-width="100px" data-name="age">Age</th>
								<th data-width="100px" data-name="salary">Salary</th>
							</tr></thead>
						</table>
					</div>
				</div>
			</fieldset>
		</div>
		
		<div class="xform box box-danger" id="frmMain">
			<fieldset class="box-body">
				<div class="row">
					<div class="col-sm-2"><section><label for="name" class="label">Name:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="name" placeholder="Name">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="email" class="label">Email:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="email" placeholder="Email">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="married" class="label">Are you married?:</label></section></div>
					<div class="col-sm-4">
						<section>
						<label class="radio"><input type=radio id="married"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="continent" class="label">Continent:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="continent"><option value="">Select Continent</option></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="favFood" class="label">Favourite Foods:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="favFood" multiple="multiple" data-role="multiselect"></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="hobbies" class="label">Hobbies:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="hobbies" data-role="multiselect"></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="country" class="label">Country:</label></section></div>
					<div class="col-sm-4">
						<section class="select select2-bg">
						<select id="country" data-role="select2" multiple="multiple" data-maximum-selection-length=1 data-placeholder="Select Country" style="width:100%"></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="languages" class="label">Languages known:</label></section></div>
					<div class="col-sm-4">
						<section class="select">
						<select id="languages" data-role="select2" multiple="multiple" data-maximum-selection-length=-1 data-placeholder="Select Languages" style="width:100%"></select>
						<b class="tooltip tooltip-top-right"></b><i></i></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="sports" class="label">Sports:</label></section></div>
					<div class="col-sm-10">
						<section class="">
						<select id="sports" multiple="multiple" data-role="bootstrapDualListbox" data-move-on-select="false"></select>
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="colors" class="label">Favourite Colors:</label></section></div>
					<div class="col-sm-4">
						<section>
						<label class="checkbox"><input type=checkbox id="colors"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="moreColors" class="label">More Favourite Colors:</label></section></div>
					<div class="col-sm-4">
						<section>
						<label class="toggle"><input type=checkbox id="moreColors"><i></i><span></span>
						<b class="tooltip tooltip-top-left"></b></label>
						</section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="dob" class="label">Date of Birth:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="dob" placeholder="Date of Birth" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="wakeupTime" class="label">Wake up Time:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="wakeupTime" placeholder="Wake up Time" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="lastSeenDateTime" class="label">Last Seen At:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<i class="icon-append fa fa-clock-o"></i>
						<input type="text" id="lastSeenDateTime" placeholder="Last Seen At" data-role="datetimepicker">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2"><section><label for="age" class="label">Age:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="age" placeholder="Age">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
					<div class="col-sm-2"><section><label for="salary" class="label">Salary:</label></section></div>
					<div class="col-sm-4">
						<section class="input">
						<input type="text" id="salary" placeholder="Salary">
						<b class="tooltip tooltip-top-right"></b></section>
						<section class="view"></section>
					</div>
				</div>
			</fieldset>
			<fieldset class="box-footer">
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-groupX pull-right">
							<button type="button" class="btn btn-primary" id=btnSave><span class="fa fa-save"></span> Save</button>
							<button type="button" class="btn btn-primary" id=btnEdit><span class="fa fa-pencil"></span> Edit</button>
							<button type="button" class="btn btn-close" id=btnClose><span class="fa fa-close"></span> Close</button>
						</div>
					</div>
				</div>
			</fieldset>
		</div>
	</div>
</div>
<%@include file="footer1.jsp" %>

	<script src="../js/bootstrap-datetimepicker.js"></script> 
	<script src="../js/jquery.bootstrap-duallistbox.js"></script>
	<script src="../js/bootstrap-multiselect.js"></script>
	<script src="../js/select2.js"></script>
	<script src="../js/bootstrap-slider.js"></script>
	<script src="../js/datatables.js"></script>

	<script type="text/javascript">
		var crudTestUser$ = null;
		$(document).ready(function() {
			var lFormConfig = <%=BeanMetaFactory.getInstance().getBeanMeta(TestUserBean.class).getJsonConfig()%>;
			var lConfig = {
					resource: "testuser",
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
			crudTestUser$ = $('#contTestUser').xcrudwrapper(lConfig);
		});
		
	</script>

</body>
</html>