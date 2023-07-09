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
	$(function (){
		//时间控件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});
		//【创建】按钮绑定事件，打开添加操作的模态窗口
		//*******************【创建】模态窗口**********************
		$("#addBtn").click(function () {
			/*
				操作模态窗口的方式：
					需要操作的模态窗口的jquery对象，调用modal方法，
					为该方法传递参数 show:打开模态窗口  hide:关闭模态窗口
			 */
			
			//走后台，目的是为了取得用户信息列表，为模态窗口的所有者下拉框铺值
			$.ajax({
				url:"workbench/activity/getUserList.do",
				dataType:"json",
				type:"GET",
				success:function (data){

					/*
						data
							"[{用户1},{},{}]"
					 */
					var html = "<option></option>"
					//遍历data,每一个n就是一个user对象
					$.each(data,function (i,n){
						html += "<option value='"+n.id+"'>"+n.name+"</option>"
					})

					$("#create-Owner").html(html)

					//将当前登录的用户，设置为下拉框默认的选项
					var userId = "${user.id}"
					$("#create-Owner").val(userId)

					//下拉框铺值完毕，展现模态窗口
					$("#createActivityModal").modal("show")
				}
			})
		})

		//***************************************************************************************
		//【保存】按钮绑定事件，执行添加操作
		$("#saveActivityBtn").click(function () {
			$.ajax({
				url:"workbench/activity/saveActivity.do",
				data:{
					"owner":$.trim($("#create-Owner").val()),
					"name":$.trim($("#create-marketActivityName").val()),
					"startDate":$.trim($("#create-startTime").val()),
					"endDate":$.trim($("#create-endTime").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-describe").val())
				},
				dataType: "json",
				type:"POST",
				success:function (data) {
					/*
                        data
                            {"success":true/false}
                     */
					if(data){

						//添加成功后
						//刷新市场活动信息列表（局部刷新）
						//pageList(1,3)
						/*
							$("#activityPage").bs_pagination('getOption', 'currentPage')
								操作后停留在当前页

							$("#activityPage").bs_pagination('getOption', 'rowsPerPage')
								操作后维持已经设置好的每页展现的记录数

							这两个参数不需要我们任何的修改操作
								直接使用即可
						 */

						//做完save操作后，应该回到第一页，维持每页展现的记录数
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//清空添加操作模态窗口中的数据
						/*
                            注意：
                                我们拿到了form表单的jquery对象
                                对于表单的jquery对象，提供了submit()方法让我们提交表单
                                但是表单的jquery对象，没有为我们提供reset()方法让我们重置表单

                                虽然jquery对象没有为我们提供reset方法，但是原生的js为我们提供了reset方法
                                所有我们要将jquery对象转换成原生dom对象

                                jquery对象转换为dom对象
                                    jquery对象[下标]
                                dom对象转换为jquery对象
                                    $(dom)
                         */
						$("#activityAddForm")[0].reset();

						//关闭添加操作的模态窗口
						$("#createActivityModal").modal("hide");
					}else {
						alert("添加市场活动失败！")
					}
				}
			})
		})

		//页面加载完毕之后触发一个方法
		//默认展开列表的第一页，每页展现两条记录
		//(1) 点击左侧菜单中的 “市场活动” 超链接，需要刷新市场活动列表，调用pageList方法
		pageList(1,3);
		//*************************************************************************
		//【查询】按钮绑定事件，触发pageList方法
		//(3) 点击查询按钮的时候，需要刷新市场活动列表，调用pageList方法
		$("#searchBtn").click(function (){

			/*
				点击查询按钮的时候，我们应该将搜索框中的信息保存起来，保存到隐藏域中
			 */
			$("#hidden-activityName").val($("#search-activityName").val())
			$("#hidden-owner").val($("#search-owner").val())
			$("#hidden-startTime").val($("#search-startTime").val())
			$("#hidden-endTime").val($("#search-endTime").val())
			pageList(1,3)
		})

		//为全选的复选框绑定事件，触发全选操作
		$("#qx").click(function (){
			$("input[name=xz]").prop("checked",this.checked)
		})
		//以下这种做法是不行的
		/*$("input[name=xz]").click(function () {
			alert(123)
		})*/
		//因为动态生成的元素，是不能够以普通绑定事件的形式来进行操作的
		/*
			动态生成的元素，我们要以on方法的形式来触发事件

			语法：
				$(需要绑定元素的有效的外层元素).on(绑定事件的方式，需要绑定的元素的jquery对象，回调函数)
		 */
		$("#activityTbody").on("click",$("input[name=xz]"),function () {
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name]:checked").length)
		})

		//【删除】按钮绑定事件，执行市场活动删除操作
		$("#deleteBtn").click(function (){

			//找到复选框中所有挑√的
			var $xz = $("input[name=xz]:checked");
			if($xz.length==0){
				alert("还未选择要删除的记录！")

			}else {

				if(confirm("确定要删除所选择的记录吗")){

					var param = ""
					for (let i = 0; i < $xz.length; i++) {
						param += "id="+$xz[i].value
						//如果元素不是最后一个，需要追加&
						if(i<$xz.length-1){
							param += "&"
						}
					}

					$.ajax({
						url:"workbench/activity/deleteActivity.do",
						data:param,
						dataType:"json",
						type:"post",
						success:function (data){
							if(data){
								//后台删除记录后，此时前端要刷新市场活动信息列表
								//pageList(1,3)
								//回到第一页，维持每页展现的记录数
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							}else {
								alert("删除市场活动失败")
							}
						}
					})
				}

			}

		})

		//【修改】按钮绑定事件，打开修改操作的模态窗口
		$("#editBtn").click(function () {

			var $xz = $("input[name=xz]:checked");

			if ($xz.length == 0) {
				alert("请选择要修改的市场活动记录！")
			}else if($xz.length > 1){
				alert("只能修改一条记录")
			}else {
				var activityId = $xz.val();
				
				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{
						"activityId":activityId
					},
					dataType:"json",
					type:"get",
					success:function (data) {
						var html = "<option></option>"
						$.each(data.uList,function (i,n) {
							html += "<option value='"+n.id+"'>"+n.name+"</option>"
						})

						$("#edit-marketActivityOwner").html(html)

						$("#edit-activityId").val(data.a.id)
						$("#edit-marketActivityOwner").val(data.a.owner)
						$("#edit-marketActivityName").val(data.a.name)
						$("#edit-startTime").val(data.a.startDate)
						$("#edit-endTime").val(data.a.endDate)
						$("#edit-cost").val(data.a.cost)
						$("#edit-describe").val(data.a.description)

						$("#editActivityModal").modal("show")
					}
					
				})
			}
		})

		//【更新】按钮，触发ajax请求
		$("#updateBtn").click(function (){
			$.ajax({
				url:"workbench/activity/updateActivity.do",
				data:{
					"id":$.trim($("#edit-activityId").val()),
					"owner":$.trim($("#edit-marketActivityOwner").val()),
					"name":$.trim($("#edit-marketActivityName").val()),
					"startDate":$.trim($("#edit-startTime").val()),
					"endDate":$.trim($("#edit-endTime").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-describe").val()),
				},
				dataType:"json",
				type:"post",
				success:function (data){
					if(data){
						//成功后刷新市场活动信息列表
						//pageList(1,3)
						/*
							修改操作后，应该维持在当前页，维持每页展现的记录数
						 */
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						//关闭修改的模态窗口
						$("#editActivityModal").modal("hide")
					}else {
						alert("更新市场活动失败！")
					}
				}
			})

		})


	});

	/*
		对于所有的关系型数据库，做前端的分页相关操作的基础组件
		就是pageNo和pageSize
		pageNo:页码
		pageSize:每页展现的记录数

		pageList方法：就是发出ajax到后台，从后台取得最新的市场活动信息列表数据
						通过响应回来的数据，局部刷新市场活动信息列表

		我们都在哪些情况下，需要调用pageList方法（什么情况下需要刷新一下市场活动列表）
		(1) 点击左侧菜单中的 “市场活动” 超链接，需要刷新市场活动列表，调用pageList方法
		(2) 添加、修改、删除后，需要刷新市场活动列表，调用pageList方法
		(3) 点击查询按钮的时候，需要刷新市场活动列表，调用pageList方法
		(4) 点击分页组件的时候，调用pageList方法

		以上为pageList方法制定了六个入口，也就是说，在以上6个操作执行完毕后，
		我们必须调用pageList方法，刷新市场活动列表

	*/
	//展示市场活动信息列表的方法
	function pageList(pageNo,pageSize) {

		//连接后台进行展示的时候，将隐藏域中保存的信息取出来，重写赋予到搜索框中
		$("#search-activityName").val($("#hidden-activityName").val())
		$("#search-owner").val($("#hidden-owner").val())
		$("#search-startTime").val($("#hidden-startTime").val())
		$("#search-endTime").val($("#hidden-endTime").val())

		$.ajax({
			url:"workbench/activity/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"activityName":$.trim($("#search-activityName").val()),
				"owner":$.trim($("#search-owner").val()),
				"startTime":$.trim($("#search-startTime").val()),
				"endTime":$.trim($("#search-endTime").val())
			},
			type:"get",
			dataType:"json",
			success:function (data) {
				/*
					data
						我们需要的：市场活动信息列表
						[{市场活动1},{2},{3}]
						一会分页插件需要的：查询出来的总记录数
						{"total":100}

						{"total":100,"dataList":[{市场活动1},{2},{3}]}
				 */
				var html="";
				
				$.each(data.dataList,function (i,n) {

					html += '<tr class="active">';
					html += '	<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '	<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '	<td>'+n.owner+'</td>';
					html += '	<td>'+n.startDate+'</td>';
					html += '	<td>'+n.endDate+'</td>';
					html += '</tr>';

				})

				$("#activityTbody").html(html)

				//计算总页数
				var totalPages = data.total%pageSize==0 ? data.total/pageSize : parseInt(data.total/pageSize)+1

				//数据处理完毕之后，结合分页查询，对前端展现出分页信息
				$("#activityPage").bs_pagination({
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

	<input type="hidden" id="hidden-activityName"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startTime"/>
	<input type="hidden" id="hidden-endTime"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-Owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<!--
						data-dismiss="modal"
							表示关闭模态窗口
					-->
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveActivityBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

						<div class="form-group">
							<!--修改市场活动的id-->
							<input type="hidden" id="edit-activityId">

							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">

								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startTime">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endTime">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe"></textarea>
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
				<h3>市场活动列表123</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-activityName">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startTime"/>
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endTime">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<!--
						点击创建按钮，观察两个属性和属性值

						data-toggle="modal"
							表示触发该按钮，将要打开一个模态窗口

						data-target="#createActivityModal"
							表示要打开哪个模态窗口，通过#id的形式找到该窗口

						现在我们是以属性和属性值的方式写在了button元素中，用来打开模态窗口
						但是这样做是有问题的：
							问题在于没有办法对按钮的功能进行扩充
					-->
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称123</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityTbody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">

				<div id="activityPage">

				</div>

			</div>
			
		</div>
		
	</div>
</body>
</html>