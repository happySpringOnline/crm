package com.happyballoon.crm.settings.service.impl;

import com.happyballoon.crm.settings.domain.DicType;
import com.happyballoon.crm.settings.domain.DicValue;
import com.happyballoon.crm.settings.mapper.DicTypeMapper;
import com.happyballoon.crm.settings.mapper.DicValueMapper;
import com.happyballoon.crm.settings.service.DicService;
import com.happyballoon.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {

    private DicTypeMapper dicTypeMapper = SqlSessionUtil.getSqlSession().getMapper(DicTypeMapper.class);
    private DicValueMapper dicValueMapper = SqlSessionUtil.getSqlSession().getMapper(DicValueMapper.class);


    @Override
    public Map<String, List<DicValue>> getDictionary() {

        Map<String,List<DicValue>> map = new HashMap<String, List<DicValue>>();
        List<DicType> dicTypeList = dicTypeMapper.selectAllDicType();

        for (DicType dicType:dicTypeList){
            //取类型的主键
            String code = dicType.getCode();

            //通过dictype外键查找该类型下所有的数值
            List<DicValue> dicValueList = dicValueMapper.selectDicValueByTypeCode(code);

            map.put(code,dicValueList);
        }

        return map;
    }
}
