<%@page import="com.xlx.treds.TredsHelper"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.xlx.common.utilities.DBHelper"%>
<%@page import="com.xlx.common.utilities.CommonUtilities"%>
<%@page
	import="java.util.*,
                java.net.*,
                java.text.*,
                java.util.zip.*,
                java.io.*"%>
<%@ include file="auth.jsp"%>

<%@ page import="java.sql.*,java.io.*,java.util.ArrayList,java.util.HashMap,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse,java.util.Iterator"%>
<%!private static final String REPORTJSP = CommonUtilities.getProperty(TredsHelper.FILE_NAME, "ReportURL") + "misreports" ; //"Report.jsp";
	private static final String PARAM_FOLDER = "f";
	private static final String PARAM_QUERY = "q";
	private static final String PARAM_SEPERATOR = "c";
	private static final String PARAM_PIPE = "p";
	private static final String PARAM_SEPERATOR_VAL_CSV = "1";
	private static final String PARAM_SEPERATOR_VAL_PIPE = "2";
	private static final String METACONFIG = ".meta.config";
	private static final String STAR = "*";
	private static final String SUFFIXUSERS = "_Users";
	private static final String SUFFIXDATABASE = "_Database";
	private static final String COMMA = ",";
	private static final String PIPE = "|";
	private static final String NEWLINE = "\r\n";
	public static final int DISPLAYLIMIT = 2000;

	private static final int COMPRESSION_LEVEL = 1;

	static void copyStreamsWithoutClose(InputStream in, OutputStream out, byte[] buffer) throws IOException {
		int b;
		while ((b = in.read(buffer)) != -1)
			out.write(buffer, 0, b);
	}
	
	/**
	 * Wrapperclass to wrap an OutputStream around a Writer
	 */
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


	public String readFile(String pFile) {
		String lStr = null;
		StringBuffer lTempStr = new StringBuffer("");
		BufferedReader lBufferedReader = null;
		FileReader lFileReader = null;
		try {
			lFileReader = new FileReader(pFile);
			lBufferedReader = new BufferedReader(lFileReader);
			while ((lStr = lBufferedReader.readLine()) != null) {
				lTempStr = lTempStr.append(lStr).append("\n");
			}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			try {
				if (lBufferedReader != null)
					lBufferedReader.close();
				if (lFileReader != null)
					lFileReader.close();
			} catch (IOException lIOException) {
			}
		}
		return lTempStr.toString();
	}

	public ArrayList<String[]> getData(String pSql, String pDatabase) {
		ArrayList<String[]> lData = new ArrayList<String[]>();
		ResultSet lResultSet = null;
		Statement lStatement = null;
		Connection lConnection = null;
		ResultSetMetaData lResultSetMetaData;
		try {
			System.out.println("SQL:" + pSql);
			System.out.println("Database:" + pDatabase);
			if (CommonUtilities.hasValue(pDatabase))
				lConnection = DBHelper.getInstance().getConnection(pDatabase);
			else
				lConnection = DBHelper.getInstance().getConnection();
			//lConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			lStatement = lConnection.createStatement();
			lResultSet = lStatement.executeQuery(pSql);
			lResultSetMetaData = lResultSet.getMetaData();
			int lColCnt = lResultSetMetaData.getColumnCount();
			String[] lRow;
			lRow = new String[lColCnt];
			for (int lPtr = 1; lPtr <= lColCnt; lPtr++)
				lRow[lPtr - 1] = lResultSetMetaData.getColumnName(lPtr);
			lData.add(lRow);
			while (lResultSet.next()) {
				lRow = new String[lColCnt];
				for (int lPtr = 1; lPtr <= lColCnt; lPtr++)
					lRow[lPtr - 1] = lResultSet.getString(lPtr);
				lData.add(lRow);
			}
		} catch (Exception lException) {
			lException.printStackTrace();
		} finally {
			try {
				if (lResultSet != null)
					lResultSet.close();
				if (lStatement != null)
					lStatement.close();
				if (lConnection != null)
					lConnection.close();
			} catch (Exception lException1) {
				lException1.printStackTrace();
			}
		}
		return lData;
	}
	public String parseSql(String pSql, HashMap<String, String> pParams, HttpServletRequest pRequest) {
		String[] lSqlParts = CommonUtilities.splitString(pSql, "~");
		StringBuffer lSql = new StringBuffer();
		boolean lAllParamsFound = true;
		for (int lPtr = 0; lPtr < lSqlParts.length; lPtr++) {
			if (lPtr % 2 == 1) {
				String lParamName = lSqlParts[lPtr].trim();
				String lParamValue = pRequest.getParameter(CommonUtilities.replace(lParamName, " ", "_"));
				pParams.put(lParamName, lParamValue);
				if (CommonUtilities.hasValue(lParamValue))
					lSql.append(lParamValue);
				else
					lAllParamsFound = false;
			} else {
				lSql.append(lSqlParts[lPtr]);
			}
		}
		if (lAllParamsFound)
			return lSql.toString().trim();
		else
			return null;
	}
	public HashMap<String, String> getConfig(String pPath) {
		System.out.println("GEtting config for " + pPath);
		HashMap<String, String> lConfigs = new HashMap<String, String>();
		String lContents = readFile(pPath + File.separator + METACONFIG);
		String[] lLines = CommonUtilities.splitString(lContents, "\n");
		for (int lPtr = 0; lPtr < lLines.length; lPtr++) {
			if (!CommonUtilities.hasValue(lLines[lPtr]))
				continue;
			int lPos = lLines[lPtr].indexOf("=");
			if (lPos >= 0) {
				String lConfigName = lLines[lPtr].substring(0, lPos).trim();
				String lConfigValue = lLines[lPtr].substring(lPos + 1).trim();
				System.out.println("\t" + lConfigName + ":" + lConfigValue);
				lConfigs.put(lConfigName, lConfigValue);
			}
		}
		return lConfigs;
	}
	public boolean checkAccess(String pUserList, String pUser) {
		boolean lAllowed = true;
		if (CommonUtilities.hasValue(pUserList)) {
			pUserList = pUserList.toUpperCase();
			lAllowed = pUserList.equals(pUser) || pUserList.startsWith(pUser + COMMA)
					|| pUserList.endsWith(COMMA + pUser) || (pUserList.indexOf(COMMA + pUser + COMMA) >= 0);
		}
		return lAllowed;
	}%>
