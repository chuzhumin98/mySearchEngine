<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
System.out.println(request.getCharacterEncoding());
response.setCharacterEncoding("utf-8");
System.out.println(response.getCharacterEncoding());
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
System.out.println(path);
System.out.println(basePath);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.min.css" />
<script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
<!--
#Layer1 {
	position:absolute;
	left:489px;
	top:326px;
	width:404px;
	height:29px;
	z-index:1;
}
#Layer2 {
	position:absolute;
	left:480px;
	top:68px;
	width:446px;
	height:152px;
	z-index:2;
}
-->
</style>
</head>
<div align="center" style="padding-top:100px; padding-bottom:50px;"> <img src= "images/cnki1.png" alt="Cnki search" width="240" height="150"/></div>
    <form class="form-horizontal" id="form1" name="form1" method="get" action="LuceneServer">
	  <div class="form-group">
	    <div class="col-sm-offset-3 col-sm-6">
	      <div class="input-group">
          	<input type="text" class="form-control" id="searchInput" placeholder="input something..." name="query">
            <span class="input-group-btn">
                <button class="btn btn-success" type="submit">
                	<span class="glyphicon glyphicon-search" aria-hidden="true"></span>搜索
                </button>
            </span>
          </div>
	    </div>
	  </div>
	</form>
    
    
    
    

</body>
</html>
