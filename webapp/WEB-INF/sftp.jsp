<!DOCTYPE html>
<html>
	<head>
		<title>TREDS | SFTP</title>
		<%@include file="includes1.jsp" %>
		<link href="../css/datatables.css" rel="stylesheet"/>
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<style type="text/css">
			h3,h4{
				border-bottom: 1px solid #A9A9A9;
				padding-bottom: 10px;
			}
			input{
				padding-top: 10px;
				padding-bottom: 10px;
				padding-left: 200px;
			}
		</style>
	</head>
	<body class="page-body">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="" />
		<jsp:param name="desc" value="" />
	</jsp:include>

	<div class="container" id="contSFTP">	
		<div class="page-title">
			<div class="title-env">
				<h1 class="title">SFTP</h1>
			</div>
		</div>
		<div>
			<p align="right">
  				<input type="button" class="btn btn-primary" onClick="window.location.reload()" value="Refresh" />
			</p>
			<div id='divData'> </div>
		</div>
	</div>	
	
	<%@ include file="footer1.jsp" %>
	<script id="tplData" type="text/x-handlebars-template">
<ul class="nav nav-tabs">
{{#each this}}
    <li {{#if @first}}class="active"{{/if}}><a data-toggle="tab" href="#id{{name}}">{{name}}</a></li>
{{/each}}
</ul>
<div class="tab-content">
{{#each this}}
	<div id="id{{name}}" {{#if @first}}class="tab-pane fade in active"{{else}}class="tab-pane fade in"{{/if}}>
	<div class="row">
	<div class="col-sm-6">
      	{{#if in}}
  				<h3><b>Downloads</b></h3>
				{{#each in}}
					<ul>
					<li>
					<span>
						<h4> 
							<b>{{key}}:</b> 
							{{#if upload}}<input type="file" id="upload{{key}}" class="display:inline" name="upload{{key}}" onchange="tryUpload('upload{{key}}','{{../../../name}}^{{key}}')">{{/if}}
						</h4>
					</span>
 					<ul>
						{{#each list}}	
   						 <li><a  href="javascript:downloadSftpFile('{{../../../name}}^in^{{../key}}^{{this}}');">{{this}}</a></li>
						{{/each}}	
  					</ul>
					</li>
					</ul>
				{{/each}}
		{{/if}}	
	</div>		
	<div class="col-sm-6">
		{{#if out}}
  				<h3><b>Uploads</b></h3>
				{{#each out}}
					<ul>
					<li>
					<span>
						<h4> 
							<b>{{key}}:</b> 
							{{#if upload}}<input type="file" id="upload{{key}}" class="display:inline" name="upload{{key}}" onchange="tryUpload('upload{{key}}','{{../../../name}}^{{key}}')">{{/if}}
						</h4>
					</span>
 					<ul>
						{{#each list}}	
   						 <li><a  href="javascript:downloadSftpFile('{{../../../name}}^out^{{../key}}^{{this}}');">{{this}}</a></li>
						{{/each}}	
  					</ul>
					</li>
					</ul>
				{{/each}}
		{{/if}}	
	</div>
</div>
    </div>
{{/each}}
</div>

	</script>
	
	<script src="../js/datatables.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
		var tplData;
		$(document).ready(function() {
			 tplData=Handlebars.compile($('#tplData').html());
			 getConfig();
		});
				
		function getConfig(){
			$.ajax({
		        url: 'sftp/config',
		        type: 'GET',
		        async: false,
		        success: function( pObj, pStatus, pXhr) {
		        	if (pObj!=null){
		        		$('#divData').html(tplData(pObj));
		        	}
		        },
		        error: function( pObj, pStatus, pXhr) {
		        }
		    });
		}
		function downloadSftpFile(pFile){
			downloadFile('sftp/download',null,JSON.stringify({"file":pFile}) );
			location.reload();
		}
		
		function tryUpload(pThis,pPath){    
			var file = null;
		    if($('#'+pThis).prop('files').length > 0)
		    {
		        file =$('#'+pThis).prop('files')[0];
		    }
		    var lData = new FormData();
			lData.append("filecontent",file);
			$.ajax({
				url:'sftp/upload/'+pPath,
				data: lData,
			    cache: false,
			    contentType: false,
			    processData: false,
			    timeout: 300000,
			    type: 'POST',
			    async: false,
			    success: function(data){
			    	location.reload();
			    },
			    error: errorHandler,
			    complete: function() {
			    }
			});
		}
		
		
	</script>


</body>
</html>