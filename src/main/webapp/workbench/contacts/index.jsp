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
	<!--引入自动补全插件-->
	<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>
<script type="text/javascript">

	$(function(){
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
		//定制字段
		$("#definedColumns > li").click(function(e) {
			//防止下拉菜单消失
	        e.stopPropagation();
	    });

		pageList(1,3);

		$("#qx").click(function (){
			$("input[name=contact]").prop("checked",this.checked);
		})
		/*
			动态生成的元素，我们要以on方法的形式来触发事件

			语法：
				$(需要绑定元素的有效的外层元素).on(绑定事件的方式，需要绑定的元素的jquery对象，回调函数)
		 */
		$("#contactsTbody").on("click",$("input[name=contact]"),function () {
			$("#qx").prop("checked",$("input[name=contact]").length==$("input[name=contact]:checked").length)
		})

		//【-------------查询--------------】
		$("#searchBtn").click(function(){
			pageList(1,3);
			return false;
		})
		//【-----------创建铺值------------】
		$("#addBtn").click(function(){
			//前端铺值
			$.ajax({
				url:"workbench/contacts/getUserList.do",
				dataType:"JSON",
				type:"GET",
				success:function (data) {
					var html = "<option></option>";
					$.each(data,function (i,n) {
						html += "<option value='"+n.id+"'>"+n.name+"</option>"
					})
					$("#create-contactsOwner").html(html)

					var currentId = "${user.id}"
					$("#create-contactsOwner").val(currentId)
				}
			})
			$("#createContactsModal").modal("show");
		})
		//【---------客户名字自动补全------】
		$("#create-customerName").typeahead({
			source: function (query, process) {
				$.get(
						"workbench/contacts/getCustomerName.do",
						{ "name" : query },
						function (data) {
							process(data);
						},
						"json"
				);
			},
			delay: 500
		});
		//【------------保存--------------】
		$("#saveBtn").click(function(){
			$.ajax({
				url:"workbench/contacts/save.do",
				data:{
					"owner":$.trim($("#create-contactsOwner").val()),
					"source":$.trim($("#create-clueSource").val()),
					"customerName":$.trim($("#create-customerName").val()),
					"fullname":$.trim($("#create-surname").val()),
					"appellation":$.trim($("#create-call").val()),
					"email":$.trim($("#create-email").val()),
					"mphone":$.trim($("#create-mphone").val()),
					"job":$.trim($("#create-job").val()),
					"birth":$.trim($("#create-birth").val()),
					"description":$.trim($("#create-describe").val()),
					"contactSummary":$.trim($("#create-contactSummary1").val()),
					"nextContactTime":$.trim($("#create-nextContactTime1").val()),
					"address":$.trim($("#create-address1").val())
				},
				dataType:"JSON",
				type:"POST",
				success:function (data) {
					if(data){
						pageList(1,3);
						$('#createContactsModal').modal('hide');
					}else {
						alert("添加联系人失败！")
					}
				}
			})

		})
		//【-------------修改铺值------------】
		$("#editBtn").click(function (){
			var $xz = $("input[name=contact]:checked");
			if($xz.length==0){
				alert("请选择需要修改的记录！")
			}else if($xz.length>1){
				alert("只能选择一条记录进行修改！")
			}else{
				var contactId = $xz.val();
				getOwner();
				$.ajax({
					url:"workbench/contacts/getContactById.do",
					data:{
						"id":contactId
					},
					dataType:"JSON",
					type:"GET",
					success:function (data) {
						//在修改页面铺值的时候，需要把联系人的id放到隐藏域
						//方便下一个模块更新使用
						$("#edit-contactId").val(data.id)
						$("#edit-contactsOwner").val(data.owner)
						$("#edit-clueSource1").val(data.source)
						$("#edit-surname").val(data.fullname)
						$("#edit-call").val(data.appellation)
						$("#edit-job").val(data.job)
						$("#edit-mphone").val(data.mphone)
						$("#edit-email").val(data.email)
						$("#edit-birth").val(data.birth)
						$("#edit-customerName").val(data.customerId)
						$("#edit-describe").val(data.description)
						$("#edit-contactSummary").val(data.contactSummary)
						$("#edit-nextContactTime").val(data.nextContactTime)
						$("#edit-address2").val(data.address)

						$("#editContactsModal").modal("show")
					}

				})
			}
		})
		//【------------更新------------】
		$("#updateBtn").click(function () {
			$.ajax({
				url:"workbench/contacts/updateContactById.do",
				data:{
					"id":$("#edit-contactId").val(),
					"owner":$("#edit-contactsOwner").val(),
					"source":$("#edit-clueSource1").val(),
					"fullname":$("#edit-surname").val(),
					"appellation":$("#edit-call").val(),
					"job":$("#edit-job").val(),
					"mphone":$("#edit-mphone").val(),
					"email":$("#edit-email").val(),
					"birth":$("#edit-birth").val(),
					"customerName":$("#edit-customerName").val(),
					"description":$("#edit-describe").val(),
					"contactSummary":$("#edit-contactSummary").val(),
					"nextContactTime":$("#edit-nextContactTime").val(),
					"address":$("#edit-address2").val()
				},
				dataType:"JSON",
				type:"POST",
				success:function (data) {
					if(data){
						pageList(1,3)
						$("#editContactsModal").modal("hide")
					}else {
						alert("联系人信息更新失败！")
					}

				}

			})
		})
		//【-------------删除------------】
		$("#deleteBtn").click(function () {
			var $xz = $("input[name=contact]:checked");
			if($xz.length==0){
				alert("请选择需要删除的记录！")
			}else {
				if(confirm("确定要删除选中的联系人吗")){
					var param = "";
					for (let i = 0; i < $xz.length; i++) {
						param += "id="+$xz[i].value;
						if(i<$xz.length-1){
							param += "&"
						}
					}
					$.ajax({
						url:"workbench/contacts/delete.do",
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

	//前端铺值
	function getOwner(){
		$.ajax({
			url:"workbench/contacts/getUserList.do",
			dataType:"JSON",
			type:"GET",
			success:function (data) {
				var html = "<option></option>";
				$.each(data,function (i,n) {
					html += "<option value='"+n.id+"'>"+n.name+"</option>"
				})
				$("#edit-contactsOwner").html(html)

				var currentId = "${user.id}"
				$("#edit-contactsOwner").val(currentId)
			}
		})
	}
	function pageList(pageNo,pageSize) {
		$.ajax({
			url:"workbench/contacts/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"owner":$.trim($("#search-owner").val()),
				"fullname":$.trim($("#search-fullname").val()),
				"name":$.trim($("#search-company").val()),
				"source":$.trim($("#search-source").val()),
				"birth":$.trim($("#search-birth").val()),
			},
			dataType:"JSON",
			type:"GET",
			success:function (data) {
				var html = "";
				$.each(data.dataList,function (i,n) {
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="contact" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/contacts/detail.do?id='+n.id+'\';">'+n.fullname+'</a></td>';
					html += '<td>'+n.customerId+'</td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.source+'</td>';
					html += '<td>'+n.birth+'</td>';
					html += '</tr>';
				})

				$("#contactsTbody").html(html)

				//计算总页数
				var totalPages = data.total%pageSize==0 ? data.total/pageSize : parseInt(data.total/pageSize)+1

				//数据处理完毕之后，结合分页查询，对前端展现出分页信息
				$("#contactsPage").bs_pagination({
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

	
	<!-- 创建联系人的模态窗口 -->
	<div class="modal fade" id="createContactsModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" onclick="$('#createContactsModal').modal('hide');">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabelx">创建联系人</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" >
					
						<div class="form-group">
							<label for="create-contactsOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-contactsOwner">
								  <option></option>
								</select>
							</div>
							<label for="create-clueSource" class="col-sm-2 control-label">来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-clueSource">
								  <option></option>
								  <c:forEach items="${source}" var="s">
									  <option value="${s.value}">${s.text}</option>
								  </c:forEach>
								</select>
							</div>
						</div>

						<div class="form-group">
							<label for="create-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-surname">
							</div>
							<label for="create-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-call" >
								  <option></option>
								  <c:forEach items="${appellation}" var="a">
									  <option value="${a.value}">${a.text}</option>
								  </c:forEach>
								</select>
							</div>
							
						</div>
						
						<div class="form-group">
							<label for="create-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-job" >
							</div>
							<label for="create-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-mphone" >
							</div>
						</div>
						
						<div class="form-group" style="position: relative;">
							<label for="create-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-email" >
							</div>
							<label for="create-birth" class="col-sm-2 control-label">生日</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time1" id="create-birth">
							</div>
						</div>
						
						<div class="form-group" style="position: relative;">
							<label for="create-customerName" class="col-sm-2 control-label">客户名称</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-customerName" placeholder="支持自动补全，输入客户不存在则新建" >
							</div>
						</div>
						
						<div class="form-group" style="position: relative;">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe" ></textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="create-contactSummary1" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="create-contactSummary1"></textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="create-nextContactTime1" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control time2" id="create-nextContactTime1">
								</div>
							</div>
						</div>

                        <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="edit-address1" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="create-address1"></textarea>
                                </div>
                            </div>
                        </div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改联系人的模态窗口 -->
	<div class="modal fade" id="editContactsModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">修改联系人</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-contactId">
						<div class="form-group">
							<label for="edit-contactsOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-contactsOwner">
								</select>
							</div>
							<label for="edit-clueSource1" class="col-sm-2 control-label">来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-clueSource1">
								  <option></option>
								  <c:forEach items="${source}" var="s">
									  <option value="${s.text}">${s.value}</option>
								  </c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-surname">
							</div>
							<label for="edit-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-call">
								  <option></option>
								  <c:forEach items="${appellation}" var="a">
									  <option value="${a.value}">${a.text}</option>
								  </c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-job">
							</div>
							<label for="edit-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-mphone">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-email">
							</div>
							<label for="edit-birth" class="col-sm-2 control-label">生日</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time1" id="edit-birth">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-customerName" class="col-sm-2 control-label">客户名称</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-customerName" placeholder="支持自动补全，输入客户不存在则新建">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe">这是一条线索的描述信息</textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="edit-contactSummary"></textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control" id="edit-nextContactTime">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="edit-address2" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="edit-address2"></textarea>
                                </div>
                            </div>
                        </div>
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>

	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>联系人列表</h3>
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
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">姓名</div>
				      <input class="form-control" type="text" id="search-fullname">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">客户名称</div>
				      <input class="form-control" type="text" id="search-company">
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">来源</div>
				      <select class="form-control" id="search-source">
						  <option></option>
						  <c:forEach items="${source}" var="s">
							  <option value="${s.value}">${s.text}</option>
						  </c:forEach>
						</select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">生日</div>
				      <input class="form-control time" type="text" id="search-birth">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
			<!--创建/修改/删除-->
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
				
			</div>
			<div style="position: relative;top: 20px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>姓名</td>
							<td>客户名称</td>
							<td>所有者</td>
							<td>来源</td>
							<td>生日</td>
						</tr>
					</thead>
					<tbody id="contactsTbody">


					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 10px;">
				<div id="contactsPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>