<%
	String lUser = getUser(request.getHeader("Authorization"));
	if (lUser != null)
		lUser = lUser.toUpperCase();
	String lServletPath = request.getServletPath();
	lServletPath = lServletPath.substring(0, lServletPath.lastIndexOf('/'));

	String ROOTFOLDER = pageContext.getServletContext().getRealPath(lServletPath);
	String lFolder = request.getParameter(PARAM_FOLDER);
	String lReportFile = request.getParameter(PARAM_QUERY);
	String lSeperator = request.getParameter(PARAM_SEPERATOR);
	String lPipe = request.getParameter(PARAM_PIPE);
	HashMap<String, String> lQueryParams = null;
	String lParsedQuery = null;

	if (lFolder == null)
		lFolder = "";
	String[] lFolderList = CommonUtilities.splitString(lFolder, File.separator);
	// CHECK ACCESS
	String lPath = ROOTFOLDER + File.separator;
	HashMap<String, HashMap<String, String>> lConfigs = new HashMap<String, HashMap<String, String>>();
	String lDatabase = null;
	for (int lPtr = 0; lPtr < lFolderList.length; lPtr++) {
		String lFolderName = lFolderList[lPtr];
		if (!CommonUtilities.hasValue(lFolderName))
			continue;
		HashMap<String, String> lFolderConfig = getConfig(lPath);
		boolean lAllowed = checkAccess(lFolderConfig.get(lFolderName + SUFFIXUSERS), lUser);
		String lDatabaseConfig = lFolderConfig.get(lFolderName + SUFFIXDATABASE);
		if (lDatabaseConfig != null)
			lDatabase = lDatabaseConfig.trim();
		lPath += lFolderName + File.separator;
		if (!lAllowed) {
			throw new Exception("Access denied on folder " + lPath);
		}
	}
	HashMap<String, String> lFolderConfig = getConfig(lPath);
	//GET FOLDERS AND FILES
	ArrayList<String> lSubFolders = new ArrayList<String>();
	ArrayList<String> lFiles = new ArrayList<String>();
	File[] lFileList = null;
	lFileList = new File(ROOTFOLDER + lFolder).listFiles();
	int lCount = lFileList == null ? 0 : lFileList.length;
	for (int lFPtr = 0; lFPtr < lCount; lFPtr++) {
		String lName = lFileList[lFPtr].getName();
		if (!checkAccess(lFolderConfig.get(lName + SUFFIXUSERS), lUser))
			continue;
		if (lFileList[lFPtr].isDirectory()) {
			lSubFolders.add(lName);
		} else if (CommonUtilities.hasValue(lFolder) && lFileList[lFPtr].isFile() && !METACONFIG.equals(lName)
				&& (lName.endsWith(".txt") || lName.endsWith(".jsp"))) {
			lFiles.add(lName);
		}
	}
	// EXECUTE THE SQL IF ANY
	if (CommonUtilities.hasValue(lReportFile)) {
		if (!checkAccess(lFolderConfig.get(lReportFile + SUFFIXUSERS), lUser))
			throw new Exception("Access denied on report " + lReportFile);
		String lDatabaseConfig = lFolderConfig.get(lReportFile + SUFFIXDATABASE);
		if (lDatabaseConfig != null)
			lDatabase = lDatabaseConfig.trim();
		try {
			lParsedQuery = readFile(ROOTFOLDER + lFolder + File.separator + lReportFile);
			lQueryParams = new HashMap<String, String>();
			lParsedQuery = parseSql(lParsedQuery, lQueryParams, request);
		} catch (Exception lException) {
			lException.printStackTrace();
		}
	}
	ArrayList<String[]> lData = null;
	if (CommonUtilities.hasValue(lParsedQuery)) {
		lData = getData(lParsedQuery, lDatabase);
		if (PARAM_SEPERATOR_VAL_CSV.equals(lSeperator) || PARAM_SEPERATOR_VAL_PIPE.equals(lSeperator)) {
			StringBuffer lBuffer = new StringBuffer();
			lCount = lData.size();
			String[] lRow;
			String lSeperatorChar = COMMA;
			String lExtension = "csv";
			if (PARAM_SEPERATOR_VAL_CSV.equals(lSeperator)) {
				lSeperatorChar = COMMA;
				lExtension = "csv";
			} else if (PARAM_SEPERATOR_VAL_PIPE.equals(lSeperator)) {
				lSeperatorChar = PIPE;
				lExtension = "txt";
			}
			for (int lPtr = 0; lPtr < lCount; lPtr++) {
				lRow = lData.get(lPtr);
				for (int lCPtr = 0; lCPtr < lRow.length; lCPtr++) {
					lBuffer.append(lRow[lCPtr]);
					if (lCPtr < lRow.length - 1)
						lBuffer.append(lSeperatorChar);
					else
						lBuffer.append(NEWLINE);
				}
			}
			if (lCount > DISPLAYLIMIT) {
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition",
						"attachment;filename=" + lReportFile + ".zip");
				ZipOutputStream zipout = new ZipOutputStream(new Writer2Stream(out));
				zipout.setComment("Created by jsp File Browser v. ");
				zipout.setLevel(COMPRESSION_LEVEL);
				//
				zipout.putNextEntry(new ZipEntry(lReportFile + "." +lExtension));
				BufferedInputStream fr = new BufferedInputStream(new ByteArrayInputStream(String.valueOf(lBuffer).getBytes()));
				byte buffer[] = new byte[0xffff];
				copyStreamsWithoutClose(fr, zipout, buffer);
				fr.close();
				//
				zipout.closeEntry();
				zipout.finish();
				out.flush();
			} else {
				response.setContentType("text/csv");
				response.setHeader("content-disposition",
						"attachment; filename=" + lReportFile + "." + lExtension);
				out.println(lBuffer.toString());
			}
			return;
		}
	}
