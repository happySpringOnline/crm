package com.happyballoon.crm.workbench.service;

import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Contacts;
import com.happyballoon.crm.workbench.domain.Customer;
import com.happyballoon.crm.workbench.domain.CustomerRemark;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    PaginationVO<Customer> pageList(Map<String, Object> map);

    boolean saveCustomer(Customer customer);

    Customer getCustomerById(String id);

    boolean updateCustomerById(Customer customer);

    boolean deleteCustomerByIds(String[] ids);

    Customer getCustomerById2(String id);

    List<String> getCustomerByName(String name);

    List<CustomerRemark> selectCustomerRemarksById(String id);

    boolean updateCustomerRemark(CustomerRemark cr);

    boolean saveCustomerRemark(CustomerRemark customerRemark);

    boolean deleteCustomerRemarkById(String id);

    List<Contacts> selectContactsByCusId(String customerId);

    Customer selectCustomerById(String customerId);
}
