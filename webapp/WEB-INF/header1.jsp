 <script id="menulist-template" type="x-handlebars-template">
{{#each items}} {{! Each item is an "li" }}
	{{#if items}} {{! Within the context of the current item }}
	<li>
		<a href="javascript:;"><i class="sd_icon {{attr.icon}}"></i><span class="title">{{label}}</span></a>
		<ul style="max-height: 340px;overflow-y: auto;">
			{{> menulist-template}} {{! Recursively render the partial }}							
		</ul>
	</li>
	{{else}}
	<li class="{{attr.class}}">
		<a href="{{#if action}}{{action}}{{/if}}"><span class="title">{{label}}</span></a>
	</li>
	{{/if}}
{{/each}}
</script>
<script id="side-menu-template" type="x-handlebars-template">
    {{> menulist-template}}
</script>
<script id="tplNotifications" type="text/x-handlebars-template">
<li class="header">{{#if total}}You have {{total}} notifications{{else}}No Notifications{{/if}}</li>
<li>
    <ul class="menu">
{{#list}}
        <li {{#if new}} class="bg-{{type}}"{{/if}}><a href="#" onMouseOver="javascript:showAlert('{{type}}','{{message}}')" onMouseOut="javascript:hideAlert()" title="{{time}}"><i class="fa fa-bell {{type}}"></i> {{message}}</a></li>
{{/list}}
    </ul>
</li>
{{#if total}}<li class="footer">&nbsp;</li>{{/if}}
</script>
<%
String lTitle = request.getParameter("title");
String lDesc = request.getParameter("desc");
if (lTitle == null) lTitle = "TREDS";
if (lDesc == null) lDesc = "";
String[] lCrumbs = request.getParameterValues("crumb");
%>
<script>
function openPrefrences(){
	location.href='entpref?code='+loginData.domain;
}
</script>
	<div class="alert alert-bottom text-center">
	  <a href="#" class="close">&times;</a>
	  <span></span>
	</div>
        
    <div id="mdl-remote" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
            	<div class="modal-header"><span>&nbsp;</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
            	<div class="modal-body"></div>
            </div>
        </div>
    </div>
		<div class="page-container">
			<div class="sidebar-menu toggle-others">
				<div class="sidebar-menu-inner">
					<header class="logo-env">
						<!-- logo -->
						<div class="mobile-menu-toggle visible-xs">
							<a data-toggle="mobile-menu" href="#"><i class="fa-bars"></i></a>
						</div>
						<div class="logo">
							<a class="logo-expanded" href="home">
								<img alt="" src="../assets/images/logo%402x.png" height="43">
							</a>
							<a class="logo-collapsed" href="home">
								<img alt="" src="../assets/images/logo-collapsed%402x.png" width="43">
							</a>
						</div>
						<div class="mobile_view dropdown user-profile visible-xs">
							<a class="dropdown-toggle" data-toggle="dropdown" href="#">
								<span><i class="fa-angle-down"></i></span>
							</a>
							<ul class="dropdown-menu user-profile-menu list-unstyled">
								<li style="display:none">
									<a href="javascript:showRemote('user?lgn=true&CHGPASS=Y','modal-sm')"><i class="fa-key"></i> Change Password</a>
								</li>
								<li style="display:none">
									<a href="javascript:showRemote('security','modal-sm', false, 'Security Settings')"><i class="fa-wrench"></i> Security Settings</a>
								</li>
								<li>
									<a href="javascript:showRemote('user?view=true')"><i class="fa-user"></i> Profile</a>
								</li>
								<li>
									<a href="javascript:alert('Coming Soon')"><i class="fa-info"></i> Help</a>
								</li>
								<li class="last">
									<a href="javascript:logout()"><i class="fa-lock"></i> Logout</a>
								</li>
							</ul>
						</div>
					</header>
					<ul class="main-menu" id="ul-sidebar-menu">
					</ul>
				</div>
			</div>

			<div class="main-content">
				
				<nav class="navbar user-info-navbar fixed" role="navigation">
					<ul class="welcome_side user-info-menu left-links list-inline list-unstyled">
						<li class="hidden-xs">
							<a data-toggle="sidebar" href="#"><i class="fa-bars"></i></a>
						</li>
						<li class="hidden-xs">
							<h2>Welcome <strong class="spnCompany"></strong></h2>
						</li>
					</ul>
					<ul class="user-info-menu right-links list-inline list-unstyled">
						<li class="">
							<a href="#"><i class="fa fa-clock-o hidden-xs"></i> <span id="spnDate"></span> <span id="spnTime"></span></a>
						</li>
                        <li class="dropdown notifications-menu" style="display:none">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            	<i class="fa fa-bell"></i>
                                <i class="caret"></i>
                                <span class="badge badge-red"></span>
                            </a>
                            <ul class="dropdown-menu">
                            </ul>
                        </li>
						<li class="dropdown user-profile">
						<li class="dropdown user-profile">
							<a class="dropdown-toggle" data-toggle="dropdown" href="#">
								<span><span id="spnUser"></span> <i class="fa-angle-down"></i></span>
							</a>
							<ul class="dropdown-menu user-profile-menu list-unstyled">
								<li style="display:none">
									<a href="javascript:showRemote('user?lgn=true&CHGPASS=Y','modal-sm')"><i class="fa-key"></i> Change Password</a>
								</li>
								<li style="display:none">
									<a href="javascript:showRemote('security','modal-sm', false, 'Security Settings')"><i class="fa-wrench"></i> Security Settings</a>
								</li>
								<li  style="display:none">
									<a href="javascript:openPrefrences()"><i class="fa-cog"></i> Preferences</a>
								</li>
								<li>
									<a href="user?view=true"><i class="fa-user"></i> Profile</a>
								</li>
								<li>
									<a href="javascript:alert('Coming Soon')"><i class="fa-info"></i> Help</a>
								</li>
								<li>
									<a href="loginsess"><i class="fa-info"></i> Login History</a>
								</li>
								<li class="last">
									<a href="javascript:logout()"><i class="fa-lock"></i> Logout</a>
								</li>
							</ul>
						</li>
					</ul>
				</nav>
			<div class="wrapper row-offcanvas row-offcanvas-left">
            <!-- Right side column. Contains the navbar and content of the page -->
            <aside class="right-side">                
			<div class="content-header">
		       <div class="pull-right" id="divPageSumm" style="padding:0px;"></div>
		    </div>
            </aside>

		
