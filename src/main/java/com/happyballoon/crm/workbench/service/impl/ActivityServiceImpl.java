package com.happyballoon.crm.workbench.service.impl;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.utils.SqlSessionUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Activity;
import com.happyballoon.crm.workbench.domain.ActivityRemark;
import com.happyballoon.crm.workbench.mapper.ActivityMapper;
import com.happyballoon.crm.workbench.mapper.ActivityRemarkMapper;
import com.happyballoon.crm.workbench.mapper.ClueActivityRelationMapper;
import com.happyballoon.crm.workbench.mapper.ContactsActivityRelationMapper;
import com.happyballoon.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    private ActivityMapper activityMapper = SqlSessionUtil.getSqlSession().getMapper(ActivityMapper.class);

    private ActivityRemarkMapper activityRemarkMapper = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkMapper.class);

    private ClueActivityRelationMapper clueActivityRelationMapper = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationMapper.class);

    private ContactsActivityRelationMapper contactsActivityRelationMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationMapper.class);
    @Override
    public boolean saveActivity(Activity a) {
        boolean flag = true;
        int count = activityMapper.saveActivity(a);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(HashMap<String, Object> map) {
        //取得总条数
        int total = activityMapper.selectTotalByCondition(map);
        //取得dataList
        List<Activity> dataList = activityMapper.selectActivitiesByCondition(map);

        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Activity> vo = new PaginationVO<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public boolean deleteActivity(String[] ids) {
        boolean flag = false;

        //查询出需要删除的市场活动备注的数量
        int count1 = activityRemarkMapper.selectDelCount(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = activityRemarkMapper.deleteRemark(ids);

        if (count1 == count2){
            flag = true;
        }

        int count3 = clueActivityRelationMapper.selectDelCount(ids);
        int count4 = clueActivityRelationMapper.deleteClueRelationByIds(ids);

        if (count3==count4){
            flag = true;
        }

        int count5 = contactsActivityRelationMapper.selectDelCount(ids);
        int count6 = contactsActivityRelationMapper.deleteContactsRelationByIds(ids);
        if (count5==count6){
            flag = true;
        }


        //删除市场活动
        int count10 = activityMapper.deleteActivity(ids);
        if (count10 == ids.length){
            flag = true;
        }

        return flag;
    }

    @Override
    public Activity selectActivity(String activityId) {
        Activity a = activityMapper.selectActivityById(activityId);
        return a;
    }

    @Override
    public boolean updateActivity(Activity a) {
        boolean flag = false;
        int count = activityMapper.updateActivity(a);
        if (count==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public Activity selectActivity2(String id) {
        Activity a = activityMapper.selectActivityById2(id);
        return a;
    }

    @Override
    public List<ActivityRemark> selectActivityRemarks(String activityId) {
        List<ActivityRemark> ars = activityRemarkMapper.selectActivityRemarks(activityId);
        return ars;
    }

    @Override
    public boolean deleteRemarkById(String id) {
        boolean flag = false;
        int count = activityRemarkMapper.deleteRemarkById(id);
        if(count==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = false;
        int count = activityRemarkMapper.saveRemark(ar);
        if(count==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = false;
        int count = activityRemarkMapper.updateRemark(ar);
        if (count==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public List<Activity> selectActivitiesByClueId(String id) {
        List<Activity> activityList = activityMapper.selectActivitiesByClueId(id);
        return activityList;
    }

    @Override
    public List<Activity> selectActivityListByAname(Map<String, String> map) {
        List<Activity> aList = activityMapper.selectActivityListByAname(map);
        return aList;
    }

    @Override
    public List<Activity> selectActivityListByAname2(Map<String, String> map) {
        List<Activity> aList = activityMapper.selectActivityListByAname2(map);
        return aList;
    }

    @Override
    public List<Activity> selectAcitivityListByAname(String aname) {
        List<Activity> aList = activityMapper.selectActivityListAllByAname(aname);
        return aList;
    }


}
