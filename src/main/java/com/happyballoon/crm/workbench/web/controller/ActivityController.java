package com.happyballoon.crm.workbench.web.controller;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.settings.service.UserService;
import com.happyballoon.crm.settings.service.impl.UserServiceImpl;
import com.happyballoon.crm.utils.DateTimeUtil;
import com.happyballoon.crm.utils.PrintJson;
import com.happyballoon.crm.utils.ServiceFactory;
import com.happyballoon.crm.utils.UUIDUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Activity;
import com.happyballoon.crm.workbench.domain.ActivityRemark;
import com.happyballoon.crm.workbench.service.ActivityService;
import com.happyballoon.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("进入到市场活动控制器");
        String path = request.getServletPath();

        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/activity/saveActivity.do".equals(path)){
            saveActivity(request,response);
        }else if("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/activity/deleteActivity.do".equals(path)){
            deleteActivity(request,response);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if("/workbench/activity/updateActivity.do".equals(path)){
            updateActivity(request,response);
        }else if("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/activity/showRemarkList.do".equals(path)){
            showRemarkList(request,response);
        }else if("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("修改市场备注信息控制器");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        //修改时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateRemark(ar);

         Map<String,Object> map = new HashMap<String, Object>();
         map.put("flag",flag);
         map.put("ar",ar);

         PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到保存备注信息的控制器");

        String id = UUIDUtil.getUUID();
        String noteContent = request.getParameter("noteContent");
        //创建时间就是系统当前时间
        String createTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String editFlag = "0";
        String activityId = request.getParameter("activityId");

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);
        ar.setActivityId(activityId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveRemark(ar);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("flag",flag);
        map.put("ar",ar);

        PrintJson.printJsonObj(response,map);


    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入删除备注信息的控制器");

        String id = request.getParameter("remarkId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.deleteRemarkById(id);

        PrintJson.printJsonFlag(response,flag);
    }

    private void showRemarkList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到市场活动详情页备注信息的控制器");

        String activityId = request.getParameter("activityId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> ars =  as.selectActivityRemarks(activityId);

        PrintJson.printJsonObj(response,ars);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到展示市场活动详情页的控制器");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Activity a = as.selectActivity2(id);

        request.setAttribute("a",a);

        //转发
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void updateActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入修改市场活动【后端】的控制器");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //修改人：系统当前用户
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        //修改时间：系统当前时间
        String editTime = DateTimeUtil.getSysTime();


        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setEditBy(editBy);
        a.setEditTime(editTime);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateActivity(a);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入修改模态窗口【前端】铺值的控制器");

        String activityId = request.getParameter("activityId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Activity a = as.selectActivity(activityId);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uList",uList);
        map.put("a",a);

        PrintJson.printJsonObj(response,map);
    }

    private void deleteActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入删除市场活动控制器");

        String[] ids = request.getParameterValues("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.deleteActivity(ids);

        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询市场活动信息列表的操作（结合条件查询+分页查询）");

        String activityName=request.getParameter("activityName");
        String owner=request.getParameter("owner");
        String startTime=request.getParameter("startTime");
        String endTime=request.getParameter("endTime");
        String pageNoStr =request.getParameter("pageNo");
        String pageSizeStr =request.getParameter("pageSize");

        //页码
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的市场活动信息数量
        int pageSize = Integer.valueOf(pageSizeStr);
        //开始下标
        int startIndex = (pageNo-1)*pageSize;

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("activityName",activityName);
        map.put("owner",owner);
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        map.put("startIndex",startIndex);
        map.put("pageSize",pageSize);
        /*
            前端要：市场活动信息列表
                    查询的总条数

                    业务层拿到了以上两项信息之后，如果做返回
                    map
                        map.put("dataList":dataList)
                        map.put("total":total)
                        PrintJson map --> json
                        {"total":total,dataList:[{市场活动1},{2},{3}]}

                    vo
                    PaginationVO<T>
                        private int total;
                        private List<T> dataList;

                    PaginationVO<Activity> vo = new PaginationVO<>();
                    vo.setTotal(tatol);
                    vo.setTotal(dataList);
                    PrintJSON vo --> json
                    {"total":total,dataList:[{市场活动1},{2},{3}]}

                    将来分页查询，每个模块都有，所以我们选项使用一个通用vo，操作起来比较方便
         */
        PaginationVO<Activity> vo = as.pageList(map);

        PrintJson.printJsonObj(response,vo);
    }

    private void saveActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动添加操作");
        //调用工具类生成一个32位的uuid
        String id= UUIDUtil.getUUID();
        String owner=request.getParameter("owner");
        String name=request.getParameter("name");
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        String cost=request.getParameter("cost");
        String description=request.getParameter("description");
        //系统当前时间
        String createTime= DateTimeUtil.getSysTime();
        //系统当前用户
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();

        //封装到实体类（activity）里
        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveActivity(a);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获得用户信息列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        PrintJson.printJsonObj(response,uList);
    }
}
