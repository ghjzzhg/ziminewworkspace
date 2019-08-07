package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.StringUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat


public Map<String, Object> saveListOfWorkByWeek(){
    Map success = ServiceUtil.returnSuccess();
    String listOfWorkByWeekId = context.get("listOfWorkByWeekId");
    Map map=new HashMap();
    map.put("sun",context.get("work_sun"));
    map.put("mon",context.get("work_mon"));
    map.put("tue",context.get("work_tue"));
    map.put("wed",context.get("work_wed"));
    map.put("thu",context.get("work_thu"));
    map.put("fri",context.get("work_fri"));
    map.put("sat",context.get("work_sat"));
    map.put("listOfWorkByWeekName",context.get("listOfWorkByWeekName"));
    map.put("defaultValue",context.get("defaultValue"));

    String msg = "班次保存成功！"
    try{
        if(UtilValidate.isNotEmpty(listOfWorkByWeekId)){
            //TODO 跟新
            map.put("listOfWorkByWeekId",listOfWorkByWeekId);
            GenericValue updateListOfWorkByWeek = delegator.makeValidValue("TblListOfWorkByWeek",map);
            updateListOfWorkByWeek.store();
            msg = "班制更新成功！"
        }else{
            listOfWorkByWeekId = delegator.getNextSeqId("TblListOfWorkByWeek");
            map.put("listOfWorkByWeekId",listOfWorkByWeekId);
            GenericValue saveListOfWorkWeek = delegator.makeValidValue("TblListOfWorkByWeek",map);
            saveListOfWorkWeek.create();
        }
    }catch (GenericEntityException e){
        msg = "服务出错！"
    }
    success.put("returnValue",msg)
    return success;
}

public Map<String, Object> searchListOfWorkByWeek(){
    Map success = ServiceUtil.returnSuccess();
    String listOfWorkByWeekName = context.get("listOfWorkByWeekName");
    String listOfWorkName = context.get("listOfWorkName");
    List<EntityCondition> conditions = FastList.newInstance();
    if(UtilValidate.isNotEmpty(listOfWorkName)){
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("lowSunName",EntityOperator.LIKE,"%"+listOfWorkName+"%"));
        conditionList.add(EntityCondition.makeCondition("lowMonName",EntityOperator.LIKE,"%"+listOfWorkName+"%"));
        conditionList.add(EntityCondition.makeCondition("lowTueName",EntityOperator.LIKE,"%"+listOfWorkName+"%"));
        conditionList.add(EntityCondition.makeCondition("lowWedName",EntityOperator.LIKE,"%"+listOfWorkName+"%"));
        conditionList.add(EntityCondition.makeCondition("lowThuName",EntityOperator.LIKE,"%"+listOfWorkName+"%"));
        conditionList.add(EntityCondition.makeCondition("lowFriName",EntityOperator.LIKE,"%"+listOfWorkName+"%"));
        conditionList.add(EntityCondition.makeCondition("lowSatName",EntityOperator.LIKE,"%"+listOfWorkName+"%"));
        conditions.add(EntityCondition.makeCondition(conditionList,EntityJoinOperator.OR));
    }
    if(UtilValidate.isNotEmpty(listOfWorkByWeekName)){
        conditions.add(EntityCondition.makeCondition("listOfWorkByWeekName",EntityOperator.LIKE,"%" + listOfWorkByWeekName + "%"));
    }
    List<GenericValue> listOfWorkByWeekList = null;
    if(conditions.size() > 0){
        listOfWorkByWeekList = EntityQuery.use(delegator)
                .from("ListOfWorkAndByWeek")
                .where(conditions)
                .queryList();
    }else {
        listOfWorkByWeekList = EntityQuery.use(delegator)
                .from("ListOfWorkAndByWeek")
                .queryList();
    }
    success.put("returnValue",listOfWorkByWeekList);
    return success;
}

public Map<String, Object> deleteListOfWorkByWeek(){
    Map success = ServiceUtil.returnSuccess();
    String listOfWorkByWeekId = context.get("listOfWorkByWeekId");
    String msg = "删除班次成功！"
    try{
        GenericValue deleteListOfWorkByWeek = delegator.makeValidValue("TblListOfWorkByWeek",UtilMisc.toMap("listOfWorkByWeekId",listOfWorkByWeekId));
        deleteListOfWorkByWeek.remove();
        success.put("returnValue",msg);
    }catch (GenericEntityException e){
        return ServiceUtil.returnError("当前班制正在使用，无法删除！");
    }

    return success;
}
