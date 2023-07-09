<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css"/>
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){
		pageList(1,3)

		$("#qx").click(function () {
			$("input[name=transaction]").prop("checked",this.checked)
		})
		
		$("#tranTbody").on("click",$("input[name=transaction]"),function () {
			$("#qx").prop("checked",$("input[name=transaction]").length==$("input[name=transaction]:checked").length)
		})

		//【查询】
        $("#searchBtn").click(function () {
            pageList(1,3)
        })
		
		//【修改】
		$("#editBtn").click(function () {
			var $xz = $("input[name=transaction]:checked");
			if ($xz.length==0){
				alert("请选择需要修改的记录!")
			}else if($xz.length>1){
				alert("只能选择一条记录进行修改！")
			}else {
				var tranId = $xz.val();
				window.location.href="workbench/transaction/edit.do?id="+tranId;
			}
		})
		
		//【删除】
		$("#deleteBtn").click(function () {
			var $xz = $("input[name=transaction]:checked");
			if ($xz.length==0){
				alert("请选择需要删除的记录!")
			}else {
				if(confirm("确定要删除选中的交易吗？")){
					var param = '';
					for (let i = 0; i < $xz.length; i++) {
						var id = $xz[i].value;
						param += "id="+id;
						if(i<$xz.length-1){
							param += "&"
						}
					}
					$.ajax({
						url:"workbench/transaction/delete.do",
						data:param,
						dataType:"JSON",
						type:"POST",
						success:function (data) {
							if (data){
								pageList(1,3)
							}else {
								alert("删除记录失败！")
							}
						}
					})
				}
			}

		})
	});

	//【展示交易信息方法】
	function pageList(pageNo,pageSize) {
		$.ajax({
			url:"workbench/transaction/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"owner":$.trim($("#search-owner").val()),
				"name":$.trim($("#search-TranName").val()),
				"customerName":$.trim($("#search-customerName").val()),
				"stage":$.trim($("#search-tranStage").val()),
				"source":$.trim($("#search-clueSource").val()),
				"contactsName":$.trim($("#search-contactsName").val())
			},
			dataType:"JSON",
			type:"GET",
			success:function (data){
				var html = "";
				$.each(data.dataList,function (i,n) {
					html += '<tr>';
					html += '<td><input type="checkbox" name="transaction" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/transaction/detail.do?id='+n.id+'\';">'+n.customerId+'-'+n.name+'</a></td>';
					html += '<td>'+n.customerId+'</td>';
					html += '<td>'+n.stage+'</td>';
					html += '<td>'+n.type+'</td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.source+'</td>';
					html += '<td>'+n.contactsId+'</td>';
					html += '</tr>';
				})

				$("#tranTbody").html(html)

				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1

				//数据处理完毕之后，结合分页查询，对前端展现出分页信息
				$("#transactionPage").bs_pagination({
					currentPage:pageNo,//页码
					rowsPerPage:pageSize,//每页显示的记录条数
					maxRowsPerPage: 20,//每页最多显示的记录条数
					totalPages:totalPages,//总页数
					totalRows:data.total,//总记录条数

					visiblePageLinks:3,//显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//该回调函数在点击分页组件的时候触发
					//(4) 点击分页组件的时候，调用pageList方法
					onChangePage:function (event,data) {
						pageList(data.currentPage,data.rowsPerPage)
					}
				})
			}



		})
	}
	
</script>
</head>
<body>
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>交易列表</h3>
			</div>
		</div>
	</div>
	
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
	
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
			<!--查询-->
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" id="search-owner" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" id="search-TranName" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">客户名称</div>
				      <input class="form-control" id="search-customerName" type="text">
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">阶段</div>
					  <select class="form-control" id="search-tranStage">
					  	<option></option>
						<c:forEach items="${stage}" var="s">
							<option value="${s.value}">${s.text}</option>
						</c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">类型</div>
					  <select class="form-control" id="search-tranType">
					  	<option></option>
						  <c:forEach items="${transactionType}" var="t">
							  <option value="${t.value}">${t.text}</option>
						  </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">来源</div>
				      <select class="form-control" id="search-clueSource">
						  <option></option>
						  <c:forEach items="${source}" var="s">
							  <option value="${s.value}">${s.text}</option>
						  </c:forEach>
						</select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">联系人名称</div>
				      <input class="form-control" type="text"id="search-contactsName">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>

			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" onclick="window.location.href='workbench/transaction/open.do';"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
			</div>

			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
							<td>客户名称</td>
							<td>阶段</td>
							<td>类型</td>
							<td>所有者</td>
							<td>来源</td>
							<td>联系人名称</td>
						</tr>
					</thead>
					<tbody id="tranTbody">

					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 20px; ">

			</div>
			<div id="transactionPage"></div>
		</div>
		
	</div>
</body>
</html>