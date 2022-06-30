<%@page import="org.apache.commons.io.comparator.LastModifiedFileComparator"%>
<%@page import="com.xlx.treds.AppConstants"%>
<%@page import="java.sql.Date"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="com.xlx.common.utilities.FormatHelper"%>
<%@page import="sun.misc.FpUtils"%>
<%@page import="java.nio.file.attribute.FileTime"%>
<%@page import="java.nio.file.attribute.BasicFileAttributeView"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.nio.file.attribute.BasicFileAttributes"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="com.sun.xml.internal.bind.v2.runtime.output.Encoded"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Emails</title>
</head>
<body>
<%!
class Writer2Stream extends OutputStream {

	Writer out;

	Writer2Stream(Writer w) {
		super();
		out = w;
	}

	public void write(int i) throws IOException {
		out.write(i);
	}

	public void write(byte[] b) throws IOException {
		for (int i = 0; i < b.length; i++) {
			int n = b[i];
			//Convert byte to ubyte
			n = ((n >>> 4) & 0xF) * 16 + (n & 0xF);
			out.write(n);
		}
	}

	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = off; i < off + len; i++) {
			int n = b[i];
			n = ((n >>> 4) & 0xF) * 16 + (n & 0xF);
			out.write(n);
		}
	}
} //End of class Writer2Stream
%>
<%
if (request.getParameter("downfile") != null) {
	String filePath = request.getParameter("downfile");
	File f = new File(filePath);
	response.setContentType("application/octet-stream");
	response.setHeader("Content-Disposition", "attachment;filename=\"" + f.getName()
			+ "\"");
	response.setContentLength((int) f.length());
	BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(f));
	byte buffer[] = new byte[8 * 1024];
	out.clearBuffer();
	OutputStream out_s = new Writer2Stream(out);
	int b;
	while ((b = fileInput.read(buffer)) != -1){
		out_s.write(buffer, 0, b);
	}
	out_s.flush();
	fileInput.close();
}	
%>
<%
try{
File dirName = new File("/home/opc/apache-tomcat/Email/");

if (dirName.exists()&& dirName.isDirectory())
{
//out.print("path: " + dirName.getAbsolutePath() + "<br>");
File[] allFiles = dirName.listFiles();
%>
<table border='1' cellpadding="10">
<%
LastModifiedFileComparator comparator = new LastModifiedFileComparator();
File[] lSortedFiles = comparator.sort(allFiles);
for (int i=0; i < lSortedFiles.length; i++)
{
	Path p = Paths.get(lSortedFiles[i].getAbsolutePath());
    BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
    FileTime fileTime=view.creationTime();
    String lDate = FormatHelper.getDisplay(AppConstants.DATETIME_FORMAT, new Date(fileTime.to(TimeUnit.MILLISECONDS)));
%>
	<tr>
		<td><%=allFiles[i].getName()%></td>
		<td><%=lDate%></td>
		<td><input type="button" value="Download" onClick="download('/home/opc/apache-tomcat/Email/<%=allFiles[i].getName()%>')" ></td>
	</tr>
<%
}
%>
</table>
<%
}
}
catch (Exception ex){
out.println("Exception Occured");
}
%>
</body>
<script type="text/javascript">
function download(file)
{
	var lUrl = encodeURIComponent(file);
	window.location = 'Emails.jsp?downfile='+lUrl;
}
</script>
</html>