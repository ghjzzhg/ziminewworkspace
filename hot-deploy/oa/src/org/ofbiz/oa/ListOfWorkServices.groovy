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
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat


public Map<String, Object> saveListOfWork(){
    Map success = ServiceUtil.returnSuccess();
    String listOfWorkId = context.get("listOfWorkId");
    String msg = "班次保存成功！"
    /*int toWorkTime_c_hour = context.get("toWorkTime_c_hour");
    int toWorkTime_c_minutes = context.get("toWorkTime_c_minutes");
    int getOffWorkTime_c_hour = context.get("getOffWorkTime_c_hour");
    int getOffWorkTime_c_minutes = context.get("getOffWorkTime_c_minutes");
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int date = calendar.get(Calendar.DATE);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date toWorkTimeDate = format.parse(year +"-" + month + "-" + date + " " + toWorkTime_c_hour + ":" + toWorkTime_c_minutes + ":" + 00);
    Date getOffWorkTimeDate = format.parse(year +"-" + month + "-" + date + " " + getOffWorkTime_c_hour + ":" + getOffWorkTime_c_minutes + ":" + 00);
    context.put("toWorkTime",new java.sql.Time(toWorkTimeDate.getTime()));
    context.put("getOffWorkTime",new java.sql.Time(getOffWorkTimeDate.getTime()));*/
    try{
        if(UtilValidate.isNotEmpty(listOfWorkId)){
            //TODO 跟新
            GenericValue updateListOfWork = delegator.makeValidValue("TblListOfWork",context);
            updateListOfWork.store();
            msg = "班次更新成功！"
        }else{
            listOfWorkId = delegator.getNextSeqId("TblListOfWork");
            context.put("listOfWorkId",listOfWorkId);
            GenericValue listOfWork = delegator.makeValidValue("TblListOfWork",context);
            listOfWork.create();
        }
    }catch (GenericEntityException e){
        msg = "服务出错！"
    }
    success.put("returnValue",msg)
    return success;
}
public Map<String, Object> deleteListOfWork(){
    Map success = ServiceUtil.returnSuccess();
    String listOfWorkId = context.get("listOfWorkId");
    String msg = "删除班次成功！"
    try{
        GenericValue deleteListOfWork = delegator.makeValidValue("TblListOfWork",UtilMisc.toMap("listOfWorkId",listOfWorkId));
        deleteListOfWork.remove();
        success.put("returnValue",msg);
    }catch (GenericEntityException e){
        return ServiceUtil.returnError("当前班次正在使用，无法删除！");
    }
    return success;
}
public Map<String, Object> findListOfWorkAll(){
    Map success = ServiceUtil.returnSuccess();
    List<GenericValue> listOfWork = null;
    try{
        listOfWork = delegator.findAll("TblListOfWork",false);
    }catch (GenericEntityException e){
        e.printStackTrace();
    }
    success.put("returnValue",listOfWork)
    return success;
}

/*public static String getListOfWorkName(String id){
    GenericValue listOfWork = delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",id));
    return listOfWork.get("name");
}*/
