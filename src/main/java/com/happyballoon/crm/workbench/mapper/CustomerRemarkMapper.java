package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.CustomerRemark;

import java.util.List;
import java.util.Map;

public interface CustomerRemarkMapper {

    int insertCustomerRemark(CustomerRemark customerRemark);

    List<CustomerRemark> selectCustomerRemarksById(String id);

    int updateCustomerRemark(CustomerRemark cr);

    int deleteCustomerRemarkById(String id);

    int deleteCustomerRemarkByCusId(String[] ids);

    int selectDelCount(String[] ids);
}
