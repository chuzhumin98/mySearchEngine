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
	width:1400px;
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
</style>
</head>

<body>
<%
	String currentQuery=(String) request.getAttribute("currentQuery");
	int currentPage=(Integer) request.getAttribute("currentPage");
%>

<script type="text/javascript">  
function myCheck()  
{  
   for(var i=0;i<document.form1.elements.length-1;i++)  
   {  
      if(document.form1.elements[i].value=="")  
      {  
         alert("当前表单不能有空项");  
         document.form1.elements[i].focus();  
         return false;  
      }  
   }  
   return true;  
    
}  
</script>  

<nav class="navbar navbar-default" role="navigation" style="height:55px;border-style:none"></nav>
<div id="Layer1">
<form class="form-inline" id="form1" name="form1" method="get" action="LuceneServer" onSubmit="return myCheck()">
<nav class="navbar navbar-default" role="navigation" style="height:81px;border-style:none">
    <div class="container-fluid"> 
        <form class="navbar-form navbar-left" role="search">
        	<img src=<%=path+"/images/cnki2.jpg" %>  alt="Cnki Search" width="50" height="50" class="img-rounded"/>
            <input style="width:570px;height:30px" type="text" class="form-control" id="searchInput" placeholder="input something..." name="query" value="<%=currentQuery %>">
            <button style="height:30px;width:60px" class="btn btn-success" type="submit">搜索</button>
        </form>
    </div>
</nav>
</form>
</div>

<%  	
	int searchMethod = (Integer) request.getAttribute("searchMethod");
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
	int resultNum = (Integer) request.getAttribute("resultNum");
	
%>
<div id="Layer2" style="top: 120px; height: 900px; left:185px">
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
  <div align="center"> <img src=<%=path+"/images/tsiiinghua.png" %> alt="Tsiiiiiiiinghua" width="360" height="60"/></div>
  	<p align="center"><font size="3">
  		<%if(currentPage!=1){ %><a href="LuceneServer?query=<%=currentQuery%>&page=<%=1%>"> 首页</a>
  		<%}else{ %> 首页<%} %>
		<%if(currentPage>1){ %>
			<a href="LuceneServer?query=<%=currentQuery%>&page=<%=currentPage-1%>"> 上一页</a>
		<%}else{ %>
		 上一页<%}; %>
		 <%for (int i=Math.max(1,currentPage-5);i<currentPage;i++){%>
			<a href="LuceneServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a>
		<%}; %>
		<strong> <%=currentPage%></strong>
		<%for (int i=currentPage+1;i<=Math.min(currentPage+5,(resultNum+9)/10);i++){ %>
			<a href="LuceneServer?query=<%=currentQuery%>&page=<%=i%>"><%=i%></a>
		<%} %>
		<%if(currentPage*10 >= resultNum){%> 下一页<%}else { %>
		<a href="LuceneServer?query=<%=currentQuery%>&page=<%=currentPage+1%>"> 下一页</a>
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
