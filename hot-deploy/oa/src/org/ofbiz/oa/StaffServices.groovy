import javolution.util.FastList
import org.ofbiz.base.util.Collections3
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.DispatchContext
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> saveEmployee() {
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String fullName = context.get("fullName");
    /*String workerSn = context.get("workerSn");
    String birthDate = context.get("birthDate");
    String gender = context.get("gender");
    String degree = context.get("degree");
    String diploma = context.get("diploma");*/
    String gender = context.get("gender");
    String post = context.get("post");
    context.remove("gender");//person中gender类型不一致

    String msg = "保存" + fullName + "成功";
    String partyId = context.get("partyId");
    if (UtilValidate.isNotEmpty(partyId)) {
        msg = "更新" + fullName + "成功";
        GenericValue genericValueForPerson = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        if (!UtilValidate.isEmpty(context.get("birthDate"))){
            genericValueForPerson.set("birthDate", java.sql.Date.valueOf(context.get("birthDate")));
        }
        context.remove("birthDate");
        genericValueForPerson.setNonPKFields(context);
        genericValueForPerson.store();
        context.put("post",post.substring(post.indexOf(",") + 1,post.length()));
        GenericValue genericValue = delegator.findByPrimaryKey("TblStaff", UtilMisc.toMap("partyId", partyId));
        context.put("gender", gender);
        genericValue.setNonPKFields(context);
        genericValue.store();
        //TODO:更新
    } else {
        serviceResult = runService("createPerson", dispatcher.getDispatchContext().getModelService("createPerson").makeValid(context, ModelService.IN_PARAM));
        partyId = serviceResult.get("partyId");
        context.put("partyId", partyId);
        context.put("gender", gender);
        context.put("jobState", "WORKING");

        context.put("post",post.substring(post.indexOf(",") + 1,post.length()));
        GenericValue staff = delegator.makeValidValue("TblStaff", context);
        staff.create();
        contextFor = [:];
        contextFor.put("staffId",partyId);
        contextFor.put("workDate",context.get("workDate").toString());
        contextFor.put("userLogin",userLogin);
        dispatcher.runSync("createSalarySend",contextFor);
    }
    //TODO:选择了岗位，需要保存组织关系。

    String[] position = post.split(",");
    String masterPartyId = position.length > 1 ? position[0] : "";
    String masterRoleTypeId = position.length > 1 ? position[1] : post;
    roleCount = from("PartyRole").where("partyId", partyId, "roleTypeId", masterRoleTypeId).queryCount();
    if (roleCount == 0) {
        Map<String, Object> partyRole = UtilMisc.toMap(
                "partyId", partyId,
                "roleTypeId", masterRoleTypeId,
                "userLogin", userLogin
        );
        dispatcher.runSync("createPartyRole", partyRole);
    }
    if(UtilValidate.isNotEmpty(masterPartyId)){
        GenericValue postRelationship = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdFrom", masterPartyId, "roleTypeIdFrom", masterRoleTypeId, "partyIdTo", partyId, "roleTypeIdTo", masterRoleTypeId, "partyRelationshipTypeId", "PROVIDE_POSITION", "fromDate", UtilDateTime.nowTimestamp()));
        delegator.createOrStore(postRelationship);
    }

    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId, "name", fullName));
    return successResult;
}

