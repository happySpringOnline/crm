package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.ContactsRemark;

import java.util.List;
import java.util.Map;

public interface ContactsRemarkMapper {

    int insertContactsRemark(ContactsRemark contactsRemark);

    List<ContactsRemark> selectContactRemarksByContactId(String contactId);

    int updateContactRemark(Map<String, Object> map);

    int deleteRemarkById(String id);

    int selectDelCount(String[] ids);

    int deleteRemarkByIds(String[] ids);
}
