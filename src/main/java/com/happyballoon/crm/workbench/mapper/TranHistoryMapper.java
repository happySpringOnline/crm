package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryMapper {

    int insertTranHistory(TranHistory tranHistory);


    List<TranHistory> selectTranHistoryByTranId(String tranId);

    int deleteTranHistory(String[] ids);

    int selectDelCount(String[] ids);
}
