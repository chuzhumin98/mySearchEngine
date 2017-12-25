<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import= "net.sf.json.JSONObject" %>
<%@ page import= "net.sf.json.JSONArray" %>
<%@ page import= "server.IndexTable" %>
<%
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String htmlPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
System.out.println(path);
System.out.println(basePath);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>搜索结果展示</title>
<link rel="stylesheet" type="text/css" href=<%=path+"/bootstrap/css/bootstrap.min.css" %> />
<script type="text/javascript" src=<%=path+"/bootstrap/js/bootstrap.min.js" %>></script>
<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
<!--
#Layer1 {
	position:absolute;
	left:0px;
	top:31px;
	width:1600px;
	height:100px;
	z-index:1;
}
#Layer2 {
	position:absolute;
	left:10px;
	top:82px;
	width:648px;
	height:602px;
	z-index:2;
}
#Layer3 {
	position:absolute;
	left:10px;
	top:697px;
	width:652px;
	height:67px;
	z-index:3;
}
-->
div{margin:5px;border:0;padding:0;}
</style>
</head>

<body>
<%
	String[] currentQuery=(String[]) request.getAttribute("currentQuery");
	if (currentQuery == null) {
		currentQuery = new String [4];
		for (int i = 0; i < 4; i++) {
			currentQuery[i] = "";
		}
	}
	int currentPage = 1;
	if (request.getAttribute("currentPage") != null) {
		currentPage = (Integer) request.getAttribute("currentPage");
	}
	int searchMethod = 7;
	String queryTotal = "query1="+currentQuery[0]+"&query2=" +currentQuery[1]+
			"&query3="+currentQuery[2]+"&query4="+currentQuery[3]+"&method="+searchMethod; //针对query的完整描述
%>

<script type="text/javascript">  
function myCheck()  
{  
   for(var i=0;i<4;i++)  
   {  
      if(document.form1.elements[i].value != "")  
      {    
         return true;  
      }  
   }  
   alert("搜索字段不能全为空!");
   return false;  
    
}  

function toSimple() {
	window.event.returnValue=false
	window.location.href="resultShow.jsp";  
}
</script>  
<script type="text/javascript">
    function setMethod(method,methodName){
    	document.getElementById("method").value = method;
    	alert("succeed change to "+methodName);
    }
</script>


<nav class="navbar navbar-default" role="navigation" style="height:45px;border-style:none">
</nav>
<div id="Layer1">
<form class="form-inline" id="form1" name="form1" method="get" action="LuceneServer" onsubmit="return myCheck()">
<nav class="navbar navbar-default" role="navigation" style="height:141px;border-style:none;left:0px;width:1600px;">
    <div class="container-fluid"> 
        <form class="navbar-form navbar-left" role="search">
        <nav class="navbar navbar-default" role="navigation" style="height:90px;border-style:none;left:50px;width:90px;top:10px;">
        <img src=<%=path+"/images/cnki2.jpg" %>  alt="Cnki Search" width="70" height="70" class="img-rounded"/> </nav>
        	<nav class="navbar navbar-default" role="navigation" style="position: absolute;height:50px;border-style:none;left:180px;width:1600px;top:0px;">
            <div>
            <span class="label label-info">题名</span>                     
            <input style="width:200px;height:30px" type="text" class="form-control" id="searchInput1" placeholder="input something..." name="query1" value="<%= currentQuery[0]%>">           
            　　　
            <span class="label label-info">作者</span> 
            <input style="width:200px;height:30px" type="text" class="form-control" id="searchInput2" placeholder="input something..." name="query2" value="<%= currentQuery[1]%>">
            </div>
            <div>
            <span class="label label-info">摘要</span>
            <input style="width:495px;height:30px" type="text" class="form-control" id="searchInput3" placeholder="input something..." name="query3" value="<%= currentQuery[2]%>">
            </div>
            <div>
            <span class="label label-info">&nbsp&nbsp年&nbsp&nbsp</span>
            <input style="width:430px;height:30px" type="text" class="form-control" id="searchInput4" placeholder="input something..." name="query4" value="<%= currentQuery[3]%>">
            <input type="hidden" name="method" value="7" id="method" />
            <button style="height:30px;width:60px" class="btn btn-success" type="submit">搜索</button>
            <button style="height:30px;width:120px" class="btn btn-link" onclick="toSimple()">返回普通搜索</button>
        	</div>
        	</nav>
        </form>
        <br/>
   </nav> 
	</div>

</form>
</div>


<%  	
	String[] fields = IndexTable.getFields(searchMethod);
	int fieldNum = fields.length;
	JSONObject results = JSONObject.fromObject(request.getAttribute("results"));	
	JSONArray[] myResults = new JSONArray [fieldNum];
	for (int i = 0; i < fieldNum; i++) {
		if (results.containsKey(fields[i])) {
			myResults[i] = results.getJSONArray(fields[i]);
		} else {
			myResults[i] = new JSONArray();
		}
		
	}
	int resultNum = 0;
	if (request.getAttribute("resultNum") != null) {
		resultNum = (Integer) request.getAttribute("resultNum");
	}
	
%>
<div id="Layer2" style="top: 180px; height: 900px; left:185px">
  <div id="imagediv">共搜到<%= resultNum %>条结果：
  <br/></br/>
  <Table style="left: 0px; width: 594px;">
  <%
  for (int i = 0; i < myResults[0].size(); i++) {
  %>
  <p># <%= currentPage*10+i-9 %> </p>
  <%if (fieldNum == 1) { %>
  <%
  String[] lines = myResults[0].getString(i).split("\n");
  for (int j = 0; j < lines.length; j++) {
  %>
  <p><%= lines[j] %> </p>
  <%} %>
  <%} else { %>
  <%for (int j = 0; j < fieldNum; j++){ %>
  	<p><%= fields[j] %>:<%= myResults[j].getString(i) %> </p>
  	<%} %>
  	<%} %>
  <hr/>
  <%} %>
  </Table>
  </div>
  <div>
  <br/>
  <div align="center"> <img src=<%=path+"/images/huawen.jpg" %> alt="Tsiiiiiiiinghua" width="360" height="60"/></div>
  	<p align="center"><font size="3">
  		<%if(currentPage!=1){ %><a href="LuceneServer?query=<%=currentQuery%>&page=<%=1%>"> 首页</a>
  		<%}else{ %> 首页<%} %>
		<%if(currentPage>1){ %>
			<a href="LuceneServer?<%=queryTotal%>&page=<%=currentPage-1%>"> 上一页</a>
		<%}else{ %>
		 上一页<%}; %>
		 <%for (int i=Math.max(1,currentPage-5);i<currentPage;i++){%>
			<a href="LuceneServer?<%=queryTotal%>&page=<%=i%>"><%=i%></a>
		<%}; %>
		<strong> <%=currentPage%></strong>
		<%for (int i=currentPage+1;i<=Math.min(currentPage+5,(resultNum+9)/10);i++){ %>
			<a href="LuceneServer?<%=queryTotal%>&page=<%=i%>"><%=i%></a>
		<%} %>
		<%if(currentPage*10 >= resultNum){%> 下一页<%}else { %>
		<a href="LuceneServer?<%=queryTotal%>&page=<%=currentPage+1%>"> 下一页</a>
		<%} %>
		</font>
	</p>
  </div>
</div>
<div id="Layer3" style="top: 1000px; left: 27px;">
	
</div>
<div>
</div>
</body>
