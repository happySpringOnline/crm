package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.Activity;
import com.happyballoon.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationMapper {

    int deleteClueRelationById(String id);

    int insertByBund(ClueActivityRelation car);

    List<ClueActivityRelation> selectClueActivityRelationList(String clueId);

    int selectDelCount(String[] ids);

    int deleteClueRelationByIds(String[] ids);

    int selectDelCount2(String[] ids);

    int deleteClueRelationByIds2(String[] ids);
}
