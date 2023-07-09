<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";

	Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
	Set<String> set = pMap.keySet();
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
	var json={

		<%
			for (String stage:set){

		%>
			"<%=stage%>":<%=pMap.get(stage)%>,
		<%
			}
		%>
	}

	$(function () {
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

		//【阶段与可能性】
		$("#edit-transactionStage").change(function () {
			var stage = $("#edit-transactionStage").val()

			var possibility = json[stage]

			$("#edit-possibility").val(possibility)
		})

		//【客户名称自动补全】
		$("#edit-accountName").typeahead({
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

		//【市场活动名称，支持模糊查询】
		$("#aname").keydown(function (event) {
			if(event.keyCode==13){
				$.ajax({
					url:"workbench/transaction/getActivityListByAname.do",
					data:{
						"aname":$.trim($("#aname").val())
					},
					dataType:"JSON",
					type:"GET",
					success:function (data){
						var html="";
						$.each(data,function (i,n) {
							html += '<tr>';
							html += '<td><input type="radio" name="activity" value="'+n.id+'"/></td>';
							html += '<td id="'+n.id+'">'+n.name+'</td>';
							html += '<td>'+n.startDate+'</td>';
							html += '<td>'+n.endDate+'</td>';
							html += '<td>'+n.owner+'</td>';
							html += '</tr>';
						})
						$("#aTbody").html(html)
					}
				})
				return false;
			}
		});
		
		//【根据用户选择的市场活动进行关联】
		$("#submitActivityBtn").click(function () {
			var actvivityId = $("input[name=activity]:checked").val();
			$("#activityId2").val(actvivityId);

			var actvivityName = $("#"+actvivityId).text();
			$("#edit-activitySrc").val(actvivityName)

			$("#findMarketActivity").modal("hide");
		})

		//【联系人名称，支持模糊查询】
		$("#cname").keydown(function (event) {
			if(event.keyCode==13){
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
							html += '<td><input type="radio" name="contact" value="'+n.id+'"/></td>';
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
		})

		//【关联联系人与交易】
		$("#submitActivityBtn2").click(function () {
			var contactId = $("input[name=contact]:checked").val()
			$("#contactId").val(contactId);

			var contactName = $("#"+contactId).text();
			$("#edit-contactsName").val(contactName)

			$("#findContacts").modal("hide")
		})

		//【更新】
		$("#updateBtn").click(function () {
			$("#tranForm").submit();
		})

	})
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
						    <input type="text" class="form-control" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询" id="aname">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable4" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
							</tr>
						</thead>
						<tbody id="aTbody">
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
						    <input type="text" class="form-control" style="width: 300px;" placeholder="请输入联系人名称，支持模糊查询" id="cname">
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
		<h3>更新交易</h3>
	  	<div style="position: relative; top: -40px; left: 70%;">
			<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
			<button type="button" class="btn btn-default" onclick="window.history.back()">取消</button>
		</div>
		<hr style="position: relative; top: -40px;">
	</div>
	<form class="form-horizontal" role="form" style="position: relative; top: -30px;" action="workbench/transaction/update.do" method="post" id="tranForm">
		<input type="hidden" name="id" value="${tran.id}">
		<div class="form-group">
			<label for="edit-transactionOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="edit-transactionOwner" name="owner">
				  <option></option>
					<c:forEach items="${uList}" var="u">
						<option value="${u.id}" ${u.name eq tran.owner?"selected":""} >${u.name}</option>
					</c:forEach>
				</select>
			</div>
			<label for="edit-amountOfMoney" class="col-sm-2 control-label">金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="edit-amountOfMoney" value="${tran.money}" name="money">
			</div>
		</div>
		
		<div class="form-group">
			<label for="edit-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="edit-transactionName" value="${tran.name}" name="name">
			</div>
			<label for="edit-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="edit-expectedClosingDate" value="${tran.expectedDate}" name="expectedDate">
			</div>
		</div>
		
		<div class="form-group">
			<label for="edit-accountName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="edit-accountName" placeholder="支持自动补全，输入客户不存在则新建" value="${tran.customerId}" name="customerName">
			</div>
			<label for="edit-transactionStage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
			  <select class="form-control" id="edit-transactionStage" name="stage">
			  	<option></option>
			  	<c:forEach items="${stage}" var="stage">
					<option value="${stage.value}" ${stage.value eq tran.stage? "selected":""}>${stage.text}</option>
				</c:forEach>
			  </select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="edit-transactionType" class="col-sm-2 control-label">类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="edit-transactionType" name="type">
				  <option></option>
				  <c:forEach items="${transactionType}" var="type">
					  <option value="${type.value}" ${type.value eq tran.type ? "selected" : ""}>${type.text}</option>
				  </c:forEach>
				</select>
			</div>
			<label for="edit-possibility" class="col-sm-2 control-label">可能性</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="edit-possibility" value="${tran.possibility}">
			</div>
		</div>
		
		<div class="form-group">
			<label for="edit-clueSource" class="col-sm-2 control-label">来源</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="edit-clueSource" value="${tran.source}" name="source">
				  <option></option>
				 <c:forEach items="${source}" var="source">
					 <option value="${source.value}"  ${source.value eq tran.source ? "selected" : ""}>${source.text}</option>
				 </c:forEach>
				</select>
			</div>
			<label for="edit-activitySrc" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" data-toggle="modal" data-target="#findMarketActivity"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="edit-activitySrc" placeholder="${tran.activityId} 请再次点击确认" readonly>
				<input type="hidden" id="activityId2" name="activityId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="edit-contactsName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" data-toggle="modal" data-target="#findContacts"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="edit-contactsName" placeholder="${tran.contactsId} 请再次点击确认" readonly>
				<input type="hidden" id="contactId" name="contactsId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-describe" class="col-sm-2 control-label">描述</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="edit-describe" name="description">${tran.description}</textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="edit-contactSummary" name="contactSummary">${tran.contactSummary}</textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time2" id="edit-nextContactTime" value="${tran.nextContactTime}" name="nextContactTime">
			</div>
		</div>
		
	</form>
</body>
</html>