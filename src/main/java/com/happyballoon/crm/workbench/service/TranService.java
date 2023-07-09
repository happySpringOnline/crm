package com.happyballoon.crm.workbench.service;

import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Tran;
import com.happyballoon.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    PaginationVO pageList(Map<String, Object> map);

    boolean saveTransaction(Tran tran, String customerName);

    Tran selectTransactionById(String id);

    List<TranHistory> selectTranHistoryByTranId(String tranId);

    boolean changeStage(Tran tran);

    Map<String,Object>  getChart();

    boolean updateTransaction(Tran tran, String customerName);

    boolean deleteTransactionByIds(String[] ids);

    List<Tran> selectTransactionByCusId(String customerId);
}
