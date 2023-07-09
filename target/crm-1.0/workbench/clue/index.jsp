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

		//时间控件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "top-left"
		});

		pageList(1,3)

		$("#qx").click(function () {
			$("input[name=clue]").prop("checked",this.checked);
		})
		/*
			动态生成的元素，我们要以on方法的形式来触发事件
			语法：
				$(需要绑定元素的有效的外层元素).on(绑定事件的方式，需要绑定的元素的jquery对象，回调函数)
		 */
		$("#clueListTbody").on("click",$("input[name=clue]"),function () {
			$("#qx").prop("checked",$("input[name=clue]").length==$("input[name=clue]:checked").length)
		})

		//【创建】线索的模态窗口的所有者铺值
		$("#addBtn").click(function () {
			$.ajax({
				url:"workbench/clue/getUserList.do",
				dataType:"json",
				type:"get",
				success: function (data) {
					var html = "<option></option>";
					$.each(data,function (i,n) {
						html += "<option value='"+n.id+"'>"+n.name+"</option>"
					})

					$("#create-clueOwner").html(html)

					var userId = "${user.id}"
					$("#create-clueOwner").val(userId);

					$("#createClueModal").modal("show")
				}
			})

		//从上下文域对象中取数据字端铺值
		})
		//【保存】按钮绑定事件
		$("#saveBtn").click(function () {
			$.ajax({
				url:"workbench/clue/saveClue.do",
				data:{

					"fullname":$.trim($("#create-surname").val()),
					"appellation":$.trim($("#create-call").val()),
					"owner":$.trim($("#create-clueOwner").val()),
					"company":$.trim($("#create-company").val()),
					"job":$.trim($("#create-job").val()),
					"email":$.trim($("#create-email").val()),
					"phone":$.trim($("#create-phone").val()),
					"website":$.trim($("#create-website").val()),
					"mphone":$.trim($("#create-mphone").val()),
					"state":$.trim($("#create-status").val()),
					"source":$.trim($("#create-source").val()),
					"description":$.trim($("#create-describe").val()),
					"contactSummary":$.trim($("#create-contactSummary").val()),
					"nextContactTime":$.trim($("#create-nextContactTime").val()),
					"address":$.trim($("#create-address").val())

				},
				dataType: "json",
				type: "post",
				success:function (data) {
					//data {"success":true/false}
					if (data){

						//刷新线索信息列表
						pageList(1,3)
						//关闭模态窗口
						$("#createClueModal").modal("hide")

					}else {
						alert("添加线索失败")
					}
				}
			})
		})
        //【搜索】按钮
        $("#search-Btn").click(function (){
            //alert(123)
            //刷新线索信息列表
			//alert($("#search-fullname").val())
            pageList(1,3);
        })
		//【删除】按钮
		$("#deleteBtn").click(function () {
			var $xz = $("input[name=clue]:checked");
			if ($xz.length==0){
				alert("请选择需要删除的记录！")
			}else {
				if (confirm("确定要删除该记录吗？")){
					var param= "";
					for (let i = 0; i < $xz.length; i++) {
						param += "id="+$xz[i].value
						if(i<$xz.length-1){
							param += "&"
						}
					}
					$.ajax({
						url:"workbench/clue/delete.do",
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
		//【修改】
		$("#editBtn").click(function () {
			var $xz = $("input[name=clue]:checked");
			if ($xz.length==0){
				alert("请选择需要删除的记录！")
			}else if($xz.length>1){
				alert("只能选择一条记录进行修改！")
			}else {
				var clueId = $xz.val();
				//给前端铺值
				$.ajax({
					url:"workbench/clue/getUserListAndClue.do",
					data:{
						"id":clueId
					},
					dataType:"JSON",
					type:"POST",
					success:function (data) {
						var html = "<option></option>";
						$.each(data.uList,function (i,n) {
							html += "<option value='"+n.id+"'>"+n.name+"</option>"
						})
						$("#edit-clueOwner").html(html)

						$("#edit-clueId").val(data.clue.id);
						$("#edit-clueOwner").val(data.clue.owner);
						$("#edit-company").val(data.clue.company);
						$("#edit-call").val(data.clue.appellation);
						$("#edit-surname").val(data.clue.fullname);
						$("#edit-job").val(data.clue.job);
						$("#edit-email").val(data.clue.email);
						$("#edit-phone").val(data.clue.phone);
						$("#edit-mphone").val(data.clue.mphone);
						$("#edit-website").val(data.clue.website);
						$("#edit-status").val(data.clue.state);
						$("#edit-source").val(data.clue.source);
						$("#edit-describe").val(data.clue.description);
						$("#edit-contactSummary").val(data.clue.contactSummary);
						$("#edit-nextContactTime").val(data.clue.nextContactTime);
						$("#edit-address").val(data.clue.address);

						$("#editClueModal").modal('show')
					}
				})

			}
		})
		//【更新】
		$("#updateBtn").click(function () {
			$.ajax({
				url:"workbench/clue/update.do",
				data:{
					"id": 				$.trim($("#edit-clueId").val()),
					"owner":			$.trim($("#edit-clueOwner").val()),
					"company":			$.trim($("#edit-company").val()),
					"appellation":		$.trim($("#edit-call").val()),
					"fullname":			$.trim($("#edit-surname").val()),
					"job":				$.trim($("#edit-job").val()),
					"email":			$.trim($("#edit-email").val()),
					"phone":			$.trim($("#edit-phone").val()),
					"mphone":			$.trim($("#edit-mphone").val()),
					"website":			$.trim($("#edit-website").val()),
					"state":			$.trim($("#edit-status").val()),
					"source":			$.trim($("#edit-source").val()),
					"description":		$.trim($("#edit-describe").val()),
					"contactSummary":	$.trim($("#edit-contactSummary").val()),
					"nextContactTime":	$.trim($("#edit-nextContactTime").val()),
					"address":			$.trim($("#edit-address").val())
				},
				dataType:"JSON",
				type:"POST",
				success:function (data) {
					if (data){
						pageList(1,3)
						$("#editClueModal").modal("hide")
					}else {
						alert("更新线索失败！")
					}
				}
				
			})

		})


		
	});
	//【展示线索列表】
	function pageList(pageNo,pageSize) {
		$.ajax({
			url:"workbench/clue/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"fullname":$.trim($("#search-fullname").val()),
				"company":$.trim($("#search-company").val()),
				"phone":$.trim($("#search-phone").val()),
				"source":$.trim($("#search-source").val()),
				"owner":$.trim($("#search-owner").val()),
				"mphone":$.trim($("#search-mphone").val()),
				"state":$.trim($("#search-state").val())
			},
			dataType:"json",
			type:"get",
			success:function (data){
				/*
				data
					{"total":?,"clueList":[{线索信息},{},{}]}
				 */
				var html = "";
				$.each(data.dataList,function (i,n) {
					html += '<tr>';
					html += '	<td><input type="checkbox" name="clue" value="'+n.id+'" /></td>';
					html += '	<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/clue/detail.do?id='+n.id+'\';">'+n.fullname+n.appellation+'</a></td>';
					html += '	<td>'+n.company+'</td>';
					html += '	<td>'+n.phone+'</td>';
					html += '	<td>'+n.mphone+'</td>';
					html += '	<td>'+n.source+'</td>';
					html += '	<td>'+n.owner+'</td>';
					html += '	<td>'+n.state+'</td>';
					html += '</tr>';
				})

				$("#clueListTbody").html(html)

				//计算总页数
				var totalPages =data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1

				$("#cluePage").bs_pagination({
					currentPage:pageNo,//页码
					rowsPerPage:pageSize,//每页显示的记录条数
					maxRowsPerPage: 20,//每页最多显示的记录条数
					totalPages:totalPages,//总页数
					totalRows:data.total,//总记录条数

					visiblePageLinks:4,//显示几个卡片

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

	<!-- 创建线索的模态窗口 -->
	<div class="modal fade" id="createClueModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">创建线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-clueOwner">
								</select>
							</div>
							<label for="create-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-company">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-call">
									<option></option>
									<c:forEach items="${appellation}" var="item">
										<option value="${item.value}">${item.text}</option>
									</c:forEach>
									</option>
								</select>
							</div>
							<label for="create-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-surname">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-job">
							</div>
							<label for="create-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-email">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-phone">
							</div>
							<label for="create-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-website">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-mphone">
							</div>
							<label for="create-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-status">
									<option></option>
									<c:forEach items="${clueState}" var="item">
										<option value="${item.value}">${item.text}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-source">
									<option></option>
									<c:forEach items="${source}" var="item">
										<option value="${item.value}">${item.text}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						

						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">线索描述</label>
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
                                <label for="create-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="create-address"></textarea>
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
	
	<!-- 修改线索的模态窗口 -->
	<div class="modal fade" id="editClueModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">修改线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">

						<input type="hidden" id="edit-clueId">
						<div class="form-group">
							<label for="edit-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-clueOwner">

								</select>
							</div>
							<label for="edit-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-company">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-call">
								  <option></option>
								  <c:forEach items="${appellation}" var="a">
									  <option value="${a.value}">${a.text}</option>
								  </c:forEach>
								</select>
							</div>
							<label for="edit-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-surname">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-job">
							</div>
							<label for="edit-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-email">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-phone" >
							</div>
							<label for="edit-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-website" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-mphone">
							</div>
							<label for="edit-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-status">
								  <option></option>
								  <c:forEach items="${clueState}" var="state">
									  <option value="${state.value}">${state.text}</option>
								  </c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-source">
								  <option></option>
								  <c:forEach items="${source}" var="source">
									  <option value="${source.value}">${source.text}</option>
								  </c:forEach>
								</select>
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
								<label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="edit-contactSummary">这个线索即将被转换</textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control" id="edit-nextContactTime">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="edit-address" class="col-sm-2 control-label">详细地址</label>
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
				<h3>线索列表</h3>
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
				      <input class="form-control" type="text" id="search-fullname">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司</div>
				      <input class="form-control" type="text" id="search-company">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司座机</div>
				      <input class="form-control" type="text" id="search-phone">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索来源</div>
					  <select class="form-control" id="search-source">
					  	  <option></option>
					  	  <c:forEach items="${source}" var="item" >
							  <option value="${item.value}">${item.text}</option>
						  </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>
				  
				  
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">手机</div>
				      <input class="form-control" type="text" id="search-mphone">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索状态</div>
					  <select class="form-control" id="search-state">
					  	<option></option>
					  	<c:forEach items="${clueState}" var="item">
							<option value="${item.value}">${item.text}</option>
						</c:forEach>
					  </select>
				    </div>
				  </div>

				  <button type="button" id="search-Btn" class="btn btn-default" >查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 40px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
				
			</div>
			<div style="position: relative;top: 50px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
							<td>公司</td>
							<td>公司座机</td>
							<td>手机</td>
							<td>线索来源</td>
							<td>所有者</td>
							<td>线索状态</td>
						</tr>
					</thead>
					<tbody id="clueListTbody">
						<%--<tr>
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/clue/detail.jsp';">李四先生</a></td>
							<td>动力节点</td>
							<td>010-84846003</td>
							<td>12345678901</td>
							<td>广告</td>
							<td>zhangsan</td>
							<td>已联系</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/clue/detail.jsp';">李四先生</a></td>
                            <td>动力节点</td>
                            <td>010-84846003</td>
                            <td>12345678901</td>
                            <td>广告</td>
                            <td>zhangsan</td>
                            <td>已联系</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 60px;">
				<div id="cluePage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>