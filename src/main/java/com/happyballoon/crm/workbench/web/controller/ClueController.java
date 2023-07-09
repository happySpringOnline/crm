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
import com.happyballoon.crm.workbench.domain.Clue;
import com.happyballoon.crm.workbench.domain.ClueRemark;
import com.happyballoon.crm.workbench.domain.Tran;
import com.happyballoon.crm.workbench.service.ActivityService;
import com.happyballoon.crm.workbench.service.ClueService;
import com.happyballoon.crm.workbench.service.impl.ActivityServiceImpl;
import com.happyballoon.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.api.scripting.ScriptUtils.convert;

public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("进入到线索控制器");
        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)) {
            getUserList(request,response);
        } else if ("/workbench/clue/saveClue.do".equals(path)) {
            saveClue(request,response);
        } else if ("/workbench/clue/pageList.do".equals(path)) {
            pageList(request,response);
        }else if ("/workbench/clue/detail.do".equals(path)) {
            detail(request,response);
        }else if ("/workbench/clue/showActivityListByClueId.do".equals(path)) {
            showActivityListByClueId(request,response);
        }else if ("/workbench/clue/unbund.do".equals(path)) {
            unbund(request,response);
        }else if ("/workbench/clue/getActivityListByAname.do".equals(path)) {
            getActivityListByAname(request,response);
        }else if ("/workbench/clue/bund.do".equals(path)) {
            bund(request,response);
        }else if ("/workbench/clue/getActivityListByAname2.do".equals(path)) {
            getActivityListByAname2(request,response);
        }else if ("/workbench/clue/showClueRemarkList.do".equals(path)) {
            showClueRemarkList(request,response);
        }else if ("/workbench/clue/addClueRemark.do".equals(path)) {
            addClueRemark(request,response);
        }else if ("/workbench/clue/deleteClueRemark.do".equals(path)) {
            deleteClueRemark(request,response);
        }else if ("/workbench/clue/convertTran.do".equals(path)) {
            convertTran(request,response);
        }else if ("/workbench/clue/delete.do".equals(path)) {
            deleteClue(request,response);
        }else if ("/workbench/clue/getUserListAndClue.do".equals(path)) {
            getUserListAndClue(request,response);
        }else if ("/workbench/clue/update.do".equals(path)) {
            updateClue(request,response);
        }
    }

    private void updateClue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("更新线索");

        String id = request.getParameter("id");
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue c = new Clue();
        c.setId(id);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setOwner(owner);
        c.setCompany(company);
        c.setJob(job);
        c.setEmail(email);
        c.setPhone(phone);
        c.setWebsite(website);
        c.setMphone(mphone);
        c.setState(state);
        c.setSource(source);
        c.setEditBy(editBy);
        c.setEditTime(editTime);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean success = cs.updateClueById(c);

        PrintJson.printJsonFlag(response,success);
    }

    private void getUserListAndClue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改前端铺值");

        String id = request.getParameter("id");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = cs.selectClueById2(id);

        Map<String, Object> map = new HashMap<>();
        map.put("uList",uList);
        map.put("clue",clue);

        PrintJson.printJsonObj(response,map);
    }

    private void deleteClue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除线索");

        String[] ids = request.getParameterValues("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean success = cs.deleteClueByIds(ids);

        PrintJson.printJsonFlag(response,success);
    }

    private void deleteClueRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除线索备注");

        String id = request.getParameter("remarkId");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean success = cs.deleteClueRemark(id);

        PrintJson.printJsonFlag(response,success);
    }

    private void addClueRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("新增线索备注");

        String noteContent = request.getParameter("noteContent");
        String clueId = request.getParameter("clueId");

        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String createTime = DateTimeUtil.getSysTime();
        String editFlag = "0";

        ClueRemark clueRemark = new ClueRemark();
        clueRemark.setId(UUIDUtil.getUUID());
        clueRemark.setNoteContent(noteContent);
        clueRemark.setCreateBy(createBy);
        clueRemark.setCreateTime(createTime);
        clueRemark.setEditFlag(editFlag);
        clueRemark.setClueId(clueId);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean success = cs.addClueRemark(clueRemark);

        PrintJson.printJsonFlag(response,success);
    }

    private void showClueRemarkList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示线索的备注列表");

        String clueId = request.getParameter("clueId");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        List<ClueRemark> clueRemarkList = cs.showClueRemarkList(clueId);

        PrintJson.printJsonObj(response,clueRemarkList);
    }

    private void convertTran(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("线索转换成交易");

        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");

        Tran tran = null;
        User user = (User) request.getSession().getAttribute("user");

        if ("a".equals(flag)) {
            //交易表单数据处理，创建交易
            String id = UUIDUtil.getUUID();
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String createBy = user.getName();
            String createTime = DateTimeUtil.getSysTime();

            tran = new Tran();

            tran.setId(id);
            tran.setMoney(money);
            tran.setName(name);
            tran.setExpectedDate(expectedDate);
            tran.setStage(stage);
            tran.setActivityId(activityId);
            tran.setCreateBy(createBy);
            tran.setCreateTime(createTime);
        }

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        /*
            为业务层传递的参数

            1、必须传递的参数clueId,有了这个clueId之后我们才知道要转换哪条记录
            2、必须传递的参数t,因为在线索准换的过程中，有可能会临时创建一笔交易（业务层接收的t也有可能是个null）
         */
        boolean success = cs.convertTran(clueId,tran,user);

        if (success){
            //转换成功后重定向到线索模块首页
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
    }

    private void getActivityListByAname2(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("在选择好的线索页面，通过市场活动关键字，查询市场活动");

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");
        Map<String,String> map = new HashMap<String, String>();
        map.put("aname",aname);
        map.put("clueId",clueId);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = as.selectActivityListByAname2(map);

        PrintJson.printJsonObj(response,activityList);
    }


    private void bund(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("关联市场活动模块");

        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.bund(cid,aids);

        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByAname(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("查询该线索尚未关联的市场活动,根据市场活动名字的关键字模糊查找");

        String aname = request.getParameter("aname");
        String cid = request.getParameter("cid");

        Map<String,String> map = new HashMap<String, String>();
        map.put("aname",aname);
        map.put("cid",cid);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.selectActivityListByAname(map);

        PrintJson.printJsonObj(response,aList);

    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("解除关联");

        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.deleteClueRelationById(id);

        PrintJson.printJsonFlag(response,flag);
    }

    private void showActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("展示线索关联的活动列表");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = as.selectActivitiesByClueId(id);

        PrintJson.printJsonObj(response,activityList);
    }


    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入线索详情页");

        String id = request.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue c = cs.selectClueById(id);

        request.setAttribute("c",c);

        //转发
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("线索信息列表展示");

        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.parseInt(pageNoStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        int startIndex = (pageNo-1)*pageSize;

        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String source = request.getParameter("source");
        String owner = request.getParameter("owner");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("startIndex",startIndex);
        map.put("pageSize",pageSize);
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("mphone",mphone);
        map.put("state",state);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        PaginationVO<Clue> vo = cs.pageList(map);

        PrintJson.printJsonObj(response,vo);
    }

    private void saveClue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("线索模块的线索信息保存");

        String id = UUIDUtil.getUUID();
        String fullname=request.getParameter("fullname");
        String appellation=request.getParameter("appellation");
        String owner=request.getParameter("owner");
        String company=request.getParameter("company");
        String job=request.getParameter("job");
        String email=request.getParameter("email");
        String phone=request.getParameter("phone");
        String website=request.getParameter("website");
        String mphone=request.getParameter("mphone");
        String state=request.getParameter("state");
        String source=request.getParameter("source");
        User user = (User) request.getSession().getAttribute("user");
        String createBy= user.getName();
        String createTime = DateTimeUtil.getSysTime();
        String description=request.getParameter("description");
        String contactSummary=request.getParameter("contactSummary");
        String nextContactTime=request.getParameter("nextContactTime");
        String address=request.getParameter("address");

        Clue c = new Clue();
        c.setId(id);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setOwner(owner);
        c.setCompany(company);
        c.setJob(job);
        c.setEmail(email);
        c.setPhone(phone);
        c.setWebsite(website);
        c.setMphone(mphone);
        c.setState(state);
        c.setSource(source);
        c.setCreateBy(createBy);
        c.setCreateTime(createTime);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.saveClue(c);

        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取线索模块的用户list[前端铺值]");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();

        PrintJson.printJsonObj(response,userList);

    }
}
