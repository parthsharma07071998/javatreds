<!DOCTYPE html>
<%@page import="com.xlx.commonn.CommonAppConstants"%>
<%@page import="com.xlx.treds.master.bean.CircularBean"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
%>
<html>
	<head>
		<title>Dashboard for Monitor.</title>
		<%@include file="includes1.jsp" %>
<!-- 		<link href="../css/datatables.css" rel="stylesheet"/> -->
		<link href="../css/bootstrap-datetimepicker.css" rel="stylesheet">
		<link href="../css/monitordashboard.css" rel="stylesheet">
		<link href="../assets/css/monitor/jquery.dataTables.min.css" rel="stylesheet">
		<link href="../assets/css/monitor/buttons.dataTables.min.css" rel="stylesheet">
		<link href="../assets/css/monitor/dataTables.bootstrap.min.css" rel="stylesheet">
		<link href="../assets/css/monitor/dataTables.material.min.css" rel="stylesheet">

<style>
	#cssTable{
	    text-align: center; 
	    vertical-align: middle;
	    font-size: 14px;
	    width : 100%
	}
	#cssTable1{
	    text-align: center; 
	    vertical-align: middle;
	    font-size: 14px;
	    width : 100%
	}
	
	div.highcharts-data-table table {
		width: 100%;		
	}
	
	div.highcharts-data-table table th, div.highcharts-data-table table td  {
		border: 1px solid #000;
		padding: 4px;
	}
	
	div.scrollCont th, div.scrollCont td { 
		word-break: keep-all; 
 	} 
 	
 	.dataTables_wrapper .dataTables_paginate .paginate_button:hover {
	  background: none;
	  color: black!important;
	  border-radius: 4px;
	  border: 1px solid #828282;
	}
	 
	.dataTables_wrapper .dataTables_paginate .paginate_button:active {
	  background: none;
	  color: black!important;
	}
	
	.dataTables_wrapper .dataTables_paginate .paginate_button {
	  padding : 0px;
	  margin-left: 0px;
	  display: inline;
	  border: 0px;
	}
	
	.red {
	    color: red;
	}
		
