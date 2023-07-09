package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkMapper {
    int selectDelCount(String[] ids);

    int deleteRemark(String[] ids);

    List<ActivityRemark> selectActivityRemarks(String activityId);

    int deleteRemarkById(String id);

    int saveRemark(ActivityRemark ar);

    int updateRemark(ActivityRemark ar);
}
