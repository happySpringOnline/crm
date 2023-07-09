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

		//定制字段
		$("#definedColumns > li").click(function(e) {
			//防止下拉菜单消失
	        e.stopPropagation();
	    });

		//时间控件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "top-left"
		});

		//【创建铺值】
		$("#addBtn").click(function () {
			$.ajax({
				url:"workbench/customer/getUserList.do",
				dataType: "json",
				type: "get",
				success:function (data) {
					var html = "<option></option>";
					$.each(data,function (i,n) {
						html += '<option value="'+n.id+'">'+n.name+'</option>'
					})
					$("#create-customerOwner").html(html)
					//将当前登录的用户，设置为下拉框默认的选项
					var userId = "${user.id}"
					$("#create-customerOwner").val(userId)
                    //铺值完毕，打开模态窗口
                    $("#createCustomerModal").modal("show")
				}
			})
		})

		//【保存】
		$("#saveCustomerBtn").click(function () {
			$.ajax({
				url:"workbench/customer/saveCustomer.do",
				data:{
					"owner":$.trim($("#create-customerOwner").val()),
					"name":$.trim($("#create-customerName").val()),
					"website":$.trim($("#create-website").val()),
					"phone":$.trim($("#create-phone").val()),
					"description":$.trim($("#create-describe").val()),
					"nextContactTime":$.trim($("#create-nextContactTime").val()),
					"address":$.trim($("#create-address1").val()),
					"contactSummary":$.trim($("#create-contactSummary").val())
				},
				dataType:"json",
				type:"post",
				success:function (data) {
					if(data){
						//刷新客户列表
						pageList(1,3)
						//关闭模态窗口
						$("#createCustomerModal").modal("hide")
					}else {
						alert("添加客户信息失败！")
					}
				}
			})
		})

		//【全选】
		$("#qx").click(function () {
			$("input[name=xz]").prop("checked",this.checked)
		})
		$("#customerTBody").on("click",$("input[name=xz]"),function () {
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length)
		})

		//【搜索】
		$("#searchBtn").click(function () {
			pageList(1,3)
			return false;
		})

		//【修改】
		$("#editBtn").click(function () {
			//所有者铺值 & 客户信息铺值
			var $xz = $("input[name=xz]:checked");
			if($xz.length==0){
				alert("请选择需要修改的记录！")
			}else if($xz.length>1){
				alert("只能选择一条记录修改！")
			}else {
				var customerId = $xz.val();
				$.ajax({
					url:"workbench/customer/getCustomerByIdAndUserList.do",
					data:{
						"id":customerId
					},
					dataType:"JSON",
					type:"get",
					success:function (data) {
						var html = "<option></option>";
						$.each(data.uList,function (i,n) {
							html += '<option value="'+n.id+'">'+n.name+'</option>'
						})
						$("#edit-customerOwner").html(html)
						/**
						 * 需要建立一个隐藏域，保存返回来的客户的id,方便下一步更新模块的使用
						 */
						$("#edit-customerId").val(data.customer.id)
						$("#edit-customerOwner").val(data.customer.owner)
						$("#edit-customerName").val(data.customer.name)
						$("#edit-website").val(data.customer.website)
						$("#edit-phone").val(data.customer.phone)
						$("#edit-describe").val(data.customer.description)
						$("#edit-contactSummary1").val(data.customer.contactSummary)
						$("#edit-nextContactTime2").val(data.customer.nextContactTime)
						$("#edit-address").val(data.customer.address)

						$("#editCustomerModal").modal("show")
					}
				})
			}
		})

		//【更新】
		$("#updateBtn").click(function () {
			$.ajax({
				url:"workbench/customer/updateCustomerById.do",
				data:{
					"id":$("#edit-customerId").val(),
					"owner":$("#edit-customerOwner").val(),
					"name":$("#edit-customerName").val(),
					"website":$("#edit-website").val(),
					"phone":$("#edit-phone").val(),
					"description":$("#edit-describe").val(),
					"contactSummary":$("#edit-contactSummary1").val(),
					"nextContactTime":$("#edit-nextContactTime2").val(),
					"address":$("#edit-address").val()
				},
				dataType:"json",
				type:"post",
				success:function (data) {
					if (data){
						pageList(1,3);

						$("#editCustomerModal").modal("hide")
					}else {
						alert("修改失败")
					}
				}
			})
		})

		//【删除】
		$("#deleteBtn").click(function () {
			var $xz = $("input[name=xz]:checked");
			if($xz.length==0){
				alert("请选择需要删除的记录")
			}else {
				if(confirm("确定要删除这条记录吗？")){
					var param = ''
					for (let i = 0; i < $xz.length; i++) {
						var customerId = $($xz[i]).val()
						alert($($xz[i]).val())
						param += "id="+customerId
						if(i<$xz.length-1){
							param += "&"
						}
					}
					$.ajax({
						url:"workbench/customer/deleteCustomerByIds.do",
						data: param,
						dataType:"json",
						type:"post",
						success:function (data) {
							if(data){
								pageList(1,3)
							}else {
								alert("删除失败")
							}
						}
					})
				}

			}
		})
	});

	//展示客户信息的方法
	function pageList(pageNo,pageSize) {
		$.ajax({
			url:"workbench/customer/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-customerName").val()),
				"owner":$.trim($("#search-owner").val()),
				"phone":$.trim($("#search-phone").val()),
				"website":$.trim($("#search-website").val()),
			},
			dataType:"JSON",
			type:"GET",
			success:function (data) {
				var html = "";
				$.each(data.dataList,function (i,n) {
					html += '<tr>';
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/customer/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.phone+'</td>';
					html += '<td>'+n.website+'</td>';
					html += '</tr>';
				})
				$("#customerTBody").html(html)

				var totalPages = data.total%pageSize== 0 ? parseInt(data.total/pageSize) : parseInt(data.total/pageSize)+1
				//数据处理完毕之后，结合分页查询，对前端展现出分页信息
				$("#customerPage").bs_pagination({
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

	<!-- 创建客户的模态窗口 -->
	<div class="modal fade" id="createCustomerModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建客户</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-customerOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-customerOwner">
								</select>
							</div>
							<label for="create-customerName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-customerName">
							</div>
						</div>
						
						<div class="form-group">
                            <label for="create-website" class="col-sm-2 control-label">公司网站</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-website">
                            </div>
							<label for="create-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-phone">
							</div>
						</div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>

                        <div style="position: relative;top: 15px;">
                            <div class="form-group">
                                <label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="3" id="create-contactSummary"></textarea>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
                                <div class="col-sm-10" style="width: 300px;">
                                    <input type="text" class="form-control time" id="create-nextContactTime">
                                </div>
                            </div>
                        </div>

                        <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="create-address1" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="create-address1"></textarea>
                                </div>
                            </div>
                        </div>
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveCustomerBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改客户的模态窗口 -->
	<div class="modal fade" id="editCustomerModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">修改客户</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
					
						<div class="form-group">

							<!--修改客户的id-->
							<input type="hidden" id="edit-customerId">

							<label for="edit-customerOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-customerOwner">
								  
								</select>
							</div>
							<label for="edit-customerName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-customerName">
							</div>
						</div>
						
						<div class="form-group">
                            <label for="edit-website" class="col-sm-2 control-label">公司网站</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-website" value="http://www.bjpowernode.com">
                            </div>
							<label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-phone">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe"></textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>

                        <div style="position: relative;top: 15px;">
                            <div class="form-group">
                                <label for="create-contactSummary1" class="col-sm-2 control-label">联系纪要</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="3" id="edit-contactSummary1"></textarea>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="create-nextContactTime2" class="col-sm-2 control-label">下次联系时间</label>
                                <div class="col-sm-10" style="width: 300px;">
                                    <input type="text" class="form-control time" id="edit-nextContactTime2">
                                </div>
                            </div>
                        </div>

                        <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="create-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="edit-address"></textarea>
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
				<h3>客户列表</h3>
			</div>
		</div>
	</div>
	
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
	
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">

			<!--查询模块-->
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" id="search-customerName" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" id="search-owner" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司座机</div>
				      <input class="form-control" id="search-phone" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司网站</div>
				      <input class="form-control" id="search-website" type="text">
				    </div>
				  </div>
				  
				  <button type="submit" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<!--创建/修改/删除-->
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span>修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
							<td>所有者</td>
							<td>公司座机</td>
							<td>公司网站</td>
						</tr>
					</thead>
					<tbody id="customerTBody">


					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="customerPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>