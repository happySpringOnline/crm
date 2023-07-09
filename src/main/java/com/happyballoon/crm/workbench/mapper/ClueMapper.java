package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueMapper {

    int insertClue(Clue c);

    int selectClueCount(Map<String, Object> map);

    List<Clue> selectClueByConditions(Map<String, Object> map);

    Clue selectClueById(String id);

    Clue selectClueById2(String clueId);

    int deleteClueById(String clueId);

    int deleteClueByIds(String[] ids);

    int updateClueById(Clue c);
}
