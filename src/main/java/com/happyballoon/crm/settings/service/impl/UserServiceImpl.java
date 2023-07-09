package com.happyballoon.crm.settings.service.impl;

import com.happyballoon.crm.exception.LoginException;
import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.settings.mapper.UserMapper;
import com.happyballoon.crm.settings.service.UserService;
import com.happyballoon.crm.utils.DateTimeUtil;
import com.happyballoon.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserMapper userMapper = SqlSessionUtil.getSqlSession().getMapper(UserMapper.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);

        User user = userMapper.login(map);
        if (user == null){
            throw new LoginException("账户密码错误");
        }

        //如果程序能够成功的执行到该行，说明账户密码正确
        //需要继续向下验证其他3项信息

        //验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime)<0){
            throw new LoginException("账户已失效");
        }

        //判断锁定状态
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw new LoginException("账户已锁定");
        }

        //判断ip地址
        String allowIps = user.getAllowIps();
        if (!allowIps.contains(ip)){
            throw new LoginException("ip地址受限");
        }
        return user;
    }

    @Override
    public List<User> getUserList() {

        List<User> uList = userMapper.getUserList();

        return uList;
    }
}