%>

<html>
<head>
<%@include file="../includes1.jsp" %>
<!-- <link href="../../css/datatables.css" rel="stylesheet"/> -->
		<link href="../css/monitordashboard.css" rel="stylesheet">
		<link href="../assets/css/monitor/jquery.dataTables.min.css" rel="stylesheet">
		<link href="../assets/css/monitor/buttons.dataTables.min.css" rel="stylesheet">
		<link href="../assets/css/monitor/dataTables.bootstrap.min.css" rel="stylesheet">
		<link href="../assets/css/monitor/dataTables.material.min.css" rel="stylesheet">

<jsp:include page="../header1.jsp">
	<jsp:param name="title" value="TReDS MIS Reports." />
	<jsp:param name="desc" value="" />
</jsp:include>

<style>
body {
	font-family: Arial, Verdana;
	font-weight: normal;
	font-size: 12px;
}

.stytbl {
	font-family: Arial, Verdana;
	font-weight: normal;
	font-size: 12px;
	background-color: #22729F;
}

.styhdr {
	background-color: #cccccc;
	font-size: 13px;
}

.styrow {
	background-color: white;
}

.stylmt {
	background-color: red;
	font-size: 13px;
}
.dtHorizontalVerticalExampleWrapper {
max-width: 600px;
margin: 0 auto;
}
#dtHorizontalVerticalExample th, td {
white-space: nowrap;
}
table.dataTable thead .sorting:after,
table.dataTable thead .sorting:before,
table.dataTable thead .sorting_asc:after,
table.dataTable thead .sorting_asc:before,
table.dataTable thead .sorting_asc_disabled:after,
table.dataTable thead .sorting_asc_disabled:before,
table.dataTable thead .sorting_desc:after,
table.dataTable thead .sorting_desc:before,
table.dataTable thead .sorting_desc_disabled:after,
table.dataTable thead .sorting_desc_disabled:before {
bottom: .5em;
}
</style>
<script language=javascript>
function browseFolder()
{
	document.mainForm.submit();
}
function runReport()
{
	var lFrm=document.mainForm;
	var lFolder=lFrm.<%=PARAM_FOLDER%>.value;
	var lReportFile=lFrm.<%=PARAM_QUERY%>.value;
	if (lReportFile.indexOf(".jsp") > 0)
	{
		if (lFolder.indexOf("\\")==0)
			lFolder=lFolder.substring(1);
		window.open(lFolder + "/" + lReportFile);
	}
	else
		document.mainForm.submit();
}
</script>
</head>
<body>
	<form name=mainForm method=get action="misreports">
		<a href="<%=REPORTJSP%>">Root</a>
		<%
			String lParams = PARAM_FOLDER + "=";
			for (int lPtr = 0; lPtr < lFolderList.length; lPtr++) {
				String lFolderName = lFolderList[lPtr];
				if (!CommonUtilities.hasValue(lFolderName))
					continue;
				lParams += File.separator + lFolderName;
		%>
		&gt;&gt; <a href="<%=REPORTJSP%>?<%=lParams%>"><%=lFolderName%></a>
		<%
			}
			if (lSubFolders.size() > 0) {
		%>
		&gt;&gt; <select id="<%=PARAM_FOLDER%>" name="<%=PARAM_FOLDER%>"
			onchange="javascript:browseFolder()">
			<option value="<%=lFolder%>">Select</option>
			<%
				for (int lPtr = 0; lPtr < lSubFolders.size(); lPtr++) {
						String lName = lSubFolders.get(lPtr);
			%><option value="<%=(lFolder + File.separator + lName)%>"><%=lName%></option>
			<%
				}
			%>
		</select>
		<%
			} else {
		%>
		<input type=hidden id="<%=PARAM_FOLDER%>" name="<%=PARAM_FOLDER%>"
			value="<%=lFolder%>">
		<%
			}
			if (lFiles.size() > 0) {
		%>
		<table class=stytbl cellspacing=1 cellpadding=2 border=0>
			<tr class=styhdr>
				<td colspan=2><select id="<%=PARAM_QUERY%>"
					name="<%=PARAM_QUERY%>">
						<option value="">Select</option>
						<%
							for (int lPtr = 0; lPtr < lFiles.size(); lPtr++) {
									String lName = lFiles.get(lPtr);
						%><option value="<%=lName%>"
							<%=lName.equals(lReportFile) ? "selected" : ""%>><%=lName%></option>
						<%
							}
						%>
				</select> <input type=radio id=<%=PARAM_SEPERATOR%> name=<%=PARAM_SEPERATOR%>
					value=1> CSV <input type=radio id=<%=PARAM_SEPERATOR%>
					name=<%=PARAM_SEPERATOR%> value=2> PIPE<input type=radio id="none"
					name=<%=PARAM_SEPERATOR%> value=0> NONE</td>
			</tr>
			<%
				if (lQueryParams != null) {
						Iterator<String> lIterator = lQueryParams.keySet().iterator();
						while (lIterator.hasNext()) {
							String lParamLabel = lIterator.next();
							String lParamName = CommonUtilities.replace(lParamLabel, " ", "_");
							String lParamValue = lQueryParams.get(lParamLabel);
							if (lParamValue == null)
								lParamValue = "";
			%>
			<tr class=styrow>
				<td><%=lParamLabel%></td>
				<td><input type="text" id="<%=lParamName%>"
					name="<%=lParamName%>" value="<%=lParamValue%>"></td>
			</tr>
			<%
				}
					}
			%>
			<tr class=styrow>
				<td colspan=2><input type=button value="Run"
					onclick=javascript:runReport()></td>
			</tr>
		</table>
		<%
			} // end if (lFiles.size() > 0)
		%>
		<table id="dtHorizontalVerticalExample" class="table table-striped table-bordered table-sm " cellspacing="0"
  width="100%" >
			<%
				lCount = (lData == null ? 0 : lData.size());
				if (lCount > DISPLAYLIMIT) {
					lCount = DISPLAYLIMIT;
			%>
			<tr class=stylmt>
				<td colspan=<%=((String[]) lData.get(0)).length + 1%>>Showing only
					<%=lCount - 1%> out of <%=lData.size() - 1%> rows. Download as CSV to
					get complete data.
				</td>
			</tr>
			<%
				}
				String[] lRow;
				for (int lPtr = 0; lPtr < lCount; lPtr++) {
					lRow = (String[]) lData.get(lPtr);
			%>
			<tr class=<%=(lPtr == 0 ? "styhdr" : "styrow")%>>
				<td class=styhdr><%=(lPtr == 0) ? "" : lPtr + ""%></td>
				<%
					for (int lCPtr = 0; lCPtr < lRow.length; lCPtr++) {
				%><td><%=lRow[lCPtr]%></td>
				<%
					}
				%>
			</tr>
			<%
				}
			%>
		</table>
	</form>
   	<%@include file="../footer1.jsp" %>
