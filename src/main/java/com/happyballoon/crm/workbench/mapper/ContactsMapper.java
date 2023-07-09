package com.happyballoon.crm.workbench.mapper;

import com.happyballoon.crm.workbench.domain.Contacts;

import java.util.List;
import java.util.Map;

public interface ContactsMapper {

    int insertContact(Contacts contacts);

    List<Contacts> selectContactsListByCname(String cname);

    List<Contacts> selectContactsByConditions(Map<String, Object> map);

    int selectCountByConditions(Map<String, Object> map);

    Contacts selectContactById(String id);

    int deleteContactsByIds(String[] ids);

    int updateContactById(Contacts c);

    Contacts selectContactById2(String id);

    List<Contacts> selectContactsByCusId(String customerId);
}
