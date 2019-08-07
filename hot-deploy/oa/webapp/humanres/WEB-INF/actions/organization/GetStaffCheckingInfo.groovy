import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.oa.CheckingInParametersUtil

import java.text.SimpleDateFormat

String type = parameters.get("type");
String staffIds = parameters.get("staffIds");
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
String dateStr = parameters.get("date");
Date date = null;
if(UtilValidate.isNotEmpty(dateStr)){
    date = format.parse(dateStr);
}
List<Map<String,Object>> valueList = new ArrayList<Map<String,Object>>()
if(UtilValidate.isNotEmpty(staffIds)){
    String[] staffIdArr = staffIds.split(",");
    if(type.equals("WorkerCheckingB")){//考勤异常
        String abnormalCause = parameters.get("abnormalCause");
        String[] abnormalCauseArr = abnormalCause.split(",");
        for(int i = 0; i < staffIdArr.length; i ++){
            Map<String,Object> valueMap = FastMap.newInstance();
            GenericValue checkingIn = EntityQuery.use(delegator)
                    .from("TblCheckingIn")
                    .where(EntityCondition.makeCondition(EntityCondition.makeCondition("staff",EntityOperator.EQUALS,staffIdArr[i]),EntityJoinOperator.AND,EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS,new java.sql.Date(date.getTime()))))
                    .queryOne();
            GenericValue listOfWork = delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",checkingIn.get("listOfWorkId")),false);
            valueMap.put("staffId",staffIdArr[i]);
            valueMap.put("listOfWorkName",listOfWork.get("listOfWorkName"));
            valueMap.put("listOfWorkType",listOfWork.get("listOfWorkType"));
            valueMap.put("cause",abnormalCauseArr[i].equals(CheckingInParametersUtil.NO_CHECKING_IN) ? "未签到" : "未签退");
            valueList.add(valueMap);
        }
    }else if(type.equals("WorkerCheckingC")){//迟到\早退
        String checkingInType = parameters.get("checkingInType");
        if("1".equals(checkingInType)){
            checkingInType = CheckingInParametersUtil.CHECKING_IN;
        }else {
            checkingInType = CheckingInParametersUtil.CHECKING_OUT;
        }
        for(String id : staffIdArr){
            Map<String,Object> valueMap = FastMap.newInstance();
            GenericValue checkingIn = EntityQuery.use(delegator)
                    .from("TblCheckingIn")
                    .where(EntityCondition.makeCondition(
                    EntityJoinOperator.AND,
                    EntityCondition.makeCondition("staff",EntityOperator.EQUALS,id),
                    EntityCondition.makeCondition("checkingInDate",EntityOperator.EQUALS,new java.sql.Date(date.getTime())),
                    EntityCondition.makeCondition("checkingInType",EntityOperator.EQUALS,checkingInType)
            ))
                    .queryOne();
            GenericValue listOfWork = delegator.findOne("TblListOfWork",UtilMisc.toMap("listOfWorkId",checkingIn.get("listOfWorkId")),false);
            valueMap.put("staffId",id);
            valueMap.put("listOfWorkName",listOfWork.get("listOfWorkName"));
            valueMap.put("listOfWorkType",listOfWork.get("listOfWorkType"));
            valueMap.put("checkingInDate",checkingIn.get("checkingInDate"));
            String status = checkingIn.get("checkingInStatus");
            if(status.equals("CHECKING_IN_STATUS_LATE")){//迟到
                valueMap.put("minutes","迟到 " + checkingIn.get("minutes") + " 分钟");
            }else {
                valueMap.put("minutes","早退 " + checkingIn.get("minutes") + " 分钟");
            }

            valueList.add(valueMap);
        }
    }else if(type.equals("WorkerCheckingA")){//旷工
        for(String id : staffIdArr){
            GenericValue staff = delegator.findOne("TblStaff",UtilMisc.toMap("partyId",id),false);
            Map<String,Object> valueMap = FastMap.newInstance();
            List<GenericValue> postList = delegator.findByAnd("PartyRelationship",UtilMisc.toMap("partyIdTo",id,"partyRelationshipTypeId","PROVIDE_POSITION"));
            GenericValue post = postList ==null|| postList.size() == 0 ? null : postList.get(0);
            if(UtilValidate.isNotEmpty(post)){
                Map<String,Object> successData = dispatcher.runSync("getMasterOccupation",UtilMisc.toMap("positionId",post.get("partyIdFrom") + "," + post.get("roleTypeIdFrom"),"userLogin",context.get("userLogin")));
                Map<String,Object> data = successData.get("data");
                String masterId = data.get("roleTypeId");
                try {
                    List<GenericValue> masterStaffList = delegator.findByAnd("PartyRelationship",UtilMisc.toMap("roleTypeIdTo",masterId,"partyRelationshipTypeId","PROVIDE_POSITION"));
                    GenericValue masterStaff = masterStaffList.get(0);
                    if(UtilValidate.isNotEmpty(masterStaff)){
                        valueMap.put("masterStaff",masterStaff.get("partyIdTo"));
                    }
                }catch (IndexOutOfBoundsException e){

                }
                valueMap.put("post",post.get("roleTypeIdTo"));
            }
            valueMap.put("staffId",id);
            valueMap.put("department",staff.get("department"));
            valueList.add(valueMap);
        }
    }
}
context.noNum = valueList.size();
context.staffCheckingList = valueList;