</style>

	</head>
	<body class="skin-blue">
	<jsp:include page="header1.jsp">
		<jsp:param name="title" value="Dashboard for Monitors" />
		<jsp:param name="desc" value="" />
	</jsp:include>
	
	<div class="content" >
	<span>
	<div id="diveodAndSessionMonitor" class="col-sm-6" style="display: none"></div>
	<div id="divholidayCheck" class="col-sm-6" style="display: none"></div>
	</span>
	<span>
	<div id="divmonetagoConnectivityCheck" class="col-sm-6" style="display: none"></div>
	<div id="divbillDetails" class="col-sm-6" style="display: none"></div>
	</span>
	<span>
	<div id="divinstrumentMonitor" class="col-sm-6" style="display: none"></div>
	</span>
	<span>
	<div id="divregistrationDetails" class="col-sm-6" style="display: none"></div>
	<div id="divonlineEntityMonitor" class="col-sm-6" style="display: none"></div>
	</span>
	<div id="divregisteredEntities" class="col-sm-12" style="display: none"></div>
	<div id="divpayFileMonitor" class="col-sm-12 scrollCont" style="display: none"></div>
	<div id="divpurchaserFuReport" class="col-sm-12 scrollCont" style="display: none"></div>
	<div id="divsupplierFuReport" class="col-sm-12 scrollCont" style="display: none"></div>
	<div id="divfinancierFuReport" class="col-sm-12 scrollCont" style="display: none"></div>
	<div id="divioclConnectivityCheck" class="col-sm-12" style="display: none"></div>
    </div>

	<%@ include file="footer1.jsp" %>
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

	<script type="text/javascript">
	var tpl;
	var nextCall; //key=templateName value=dateTime
	var metaData;
	$(document).ready(function() {
		var elem,lRegChart,lRegCumulativeChart,lRegDetailChart; 
			$.ajax( {
	            url: "monitors/getMonitorMeta",
	            type: "POST",
	            data:"",
	            success: function( pObj, pStatus, pXhr) {
	            	var lData;
	            	tpl = {};
	            	nextCall = {};
	            	metaData = {};
	            	for (var lKey in pObj){
	            		lData = pObj[lKey];
	            		elem = document.createElement('script');
		            	elem.id = lData.templateName;
		            	elem.innerHTML = lData.handelbarTemplate;
		            	elem.type="text/x-handlebars-template"
		            	document.body.appendChild(elem);
		            	var lTemplateId = '#'+lData.templateName;
 		            	tpl[lData.templateName] = Handlebars.compile($(lTemplateId).html());
 		            	nextCall[lData.templateName] = null;
 		            	metaData[lData.templateName] = lData;

	            	}
	            },
	        	error: errorHandler,
	        	complete: function() {
	        		fetchData();
					//function 
					//loop through nextCall
					//if null OR currenttime > nextcall(template) then ajax call
					//after ajax complete put the (current time + frequenc) to nextCall.
					//tpl(template).set()
	        	}
	        });
			
			/*
			$.ajax({
				url: "monitors/lov",
	            type: "POST",
	            data:"",
	            success: function( pObj, pStatus, pXhr){
	            	alert(JSON.stringify(pOBJ));
	            }
				
			})  */
			
		});
	
		function toggleTable(pKey){
			$('.'+pKey).toggle();
			$('.'+pKey).toggleClass('hide');	
		}

	
		function fetchData(){
			var lLastTime, lCurrentTime;
			for (var lKey in nextCall){
				lLastTime = nextCall[lKey];
				lCurrentTime = new Date().getTime();
				if(lLastTime==null || lCurrentTime > lLastTime){
					var lDataHash = {'templateName':lKey};
					var dataKey ;
					$.ajax( {
			            url: "monitors/getData",
			            type: "POST",
			            data: JSON.stringify(lDataHash),
			            success: function( pObj, pStatus, pXhr) {
			            	for(key in pObj){
			            		dataKey = key;
			            		pObj['currdate']=loginData.curDate;
			            		pObj['currtime']=loginData.curTime;
			            		$('#div'+dataKey).html(tpl[dataKey](pObj));
			            		$('#div'+dataKey).show();
			            		if (dataKey=='registeredEntities'){
			            			createRegEntityChart(pObj);
			            			lRegCumulativeChart.reflow();
			            			lRegChart.reflow();
			            		}else if (dataKey=='registrationDetails'){
			            			createRegDetailsChart(pObj);
			            			lRegDetailChart.reflow();
			            		}else if  (dataKey=='purchaserFuReport'){
			            			getDataTable('purchaserFuReport');
			            		}else if  (dataKey=='supplierFuReport'){
			            			getDataTable('supplierFuReport');
			            		}else if  (dataKey=='financierFuReport'){
			            			getDataTable('financierFuReport');
			            		}else if  (dataKey=='payFileMonitor'){
			            			getDataTable('payFileMonitor');
			            		}else if  (dataKey=='ioclConnectivityCheck'){
			            			getDataTable('ioclConnectivityCheck');
			            		}
			            		
			            	}
			            },
			        	error: errorHandler,
			        	complete: function() {
			        		nextCall[dataKey] = lCurrentTime + (Number(metaData[dataKey].frequency)*6000);
			        	}
			        });
					
				}
        	}
		}
		
		function createRegEntityChart(pData){
			var lData = pData.registeredEntities;
			var financialYears = [];
			var purchasers = [];
			var financiers = [];
			var suppliers = [];
			var purchasersCumulative = [];
			var financiersCumulative = [];
			var suppliersCumulative = [];
			var data = false;
			lData.forEach(function(elem) {
				financialYears.push(elem.FinancialYear);
				purchasers.push(elem.Buyer);
				financiers.push(elem.Finacier);
				suppliers.push(elem.Seller);
				purchasersCumulative.push(elem.cumulativeBuyer);
				financiersCumulative.push(elem.cumulativeFinacier);
				suppliersCumulative.push(elem.cumulativeSeller);
				data=true;
		    });
			if(lData!=null){
				lRegChart = Highcharts.chart('RegContainer', {
					  chart: {
					    type: 'bar'
					  },
					  title: {
					    text: null
					  },
					  colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],
					  xAxis: {
					    categories: financialYears,
					    title: {
						      text: 'Financial Year',
						      align: 'high'
						    },
					  },
					  yAxis: {
					    min: 0,
					    title: {
					      text: 'Count',
					      align: 'high'
					    },
					    labels: {
					      overflow: 'justify'
					    }
					  },
					  plotOptions: {
						bar: {
						  dataLabels: {
							enabled: true,
							allowOverlap: true
						  }
						}
					  },
					  legend: {
					    layout: 'vertical',
					    align: 'right',
					    verticalAlign: 'top',
					    x: -40,
					    y: 80,
					    floating: true,
					    borderWidth: 1,
					    backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
					    shadow: true
					  },
					  credits: {
					    enabled: false
					  },
					  series: [{
					    name: 'Purchaser',
					    data: purchasers
					  }, {
					    name: 'Supplier',
					    data: suppliers
					  }, {
					    name: 'Financier',
					    data: financiers
					  }]
					});
				
				lRegCumulativeChart = Highcharts.chart('RegCumulative', {
				  	title: {
					    text: null
					},
					colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],
				    yAxis: {
				        title: {
				            text: 'Number of Registrations'
				        }
				    },
				    xAxis: {
					    categories: financialYears,
					  },
				    legend: {
				        layout: 'vertical',
				        align: 'right',
				        verticalAlign: 'middle'
				    },

				    plotOptions: {
				        series: {
				            label: {
				                connectorAllowed: false
				            }
				        }
				    },
				    credits: {
					    enabled: false
					},
				    series: [{
				        name: 'Purchaser',
				        data: purchasersCumulative
				    }, {
				        name: 'Supplier',
				        data: suppliersCumulative
				    }, {
				        name: 'Financier',
				        data: financiersCumulative
				    }],

				    responsive: {
				        rules: [{
				            condition: {
				                maxWidth: 500
				            },
				            chartOptions: {
				                legend: {
				                    layout: 'horizontal',
				                    align: 'center',
				                    verticalAlign: 'bottom'
				                }
				            }
				        }]
				    }

				});
			}
		}
		
		function createRegDetailsChart(pData){
			var lData = pData.registrationDetails;
			var lDraft = lData.Draft;
			var lSubmitted =lData.Submitted;
			var lReturned = lData.Returned;
			var lTotalSum = lDraft+lReturned+lSubmitted;
			if(lData!=null){
				lRegDetailChart = Highcharts.chart('RegDetails', {
					chart : {
				               plotBackgroundColor: null,
				               plotBorderWidth: null,
				               plotShadow: false,
				               options3d: {
				                   enabled: true,
				                   alpha: 65,
				                   beta: 0
				               }
				            },
		            title : {
				               text: null   
				            },
		            tooltip : {
				               pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
				            },
				            
		            plotOptions : {
		               pie: {
		                  allowPointSelect: true,
		                  cursor: 'pointer',
		                  depth: 45,
		                  dataLabels: {
		                     enabled: false           
		                  },
		                  showInLegend: true
		               }
		            },
		            series : [{
			               type: 'pie',
			               name:'Total',
			               data: [
			            	  ['Draft - '+lDraft,       parseFloat(( lDraft / lTotalSum * 100 ).toFixed(2))],
			            	  ['Returned -'+lReturned,    parseFloat(( lReturned / lTotalSum * 100 ).toFixed(2))],
			                  ['Submitted -'+lSubmitted,   parseFloat(( lSubmitted/ lTotalSum * 100 ).toFixed(2))]
			               ]
			            }],
		            credits :{
					    enabled: false
					  },
					colors :  ['#058DC7', '#69CD4B', '#ED561B']
				});
			}
		}
		
		function getDataTable(pName){
			$('#'+pName).DataTable( {
				pagingType: "full_numbers",
				responsive: true,
				autoWidth: false,
				scrollX: "100%",
		        dom: 'Bfrtip',
		        language: {
		            infoEmpty: "No entries to show"
		        },
		        columnDefs: [
		            {
		                targets: [ 0, 1, 2 ],
		                className: 'mdl-data-table__cell--non-numeric'
		            }
		        ],
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
		}
		function payFile(pDate){
			location.href = 'payfile?date='+pDate;
		}
		
	</script>	

</body>
</html>