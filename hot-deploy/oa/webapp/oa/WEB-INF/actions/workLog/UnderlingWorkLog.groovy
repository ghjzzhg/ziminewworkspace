import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ModelService

import java.sql.Timestamp
import java.text.SimpleDateFormat

Calendar cal = Calendar.getInstance();
cal.setTime(new Date(System.currentTimeMillis()));
/*
* 搜索条件
* */
String weekPageStr = parameters.get("weekPage");
String search = parameters.get("search");
String dateTime = parameters.get("dateTime");
String type = parameters.get("type");
int weekPage = 0;
if (weekPageStr!=null){
    weekPage = Integer.parseInt(weekPageStr);
}
//当前用户的职位
List<GenericValue> positionMapForPerson = delegator.findByAnd("StaffPositionDetailView",UtilMisc.toMap("memberId",parameters.get("userLogin").get("partyId")),null,false);
int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - 1);
dayOfWeek = dayOfWeek+weekPage*7;
Date date = null;
if(search =="true"&&UtilValidate.isNotEmpty(dateTime)){
date =new SimpleDateFormat("yyyy-MM-dd").parse(dateTime);
}else {
    date = new Date();//取时间
}

Calendar calendar = new GregorianCalendar();
calendar.setTime(date);
calendar.add(calendar.DATE, -dayOfWeek);
date = calendar.getTime();
List weekList = new ArrayList();
for (int i = 1; i <= 7; i++) {
    dateMap = [:];
    String dateForString = dateString(date);
    dateMap.put("date", dateForString);
    switch (i) {
        case 1: dateMap.put("week", "星期日"); dateMap.put("weekForEnglish", "Sun"); break;
        case 2: dateMap.put("week", "星期一"); dateMap.put("weekForEnglish", "Mon"); break;
        case 3: dateMap.put("week", "星期二"); dateMap.put("weekForEnglish", "Tues"); break;
        case 4: dateMap.put("week", "星期三"); dateMap.put("weekForEnglish", "Wed"); break;
        case 5: dateMap.put("week", "星期四"); dateMap.put("weekForEnglish", "Thur");break;
        case 6: dateMap.put("week", "星期五"); dateMap.put("weekForEnglish", "Fri"); break;
        case 7: dateMap.put("week", "星期六"); dateMap.put("weekForEnglish", "Sat"); break;
    }
    weekList.add(dateMap);
    Calendar calendarFor = new GregorianCalendar();
    calendarFor.setTime(date);
    calendarFor.add(calendarFor.DATE, 1);
    date = calendarFor.getTime();
}
GenericValue userLoginId = context.get("userLogin");
String partyId = userLoginId.get("partyId");
List underlingList = new ArrayList();
List underlingWorkLogIdList = new ArrayList();
String viewType = "viewUnderling";
/*
* 查看关注下属
* */
if (type==null||type!="viewAll"){
    underlingList = delegator.findByAnd("TblFocusUnderling", UtilMisc.toMap("partyIdFrom", partyId),null,false);
    for (Map map : underlingList) {
        String partyIdForFindWorkLog = map.get("partyIdTo")
        underlingWorkLogIdList.add(partyIdForFindWorkLog);
    }
}else {//查看所有下属日志
    viewType = "viewAll"
    for (Map map1:positionMapForPerson){
        String positionId = map1.get("positionId")
        context.positionId = positionId
        Map<String,Object> resultList = runService("getLowerOccupationMembers", dispatcher.getDispatchContext().getModelService("getLowerOccupationMembers").makeValid(context, ModelService.IN_PARAM));
        List<GenericValue> dataList = resultList.get("data");
        if(dataList.size() > 0){
            for (Map value : dataList) {
                String partyIdForFindWorkLog = value.get("partyId")
                underlingWorkLogIdList.add(partyIdForFindWorkLog);
            }
        }
    }

}
String startDate = weekList[0].get("date");
String endDate = weekList[6].get("date");
List criteria = new LinkedList();
criteria.add(java.sql.Date.valueOf(startDate));
criteria.add(java.sql.Date.valueOf(endDate));
conditionForPerson = EntityCondition.makeCondition("partyId", EntityOperator.IN, underlingWorkLogIdList)
List<GenericValue> personList = delegator.findList("Person", conditionForPerson, null, null, null, false);
List personListForView = new ArrayList(){};
for (Map map1:personList){
    personMap = [:];
    String name = (map1.get("fullName").toString()=="null")?"":map1.get("fullName").toString();
    String partyIdForPerson = map1.get("partyId");
    personMap.put("name",name);
    personMap.put("partyId",partyIdForPerson);
    personMap.put("post",partyIdForPerson);
    personListForView.add(personMap);
}
List conditionList = new ArrayList();
conditionList.add(EntityCondition.makeCondition("workLogDate", EntityOperator.BETWEEN, criteria));
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, underlingWorkLogIdList));
EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList), EntityOperator.AND);
List<GenericValue> workLogMapList = delegator.findList("TblWorkLog", condition, null, null, null, false);//所有下属日志
List resultListForClone = new ArrayList();
for (Map map : workLogMapList) {
    Map<String,Object> mapForClone = (Map) map.clone();
    String partyIdFromMap = mapForClone.get("partyId");
    String dateString = mapForClone.get("workLogDate");
    String dateForString = dateForWeek(dateString);
    String id = partyIdFromMap.concat(dateForString);
    mapForClone.put("partyId", id);
    resultListForClone.add(mapForClone);
}
context.underlingWorkLogList = underlingWorkLogIdList;
context.resultListForClone = resultListForClone ;
context.weekList = weekList;
context.personList = personList;
context.weekPage = weekPage;
context.personListForView = personListForView;
context.viewType = viewType;

public String dateString(Date date) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = df.format(date);
    return dateString;
}
public String dateForWeek(String date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date dateForChange = sdf.parse(date);
    Calendar cal = Calendar.getInstance();
    cal.setTime(dateForChange);
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    String weekForEnglish;
    switch (dayOfWeek) {
        case 1: weekForEnglish = "Sun"; break;
        case 2: weekForEnglish = "Mon"; break;
        case 3: weekForEnglish = "Tues"; break;
        case 4: weekForEnglish = "Wed"; break;
        case 5: weekForEnglish = "Thur"; break;
        case 6: weekForEnglish = "Fri"; break;
        case 7: weekForEnglish = "Sat"; break;
    }
    return weekForEnglish;
}
