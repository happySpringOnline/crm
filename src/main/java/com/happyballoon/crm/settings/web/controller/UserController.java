package com.happyballoon.crm.settings.web.controller;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.settings.service.UserService;
import com.happyballoon.crm.settings.service.impl.UserServiceImpl;
import com.happyballoon.crm.utils.MD5Util;
import com.happyballoon.crm.utils.PrintJson;
import com.happyballoon.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("进入用户控制器");

        String path = request.getServletPath();

        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if("/settings/user/xxx.do".equals(path)){
            //xxx(request,response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response){
        System.out.println("进入验证登录操作");

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码的明文形式转换为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接收浏览器端的ip地址
        String ip = request.getRemoteAddr();
        System.out.println("---------ip:"+ip);
        //未来业务层开发，统一使用代理类形态的接口对象
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try {
            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);

            //如果程序执行到此处，说明业务层没有为controller抛出任何的异常
            //表示登录成功
            /*
                {"success":true}
             */
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            e.printStackTrace();

            //一旦程序执行了catch块的信息，说明业务层为我们验证登录失败，为controller抛出异常
            //表示登录失败
            /*
                {"success":true,"msg":"?"}
             */
            String msg = e.getMessage();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",msg);

            PrintJson.printJsonObj(response,map);
        }

    }
}
