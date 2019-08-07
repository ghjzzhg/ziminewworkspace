package org.ofbiz.activiti.container;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.util.*;

/**
 * 将所有具有登录账户的partyId作为activiti的用户id同步到工作流引擎中。
 * 不用登录账户id是因为在业务系统中分配任务时会按人员岗位或部门来分配，最终会找到对应的人而不是具体账户。
 *
 * Created by galaxypan on 2014/12/9.
 */
public class ActivitiSyncUtil{

    /**
     * 手工同步所有Activiti数据
     */

    public static Map<String, Object> synToActiviti(DispatchContext dctx, Map<String, ? extends Object> context) {
        try {
            synToActiviti();
        } catch (GenericEntityException e) {
            throw new RuntimeException(e);
        }
        return ServiceUtil.returnSuccess();
    }

    public static void synToActiviti() throws GenericEntityException {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        if(processEngine == null){//工作流引擎尚未就绪
            return;
        }
        IdentityService identityService = processEngine.getIdentityService();


        Delegator delegator = DelegatorFactory.getDelegator("default");
        List<GenericValue> users = delegator.findAll("UserLogin", true);

        Set<String> userPartIds = new HashSet<>();
        for (GenericValue user : users) {
            String partyId = user.getString("partyId");
            if (UtilValidate.isNotEmpty(partyId)) {
                userPartIds.add(partyId);
            }
        }

        Map<String, String> userGroups = new HashMap<>();
        Map<String, String> userPostGroups = new HashMap<>();
        Map<String, String> userLevelGroups = new HashMap<>();
        Map<String, String> userNames = new HashMap<>();
        Map<String, String> groupNames = new HashMap<>();

//        Set<String> groupIds = new HashSet<>();
        List<GenericValue> staffs = delegator.findList("TblStaff", EntityCondition.makeCondition("partyId", EntityOperator.IN, userPartIds), null, null, null, true);

        for (GenericValue staff : staffs) {
            String staffId = staff.getString("partyId");

            userNames.put(staffId, staff.getRelatedOne("Person", true).getString("firstName"));
            String department = staff.getString("department");
            if (UtilValidate.isNotEmpty(department)) {
//                groupIds.add(department);
                userGroups.put(staffId, department);
                GenericValue partyGroup = staff.getRelatedOne("PartyGroup", true);
                if(partyGroup != null){
                    groupNames.put(department, partyGroup.getString("groupName"));
                }

            }
            //岗位
            String post = staff.getString("post");
            if(UtilValidate.isNotEmpty(post)){
                String groupId = "post-" + post;
//                groupIds.add(groupId);
                userPostGroups.put(staffId, groupId);
                GenericValue postRoleType = staff.getRelatedOne("postRoleType", true);
                if (postRoleType != null) {
                    groupNames.put(groupId, postRoleType.getString("description"));
                }
            }
            //职级
            String level = staff.getString("position");
            if(UtilValidate.isNotEmpty(level)){
                String groupId = "level-" + level;
//                groupIds.add(groupId);
                userLevelGroups.put(staffId, groupId);
                GenericValue levelEnum = staff.getRelatedOne("PositionEnumeration", true);
                if (levelEnum != null) {
                    groupNames.put(groupId, levelEnum.getString("description"));
                }
            }
        }

        List<User> actUsers = identityService.createUserQuery().list();
        Map<String, User> existingUsers = new HashMap<>();
        for (User actUser : actUsers) {
            String actUserId = actUser.getId();
            existingUsers.put(actUserId, actUser);
            if(!userPartIds.contains(actUserId)){
                identityService.deleteUser(actUserId);
            }
        }

        for (String userPartId : userPartIds) {
            User user = null;
            if(!existingUsers.keySet().contains(userPartId)){
                user = identityService.newUser(userPartId);
            }else{
                user = existingUsers.get(userPartId);
            }
            user.setFirstName(userNames.get(userPartId));
            identityService.saveUser(user);

            String groupId = userGroups.get(userPartId);
            syncUserGroup(identityService, userPartId, groupId, groupNames.get(groupId));
            groupId = userPostGroups.get(userPartId);
            syncUserGroup(identityService, userPartId, groupId, groupNames.get(groupId));
            groupId = userLevelGroups.get(userPartId);
            syncUserGroup(identityService, userPartId, groupId, groupNames.get(groupId));

        }
    }

    private static void syncUserGroup(IdentityService identityService, String userPartId, String groupId, String groupName){
        if(UtilValidate.isNotEmpty(groupId)) {
            identityService.deleteMembership(userPartId, groupId);//无法查询当前的关系，只能删除避免重复，原关系无法删除，只能在单个用户维护时处理.
            List<Group> groups = identityService.createGroupQuery().groupId(groupId).list();
            boolean groupExist = groups.size() > 0;
            Group group = null;
            if (!groupExist) {
                group = identityService.newGroup(groupId);
                group.setType("assignment");
            }else{
                group = groups.get(0);
            }
            group.setName(groupName);
            identityService.saveGroup(group);

            identityService.createMembership(userPartId, groupId);
        }
    }

    ///////////////// Synchronized to the Activiti end //////////////////
}
