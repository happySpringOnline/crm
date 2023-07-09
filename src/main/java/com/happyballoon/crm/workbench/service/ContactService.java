package com.happyballoon.crm.workbench.service;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Activity;
import com.happyballoon.crm.workbench.domain.Contacts;
import com.happyballoon.crm.workbench.domain.ContactsRemark;
import com.happyballoon.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ContactService {
    List<Contacts> getContactsListByCname(String cname);

    PaginationVO pageList(Map<String, Object> map);

    boolean saveContact(Contacts c, String customerName);

    Contacts selectContactById(String id);

    boolean deleteContactByIds(String[] ids);

    boolean updateContactById(Contacts c, String customerName);

    Contacts selectContactById2(String id);

    List<ContactsRemark> selectContactRemarks(String contactId);

    boolean insertContactRemark(ContactsRemark contactsRemark);

    boolean updateContactRemark(Map<String, Object> map);

    boolean deleteRemarkById(String id);

    List<Tran> selectTranListByContactsId(String contactsId);

    List<Activity> selectActivitiesByContactsId(String contactsId);

    List<Activity> getActivityListByAname(Map<String, String> map);

    boolean bund(String contactsId, String[] aid);


    boolean unbund(Map<String, String> map);

    boolean saveContact2(Contacts c);
}
