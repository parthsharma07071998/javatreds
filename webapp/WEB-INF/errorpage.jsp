<%@ page isErrorPage="true" import="java.io.*"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Error</title>
    </head>

	<h1>Something went wrong</h1>
	<b>Error Code</b> <%=pageContext.getErrorData().getStatusCode() %>
	<br>Please contact application administrator.
    </body>
</html>

