 <script id="menulist-template" type="x-handlebars-template">
{{#each items}} {{! Each item is an "li" }}
	{{#if items}} {{! Within the context of the current item }}
	<li>
		<a href="javascript:;"><i class="sd_icon {{attr.icon}}"></i><span class="title">{{label}}</span></a>
		<ul>
			{{> menulist-template}} {{! Recursively render the partial }}							
		</ul>
	</li>
	{{else}}
	<li>
		<a href="{{#if action}}{{action}}{{/if}}"><i class="sd_icon {{attr.icon}}"></i><span class="title">{{label}}</span></a>
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
        <li><a href="#" onMouseOver="javascript:showAlert('{{type}}','{{message}}')" onMouseOut="javascript:hideAlert()" title="{{time}}"><i class="fa fa-bell {{type}}"></i> {{message}}</a></li>
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
String lEntityId = request.getParameter("entityId"); 
String lIsProv = request.getParameter("isProv"); 
if(lIsProv==null){
	lIsProv = "false";
};
String lQueryParam = "";
if(lEntityId!=null&&lEntityId!="") {
	lQueryParam = "?entityId="+lEntityId+"&isProv="+lIsProv;
}else{
	lQueryParam = "?isProv="+lIsProv;
}
%>


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
							<a class="logo-expanded" href="reghome<%=lQueryParam%>">
								<img alt="" src="../assets/images/logo%402x.png" height="43">
							</a>
							<a class="logo-collapsed" href="reghome<%=lQueryParam%>">
								<img alt="" src="../assets/images/logo-collapsed%402x.png" width="43">
							</a>
						</div>
						<div class="mobile_view dropdown user-profile visible-xs">
							<a class="dropdown-toggle" data-toggle="dropdown" href="#">
								<img alt="user-image" class="img-circle img-inline userpic-32" src="../assets/images/user-4.png">
								<span><i class="fa-angle-down"></i></span>
							</a>
							<ul class="dropdown-menu user-profile-menu list-unstyled">
								<li>
									<a href="#edit-profile"><i class="fa-edit"></i> New Post</a>
								</li>
								<li>
									<a href="#settings"><i class="fa-wrench"></i> Settings</a>
								</li>
								<li>
									<a href="#profile"><i class="fa-user"></i> Profile</a>
								</li>
								<li>
									<a href="#help"><i class="fa-info"></i> Help</a>
								</li>
								<li class="last">
									<a href="extra/lockscreen/index.html"><i class="fa-lock"></i> Logout</a>
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
					<ul class="welcome_side user-info-menu left-links list-inline list-unstyled  state-login">
						<li class="hidden-xs">
							<a data-toggle="sidebar" href="#"><i class="fa-bars"></i></a>
						</li>
						<li class="hidden-xs">
							<h2>Welcome <strong class="spnCompany"></strong></h2>
						</li>
					</ul>
					<ul class="user-info-menu right-links list-inline list-unstyled">
                    	<li class="user user-menu state-login">
							<a href="#"><i class="fa fa-clock-o hidden-xs"></i> <span id="spnDate"></span> <span id="spnTime"></span></a>
						</li>
                        <li class="user user-menu state-logout">
                        	<a href="reglogin"><i class="fa fa-sign-in"></i> Sign In</a>
                        </li>
                        <li class="dropdown user user-menu state-login">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            	<i class="fa fa-bell"></i>
                                <i class="caret"></i>
                                <span class="badge badge-red"></span>
                            </a>
                            <ul class="dropdown-menu">
                            	 <li class="user-header bg-light-blue">
                                    <p>
                                        <span id="spnUserFull"></span> 
                                    </p>
                                </li>
                                <li class="user-body">
                                    <div class="pull-left">
                                        <a href="javascript:showRemote('security','modal-sm', false, 'Security Settings')" class="btn btn-link btn-flat"><span class="fa fa-user-secret"></span> Security</a>
                                    </div>
                                    <div class="pull-right">
                                        <a href="javascript:showRemote('user?lgn=true&CHGPASS=Y','modal-sm')" class="btn btn-link btn-flat"><span class="fa fa-key"></span> Change Password</a>
                                    </div>
                                </li>
                                <li class="user-footer">
                                        <a href="javascript:logout('reglogin')" class="btn btn-default btn-flat btn-block"><span class="fa fa-sign-out"></span> Sign out</a>
                                </li>
                            </ul>
                        </li>
                        <li class="dropdown user user-menu state-login">
							<a class="dropdown-toggle" data-toggle="dropdown" href="#">
								<span><span id="spnUser"></span> <i class="fa-angle-down"></i></span>
							</a>
							<ul class="dropdown-menu user-profile-menu list-unstyled">
								<li>
									<a href="javascript:showRemote('user?lgn=true&CHGPASS=Y','modal-sm')"><i class="fa-key"></i> Change Password</a>
								</li>
								<li>
									<a href="javascript:showRemote('security','modal-sm', false, 'Security Settings')"><i class="fa-wrench"></i> Security Settings</a>
								</li>
								<li>
									<a  href="user?view=true&home=reghome"><i class="fa-user"></i> User Profile</a>
								</li>
								<li>
									<a href="javascript:alert('Coming Soon')"><i class="fa-info"></i> Help</a>
								</li>
								<li class="last">
									<a href="javascript:logout('reglogin')"><i class="fa-lock"></i> Logout</a>
								</li>
							</ul>
						</li>
					</ul>
				</nav>
			<header>
			<div class="wrapper row-offcanvas row-offcanvas-left">
            <!-- Right side column. Contains the navbar and content of the page -->
            <aside class="right-side state-logout">                
			<div class="content-header">
		       <h1>
		           <%=lTitle %>
		           <small><%=lDesc %></small>
		       </h1>
		    </div>
            </aside>
          

<script>
regMenu = {"id":0, "items":[
	{"id":6, "action":"reghome"+"<%=lQueryParam%>", "attr":{"icon":"sd_icon status_icon"}, "label":"Status Tracker"} ,
	{"id":1, items:[{"id":101, "action":"javascript:showPageTab('company','tabGeneral')","attr":{"icon":""},"label":"General Information"},
	                {"id":102, "action":"javascript:showPageTab('company','tabAddress')","attr":{"icon":""},"label":"Contact Details"},
	                {"id":104, "action":"javascript:showPageTab('company','tabMSME')","attr":{"icon":""},"label":"MSME Status"},
	                {"id":105, "action":"javascript:showPageTab('company','tabOthers')","attr":{"icon":""},"label":"Others"},
					{"id":106, items:[{"id":1061, "action":"companyshareindividual<%=lQueryParam%>"+"&tab=tabIndiv","attr":{"icon":""},"label":"Individual"},
	                	{"id":1062, "action":"companyshareentity<%=lQueryParam%>","attr":{"icon":""},"label":"Entity"}],
	"attr":{"icon":""}, "label":"Share Holder"}
			],
	"attr":{"icon":"sd_icon entity_icon"}, "label":"Entity Details"}, 
	{"id":2, items:[{"id":201, "action":"javascript:showPageTab('companycontact','tabPromoters')","attr":{"icon":""},"label":"Promoter/Director/Partner"},
	                {"id":202, "action":"javascript:showPageTab('companycontact','tabAuthPer')","attr":{"icon":""},"label":"Authorised Officials"},
	                {"id":203, "action":"javascript:showPageTab('companycontact','tabAdmin')","attr":{"icon":""},"label":"Administrators"},
	                {"id":204, "action":"javascript:showPageTab('companycontact','tabUltimateBeneficiary')","attr":{"icon":""},"label":"Ultimate Beneficiary"},
	                {"id":205, "action":"javascript:showPageTab('companycontact','tabAll')","attr":{"icon":""},"label":"All"}],
	"attr":{"icon":"sd_icon management_icon"}, "label":"Management"}, 
	{"id":4, "action":"companybankdetail"+"<%=lQueryParam%>", "attr":{"icon":"sd_icon banking_icon"}, "label":"Banking Details"},
	{"id":3, "action":"companylocation"+"<%=lQueryParam%>", "attr":{"icon":"sd_icon location_icon"}, "label":"Location/Branches"}, 
	{"id":5, "action":"companykycdocument"+"<%=lQueryParam%>", "attr":{"icon":"sd_icon enclosures_icon"}, "label":"Enclosures/Uploads"}
	]}; 
	
	function showPageTab(pPage,pTab) {
		var lPos=location.href.lastIndexOf('/');
		var lPage = location.href.substring(lPos+1);
		var lPos=lPage.indexOf('?');
		if (lPos > 0)
			lPage = lPage.substring(0,lPos);
		if(lPage==pPage)
			showTab(pTab);
		else{
			if("<%=lQueryParam%>"==""){
				location.href=pPage+'?tab='+pTab;
			}else {
				location.href=pPage+'<%=lQueryParam%>'+'&tab='+pTab;
			}
		}
	}
</script>
		
