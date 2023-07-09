package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkMapper {


    List<ClueRemark> selectClueRemarkByClueId(String clueId);

    int insertClueRemark(ClueRemark clueRemark);

    int deleteClueRemarkById(String id);

    int selectDelCount(String[] ids);

    int deleteClueRemarkByIds(String[] ids);
}
