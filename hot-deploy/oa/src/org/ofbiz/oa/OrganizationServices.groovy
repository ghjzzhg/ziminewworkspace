package org.ofbiz.oa

import javolution.util.FastList
import javolution.util.FastMap
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityExpr
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.GeneralServiceException
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

/**
 * Created by galaxypan on 2015/5/21.
 */

public Map<String, Object> updateTitle() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();

    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();

    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String enumId = context.get("enumId");
    String name = context.get("name");
    String level = context.get("level");
    GenericValue positionLevel = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId), false);
    if(UtilValidate.isNotEmpty(name)){
        positionLevel.set("description", name);
    }
    if(UtilValidate.isNotEmpty(level)){
        level = StringUtils.leftPad(level, 2, "0");
        positionLevel.set("enumCode", level);
        positionLevel.set("sequenceId", level);
    }
    positionLevel.store();
    return successResult;
}

public Map<String, Object> removeSubOrg() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();

    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();

    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String partyIdFrom = context.get("partyIdFrom");
    String partyId = context.get("partyId");

    List partyRelationships = from("PartyRelationship").where("partyIdFrom", partyIdFrom, "partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP").filterByDate().queryList();
    if (partyRelationships) {
        delegator.removeAll(partyRelationships);
    }
    return successResult;
}

public Map<String, Object> updateOrgName() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();

    String name = context.get("name");
    String partyId = context.get("partyId");

    GenericValue partyGroup = from("PartyGroup").where("partyId", partyId).queryOne();
    partyGroup.set("groupName", name);
    partyGroup.store();
    return successResult;
}

public Map<String, Object> saveOrganization() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String partyId = context.get("partyId");
    String parentPartyId = context.get("parentPartyId");
    String manager = context.get("manager");
    String msg = "保存" + context.get("groupName") + "成功";
    //新建或更新PartyGroup

    if (UtilValidate.isNotEmpty(partyId)) {
        runService("updatePartyGroup", dispatcher.getDispatchContext().getModelService("updatePartyGroup").makeValid(context, ModelService.IN_PARAM));
        msg = "更新" + context.get("groupName") + "成功";
    } else {
        serviceResult = runService("createPartyGroup", dispatcher.getDispatchContext().getModelService("createPartyGroup").makeValid(context, ModelService.IN_PARAM));
        partyId = serviceResult.get("partyId");
    }

    roleCount = from("PartyRole").where("partyId", partyId, "roleTypeId", "INTERNAL_ORGANIZATIO").queryCount();
    if (roleCount == 0) {
        Map<String, Object> partyRole = UtilMisc.toMap(
                "partyId", partyId,
                "roleTypeId", "INTERNAL_ORGANIZATIO",
                "userLogin", userLogin
        );
        dispatcher.runSync("createPartyRole", partyRole);
    }


    List<GenericValue> partyRelationships = from("PartyRelationship").where(/*"partyIdFrom", parentPartyId, */ "partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_ROLLUP").filterByDate().queryList();

//    //上级变化时修改关系
//    if (UtilValidate.isNotEmpty(partyRelationships) && (!partyRelationships.get(0).get("partyIdFrom").equals(parentPartyId))) {
//        delegator.removeAll(partyRelationships);
//        //跨级间的关联关系,便于查询
//        partyRelationships = from("PartyRelationship").where(/*"partyIdFrom", parentPartyId, */ "partyIdTo", partyId, "partyRelationshipTypeId", "GROUP_LINK").filterByDate().queryList();
//        if (UtilValidate.isNotEmpty(partyRelationships)) {
//            delegator.removeAll(partyRelationships);
//        }
//    }

    //如果有上级id传入，建立上下级关系
    if (UtilValidate.isEmpty(partyRelationships) && UtilValidate.isNotEmpty(parentPartyId)) {
        GenericValue parent = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdFrom", parentPartyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "partyIdTo", partyId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "GROUP_ROLLUP", "fromDate", UtilDateTime.nowTimestamp()));
        delegator.createOrStore(parent);

        processOrganizationLink(delegator, partyId, parentPartyId);
    }
    //如果有负责人id传入，建立负责人与部门关系
    if (UtilValidate.isNotEmpty(manager)) {
        GenericValue partyRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", manager, "roleTypeId", "MANAGER"));
        delegator.createOrStore(partyRole);
        GenericValue managerRelationship = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "partyIdTo", manager, "roleTypeIdTo", "MANAGER", "partyRelationshipTypeId", "MANAGER", "fromDate", UtilDateTime.nowTimestamp()));
        delegator.createOrStore(managerRelationship);
    }
    successResult.put("data", UtilMisc.toMap("msg", msg, "parentPartyId", parentPartyId, "partyId", partyId));
    return successResult;
}

