package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.workbench.domain.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ActivityMapper {
    int saveActivity(Activity a);

    List<Activity> selectActivitiesByCondition(HashMap<String, Object> map);

    int selectTotalByCondition(HashMap<String, Object> map);

    int deleteActivity(String[] ids);


    List<User> selectUserList(String activityId);

    Activity selectActivityById(String activityId);


    int updateActivity(Activity a);

    Activity selectActivityById2(String id);

    List<Activity> selectActivitiesByClueId(String id);

    List<Activity> selectActivityListByAname(Map<String, String> map);

    List<Activity> selectActivityListByAname2(Map<String, String> map);

    List<Activity> selectActivityListAllByAname(String aname);

    List<Activity> selectActivitiesByContactsId(List aIds);

    List<Activity> selectActivityListByAname3(Map<String, String> map);
}
