package com.happyballoon.crm.settings.mapper;

import com.happyballoon.crm.settings.domain.User;

import java.util.HashMap;
import java.util.List;

public interface UserMapper {

    List<User> getUserList();

    User login(HashMap<String, String> map);
}
