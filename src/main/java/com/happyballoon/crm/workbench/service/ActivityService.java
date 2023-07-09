package com.happyballoon.crm.workbench.service;

import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Activity;
import com.happyballoon.crm.workbench.domain.ActivityRemark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean saveActivity(Activity a);

    PaginationVO<Activity> pageList(HashMap<String, Object> map);

    boolean deleteActivity(String[] ids);


    Activity selectActivity(String activityId);

    boolean updateActivity(Activity a);

    Activity selectActivity2(String id);

    List<ActivityRemark> selectActivityRemarks(String activityId);

    boolean deleteRemarkById(String id);

    boolean saveRemark(ActivityRemark ar);

    boolean updateRemark(ActivityRemark ar);

    List<Activity> selectActivitiesByClueId(String id);

    List<Activity> selectActivityListByAname(Map<String, String> map);

    List<Activity> selectActivityListByAname2(Map<String, String> map);

    List<Activity> selectAcitivityListByAname(String aname);
}