//创建跨级间的关联，便于查询: 低级别(from) -> 高级别(to)
private void processOrganizationLink(GenericDelegator delegator, String partyId, String parentPartyId) {
    List<GenericValue> partyRelationships = from("PartyRelationship").where(/*"partyIdFrom", parentPartyId, */ "partyIdTo", parentPartyId, "partyRelationshipTypeId", "GROUP_ROLLUP").filterByDate().queryList();
    if (UtilValidate.isNotEmpty(partyRelationships)) {
        String grandPartyId = partyRelationships.get(0).get("partyIdFrom")
        GenericValue parent = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdTo", grandPartyId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyIdFrom", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "GROUP_LINK", "fromDate", UtilDateTime.nowTimestamp()));
        delegator.createOrStore(parent);
        processOrganizationLink(delegator, partyId, grandPartyId);
    }
}


public Map<String, Object> addOccupation() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String partyId = context.get("partyId");//部门参数
    String generalOccupation = context.get("generalOccupation");
    String newOccupation = context.get("newOccupation");//新岗位
    String newOccupationMaster = context.get("newOccupationMaster");//新岗位主管岗位
    String occupationName = newOccupation;
    String managerForOccupation = context.get("managerForOccupation");//新岗位负责人
    try {
        if (UtilValidate.isNotEmpty(generalOccupation)) {//选择已存在的岗位时拷贝一份，防止互相篡改
            newOccupation = delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", generalOccupation)).get("description");
        }//全新岗位
        Map result = dispatcher.runSync("createRoleType", UtilMisc.toMap("userLogin", userLogin, "roleTypeId", delegator.getNextSeqId("RoleType"), "parentTypeId", "JOB_POSITION", "description", newOccupation));
        GenericValue newRole = result.get("roleType");
        generalOccupation = newRole.get("roleTypeId");
        String msg = "添加岗位【" + occupationName + "】成功";
        //新建PartyRole
        dispatcher.runSync("createPartyRole", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", generalOccupation));

        if (UtilValidate.isNotEmpty(newOccupationMaster)) {//设置主管岗位
            String[] position = newOccupationMaster.split(",");
            String masterPartyId = position[0];
            String masterRoleTypeId = position[1];
            dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", masterPartyId, "roleTypeIdFrom", masterRoleTypeId, "partyIdTo", partyId, "roleTypeIdTo", generalOccupation, "partyRelationshipTypeId", "MASTER_POSITION"));
//            //保存跨级别关联关系
//            processOccupationLink(delegator, partyId, masterPartyId, generalOccupation, masterRoleTypeId);
        }
        if(UtilValidate.isNotEmpty(managerForOccupation)){
            dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", partyId,
                    "roleTypeIdFrom", generalOccupation, "partyIdTo", managerForOccupation, "roleTypeIdTo", "MANAGER",
                    "partyRelationshipTypeId", "MANAGER"));
        }

        successResult.put("data", UtilMisc.toMap("msg", msg, "roleTypeId", generalOccupation, "partyId", partyId));
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("新增岗位错误");
    }

    return successResult;
}

