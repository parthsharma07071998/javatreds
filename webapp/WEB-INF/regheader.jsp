 <script id="menulist-template" type="x-handlebars-template">
{{#each items}} {{! Each item is an "li" }}
	{{#if items}} {{! Within the context of the current item }}
	<li class="treeview">
	    <a href="#">
	        <i class="fa {{attr.icon}}"></i>
	        <span>{{label}}</span>
	        <i class="fa fa-angle-left pull-right"></i>
	    </a>
	    <ul class="treeview-menu">
	        {{> menulist-template}} {{! Recursively render the partial }}
	    </ul>
	</li>
	{{else}}
	<li>
	    <a href="{{#if action}}{{action}}{{/if}}">
	        <i class="fa {{attr.icon}}"></i> <span>{{label}}</span>
	    </a>
	</li>
	{{/if}}
{{/each}}
</script>
<%
String lTitle = request.getParameter("title");
String lDesc = request.getParameter("desc");
if (lTitle == null) lTitle = "TREDS";
if (lDesc == null) lDesc = "";
%>
<script id="side-menu-template" type="x-handlebars-template">
    {{> menulist-template}}
</script>
        <!-- header logo: style can be found in header.less -->
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

        <header class="header">
            <a href="login" class="logo">
                <img src="../images/logo.png" class="icon" border=0 title="TREDS"/>
            </a>
            <!-- Header Navbar: style can be found in header.less -->
            <nav class="navbar navbar-static-top" role="navigation">
                <!-- Sidebar toggle button-->
                <a href="#" class="navbar-btn sidebar-toggle state-login" data-toggle="offcanvas" role="button">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <div class="navbar-right">
                    <ul class="nav navbar-nav">
                    	<li class="user user-menu state-login">
                    		<a href="#"><i class="fa fa-clock-o"></i><span id="spnDate"></span> <span id="spnTime"></span></a>
                    	</li>
                        <li class="dropdown notifications-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            	<i class="fa fa-bell"></i>
                                <i class="caret"></i>
                                <span class="label label-warning"></span>
                            </a>
                            <ul class="dropdown-menu">
                            </ul>
                        </li>
                        <!-- User Account: style can be found in dropdown.less -->
                        <li class="user user-menu state-logout">
                        	<a href="login"><i class="fa fa-sign-in"></i> Sign In</a>
                        </li>
                        <li class="dropdown user user-menu state-login">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="glyphicon glyphicon-user"></i>
                                <span><span  id="spnUser" class="hidden-xs"></span> <i class="caret"></i></span>
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
                                        <a href="javascript:logout('login')" class="btn btn-default btn-flat btn-block"><span class="fa fa-sign-out"></span> Sign out</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>
        </header>
        <div class="wrapper row-offcanvas row-offcanvas-left">
            <!-- Left side column. contains the logo and sidebar -->
            <aside class="left-side sidebar-offcanvas state-login">                
                <!-- sidebar: style can be found in sidebar.less -->
                <section class="sidebar">
                    <ul class="sidebar-menu" id="ul-sidebar-menu">
                        
                    </ul>
                </section>
                <!-- /.sidebar -->
            </aside>
            <!-- Right side column. Contains the navbar and content of the page -->
            <aside class="right-side state-logout">                
			<div class="content-header">
		       <h1>
		           <%=lTitle %>
		           <small><%=lDesc %></small>
		       </h1>
		    </div>
<script>
regMenu = {"id":0, "items":[
	{"id":1, "action":"company", "attr":{"icon":"fa-dashboard"}, "label":"Entity Details"}, 
		{"id":2, "action":"companycontact", "attr":{"icon":"fa-dashboard"}, "label":"Management"}, 
		{"id":3, "action":"companylocation", "attr":{"icon":"fa-dashboard"}, "label":"Location/Branches"}, 
		{"id":4, "action":"companybankdetail", "attr":{"icon":"fa-dashboard"}, "label":"Banking Details"}, 
		{"id":5, "action":"companykycdocument", "attr":{"icon":"fa-dashboard"}, "label":"Enclosures/Uploads"}, 
		{"id":6, "action":"reghome", "attr":{"icon":"fa-dashboard"}, "label":"Status Tracker"} 
	]}; 
</script>
