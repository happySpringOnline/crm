package com.happyballoon.crm.workbench.web.controller;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.settings.service.UserService;
import com.happyballoon.crm.settings.service.impl.UserServiceImpl;
import com.happyballoon.crm.utils.DateTimeUtil;
import com.happyballoon.crm.utils.PrintJson;
import com.happyballoon.crm.utils.ServiceFactory;
import com.happyballoon.crm.utils.UUIDUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Contacts;
import com.happyballoon.crm.workbench.domain.Customer;
import com.happyballoon.crm.workbench.domain.CustomerRemark;
import com.happyballoon.crm.workbench.domain.Tran;
import com.happyballoon.crm.workbench.service.ContactService;
import com.happyballoon.crm.workbench.service.CustomerService;
import com.happyballoon.crm.workbench.service.TranService;
import com.happyballoon.crm.workbench.service.impl.ClueServiceImpl;
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

public class CustomerController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入客户模块控制器");

        String servletPath = request.getServletPath();
        if("/workbench/customer/pageList.do".equals(servletPath)){
            pageList(request,response);
        }else if("/workbench/customer/getUserList.do".equals(servletPath)){
            getUserList(request,response);
        }else if("/workbench/customer/saveCustomer.do".equals(servletPath)){
            saveCustomer(request,response);
        }else if("/workbench/customer/getCustomerByIdAndUserList.do".equals(servletPath)){
            getCustomerByIdAndUserList(request,response);
        }else if("/workbench/customer/updateCustomerById.do".equals(servletPath)){
            updateCustomerById(request,response);
        }else if("/workbench/customer/deleteCustomerByIds.do".equals(servletPath)){
            deleteCustomerByIds(request,response);
        }else if("/workbench/customer/detail.do".equals(servletPath)){
            detail(request,response);
        }else if("/workbench/customer/showCustomerRemarks.do".equals(servletPath)){
            showCustomerRemarks(request,response);
        }else if("/workbench/customer/updateRemark.do".equals(servletPath)){
            updateRemark(request,response);
        }else if("/workbench/customer/saveCustomerRemarks.do".equals(servletPath)){
            saveCustomerRemarks(request,response);
        }else if("/workbench/customer/deleteCustomerRemark.do".equals(servletPath)){
            deleteCustomerRemark(request,response);
        }else if("/workbench/customer/showContactsListByCusId.do".equals(servletPath)){
            showContactsListByCusId(request,response);
        }else if("/workbench/customer/saveContact.do".equals(servletPath)){
            saveContact(request,response);
        }else if("/workbench/customer/showTranListByCusId.do".equals(servletPath)){
            showTranListByCusId(request,response);
        }else if("/workbench/customer/addTranByCusId.do".equals(servletPath)){
            addTranByCusId(request,response);
        }
    }

    private void addTranByCusId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("通过客户id新建交易前端铺值");

        String customerId = request.getParameter("customerId");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        Customer c = cs.selectCustomerById(customerId);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();

        request.setAttribute("uList",uList);
        request.setAttribute("customer",c);

        request.getRequestDispatcher("/workbench/customer/save.jsp").forward(request,response);

    }

    private void showTranListByCusId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("通过客户id查询交易");

        String customerId = request.getParameter("customerId");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<Tran>  tranList = ts.selectTransactionByCusId(customerId);

        ServletContext application = this.getServletContext();
        Map<String,String> map = (Map<String, String>) application.getAttribute("pMap");

        for (Tran t:tranList){
            String possibility = map.get(t.getStage());
            t.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,tranList);
    }

    private void saveContact(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("保存联系人");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String source = request.getParameter("source");
        String customerId= request.getParameter("customerId");
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
        c.setCustomerId(customerId);

        ContactService cs = (ContactService) ServiceFactory.getService(new ContactServiceImpl());
        boolean success = cs.saveContact2(c);

        PrintJson.printJsonFlag(response,success);
    }

    private void showContactsListByCusId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("通过客户id查找联系人");

        String customerId = request.getParameter("customerId");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<Contacts> contactsList = cs.selectContactsByCusId(customerId);

        PrintJson.printJsonObj(response,contactsList);
    }

    private void deleteCustomerRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除备注");

        String id = request.getParameter("id");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        boolean success = cs.deleteCustomerRemarkById(id);

        PrintJson.printJsonFlag(response,success);
    }

    private void saveCustomerRemarks(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("保存客户备注");

        String id = UUIDUtil.getUUID();
        String customerId = request.getParameter("customerId");
        String noteContent = request.getParameter("noteContent");
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String createTime = DateTimeUtil.getSysTime();
        String editFlag = "0";

        CustomerRemark customerRemark = new CustomerRemark();
        customerRemark.setId(id);
        customerRemark.setNoteContent(noteContent);
        customerRemark.setCreateBy(createBy);
        customerRemark.setCreateTime(createTime);
        customerRemark.setEditFlag(editFlag);
        customerRemark.setCustomerId(customerId);

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        boolean success = cs.saveCustomerRemark(customerRemark);

        PrintJson.printJsonFlag(response,success);
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("客户备注更新");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        User user = (User) request.getSession().getAttribute("user");
        String editBy= user.getName();
        String editTime = DateTimeUtil.getSysTime();
        String editFlag = "1";

        CustomerRemark cr = new CustomerRemark();
        cr.setId(id);
        cr.setNoteContent(noteContent);
        cr.setEditBy(editBy);
        cr.setEditTime(editTime);
        cr.setEditFlag(editFlag);

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        boolean success = cs.updateCustomerRemark(cr);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("flag",success);
        map.put("cr",cr);

        PrintJson.printJsonObj(response,map);
    }

    private void showCustomerRemarks(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("客户备注展示");

        String id = request.getParameter("id");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<CustomerRemark> customerRemarkList = cs.selectCustomerRemarksById(id);

        PrintJson.printJsonObj(response,customerRemarkList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入详情页");
        String id = request.getParameter("id");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        Customer customer = cs.getCustomerById2(id);

        request.setAttribute("customer",customer);

        request.getRequestDispatcher("/workbench/customer/detail.jsp").forward(request,response);
    }

    private void deleteCustomerByIds(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除客户");
        String[] ids = request.getParameterValues("id");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        boolean success = cs.deleteCustomerByIds(ids);

        PrintJson.printJsonFlag(response,success);
    }

    private void updateCustomerById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改客户信息后端");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String website = request.getParameter("website");
        String phone = request.getParameter("phone");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editTime = DateTimeUtil.getSysTime();

        Customer customer = new Customer();
        customer.setId(id);
        customer.setOwner(owner);
        customer.setName(name);
        customer.setWebsite(website);
        customer.setPhone(phone);
        customer.setDescription(description);
        customer.setContactSummary(contactSummary);
        customer.setNextContactTime(nextContactTime);
        customer.setAddress(address);
        customer.setEditBy(editBy);
        customer.setEditTime(editTime);

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        boolean success = cs.updateCustomerById(customer);

        PrintJson.printJsonFlag(response,success);
    }

    private void getCustomerByIdAndUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改客户信息前端铺值");

        String id = request.getParameter("id");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        Customer c= cs.getCustomerById(id);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("uList",uList);
        map.put("customer",c);

        PrintJson.printJsonObj(response,map);
    }

    private void saveCustomer(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("保存客户信息");

        String id = UUIDUtil.getUUID();
        String owner=request.getParameter("owner");
        String name=request.getParameter("name");
        String website=request.getParameter("website");
        String phone=request.getParameter("phone");
        String contactSummary=request.getParameter("contactSummary");
        String nextContactTime=request.getParameter("nextContactTime");
        String description=request.getParameter("description");
        String address=request.getParameter("address");
        User user = (User) request.getSession().getAttribute("user");
        String createBy=user.getName();
        String createTime= DateTimeUtil.getSysTime();

        Customer customer = new Customer();
        customer.setId(id);
        customer.setOwner(owner);
        customer.setName(name);
        customer.setWebsite(website);
        customer.setPhone(phone);
        customer.setContactSummary(contactSummary);
        customer.setNextContactTime(nextContactTime);
        customer.setDescription(description);
        customer.setAddress(address);
        customer.setCreateTime(createTime);
        customer.setCreateBy(createBy);

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        boolean success = cs.saveCustomer(customer);

        PrintJson.printJsonObj(response,success);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("创建客户前端铺值");

        UserService cs = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = cs.getUserList();

        PrintJson.printJsonObj(response,userList);

    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示客户列表");

        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");

        int pageNo = Integer.parseInt(pageNoStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        int startIndex = (pageNo-1)*pageSize;

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("startIndex",startIndex);
        map.put("pageSize",pageSize);
        map.put("name",name);
        map.put("owner",owner);
        map.put("phone",phone);
        map.put("website",website);

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        PaginationVO<Customer> vo = cs.pageList(map);

        PrintJson.printJsonObj(response,vo);
    }
}