/*//创建跨级间的关联，便于查询: 低级别(from) -> 高级别(to)
private void processOccupationLink(GenericDelegator delegator, String partyId, String parentPartyId, String roleTypeId, String parentRoleTypeId){
    List<GenericValue> partyRelationships = from("PartyRelationship").where(*//*"partyIdFrom", parentPartyId, *//*"partyIdTo", parentPartyId, "roleTypeIdTo", parentRoleTypeId, "partyRelationshipTypeId", "MASTER_POSITION").filterByDate().queryList();
    if(UtilValidate.isNotEmpty(partyRelationships)){
        String grandPartyId = partyRelationships.get(0).get("partyIdFrom")
        String grandRoleTypeId = partyRelationships.get(0).get("roleTypeIdFrom")
        GenericValue parent = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdTo", grandPartyId, "roleTypeIdTo", grandRoleTypeId, "partyIdFrom", partyId, "roleTypeIdFrom", roleTypeId, "partyRelationshipTypeId", "POSITION_LINK", "fromDate", UtilDateTime.nowTimestamp()));
        delegator.createOrStore(parent);
        processOccupationLink(delegator, partyId, grandPartyId, roleTypeId, grandRoleTypeId);
    }
}*/


public Map<String, Object> editOccupation() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String partyId = context.get("partyId");
    String oldOccupation = context.get("oldOccupation");
    String newOccupation = context.get("newOccupation");
    String newOccupationMaster = context.get("newOccupationMasterForEdit");
    String occupationName = newOccupation;
    String managerForOccupation = context.get("managerForEditOccupation");//岗位负责人
    try {
        GenericValue oldPosition = delegator.findByPrimaryKey("RoleType", UtilMisc.toMap("roleTypeId", oldOccupation));
        oldPosition.set("description", newOccupation);
        oldPosition.store();

        List<GenericValue> oldMasters = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", oldOccupation, "partyRelationshipTypeId", "MASTER_POSITION"));
        if (UtilValidate.isNotEmpty(newOccupationMaster)) {//设置主管岗位
            String[] position = newOccupationMaster.split(",");
            String masterPartyId = position[0];
            String masterRoleTypeId = position[1];

            if (UtilValidate.isNotEmpty(oldMasters)) {
                GenericValue oldMaster = oldMasters.get(0);
                if (!oldMaster.get("partyIdFrom").equals(masterPartyId) || !oldMaster.get("roleTypeIdFrom").equals(masterRoleTypeId)) {
                    //主管岗位变化
                    oldMaster.remove();
//                    //删除跨级别关联关系
//                    delegator.removeByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", oldOccupation, "partyRelationshipTypeId", "POSITION_LINK"))
                }
            }
            dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", masterPartyId, "roleTypeIdFrom", masterRoleTypeId, "partyIdTo", partyId, "roleTypeIdTo", oldOccupation, "partyRelationshipTypeId", "MASTER_POSITION"));
            /*//保存跨级别关联关系
            processOccupationLink(delegator, partyId, masterPartyId, oldOccupation, masterRoleTypeId);*/

        } else if (UtilValidate.isNotEmpty(oldMasters)) {
            oldMasters.get(0).remove();
        }
        List<GenericValue> oldManagers = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", oldOccupation, "partyRelationshipTypeId", "MANAGER"));
        if(UtilValidate.isNotEmpty(managerForOccupation)){
            if (UtilValidate.isNotEmpty(oldManagers)) {
                GenericValue oldManager = oldManagers.get(0);
                if (!oldManager.get("partyIdTo").equals(managerForOccupation) || !oldManager.get("roleTypeIdTo").equals("MANAGER")) {
                    //岗位负责人发生变化
                    oldMaster.remove();
                }
            }
            dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", partyId,
                    "roleTypeIdFrom", oldOccupation, "partyIdTo", managerForOccupation, "roleTypeIdTo", "MANAGER", "partyRelationshipTypeId", "MANAGER"));
        }else if(UtilValidate.isNotEmpty(oldManagers)){
            oldManagers.get(0).remove();
        }

        String msg = "修改岗位【" + occupationName + "】成功";


        successResult.put("data", UtilMisc.toMap("msg", msg, "roleTypeId", oldOccupation, "partyId", partyId));
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("修改岗位错误");
    }

    return successResult;
}

