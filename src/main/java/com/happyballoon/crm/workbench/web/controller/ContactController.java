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
import com.happyballoon.crm.workbench.service.ContactService;
import com.happyballoon.crm.workbench.service.CustomerService;
import com.happyballoon.crm.workbench.service.TranService;
import com.happyballoon.crm.workbench.service.impl.ContactServiceImpl;
import com.happyballoon.crm.workbench.service.impl.CustomerServiceImpl;
import com.happyballoon.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("进入联系人模块控制器");
        String servletPath = request.getServletPath();

        if ("/workbench/contacts/pageList.do".equals(servletPath)){
            pageList(request,response);
        }else if("/workbench/contacts/getUserList.do".equals(servletPath)){
            getUserList(request,response);
        }else if("/workbench/contacts/save.do".equals(servletPath)){
            saveContact(request,response);
        }else if("/workbench/contacts/getCustomerName.do".equals(servletPath)){
            getCustomerName(request,response);
        }else if("/workbench/contacts/getContactById.do".equals(servletPath)){
            getContactById(request,response);
        }else if("/workbench/contacts/delete.do".equals(servletPath)){
            deleteContactByIds(request,response);
        }else if("/workbench/contacts/updateContactById.do".equals(servletPath)){
            updateContactById(request,response);
        }else if("/workbench/contacts/detail.do".equals(servletPath)){
            showDetailById(request,response);
        }else if("/workbench/contacts/showContactsRemarksById.do".equals(servletPath)){
            showContactsRemarksById(request,response);
        }else if("/workbench/contacts/addRemark.do".equals(servletPath)){
            addRemark(request,response);
        }else if("/workbench/contacts/updateRemark.do".equals(servletPath)){
            updateRemark(request,response);
        }else if("/workbench/contacts/deleteRemark.do".equals(servletPath)){
            deleteRemark(request,response);
        }else if("/workbench/contacts/showTranByContactId.do".equals(servletPath)){
            showTranByContactId(request,response);
        }else if("/workbench/contacts/showActivityByContactId.do".equals(servletPath)){
            showActivityByContactId(request,response);
        }else if("/workbench/contacts/getActivityListByAname.do".equals(servletPath)){
            getActivityListByAname(request,response);
        }else if("/workbench/contacts/bund.do".equals(servletPath)){
            bund(request,response);
        }else if("/workbench/contacts/unbund.do".equals(servletPath)){
            unbund(request,response);
        }else if("/workbench/contacts/deleteTran.do".equals(servletPath)){
            deleteTran(request,response);
        }else if("/workbench/contacts/open.do".equals(servletPath)){
            openTran(request,response);
        }else if("/workbench/contacts/saveTransaction.do".equals(servletPath)){
            saveTransaction(request,response);
        }
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
            response.sendRedirect(request.getContextPath()+"/workbench/contacts/index.jsp");
        }
    }

    private void openTran(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("交易创建模块前端铺值");

        String id = request.getParameter("contactId");
        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        Contacts contact = cs.selectContactById(id);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList =  us.getUserList();

        request.setAttribute("uList",uList);
        request.setAttribute("contact",contact);

        request.getRequestDispatcher("/workbench/contacts/save.jsp").forward(request,response);
    }

    private void deleteTran(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除联系人相关联的交易");
        String tid = request.getParameter("tid");

        String[] id = {tid};

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean success = ts.deleteTransactionByIds(id);

        PrintJson.printJsonFlag(response,success);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("解除联系人和市场活动关联");

        String cid = request.getParameter("cid");
        String aid = request.getParameter("aid");

        Map<String,String> map = new HashMap<String, String>();
        map.put("cid",cid);
        map.put("aid",aid);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.unbund(map);

        PrintJson.printJsonFlag(response,success);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("关联市场活动和联系人");

        String contactsId = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.bund(contactsId,aids);

        PrintJson.printJsonObj(response,success);
    }

    private void getActivityListByAname(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据关键字模糊查询市场活动");

        String aname = request.getParameter("aname");
        String contactsId = request.getParameter("contactsId");

        Map<String,String> map = new HashMap<String, String>();
        map.put("aname",aname);
        map.put("contactsId",contactsId);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        List<Activity> aList = cs.getActivityListByAname(map);

        PrintJson.printJsonObj(response,aList);
    }

    private void showActivityByContactId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示联系人相关联的市场活动");

        String contactsId = request.getParameter("contactsId");
        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        List<Activity> activityList = cs.selectActivitiesByContactsId(contactsId);

        System.out.println(activityList);

        PrintJson.printJsonObj(response,activityList);
    }

    private void showTranByContactId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("显示联系人相关的交易列表");

        String contactsId = request.getParameter("contactsId");
        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        List<Tran> tranList = cs.selectTranListByContactsId(contactsId);

        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");

        for (Tran t:tranList){
            String possibility = pMap.get(t.getStage());
            t.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,tranList);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除备注");

        String id = request.getParameter("id");
        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.deleteRemarkById(id);

        PrintJson.printJsonFlag(response,success);
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("更新备注");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editTime = DateTimeUtil.getSysTime();
        String editFlag = "1";

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("id",id);
        map.put("noteContent",noteContent);
        map.put("editBy",editBy);
        map.put("editTime",editTime);
        map.put("editFlag",editFlag);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.updateContactRemark(map);

        PrintJson.printJsonObj(response,success);
    }

    private void addRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("添加备注");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        ContactsRemark contactsRemark = new ContactsRemark();
        contactsRemark.setId(UUIDUtil.getUUID());
        contactsRemark.setNoteContent(noteContent);
        User user = (User) request.getSession().getAttribute("user");
        contactsRemark.setCreateBy(user.getName());
        contactsRemark.setCreateTime(DateTimeUtil.getSysTime());
        contactsRemark.setEditFlag("0");
        contactsRemark.setContactsId(id);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.insertContactRemark(contactsRemark);

        PrintJson.printJsonObj(response,success);
    }

    private void showContactsRemarksById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("联系人备注展示");

        String contactId = request.getParameter("id");
        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        List<ContactsRemark> crList =cs.selectContactRemarks(contactId);

        PrintJson.printJsonObj(response,crList);
    }

    private void showDetailById(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("联系人详情页");

        String id = request.getParameter("id");

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        Contacts c = cs.selectContactById2(id);

        request.setAttribute("contact",c);
        request.getRequestDispatcher("/workbench/contacts/detail.jsp").forward(request,response);
    }

    private void updateContactById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("更新联系人信息");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String source = request.getParameter("source");
        String customerName = request.getParameter("customerName");
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String email = request.getParameter("email");
        String mphone = request.getParameter("mphone");
        String job = request.getParameter("job");
        String birth = request.getParameter("birth");
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Contacts c = new Contacts();
        c.setId(id);
        c.setOwner(owner);
        c.setSource(source);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setEmail(email);
        c.setMphone(mphone);
        c.setJob(job);
        c.setBirth(birth);
        c.setEditBy(editBy);
        c.setEditTime(editTime);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.updateContactById(c,customerName);

        PrintJson.printJsonFlag(response,success);

    }

    private void deleteContactByIds(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("删除联系人");

        String[] ids = request.getParameterValues("id");
        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.deleteContactByIds(ids);

        PrintJson.printJsonFlag(response,success);
    }

    private void getContactById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("联系人修改前端铺值");

        String id = request.getParameter("id");
        ContactService  cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        Contacts c = cs.selectContactById(id);

        PrintJson.printJsonObj(response,c);
    }

    private void saveContact(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("保存联系人");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String source = request.getParameter("source");
        String customerName = request.getParameter("customerName");
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String email = request.getParameter("email");
        String mphone = request.getParameter("mphone");
        String job = request.getParameter("job");
        String birth = request.getParameter("birth");
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Contacts c = new Contacts();
        c.setId(id);
        c.setOwner(owner);
        c.setSource(source);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setEmail(email);
        c.setMphone(mphone);
        c.setJob(job);
        c.setBirth(birth);
        c.setCreateBy(createBy);
        c.setCreateTime(createTime);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.saveContact(c,customerName);

        PrintJson.printJsonObj(response,success);
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("客户名称自动补全");

        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> cusNameList  = cs.getCustomerByName(name);

        PrintJson.printJsonObj(response,cusNameList);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取用户列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();

        PrintJson.printJsonObj(response,uList);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示联系人的信息列表");

        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.parseInt(pageNoStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        int startIndex = (pageNo-1)*pageSize;

        String owner = request.getParameter("owner");
        String fullname = request.getParameter("fullname");
        String name = request.getParameter("name");
        String source = request.getParameter("source");
        String birth = request.getParameter("birth");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("startIndex",startIndex);
        map.put("pageSize",pageSize);
        map.put("owner",owner);
        map.put("fullname",fullname);
        map.put("name",name);
        map.put("source",source);
        map.put("birth",birth);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        PaginationVO<Contacts> vo = cs.pageList(map);

        PrintJson.printJsonObj(response,vo);

    }
}
