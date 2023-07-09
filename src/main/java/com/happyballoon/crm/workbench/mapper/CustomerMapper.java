package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.Contacts;
import com.happyballoon.crm.workbench.domain.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerMapper {

    Customer selectCustomerByName(String company);

    int insertCustomer(Customer customer);

    List<Customer> selectCustomerByConditions(Map<String, Object> map);

    int selectcustomerCount(Map<String, Object> map);

    Customer selectCustomerById(String id);

    int updateCustomerById(Customer customer);

    int deleteCustomerByIds(String[] ids);

    Customer selectCustomerById2(String id);

    List<String> selectCustomerByName2(String name);


}