/**
 * 给员工分配岗位。
 * 员工可以兼任不同岗位，因此已存在岗位时，需要考虑是转岗还是兼任
 *
 * @return
 */
public Map<String, Object> saveOccupationMember() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String getMasterOccupation = context.get("positionId");
    String removePositionIds = context.get("removePositionIds");//转岗时提供从哪(些）岗位转出
    String memberPartyId = context.get("member");
    String[] position = positionId.split(",");
    String departmentPartyId = position[0];
    String departmentPosition = position[1];
    String msg = "添加成功";
    try {
        Map positionMap = UtilMisc.toMap("partyIdTo", memberPartyId, "partyRelationshipTypeId", "PROVIDE_POSITION")
        List<GenericValue> oldPositions = delegator.findByAnd("PartyRelationship", positionMap);
        boolean exist = false;
        if (UtilValidate.isNotEmpty(oldPositions)) {//已分配岗位
            List<String> toRemovePositions = FastList.newInstance();
            if (UtilValidate.isNotEmpty(removePositionIds)) {//原岗位转出
                toRemovePositions = UtilMisc.toListArray(removePositionIds.split(";"))
            }
            Iterator<GenericValue> iterator = oldPositions.iterator();
            while (iterator.hasNext()) {
                GenericValue oldPosition = iterator.next();

                String oldDep = oldPosition.get("partyIdFrom");
                String oldPos = oldPosition.get("roleTypeIdFrom");
                if (oldDep.equals(departmentPartyId) && oldPos.equals(departmentPosition)) {
                    msg = "该员工已是本岗位员工";
                    exist = true;
                }
                if (toRemovePositions.contains(oldDep + "," + oldPos)) {
                    oldPosition.remove();
                }
            }
        }

        if (!exist) {
            dispatcher.runSync("ensurePartyRoleFrom", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", memberPartyId, "roleTypeIdFrom", departmentPosition));
//            dispatcher.runSync("createPartyRole", UtilMisc.toMap("userLogin", userLogin, "partyId", memberPartyId, "roleTypeId", departmentPosition));

            positionMap.put("userLogin", userLogin);
            positionMap.put("partyIdFrom", departmentPartyId);
            positionMap.put("roleTypeIdFrom", departmentPosition);
            positionMap.put("roleTypeIdTo", departmentPosition);
            dispatcher.runSync("createPartyRelationship", positionMap);
        }
        //员工部门设置
        GenericValue staff = delegator.findOne("TblStaff", UtilMisc.toMap("partyId", memberPartyId), false);
        //TODO:员工兼任岗位处理,兼任的岗位所属部门不作为员工直属部门
        staff.set("department", departmentPartyId);
        staff.store();

        successResult.put("data", UtilMisc.toMap("msg", msg, "partyId", departmentPartyId));
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("员工岗位设置错误");
    }

    return successResult;
}

public Map<String, Object> getLowerOccupations() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String positionId = context.get("positionId");
    String[] position = positionId.split(",");
    String departmentPartyId = position[0];
    String departmentPosition = position[1];
    try {
        Map positionMap = UtilMisc.toMap("partyIdFrom", departmentPartyId, "roleTypeIdFrom", departmentPosition, "partyRelationshipTypeId", "MASTER_POSITION")
        List<GenericValue> members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);
        List data = FastList.newInstance();
        if (UtilValidate.isNotEmpty(members)) {
            for (GenericValue member : members) {
                Map memberMap = FastMap.newInstance();
                memberMap.put("partyId", member.get("partyIdTo"));
                memberMap.put("roleTypeId", member.get("roleTypeIdTo"));
                memberMap.put("positionId", memberMap.get("partyId") + "," + memberMap.get("roleTypeId"));
                memberMap.put("departmentName", member.get("groupName"));
                memberMap.put("description", delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", member.get("roleTypeIdTo"))).get("description", locale));
                data.add(memberMap);
                //TODO:获取岗位详细信息
            }
        }
        successResult.put("data", data);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("修改岗位错误");
    }

    return successResult;
}

