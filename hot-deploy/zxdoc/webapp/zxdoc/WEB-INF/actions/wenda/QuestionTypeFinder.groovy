package wenda

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue

List<GenericValue> questionTypeList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "QUESTIONTYPE_STATUS"), UtilMisc.toList("sequenceId"), true);
parameters.put("questionTypeList", questionTypeList);