<!--    	<script src="../../js/datatables.js"></script> -->
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script src="../js/jquery.xtemplatetable.js"></script>
	<script id="script-resource-12" src="../assets/js/highcharts.js"></script>
	<script src="../assets/js/highcharts-3d.js"></script>
	<script src="../assets/js/exporting.js"></script>
	<script src="../assets/js/export-data.js"></script>
	<script src="../assets/js/monitor/jquery.dataTables.min.js"></script>
	<script src="../assets/js/monitor/dataTables.buttons.min.js"></script>
	<script src="../assets/js/monitor/jszip.min.js"></script>
	<script src="../assets/js/monitor/buttons.html5.min.js"></script>
	<script src="../assets/js/monitor/dataTables.bootstrap.min.js"></script>
	<script src="../assets/js/monitor/pdfmake.min.js"></script>
	<script src="../assets/js/monitor/vfs_fonts.js"></script>
	<script src="../assets/js/monitor/buttons.print.min.js"></script>
   	<script>
   	$(document).ready(function () {
 		$('#dtHorizontalVerticalExample').DataTable({
 			"scrollX": true,
 			"scrollY": 200,
 		});
 		$('.dataTables_length').addClass('bs-select');
		$('.stytbl').DataTable( {
			pagingType: "full_numbers",
			responsive: true,
			autoWidth: false,
			scrollX: "100%",
	        dom: 'Bfrtip',
	        language: {
	            infoEmpty: "No entries to show"
	        },
	        lengthMenu: [
	            [10, 25, 50, -1 ],
	            ['10 rows', '25 rows', '50 rows', 'Show all' ]
	        ],
	        buttons: [
	        	'pageLength',
	        	{
	                extend: 'collection',
	                text: 'Export',
	                buttons: [
	                	
			            'copyHtml5',
			            'print',
			              	{
			                    extend: 'excelHtml5',
			                    title: pName+' - ' + new Date().toJSON().slice(0,10).replace(/-/g,'-'),
			                },
							{
			                     extend: 'pdfHtml5',
			                     titleAttr: 'Export to PDF'
			                },
			            'csvHtml5'
	                ]
	            }
	        ]
	    } );
 	});
   	</script>
</body>
</html>