public Map<String, Object> getMasterOccupation() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String positionId = context.get("positionId");
    String[] position = positionId.split(",");
    String departmentPartyId = position[0];
    String departmentPosition = position[1];
    try {
        Map positionMap = UtilMisc.toMap("partyIdTo", departmentPartyId, "roleTypeIdTo", departmentPosition, "partyRelationshipTypeId", "MASTER_POSITION")
        List<GenericValue> members = delegator.findByAnd("PartyRelationship", positionMap);

        Map memberMap = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(members)) {
            memberMap.put("partyId", members.get(0).get("partyIdFrom"));
            memberMap.put("roleTypeId", members.get(0).get("roleTypeIdFrom"));
        }
        successResult.put("data", memberMap);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("修改岗位错误");
    }

    return successResult;
}

public Map<String, Object> getOccupationMembers() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String positionId = context.get("positionId");
    String[] position = positionId.split(",");
    String departmentPartyId = position[0];
    String departmentPosition = position[1];
    try {
        Map positionMap = UtilMisc.toMap("departmentId", departmentPartyId, "roleTypeId", departmentPosition)

//        List members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);//TODO:分页
        List members = EntityQuery.use(delegator).from("PositionMember").where(EntityCondition.makeCondition(positionMap)).queryList();
        /*List data = FastList.newInstance();
        if(UtilValidate.isNotEmpty(members)){
            for (GenericValue member : members) {
                Map memberMap = FastMap.newInstance();
                memberMap.put("partyId", member.get("partyId"));
                memberMap.put("post", delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", departmentPosition)).get("description", locale));
                memberMap.put("fullName", member.get("fullName"));
                //TODO:获取员工详细信息
                data.add(memberMap);
            }
        }*/
        successResult.put("data", members);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("修改岗位错误");
    }

    return successResult;
}

public Map<String, Object> getLowerOccupationMembers() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String positionId = context.get("positionId");
    String[] position = positionId.split(",");
    String departmentPartyId = position[0];
    String departmentPosition = position[1];

    try {

        Map positionMap = UtilMisc.toMap("masterPartyId", departmentPartyId, "masterRoleTypeId", departmentPosition);
        //TODO:分页
        List members = EntityQuery.use(delegator)
//                .select("partyId", "fullName", "lastName", "firstName", "workerSn", "memberRoleTypeId")
                .from("LowerPositionMemberView")
                .where(EntityCondition.makeCondition(positionMap))
                .distinct()//因为兼任岗位的可能导致会出现重复
                .queryList();

//        List members = delegator.findByAnd("LowerPositionMemberView", positionMap);
//        List data = FastList.newInstance();
//        if (UtilValidate.isNotEmpty(members)) {
//            for (GenericValue member : members) {
//                Map memberMap = FastMap.newInstance();
//                memberMap.put("partyId", member.get("partyId"));
//                memberMap.put("post", delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", member.get("memberRoleTypeId"))).get("description", locale));
//
//                String fullName = member.get("fullName");
//                if(UtilValidate.isEmpty(fullName)){
//                    fullName = member.get("lastName") + member.get("firstName");
//                }
//                memberMap.put("employeeName", fullName);
//                memberMap.put("employeeJobNumber", member.get("workerSn"));
//                data.add(memberMap);
//                //TODO:获取人员详细信息
//            }
//        }

        successResult.put("data", members);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("查询岗位成员错误");
    }

    return successResult;
}

/**
 * 获取下级部门
 * @return
 */
