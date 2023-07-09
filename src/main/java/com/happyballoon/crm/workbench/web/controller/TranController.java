package com.happyballoon.crm.workbench.web.controller;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.settings.service.UserService;
import com.happyballoon.crm.settings.service.impl.UserServiceImpl;
import com.happyballoon.crm.utils.DateTimeUtil;
import com.happyballoon.crm.utils.PrintJson;
import com.happyballoon.crm.utils.ServiceFactory;
import com.happyballoon.crm.utils.UUIDUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.*;
import com.happyballoon.crm.workbench.service.*;
import com.happyballoon.crm.workbench.service.impl.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("进入交易模块控制器");

        String servletPath = request.getServletPath();
        if("/workbench/transaction/pageList.do".equals(servletPath)){
            pageList(request,response);
        }else if("/workbench/transaction/open.do".equals(servletPath)){
            open(request,response);
        }else if("/workbench/transaction/getCustomerName.do".equals(servletPath)){
            getCustomerName(request,response);
        }else if("/workbench/transaction/getActivityListByAname.do".equals(servletPath)){
            getActivityListByAname(request,response);
        }else if("/workbench/transaction/getContactsListByCname.do".equals(servletPath)){
            getContactsListByCname(request,response);
        }else if("/workbench/transaction/saveTransaction.do".equals(servletPath)){
            saveTransaction(request,response);
        }else if("/workbench/transaction/detail.do".equals(servletPath)){
            detail(request,response);
        }else if("/workbench/transaction/getTranHistoryByTranId.do".equals(servletPath)){
            getTranHistoryByTranId(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(servletPath)){
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(servletPath)){
            getCharts(request,response);
        }else if("/workbench/transaction/edit.do".equals(servletPath)){
            editTransaction(request,response);
        }else if("/workbench/transaction/update.do".equals(servletPath)){
            updateTransaction(request,response);
        }else if("/workbench/transaction/delete.do".equals(servletPath)){
            deleteTransaction(request,response);
        }
    }

    private void deleteTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("删除交易");

        String[] ids = request.getParameterValues("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean success = ts.deleteTransactionByIds(ids);
        if (success){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    private void updateTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("交易修改保存【后端】");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();
        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setEditBy(editBy);
        tran.setEditTime(editTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean success = ts.updateTransaction(tran,customerName);

        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibilty = pMap.get(tran.getStage());
        tran.setPossibility(possibilty);

        if (success){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    private void editTransaction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("交易修改【前端铺值】");

        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = ts.selectTransactionById(id);

        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(tran.getStage());
        tran.setPossibility(possibility);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();

        request.setAttribute("tran",tran);
        request.setAttribute("uList",uList);

        request.getRequestDispatcher("/workbench/transaction/edit.jsp").forward(request,response);
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示图表");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map = ts.getChart();

        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("改变阶段，更新图标");
        //tran更新操作
        //tranHistory添加操作
        //给前端返回一个tran和success
        String id = request.getParameter("tranId");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran tran = new Tran();
        tran.setId(id);
        tran.setStage(stage);
        tran.setEditBy(editBy);
        tran.setEditTime(editTime);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean success = ts.changeStage(tran);

        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        tran.setPossibility(pMap.get(stage));

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",success);
        map.put("t",tran);

        PrintJson.printJsonObj(response,map);
    }

    private void getTranHistoryByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示交易历史列表");

        String tranId = request.getParameter("tranId");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = ts.selectTranHistoryByTranId(tranId);

        ServletContext application = this.getServletContext();
        Map<String,String> map = (Map<String, String>) application.getAttribute("pMap");
        for (int i = 0; i < thList.size(); i++) {
            TranHistory history = thList.get(i);
            String stage = history.getStage();

            String possibility = map.get(stage);
            history.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,thList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入交易详细展示页面");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = ts.selectTransactionById(id);

        ServletContext application = this.getServletContext();
        String stage = tran.getStage();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(stage);

        tran.setPossibility(possibility);
        System.out.println(tran);
        request.setAttribute("tran",tran);

        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void saveTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("保存交易");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();
        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean success = ts.saveTransaction(tran,customerName);

        if (success){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    private void getContactsListByCname(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据联系人名字关键字模糊查询联系人列表");

        String cname = request.getParameter("cname");
        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        List<Contacts> contactsList = cs.getContactsListByCname(cname);

        PrintJson.printJsonObj(response,contactsList);
    }

    private void getActivityListByAname(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据关键字模糊查询市场活动");

        String aname = request.getParameter("aname");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.selectAcitivityListByAname(aname);

        PrintJson.printJsonObj(response,aList);
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("创建模块客户名称自动补全后端");

        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> uStrList = cs.getCustomerByName(name);

        PrintJson.printJsonObj(response,uStrList);
    }

    private void open(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("交易创建模块前端铺值");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList =  us.getUserList();

        request.setAttribute("uList",uList);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示交易信息列表");

        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");

        int pageNo= Integer.parseInt(pageNoStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        int startIndex = (pageNo-1)*pageSize;

        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String source = request.getParameter("source");
        String contactsName = request.getParameter("contactsName");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("startIndex",startIndex);
        map.put("pageSize",pageSize);
        map.put("owner",owner);
        map.put("name",name);
        map.put("customerName",customerName);
        map.put("stage",stage);
        map.put("source",source);
        map.put("contactsName",contactsName);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        PaginationVO vo = ts.pageList(map);

        System.out.println("--------------------"+vo.getTotal());
        PrintJson.printJsonObj(response,vo);


    }
}
