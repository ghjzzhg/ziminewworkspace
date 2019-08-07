package caseManage

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/11/24.
 */
String id = parameters.id;
Boolean flag = Boolean.parseBoolean(parameters.flag);
GenericValue caseTemplate = EntityQuery.use(delegator).from("TblCaseTemplate").where("id",id).queryOne();
String msg = "N";
if(flag){
    msg = "Y";
    //如果生效，那么将其他类型的模板失效
    String caseType = caseTemplate.get("templateKey");
    List<GenericValue> caseTemplateList = EntityQuery.use(delegator).from("TblCaseTemplate").where("templateKey",caseType).queryList();
    for(GenericValue caseTemplateInfo : caseTemplateList){
        caseTemplateInfo.put("active","N");
    }
    delegator.storeAll(caseTemplateList)
}
caseTemplate.put("active",msg);
caseTemplate.store();