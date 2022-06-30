<!DOCTYPE html>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.treds.user.bean.AppUserBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<html>
    <head>
        <title>TREDS | Login</title>
        <%@include file="includes1.jsp" %>
        <style>
@font-face {
  font-family: 'Montserrat-Bold';
  src: url('../fonts/Montserrat-Bold_0.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}        
@font-face {
  font-family: 'Montserrat-Regular';
  src: url('../fonts/Montserrat-Regular.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}        
        body {
        	background:url('../images/login/loginbg.jpg');
        	background-size:cover;
    		background-repeat:   no-repeat;
    		background-position: center center;
    		overflow:hidden;      
        }
        .divText {
        	margin-top: 160px;
			color: white;
			font-weight: bold;
        }
        h1 {
	        margin: 3px;
			font-size: 50px;
			font-weight: 900;
			font-family: Montserrat-Bold;
        }
        h2 {
        	margin:3px;
        	font-size:40px;
        	font-family: Montserrat-Regular;
        }
        #colDemo {
        	width:146px;
        	height:48px;
        	background:url('../images/login/demo.png') no-repeat;
        	background-size:100% auto;
        }
        #colContact {
        	width:146px;
        	height:48px;
        	background:url('../images/login/contact.png') no-repeat;
        	background-size:100% auto;
        }
        #colDemo a {
        	color:inherit;
        }
        h3 {
        	font-size: 16px;
			margin-left: 55px;
			padding-top: 5px;
			padding-bottom:0px;
			margin-bottom:0px;
        }
        h4 {
        	font-size: 14px;
			margin-left: 55px;
			margin-top:0px;
			padding-top: 0px;
        }
        .scroll-body .modal-body {
        	height:400px;
        	overflow-y:auto;
        }
        .form-box {
		  width: 360px;
		  margin: 90px auto 0 auto;
		}
        
        </style>
        <script>
        	var publicPage = true;
        </script>
		<script src="../js/jquery-3.4.1.min.js"></script>
	    <script src="../js/jquery-migrate-3.0.1.min.js"></script>
		<script id="script-resource-1" src="../js/bootstrap.min.js"></script> 
        <script src="../js/handlebars-v3.0.0.js"></script> 
        <script src="../js/handlebars.helpers.js"></script> 
        <script src="../js/jquery.xform.js?v1"></script> 
        <script src="../js/app.js?v1"></script> 

    </head>
    <body class="skin-blue">
   	<div class="row">
   		<div class="col-sm-6">
   	<div class="form-box pull-right" id="myDivForLive">
	        <%@include file="loginpop.jsp" %>
   	</div>
   		</div>
   		<div class="col-sm-6 hidden-xs">
   			<div class="divText">
	   			<h1>"Join Us"</h1>
	   			<h2>for Uncommon</h2>
	   			<h2>Results.</h2>
   			</div>
<!--    			<h1 style="color:red;">Site under maintenance </h1> -->
<!-- 			<h2 style="color:red;">Inconvenience caused is deeply regretted</h2> -->
   			<div class="row">
   				<!-- <div class="col-sm-5 col-md-4 col-lg-3"><div id="colDemo"><h3>Demo</h3><h4>How to Use</h4></div></div> -->
   				<div class="col-sm-5 col-md-4 col-lg-3"><div id="colContact">
   					<h3>Contact Us <a href="javascript:;" ondblClick="javascript:toggleMyDiv();">.</a></h3>
   				<h5 style="margin-left:51px">1800-266-8733</h5></div></div>
   			</div>
   		</div>
   	</div>
   	 <div id="mdl-remote" class="modal fade" tabindex=-1 data-backdrop='false'>
	    <div class="modal-dialog">
	        <div class="modal-content">
	        	<div class="modal-header"><span>&nbsp;</span><button type="button" class="btn btn-sm pull-right" data-dismiss="modal"><i class="fa fa-close"></i></button></div>
	        	<div class="modal-body"></div>
	        </div>
	    </div>
	 </div>
	</div>
	 
    </body>
</html>

<script type="text/javascript">
function toggleMyDiv(){
			var varXLive = document.getElementById("myDivForLive");
			if (varXLive.style.display === 'none'){
				varXLive.style.display = 'block';
			}else{
				varXLive.style.display = 'none';
			}
		}
</script>	