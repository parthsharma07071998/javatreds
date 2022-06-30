<%@page import="com.xlx.treds.TredsHelper"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>Test API Tool</title>
<link rel="stylesheet" type="text/css" href="css/global.css" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
function init()
{

}
function login()
{
	$('#spnloginkey').html("");
	send("POST","/rest/login",
			JSON.stringify({
		"domain": $('#txtmem').val(),
		"login": $('#txtlogin').val(),
		"password": $('#txtpass').val()
	}));
}
function chgPass()
{
	send("POST","/login",
			JSON.stringify({
		"domain": $('#txtmem').val(),
		"login": $('#txtlogin').val(),
		"password": $('#txtpass').val(),
		"newPassword1": $('#txtnewpass').val()
	}));
}
function logout()
{
	send("GET", "/rest/logout", null);
}

function postApi()
{
	send("POST",$('#txtPostApi').val(), $('#txtTransactionJson').val());
}
function putApi()
{
	send("PUT",$('#txtPutApi').val(), $('#txtTransactionJson').val());
}
function getApi()
{
	send("GET", $('#txtGetApi').val(), null);
}


function send(pMethod,pUrl,pData)
{
	var lUrl = $('#txturl').val() + pUrl;
	$('#txtTransactionJson').html(pData);
	var lToken = $('#spnloginkey').html();
	$('#txtresponse').html(">> Url: " + pMethod + " : " + lUrl + " : " + lToken + "\n>> Data: " + pData);
	$.ajax({
		dataType: "json",
		contentType: pMethod=="POST"?"application/json":(pMethod=="PUT"?"application/json":null),
		timeout: 30000,
		cache: false,
		headers: {
			"loginKey": lToken
		},
		url: lUrl,
		type: pMethod,
		data: pData,
		success: function( pObj, pStatus, pXhr) {
			$('#txtresponse').append("\n<< Success: " + JSON.stringify(pObj,null,2));
			if ("/rest/login" == pUrl)
				$('#spnloginkey').html(pObj.loginKey);
			if ("/rest/logout" == pUrl)
				$('#spnloginkey').html("");
		},
		error: function( pXhr, pStatus, pError ) {
			$('#txtresponse').append("\n<< Error: " + pError);
			var lErrObj = JSON.parse(pXhr.responseText);
			$('#txtresponse').append("\n<< Error 1st msg : " + lErrObj.messages[0]);
			$('#txtresponse').append("\n<< Error JSON : " +  pXhr.responseText);
		}
	});

}
</script>
</head>
<body>
	<table border=0 cellspacing=0 cellpadding=0 width=90% >
	<thead>
	<tr><th class=tblHead> <h1>Web-API</h1>&nbsp;&nbsp;
	</th></tr>
	</thead>
	<tbody>
	<tr>
	<td valign=top>
	<table id="formtbl" name="formtbl" class="tbody" border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr><td class=fldlbl>BASE URL </td><td class=fldval> <input type=text id=txturl size=70 value="http://103.58.9.242:90/treds"/> </td></tr>

	<tr><td class=fldlbl>Login</td>
	<td class=fldval>
		<table border=0 cellspacing=0 cellpadding=0>
		<tr><td>Member</td><td>Login</td><td>Password</td><td></td><td>LoginToken</td></tr>
		<tr>
			<td><input type=text id=txtmem size=15 /></td>
			<td><input type=text id=txtlogin size=15/></td>
			<td> <input type=text id=txtpass size=15/></td>
			<td><input type=button onclick=javascript:login() value=Login></td>
			<td><span id=spnloginkey></span></td>
		</tr>
		</table>	
	</td>
	</tr>

	<tr><td class=fldlbl>Logout</td>
	<td class=fldval><input type=button onclick=javascript:logout() value="Logout" >
	</td></tr>
	
	<tr><td class=fldlbl>Change Password</td>
	<td class=fldval>
		<table border=0 cellspacing=0 cellpadding=0>
		<tr><td>New Password</td><td></td></tr>
		<tr>
			<td><input type=text id=txtnewpass size=15/></td>
			<td><input type=button onclick=javascript:chgPass() value="Change Password"></td>
		</tr>
		</table>	
	</td>
	</tr>

	<tr>
	</tr>
	
	<tr>
	<td class=fldlbl>GET API</td>
	<td class=fldval><input type=text id=txtGetApi size=25/><input type=button onclick=javascript:getApi() value="GET" ></td>
	</tr>
	<tr>
	<td class=fldlbl>POST API</td>
	<td class=fldval><input type=text id=txtPostApi size=25 value="/rest/v1/"/><input type=button onclick=javascript:postApi() value="POST" > </td>
	</tr>
	<td class=fldlbl>PUT API</td>
	<td class=fldval><input type=text id=txtPutApi size=25 value="/rest/v1/"/><input type=button onclick=javascript:putApi() value="PUT" > </td>
	</tr>
	
	<tr><td colspan=2>
	<table width=100%  border=0 cellspacing=0 cellpadding=0>
	<tr><td class=fldlbl>Request</td><td class=fldlbl>Response</td></tr>
	<tr><td>
		<textarea id=txtTransactionJson rows=15 style="width:100%"></textarea>
	</td><td>
		<textarea id=txtresponse rows=15 style="width:100%"></textarea>
	</td></tr>
	</table>
	</td></tr>

</table></td></tr>
</tbody>
</table>
</body>
</html>
