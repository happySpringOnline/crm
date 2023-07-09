package com.happyballoon.crm.workbench.service;

import com.happyballoon.crm.settings.domain.User;
import com.happyballoon.crm.vo.PaginationVO;
import com.happyballoon.crm.workbench.domain.Activity;
import com.happyballoon.crm.workbench.domain.Clue;
import com.happyballoon.crm.workbench.domain.ClueRemark;
import com.happyballoon.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean saveClue(Clue c);

    PaginationVO<Clue> pageList(Map<String, Object> map);

    Clue selectClueById(String id);

    boolean deleteClueRelationById(String id);

    boolean bund(String cid, String[] aids);

    boolean convertTran(String clueId, Tran tran, User user);

    List<ClueRemark> showClueRemarkList(String clueId);

    boolean addClueRemark(ClueRemark clueRemark);

    boolean deleteClueRemark(String id);

    boolean deleteClueByIds(String[] ids);

    Clue selectClueById2(String id);

    boolean updateClueById(Clue c);
}
