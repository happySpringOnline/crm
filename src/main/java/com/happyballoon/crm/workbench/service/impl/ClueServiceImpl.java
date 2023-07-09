package com.happyballoon.crm.workbench.service.impl;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.utils.SqlSessionUtil;
import com.happyballoon.crm.utils.UUIDUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.*;
import com.happyballoon.crm.workbench.mapper.*;
import com.happyballoon.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {

    private ClueMapper clueMapper = SqlSessionUtil.getSqlSession().getMapper(ClueMapper.class);
    private ClueRemarkMapper clueRemarkMapper = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkMapper.class);
    private ClueActivityRelationMapper clueActivityRelationMapper = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationMapper.class);
    private CustomerMapper customerMapper = SqlSessionUtil.getSqlSession().getMapper(CustomerMapper.class);
    private CustomerRemarkMapper customerRemarkMapper = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkMapper.class);
    private ContactsMapper contactsMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsMapper.class);
    private ContactsRemarkMapper contactsRemarkMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkMapper.class);
    private ContactsActivityRelationMapper contactsActivityRelationMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationMapper.class);
    private TranMapper tranMapper = SqlSessionUtil.getSqlSession().getMapper(TranMapper.class);
    private TranHistoryMapper tranHistoryMapper = SqlSessionUtil.getSqlSession().getMapper(TranHistoryMapper.class);


    @Override
    public boolean saveClue(Clue c) {
        boolean flag = false;
        int count = clueMapper.insertClue(c);
        if (count==1){
            flag = true;
        }
        return flag;

    }

    @Override
    public PaginationVO<Clue> pageList(Map<String, Object> map) {
        int total = clueMapper.selectClueCount(map);
        List<Clue> clueList = clueMapper.selectClueByConditions(map);

        PaginationVO vo = new PaginationVO<Clue>();
        vo.setTotal(total);
        vo.setDataList(clueList);

        return vo;
    }

    @Override
    public Clue selectClueById(String id) {
        Clue c = clueMapper.selectClueById(id);

        return c;
    }

    @Override
    public boolean deleteClueRelationById(String id) {
        boolean flag = false;
        int count = clueActivityRelationMapper.deleteClueRelationById(id);
        if (count==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag = false;
        for(String aid:aids){

            ClueActivityRelation car = new ClueActivityRelation();
            String id = UUIDUtil.getUUID();
            car.setId(id);
            car.setActivityId(aid);
            car.setClueId(cid);

            int count = clueActivityRelationMapper.insertByBund(car);
            if(count==1){
                flag=true;
            }
        }
        return flag;
    }

    @Override
    public List<ClueRemark> showClueRemarkList(String clueId) {
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkByClueId(clueId);
        return clueRemarkList;
    }

    @Override
    public boolean addClueRemark(ClueRemark clueRemark) {
        boolean flag = false;
        int count = clueRemarkMapper.insertClueRemark(clueRemark);
        if(count==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean deleteClueRemark(String id) {
        boolean flag = false;
        int count = clueRemarkMapper.deleteClueRemarkById(id);
        if(count==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean deleteClueByIds(String[] ids) {
        boolean success = false;
        //查询出需要删除的线索备注的数量
        int count = clueRemarkMapper.selectDelCount(ids);
        //删除备注，返回受到影响的条数（实际删除的数量）
        int count1 = clueRemarkMapper.deleteClueRemarkByIds(ids);
        if (count==count1){
            success = true;
        }

        int count2 = clueActivityRelationMapper.selectDelCount2(ids);
        int coun3 = clueActivityRelationMapper.deleteClueRelationByIds2(ids);
        if (count2==coun3){
            success = true;
        }

        int count4 = clueMapper.deleteClueByIds(ids);
        if (count4==ids.length){
            success = true;
        }
        return success;
    }

    @Override
    public Clue selectClueById2(String id) {
        Clue clue = clueMapper.selectClueById2(id);
        return clue;
    }

    @Override
    public boolean updateClueById(Clue c) {
        boolean success = false;
        int count = clueMapper.updateClueById(c);
        if (count==1){
            success = false;
        }
        return success;
    }

    @Override
    public boolean convertTran(String clueId, Tran tran, User user) {
        boolean flag = false;

        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueMapper.selectClueById2(clueId);

        //(2) 通过线索对象提取客户信息，
        // 当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer customer = customerMapper.selectCustomerByName(company);
        //如果customer为null,说明以前没有这个客户，需要新建一个
        if(customer==null){
            customer = new Customer();
            //从clue里面取过来就行
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(clue.getOwner());
            customer.setName(clue.getCompany());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setCreateBy(clue.getCreateBy());
            customer.setCreateTime(clue.getCreateTime());
            customer.setContactSummary(clue.getContactSummary());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setDescription(clue.getDescription());
            customer.setAddress(clue.getAddress());
            //添加客户
            int count = customerMapper.insertCustomer(customer);
            if (count==1){
                flag = true;
            }
        }

        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setOwner(clue.getOwner());
        contacts.setSource(clue.getSource());
        contacts.setCustomerId(customer.getId());
        contacts.setFullname(clue.getFullname());
        contacts.setAppellation(clue.getAppellation());
        contacts.setEmail(clue.getEmail());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setCreateBy(clue.getCreateBy());
        contacts.setCreateTime(clue.getCreateTime());
        contacts.setDescription(clue.getDescription());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setAddress(clue.getAddress());
        //添加联系人
        int count1 = contactsMapper.insertContact(contacts);
        if (count1==1){
            flag=true;
        }

        //(4) 线索备注转换到客户备注以及联系人备注
        //查询出与线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkByClueId(clueId);
        //取出每一条线索的备注
        for(ClueRemark clueRemark:clueRemarkList){

            //取出备注信息（主要转换到客户备注和联系人备注的就是这个备注信息）
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注，把信息存入客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setCreateBy(clueRemark.getCreateBy());
            customerRemark.setCreateTime(clueRemark.getCreateTime());
            customerRemark.setEditBy(clueRemark.getEditBy());
            customerRemark.setEditTime(clueRemark.getEditTime());
            customerRemark.setEditFlag(clueRemark.getEditFlag());
            customerRemark.setCustomerId(customer.getId());

            int count2 = customerRemarkMapper.insertCustomerRemark(customerRemark);
            if(count2==1){
                flag = true;
            }

            //创建联系人备注，把信息存入联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setCreateBy(clueRemark.getCreateBy());
            contactsRemark.setCreateTime(clueRemark.getCreateTime());
            contactsRemark.setEditBy(clueRemark.getEditBy());
            contactsRemark.setEditTime(clueRemark.getEditTime());
            contactsRemark.setEditFlag(clueRemark.getEditFlag());
            contactsRemark.setContactsId(contacts.getId());

            int count3 = contactsRemarkMapper.insertContactsRemark(contactsRemark);
            if(count3==1){
                flag=true;
            }
        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //查询出与该条线索关联的市场活动，创建与该市场活动相关联的联系人关系表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationMapper.selectClueActivityRelationList(clueId);

        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            String activityId = clueActivityRelation.getActivityId();

            //创建与该市场活动相关联的联系人关系表
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(activityId);

            int count4 = contactsActivityRelationMapper.insertRelation(contactsActivityRelation);
            if(count4==1){
                flag = true;
            }
        }

        //(6) 如果有创建交易需求，创建一条交易
        if(tran!=null){
            /*
                t对象在controller里面已经封装好的信息如下：
                    id,money,name,expectedDate,stage,activityId,createBy,createTime

                 接下来可以通过第一步查出来的clue对象，取出一些信息，继续完善对tran对象的封装
             */
            tran.setOwner(clue.getOwner());
            tran.setSource(clue.getSource());
            tran.setType("新业务");
            tran.setContactsId(contacts.getId());
            tran.setCustomerId(customer.getId());
            tran.setDescription(clue.getDescription());
            tran.setContactSummary(clue.getContactSummary());
            tran.setNextContactTime(clue.getNextContactTime());
            //添加交易
            int count5 =  tranMapper.insertTran(tran);
            if(count5 == 1){
                flag = true;
            }

            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setStage(tran.getStage());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setCreateTime(tran.getCreateTime());
            tranHistory.setCreateBy(tran.getCreateBy());
            tranHistory.setTranId(tran.getId());

            int count6 = tranHistoryMapper.insertTranHistory(tranHistory);
            if(count6 == 1){
                flag = true;
            }
        }

        //(8) 删除线索备注
        for(ClueRemark clueRemark:clueRemarkList){
            int count7 = clueRemarkMapper.deleteClueRemarkById(clueRemark.getId());
            if(count7==1){
                flag = true;
            }
        }
        //(9) 删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            int count8 = clueActivityRelationMapper.deleteClueRelationById(clueActivityRelation.getId());
            if(count8==1){
                flag = true;
            }
        }

        //(10) 删除线索
        int count9 = clueMapper.deleteClueById(clueId);
        if(count9==1){
            flag = true;
        }



        return flag;
    }


}
