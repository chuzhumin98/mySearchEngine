<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<script>
const translate = require('translate-api');

let transUrl = 'https://nodejs.org/en/';
translate.getPage(transUrl).then(function(htmlStr){
  console.log(htmlStr.length)
});

let transText = 'hello world!';
translate.getText(transText,{to: 'zh-CN'}).then(function(text){
  console.log(text)
  alert(text);
});

</script>
<input style="width:570px;height:30px" type="text" class="form-control" id="searchInput" placeholder="input something..." name="query" value="">
<button style="height:30px;width:60px" class="btn btn-success">搜索</button>
</body>
</html>