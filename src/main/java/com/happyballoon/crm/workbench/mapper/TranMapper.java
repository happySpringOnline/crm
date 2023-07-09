package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranMapper {

    int insertTran(Tran tran);

    int selectTranCountByConditions(Map<String, Object> map);

    List<Tran> selectTranByConditions(Map<String, Object> map);

    Tran selectTranById(String id);

    int updateTran(Tran tran);

    int selectCount();

    List<Map<String, Object>> selectTranToChart();

    int deleteTransactionByIds(String[] ids);

    int updateTran2(Tran tran);

    List<Tran> selectTranListByContactsId(String contactsId);

    List<Tran> selectTranByCusId(String customerId);
}
