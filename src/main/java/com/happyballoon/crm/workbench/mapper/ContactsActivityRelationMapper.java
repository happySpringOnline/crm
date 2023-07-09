package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.ClueActivityRelation;
import com.happyballoon.crm.workbench.domain.ContactsActivityRelation;

import java.util.List;
import java.util.Map;

public interface ContactsActivityRelationMapper {


    int insertRelation(ContactsActivityRelation contactsActivityRelation);

    List<String> selectActivityId(String contactsId);

    int deleteRelation(Map<String, String> map);

    int selectDelCount(String[] ids);

    int deleteContactsRelationByIds(String[] ids);

    int selectDelCount2(String[] ids);

    int deleteContactsRelationByIds2(String[] ids);
}
