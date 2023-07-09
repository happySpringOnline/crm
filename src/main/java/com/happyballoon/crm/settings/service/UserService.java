package com.happyballoon.crm.settings.service;

import com.happyballoon.crm.exception.LoginException;
import com.happyballoon.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
