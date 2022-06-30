<%@page import="groovy.json.JsonBuilder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.xlx.treds.master.bean.ConfirmationWindowBean"%>
<%@page import="com.xlx.treds.OtherResourceCache"%>
<%@page import="com.xlx.treds.master.bean.AuctionCalendarBean"%>
<%@page import="com.xlx.commonn.bean.BeanMeta"%>
<%@page import="com.xlx.commonn.BeanMetaFactory"%>
<%
AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getCurrentNextConfirmationWindowBeanForToday(OtherResourceCache.AUCTIONTYPE_NORMAL);
List<Map<String,Object>> lList = new ArrayList<Map<String, Object>>();
if (lAuctionCalendarBean != null) {
    Map<String, Object> lMap = new HashMap<String, Object>();
    lMap.put("lbl","Bid");
    lMap.put("start",BeanMetaFactory.getInstance().getTimeFormatter().format(lAuctionCalendarBean.getBidStartTime()));
    lMap.put("end",BeanMetaFactory.getInstance().getTimeFormatter().format(lAuctionCalendarBean.getBidEndTime()));
    lMap.put("sm", lAuctionCalendarBean.getBidStartTime().getTime());
    lMap.put("em", lAuctionCalendarBean.getBidEndTime().getTime());
    lMap.put("clr","#ccc");
    lMap.put("clrbg","#3c8dbc");
    lList.add(lMap);
}
if (lConfirmationWindowBean != null) {
    Map<String, Object> lMap = new HashMap<String, Object>();
    lMap.put("lbl","Accept");
    lMap.put("start",BeanMetaFactory.getInstance().getTimeFormatter().format(lConfirmationWindowBean.getConfStartTime()));
    lMap.put("end",BeanMetaFactory.getInstance().getTimeFormatter().format(lConfirmationWindowBean.getConfEndTime()));
    lMap.put("sm", lConfirmationWindowBean.getConfStartTime().getTime());
    lMap.put("em", lConfirmationWindowBean.getConfEndTime().getTime());
    lMap.put("clr","#ccc");
    lMap.put("clrbg","#3c8dbc");
    lList.add(lMap);
}
String lJson = new JsonBuilder(lList).toString();

%>

<script id="tplPageSumm" type="text/x-handlebars-template">
{{#each this}}
<div style="display:inline-block;padding:0px;width:240px;">
  <div class="col-sm-4 hidden-xs hidden-sm" style="font-size:24px; padding:0px;" >{{lbl}}</div>
  <div class="col-sm-4 hidden-xs hidden-sm" style="padding:0px">{{start}}<br>{{end}}</div>
  <div class="col-sm-4"  style="padding:0px"><div class="knob" data-start="{{sm}}" data-end="{{em}}" data-clr="{{clr}}" data-clrbg="{{clrbg}}" 
  style="width:36px;height:36px;border-radius:100%; padding:0px"></div></div>
</div>
{{/each}}
</script>

<script>
$(document).ready(function() {
	var lTplPageSumm = Handlebars.compile($('#tplPageSumm').html());
	$('#divPageSumm').html(lTplPageSumm(<%=lJson%>));
	$('.page-title h1').html('Auctions: <%=BeanMetaFactory.getInstance().getDateFormatter().format(lAuctionCalendarBean.getDate())%>'); 
});
function handleClock(pMillis) {
	$('.knob').each(function(){
		var lThis$=$(this);
		var lStart=parseInt(lThis$.data('start'));
		var lEnd=parseInt(lThis$.data('end'));
		var lColor=lThis$.data('clr');
		var lColorBg=lThis$.data('clrbg');
		var lDeg=parseInt((pMillis-lStart)*360/(lEnd-lStart));
		if (lDeg<0) lDeg=0;
		else if (lDeg>360) lDeg=360;
		var lMsg=pMillis<lStart?'Time to Start : ' + timeRemaining(lStart-pMillis):'Time to Close : ' + timeRemaining(lEnd-pMillis);
		lThis$.attr('title',lMsg);
		lThis$.css('background-color',lColorBg);
		var lBgImg='linear-gradient(' + (lDeg<=180?90+lDeg:lDeg-90) + 'deg, transparent 50%, '+(lDeg<=180?lColor:lColorBg)+' 50%),linear-gradient(90deg, '+lColor+' 50%, transparent 50%)';
		lThis$.css('background-image', lBgImg);
	});
}
function timeRemaining(pMillis) {
	pMillis=pMillis/1000;
	var lH=parseInt(pMillis/3600);
	pMillis=pMillis%3600;
	var lM=parseInt(pMillis/60);
	var lS=parseInt(pMillis%60);
	return lH+' Hr '+lM+' Min '+lS+' Sec';
}
</script>