public Map<String, Object> getLowerDepartments() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();

    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String departmentId = context.get("departmentId");
    try {
        Map positionMap = UtilMisc.toMap("partyIdFrom", departmentId, "partyRelationshipTypeId", "GROUP_ROLLUP")
        List<GenericValue> members = delegator.findByAnd("PartyRelationshipAndDetail", positionMap);
        List data = FastList.newInstance();
        if (UtilValidate.isNotEmpty(members)) {
            for (GenericValue member : members) {
                Map memberMap = FastMap.newInstance();
                memberMap.put("partyId", member.get("partyIdTo"));
                data.add(memberMap);
                //TODO:获取部门详细信息
            }
        }
        successResult.put("data", data);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("获取下级部门错误");
    }

    return successResult;
}

/**
 * 查询部门成员，直接通过Staff表中的department查询。
 * @return
 */
public Map<String, Object> getDepartmentMembers() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String departmentPartyId = context.get("partyId");
    boolean direct = context.get("direct");
    EntityListIterator eli = null;
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

        String sortField = (String) context.get("sortField");
        List<String> orderBy = FastList.newInstance();
        if (UtilValidate.isNotEmpty(sortField)) {
            orderBy.add(sortField);
        }

        // 计算当前显示页的最小、最大索引(可能会超出实际条数)
        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;

        List<EntityExpr> conditions = FastList.newInstance();

        if (!direct) {
            conditions.add(EntityCondition.makeCondition([EntityCondition.makeCondition("lowerPartyId", EntityOperator.EQUALS, departmentPartyId),
                                                       EntityCondition.makeCondition("masterPartyId", EntityOperator.EQUALS, departmentPartyId)], EntityOperator.OR));
        }else{
            conditions.add(EntityCondition.makeCondition("lowerPartyId", EntityOperator.EQUALS, departmentPartyId));
        }
//        String flag = context.get("flag");
//        //默认没有标志位的状态下查询在职人员，有标志位表示查询所有人员
//        if(!UtilValidate.isNotEmpty(flag)){
//            conditions.add(EntityCondition.makeCondition("jobState", EntityOperator.EQUALS, "WORKING"));
//        }
        EntityConditionList condition = EntityCondition.makeCondition(conditions);

        eli = EntityQuery.use(delegator).select("partyId", "fullName", "firstName", "lastName", "workerSn", "memberPartyId", "jobState")
                .from("DepartmentMemberView")
                .where(condition)
                .orderBy(orderBy)
                .cursorScrollInsensitive()
                .fetchSize(highIndex)
                .distinct()//因为兼任岗位的可能结果人员重复出现
//                .cache()//queryIterator时使用cache无效
                .queryIterator();

        // 获取结果片段
        List<GenericValue> members = eli.getPartialList(lowIndex, viewSize);

        List data = FastList.newInstance();
        for (GenericValue member : members) {
            Map memberMap = FastMap.newInstance();
            memberMap.put("partyId", member.get("memberPartyId"));
            //获取员工在某个部门担任的岗位
            //changed by pzq@20160629: StaffDetails获取的岗位信息是直属岗位信息，只会有一条记录，TODO:如果需要根据部门显示其对于的岗位信息，需从其他渠道获取
            /*StringBuilder post = new StringBuilder();
            String gender = "";
            List<GenericValue> roles = from("StaffDetails").where(UtilMisc.toMap("partyId", memberMap.get("partyId"), "departmentId", departmentPartyId)).queryList();
            if(UtilValidate.isNotEmpty(roles)){
                for (GenericValue role : roles) {
                    post.append(role.get("occupationName")).append(" ");
                    if(UtilValidate.isEmpty(gender)){
                        gender = role.getString("genderName");
                    }
                }
            }
            memberMap.put("post", post.toString());
            */
            GenericValue staffDetail = from("StaffDetails").where(UtilMisc.toMap("partyId", memberMap.get("partyId"))).queryOne();
            memberMap.put("gender", staffDetail.getString("genderName"));
            memberMap.put("post", staffDetail.getString("occupationName"));
            memberMap.put("department", staffDetail.getString("departmentName"))

            List<GenericValue> logins = from("UserLogin").where("partyId", memberMap.get("partyId")).queryList();
            if(UtilValidate.isNotEmpty(logins)){
                List<String> loginId = new ArrayList<>();
                for (GenericValue login : logins) {
                    loginId.add(login.getString("userLoginId"));
                }
                memberMap.put("loginIds", loginId);;
            }

            String fullName = member.get("fullName");
            if(UtilValidate.isEmpty(fullName)){
                fullName = member.get("lastName") + member.get("firstName");
            }
            String jobState = member.getString("jobState");
            if (jobState != null && jobState.equals("DEPARTURE")){
                memberMap.put("employeeName", fullName + "[离职]");
            } else {
                memberMap.put("employeeName", fullName);
            }
            memberMap.put("employeeJobNumber", member.get("workerSn"));
            data.add(memberMap);
            //TODO:获取人员详细信息
        }

        // 获取实际总条数
        int memberSize = eli.getResultsSizeAfterPartialList();
        if (highIndex > memberSize) {
            highIndex = memberSize;
        }
        successResult.put("data", data);
        successResult.put("viewIndex", viewIndex);
        successResult.put("viewSize", viewSize);
        successResult.put("highIndex", highIndex);
        successResult.put("lowIndex", lowIndex);
        successResult.put("sortField", sortField);
        successResult.put("totalCount", memberSize);

    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("查询部门成员错误");
    } finally {
        if (eli != null) {
            // close the list iterator
            eli.close();
        }
    }

    return successResult;
}
/*
* 员工岗位信息
* */
public Map<String, Object> getStaffPost() {
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = context.get("partyId");
    if (UtilValidate.isNotEmpty(partyId)) {
        List<GenericValue> genericValue = EntityQuery.use(delegator)
                   .from("StaffPositionDetailView")
                   .where(EntityCondition.makeCondition("partyIdTo",partyId))
                   .queryList();
        successResult.put("data",genericValue);
    }
    return successResult;
}

