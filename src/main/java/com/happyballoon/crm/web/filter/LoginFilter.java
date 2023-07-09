package com.happyballoon.crm.web.filter;

import com.happyballoon.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("进入到验证有没有登录过的过滤器");

        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String servletPath = request.getServletPath();

        HttpSession session = request.getSession(false);
        if ("/settings/user/login.do".equals(servletPath)
                ||"/login.jsp".equals(servletPath)
                ||"/index.jsp".equals(servletPath)
                ||session!=null && session.getAttribute("user")!=null){
            //放行
            chain.doFilter(request,response);
        }else {
            //拦截
            //重定向
            response.sendRedirect(request.getContextPath()+"/index.jsp");
        }


    }
}
