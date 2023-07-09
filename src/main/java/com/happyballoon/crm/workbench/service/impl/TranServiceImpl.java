package com.happyballoon.crm.workbench.service.impl;

import com.happyballoon.crm.utils.DateTimeUtil;
import com.happyballoon.crm.utils.SqlSessionUtil;
import com.happyballoon.crm.utils.UUIDUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Customer;
import com.happyballoon.crm.workbench.domain.Tran;
import com.happyballoon.crm.workbench.domain.TranHistory;
import com.happyballoon.crm.workbench.mapper.CustomerMapper;
import com.happyballoon.crm.workbench.mapper.TranHistoryMapper;
import com.happyballoon.crm.workbench.mapper.TranMapper;
import com.happyballoon.crm.workbench.mapper.TranRemarkMapper;
import com.happyballoon.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranMapper tranMapper = SqlSessionUtil.getSqlSession().getMapper(TranMapper.class);
    private TranHistoryMapper tranHistoryMapper = SqlSessionUtil.getSqlSession().getMapper(TranHistoryMapper.class);
    private TranRemarkMapper tranRemarkMapper = SqlSessionUtil.getSqlSession().getMapper(TranRemarkMapper.class);
    private CustomerMapper customerMapper = SqlSessionUtil.getSqlSession().getMapper(CustomerMapper.class);

    @Override
    public PaginationVO pageList(Map<String, Object> map) {
        int total = tranMapper.selectTranCountByConditions(map);
        List<Tran> tranList = tranMapper.selectTranByConditions(map);

        PaginationVO vo = new PaginationVO<Tran>();
        vo.setTotal(total);
        vo.setDataList(tranList);

        return vo;
    }

    @Override
    public boolean saveTransaction(Tran tran, String customerName) {
        /*
            交易添加业务：
                在添加之前，参数t里面就少了一项信息，就是客户的主键，customerId
                先处理客户相关的需求：
                (1)判断customerName,根据客户名称在客户表进行精确查询
                    如果有这个客户，则取出这个客户的id，封装到t对象中
                    如果没有这个客户，则再客户表新建一条客户信息，然后将新建的客户的id取出，封装到t对象中

                (2)经过以上操作后，t对象中的信息就全了，需要执行添加交易的操作

                (3)添加交易完毕后，需要创建一条交易历史
         */
        boolean success = false;
        Customer customer = customerMapper.selectCustomerByName(customerName);
        if(customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());
            //添加客户
            int count1 =  customerMapper.insertCustomer(customer);
            if(count1==1){
                success=true;
            }
        }
        //通过以上对于客户的处理，不论是查询出来已有的客户，还是我们新增的客户，总之客户已经有了，客户的id就有了
        //将客户id封装到tran对象中
        tran.setCustomerId(customer.getId());

        //执行添加交易的操作
        int count2 = tranMapper.insertTran(tran);
        if(count2==1){
            success=true;
        }

        //执行添加交易历史
        TranHistory th = new TranHistory();

        th.setStage(tran.getStage());
        th.setTranId(tran.getId());
        th.setId(UUIDUtil.getUUID());
        th.setMoney(tran.getMoney());
        th.setCreateBy(tran.getCreateBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(tran.getExpectedDate());

        int count3 = tranHistoryMapper.insertTranHistory(th);
        if(count3==1){
            success=true;
        }


        return success;
    }

    @Override
    public Tran selectTransactionById(String id) {
        Tran tran = tranMapper.selectTranById(id);
        return tran;
    }

    @Override
    public List<TranHistory> selectTranHistoryByTranId(String tranId) {
        List<TranHistory> thList = tranHistoryMapper.selectTranHistoryByTranId(tranId);
        return thList;
    }

    @Override
    public boolean changeStage(Tran tran) {
        boolean success = false;
        int count1 = tranMapper.updateTran(tran);
        if (count1==1){
            success=true;
        }

        //交易更新后，生成一笔交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateBy(tran.getEditBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setTranId(tran.getId());
        int count2 = tranHistoryMapper.insertTranHistory(tranHistory);
        if(count2==1){
            success=true;
        }
        return success;
    }

    @Override
    public Map<String, Object> getChart() {
        int total = tranMapper.selectCount();
        List<Map<String, Object> > dataList = tranMapper.selectTranToChart();

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("dataMap",dataList);
        return map;
    }

    @Override
    public boolean updateTransaction(Tran tran, String customerName) {
        boolean success = false;
        //先查出客户id
        Customer customer = customerMapper.selectCustomerByName(customerName);
        //查不到客户，新建客户
        if (customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());
            //添加客户
            int count =  customerMapper.insertCustomer(customer);
            if(count == 1){
                success=true;
            }
        }
        tran.setCustomerId(customer.getId());

        int count1 = tranMapper.updateTran2(tran);
        if(count1 == 1){
            success=true;
        }
        //交易更新后，生成一笔交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateBy(tran.getEditBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setTranId(tran.getId());
        int count2 = tranHistoryMapper.insertTranHistory(tranHistory);
        if(count2==1){
            success=true;
        }
        return success;
    }

    @Override
    public boolean deleteTransactionByIds(String[] ids) {
        boolean success = false;

        int count = tranHistoryMapper.selectDelCount(ids);
        int count1 = tranHistoryMapper.deleteTranHistory(ids);
        if (count1==count){
            success = true;
        }
        int count4 = tranRemarkMapper.selectDelCount(ids);
        int count2 = tranRemarkMapper.deleteTranRemark(ids);
        if (count2 ==count4){
            success = true;
        }
        int count3 = tranMapper.deleteTransactionByIds(ids);
        if (count3==ids.length){
            success = true;
        }
        return success;
    }

    @Override
    public List<Tran> selectTransactionByCusId(String customerId) {
        List<Tran> tranList = tranMapper.selectTranByCusId(customerId);
        return tranList;
    }


}