public Map<String, Object> removeEmployee() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    String fullName = context.get("fullName");
    String msg = "删除" + fullName + "成功";
    try {
        delegator.removeByAnd("TblStaff", UtilMisc.toMap("partyId", partyId));
    }
    catch (GenericEntityException ex) {
        msg = "删除" + fullName + "失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}

public Map<String, Object> getAllLowerDepartments() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String departmentId = context.get("departmentId");
    try {
        Map positionMap = UtilMisc.toMap("partyIdFrom", departmentId, "partyRelationshipTypeId", "GROUP_ROLLUP")
        List<GenericValue> members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);
        List dataList = new ArrayList();
        dataList.add(departmentId);
        if (UtilValidate.isNotEmpty(members)) {
            for (GenericValue member : members) {
                dataList.add(member.get("partyIdTo"));
                positionMap1 = UtilMisc.toMap("partyIdFrom", member.get("partyIdTo"), "partyRelationshipTypeId", "GROUP_ROLLUP")
                members1 = delegator.findByAnd("PartyRelationshipAndDetail", positionMap1);
                if (UtilValidate.isNotEmpty(members1)) {
                    for (GenericValue member1 : members1) {
                        dataList.add(member1.get("partyIdTo"));
                        positionMap2 = UtilMisc.toMap("partyIdFrom", member1.get("partyIdTo"), "partyRelationshipTypeId", "GROUP_ROLLUP")
                        members2 = delegator.findByAnd("PartyRelationshipAndDetail", positionMap2);
                        if (UtilValidate.isNotEmpty(members2)) {
                            for (GenericValue member2 : members2) {
                                dataList.add(member2.get("partyIdTo"));
                                positionMap3 = UtilMisc.toMap("partyIdFrom", member2.get("partyIdTo"), "partyRelationshipTypeId", "GROUP_ROLLUP");
                                members3 = delegator.findByAnd("PartyRelationshipAndDetail", positionMap3);
                                if (UtilValidate.isNotEmpty(members3)) {
                                    for (GenericValue member3 : members3) {
                                        dataList.add(member3.get("partyIdTo"));
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        successResult.put("data", dataList);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("获取下级部门错误");
    }

    return successResult;
}
public Map<String, Object> saveFamilySituation() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    familyId = context.get("familyId");
    partyId = context.get("partyId");
    String msg = "创建成功";
    if (familyId==null){
        if(context.get("relationship").equals("兄长")==true){
            familyId = delegator.getNextSeqId("TblFamilyInformation").toString();
            genericValue = delegator.makeValue("TblFamilyInformation",UtilMisc.toMap("familyId", familyId));
            genericValue.setNonPKFields(context);
            genericValue.create();
        }else {
            GenericValue genericValue =EntityQuery.use(delegator).from("TblFamilyInformation").where(UtilMisc.toMap("partyId",partyId,"relationship",context.get("relationship"))).queryOne();
            if(genericValue==null){
                familyId = delegator.getNextSeqId("TblFamilyInformation").toString();
                genericValue = delegator.makeValue("TblFamilyInformation",UtilMisc.toMap("familyId", familyId));
                genericValue.setNonPKFields(context);
                genericValue.create();
            }else {
                familyId =genericValue.get("familyId");
                genericValue = delegator.findByPrimaryKey("TblFamilyInformation",UtilMisc.toMap("familyId", familyId));
                genericValue.setNonPKFields(context);
                genericValue.store();
                msg = "更新成功";
            }
        }
    }else {
        genericValue = delegator.findByPrimaryKey("TblFamilyInformation",UtilMisc.toMap("familyId", familyId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        msg = "更新成功";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> removeFamilySituation() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String familyId = context.get("familyId");
    String partyId = context.get("partyId");
    String msg = "删除成功";
    try {
        delegator.removeByAnd("TblFamilyInformation", UtilMisc.toMap("familyId", familyId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> saveEducational() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    educationId = context.get("educationId");
    partyId = context.get("partyId");
    String msg = "创建成功"
    if (educationId == null){
        educationId = delegator.getNextSeqId("TblEducational").toString();
        genericValue = delegator.makeValue("TblEducational",UtilMisc.toMap("educationId",educationId));
        genericValue.setNonPKFields(context);
        genericValue.create();
    }else {
        genericValue = delegator.findByPrimaryKey("TblEducational",UtilMisc.toMap("educationId",educationId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        msg = "更新成功"
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> removeEducation() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String educationId = context.get("educationId");
    String partyId = context.get("partyId");
    String msg = "删除成功";
    try {
        delegator.removeByAnd("TblEducational", UtilMisc.toMap("educationId", educationId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> saveWorkExperience() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    workId = context.get("workId");
    partyId = context.get("partyId");
    String msg = "创建成功"
    if (workId == null){
        workId = delegator.getNextSeqId("TblWorkExperience").toString();
        genericValue = delegator.makeValue("TblWorkExperience",UtilMisc.toMap("workId",workId));
        genericValue.setNonPKFields(context);
        genericValue.create();
    }else {
        genericValue = delegator.findByPrimaryKey("TblWorkExperience",UtilMisc.toMap("workId",workId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        msg = "更新成功"
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> removeWorkExperience() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String workId = context.get("workId");
    String partyId = context.get("partyId");
    String msg = "删除成功";
    try {
        delegator.removeByAnd("TblWorkExperience", UtilMisc.toMap("workId", workId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> savePostChange() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    postId = context.get("postId");
    partyId = context.get("partyId");
    GenericValue StaffMap = EntityQuery.use(delegator).select().from("TblStaff").where(UtilMisc.toMap("partyId",partyId)).queryOne();
    String msg = "创建成功"
    Map map=new HashMap();
    map.put("partyId",partyId);
    map.put("department",context.get("newGroup"));
    map.put("post",context.get("newGroupSelect"));
    context.put("lastGroup",StaffMap.get("department"));
    String newPost = context.get("newGroupSelect");
    String lastPost = StaffMap.get("post");
    context.put("lastPost",lastPost);
    if(newPost.indexOf(",") >= 0){
        String[] position = newPost.split(",");
        String masterPartyId = position[0];
        String masterRoleTypeId = position[1];
        roleCount = from("PartyRole").where("partyId", partyId, "roleTypeId", masterRoleTypeId).queryCount();
        if (roleCount == 0) {
            Map<String, Object> partyRole = UtilMisc.toMap(
                    "partyId", partyId,
                    "roleTypeId", masterRoleTypeId,
                    "userLogin", userLogin
            );
            dispatcher.runSync("createPartyRole", partyRole);
        }
        if(UtilValidate.isNotEmpty(masterPartyId)){
            GenericValue postRelationship = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdFrom", masterPartyId, "roleTypeIdFrom", masterRoleTypeId, "partyIdTo", partyId, "roleTypeIdTo", masterRoleTypeId, "partyRelationshipTypeId", "PROVIDE_POSITION", "fromDate", UtilDateTime.nowTimestamp()));
            delegator.createOrStore(postRelationship);
        }
        context.put("newPost",masterRoleTypeId);
        map.put("post",masterRoleTypeId);
    }
    map.put("position",context.get("newPosition"));
    context.remove("newGroupSelect");

    if (postId == null){
        postId = delegator.getNextSeqId("TblPostChange").toString();
        genericValue = delegator.makeValue("TblPostChange",UtilMisc.toMap("postId",postId));
        genericValue.setNonPKFields(context);
        genericValue.create();
        delegator.store(delegator.makeValidValue("TblStaff",map));
    }else {
        genericValue = delegator.findByPrimaryKey("TblPostChange",UtilMisc.toMap("postId",postId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        delegator.store(delegator.makeValidValue("TblStaff",map));
        msg = "更新成功"
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> removePostChange() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String postId = context.get("postId");
    String partyId = context.get("partyId");
    String msg = "删除成功";
    try {
        delegator.removeByAnd("TblPostChange", UtilMisc.toMap("postId", postId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}

public Map<String, Object> saveDeparture() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg;
    String departureId = context.get("departureId");
    String partyId = context.get("fullName1");
    String jobState = context.get("jobState");
    if (jobState.equals("DEPARTURE")){
        msg = "DEPARTURE";
        successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
        return successResult;
    }
    if (departureId == null){
        departureId = delegator.getNextSeqId("TblDeparture").toString();
        genericValue = delegator.makeValue("TblDeparture",UtilMisc.toMap("departureId",departureId));
        Map map = new HashMap();
        map.put("departureId", departureId);
        map.put("partyId", partyId);
        map.put("departureDate", context.get("departureDate"));
        map.put("departureType", context.get("departureType"));
        map.put("departureReason", context.get("departureReason"));
        map.put("transferContent", context.get("transferContent"));
        map.put("remarks", context.get("remarks"));
        GenericValue genericValue = delegator.makeValidValue("TblDeparture", map);
        genericValue.create();
        //离职创建成功后将员工表内的“在职状态 jobState”修改为离职
        GenericValue InventoryTypes = EntityQuery.use(delegator)
                .select().from("TblStaff")
                .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId))
                .queryOne();
        Map<String,Object> valueMap = new HashMap<String,Object>();
        valueMap.putAll(InventoryTypes);
        valueMap.put("jobState","DEPARTURE");
        GenericValue NoticeTemplate = delegator.findByPrimaryKey("TblStaff",UtilMisc.toMap("partyId",partyId));
        NoticeTemplate.setNonPKFields(valueMap);
        NoticeTemplate.store();
        msg = "创建成功";
    }else {
        genericValue = delegator.findByPrimaryKey("TblDeparture",UtilMisc.toMap("departureId",departureId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        msg = "更新成功";
    }
    String fileId = context.get("fileId");
    List<String> newFileList = new ArrayList<String>();
    if(null != fileId && !"".equals(fileId)){
        String[] files = fileId.split(",");
        for(String file : files){
            newFileList.add(file);
        }
    }
    String entityName = "TblDeparture";
    List<String> oldFileList = new ArrayList<String>();
    List<GenericValue> departureFile = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", departureId, "entityName", entityName))).queryList();
    if(null != departureFile && departureFile.size() > 0){
        for(GenericValue workAccess : departureFile){
            oldFileList.add(workAccess.get("accessoryId").toString());
        }
    }
    saveFile(departureId,oldFileList,newFileList,entityName);
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}

public Map<String, Object> searchDeparture(){
    Locale locale = (Locale) context.get("locale");
    GenericValue userLoginId = context.get("userLogin");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    java.sql.Date startDate = context.get("startDate");
    java.sql.Date endDate = context.get("endDate");
    String departureType = context.get("departureType");
    String partyIdForSearch = context.get("partyIdForSearch");
    String partyId = context.get("partyId");
    List<EntityCondition> conditionList = FastList.newInstance();
    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(departureType)){
        conditionList.add(EntityCondition.makeCondition("departureType",EntityOperator.EQUALS,departureType));
    }
    if(UtilValidate.isNotEmpty(partyIdForSearch)){
        conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyIdForSearch));
    }
    if(UtilValidate.isNotEmpty(partyId)){
        conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
    }
    if(UtilValidate.isNotEmpty(startDate)&&UtilValidate.isNotEmpty(endDate)){
        timeCondition.add(startDate);
        timeCondition.add(endDate);
        conditionList.add(EntityCondition.makeCondition("departureDate",EntityOperator.BETWEEN,timeCondition));
    }
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    List<GenericValue> departureList = new ArrayList<>();
    EntityListIterator departureIterator = EntityQuery.use(delegator).select().from("TblDepartureDetail").where(condition).orderBy("departureDate DESC").queryIterator();
    if(null != departureIterator && departureIterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = departureIterator.getResultsSizeAfterPartialList();
        departureList = departureIterator.getPartialList(lowIndex, viewSize);
        }
    departureIterator.close();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("lowIndex",lowIndex);
    viewData.put("startDate",startDate);
    viewData.put("endDate",endDate);
    viewData.put("departureType",departureType);
    viewData.put("partyIdForSearch",partyIdForSearch);
    viewData.put("partyId",partyId);
    viewData.put("departureList",departureList);
    successResult.put("data",viewData);
    return successResult;

}

public Map<String, Object> searchRewardAndPunishment(){
    Locale locale = (Locale) context.get("locale");
    GenericValue userLoginId = context.get("userLogin");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    java.sql.Date startDate = context.get("startDate");
    java.sql.Date endDate = context.get("endDate");
    String number = context.get("number");
    String name = context.get("name");
    String type = context.get("type");
    String level = context.get("level");
    String partyIdForSearch = context.get("partyIdForSearch");
    List<EntityCondition> conditionList = FastList.newInstance();
    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(number)){
        conditionList.add(EntityCondition.makeCondition("number",EntityOperator.LIKE,"%"+number+"%"));
    }
    if(UtilValidate.isNotEmpty(name)){
        conditionList.add(EntityCondition.makeCondition("name",EntityOperator.LIKE,"%"+name+"%"));
    }
    if(UtilValidate.isNotEmpty(type)){
        conditionList.add(EntityCondition.makeCondition("type",EntityOperator.EQUALS,type));
    }
    if(UtilValidate.isNotEmpty(level)){
        conditionList.add(EntityCondition.makeCondition("level",EntityOperator.EQUALS,level));
    }
    if(UtilValidate.isNotEmpty(partyIdForSearch)){
        conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyIdForSearch));
    }
    if(UtilValidate.isNotEmpty(startDate)&&UtilValidate.isNotEmpty(endDate)){
        timeCondition.add(startDate);
        timeCondition.add(endDate);
        conditionList.add(EntityCondition.makeCondition("date",EntityOperator.BETWEEN,timeCondition));
    }
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    List<GenericValue> rewordsPunishmentList = new ArrayList<>();
    EntityListIterator rewordsPunishmentIterator = EntityQuery.use(delegator).select().from("TblRewordsPunishmentDetail").where(condition).orderBy("date DESC").queryIterator();
    if(null != rewordsPunishmentIterator && rewordsPunishmentIterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = rewordsPunishmentIterator.getResultsSizeAfterPartialList();
        rewordsPunishmentList = rewordsPunishmentIterator.getPartialList(lowIndex, viewSize);
    }
    rewordsPunishmentIterator.close();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("lowIndex",lowIndex);
    viewData.put("startDate",startDate);
    viewData.put("endDate",endDate);
    viewData.put("number",number);
    viewData.put("name",name);
    viewData.put("type",type);
    viewData.put("level",level);
    viewData.put("partyIdForSearch",partyIdForSearch);
    viewData.put("rewordsPunishmentList",rewordsPunishmentList);
    successResult.put("data",viewData);
    return successResult;

}

public Map<String, Object> findRewardsAndPunishment(){
    Locale locale = (Locale) context.get("locale");
    GenericValue userLoginId = context.get("userLogin");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    String partyId = context.get("partyId");
    List<EntityCondition> conditionList = FastList.newInstance();
    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(partyId)){
        conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
    }
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    List<GenericValue> rewordsPunishmentList = new ArrayList<>();
    EntityListIterator rewordsPunishmentIterator = EntityQuery.use(delegator).select().from("TblRewordsPunishmentDetail").where(condition).queryIterator();
    if(null != rewordsPunishmentIterator && rewordsPunishmentIterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = rewordsPunishmentIterator.getResultsSizeAfterPartialList();
        rewordsPunishmentList = rewordsPunishmentIterator.getPartialList(lowIndex, viewSize);
    }
    rewordsPunishmentIterator.close();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("partyId",partyId);
    viewData.put("rewordsPunishmentList",rewordsPunishmentList);
    successResult.put("data",viewData);
    return successResult;

}

public Map<String, Object> searchTrain(){
    Locale locale = (Locale) context.get("locale");
    GenericValue userLoginId = context.get("userLogin");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    java.sql.Date startDate = context.get("startDate");
    java.sql.Date endDate = context.get("endDate");
    String number = context.get("number");
    String name = context.get("name");
    String type = context.get("type");
    String partyIdForSearch = context.get("partyIdForSearch");
    List<EntityCondition> conditionList = FastList.newInstance();
    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(number)){
        conditionList.add(EntityCondition.makeCondition("number",EntityOperator.LIKE,"%"+number+"%"));
    }
    if(UtilValidate.isNotEmpty(name)){
        conditionList.add(EntityCondition.makeCondition("name",EntityOperator.LIKE,"%"+name+"%"));
    }
    if(UtilValidate.isNotEmpty(type)){
        conditionList.add(EntityCondition.makeCondition("type",EntityOperator.EQUALS,type));
    }
    if(UtilValidate.isNotEmpty(partyIdForSearch)){
        conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyIdForSearch));
    }
    if(UtilValidate.isNotEmpty(startDate)&&UtilValidate.isNotEmpty(endDate)){
        timeCondition.add(startDate);
        timeCondition.add(endDate);
        conditionList.add(EntityCondition.makeCondition("date",EntityOperator.BETWEEN,timeCondition));
    }
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    List<GenericValue> trainList = new ArrayList<>();
    EntityListIterator trainIterator = EntityQuery.use(delegator).select().from("TblTrainDetail").where(condition).orderBy("date DESC").queryIterator();
    if(null != trainIterator && trainIterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = trainIterator.getResultsSizeAfterPartialList();
        trainList = trainIterator.getPartialList(lowIndex, viewSize);
    }
    trainIterator.close();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("lowIndex",lowIndex);
    viewData.put("startDate",startDate);
    viewData.put("endDate",endDate);
    viewData.put("number",number);
    viewData.put("name",name);
    viewData.put("type",type);
    viewData.put("partyIdForSearch",partyIdForSearch);
    viewData.put("trainList",trainList);
    successResult.put("data",viewData);
    return successResult;

}

public Map<String, Object> removeDeparture() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String departureId = context.get("departureId");
    String partyId = context.get("partyId");
    String msg = "删除成功";
    try {
        delegator.removeByAnd("TblDeparture", UtilMisc.toMap("departureId", departureId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}

public Map<String,Object> FindContract(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    String data ="";
    if(UtilValidate.isNotEmpty(partyId)){
        List conditionList = new ArrayList();
        java.sql.Date todayDate = new java.sql.Date(new java.util.Date().getTime());
        todayForSql = UtilDateTime.toSqlDate(todayDate.toString(), "yyyy-MM-dd");
        conditionList.add(EntityCondition.makeCondition("startDate", EntityOperator.LESS_THAN_EQUAL_TO, todayForSql));
        conditionList.add(EntityCondition.makeCondition("endDate", EntityOperator.GREATER_THAN_EQUAL_TO, todayForSql));
        conditionList.add(EntityCondition.makeCondition("contractStatus", EntityOperator.NOT_EQUAL, "CONTRACT_STATUS_D"));
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("contractType", EntityOperator.EQUALS, "LABOR_CONTRACT"));
        EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
        ContractForEmployeeList = EntityQuery.use(delegator).select().from("ContractList").where(condition).queryList();
        if(UtilValidate.isNotEmpty(ContractForEmployeeList)){
          data = "1";
        }
    }
    successResult.put("data",data);
    return successResult;
}

public Map<String,Objects> searchContract(){

    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    String contractNumber1 = context.get("contractNumber")==null?"":context.get("contractNumber");
    String contractName1 = context.get("contractName")==null?"":context.get("contractName");
    String contractType = context.get("contractType")==null?"":context.get("contractType");
    String startDate = context.get("startDate")==null?"":context.get("startDate");
    String endDate = context.get("endDate")==null?"":context.get("endDate");
    String partyIdForSearch = context.get("partyIdForSearch")==null?"":context.get("partyIdForSearch");
    String state = context.get("state")==null?"":context.get("state");
    if(state == null || state == ""){
        state = "CONTRACT_STATUS_B";
    }
    List conditionList = new ArrayList();
    if(UtilValidate.isNotEmpty(contractNumber1)){
        String contractNumber= contractNumber1.trim();
        conditionList.add(EntityCondition.makeCondition("contractNumber", EntityOperator.LIKE, "%"+contractNumber+"%"));
    }
    if(UtilValidate.isNotEmpty(contractName1)){
        String contractName= contractName1.trim();
        conditionList.add(EntityCondition.makeCondition("contractName", EntityOperator.LIKE, "%"+contractName+"%"));
    }
    if(UtilValidate.isNotEmpty(contractType)){
        conditionList.add(EntityCondition.makeCondition("contractType", EntityOperator.EQUALS, contractType));
    }
    if (UtilValidate.isNotEmpty(startDate) && !UtilValidate.isNotEmpty(endDate)) {
        startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
        conditionList.add(EntityCondition.makeCondition("signDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDateForSql));
    }
    if (UtilValidate.isNotEmpty(endDate) && !UtilValidate.isNotEmpty(startDate)) {
        endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
        conditionList.add(EntityCondition.makeCondition("signDate", EntityOperator.LESS_THAN_EQUAL_TO, endDateForSql));
    }
    if (UtilValidate.isNotEmpty(endDate) && UtilValidate.isNotEmpty(startDate)) {
        startDateForSql = UtilDateTime.toSqlDate(startDate, "yyyy-MM-dd");
        endDateForSql = UtilDateTime.toSqlDate(endDate, "yyyy-MM-dd");
        conditionList.add(EntityCondition.makeCondition("signDate", EntityOperator.BETWEEN, UtilMisc.toList(startDateForSql,endDateForSql)));
    }
    if(UtilValidate.isNotEmpty(partyIdForSearch)){
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdForSearch));
    }
    if(UtilValidate.isNotEmpty(state) && state != "1") {
        java.sql.Date todayDate = new java.sql.Date(new java.util.Date().getTime());
        todayForSql = UtilDateTime.toSqlDate(todayDate.toString(), "yyyy-MM-dd");
        if ("CONTRACT_STATUS_D".equals(state)) {
            conditionList.add(EntityCondition.makeCondition("contractStatus", EntityOperator.EQUALS, state));
        }
        else if ("CONTRACT_STATUS_A".equals(state)) {
            conditionList.add(EntityCondition.makeCondition("startDate", EntityOperator.GREATER_THAN, todayForSql));
            conditionList.add(EntityCondition.makeCondition("contractStatus", EntityOperator.NOT_EQUAL, "CONTRACT_STATUS_D"));
        }
        else if ("CONTRACT_STATUS_B".equals(state)) {
            conditionList.add(EntityCondition.makeCondition("startDate", EntityOperator.LESS_THAN_EQUAL_TO, todayForSql));
            conditionList.add(EntityCondition.makeCondition("endDate", EntityOperator.GREATER_THAN_EQUAL_TO, todayForSql));
            conditionList.add(EntityCondition.makeCondition("contractStatus", EntityOperator.NOT_EQUAL, "CONTRACT_STATUS_D"));
        }
        else if ("CONTRACT_STATUS_C".equals(state)) {
            conditionList.add(EntityCondition.makeCondition("endDate", EntityOperator.LESS_THAN, todayForSql));
            conditionList.add(EntityCondition.makeCondition("contractStatus", EntityOperator.NOT_EQUAL, "CONTRACT_STATUS_D"));
        }
    }

    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));


    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    EntityListIterator ContractForEmployeeList = EntityQuery.use(delegator).select().from("ContractList").where(condition).orderBy("signDate DESC").queryIterator();
    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    if(null != ContractForEmployeeList && ContractForEmployeeList.getResultsSizeAfterPartialList() > 0){
        totalCount = ContractForEmployeeList.getResultsSizeAfterPartialList();
        List<GenericValue> employeeList = ContractForEmployeeList.getPartialList(lowIndex, viewSize);

        for(GenericValue genericValue :employeeList){
            Map<String,Object> map = new HashMap<String,Object>();
            map.putAll(genericValue);
            String partyId = map.get("partyId");
            String workerSn = "";
            String fullName = "";
            List<GenericValue> resultList = delegator.findByAnd("StaffDetail",UtilMisc.toMap("partyId",partyId));
            if (resultList.size()!=0){
                Map<String,Object> resultMap = resultList.get(0);
                workerSn = resultMap.get("workerSn");//员工编号
                fullName = resultMap.get("fullName");//员工姓名
            }
            map.put("workerSn",workerSn);
            map.put("fullName",fullName);
            list.add(map);
        }
    }
    ContractForEmployeeList.close();
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("list",list);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("contractNumber",contractNumber1);
    map.put("contractName",contractName1);
    map.put("contractType",contractType);
    map.put("startDate",startDate);
    map.put("endDate",endDate);
    map.put("partyIdForSearch",partyIdForSearch);
    map.put("state",state);
    successResult.put("data", map);
    return successResult;
}
public Map<String,Object> editContract() {
    String contractId = context.get("contractId")==null?"":context.get("contractId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue contractValue = EntityQuery.use(delegator).select().from("TblContract").where(UtilMisc.toMap("contractId",contractId)).queryOne();
    Map<String,Object> map = new HashMap<String,Object>();
    map.putAll(contractValue);
    String partyId = map.get("partyId");
    String workerSn = "";
    String fullName = "";
    String groupName = "";
    String occupationName = "";
    List<GenericValue> resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
    if (resultList.size()!=0){
        Map<String,Object> resultMap = resultList.get(0);
        workerSn = resultMap.get("workerSn");//员工编号
        fullName = resultMap.get("fullName");//员工姓名
        groupName = resultMap.get("departmentName");
        occupationName = resultMap.get("occupationName");
    }

    List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", contractId, "entityName", "TblContract"))).queryList();
    String fileId = "";
    if(null != genericValueList && genericValueList.size() > 0){
        for(GenericValue genericValue : genericValueList){
            fileId = fileId + genericValue.get("accessoryId") + ",";
        }
    }
    if(!"".equals(fileId)){
        fileId = fileId.substring(0,fileId.length() - 1);
        context.put("files",fileId);
        Map data = runService("searchFileByIds", dispatcher.getDispatchContext().getModelService("searchFileByIds").makeValid(context, ModelService.IN_PARAM));
        Map filemap = data.get("data");
        map.put("fileId",filemap.get("fileIds"));
        map.put("fileList",filemap.get("fileList"));
    }

//    String fileId = map.get("fileId");
//    if (null != fileId && !"".equals(fileId)) {
//        fileId = fileId.substring(0,fileId.length());
//        context.put("files",fileId);
//        Map data = runService("searchFileByIds", dispatcher.getDispatchContext().getModelService("searchFileByIds").makeValid(context, ModelService.IN_PARAM));
//        Map filemap = data.get("data");
//        map.put("fileId",filemap.get("fileIds"));
//        map.put("fileList",filemap.get("fileList"));
//    }
    map.put("workerSn",workerSn);
    map.put("fullName",fullName);
    map.put("groupName",groupName);
    map.put("occupationName",occupationName);
    successResult.put("contractMap", map);
    return successResult;
}

public Map<String,Object> saveContract() {
    String contractStatus = "CONTRACT_STATUS_A";
    GenericValue userLogin = (GenericValue)context.get("userLogin");
    String loginId = userLogin.getString("partyId");
    String partyId = context.get("partyId");//员工ID
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String message = "创建成功";
    String contractId = context.get("contractId");
    String editStartDate = context.get("editStartDate");
    String editEndDate = context.get("editEndDate");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date startDate = format.parse(editStartDate);
    Date endDate = format.parse(editEndDate);
    String jobState = context.get("jobState");
    String contractType = context.get("contractType")
    if (jobState.equals("DEPARTURE") && !contractType.equals("LABOR_CONTRACT")){
        message = "DEPARTURE";
        successResult.put("data", UtilMisc.toMap("message", message, "partyId",context.get("fullName")));
        return successResult;
    }

    if (null != contractId && UtilValidate.isNotEmpty(contractId)) {
            message = "更新成功";
            context.put("startDate", new java.sql.Date(startDate.getTime()));
            context.put("endDate", new java.sql.Date(endDate.getTime()));
            GenericValue editContract = delegator.makeValidValue("TblContract", context);
            editContract.store();
    } else {
        contractId = delegator.getNextSeqId("TblContract").toString();//获取主键ID
        Map map = new HashMap();
        map.put("contractId", contractId);
        map.put("startDate", new java.sql.Date(startDate.getTime()));
        map.put("endDate", new java.sql.Date(endDate.getTime()));
        map.put("content", context.get("content"));
        map.put("remarks", context.get("remarks"));
        map.put("signDate", context.get("signDate"));
        map.put("contractType", context.get("contractType"));
        map.put("salary", context.get("salary"));
        map.put("contractName", context.get("contractName"));
        map.put("contractNumber", context.get("contractNumber"));
        map.put("partyId", context.get("fullName"));
        map.put("contractStatus", contractStatus);
        GenericValue createContract = delegator.makeValidValue("TblContract", map)
        createContract.create();
        if (!jobState.equals("WORKING")){
            //劳动合同创建成功后将员工表内的“在职状态 jobState”修改为在职
            GenericValue InventoryTypes = EntityQuery.use(delegator)
                    .select().from("TblStaff")
                    .where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,context.get("fullName")))
                    .queryOne();
            Map<String,Object> valueMap = new HashMap<String,Object>();
            valueMap.putAll(InventoryTypes);
            valueMap.put("jobState","WORKING");
            GenericValue NoticeTemplate = delegator.findByPrimaryKey("TblStaff",UtilMisc.toMap("partyId",context.get("fullName")));
            NoticeTemplate.setNonPKFields(valueMap);
            NoticeTemplate.store();
        }
        List<GenericValue> adjustmentList = EntityQuery.use(delegator).select().from("TblSalaryAdjustment").where(EntityCondition.makeCondition("partyId", partyId)).queryList();
        if(null == adjustmentList || adjustmentList.size() <= 0){
            Map adjustmentMap = new HashMap();
            String salaryId = delegator.getNextSeqId("TblSalaryAdjustment").toString();//获取主键ID
            adjustmentMap.put("salaryId",salaryId);
            adjustmentMap.put("adjustmentTime",new Timestamp(startDate.getTime()));
            adjustmentMap.put("startTime",new Timestamp(((Date)context.get("signDate")).getTime()));
            adjustmentMap.put("partyId",context.get("fullName"));
            adjustmentMap.put("inputId",loginId);
            adjustmentMap.put("remarks","");
            GenericValue salaryAdjustment = delegator.makeValidValue("TblSalaryAdjustment",adjustmentMap);
            salaryAdjustment.create();
            Map<String,Object> salaryHistoryMap = new HashMap<String,Object>();
            salaryHistoryMap.put("salaryId",salaryId);
            salaryHistoryMap.put("entryId","10001");
            salaryHistoryMap.put("oldAmount","");
            salaryHistoryMap.put("newAmount",context.get("salary"));
            GenericValue salaryInfoHistory = delegator.makeValidValue("TblSalaryInfoHistory",salaryHistoryMap);
            salaryInfoHistory.create();
        }
    }
    String fileId = context.get("fileId");
    List<String> newFileList = new ArrayList<String>();
    if(null != fileId && !"".equals(fileId)){
        String[] files = fileId.split(",");
        for(String file : files){
            newFileList.add(file);
        }
    }
    List<String> oldFileList = new ArrayList<String>();
    List<GenericValue> contractFile = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", contractId, "entityName", "TblContract"))).queryList();
    if(null != contractFile && contractFile.size() > 0){
        for(GenericValue workAccess : contractFile){
            oldFileList.add(workAccess.get("accessoryId").toString());
        }
    }
    String entityName = "TblContract";
    saveFile(contractId,oldFileList,newFileList,entityName);
    successResult.put("data", UtilMisc.toMap("message", message, "contractId", contractId ,"partyId",context.get("fullName")));
    return successResult;
}

public void saveFile(String id,List<String> oldFileList,List<String> newFileList, String entityName){
    for(String fileId:newFileList) {
        if (!oldFileList.contains(fileId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            String fileScopeId = delegator.getNextSeqId("TblFileScope");
            map.put("fileScopeId", fileScopeId);
            map.put("entityName", entityName);
            map.put("dataId", id);
            map.put("accessoryId", fileId);
            GenericValue accessory = delegator.makeValidValue("TblFileScope", map);
            accessory.create();
        }
    }
    for(String fileId1:oldFileList){
        if(!newFileList.contains(fileId1)) {
            delegator.removeByAnd("TblFileScope", UtilMisc.toMap("entityName", entityName, "dataId", id, "accessoryId", fileId1));
            GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId1)).queryOne();
            if (null != fileData&& "ATTACHMENT_FILE".equals(fileData.get("dataResourceTypeId").toString())) {
                File file = new File(fileData.get("objectInfo").toString() + fileData.get("dataResourceName").toString());
                if (file.exists()) {
                    file.delete();
                }
                
                delegator.removeByAnd("TblDataScope", UtilMisc.toMap("dataId", fileId1,"entityName", "DataResource"));
                delegator.removeByAnd("DataResourceRole", UtilMisc.toMap("dataResourceId", fileId1));
                delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", fileId1));

            }
        }
    }
}

public Map<String, Object> cancelContract() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String contractId = context.get("contractId");
    GenericValue genericValue = EntityQuery.use(delegator).select().from("TblContract").where(UtilMisc.toMap("contractId",contractId)).queryOne();
    genericValue.put("contractStatus","CONTRACT_STATUS_D");
    genericValue.store();
    successResult.put("data", UtilMisc.toMap("",""));
    return successResult;
}

public Map<String, Object> saveReword() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    rewordId = context.get("rewordId");
    partyId = context.get("partyId");
    String msg = "创建成功"
    if (rewordId == null){
        rewordId = delegator.getNextSeqId("TblRewordsPunishment").toString();
        genericValue = delegator.makeValue("TblRewordsPunishment",UtilMisc.toMap("rewordId",rewordId));
        genericValue.setNonPKFields(context);
        genericValue.create();
    }else {
        genericValue = delegator.findByPrimaryKey("TblRewordsPunishment",UtilMisc.toMap("rewordId",rewordId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        msg = "更新成功"
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> removeReword() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String rewordId = context.get("rewordId");
    String partyId = context.get("partyId");
    String msg = "删除成功";
    try {
        delegator.removeByAnd("TblRewordsPunishment", UtilMisc.toMap("rewordId", rewordId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> saveTrain() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    trainId = context.get("trainId");
    partyId = context.get("partyId");
    String msg = "创建成功"
    if (trainId == null){
        trainId = delegator.getNextSeqId("TblTrain").toString();
        genericValue = delegator.makeValue("TblTrain",UtilMisc.toMap("trainId",trainId));
        genericValue.setNonPKFields(context);
        genericValue.create();
    }else {
        genericValue = delegator.findByPrimaryKey("TblTrain",UtilMisc.toMap("trainId",trainId));
        genericValue.setNonPKFields(context);
        genericValue.store();
        msg = "更新成功"
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}
public Map<String, Object> removeTrain() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String trainId = context.get("trainId");
    String partyId = context.get("partyId");
    String msg = "删除成功";
    try {
        delegator.removeByAnd("TblTrain", UtilMisc.toMap("trainId", trainId));
    }
    catch (GenericEntityException ex) {
        msg = "删除失败";
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", partyId));
    return successResult;
}

public Map<String,Object> searchPartyInfo(){
    List<GenericValue> resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
    Map<String,Object> resultMap = new HashMap<String,Object>();
    if (resultList.size()!=0){
        Map<String,Object> map = resultList.get(0);
        resultMap.put("groupName",map.get("departmentName"));//部门
        resultMap.put("groupId",map.get("departmentId"));//部门
        resultMap.put("workerSn",map.get("workerSn"));//工号
        resultMap.put("occupationName",map.get("occupationName"));//岗位
        resultMap.put("partyId",partyId);//用户Id
        resultMap.put("jobState",map.get("jobState"));//员工在职状态
    }
    Map<String,Object> result = ServiceUtil.returnSuccess();
    result.put("data",resultMap);
    return result;
}

public Map<String,Object> transactionRecord(){
    GenericValue resultList = EntityQuery.use(delegator).select().from("StaffDetails").where(UtilMisc.toMap("partyId",partyId)).queryOne();
    Map<String,Object> resultMap = new HashMap<String,Object>();
    if (UtilValidate.isNotEmpty(resultList)){
        resultMap.put("fullName",resultList.get("fullName"));
        resultMap.put("workerSn",resultList.get("workerSn"));
        resultMap.put("partyId",partyId);
        resultMap.put("lastGroup",resultList.get("departmentName"));
        resultMap.put("lastPost",resultList.get("occupationName"));
        resultMap.put("lastPosition",resultList.get("position"));
        resultMap.put("flag","1");
    }else{
        resultMap.put("flag","0");
    }
    Map<String,Object> result = ServiceUtil.returnSuccess();
    result.put("data",resultMap);
    return result;
}

public Map<String, Object> createUserLogin() {
    DispatchContext dctx = dispatcher.getDispatchContext();
    ModelService model = dctx.getModelService("createUserLogin");
    Map<String, Object> successResult = runService("createUserLogin", model.makeValid(context, ModelService.IN_PARAM));
    if(ServiceUtil.isSuccess(successResult)){
        List<String> assignedGroups = context.get("securityGroups");
        for (String groupId : assignedGroups) {
            runService("addUserLoginToSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId, "groupId", groupId));
        }
    }
    //根据loginid找到partyid，在根据partyid找到fullname、staffName
    String fullName = "";
    String staffName = "";
    GenericValue userLoginEntity = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId",userLoginId), false);
    if(null != userLoginEntity){
        GenericValue personEntity = delegator.findOne("Person", UtilMisc.toMap("partyId", userLoginEntity.get("partyId")), false);
        GenericValue staffEntity = delegator.findOne("TblStaff", UtilMisc.toMap("partyId", userLoginEntity.get("partyId")), false)
        if(null != personEntity && null != staffEntity){
            fullName = personEntity.get("fullName") == null ? personEntity.get("firstName") : personEntity.get("fullName");
            staffName = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", staffEntity.get("department")), false).get("groupName");
        }
    }
    successResult.put("data", UtilMisc.toMap("msg", "创建成功", "fullName", fullName, "userLoginId", userLoginId, "staffName", staffName));
    return successResult;
}

public Map<String, Object> updateUserLogin() {
    DispatchContext dctx = dispatcher.getDispatchContext();
    ModelService model = dctx.getModelService("updatePassword");
    Map<String, Object> successResultData = runService("updatePassword", model.makeValid(context, ModelService.IN_PARAM));
    if(ServiceUtil.isSuccess(successResultData)){
        String userLoginId = context.get("userLoginId")
        List<GenericValue> oldGroups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
        List<String> oldGroupIds = Collections3.extractToList(oldGroups, "groupId");
        List<String> assignedGroups = context.get("securityGroups");
        for (String groupId : assignedGroups) {
            if(!oldGroupIds.contains(groupId)){
                runService("addUserLoginToSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId, "groupId", groupId));
            }
            oldGroupIds.remove(groupId);
        }
        for (String groupId: oldGroupIds) {
            Map<String, String> deleteMap = UtilMisc.toMap("userLoginId", userLoginId, "groupId", groupId);
            delegator.removeByAnd("UserLoginSecurityGroup", deleteMap);
            //runService("removeUserLoginToSecurityGroupNew", UtilMisc.toMap("userLoginId", userLoginId, "groupId", groupId));
        }
    }
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    successResult.put("data", UtilMisc.toMap("msg", "更新成功"));
    return successResult;
}

/**
 * 判断是否有管理员权限
 * @return
 */
public Map<String,Object> hrEditUserLogin(){
    DispatchContext dctx = dispatcher.getDispatchContext();
    ModelService model = dctx.getModelService("updatePassword");
    Map<String, Object> successResult = runService("isPermission", model.makeValid(context, ModelService.IN_PARAM));
    return successResult;
}

public Map<String, Object> searchCardId(){
    String cardId = context.get("num");
    String partyId = context.get("partyId");
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    List conditionList = new ArrayList();
    conditionList.add(EntityCondition.makeCondition("cardId", EntityOperator.EQUALS, cardId));
    conditionList.add(EntityCondition.makeCondition("cardId", EntityOperator.NOT_EQUAL, "null"));
    GenericValue genericValue = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("cardId",cardId)).queryOne();
    if(UtilValidate.isNotEmpty(genericValue) && !partyId.equals(genericValue.get("partyId").toString())){
        successResult.put("flag", "1");
    }else{
        successResult.put("flag", "2");
    }

    return successResult;
}

public Map<String, Object> changeContractType() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String enumTypeId = context.get("enumTypeId");
    List list = new ArrayList();
    if(UtilValidate.isNotEmpty(enumTypeId)){
        list=EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId",enumTypeId)).queryList();
    }
    successResult.put("data", list);
    return successResult;
}
