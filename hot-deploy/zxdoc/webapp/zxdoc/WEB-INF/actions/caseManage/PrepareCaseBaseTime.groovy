import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

GenericValue caseObj = from("TblCase").where("caseId", parameters.caseId).queryOne();
String templateId = caseObj.getString("caseTemplate");
context.baseTimes = from("TblCaseTemplateBaseTime").where("templateId",templateId).cache().orderBy("seq").queryList();
List<GenericValue> oldTimes = from("TblCaseBaseTime").where("caseId", parameters.caseId).queryList();
if(UtilValidate.isNotEmpty(oldTimes)){
    context.oldTimes = oldTimes.collectEntries{b -> [b.getString("baseTimeId"), b.getDate("baseTime")]};
}else{
    context.oldTimes = [];
}
