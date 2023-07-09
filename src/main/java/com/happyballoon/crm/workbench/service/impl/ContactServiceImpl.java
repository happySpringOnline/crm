package com.happyballoon.crm.workbench.service.impl;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.utils.DateTimeUtil;
import com.happyballoon.crm.utils.SqlSessionUtil;
import com.happyballoon.crm.utils.UUIDUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.*;
import com.happyballoon.crm.workbench.mapper.*;
import com.happyballoon.crm.workbench.service.ContactService;

import java.util.List;
import java.util.Map;

public class ContactServiceImpl implements ContactService {

    private ContactsMapper contactsMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsMapper.class);
    private ContactsRemarkMapper contactsRemarkMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkMapper.class);
    private CustomerMapper customerMapper = SqlSessionUtil.getSqlSession().getMapper(CustomerMapper.class);
    private TranMapper tranMapper = SqlSessionUtil.getSqlSession().getMapper(TranMapper.class);
    private ActivityMapper activityMapper = SqlSessionUtil.getSqlSession().getMapper(ActivityMapper.class);
    private ContactsActivityRelationMapper contactsActivityRelationMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationMapper.class);

    @Override
    public List<Contacts> getContactsListByCname(String cname) {
        List<Contacts> cList = contactsMapper.selectContactsListByCname(cname);
        return cList;
    }

    @Override
    public PaginationVO pageList(Map<String, Object> map) {
        int total = contactsMapper.selectCountByConditions(map);
        List<Contacts> dataList = contactsMapper.selectContactsByConditions(map);

        PaginationVO<Contacts> vo = new PaginationVO<Contacts>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public boolean saveContact(Contacts c, String customerName) {
        boolean success = false;
        //查客户名字，没有新建
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if(customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(c.getOwner());
            customer.setName(customerName);
            customer.setCreateBy(c.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(c.getContactSummary());
            customer.setNextContactTime(c.getNextContactTime());
            customer.setDescription(c.getDescription());
            customer.setAddress(c.getAddress());

            int count = customerMapper.insertCustomer(customer);
            if(count==1){
                success= true;
            }
        }
        c.setCustomerId(customer.getId());
        //保存联系人
        int count = contactsMapper.insertContact(c);
        if(count==1){
            success = true;
        }
        return success;
    }

    @Override
    public Contacts selectContactById(String id) {
        Contacts c = contactsMapper.selectContactById(id);
        return c;
    }

    @Override
    public boolean deleteContactByIds(String[] ids) {
        boolean success = false;
        int count = contactsRemarkMapper.selectDelCount(ids);
        int count1 = contactsRemarkMapper.deleteRemarkByIds(ids);
        if (count==count1){
            success = true;
        }

        int count2 = contactsActivityRelationMapper.selectDelCount2(ids);
        int count3 = contactsActivityRelationMapper.deleteContactsRelationByIds2(ids);
        if (count2==count3){
            success = true;
        }

        int count4 = contactsMapper.deleteContactsByIds(ids);
        if(count4==ids.length){
            success = true;
        }
        return success;
    }

    @Override
    public boolean updateContactById(Contacts c, String customerName) {
        boolean success = false;
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if(customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(c.getOwner());
            customer.setName(customerName);
            customer.setCreateBy(c.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(c.getContactSummary());
            customer.setNextContactTime(c.getNextContactTime());
            customer.setDescription(c.getDescription());
            customer.setAddress(c.getAddress());

            int count = customerMapper.insertCustomer(customer);
            if(count==1){
                success= true;
            }
        }
        c.setCustomerId(customer.getId());
        int count = contactsMapper.updateContactById(c);
        if(count==1){
            success= true;
        }
        return success;
    }

    @Override
    public Contacts selectContactById2(String id) {
        Contacts c = contactsMapper.selectContactById2(id);
        return c;
    }

    @Override
    public List<ContactsRemark> selectContactRemarks(String contactId) {
        List<ContactsRemark> contactsRemarkList = contactsRemarkMapper.selectContactRemarksByContactId(contactId);
        return contactsRemarkList;
    }

    @Override
    public boolean insertContactRemark(ContactsRemark contactsRemark) {
        boolean success = false;
        int count = contactsRemarkMapper.insertContactsRemark(contactsRemark);
        if(count==1){
            success = true;
        }
        return success;
    }

    @Override
    public boolean updateContactRemark(Map<String, Object> map) {
        boolean success = false;
        int count = contactsRemarkMapper.updateContactRemark(map);
        if (count==1){
            success = true;
        }
        return success;
    }

    @Override
    public boolean deleteRemarkById(String id) {
        boolean success = false;
        int count = contactsRemarkMapper.deleteRemarkById(id);
        if (count==1){
            success = true;
        }
        return success;
    }

    @Override
    public List<Tran> selectTranListByContactsId(String contactsId) {
        List<Tran> tranList = tranMapper.selectTranListByContactsId(contactsId);

        return tranList;
    }

    @Override
    public List<Activity> selectActivitiesByContactsId(String contactsId) {
        List<String> aIdList = contactsActivityRelationMapper.selectActivityId(contactsId);

        List<Activity> activityList = activityMapper.selectActivitiesByContactsId(aIdList);
        return activityList;
    }

    @Override
    public List<Activity> getActivityListByAname(Map<String, String> map) {
        List<Activity> activityList = activityMapper.selectActivityListByAname3(map);
        return activityList;
    }

    @Override
    public boolean bund(String contactsId, String[] aids) {
        boolean success = false;
        ContactsActivityRelation car = null;
        for (String aid:aids){
            car = new ContactsActivityRelation();
            car.setActivityId(aid);
            car.setContactsId(contactsId);
            car.setId(UUIDUtil.getUUID());

            int count = contactsActivityRelationMapper.insertRelation(car);
            if (count==1){
                success = true;
            }
        }
        return success;
    }

    @Override
    public boolean unbund(Map<String, String> map) {
        boolean success = false;
        int count = contactsActivityRelationMapper.deleteRelation(map);
        if (count==1){
            success = true;
        }
        return success;
    }

    @Override
    public boolean saveContact2(Contacts c) {
        boolean success = false;
        int count = contactsMapper.insertContact(c);
        if(count==1){
            success = true;
        }
        return success;
    }


}
