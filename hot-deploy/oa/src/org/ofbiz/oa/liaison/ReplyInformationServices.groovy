import javolution.util.FastList
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String,Object> replyInformationLiaison(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String contactListId = context.get("contactListId");
    String content=context.get("content");

    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.getString("partyId");
    List<GenericValue> genericValueUserLogin = delegator.findByAnd("StaffInfo", UtilMisc.toMap("staffId", partyId));
    String departmentName = genericValueUserLogin.get(0).getString("departmentName");


    String workSheetSignForInformationId = delegator.getNextSeqId("TblWorkSheetReplyInfor");
    GenericValue genericValue = delegator.makeValidValue(
            "TblWorkSheetReplyInfor",UtilMisc.toMap("workSheetReplyInforId",workSheetSignForInformationId));
    genericValue.put("contactListId",contactListId);
    genericValue.put("replyContent",content);
    genericValue.put("departmentName",departmentName);
    genericValue.put("fullName",partyId);
    genericValue.put("replyTime",new Timestamp(new Date().getTime()));
    genericValue.create();
    msg = "回复成功！"

    //list
    List<GenericValue> replyInformationList = delegator.findByAnd("TblWorkSheetReplyInfor",UtilMisc.toMap("contactListId",contactListId),null,false);
    List<GenericValue> replyInformationLists=new ArrayList<>();
    for (int i=0;i<replyInformationList.size();i++){
        Map map=new HashMap();
        map.putAll(replyInformationList.get(i));
        String replyTime=map.get("replyTime");
        if (UtilValidate.isNotEmpty(replyTime)){
            map.put("replyTime",replyTime.substring(0,replyTime.length()-2));
        }
        replyInformationLists.add(map);
    }
    liaisonMap=delegator.findByPrimaryKey("TblWorkContactList",UtilMisc.toMap("contactListId",contactListId));

    successResult.put("data",UtilMisc.toMap("msg",msg,"replyInformationList",replyInformationLists,"liaisonMap",liaisonMap));
    return successResult;
}


public Map<String,Object> findWorkSheetReplyInformation(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String contactListId = context.get("contactListId");

    try {
        // 分页参数
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = 2;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 2;
        }

        /*String sortField = (String) context.get("sortField");
        List<String> orderBy = FastList.newInstance();
        if (UtilValidate.isNotEmpty(sortField)) {
            orderBy.add(sortField);
        } else {
            orderBy.add("-startTime");
        }*/
        // 计算当前显示页的最小、最大索引(可能会超出实际条数)
        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;


        //查总计划
        eli = EntityQuery.use(delegator)
                .from("TblWorkSheetReplyInfor")
                .where(EntityCondition.makeCondition("contactListId",EntityOperator.EQUALS,contactListId))
                .orderBy("-replyTime")
                .cursorScrollInsensitive()
                .fetchSize(highIndex)
                .distinct()
                .queryIterator();
        // 获取结果片段
        List<GenericValue> members = eli.getPartialList(lowIndex, viewSize);
        // 获取实际总条数
        int memberSize = eli.getResultsSizeAfterPartialList();
        if (highIndex > memberSize) {
            highIndex = memberSize;
        }

        List<GenericValue> replyInformationLists=new ArrayList<>();
        for (int i=0;i<members.size();i++){
            Map map=new HashMap();
            map.putAll(members.get(i));
            String replyTime=map.get("replyTime");
            if (UtilValidate.isNotEmpty(replyTime)){
                map.put("replyTime",replyTime.substring(0,replyTime.length()-2));
            }
            replyInformationLists.add(map);
        }

        successResult.put("viewIndex",viewIndex);
        successResult.put("viewSize",viewSize);
        successResult.put("highIndex",highIndex);
        successResult.put("lowIndex",lowIndex);
        successResult.put("totalCount",memberSize);
        successResult.put("contactListId",contactListId);
       /* successResult.put("sortField",);*/
        successResult.put("data",replyInformationLists);

    }catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("查询错误");
    }finally {
        if (eli != null) {
            eli.close();
        }
    }
    return successResult;
}