package com.happyballoon.crm.settings.mapper;

import com.happyballoon.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueMapper {

    List<DicValue> selectDicValueByTypeCode(String code);
}
