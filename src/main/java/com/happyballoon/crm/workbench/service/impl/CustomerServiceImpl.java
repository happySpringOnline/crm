package com.happyballoon.crm.workbench.service.impl;

import com.happyballoon.crm.utils.SqlSessionUtil;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Contacts;
import com.happyballoon.crm.workbench.domain.Customer;
import com.happyballoon.crm.workbench.domain.CustomerRemark;
import com.happyballoon.crm.workbench.mapper.ContactsMapper;
import com.happyballoon.crm.workbench.mapper.CustomerMapper;
import com.happyballoon.crm.workbench.mapper.CustomerRemarkMapper;
import com.happyballoon.crm.workbench.service.CustomerService;

import java.util.List;
import java.util.Map;

public class CustomerServiceImpl implements CustomerService {

    private CustomerMapper customerMapper = SqlSessionUtil.getSqlSession().getMapper(CustomerMapper.class);
    private CustomerRemarkMapper customerRemarkMapper = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkMapper.class);
    private ContactsMapper contactsMapper = SqlSessionUtil.getSqlSession().getMapper(ContactsMapper.class);

    @Override
    public PaginationVO<Customer> pageList(Map<String, Object> map) {
        List<Customer> dataList = customerMapper.selectCustomerByConditions(map);
        int total = customerMapper.selectcustomerCount(map);

        PaginationVO<Customer> vo = new PaginationVO<Customer>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public boolean saveCustomer(Customer customer) {
        boolean success = false;
        int count = customerMapper.insertCustomer(customer);
        if (count==1){
            success=true;
        }
        return success;
    }

    @Override
    public Customer getCustomerById(String id) {
        Customer c = customerMapper.selectCustomerById(id);
        return c;
    }

    @Override
    public boolean updateCustomerById(Customer customer) {
        boolean success = false;
        int count = customerMapper.updateCustomerById(customer);
        if (count==1){
            success=true;
        }
        return success;
    }

    @Override
    public boolean deleteCustomerByIds(String[] ids) {
        boolean success = false;
        //查询出需要删除的客户备注的数量
        int count = customerRemarkMapper.selectDelCount(ids);
        //删除备注，返回受到影响的条数（实际删除的数量）
        int count1 = customerRemarkMapper.deleteCustomerRemarkByCusId(ids);
        if (count1==count){
            success=true;
        }
        int count2 = customerMapper.deleteCustomerByIds(ids);
        if (count2==ids.length){
            success=true;
        }
        return success;
    }

    @Override
    public Customer getCustomerById2(String id) {
        Customer customer = customerMapper.selectCustomerById2(id);
        return customer;
    }

    @Override
    public List<String> getCustomerByName(String name) {
        List<String> uStrList = customerMapper.selectCustomerByName2(name);
        return uStrList;
    }

    @Override
    public List<CustomerRemark> selectCustomerRemarksById(String id) {
        List<CustomerRemark> customerRemarkList = customerRemarkMapper.selectCustomerRemarksById(id);
        return customerRemarkList;
    }

    @Override
    public boolean updateCustomerRemark(CustomerRemark cr) {
        boolean success = false;
        int count = customerRemarkMapper.updateCustomerRemark(cr);
        if (count==1){
            success = true;
        }
        return success;
    }

    @Override
    public boolean saveCustomerRemark(CustomerRemark customerRemark) {
        boolean success = false;
        int count = customerRemarkMapper.insertCustomerRemark(customerRemark);
        if (count==1){
            success = true;
        }
        return success;
    }

    @Override
    public boolean deleteCustomerRemarkById(String id) {
        boolean success = false;
        int count = customerRemarkMapper.deleteCustomerRemarkById(id);
        if (count==1){
            success = true;
        }
        return success;
    }

    @Override
    public List<Contacts> selectContactsByCusId(String customerId) {
        List<Contacts> contactsList = contactsMapper.selectContactsByCusId(customerId);
        return contactsList;
    }

    @Override
    public Customer selectCustomerById(String customerId) {
        Customer customer = customerMapper.selectCustomerById(customerId);
        return customer;
    }


}