public Map<String, Object> getAllDepartmentMember() {
    List<GenericValue> toBeStored = new LinkedList<GenericValue>();
    Locale locale = (Locale) context.get("locale");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();



    GenericValue userLogin = (GenericValue) context.get("userLogin");

    String departmentPartyId = context.get("partyId");
    boolean direct = context.get("direct");


    try {
        List<EntityExpr> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition("lowerPartyId", EntityOperator.EQUALS, departmentPartyId));
        if (!direct) {
            conditions.add(EntityCondition.makeCondition("masterPartyId", EntityOperator.EQUALS, departmentPartyId));
        }
        EntityConditionList condition = EntityCondition.makeCondition(conditions, EntityOperator.OR);

        List<GenericValue> members = EntityQuery.use(delegator).select("partyId", "fullName", "firstName", "lastName", "workerSn", "memberPartyId")
                .from("DepartmentMemberView")
                .where(condition)
                .distinct()//因为兼任岗位的可能结果人员重复出现
                .cache()
                .queryList();

        List data = FastList.newInstance();
        for (GenericValue member : members) {
            Map memberMap = FastMap.newInstance();
            memberMap.put("partyId", member.get("memberPartyId"));
            //获取员工在某个部门担任的岗位
            StringBuilder post = new StringBuilder();
            List<GenericValue> roles = from("StaffPositionDetailView").where(UtilMisc.toMap("memberId", memberMap.get("partyId"), "groupId", departmentPartyId)).queryList();
            if (UtilValidate.isNotEmpty(roles)) {
                for (GenericValue role : roles) {
                    post.append(role.get("description")).append(" ");
                }
            }
            memberMap.put("post", post.toString());

            String fullName = member.get("fullName");
            if (UtilValidate.isEmpty(fullName)) {
                fullName = member.get("lastName") + member.get("firstName");
            }
            memberMap.put("employeeName", fullName);
            memberMap.put("employeeJobNumber", member.get("workerSn"));
            data.add(memberMap);
            //TODO:获取人员详细信息
        }
        successResult.put("data", data);
    } catch (GeneralServiceException e) {
        Debug.logError(e, this.getClass().getName());
        successResult = ServiceUtil.returnError("查询部门成员错误");
    }
    return successResult;
}
