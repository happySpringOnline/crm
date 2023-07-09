package com.happyballoon.crm.settings.service;

import com.happyballoon.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

public interface DicService {
    Map<String, List<DicValue>> getDictionary();
}
