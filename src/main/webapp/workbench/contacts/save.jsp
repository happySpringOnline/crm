<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";

	Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
	Set<String> stages = pMap.keySet();
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
<!--引入自动补全插件-->
<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>

	<script>

		var json = {
			<%
				for (String stage:stages){

				String possibility = pMap.get(stage);
			%>
				"<%=stage%>":<%=possibility%>,
			<%
				}
			%>
		};

		//alert(json)
		$(function (){
			//时间控件
			$(".time1").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			//时间控件
			$(".time2").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "top-left"
			});

			//【客户名称自动补全】
			$("#create-customerName").typeahead({
				source: function (query, process) {
					$.get(
							"workbench/transaction/getCustomerName.do",
							{ "name" : query },
							function (data) {
								process(data);
							},
							"json"
					);
				},
				delay: 500
			});

			//【阶段与可能性关联】
			$("#create-transactionStage").change(function (){
				//取得选中的阶段
				//var stage = this.value
				var stage = $("#create-transactionStage").val()
				/*
					我们现在以json.key的形式不能取得value
					因为今天的stage是一个可变的变量
					如果是这样的key,那么我们就不能以传统的json.key的形式来取值
					我们要使用的取值方式为
					json[key]
				 */
				var possibility = json[stage];
				$("#create-possibility").val(possibility);
			})

			//【查找市场活动】
			$("#aname").keydown(function (event) {
				if(event.keyCode==13){
					$.ajax({
						url:"workbench/transaction/getActivityListByAname.do",
						data:{
							"aname":$.trim($("#aname").val())
						},
						dataType:"json",
						type:"get",
						success:function (data) {
							var html = "";
							$.each(data,function (i,n) {
								html += '<tr>'
								html += '<td><input type="radio" name="activity" value="'+n.id+'"/></td>'
								html += '<td id='+n.id+'>'+n.name+'</td>'
								html += '<td>'+n.startDate+'</td>'
								html += '<td>'+n.endDate+'</td>'
								html += '<td>'+n.owner+'</td>'
								html += '</tr>'
							})
							$("#activityTbody").html(html)
						}
					})
					return false;
				}
			})

			//【关联市场活动与交易】
			$("#submitActivityBtn").click(function () {
				var $xz = $("input[name=activity]:checked");
				//把选择需要关联交易的市场活动的id存到隐藏域
				var activityId = $xz.val();
				$("#activityId").val(activityId);
				//把选中关联的市场活动的名字放到文本框中
				var acitivityName= $("#"+activityId).text()
				$("#create-activitySrc").val(acitivityName)
				//关闭模态窗口
				$("#findMarketActivity").modal("hide");
			})

			//【查找联系人】
			$("#cname").keydown(function (event) {
				if(event.keyCode == 13){
					$.ajax({
						url:"workbench/transaction/getContactsListByCname.do",
						data:{
							"cname":$.trim($("#cname").val())
						},
						dataType:"json",
						type:"get",
						success:function (data) {
							var html = "";
							$.each(data,function (i,n) {
								html += '<tr>';
								html += '<td><input type="radio" name="contacts" value="'+n.id+'"/></td>';
								html += '<td id="'+n.id+'">'+n.fullname+'</td>';
								html += '<td>'+n.email+'</td>';
								html += '<td>'+n.mphone+'</td>';
								html += '</tr>';
							})
							$("#contactsTbody").html(html)
						}
					})
					return false;
				}
			});

			//【关联联系人与交易】
			$("#submitActivityBtn2").click(function () {
				var $xz = $("input[name=contacts]:checked");
				var contactsId = $xz.val()
				$("#contactsId").val(contactsId)

				var contactName = $("#"+contactsId).text()
				$("#create-contactsName").val(contactName)

				$("#findContacts").modal("hide")
			})

			//【保存交易】
			$("#saveTran").click(function (){
				$("#tranForm").submit()
			})
		});

	</script>
</head>
<body>

	<!-- 查找市场活动 -->	
	<div class="modal fade" id="findMarketActivity" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">查找市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" style="width: 300px;" id="aname" placeholder="请输入市场活动名称，支持模糊查询" >
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable3" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
							</tr>
						</thead>
						<tbody id="activityTbody">


						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitActivityBtn">关联</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 查找联系人 -->	
	<div class="modal fade" id="findContacts" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">查找联系人</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="cname" style="width: 300px;" placeholder="请输入联系人名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>邮箱</td>
								<td>手机</td>
							</tr>
						</thead>
						<tbody id="contactsTbody">


						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitActivityBtn2">关联</button>
				</div>
			</div>
		</div>
	</div>
	
	
	<div style="position:  relative; left: 30px;">
		<h3>创建交易</h3>
	  	<div style="position: relative; top: -40px; left: 70%;">
			<button type="button" class="btn btn-primary" id="saveTran">保存</button>
			<button type="button" class="btn btn-default" onclick="window.history.back()">取消</button>
		</div>
		<hr style="position: relative; top: -40px;">
	</div>
	<form class="form-horizontal" id="tranForm" action="workbench/transaction/saveTransaction.do" role="form" style="position: relative; top: -30px;">
		<div class="form-group">
			<label for="create-transactionOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionOwner" name="owner">
				  <option></option>
					<c:forEach items="${uList}" var="u">
						<option value="${u.id}" ${user.id eq u.id ? "selected" : ""}>${u.name}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-amountOfMoney" class="col-sm-2 control-label">金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-amountOfMoney" name="money">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-transactionName" name="name">
			</div>
			<label for="create-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="create-expectedClosingDate" name="expectedDate">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-accountName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-customerName" name="customerName" placeholder="支持自动补全，输入客户不存在则新建1">
				<input type="hidden" id="customerId">
			</div>
			<label for="create-transactionStage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
			  <select class="form-control" id="create-transactionStage" name="stage">
			  	<option></option>
			  	<c:forEach items="${stage}" var="s1">
					<option value="${s1.value}">${s1.text}</option>
				</c:forEach>
			  </select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionType" class="col-sm-2 control-label">类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionType" name="type">
				  <option></option>
				  <c:forEach items="${transactionType}" var="t">
					  <option value="${t.value}">${t.text}</option>
				  </c:forEach>
				</select>
			</div>
			<label for="create-possibility" class="col-sm-2 control-label">可能性</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-possibility">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-clueSource" class="col-sm-2 control-label">来源</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-clueSource" name="source">
				  <option></option>
					<c:forEach items="${source}" var="s2">
						<option value="${s2.value}">${s2.text}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-activitySrc" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" data-toggle="modal" data-target="#findMarketActivity"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-activitySrc" readonly>
				<input type="hidden" id="activityId" name="activityId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactsName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" ><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-contactsName" value="${contact.fullname}" readonly>
				<input type="hidden" id="contactsId" name="contactsId" value="${contact.id}">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-describe" class="col-sm-2 control-label">描述</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-describe" name="description"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-contactSummary" name="contactSummary"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time2" id="create-nextContactTime" name="nextContactTime">
			</div>
		</div>
		
	</form>
</body>
</html>