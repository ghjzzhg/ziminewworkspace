package org.ofbiz.zxdoc

import org.apache.commons.collections.CollectionUtils
import org.apache.commons.collections.map.HashedMap
import org.apache.commons.collections.map.LinkedMap
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.*
import org.ofbiz.content.data.DataServices
import org.ofbiz.content.data.FileManagerFactory
import org.ofbiz.content.data.FileTypeManager
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntity
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination
import org.ofbiz.party.party.PartyHelper
import org.ofbiz.service.ServiceUtil

import javax.rmi.CORBA.Util
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.swing.text.html.parser.Entity
import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String, Object> createCaseTemplate() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String key = context.get("templateKey");
    GenericValue template = delegator.makeValue("TblCaseTemplate");
    String id = delegator.getNextSeqId("TblCaseTemplate")
    if(UtilValidate.isNotEmpty(key)){
        template.setString("active","N");
    }else{
        template.setString("active","Y");
    }
    template.setString("id", id);
    List<String> dataResouceIds = DataServices.storeAllDataResourceInMap(dispatcher, delegator, userLogin, context);
    if (UtilValidate.isNotEmpty(dataResouceIds)) {
        String dataResourceId = dataResouceIds.get(0);
        template.setString("dataResourceId", dataResourceId)
    }
    template.setNonPKFields(context);
    String privateFlag = context.get("privateFlag");
    if (UtilValidate.isNotEmpty(privateFlag) && privateFlag.equals("true")) {
        template.setString("partyId", userLogin.get("partyId").toString());
    }
    GenericValue oldTemplate = from("TblCaseTemplate").where("templateKey", key).cache().orderBy("-version").queryFirst();
    template.set("version", oldTemplate == null ? 1 : (oldTemplate.getInteger("version") + 1));
    template.create();
    success.put("id", id);
    return success;
}

public Map<String, Object> updateCaseTemplate() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String key = context.get("templateKey");
    String name = context.get("templateName");
    String templateId = context.get("id");
    GenericValue template = from("TblCaseTemplate").where("id", templateId).queryOne();
    template.setNonPKFields(context);

    GenericValue oldTemplate = EntityQuery.use(delegator).from("TblCaseTemplate").where(UtilMisc.toMap("id", templateId, "templateKey", key)).queryOne()
    List<String> dataResouceIds = DataServices.storeAllDataResourceInMap(dispatcher, delegator as GenericDelegator, userLogin, context);
    if (UtilValidate.isNotEmpty(dataResouceIds)) {
        if (oldTemplate != null) {
            String olddataResourceId = oldTemplate.get("dataResourceId")
            if (UtilValidate.isNotEmpty(olddataResourceId)) {
                GenericValue dataResource = EntityQuery.use(delegator).from("DataResource").where(UtilMisc.toMap("dataResourceId", olddataResourceId)).queryOne();
                if (dataResource != null) {
                    fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator) delegator);
                    fileManager.delFile(olddataResourceId);
                }
            }
            String dataResourceId = dataResouceIds.get(0);
            template.setString("dataResourceId", dataResourceId)
        }
    }

    template.set("version", oldTemplate == null ? 1 : (oldTemplate.getInteger("version") + 1));
    template.store();

    success.put("id", templateId);
    return success;
}

public Map<String, Object> deleteCaseTemplate() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String templateId = context.get("id");

    GenericValue template = from("TblCaseTemplate").where("id", templateId).queryOne();
    template.setString("deleted", "Y");

    template.store();

    //TODO:如果模板未被引用可以直接物理删除基本信息，节点及附件记录
    return success;
}

public Map<String, Object> saveCaseTemplateNodes() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String templateId = context.get("templateId");

    Map<String, String> ids = context.get("id");

    Map<String, String> dueDays = context.get("dueDay");
    Map<String, String> dueBases = context.get("dueBase");
    Map<String, String> dueTasks = context.get("dueTask");
    if(UtilValidate.isEmpty(dueDays)){
        Map<String, String> names = context.get("name");
        Map<String, String> executors = context.get("executor");
        Map<String, String> groups = context.get("group");
        Map<String, String> seqs = context.get("seq");
        Map<String, String> files = context.get("atta");

        List<GenericValue> oldNodes = from("TblCaseTemplateNode").where("templateId", templateId).queryList();
        Map<String, GenericValue> oldNodesMap = new HashMap<>();
        if (oldNodes != null) {
            for (GenericValue node : oldNodes) {
                oldNodesMap.put(node.getString("id"), node);
            }
        }

        List<GenericValue> toBeStored = new ArrayList<>();
        List<GenericValue> nodeFiles = new ArrayList<>();

        for (Map.Entry<String, String> pair : ids.entrySet()) {
            String index = pair.key;
            String id = pair.value;
            String name = names.get(index);
            if (UtilValidate.isEmpty(name)) {
                //            throw new RuntimeException("节点名称不能为空");
                continue;
            }
            String executor = executors.get(index);
            String dueDay = dueDays.get(index);
            String dueTask = dueTasks.get(index);
            String group = groups.get(index);
            String dueBase = dueBases.get(index);
            GenericValue node = null;
            if (UtilValidate.isNotEmpty(id)) {
                node = oldNodesMap.get(id);
                oldNodesMap.remove(id);//移除后剩下的需要删除
                List<GenericValue> oldNodeFiles = node.getRelated("TblCaseTemplateNodeAtta");
                delegator.removeAll(oldNodeFiles);
            } else {
                node = delegator.makeValue("TblCaseTemplateNode");
                id = delegator.getNextSeqId("TblCaseTemplateNode");
                node.setString("id", id);
                node.setString("templateId", templateId);
            }
            toBeStored.add(node);
            node.setString("name", name);
            node.setString("executor", UtilValidate.isEmpty(executor) ? null : executor);
            if (UtilValidate.isNotEmpty(dueDay)) {
                node.set("dueDay", Integer.parseInt(dueDay));
            }
            if (UtilValidate.isNotEmpty(dueTask)) {
                node.set("dueTask", Integer.parseInt(dueTask));
            }
            if (UtilValidate.isNotEmpty(group)) {
                node.setString("groupId", group);
            }
            if (UtilValidate.isNotEmpty(dueBase)) {
                node.setString("dueBase", dueBase);
            }
            node.set("seq", Integer.parseInt(seqs.get(index)));

            String dataResouceIdStr = files.get(index);
            if (UtilValidate.isNotEmpty(dataResouceIdStr)) {//有附件
                String[] dataResouceIds = dataResouceIdStr.split(",");
                for (String dataResourceId : dataResouceIds) {
                    GenericValue nodeFile = delegator.makeValue("TblCaseTemplateNodeAtta");
                    nodeFile.setString("templateNodeId", id);
                    nodeFile.setString("dataResourceId", dataResourceId);
                    nodeFiles.add(nodeFile);
                }
            }
        }
        delegator.storeAll(toBeStored);
        delegator.storeAll(nodeFiles);

        Collection deleted = oldNodesMap.values();
        if (UtilValidate.isNotEmpty(deleted)) {
            delegator.removeAll(new ArrayList<GenericEntity>(deleted));//TODO:逻辑删除？
        }
    }else{
        List<GenericValue> list = new ArrayList<>();
        for (Map.Entry<String, String> pair : ids.entrySet()) {
            String index = pair.key;
            String id = pair.value;
            String dueDay = dueDays.get(index);
            String dueBase = dueBases.get(index);
            GenericValue genericValue = delegator.findByAnd("TblCaseTemplateNode", UtilMisc.toMap("id", id), null, false).get(0);

            if(UtilValidate.isNotEmpty(dueBase) && dueBase.indexOf("BT") < 0){
                GenericValue dueGeneric = delegator.findByAnd("TblCaseTemplateNode", UtilMisc.toMap("id", dueBase), null, false).get(0);
//                dueBase = dueGeneric.getString("groupId");
                genericValue.set("dueTask",Integer.parseInt(dueGeneric.getString("seq")));
            }
            if(UtilValidate.isNotEmpty(dueDay)) {
                genericValue.set("dueDay",Integer.parseInt(dueDay));
            }
            genericValue.set("dueBase",dueBase);
            list.add(genericValue);
        }
        delegator.storeAll(list);
    }

    success.put("templateId", templateId);
    success.put("templateName", delegator.findByAnd("TblCaseTemplate", UtilMisc.toMap("id", templateId), null, false).get(0).get("templateName"));
    return success;
}


public Map<String, Object> saveCaseTemplateNodeGroups() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String templateId = context.get("templateId");

    Map<String, String> ids = context.get("id");
    Map<String, String> names = context.get("name");
    Map<String, String> seqs = context.get("seq");

    if (UtilValidate.isNotEmpty(templateId)) {
        List<GenericValue> oldNodes = from("TblCaseTemplateNodeGroup").where("templateId", templateId).queryList();
        Map<String, GenericValue> oldNodesMap = new HashMap<>();
        if (oldNodes != null) {
            for (GenericValue node : oldNodes) {
                oldNodesMap.put(node.getString("id"), node);
            }
        }

        List<GenericValue> toBeStored = new ArrayList<>();
        for (Map.Entry<String, String> pair : ids.entrySet()) {
            String index = pair.key;
            String id = pair.value;
            String name = names.get(index);
            if (UtilValidate.isEmpty(name)) {
                //            throw new RuntimeException("节点名称不能为空");
                continue;
            }
            GenericValue node = null;
            if (UtilValidate.isNotEmpty(id)) {
                node = oldNodesMap.get(id);
                oldNodesMap.remove(id);//移除后剩下的需要删除
            } else {
                node = delegator.makeValue("TblCaseTemplateNodeGroup");
                id = delegator.getNextSeqId("TblCaseTemplateNodeGroup");
                node.setString("id", "NG-" + id);//便于后面引用时判断是否是基于大步骤
                node.setString("templateId", templateId);
            }
            toBeStored.add(node);
            node.setString("name", name);
            node.set("seq", Integer.parseInt(seqs.get(index)));
        }
        delegator.storeAll(toBeStored);

        Collection deleted = oldNodesMap.values();
        if (UtilValidate.isNotEmpty(deleted)) {
            delegator.removeAll(new ArrayList<GenericEntity>(deleted));//TODO:逻辑删除？
        }

        success.put("templateId", templateId);

        success.put("templateName", delegator.findByAnd("TblCaseTemplate", UtilMisc.toMap("id", templateId), null, false).get(0).get("templateName"));
    }
    return success;
}

public Map<String, Object> saveCaseTemplateBaseTime() {
    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String templateId = context.get("templateId");

    Map<String, String> ids = context.get("id");
    Map<String, String> names = context.get("name");
    Map<String, String> seqs = context.get("seq");

    List<GenericValue> oldNodes = from("TblCaseTemplateBaseTime").where("templateId", templateId).queryList();
    Map<String, GenericValue> oldNodesMap = new HashMap<>();
    if (oldNodes != null) {
        for (GenericValue node : oldNodes) {
            oldNodesMap.put(node.getString("id"), node);
        }
    }

    List<GenericValue> toBeStored = new ArrayList<>();
    for (Map.Entry<String, String> pair : ids.entrySet()) {
        String index = pair.key;
        String id = pair.value;
        String name = names.get(index);
        if (UtilValidate.isEmpty(name)) {
//            throw new RuntimeException("节点名称不能为空");
            continue;
        }
        GenericValue node = null;
        if (UtilValidate.isNotEmpty(id)) {
            node = oldNodesMap.get(id);
            oldNodesMap.remove(id);//移除后剩下的需要删除
        } else {
            node = delegator.makeValue("TblCaseTemplateBaseTime");
            id = delegator.getNextSeqId("TblCaseTemplateBaseTime");
            node.setString("id", "BT-" + id);//便于后面引用时判断是否是基于基准时间
            node.setString("templateId", templateId);
        }
        toBeStored.add(node);
        node.setString("name", name);
        node.set("seq", Integer.parseInt(seqs.get(index)));
    }
    delegator.storeAll(toBeStored);

    Collection deleted = oldNodesMap.values();
    if (UtilValidate.isNotEmpty(deleted)) {
        delegator.removeAll(new ArrayList<GenericEntity>(deleted));//TODO:逻辑删除？
    }

    success.put("templateId", templateId);
    success.put("templateName", delegator.findByAnd("TblCaseTemplate", UtilMisc.toMap("id", templateId), null, false).get(0).get("templateName"));
    return success;
}
//保存case进程大节点
public Map<String, Object> saveCaseProgressGroup() {
    Map success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");

    Map<String, String> ids = context.get("id");
    Map<String, String> names = context.get("name");
    Map<String, String> seqs = context.get("seq");
    Map<String, String> templateGroupIds = context.get("groupId");


    List<GenericValue> toBeStored = new ArrayList<>();
    for (Map.Entry<String, String> pair : ids.entrySet()) {
        String index = pair.key;
        String id = pair.value;
        String name = names.get(index);
        if (UtilValidate.isEmpty(name)) {
            continue;
        }
        GenericValue node = null;
        node = delegator.makeValue("TblCaseProgressGroup");
        if(UtilValidate.isEmpty(id)){
            id = delegator.getNextSeqId("TblCaseProgressGroup");
        }
        node.setString("id", id);
        node.setString("caseId", caseId);
        toBeStored.add(node);
        node.setString("name", name);
        node.setString("templateGroupId", templateGroupIds.get(index));
        node.set("seq", Integer.parseInt(seqs.get(index)));
    }
    delegator.storeAll(toBeStored);

    success.put("caseName", delegator.findByAnd("TblCase", UtilMisc.toMap("caseId", caseId), null, false).get(0).get("title"));
    success.put("caseId", caseId);
    return success;
}

//保存case进程大节点
public Map<String, Object> saveBlankCaseProgressGroup() {
    Map success = ServiceUtil.returnSuccess();
    HttpServletRequest request = context.get("request");
    HttpSession session = request.getSession();
    String blankCaseSessionKey = request.getParameter("blankCaseSessionKey");
    Map<String, Object> blankCaseData = session.getAttribute(blankCaseSessionKey);
    if(blankCaseData == null){
        throw new RuntimeException("会话已过期，请重新创建");
    }
    Map<String, String> ids = context.get("id");
    Map<String, String> names = context.get("name");
    Map<String, String> seqs = context.get("seq");
    Map<String, String> templateGroupIds = context.get("groupId");
    GenericValue caseEntity = blankCaseData.get("case");
    String caseId = caseEntity.getString("caseId");
    String caseName = caseEntity.getString("title");

    List<GenericValue> toBeStored = new ArrayList<>();
    for (Map.Entry<String, String> pair : ids.entrySet()) {
        String index = pair.key;
        String id = pair.value;
        String name = names.get(index);
        if (UtilValidate.isEmpty(name)) {
            continue;
        }
        GenericValue node = null;
        node = delegator.makeValue("TblCaseProgressGroup");
        if(UtilValidate.isEmpty(id)){
            id = delegator.getNextSeqId("TblCaseProgressGroup");
        }
        node.setString("id", id);
        node.setString("caseId", caseId);
        node.setString("name", name);
        node.setString("templateGroupId", templateGroupIds.get(index));
        node.set("seq", Integer.parseInt(seqs.get(index)));

        toBeStored.add(node);
    }
    blankCaseData.put("nodes", toBeStored);

    Map<String, Object> data = new HashMap();
    data.put("caseName", caseName);
    data.put("caseId", caseId);
    success.put("data", data);
    return success;
}

public Map<String, Object> getCaseTemplates() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    Map<String, Object> data = new HashMap<>();

    String qPartyId = context.get("userLogin").get("partyId");
    GenericValue party = EntityQuery.use(delegator).from("Party").where(UtilMisc.toMap("partyId", qPartyId)).queryOne();
//    if(party.get("partyTypeId").equals("PARTY_GROUP")){
    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", qPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    qPartyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : qPartyId;

    List<EntityCondition> conditionList = new ArrayList<>();
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, qPartyId));
    conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
//    conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "CASE_ROLE_OWNER"));
    GenericValue partyRole = EntityQuery.use(delegator).from("BasicGroupInfo").where(EntityCondition.makeCondition(conditionList)).queryOne();
    String groupRoleType = partyRole == null ? "" : partyRole.getString("roleTypeId");

    Map<String, List<GenericValue>> templatesGroup = new LinkedMap();
    List<GenericValue> templates = EntityQuery.use(delegator).from("TblCaseTemplate").where(UtilMisc.toMap("deleted", null, "active", "Y", "partyId", null)).queryList();
    for (GenericValue template : templates) {
        String requiredRoles = template.getString("roles");
        //changed by galaxypan@2017-10-12:仅搜索与自己相关的CASE模板
        if((UtilValidate.isNotEmpty(groupRoleType) && UtilValidate.isNotEmpty(requiredRoles) && requiredRoles.contains(groupRoleType)) || "CASE_ROLE_OWNER".equals(groupRoleType)){
            String templateGroup = template.getString("templateGroup");
            if (UtilValidate.isEmpty(templateGroup)) {
                templateGroup = "其它";
            } else {
                templateGroup = EntityQuery.use(delegator).from("Enumeration").where("enumId", templateGroup).queryOne().getString("description");
            }

            List<GenericValue> rows = templatesGroup.get(templateGroup);
            if (rows == null) {
                rows = new ArrayList<>();
                templatesGroup.put(templateGroup, rows);
            }
            rows.add(template);
        }
    }
    List<GenericValue> other = templatesGroup.get("其它");
    templatesGroup.remove("其它");
    templatesGroup.put("其它", other);

    conditionList = new ArrayList();
    conditionList.add(EntityCondition.makeCondition("deleted", EntityOperator.EQUALS, null));
    conditionList.add(EntityCondition.makeCondition("active", EntityOperator.EQUALS, "Y"));
    conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, qPartyId));
    List<GenericValue> privateTemplates = EntityQuery.use(delegator).from("TblCaseTemplate").where(conditionList).queryList();
    for (GenericValue template : privateTemplates) {
        String requiredRoles = template.getString("roles");
        //changed by galaxypan@2017-10-12:仅搜索与自己相关的CASE模板
        if(UtilValidate.isNotEmpty(groupRoleType) && UtilValidate.isNotEmpty(requiredRoles) && requiredRoles.contains(groupRoleType) || "CASE_ROLE_OWNER".equals(groupRoleType)) {
            templateGroup = "privateTemplate";
            List<GenericValue> rows = templatesGroup.get(templateGroup);
            if (rows == null) {
                rows = new ArrayList<>();
                templatesGroup.put(templateGroup, rows);
            }
            rows.add(template);
        }
    }
//    }
    data.put("casePartyTypeId", party.get("partyTypeId"));
    data.put("caseTemplates", templatesGroup);
    success.put("data", data);
    return success;
}

public Map<String, Object> updateCaseBasicInfo() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    Map<String, String> baseTimes = context.get("baseTimes");
    Map<String, String> personNames = context.get("personName");
    GenericValue userLogin = context.get("userLogin");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String oldCaseId = context.get("caseId");
    String caseCompanyGroupId = "";
    List<String> partyRoles = new ArrayList<>();
    //必须有企业参与方
    boolean hasCompany = false;
    for (String key : personNames.keySet()) {
        partyRoles.add(key);
        if (key.equals("CASE_ROLE_OWNER")) {
            String personId = personNames.get(key);
            GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where(UtilMisc.toMap("partyId", personId)).queryOne();
            String partyGroupId = person.get("groupId");
            if (PartyHelper.partyHasRoleType(delegator, partyGroupId, "CASE_ROLE_OWNER")) {
                hasCompany = true;
                caseCompanyGroupId = partyGroupId;
                break;
            }
        }
    }

    if (!hasCompany) {
        throw new RuntimeException("请选择企业");
    }
    java.sql.Date sqldate = null;
    if (UtilValidate.isNotEmpty(context.get("dueDate"))) {
        Date date = simpleDateFormat.parse(context.get("dueDate"))
        sqldate = new java.sql.Date(date.getTime());
    }
    //保存case基本信息
    GenericValue oldCase = EntityQuery.use(delegator).from("TblCase").where("caseId", oldCaseId).queryOne();
    GenericValue caseTemplate = oldCase.getRelatedOne("TblCaseTemplate");
    String oldCaseName = oldCase.getString("title");
    String caseName = context.get("caseName")
    oldCase.setString("title", caseName);
    oldCase.setString("summary", context.get("caseRemark"));
    oldCase.set("dueDate", sqldate);

    GenericValue caseFolderInfo = EntityQuery.use(delegator).from("TblDirectoryStructure").where("parentFolderId", "3", "folderType", "1", "folderId", oldCase.getString("folderId")).queryOne();
    if(caseFolderInfo == null){//修改前的case没有目录，需要创建
        //创建CASE文档目录
        Map<String, Object> caseFolderResult = runService("createCaseRootFolder", UtilMisc.toMap("caseTitle", caseName, "ownerPartyId", caseCompanyGroupId, "userLogin", userLogin));
        String caseFolderId = caseFolderResult.get("folderId");
        oldCase.setString("folderId", caseFolderId);
        //默认共享给当前所有参与方
        runService("addCasePartyFolderPermission", UtilMisc.toMap("caseId", oldCaseId, "fileId", caseFolderId, "fileType", "folder", "sharedRoles", partyRoles.join(","), "userLogin", userLogin));
    }

    oldCase.store();

    if(!oldCaseName.equalsIgnoreCase(caseName) && caseFolderInfo != null){//修改case名称时，修改case目录名称
        caseFolderInfo.setString("folderName", caseName);
        caseFolderInfo.store();
    }


    if (baseTimes != null && baseTimes.size() > 0) {
        List<GenericValue> caseBaseTimes = new ArrayList<>();
        for (Map.Entry<String, String> pair : baseTimes.entrySet()) {
            String baseTimeId = pair.key;
            String baseTimeValue = pair.value;
            caseBaseTimes.add(delegator.makeValidValue("TblCaseBaseTime", UtilMisc.toMap(
                    "caseId", oldCaseId,
                    "baseTimeId", baseTimeId,
                    "baseTime", UtilValidate.isNotEmpty(baseTimeValue) ? UtilDateTime.toSqlDate(baseTimeValue, "yyyy-MM-dd") : null)));
        }
        delegator.storeAll(caseBaseTimes);
    }

    GenericValue systemUser = from("UserLogin").where("userLoginId", "system").queryOne();

    //更新参与方或项目经理
    List<GenericValue> oldCaseParties = EntityQuery.use(delegator).from("TblCaseParty").where("caseId", oldCaseId).queryList();
    Map<String, List> oldCasePartiesMap = oldCaseParties.collectEntries{
        [it.getString("roleTypeId"), [it.getString("partyId"), it.getString("personId")]]
    };
    //找出变动的参与方
    List<GenericValue> newCaseManagers = EntityQuery.use(delegator).from("FullNameGroupName").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, personNames.values())).queryList();
    Map<String, String> rolesMapByManager = new HashMap<>();
    for (String key : personNames.keySet()) {
        rolesMapByManager.put(personNames.get(key), key);
    }
    Map<String, List> newCasePartiesMap = new HashMap<>();
    for (GenericValue caseManager : newCaseManagers) {
        String personId = caseManager.getString("partyId");
        String roleTypeId = rolesMapByManager.get(personId);
        String groupId = caseManager.getString("groupId");
        newCasePartiesMap.put(roleTypeId, [groupId, personId])
    }
    List<String> newCasePartyRoles = CollectionUtils.removeAll(newCasePartiesMap.keySet(), oldCasePartiesMap.keySet());
    List<String> toBeRemoved = CollectionUtils.removeAll(oldCasePartiesMap.keySet(), newCasePartiesMap.keySet());
    List<String> toBeChanged = CollectionUtils.intersection(oldCasePartiesMap.keySet(), newCasePartiesMap.keySet());
    List<String> permissionUpdated = new ArrayList<>();
    for (String roleType : toBeChanged) {
        String[] newParty = newCasePartiesMap.get(roleType);
        String[] oldParty = oldCasePartiesMap.get(roleType);
        String newPartyGroupId = newParty[0]
        String oldPartyGroupId = oldParty[0]
        if(!newPartyGroupId.equals(oldPartyGroupId)){
            //参与方变更了，做删除-新增处理
            toBeRemoved.add(roleType);//删除原有参与方各项权限
            newCasePartyRoles.add(roleType);//新增新参与方各项权限
            //迁移同一参与方的文件授权
            switchCasePartyFolderPermission(oldCaseId, oldPartyGroupId, newPartyGroupId);
            permissionUpdated.add(oldPartyGroupId);
            permissionUpdated.add(newPartyGroupId);
        }else if(!newParty[1].equals(oldParty[1])){
            //参与方未变，项目经理变更
            //1、回收企业目录共享给参与方原项目经理的权限
            delegator.removeByCondition("TblCasePartyMember", EntityCondition.makeCondition(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, oldCaseId), EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, oldParty[1])));
//            delegator.removeByCondition("TblCasePrompt", EntityCondition.makeCondition(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, oldCaseId), EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, oldParty[1])));

            //2、设置新项目经理权限
            runService("AddCasePartyMember", UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true, "caseId", oldCaseId, "partyId", newParty[1], "groupPartyId", newPartyGroupId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"));
        }
    }


    if (UtilValidate.isNotEmpty(toBeRemoved)) {
        List<String> removedGroupIds = new ArrayList<>();
        for (String roleId : toBeRemoved) {
            List removedRole = oldCasePartiesMap.get(roleId);
            removedGroupIds.add(removedRole[0]);
            //回收被移除参与方的文件授权，变更参与方的在上面已经转移了共享权限，不需要做回收
            if(!permissionUpdated.contains(removedRole[0])){
                runService("revokeCasePartyFolderPermission", UtilMisc.toMap("userLogin", systemUser, "caseId", oldCaseId, "memberId", removedRole[0]));
            }
        }
        //删除参与方
        delegator.removeByCondition("TblCaseParty", EntityCondition.makeCondition(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, oldCaseId), EntityCondition.makeCondition("partyId", EntityOperator.IN, removedGroupIds)));
        //回收企业目录共享给参与方人员的权限
        List<GenericValue> toBeRevoked = EntityQuery.use(delegator).from("TblCasePartyMember").where(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, oldCaseId), EntityCondition.makeCondition("groupPartyId", EntityOperator.IN, removedGroupIds)).queryList();
        delegator.removeAll(toBeRevoked);
        List<String> removedPersonIds = new ArrayList<>();
        for (GenericValue party : toBeRevoked) {
            String removePersonId = party.getString("partyId");
            removedPersonIds.add(removePersonId);
        }
        //删除case任务提示
//        delegator.removeByCondition("TblCasePrompt", EntityCondition.makeCondition(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, oldCaseId), EntityCondition.makeCondition("partyId", EntityOperator.IN, removedPersonIds)));
    }
    if (UtilValidate.isNotEmpty(newCasePartyRoles)) {
        String qPartyId = context.get("userLogin").partyId;
        List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", qPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
        qPartyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : qPartyId;//子账户创建时记录子账户对应的主账号party

        List<GenericValue> caseParties = new ArrayList<GenericValue>();
        java.sql.Date now = new java.sql.Date(new Date().getTime());
        for (String partyRole : newCasePartyRoles) {
            String[] partyRoleData = newCasePartiesMap.get(partyRole);
            caseParties.add(delegator.makeValidValue("TblCaseParty", UtilMisc.toMap(
                    "partyId", partyRoleData[0],
                    "caseId", oldCaseId,
                    "roleTypeId", partyRole,
                    "personId", partyRoleData[1],
                    "joinDate", now)));
            runService("AddCasePartyMember", UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true, "caseId", oldCaseId, "partyId", partyRoleData[1], "groupPartyId", partyRoleData[0], "roleTypeId", "CASE_PERSON_ROLE_MANAGER"));
        }
        delegator.storeAll(caseParties);

        for (String partyRole : newCasePartyRoles) {
            String[] partyRoleData = newCasePartiesMap.get(partyRole);
            //新加入的参与方按照模板设置的授权方案来设置默认授权
            if(!permissionUpdated.contains(partyRoleData[0]) && caseTemplate != null && qPartyId == caseCompanyGroupId){//仅当企业主账号或其子账号保存CASE时设置授权，避免机构创建CASE则立即获得授权的漏洞)
                addCaseFolderShareUsingTemplate(caseTemplate, caseCompanyGroupId, context.get("userLogin"), partyRole);
            }
        }
    }
    success.put("caseId", oldCaseId)
    return success;
}

/**
 * 新增一个CASE
 * @return
 */
public Map<String, Object> saveCase() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    Map<String, String> personNames = context.get("personName");
    Map<String, String> baseTimes = context.get("baseTimes");
    String templateId = context.get("templateId");
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String qPartyId = partyId;
    String caseCompanyGroupId = "";
    //必须有企业参与方
    boolean isPartner = true;
    boolean hasCompany = false;
    for (String key : personNames.keySet()) {
        if (key.equals("CASE_ROLE_OWNER")) {
            String personId = personNames.get(key);
            GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where(UtilMisc.toMap("partyId", personId)).queryOne();
            String partyGroupId = person.get("groupId");
            if (PartyHelper.partyHasRoleType(delegator, partyGroupId, "CASE_ROLE_OWNER")) {
                hasCompany = true;
                caseCompanyGroupId = partyGroupId;
                break;
            }
        }
    }

    if (!hasCompany) {
        throw new RuntimeException("请选择企业");
    }

    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", qPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    qPartyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : qPartyId;//子账户创建时记录子账户对应的主账号party
    GenericValue caseTemplate = null;
    if (UtilValidate.isNotEmpty(templateId)) {
        caseTemplate = EntityQuery.use(delegator).from("TblCaseTemplate").where("id", templateId).queryOne();
    }

    String caseId = delegator.getNextSeqId("TblCase");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    java.sql.Date sqldate = null;
    if (UtilValidate.isNotEmpty(context.get("dueDate"))) {
        Date date = format.parse(context.get("dueDate"))
        sqldate = new java.sql.Date(date.getTime());
    }
    //保存case基本信息
    delegator.create(delegator.makeValidValue("TblCase", UtilMisc.toMap(
            "caseId", caseId,
            "title", context.get("caseName"),
            "summary", context.get("caseRemark"),
            "caseCategory", caseTemplate == null ? null : caseTemplate.getString("templateKey"),
            "caseTemplate", templateId,
            "startDate", new java.sql.Date((new Date()).getTime()),
            "dueDate", sqldate,
            "createPartyId", partyId,
            "partyId", qPartyId)));
    //保存参与方信息
    List<String> partyRoles = new ArrayList<>();
    List<GenericValue> caseParties = new ArrayList<>();
    boolean selfManager = false;//创建人（子账户时）是case的manager
    for (String key : personNames.keySet()) {
        if (personNames.get(key) != '' && !key.equals("")) {
            partyRoles.add(key);
            String personId = personNames.get(key);
            if (UtilValidate.isNotEmpty(personId)) {
                if(partyId.equals(personId)){
                    selfManager = true;
                }
                GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where(UtilMisc.toMap("partyId", personId)).queryOne();
                String groupId = person.get("groupId");
                //当两个case参与方不同时才保存数据
                if(!qPartyId.equals(groupId)){
                    savePartner(qPartyId, groupId, userLogin);
                    savePartner(groupId, qPartyId, userLogin);
                }
                caseParties.add(delegator.makeValidValue("TblCaseParty", UtilMisc.toMap(
                        "partyId", groupId,
                        "caseId", caseId,
                        "personId", personId,
                        "roleTypeId", key,
                        "joinDate", new java.sql.Date(new Date().getTime()))));
                runService("AddCasePartyMember", UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true, "caseId", caseId, "partyId", personId, "groupPartyId", groupId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"));
            }
        }
    }
    delegator.storeAll(caseParties);

    if(!selfManager && !partyId.equals(qPartyId)){//子账号创建case时设置其他人为项目经理时，自己作为case的成员
        runService("AddCasePartyMember", UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true, "caseId", caseId, "partyId", partyId, "groupPartyId", qPartyId, "roleTypeId", "CASE_PERSON_ROLE_STAFF"));
    }

    if (baseTimes != null && baseTimes.size() > 0) {
        List<GenericValue> caseBaseTimes = new ArrayList<>();
        for (Map.Entry<String, String> pair : baseTimes.entrySet()) {
            String baseTimeId = pair.key;
            String baseTimeValue = pair.value;
            caseBaseTimes.add(delegator.makeValidValue("TblCaseBaseTime", UtilMisc.toMap(
                    "caseId", caseId,
                    "baseTimeId", baseTimeId,
                    "baseTime", UtilValidate.isNotEmpty(baseTimeValue) ? UtilDateTime.toSqlDate(baseTimeValue, "yyyy-MM-dd") : null)));
        }
        delegator.storeAll(caseBaseTimes);
    }

    String progressId = "";

    if (caseTemplate != null) {
        //创建大步骤
        List<GenericValue> templateGroups = EntityQuery.use(delegator).from("TblCaseTemplateNodeGroup").where("templateId", caseTemplate.getString("id")).queryList();
        List<GenericValue> toBeStored = new ArrayList<>();
        for (GenericValue templateGroup : templateGroups) {
            String seq = templateGroup.get("seq");
            String id = delegator.getNextSeqId("TblCaseProgressGroup");
            if (seq.equals("0")) {
                progressId = id
            }
            GenericValue node = delegator.makeValue("TblCaseProgressGroup");
            node.setString("id", id);
            node.setString("caseId", caseId);
            node.setString("name", templateGroup.getString("name"));
            node.setString("templateGroupId", templateGroup.getString("id")); ;
            node.set("seq", templateGroup.getInteger("seq"));
            toBeStored.add(node);
        }
        delegator.storeAll(toBeStored);

        //创建具体任务
        runService("saveCaseProgressFromTemplate", UtilMisc.toMap("userLogin", context.get("userLogin"), "caseId", caseId, "templateId", caseTemplate.getString("id")));

        //新增CASE时，如果有模板，则根据模板设置的授权方案授权
        if(qPartyId == caseCompanyGroupId){//仅当企业主账号或其子账号保存CASE时设置授权，避免机构创建CASE则立即获得授权的漏洞
            createCaseFolderShareUsingTemplate(caseId, caseTemplate, caseCompanyGroupId, context.get("userLogin"));
        }

    }

    //创建CASE文档目录
    Map<String, Object> caseFolderResult = runService("createCaseRootFolder", UtilMisc.toMap("caseTitle", context.get("caseName"), "ownerPartyId", caseCompanyGroupId, "userLogin", userLogin));
    String caseFolderId = caseFolderResult.get("folderId");
    GenericValue caseEntity = EntityQuery.use(delegator).from("TblCase").where("caseId", caseId).queryOne();
    caseEntity.setString("folderId", caseFolderId);
    caseEntity.store();

    //默认共享给当前所有参与方
    runService("addCasePartyFolderPermission", UtilMisc.toMap("caseId", caseId, "fileId", caseFolderId, "fileType", "folder", "sharedRoles", partyRoles.join(","), "userLogin", userLogin));

    success.put("caseProgressGroupId", progressId);
    success.put("caseId", caseId);
    return success;
}

public Map<String, Object> createCaseRootFolder(){
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseTitle = context.get("caseTitle");
    String ownerPartyId = context.get("ownerPartyId");
    Date date = new Date();
    String id = delegator.getNextSeqId("TblDirectoryStructureId");
    String folderId = delegator.getNextSeqId("TblDirectoryStructure");
    Map<String, Object> folderMap = new HashMap<>();
    folderMap.put("parentFolderId", "3");//CASE文档
    folderMap.put("folderType", "1");
    folderMap.put("folderName", caseTitle);
    folderMap.put("folderPath","/" + ownerPartyId + "/");
    List<GenericValue> folderList = EntityQuery.use(delegator).from("TblDirectoryStructure").where(folderMap).queryList();
    if(UtilValidate.isNotEmpty(folderList)){//重名处理
        folderMap.put("folderName",caseTitle + new SimpleDateFormat("yyyyMMddHHmmss").format(date));
    }
    folderMap.put("id", id);
    folderMap.put("foldeRemarks","CASE文档根目录");
    folderMap.put("folderId", folderId);
    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", ownerPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    String groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : ownerPartyId;
    folderMap.put("partyId", groupId);//CASE文件夹所属为对应的主账号
    folderMap.put("folderPermissions", "111111");
    folderMap.put("createPartyId", ownerPartyId);
    Timestamp timestamp = new Timestamp(date.getTime());
    folderMap.put("createFolderTime", timestamp);
    GenericValue folder = delegator.makeValue("TblDirectoryStructure",folderMap);
    folder.create();
    success.put("folderId", folderId);
    return success;
}

private void createCaseFolderShareUsingTemplate(String caseId, GenericValue caseTemplate, String caseCompanyGroupId, GenericValue userLogin){
    String category = caseTemplate.getString("id");
    //优先根据id查询具体的设置
    List<GenericValue> shareFolders = EntityQuery.use(delegator).from("TblCaseCategoryFolder").where("caseCategory", category).queryList();
    //changed by galaxypan@2017-10-28:只根据具体的模板id查询
    /*if(UtilValidate.isEmpty(shareFolders)){//如果没有则根据类型key查询
        category = caseTemplate.getString("templateKey");
        shareFolders = EntityQuery.use(delegator).from("TblCaseCategoryFolder").where("caseCategory", category).queryList();
    }*/
    for (GenericValue shareFolder : shareFolders) {
        String folderPath = shareFolder.getString("folderPath");
        String fileType = "";
        if(folderPath.startsWith("/")){//非企业自定义模板定义的是路径，而不是具体的folder id
            String searchPath = folderPath.substring(1);
            GenericValue realFolder = EntityQuery.use(delegator).from("TblDirectoryStructure").where("folderPath", "/" + caseCompanyGroupId + "/", "folderName", searchPath, "partyId", caseCompanyGroupId, "folderType", "1").queryOne();
            folderPath = realFolder.getString("folderId");
            fileType = "folder";
        }else{
            GenericValue realFolder = EntityQuery.use(delegator).from("TblDirectoryStructure").where("folderId", folderPath, "folderType", "1").queryOne();
            if(realFolder != null){
                fileType = "folder";
            }else{
                fileType = "file";
            }
        }
        String sharedRoles = shareFolder.getString("roles");
        runService("updateCasePartyFolderShare", UtilMisc.toMap("caseId", caseId, "fileId", folderPath, "fileType", fileType, "sharedRoles", sharedRoles, "userLogin", userLogin));
    }
}

public Map<String, Object> saveBlankCase() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    HttpServletRequest request = context.get("request");
    HttpSession session = request.getSession();
    String blankCaseSessionKey = request.getParameter("blankCaseSessionKey");
    Map<String, String> personNames = context.personName;
    Map<String, String> baseTimes = context.baseTimes;
    GenericValue userLogin = context.userLogin;
    String partyId = userLogin.get("partyId");
    String qPartyId = partyId;
    String caseCompanyGroupId = "";
//必须有企业参与方
    boolean isPartner = true;
    boolean hasCompany = false;
    for (String key : personNames.keySet()) {
        if (key.equals("CASE_ROLE_OWNER")) {
            String personId = personNames.get(key);
            GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where(UtilMisc.toMap("partyId", personId)).queryOne();
            String partyGroupId = person.get("groupId");
            if (PartyHelper.partyHasRoleType(delegator, partyGroupId, "CASE_ROLE_OWNER")) {
                hasCompany = true;
                caseCompanyGroupId = partyGroupId;
                break;
            }
        }
    }

    if (!hasCompany) {
        throw new RuntimeException("请选择企业");
    }

    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", qPartyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    qPartyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : qPartyId;//子账户创建时记录子账户对应的主账号party


    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    java.sql.Date sqldate = null;
    if (UtilValidate.isNotEmpty(context.get("dueDate"))) {
        Date date = format.parse(context.get("dueDate"))
        sqldate = new java.sql.Date(date.getTime());
    }
    Map<String, Object> blankCaseData = session.getAttribute(blankCaseSessionKey);
    if(blankCaseData == null){
        blankCaseData = new HashMap<>();
        session.setAttribute(blankCaseSessionKey, blankCaseData);
    }
//保存case基本信息
    String caseId = delegator.getNextSeqId("TblCase");
    blankCaseData.put("case", delegator.makeValidValue("TblCase", UtilMisc.toMap(
            "caseId", caseId,
            "title", context.get("caseName"),
            "summary", context.get("caseRemark"),
            "caseCategory", null,
            "caseTemplate", null,
            "startDate", new java.sql.Date((new Date()).getTime()),
            "dueDate", sqldate,
            "createPartyId", partyId,
            "partyId", qPartyId)));

//保存参与方信息
    List<String> partyRoles = new ArrayList<>();
    List<GenericValue> caseParties = new ArrayList<>();
    List<Map<String, Object>> casePartyMembers = new ArrayList<>();
    blankCaseData.put("caseParties", caseParties);
    blankCaseData.put("casePartyMembers", casePartyMembers);

    boolean selfManager = false;//创建人（子账户时）是case的manager
    for (String key : personNames.keySet()) {
        if (personNames.get(key) != '' && !key.equals("")) {
            partyRoles.add(key);
            String personId = personNames.get(key);
            if (UtilValidate.isNotEmpty(personId)) {
                if(partyId.equals(personId)){
                    selfManager = true;
                }
                GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where(UtilMisc.toMap("partyId", personId)).queryOne();
                String groupId = person.get("groupId");
                caseParties.add(delegator.makeValidValue("TblCaseParty", UtilMisc.toMap(
                        "partyId", groupId,
                        "caseId", caseId,
                        "personId", personId,
                        "roleTypeId", key,
                        "joinDate", new java.sql.Date(new Date().getTime()))));
                casePartyMembers.add(UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true, "caseId", caseId, "partyId", personId, "groupPartyId", groupId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"));
            }
        }
    }

    if(!selfManager && !partyId.equals(qPartyId)){//子账号创建case时设置其他人为项目经理时，自己作为case的成员
        casePartyMembers.add(UtilMisc.toMap("userLogin", context.get("userLogin"), "isAdd", true, "caseId", caseId, "partyId", partyId, "groupPartyId", qPartyId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"));
    }

    List<GenericValue> caseBaseTimes = new ArrayList<>();
    if (baseTimes != null && baseTimes.size() > 0) {
        for (Map.Entry<String, String> pair : baseTimes.entrySet()) {
            String baseTimeId = pair.key;
            String baseTimeValue = pair.value;
            caseBaseTimes.add(delegator.makeValidValue("TblCaseBaseTime", UtilMisc.toMap(
                    "caseId", caseId,
                    "baseTimeId", baseTimeId,
                    "baseTime", UtilValidate.isNotEmpty(baseTimeValue) ? UtilDateTime.toSqlDate(baseTimeValue, "yyyy-MM-dd") : null)));
        }
    }

    blankCaseData.put("caseBaseTimes", caseBaseTimes);
    success.put("data", UtilMisc.toMap("blankCaseSessionKey", blankCaseSessionKey));
    return success;
}

/**
 * 给某个参与方添加默认目录授权
 * @param caseTemplate
 * @param caseCompanyGroupId
 * @param userLogin
 * @param specificRole
 */
private void addCaseFolderShareUsingTemplate(GenericValue caseTemplate, String caseCompanyGroupId, GenericValue userLogin, String specificRole){
    String category = caseTemplate.getString("id");
    EntityCondition rolesCon = EntityCondition.makeCondition("roles", EntityOperator.LIKE, "%" + specificRole + "%");
    EntityCondition templateKeyCon = EntityCondition.makeCondition("caseCategory", EntityOperator.EQUALS, category);
    List<GenericValue> shareFolders = EntityQuery.use(delegator).from("TblCaseCategoryFolder").where(rolesCon, templateKeyCon).queryList();
    if(UtilValidate.isEmpty(shareFolders)){
        category = caseTemplate.getString("templateKey");
        templateKeyCon = EntityCondition.makeCondition("caseCategory", EntityOperator.EQUALS, category);
        shareFolders = EntityQuery.use(delegator).from("TblCaseCategoryFolder").where(rolesCon, templateKeyCon).queryList();
    }
    for (GenericValue shareFolder : shareFolders) {
        String folderPath = shareFolder.getString("folderPath");
        String fileType = "";
        if(folderPath.startsWith("/")){//非企业自定义模板定义的是路径，而不是具体的folder id
            String searchPath = folderPath.substring(1);
            GenericValue realFolder = EntityQuery.use(delegator).from("TblDirectoryStructure").where("folderPath", "/" + caseCompanyGroupId + "/", "folderName", searchPath, "partyId", caseCompanyGroupId, "folderType", "1").queryOne();
            folderPath = realFolder.getString("folderId");
            fileType = "folder";
        }else{
            GenericValue realFolder = EntityQuery.use(delegator).from("TblDirectoryStructure").where("folderId", folderPath).queryOne();
            if(realFolder != null){
                fileType = "folder";
            }else{
                fileType = "file";
            }
        }
        runService("addCasePartyFolderPermission", UtilMisc.toMap("caseId", caseId, "fileId", folderPath, "fileType", fileType, "sharedRoles", specificRole, "userLogin", userLogin));
    }
}

public Map<String, Object> fixedCaseTemplate() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    //TODO case开始时间和party加入时间默认为当前时间，需根据实际情况来决定
    long mesc = 24 * 3600 * 1000;
    Date date = new Date();
    List<GenericValue> templateNodes = delegator.findByAnd("TblCaseTemplateNode", UtilMisc.toMap("templateId", context.get("templateId")), null, false);
    int dueDays = 0;
    for (GenericValue templateNode : templateNodes) {
        dueDays += Integer.parseInt(templateNode.get("dueDay") == null ? "0" : templateNode.get("dueDay") + "");
    }
    Date completeDate = new Date(date.getTime() + dueDays * mesc);
    //将模板信息存入(更新)case
    GenericValue caseInfo = from("TblCase").where("caseId", context.get("caseId")).queryOne();
    if (UtilValidate.isNotEmpty(context.get("templateId"))) {
        String caseCategory = delegator.findOne("TblCaseTemplate", UtilMisc.toMap("id", context.get("templateId")), false).get("templateKey");
        caseInfo.put("caseCategory", caseCategory);
    }
    caseInfo.put("startDate", new java.sql.Date(date.getTime()));
    caseInfo.put("completeDate", new java.sql.Date(completeDate.getTime()));
    caseInfo.put("caseTemplate", context.get("templateId"));
    caseInfo.store();
    success.put("caseId", context.get("caseId"));
    success.put("templateId", context.get("templateId"));
    return success;
}

private void savePartner(String partyIdFrom, String partyIdTo, GenericValue userLogin) {
    GenericValue genericValue = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "partyRelationshipTypeId", "ORG_LINK_CONTACT")).queryOne();
    if (genericValue == null) {
        GenericValue genericValue1 = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", partyIdTo)).queryOne();
        Map<String, Object> partner = UtilMisc.toMap("partyId", partyIdFrom, "userLoginId", genericValue1.get("userLoginId"));
        GenericValue genericValuess = EntityQuery.use(delegator).from("TblCustomer").where(partner).queryOne();
        if (UtilValidate.isNotEmpty(genericValuess)) {
            genericValuess.put("status", "Y")
            genericValuess.store();
        } else {
            String id = delegator.getNextSeqId("TblCustomer");
            partner.put("id", id);
            partner.put("status", "Y")
            GenericValue customer = delegator.makeValidValue("TblCustomer", partner);
            delegator.create(customer);
        }
        dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "partyRelationshipTypeId", "ORG_LINK_CONTACT", "userLogin", userLogin));
    }
}

public Map<String, Object> saveCaseProgress() {

    Map success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String caseId = context.get("caseId");
    Map<String, String> ids = context.get("id");
    Map<String, String> dueBases = context.get("dueBase");
    Map<String, String> dueDays = context.get("dueDay");
    if(UtilValidate.isEmpty(dueBases)){
        Map<String, String> names = context.get("name");
        Map<String, String> executors = context.get("executor");
        Map<String, String> seqs = context.get("seq");
        Map<String, String> files = context.get("atta");
        Map<String, String> groups = context.get("group");
        List<GenericValue> caseParties = delegator.findByAnd("TblCaseParty", UtilMisc.toMap("caseId", caseId), null, false);
        Map<String, Object> partyGroups = new HashMap<>();
        for (GenericValue caseParty : caseParties) {
            partyGroups.put(caseParty.get("roleTypeId") + "", caseParty.get("partyId") + "");
        }

        List<GenericValue> oldNodes = from("TblCaseProgress").where("caseId", caseId).queryList();
        Map<String, GenericValue> oldNodesMap = new HashMap<>();
        if (oldNodes != null) {
            for (GenericValue node : oldNodes) {
                oldNodesMap.put(node.getString("id"), node);
            }
        }

        List<GenericValue> toBeStored = new ArrayList<>();
        List<GenericValue> nodeTemplates = new ArrayList<>();

        for (Map.Entry<String, String> pair : ids.entrySet()) {
            String index = pair.key;
            String id = pair.value;
            String name = names.get(index);
            if (UtilValidate.isEmpty(name)) {
                //            throw new RuntimeException("节点名称不能为空");
                continue;
            }
            String executor = executors.get(index);
            String dueDay = dueDays.get(index);
            String group = groups.get(index);
            String dueBase = dueBases.get(index);
            GenericValue node = null;
            if (UtilValidate.isNotEmpty(id)) {
                node = oldNodesMap.get(id);
                oldNodesMap.remove(id);//移除后剩下的需要删除
                List<GenericValue> nodeFiles = node.getRelated("TblCaseProgressAtta");
                delegator.removeAll(nodeFiles);
            } else {
                node = delegator.makeValue("TblCaseProgress");
                id = delegator.getNextSeqId("TblCaseProgress");
                node.setString("id", id);
                node.setString("caseId", caseId);
            }

            toBeStored.add(node);
            String roleTypeId = executors.get(index);
            node.setString("title", name);
            node.setString("executor", UtilValidate.isEmpty(executor) ? null : executor);
            if (UtilValidate.isNotEmpty(dueDay)) {
                node.set("dueDay", Integer.parseInt(dueDay));
            }
            if (UtilValidate.isNotEmpty(group)) {
                node.setString("caseProgressGroupId", group);
            }
            if (UtilValidate.isNotEmpty(dueBase)) {
                node.setString("dueBase", dueBase);
            }
            node.set("partyId", partyGroups.get(roleTypeId));
            node.set("progressIndex", Integer.parseInt(seqs.get(index)));

            String dataResouceIdStr = files.get(index);
            if (UtilValidate.isNotEmpty(dataResouceIdStr)) {//有附件
                String[] dataResouceIds = dataResouceIdStr.split(",");
                for (String dataResourceId : dataResouceIds) {
                    GenericValue nodeFile = delegator.makeValue("TblCaseProgressAtta");
                    nodeFile.setString("progressId", id);
                    nodeFile.setString("dataResourceId", dataResourceId);
                    nodeTemplates.add(nodeFile);
                }
            }
        }
        delegator.storeAll(toBeStored);
        delegator.storeAll(nodeTemplates);

        Collection deleted = oldNodesMap.values();
        if (UtilValidate.isNotEmpty(deleted)) {
            delegator.removeAll(new ArrayList<GenericEntity>(deleted));//TODO:逻辑删除？
        }
    }else{
        List<GenericValue> list = new ArrayList<>();
        for (Map.Entry<String, String> pair : ids.entrySet()) {
            String index = pair.key;
            String id = pair.value;
            String dueDay = dueDays.get(index);
            String dueBase = dueBases.get(index);
            GenericValue genericValue = delegator.findByAnd("TblCaseProgress", UtilMisc.toMap("id", id), null, false).get(0);
            if(dueBase.indexOf("BT") < 0){
                GenericValue dueGeneric = delegator.findByAnd("TblCaseProgress", UtilMisc.toMap("id", dueBase), null, false).get(0);
                dueBase = dueGeneric.getString("caseProgressGroupId");
                genericValue.put("dueTask",Integer.parseInt(dueGeneric.getString("progressIndex")));
            }
            if(UtilValidate.isNotEmpty(dueDay)) {
                genericValue.put("dueDay",Integer.parseInt(dueDay));
            }
            genericValue.put("dueBase",dueBase);
            list.add(genericValue);
        }
        delegator.storeAll(list);
    }
    //如果创建case的人不是该case参与方的企业人员，应该直接关闭，不进入目录设置
    String partyId = userLogin.get("partyId");
    GenericValue user = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
    String accountType = user.get("partyTypeId");
    if (accountType.equals("PERSON")) {
        partyId = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT").queryOne().get("partyIdFrom");
    }
    GenericValue caseMember = EntityQuery.use(delegator).from("casePartyMembers").where("caseId", caseId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER", "groupRoleTypeId", "CASE_ROLE_OWNER").queryOne();
    String groupId = caseMember.get("groupPartyId");
    if (partyId.equals(groupId)) {
        success.put("msg", "ok");
    } else {
        success.put("msg", "保存并关闭");
    }
    return success;
}

public Map<String, Object> saveBlankCaseProgress() {
    Map success = ServiceUtil.returnSuccess();
    HttpServletRequest request = context.get("request");
    HttpSession session = request.getSession();
    String blankCaseSessionKey = request.getParameter("blankCaseSessionKey");
    if(UtilValidate.isEmpty(blankCaseSessionKey)){
        Map<String, Object> multiPartMap = request.getAttribute("multiPartMap");
        blankCaseSessionKey = multiPartMap.get("blankCaseSessionKey");
    }
    Map<String, Object> blankCaseData = session.getAttribute(blankCaseSessionKey);
    if(blankCaseData == null){
        throw new RuntimeException("会话已过期，请重新创建");
    }


    GenericValue userLogin = context.get("userLogin");
    String caseId = context.get("caseId");
    Map<String, String> ids = context.get("id");
    Map<String, String> dueBases = context.get("dueBase");
    Map<String, String> dueDays = context.get("dueDay");
    Map<String, List<GenericValue>> oldNodeTasks = blankCaseData.get("nodeTasks");
    List<GenericValue> oldNodes = new ArrayList<>();
    if(UtilValidate.isNotEmpty(oldNodeTasks)){
        for (List<GenericValue> oldTasks : oldNodeTasks.values()) {
            oldNodes.addAll(oldTasks);
        }
    }
    List<GenericValue> caseParties = blankCaseData.get("caseParties");
    Map<String, Object> partyGroups = new HashMap<>();
    for (GenericValue caseParty : caseParties) {
        partyGroups.put(caseParty.getString("roleTypeId"), caseParty.getString("partyId"));
    }
    if(UtilValidate.isEmpty(dueBases)){
        Map<String, String> names = context.get("name");
        Map<String, String> executors = context.get("executor");
        Map<String, String> seqs = context.get("seq");
        Map<String, String> files = context.get("atta");
        Map<String, String> groups = context.get("group");
        Map<String, List<GenericValue>> newNodeTasks = new HashMap<>();
        blankCaseData.put("nodeTasks", newNodeTasks);
        Map<String, GenericValue> oldNodesMap = new HashMap<>();
        if (oldNodes != null) {
            for (GenericValue node : oldNodes) {
                oldNodesMap.put(node.getString("id"), node);
            }
        }
        Map<String, List<GenericValue>> progressAttas = blankCaseData.get("progressAttas");//每个小任务的附件集合

        for (Map.Entry<String, String> pair : ids.entrySet()) {
            String index = pair.key;
            String id = pair.value;
            String name = names.get(index);
            if (UtilValidate.isEmpty(name)) {
                //            throw new RuntimeException("节点名称不能为空");
                continue;
            }
            String executor = executors.get(index);
            String dueDay = dueDays.get(index);
            String group = groups.get(index);
            List<GenericValue> groupProgresses = newNodeTasks.get(group);
            if(groupProgresses == null){
                groupProgresses = new ArrayList<>();
                newNodeTasks.put(group, groupProgresses);
            }

            String dueBase = dueBases.get(index);
            GenericValue node = null;
            if (UtilValidate.isNotEmpty(id)) {
                node = oldNodesMap.get(id);
            } else {
                node = delegator.makeValue("TblCaseProgress");
                id = delegator.getNextSeqId("TblCaseProgress");
                node.setString("id", id);
                node.setString("caseId", caseId);
            }

            groupProgresses.add(node);
            String roleTypeId = executors.get(index);
            node.setString("title", name);
            node.setString("executor", UtilValidate.isEmpty(executor) ? null : executor);
            if (UtilValidate.isNotEmpty(dueDay)) {
                node.set("dueDay", Integer.parseInt(dueDay));
            }
            if (UtilValidate.isNotEmpty(group)) {
                node.setString("caseProgressGroupId", group);
            }
            if (UtilValidate.isNotEmpty(dueBase)) {
                node.setString("dueBase", dueBase);
            }
            node.set("partyId", partyGroups.get(roleTypeId));
            node.set("progressIndex", Integer.parseInt(seqs.get(index)));

            String dataResouceIdStr = files.get(index);
            if (UtilValidate.isNotEmpty(dataResouceIdStr)) {//有附件
                String[] dataResouceIds = dataResouceIdStr.split(",");
                List<GenericValue> nodeTemplates = new ArrayList<>();
                for (String dataResourceId : dataResouceIds) {
                    GenericValue nodeFile = delegator.makeValue("TblCaseProgressAtta");
                    nodeFile.setString("progressId", id);
                    nodeFile.setString("dataResourceId", dataResourceId);
                    nodeTemplates.add(nodeFile);
                }
                progressAttas.put(id, nodeTemplates);
            }
        }
    }else{
        Map<String, GenericValue> progressMapById = new HashMap<>();
        for (GenericValue progress : oldNodes) {
            progressMapById.put(progress.getString("id"), progress);
        }
        for (Map.Entry<String, String> pair : ids.entrySet()) {
            String index = pair.key;
            String id = pair.value;
            String dueDay = dueDays.get(index);
            String dueBase = dueBases.get(index);
            GenericValue genericValue = progressMapById.get(id);
            if(dueBase.indexOf("BT") < 0){
                GenericValue dueGeneric = progressMapById.get(dueBase);
                dueBase = dueGeneric.getString("caseProgressGroupId");
                genericValue.put("dueTask",Integer.parseInt(dueGeneric.getString("progressIndex")));
            }
            if(UtilValidate.isNotEmpty(dueDay)) {
                genericValue.put("dueDay",Integer.parseInt(dueDay));
            }
            genericValue.put("dueBase",dueBase);
        }
    }
    //如果创建case的人不是该case参与方的企业人员，应该直接关闭，不进入目录设置
    String partyId = userLogin.get("partyId");
    GenericValue user = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
    String accountType = user.get("partyTypeId");
    if (accountType.equals("PERSON")) {
        partyId = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT").queryOne().get("partyIdFrom");
    }
    String caseOwnerParty = partyGroups.get("CASE_ROLE_OWNER");
    if (partyId.equals(caseOwnerParty)) {
        success.put("msg", "ok");
    } else {
        //创建case记录
        Map<String, Object> result = runService("createCaseFromSession", UtilMisc.toMap("blankCaseData", blankCaseData, "userLogin", context.get("userLogin")));
        success.put("msg", "保存并关闭");
    }
    return success;
}

public Map<String, Object> saveCaseProgressFromTemplate() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String caseId = context.get("caseId");
    String templateId = context.get("templateId");

    GenericValue caseObj = EntityQuery.use(delegator).from("TblCase").where("caseId", caseId).queryOne();
    List<GenericValue> caseParties = delegator.findByAnd("TblCaseParty", UtilMisc.toMap("caseId", caseId), null, false);
    Map<String, String> partyGroups = new HashMap<>();
    for (GenericValue caseParty : caseParties) {
        partyGroups.put(caseParty.get("roleTypeId") + "", caseParty.get("partyId") + "");
    }
    List<GenericValue> progresses = new ArrayList<>();

    List<GenericValue> progressAttasToStore = new ArrayList<>();
    List<GenericValue> templateNodes = EntityQuery.use(delegator).from("TblCaseTemplateNode").where("templateId", templateId).orderBy("seq").queryList();
    List<GenericValue> baseTimes = EntityQuery.use(delegator).from("TblCaseBaseTime").where("caseId", caseId).queryList();
    Map<String, Date> baseTimeMap = new HashMap<>();
    for (GenericValue baseTime : baseTimes) {
        baseTimeMap.put(baseTime.getString("baseTimeId"), baseTime.getDate("baseTime"));
    }
    Calendar cal = Calendar.instance;
    for (GenericValue templateNode : templateNodes) {
        String roleTypeId = templateNode.getString("executor");
        String progressId = delegator.getNextSeqId("TblCaseProgress");
        List<GenericValue> fileIds = EntityQuery.use(delegator).from("TblCaseTemplateNodeAtta").where("templateNodeId", templateNode.getString("id")).queryList();
        for (GenericValue fileId : fileIds) {
            GenericValue progressAtta = delegator.makeValidValue("TblCaseProgressAtta");
            progressAtta.put("progressId", progressId);
            progressAtta.put("dataResourceId", fileId.getString("dataResourceId"));
            progressAtta.put("template", "Y");
            progressAttasToStore.add(progressAtta);
        }

        java.sql.Timestamp dueTime = null;
        String dueBase = templateNode.getString("dueBase");
        if (UtilValidate.isNotEmpty(dueBase)) {
            if (dueBase.equalsIgnoreCase("BT-start")) {//BT-start：以CASE启动日期为准
                baseTime = caseObj.getTimestamp("createdStamp");
            } else if (dueBase.startsWith("BT")) {//以基准时间限定的可以直接计算
                Date baseTime = baseTimeMap.get(dueBase);
                if (baseTime != null) {
                    Integer dueDay = templateNode.getInteger("dueDay");
                    if (dueDay != null) {
                        cal.setTime(baseTime);
                        cal.add(Calendar.DAY_OF_MONTH, dueDay);
                        dueTime = new java.sql.Timestamp(cal.getTimeInMillis());
                    }
                }
            }
        }
        GenericValue progressGroup = EntityQuery.use(delegator).from("TblCaseProgressGroup").where("caseId", caseId, "templateGroupId", templateNode.getString("groupId")).queryOne();
        GenericValue progress = delegator.makeValidValue("TblCaseProgress", UtilMisc.toMap(
                "id", progressId,
                "caseProgressGroupId", progressGroup.getString("id"),
                "caseId", caseId,
                "caseTemplateNodeId", templateNode.getString("id"),
                "title", templateNode.getString("name"),
                "progressIndex", templateNode.getInteger("seq"),
                "partyId", partyGroups.get(roleTypeId),
                "dueTime", dueTime,
                "dueBase", templateNode.getString("dueBase"),
                "dueTask", templateNode.getInteger("dueTask"),
                "dueDay", templateNode.getInteger("dueDay")));
        progresses.add(progress);
    }

    delegator.storeAll(progressAttasToStore);
    delegator.storeAll(progresses);
    success.put("data", "保存成功");
    //throw new RuntimeException("TESTINGGGGGGGGGGGGGGGGGGGGGGGG");
    return success;
}

public Map<String, Object> saveCaseStep() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String progressId = context.get("progressId");
    String description = context.get("description");
    boolean isSkipped = context.get("isSkipped");
    GenericValue caseProgress = delegator.findOne("TblCaseProgress", UtilMisc.toMap("id", progressId), false);
    if (isSkipped) {
        caseProgress.put("skipped", isSkipped);
    }
    caseProgress.put("completeDesc", description);
    caseProgress.put("completeTime", new Timestamp(new Date().getTime()));

    caseProgress.store();

    //获取caseId传递给case完成监听事件以发送邮件
    String caseId = delegator.findOne("TblCaseProgress", UtilMisc.toMap("id", progressId), false).get("caseId");

    String caseProgressGroupId = delegator.findOne("TblCaseProgress", UtilMisc.toMap("id", progressId), false).get("caseProgressGroupId");
    List<GenericValue> progressGroupList = delegator.findByAnd("TblCaseProgressGroup", UtilMisc.toMap("caseId", caseId), UtilMisc.toList("-seq"), false);
    GenericValue lastProgress = progressGroupList.get(0);
    if (caseProgressGroupId.equals(lastProgress.get("id"))) {//修改case状态
        GenericValue caseInfo = EntityQuery.use(delegator).from("TblCase").where(UtilMisc.toMap("caseId", caseId)).queryOne();
        if (caseInfo != null) {
            caseInfo.put("status", "CASE_STATUS_FINISH")
            caseInfo.store();
        }
    }
    success.put("progressId", progressId);
    success.put("caseId", caseId);
    success.put("data", "保存成功");
    return success;
}

public Map<String, Object> addCaseCompletedFiles() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String progressId = context.get("progressId");
    String[] dataResourceIds = context.get("dataResourceIds").toString().split(",");
    try {
        for (String id : dataResourceIds.toList()) {
            GenericValue genericValue = EntityQuery.use(delegator).from("DataResource").where(UtilMisc.toMap("dataResourceId",id)).queryOne();
            if(UtilValidate.isNotEmpty(genericValue)){
                delegator.makeValue("TblCaseProgressAtta", UtilMisc.toMap("progressId", progressId, "dataResourceId", id, "template", "N")).create();
            }else{
                throw new RuntimeException("文件已被删除，请刷新后重试！");
            }
        }
    } catch (GenericEntityException e) {
        throw new RuntimeException("请勿重复上传文件")
    }

    success.put("msg", "上传成功");
    return success;
}
//完成case进程后发送邮件提醒
public Map<String, Object> caseCompleteMail() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    boolean isProgressGroupFinished = false;
    String caseId = context.get("caseId");
    String progressId = context.get("progressId");
    String caseProgressGroupId = delegator.findOne("TblCaseProgress", UtilMisc.toMap("id", progressId), false).get("caseProgressGroupId");
    List<GenericValue> caseProgressList = delegator.findByAnd("TblCaseProgress", UtilMisc.toMap("caseProgressGroupId", caseProgressGroupId), null, false);
    int completeAmount = 0;
    for (GenericValue caseProgress : caseProgressList) {
        if (UtilValidate.isNotEmpty(caseProgress.get("completeTime")) || "Y".equals(caseProgress.get("skipped"))) {
            completeAmount++;
        }
    }
    if (caseProgressList.size() == completeAmount) {
        isProgressGroupFinished = true;
    }
    if (!isProgressGroupFinished) {
        return success;
    }
    //判断progress是否结束
    List<GenericValue> progressGroupList = delegator.findByAnd("TblCaseProgressGroup", UtilMisc.toMap("caseId", caseId), UtilMisc.toList("-seq"), false);
    GenericValue lastProgress = progressGroupList.get(0);
    if (caseProgressGroupId.equals(lastProgress.get("id"))) {//如果是最后一个进程则不发送邮件
        return success;
    } else {//如果不是则发送邮件通知该case下一个进程的 项目经理
        int thisProgressGroupIndex = delegator.findOne("TblCaseProgressGroup", UtilMisc.toMap("id", caseProgressGroupId), false).get("seq");
        GenericValue nextProgressGroup = null;
        for (int i = 0; i < progressGroupList.size(); i++) {
            int groupSeq = progressGroupList.get(i).getInteger("seq");
            if (groupSeq > thisProgressGroupIndex) {
                nextProgressGroup = progressGroupList.get(i);
            } else {
                break;
            }
        }
        List<GenericValue> nextProgressList = delegator.findByAnd("TblCaseProgress", UtilMisc.toMap("caseProgressGroupId", nextProgressGroup.get("id")), null, false);
        Set<String> idSet = new HashSet<>();
        for (GenericValue nextProgress : nextProgressList) {
            nextProgress.set("active", "Y");//标记任务为激活状态,结合completeTime、skipped可获取当前进行中的任务
            nextProgress.store();
            String nextProgressPartyId = nextProgress.get("partyId");
            if (UtilValidate.isNotEmpty(nextProgressPartyId)) {
                //changed by galaxypan@2017-10-07:case任务提醒分配到参与方，而不是具体某个人，查询时控制可见性
                //保存提示信息到提示表中
                /*String id = delegator.getNextSeqId("TblCasePrompt");
                Map<String, Object> map = new HashMap<>();
                map.put("partyId", nextProgressPartyId);
                map.put("id", id);
                map.put("caseId", caseId);
                map.put("progressId", nextProgress.get("id"));
                GenericValue casePrompt = delegator.makeValidValue("TblCasePrompt", map);
                casePrompt.create();*/

                List<GenericValue> progressManager = delegator.findByAnd("TblCasePartyMember", UtilMisc.toMap("caseId", caseId, "groupPartyId", nextProgressPartyId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"), null, false);
                if (progressManager.size() > 0) {
                    String managerPartyId = progressManager.get(0).get("partyId");
                    String caseName = delegator.findOne("TblCase", UtilMisc.toMap("caseId", caseId), false).get("title");
//发送即时消息
                    String msgTitle = caseName + ":" + nextProgress.getString("title");
                    String titleClickAction = "displayInside('/zxdoc/control/CaseHome?caseId=" + caseId + "')";
                    runService("createSiteMsg", UtilMisc.toMap("partyId", managerPartyId, "title", msgTitle, "titleClickAction", titleClickAction, "type", "task"));

                    List<EntityCondition> conds = new ArrayList<>();
                    conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, managerPartyId));
                    conds.add(EntityCondition.makeCondition("emailAddress", EntityOperator.NOT_EQUAL, null));
                    List<GenericValue> memberInfoList = EntityQuery.use(delegator).from("PartyExport").where(conds).queryList();
                    if (memberInfoList.size() > 0) {
                        //相关文件
                        String fileNames = "";
                        List<GenericValue> progressFileList = delegator.findByAnd("CaseProgressDataResource", UtilMisc.toMap("progressId", nextProgress.get("id")), null, false);
                        if (progressFileList.size() > 0) {
                            for (GenericValue progressFile : progressFileList) {
                                fileNames += progressFile.get("dataResourceName") + ",";
                            }
                            fileNames = fileNames.substring(0, fileNames.length() - 1);
                        }
                        String contextPath = UtilProperties.getPropertyValue("general", "contextPath");
                        String emailAddress = memberInfoList.get(0).get("emailAddress");
                        String name = memberInfoList.get(0).get("firstName");
                        String progressName = nextProgress.get("title");
                        String filesRelated = UtilValidate.isEmpty(fileNames) ? "" : "相关的文件有：" + fileNames;
                        String dueTime = UtilValidate.isEmpty(nextProgress.get("dueTime")) ? "请尽快完成" : "请于" + nextProgress.get("dueTime").toString().split(" ")[0] + "之前完成相应的工作";
                        String caseURL = (UtilProperties.getPropertyValue("general", "host-url") + (contextPath.contains("/") ? contextPath : ("/" + contextPath))) + "/control/CaseHome?caseId=" + caseId;
                        runService("sendEmailNotice", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_CASE_NODE_BEGIN", "toAddress", emailAddress, "bodyParameters", UtilMisc.toMap("name", name, "caseName", caseName, "progressName", progressName, "filesRelated", filesRelated, "dueTime", dueTime, "caseURL", caseURL), "dataResourceIds", null));
                    }
                }
            }
        }
    }
    return success;
}

//开始case进程后发送邮件提醒
public Map<String, Object> casesStartCompleteMail() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    String caseGroupId = context.get("caseProgressGroupId");
    if (UtilValidate.isNotEmpty(caseGroupId) && UtilValidate.isNotEmpty(caseId)) {
        //判断progress是否结束
        List<GenericValue> nextProgressList = delegator.findByAnd("TblCaseProgress", UtilMisc.toMap("caseProgressGroupId", caseGroupId), null, false);
        for (GenericValue nextProgress : nextProgressList) {
            nextProgress.set("active", "Y");//标记任务为激活状态,结合completeTime、skipped可获取当前进行中的任务
            nextProgress.store();

            String nextProgressPartyId = nextProgress.get("partyId");
            if (UtilValidate.isNotEmpty(nextProgressPartyId)) {
                //changed by galaxypan@2017-10-07:case任务提醒分配到参与方，而不是具体某个人，查询时控制可见性
                //保存提示信息到提示表中
                /*String id = delegator.getNextSeqId("TblCasePrompt");
                Map<String, Object> map = new HashMap<>();
                map.put("partyId", nextProgressPartyId);
                map.put("id", id);
                map.put("caseId", caseId);
                String progressId = nextProgress.get("id")
                map.put("progressId", progressId);
                GenericValue casePrompt = delegator.makeValidValue("TblCasePrompt", map);
                casePrompt.create();*/

                String progressId = nextProgress.get("id");
                List<GenericValue> progressManager = delegator.findByAnd("TblCasePartyMember", UtilMisc.toMap("caseId", caseId, "groupPartyId", nextProgressPartyId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"), null, false);
                if (progressManager.size() > 0) {
                    String managerPartyId = progressManager.get(0).get("partyId");

                    String caseName = delegator.findOne("TblCase", UtilMisc.toMap("caseId", caseId), false).get("title");
                    //发送即时消息
                    String msgTitle = caseName + ":" + nextProgress.getString("title");
                    String titleClickAction = "displayInside('/zxdoc/control/CaseHome?caseId=" + caseId + "')";
                    runService("createSiteMsg", UtilMisc.toMap("partyId", managerPartyId, "title", msgTitle, "titleClickAction", titleClickAction, "type", "task"));

                    List<EntityCondition> conds = new ArrayList<>();
                    conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, managerPartyId));
                    conds.add(EntityCondition.makeCondition("emailAddress", EntityOperator.NOT_EQUAL, null));
                    List<GenericValue> memberInfoList = EntityQuery.use(delegator).from("PartyExport").where(conds).queryList();
                    if (memberInfoList.size() > 0) {
                        //相关文件
                        String fileNames = "";
                        List<GenericValue> progressFileList = delegator.findByAnd("CaseProgressDataResource", UtilMisc.toMap("progressId", progressId), null, false);
                        if (progressFileList.size() > 0) {
                            for (GenericValue progressFile : progressFileList) {
                                fileNames += progressFile.get("dataResourceName") + ",";
                            }
                            fileNames = fileNames.substring(0, fileNames.length() - 1);
                        }
                        String contextPath = UtilProperties.getPropertyValue("general", "contextPath");
                        String emailAddress = memberInfoList.get(0).get("emailAddress");
                        String name = memberInfoList.get(0).get("firstName");
                        String progressName = nextProgress.get("title");
                        String filesRelated = UtilValidate.isEmpty(fileNames) ? "" : "相关的文件有：" + fileNames;
                        String dueTime = UtilValidate.isEmpty(nextProgress.get("dueTime")) ? "请尽快完成" : "请于" + nextProgress.get("dueTime").toString().split(" ")[0] + "之前完成相应的工作";
                        String caseURL = (UtilProperties.getPropertyValue("general", "host-url") + (contextPath.contains("/") ? contextPath : ("/" + contextPath))) + "/control/CaseHome?caseId=" + caseId;
                        runService("sendEmailNotice", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_CASE_NODE_BEGIN", "toAddress", emailAddress, "bodyParameters", UtilMisc.toMap("name", name, "caseName", caseName, "progressName", progressName, "filesRelated", filesRelated, "dueTime", dueTime, "caseURL", caseURL), "dataResourceIds", null));
                    }
                }
            }
        }
    }
    return success;
}

//完成启动后发送邮件提醒
public Map<String, Object> caseStartMail() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    boolean isProgressGroupFinished = false;
    String caseId = context.get("caseId");

    //TODO:给所有参与方发送邮件提醒，发送到机构账户对应的邮箱中

    //找到第一个Group
    List<GenericValue> progressGroupList = delegator.findByAnd("TblCaseProgressGroup", UtilMisc.toMap("caseId", caseId), UtilMisc.toList("seq"), false);
    if(UtilValidate.isNotEmpty(progressGroupList)){
        GenericValue targetGroup = progressGroupList.get(0);
        List<GenericValue> caseProgressList = delegator.findByAnd("TblCaseProgress", UtilMisc.toMap("caseProgressGroupId", targetGroup.getString("id")), null, false);

        for (GenericValue caseProgress : caseProgressList) {
            String nextProgressPartyId = caseProgress.get("partyId");
            if (UtilValidate.isNotEmpty(nextProgressPartyId)) {
                List<GenericValue> progressManager = delegator.findByAnd("TblCasePartyMember", UtilMisc.toMap("caseId", caseId, "groupPartyId", nextProgressPartyId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER"), null, false);
                if (progressManager.size() > 0) {
                    String managerPartyId = progressManager.get(0).get("partyId");
                    List<EntityCondition> conds = new ArrayList<>();
                    conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, managerPartyId));
                    conds.add(EntityCondition.makeCondition("emailAddress", EntityOperator.NOT_EQUAL, null));
                    List<GenericValue> memberInfoList = EntityQuery.use(delegator).from("PartyExport").where(conds).queryList();
                    if (memberInfoList.size() > 0) {
                        //相关文件
                        String fileNames = "";
                        List<GenericValue> progressFileList = delegator.findByAnd("CaseProgressDataResource", UtilMisc.toMap("progressId", caseProgress.get("id")), null, false);
                        if (progressFileList.size() > 0) {
                            for (GenericValue progressFile : progressFileList) {
                                fileNames += progressFile.get("dataResourceName") + ",";
                            }
                            fileNames = fileNames.substring(0, fileNames.length() - 1);
                        }
                        String contextPath = UtilProperties.getPropertyValue("general", "contextPath");
                        String emailAddress = memberInfoList.get(0).get("emailAddress");
                        String name = memberInfoList.get(0).get("firstName");
                        String caseName = delegator.findOne("TblCase", UtilMisc.toMap("caseId", caseId), false).get("title");
                        String progressName = caseProgress.get("title");
                        String filesRelated = UtilValidate.isEmpty(fileNames) ? "" : "相关的文件有：" + fileNames;
                        String dueTimeStr = UtilValidate.isEmpty(caseProgress.get("dueTime")) ? "请尽快完成" : "请于" + caseProgress.get("dueTime").toString().split(" ")[0] + "之前完成相应的工作";
                        String caseURL = UtilProperties.getPropertyValue("general.properties", "host-url") + "control/CaseHome?caseId=" + caseId;
                        runService("sendEmailNotice", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_CASE_NODE_BEGIN", "toAddress", emailAddress, "bodyParameters", UtilMisc.toMap("name", name, "caseName", caseName, "progressName", progressName, "filesRelated", filesRelated, "dueTime", dueTimeStr, "caseURL", caseURL), "dataResourceIds", null));
                    }
                }
            }
        }
    }
    return success;
}

//完成case进程后更新其他步骤完成期限
public Map<String, Object> updateCaseProgressDueTime() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    boolean isProgressGroupFinished = false;
    String caseId = context.get("caseId");
    String progressId = context.get("progressId");
    GenericValue currentCaseProgress = delegator.findOne("TblCaseProgress", UtilMisc.toMap("id", progressId), false);
    String caseProgressGroupId = currentCaseProgress.getString("caseProgressGroupId");
    GenericValue currentCaseProgressGroup = delegator.findOne("TblCaseProgressGroup", UtilMisc.toMap("id", caseProgressGroupId), false);
    List<GenericValue> caseProgressList = delegator.findByAnd("TblCaseProgress", UtilMisc.toMap("caseProgressGroupId", caseProgressGroupId), null, false);
    int completeAmount = 0;

    for (GenericValue caseProgress : caseProgressList) {
        if (UtilValidate.isNotEmpty(caseProgress.get("completeTime")) || "Y".equals(caseProgress.get("skipped"))) {
            completeAmount++;
        }
    }
    if (caseProgressList.size() == completeAmount) {
        isProgressGroupFinished = true;
    }

    List<GenericValue> basedOnThisProgress = new ArrayList<>();
    List<GenericValue> allLeftProgresses = EntityQuery.use(delegator).from("TblCaseProgress").where("caseId", caseId).queryList();
    Calendar cal = Calendar.instance;
    for (GenericValue activeProgress : allLeftProgresses) {
        String dueBase = activeProgress.getString("dueBase");
        if (UtilValidate.isNotEmpty(dueBase)) {
            if (dueBase.startsWith("NG")) {
                Integer dueTask = activeProgress.getInteger("dueTask");
                if (UtilValidate.isEmpty(dueTask)) {
                    //如果是基于本步骤且步骤内所有任务已完成，则更新其期限。
                    if (dueBase.equals(currentCaseProgressGroup.getString("templateGroupId")) && isProgressGroupFinished) {
                        cal.setTime(new Date());
                        cal.add(Calendar.DAY_OF_MONTH, activeProgress.getInteger("dueDay"));
                        activeProgress.set("dueTime", new Timestamp(cal.getTimeInMillis()));
                        basedOnThisProgress.add(activeProgress);
                    }
                } else if (dueTask.equals(currentCaseProgress.getInteger("progressIndex") + 1)) {
                    //基于当前任务
                    cal.setTime(new Date());
                    cal.add(Calendar.DAY_OF_MONTH, activeProgress.getInteger("dueDay"));
                    activeProgress.set("dueTime", new Timestamp(cal.getTimeInMillis()));
                    basedOnThisProgress.add(activeProgress);
                }
            }
        }
    }

    delegator.storeAll(basedOnThisProgress);
    return success;
}

//更新基准日后触发
public Map<String, Object> updateCaseProgressDueTimeOnBaseTime() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    boolean isProgressGroupFinished = false;
    String caseId = context.get("caseId");
    String baseTimeId = context.get("baseTimeId");
    Date baseTime = context.get("baseTime");

    List<GenericValue> caseProgressList = EntityQuery.use(delegator).from("TblCaseProgress").where("dueBase", baseTimeId, "caseId", caseId).queryList();
    Calendar cal = Calendar.instance;
    for (GenericValue caseProgress : caseProgressList) {
        cal.setTime(baseTime);
        cal.add(Calendar.DAY_OF_MONTH, caseProgress.getInteger("dueDay"));
        caseProgress.set("dueTime", new Timestamp(cal.getTimeInMillis()));
    }

    delegator.storeAll(caseProgressList);
    return success;
}

/**
 * 服务机构（个人或机构）  企业（个人或机构） 的case列表
 * @return
 */
public Map<String, Object> providerCases() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String partyId = context.get("userLogin").get("partyId");
    String customerName = context.get("customerName");//TODO:根据企业名称查询CASE
    Map pageParam = context.get("pageParam");//分页参数
    String groupPartyId = null;
    List<GenericValue> relatedCases = null;
    List<String> caseIds = new ArrayList<>();
    //判断是个人还是机构
    String partyTypeId = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false).get("partyTypeId");

    List<EntityCondition> cons = new ArrayList<>();
    if(UtilValidate.isNotEmpty(customerName)){
        cons.add(EntityCondition.makeCondition("ownerGroupName", EntityOperator.LIKE, "%" + customerName + "%"));
    }
    if ("PARTY_GROUP".equals(partyTypeId)) {
        cons.add(EntityCondition.makeCondition(EntityCondition.makeCondition("providerPartyId", EntityOperator.EQUALS, partyId), EntityJoinOperator.OR, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId)));
        relatedCases = EntityQuery.use(delegator).select("caseId").from("CaseProviderAndOwner").where(cons).queryList();
        groupPartyId = partyId;
    } else if ("PERSON".equals(partyTypeId)) {
        cons.add(EntityCondition.makeCondition("providerPartyId", EntityOperator.EQUALS, partyId));
        relatedCases = EntityQuery.use(delegator).select("caseId").from("CaseProviderPersonAndOwner").where(cons).queryList();
    }
    if(UtilValidate.isNotEmpty(relatedCases)){
        for (GenericValue relatedCase: relatedCases) {
            caseIds.add(relatedCase.getString("caseId"));
        }
    }

    List list = (UtilMisc.toList(
            EntityCondition.makeCondition("caseId", EntityOperator.IN, caseIds),
            EntityCondition.makeCondition("deleted", EntityOperator.EQUALS, null),
            EntityCondition.makeCondition("archiveDate", EntityOperator.EQUALS, null)
    ));
    List cases = null;
    EntityQuery caseQuery = EntityQuery.use(delegator).from("CaseWithCreatePartyInfo").where(list).orderBy("-startDate");
    UtilPagination.PaginationResultDatatables queryRs = null;
    if(pageParam != null){
        queryRs = UtilPagination.queryPageDatatables(caseQuery, pageParam);
        cases = queryRs.data;
        success.put("pageResult", queryRs);
    }else{
        cases = caseQuery.queryList();
    }
    List<Map<String, Object>> caseList = new ArrayList<>();
    for (GenericValue oneCase : cases) {
        String caseId = oneCase.get("caseId");
        String contractId = oneCase.get("contract");
        List<String> joinGroups = new ArrayList<>();

        /*GenericValue contract = delegator.findOne("TblContract", UtilMisc.toMap("contractId", contractId), false);
        String contractName = null;
        if(UtilValidate.isNotEmpty(contract)){
            contractName = contract.getString("contractName");
        }*/
        //合同功能已经重构，这边需要修改
        List<GenericValue> contractList = EntityQuery.use(delegator).from("TblCaseRelatedContract").where("caseId", caseId).queryList();
        List contractNameList = new ArrayList();

        if (contractList != null && contractList.size() != 0) {
            for (GenericValue contractRealted : contractList) {
                String contractIds = contractRealted.get("contractId");
                GenericValue contract = EntityQuery.use(delegator).from("TblContract").where("contractId", contractIds).queryOne();
                if (contract != null) {
                    contractNameList.add(contract.get("contractName"));
                }
            }
        }
        String contractName = StringUtils.join(contractNameList, ", ");
        Map<String, Object> map = new HashMap<>();
        map.put("caseId", caseId);
        map.put("groupName", oneCase.getString("ownerGroupName"));//企业名称
        map.put("title", oneCase.get("title"));
        map.put("startDate", oneCase.get("startDate"));
        map.put("status", oneCase.get("status"));
        map.put("dueDate", oneCase.get("dueDate"));
        map.put("completeDate", oneCase.get("completeDate"));
        map.put("contractName", contractName);
        String createPartyId = oneCase.get("createPartyId")
        String name1 = oneCase.getString("partnerGroupName");
        String name2 = oneCase.getString("groupName");
        String name3 = oneCase.getString("fullName");
        String fullName = UtilValidate.isNotEmpty(name1) ? name1 : (UtilValidate.isNotEmpty(name2) ? name2 : name3);

        map.put("fullName", fullName);//创建CASE人员/机构信息

        List<GenericValue> caseParties = EntityQuery.use(delegator).from("casePartyIdNameDescription").where("caseId", caseId).queryList();
        for (GenericValue caseParty : caseParties) {
            if (!"CASE_ROLE_OWNER".equals(caseParty.getString("roleTypeId"))) {
                String caseGroupName = caseParty.getString("groupName");
                String casePartnerGroupName = caseParty.getString("partnerGroupName");
                joinGroups.add(UtilValidate.isNotEmpty(casePartnerGroupName) ? casePartnerGroupName : caseGroupName);
            } else if (caseParty.getString("partyId").equals(groupPartyId)) {
                map.put("caseOwner", true);//企业账户管理自己的case
            }
        }
        if(createPartyId.equals(partyId)){
            map.put("caseAscription", true);
        }

        //joinGroups.remove(joinGroups.indexOf(groupName))
        map.put("joinGroups", joinGroups.join(", "));
        caseList.add(map);
    }
    if(queryRs != null){
        queryRs.setDataMap(caseList);
    }
    success.put("data", caseList);
    return success;
}


public Map<String, Object> caseHome() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    String caseName = delegator.findOne("TblCase", UtilMisc.toMap("caseId", caseId), false).get("title");
    String partyId = context.get("userLogin").get("partyId");
    List<GenericValue> genericValue = EntityQuery.use(delegator).from("TblCasePartyMember").where(UtilMisc.toMap("caseId", caseId, "partyId", partyId)).queryList();
    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    String groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
    List<GenericValue> casePartyList = EntityQuery.use(delegator).from("TblCaseParty").where(UtilMisc.toMap("partyId", partyId, "caseId", caseId)).queryList();
    Boolean flag = false;
    if (UtilValidate.isNotEmpty(casePartyList)) {
        flag = true;
    }
    if (UtilValidate.isEmpty(genericValue) && !flag) {
        return ServiceUtil.returnError("您已不是当前case的成员，请刷新页面！");
    }

    //查询case参与方、case进度
    List<GenericValue> joinParties = delegator.findByAnd("casePartyIdNameDescription", UtilMisc.toMap("caseId", caseId), null, false);
    for (GenericValue party : joinParties) {
        if ("CASE_ROLE_OWNER".equals(party.getString("roleTypeId"))) {
            success.put("companyPartyId", party.getString("partyId"));
            break;
        }
    }

    success.put("caseId", caseId);
    success.put("caseName", caseName);
    success.put("partyId", partyId);
    success.put("groupId", groupId);
    success.put("joinParties", joinParties);
    return success;
}

public Map<String, Object> addCasePartyMember() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    String caseId = context.get("caseId");
    String roleTypeId = context.get("roleTypeId");
    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    String groupPartyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;
    GenericValue memberToStore = from("TblCasePartyMember").where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId)).queryOne();
    if (!context.get("isAdd") && UtilValidate.isNotEmpty(memberToStore)) {//修改
        String oldRoleTypeId = memberToStore.get("roleTypeId").toString();
        List<GenericValue> genericValueList = EntityQuery.use(delegator).from("TblCasePartyMember").where(UtilMisc.toMap("groupPartyId", groupPartyId, "caseId", caseId)).queryList();
        for (GenericValue genericValue : genericValueList) {
            if (!partyId.equals(genericValue.get("partyId").toString()) && genericValue.get("roleTypeId").equals("CASE_PERSON_ROLE_MANAGER")) {
                genericValue.put("roleTypeId", "CASE_PERSON_ROLE_STAFF");
                genericValue.store();
            } else if (partyId.equals(genericValue.get("partyId").toString())) {
                genericValue.put("roleTypeId", "CASE_PERSON_ROLE_MANAGER");
                genericValue.store();
            }
        }
        memberToStore.put("roleTypeId", roleTypeId);
        memberToStore.store();
        savePartyMember(partyId, caseId, roleTypeId, oldRoleTypeId);
        success.put("data", "修改成功");
    } else {//添加
        if (roleTypeId.equals("CASE_PERSON_ROLE_MANAGER")) {
            List<GenericValue> genericValueList = EntityQuery.use(delegator).from("TblCasePartyMember").where(UtilMisc.toMap("groupPartyId", groupPartyId, "caseId", caseId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER")).queryList();
            for(GenericValue genericValue1 : genericValueList){
                genericValue1.set("roleTypeId","CASE_PERSON_ROLE_STAFF")
            }
            delegator.storeAll(genericValueList)
        }
        context.put("joinDate", new java.sql.Date(new java.util.Date().getTime()));
        if(UtilValidate.isNotEmpty(memberToStore)){
            memberToStore.set("roleTypeId", "CASE_PERSON_ROLE_MANAGER");
            memberToStore.store();
            success.put("data", "修改成功");
        }else{
            delegator.create(delegator.makeValidValue("TblCasePartyMember", context));
            success.put("data", "添加成功");
        }

        savePartyMember(partyId, caseId, roleTypeId, "add");

    }
    /*if (roleTypeId.equals("CASE_PERSON_ROLE_MANAGER")) {
        saveCasePrompt(caseId, partyId);
    }*/
    return success;
}
/*
public void saveCasePrompt(String caseId, String partyId) {
    List<GenericValue> genericValueList = EntityQuery.use(delegator).from("TblCasePrompt").where(UtilMisc.toMap("caseId", caseId)).queryList();
    if (genericValueList != null) {
        for (GenericValue genericValue : genericValueList) {
            genericValue.put("partyId", partyId);
            genericValue.store();
        }
    }
}*/

/**
 * 修改或者新增项目经理时，修改节点负责人
 * @param partyId 负责人
 * @param caseId caseId
 * @param roleTypeId 新类型
 * @param oldRoleTypeId 旧类型或者新增标志
 */
public void savePartyMember(String partyId, String caseId, String roleTypeId, String oldRoleTypeId) {
    GenericValue person = EntityQuery.use(delegator).from("FullNameGroupName").where(UtilMisc.toMap("partyId", partyId)).distinct().queryOne();
    if (person != null) {
        String orgId = person.get("groupId");
        GenericValue memberInfo = from("TblCaseParty").where(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId), EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, orgId)).queryOne();
        if (memberInfo != null) {
            String memberId = memberInfo.get("personId");
            if (roleTypeId.equals("CASE_PERSON_ROLE_MANAGER") && (oldRoleTypeId.equals("CASE_PERSON_ROLE_STAFF") || oldRoleTypeId.equals("add"))) {
                memberInfo.put("personId", partyId);
            } else if (oldRoleTypeId.equals("CASE_PERSON_ROLE_MANAGER") && roleTypeId.equals("CASE_PERSON_ROLE_STAFF")) {
                memberInfo.put("personId", "");
            }
            if (!memberInfo.get("personId").equals(memberId)) {
                memberInfo.store()
            }
        }

    }
}


public Map<String, Object> saveCaseFolders() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    Map<String, Object> roleFolders = context.get("roleFolders");
    String templateId = context.get("templateId");
    String caseId = context.get("caseId");
    Boolean isCompany = context.get("isCompany");//是否是企业参与方修改保存目录设置

    Map<String, List<String>> newRolePath = new HashMap<>();
    for (Map.Entry<String, Object> pair : roleFolders.entrySet()) {
        String role = pair.key;
        role = role.substring(0, role.lastIndexOf("-"));
        String folderPath = pair.value;
        List<String> folderRoles = newRolePath.get(folderPath);
        if (folderRoles == null) {
            folderRoles = new ArrayList<>();
            newRolePath.put(folderPath, folderRoles);
        }
        folderRoles.add(role);
    }

    if(UtilValidate.isEmpty(templateId) && UtilValidate.isNotEmpty(caseId)){//已存在的CASE只能由企业维护目录授权
        if(!isCompany){
            throw new RuntimeException("无权修改企业文件授权");
        }
        if (newRolePath.size() > 0) {
            for (Map.Entry<String, List<String>> pair : newRolePath.entrySet()) {//根据目录或文件逐个授权给所选机构
                if (UtilValidate.isEmpty(pair.value)) {
                    continue;
                }
                String[] fileInfo = pair.key.split("_");
                runService("updateCasePartyFolderShare", UtilMisc.toMap("caseId", caseId, "fileId", fileInfo[0], "fileType", fileInfo[1], "sharedRoles", pair.value.join(","), "userLogin", context.get("userLogin")));
            }
        }

    }else{//维护某个模板，不涉及已存在CASE的权限变更问题
        //清理原先的目录
        GenericValue template = from("TblCaseTemplate").where("id", templateId).queryOne();
        /*String caseCategory = template.getString("templateKey");
        if (UtilValidate.isEmpty(caseCategory)) {
            caseCategory = templateId;//企业、机构自建模板
        }*/
        delegator.removeByAnd("TblCaseCategoryFolder", "caseCategory", templateId);//相同类型的模板可能有多个，目录设置应该以模板为单位


        if (newRolePath.size() > 0) {
            for (Map.Entry<String, List<String>> pair : newRolePath.entrySet()) {
                if (UtilValidate.isEmpty(pair.value)) {
                    continue;
                }
                int splitIdx = pair.key.lastIndexOf("_");
                String[] fileInfo = pair.key.split("_");
                String folderPath = "";
                String fileType = "folder";
                if(splitIdx > -1){
                    folderPath = pair.key.substring(0, splitIdx);
                    fileType = pair.key.substring(splitIdx + 1);
                }else{
                    folderPath = pair.key;
                }
                GenericValue newFolder = delegator.makeValue("TblCaseCategoryFolder");
                newFolder.setString("id", delegator.getNextSeqId("TblCaseCategoryFolder"));
                newFolder.setString("caseCategory", templateId);
                newFolder.setString("folderPath", folderPath);
                newFolder.setString("fileType", fileType);
                newFolder.setString("roles", pair.value.join(","));
                newFolder.create();
            }
        }
    }

    String caseProgressGroupId = "";
    if(UtilValidate.isNotEmpty(caseId)){
        GenericValue progressGroup = EntityQuery.use(delegator).from("TblCaseProgressGroup").where(UtilMisc.toMap("caseId", caseId, "seq", 0)).queryOne();
        if (UtilValidate.isNotEmpty(progressGroup)) {
            caseProgressGroupId = progressGroup.get("id");
        }
    }
    success.put("caseProgressGroupId", caseProgressGroupId);
    success.put("caseId", caseId);
    return success;
}

public Map<String, Object> createCaseFromSession() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    Map<String, Object> blankCaseData = context.get("blankCaseData");
    GenericValue userLogin = context.get("userLogin");
    //保存case基本信息
    GenericValue caseEntity = blankCaseData.get("case");
    delegator.create(caseEntity);
    String caseId = caseEntity.getString("caseId");
    delegator.storeAll(blankCaseData.get("caseParties"));
    List<Map<String, Object>> casePartyMembers = blankCaseData.get("casePartyMembers");
    for (Map<String, Object> casePartyMember : casePartyMembers) {
        runService("AddCasePartyMember", UtilMisc.toMap("userLogin", userLogin, "isAdd", true, "caseId", caseId, "partyId", casePartyMember.get("partyId"), "groupPartyId", casePartyMember.get("groupPartyId"), "roleTypeId", casePartyMember.get("roleTypeId")));
    }
    delegator.storeAll(blankCaseData.get("caseBaseTimes"));
    List<GenericValue> nodeGroups =  blankCaseData.get("nodes");
    delegator.storeAll(nodeGroups);

    Map<String, List<GenericValue>> tasks = blankCaseData.get("nodeTasks");
    List<GenericValue> progresses = new ArrayList<>();
    if(UtilValidate.isNotEmpty(tasks)){
        for (List<GenericValue> oldTasks : tasks.values()) {
            progresses.addAll(oldTasks);
        }
    }
    delegator.storeAll(progresses);

    Map<String, List<GenericValue>> progressAttas = blankCaseData.get("progressAttas")
    List<GenericValue> progressAttaList = new ArrayList<>();
    if(UtilValidate.isNotEmpty(progressAttas)){
        for (List<GenericValue> attas : progressAttas.values()) {
            progressAttaList.addAll(attas);
        }
    }
    delegator.storeAll(progressAttaList);

    for (GenericValue nodeGroup : nodeGroups) {
        if("0".equals(nodeGroup.get("seq").toString())){
            success.put("caseProgressGroupId", nodeGroup.getString("id"));
        }
    }

    success.put("caseId", caseId);
    return success;
}


public Map<String, Object> saveBlankCaseFolders() {
    HttpServletRequest request = context.get("request");
    HttpSession session = request.getSession();
    String blankCaseSessionKey = request.getParameter("blankCaseSessionKey");
    Map<String, Object> blankCaseData = session.getAttribute(blankCaseSessionKey);
    if(blankCaseData == null){
        throw new RuntimeException("会话已过期，请重新创建");
    }
    //创建case记录
    Map<String, Object> result = runService("createCaseFromSession", UtilMisc.toMap("blankCaseData", blankCaseData, "userLogin", context.get("userLogin")));
    String caseId = result.get("caseId");
    Map<String, Object> roleFolders = context.get("roleFolders");
    Boolean isCompany = context.get("isCompany");//是否是企业参与方修改保存目录设置
    //设置目录授权
    return runService("saveCaseFolders", UtilMisc.toMap("caseId", caseId, "roleFolders", roleFolders, "isCompany", isCompany, "userLogin", context.get("userLogin")));
}

public Map<String, Object> removeMember() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    String memberId = context.get("memberId");
    delegator.removeByAnd("TblCasePartyMember", UtilMisc.toMap("caseId", caseId, "partyId", memberId));
    success.put("data", "移除成功");
    return success;
}

/**
 * 新增某个或几个参与方在case上的授权
 * @return
 */
public Map<String, Object> addCasePartyFolderPermission() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");

    String fileId = context.get("fileId");
    String fileType = context.get("fileType");
    String moduleType = "case";
    String sharedRoles = context.get("sharedRoles");

    Map<String, String> providerRoleMap = new HashMap<>();
    if(UtilValidate.isNotEmpty(sharedRoles)){
        List<GenericValue> caseParties = EntityQuery.use(delegator).from("CaseProviderAndOwner").where("caseId", caseId).queryList();
        if(UtilValidate.isNotEmpty(caseParties)){
            for (GenericValue caseParty : caseParties) {
                String prividerRole = caseParty.getString("providerRole");
                providerRoleMap.put(prividerRole, caseParty.getString("providerPartyId"));
            }
        }
    }

    if("folder".equalsIgnoreCase(fileType)){
        GenericValue dataResourceRole = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",fileId,"folderType","1")).queryOne();
        String[] sharedRolesArray = StringUtils.split(sharedRoles, ",");
        for (String sharedRole : sharedRolesArray) {
            if("CASE_ROLE_OWNER".equals(sharedRole)){
                continue;
            }
            String sharePartyId = providerRoleMap.get(sharedRole);
            if(UtilValidate.isEmpty(sharePartyId)){
                continue;
            }
            dataResourceRole.put("id", delegator.getNextSeqId("TblDirectoryStructureId"));
            dataResourceRole.put("folderType", "2");
            dataResourceRole.put("folderPermissions", "000111");//除分享外的所有权限
            dataResourceRole.put("partyId", sharePartyId);
            dataResourceRole.put("shareFromModule", moduleType);
            dataResourceRole.put("shareFromModuleId", caseId);
            dataResourceRole.create();
        }
    }else if("file".equalsIgnoreCase(fileType)){
        GenericValue dataResourceRole = EntityQuery.use(delegator).select().from("TblFileOwnership").where(UtilMisc.toMap("fileId",fileId,"fileType","1")).queryOne();
        String[] sharedRolesArray = StringUtils.split(sharedRoles, ",");
        for (String sharedRole : sharedRolesArray) {
            if("CASE_ROLE_OWNER".equals(sharedRole)){
                continue;
            }
            dataResourceRole.put("id", delegator.getNextSeqId("TblDirectoryStructureId"));
            dataResourceRole.put("fileType", "2");
            dataResourceRole.put("filePermissions", "000111");//除分享外的所有权限
            dataResourceRole.put("partyId", providerRoleMap.get(sharedRole));
            dataResourceRole.put("shareFromModule", moduleType);
            dataResourceRole.put("shareFromModuleId", caseId);
            dataResourceRole.create();
        }
    }
    return success;
}

/**
 * 修改某个文件或文件夹针对各参与方的授权。
 * 先回收原有各参与方在该目录或文件上的授权，
 * 然后重新授权给所选的参与方
 * @return
 */
public Map<String, Object> updateCasePartyFolderShare() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");

    String fileId = context.get("fileId");
    String fileType = context.get("fileType");
    String moduleType = "case";
    String sharedRoles = context.get("sharedRoles");

    Map<String, String> providerRoleMap = new HashMap<>();
    if(UtilValidate.isNotEmpty(sharedRoles)){
        List<GenericValue> caseParties = EntityQuery.use(delegator).from("CaseProviderAndOwner").where("caseId", caseId).queryList();
        if(UtilValidate.isNotEmpty(caseParties)){
            for (GenericValue caseParty : caseParties) {
                String prividerRole = caseParty.getString("providerRole");
                providerRoleMap.put(prividerRole, caseParty.getString("providerPartyId"));
            }
        }
    }

    if("folder".equalsIgnoreCase(fileType)){
//删除原共享目录
        delegator.removeByAnd("TblDirectoryStructure", "folderType","2","folderId",fileId, "shareFromModule", moduleType, "shareFromModuleId", caseId);

        GenericValue dataResourceRole = EntityQuery.use(delegator).select().from("TblDirectoryStructure").where(UtilMisc.toMap("folderId",fileId,"folderType","1")).queryOne();
        String[] sharedRolesArray = StringUtils.split(sharedRoles, ",");
        for (String sharedRole : sharedRolesArray) {
            if("CASE_ROLE_OWNER".equals(sharedRole)){
                continue;
            }
            String casePartyId = providerRoleMap.get(sharedRole);
            if(UtilValidate.isEmpty(casePartyId)){
                continue;
            }
            dataResourceRole.put("id", delegator.getNextSeqId("TblDirectoryStructureId"));
            dataResourceRole.put("folderType", "2");
            dataResourceRole.put("folderPermissions", "000111");//除分享外的所有权限
            dataResourceRole.put("partyId", casePartyId);
            dataResourceRole.put("shareFromModule", moduleType);
            dataResourceRole.put("shareFromModuleId", caseId);
            dataResourceRole.create();
        }
    }else if("file".equalsIgnoreCase(fileType)){
        //删除原共享文件
        delegator.removeByAnd("TblFileOwnership", "fileType","2","fileId",fileId, "shareFromModule", moduleType, "shareFromModuleId", caseId);

        GenericValue dataResourceRole = EntityQuery.use(delegator).select().from("TblFileOwnership").where(UtilMisc.toMap("fileId",fileId,"fileType","1")).queryOne();
        String[] sharedRolesArray = StringUtils.split(sharedRoles, ",");
        for (String sharedRole : sharedRolesArray) {
            String casePartyId = providerRoleMap.get(sharedRole);

            if("CASE_ROLE_OWNER".equals(sharedRole)){
                continue;
            }
            if(UtilValidate.isEmpty(casePartyId)){
                continue;
            }
            dataResourceRole.put("id", delegator.getNextSeqId("TblFileOwnershipId"))
            dataResourceRole.put("fileType", "2");
            dataResourceRole.put("filePermissions", "000111");//除分享外的所有权限
            dataResourceRole.put("partyId", casePartyId);
            dataResourceRole.put("shareFromModule", moduleType);
            dataResourceRole.put("shareFromModuleId", caseId);
            dataResourceRole.create();
        }
    }


    return success;
}

/**
 * 当人员从CASE成员中移除时需要删除目录共享，取消其对企业目录的可见权限
 * //changed by galaxypan@2017-09-15:共享只涉及参与方层次，不需要具体到成员。成员的权限继承与参与方。子账号通过case成员才能进入case进行控制。
 * @return
 */
public Map<String, Object> revokeCasePartyFolderPermission() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    String partyId = context.get("memberId");
    delegator.removeByAnd("TblDirectoryStructure", "shareFromModule", "case", "shareFromModuleId", caseId, "partyId", partyId, "folderType", "2");
    delegator.removeByAnd("TblFileOwnership", "shareFromModule", "case", "shareFromModuleId", caseId, "partyId", partyId, "fileType", "2");
    return success;
}

/**
 * CASE的参与方更改时，新的参与方继承原参与方的文件授权方案
 * @return
 */
private void switchCasePartyFolderPermission(String caseId, String fromGroup, String toGroup) {
    List<GenericValue> oldShares = EntityQuery.use(delegator).from("TblDirectoryStructure").where("shareFromModule", "case", "shareFromModuleId", caseId, "partyId", fromGroup, "folderType", "2").queryList();
    if(UtilValidate.isNotEmpty(oldShares)){
        for (GenericValue oldShare : oldShares) {
            oldShare.setString("partyId", toGroup);
        }
        delegator.storeAll(oldShares);
    }


    oldShares = EntityQuery.use(delegator).from("TblFileOwnership").where("shareFromModule", "case", "shareFromModuleId", caseId, "partyId", fromGroup, "fileType", "2").queryList();
    if(UtilValidate.isNotEmpty(oldShares)){
        for (GenericValue oldShare : oldShares) {
            oldShare.setString("partyId", toGroup);
        }
        delegator.storeAll(oldShares);
    }
}

//TODO:当一个CASE结束时需要回收所有参与方对企业文档的可见权限
public Map<String, Object> revokeAllCasePartyFolderPermission() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");

    //查询该case中所有非企业参与方
    List<EntityCondition> searchConds = new ArrayList<>();
    searchConds.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
    searchConds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "CASE_ROLE_OWNER"));
    List<GenericValue> caseMembers = EntityQuery.use(delegator).from("TblCaseParty").where(searchConds).queryList();
    //依次取消所有非企业成员的共享权限
    for (GenericValue caseMember : caseMembers) {
        runService("revokeCasePartyFolderPermission", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "caseId", caseId, "memberId", caseMember.getString("partyId")));
    }
    return success;
}

/**
 * 初始化任务页面
 * @return
 */
public Map<String, Object> initCaseTask() {
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String caseId = context.get("caseId");
    String partyGroupId = context.get("partyGroupId");
    GenericValue casePartyMember = EntityQuery.use(delegator).select().from("TblCasePartyMember").where(UtilMisc.toMap("caseId", caseId, "partyId", partyId)).queryOne();
    Map<String, Object> map = new HashMap<>();
    if (UtilValidate.isNotEmpty(casePartyMember)) {
        map.put("roleTypeId", casePartyMember.get("roleTypeId"));
    }
    List<Map<String, Object>> memberList = new ArrayList<>();
    List memberConditionList = new ArrayList();
    String successCode = "";
    //如果当前用户是项目经理，则需要在查询组员列表
    if (casePartyMember.get("roleTypeId").equals("CASE_PERSON_ROLE_MANAGER")) {
        memberConditionList.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
        memberConditionList.add(EntityCondition.makeCondition("groupPartyId", EntityOperator.EQUALS, partyGroupId));
        /*memberConditionList.add(EntityCondition.makeCondition("personPartyId", EntityOperator.NOT_EQUAL, partyId));*/
        successCode = "leaderCaseTask"
    } else {
        memberConditionList.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
        memberConditionList.add(EntityCondition.makeCondition("groupPartyId", EntityOperator.EQUALS, partyGroupId));
        memberConditionList.add(EntityCondition.makeCondition("personPartyId", EntityOperator.EQUALS, partyId));
        successCode = "memberCaseTask";
    }
    //如果是项目经理，那么这里的memberlist获取的是所有任务，如果是成员，这里获取的个人任务
    List<GenericValue> memberInfoList = EntityQuery.use(delegator).select().from("casePartyMembers").where(memberConditionList).queryList();
    if (memberInfoList != null) {
        for (GenericValue memberInfo : memberInfoList) {
            Map<String, Object> memberMap = new HashMap<>();
            memberMap.putAll(memberInfo);
            Map<String, Object> caseTaskDataMap = dispatcher.runSync("searchCaseTaskList", UtilMisc.toMap("caseId", caseId, "partyGroupId", partyGroupId, "memberId", memberMap.get("personPartyId"), "userLogin", userLogin));
            if (caseTaskDataMap != null) {
                Map<String, Object> caseTaskMap = caseTaskDataMap.get("data");
                if (caseTaskMap.get("caseTasUnfinishedList") != null || caseTaskMap.get("caseTaskFinishList")) {
                    memberMap.put("caseTasUnfinishedList", caseTaskMap.get("caseTasUnfinishedList"));
                    memberMap.put("caseTaskFinishList", caseTaskMap.get("caseTaskFinishList"));
                    memberList.add(memberMap);

                }
            }
        }
    }
    //项目经理获取个人任务
    List conditions = new ArrayList();
    List manageList = new ArrayList();
    if (casePartyMember.get("roleTypeId").equals("CASE_PERSON_ROLE_MANAGER")) {
        conditions.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
        conditions.add(EntityCondition.makeCondition("groupPartyId", EntityOperator.EQUALS, partyGroupId));
        memberConditionList.add(EntityCondition.makeCondition("personPartyId", EntityOperator.EQUALS, partyId));
        List<GenericValue> memberInfos = EntityQuery.use(delegator).select().from("casePartyMembers").where(memberConditionList).queryList();
        if (memberInfoList != null) {
            for (GenericValue memberInfo : memberInfos) {
                Map<String, Object> memberMap = new HashMap<>();
                memberMap.putAll(memberInfo);
                Map<String, Object> caseTaskDataMap = dispatcher.runSync("searchCaseTaskList", UtilMisc.toMap("caseId", caseId, "partyGroupId", partyGroupId, "memberId", memberMap.get("personPartyId"), "userLogin", userLogin, "isManageTask", "Y"));
                if (caseTaskDataMap != null) {
                    Map<String, Object> caseTaskMap = caseTaskDataMap.get("data");
                    if (caseTaskMap.get("caseTasUnfinishedList") != null || caseTaskMap.get("caseTaskFinishList")) {
                        memberMap.put("caseTasUnfinishedList", caseTaskMap.get("caseTasUnfinishedList"));
                        memberMap.put("caseTaskFinishList", caseTaskMap.get("caseTaskFinishList"));
                        manageList.add(memberMap);
                    }
                }
            }
        }
    }

    List codeConditionList = new ArrayList();
    codeConditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CASE_STATUS"));
    codeConditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "CASE_STATUS_FILED"));
    List<GenericValue> caseStatusTypeList = EntityQuery.use(delegator).from("Enumeration").where(codeConditionList).queryList();
    map.put("caseStatusTypeList", caseStatusTypeList);
    map.put("memberList", memberList);
    map.put("manageList", manageList);
    map.put("caseId", caseId);
    map.put("partyGroupId", partyGroupId);
    Map<String, Object> success = ServiceUtil.returnMessage(successCode, null);
    success.put("data", map);
    return success;
}

/**
 * 查询任务列表
 * @return
 */
public Map<String, Object> searchCaseTaskList() {

    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String memberId = context.get("memberId");
    String isManageTask = context.get("isManageTask");
    if (UtilValidate.isNotEmpty(memberId)) {
        partyId = memberId;
    }
    String caseId = context.get("caseId");
    String partyGroupId = context.get("partyGroupId");
    GenericValue casePartyMember = EntityQuery.use(delegator).select().from("TblCasePartyMember").where(UtilMisc.toMap("caseId", caseId, "partyId", partyId)).queryOne();
    String personType = "";
    if (UtilValidate.isNotEmpty(casePartyMember)) {
        personType = casePartyMember.get("roleTypeId");
    }
    List conditionList = new ArrayList();
    //区分组员还是项目经理
    if (personType.equals("CASE_PERSON_ROLE_MANAGER")) {
        conditionList.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
        conditionList.add(EntityCondition.makeCondition("partyGroupId", EntityOperator.EQUALS, partyGroupId));
        /*conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, partyId));*/
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    } else {
        conditionList.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
        conditionList.add(EntityCondition.makeCondition("partyGroupId", EntityOperator.EQUALS, partyGroupId));
        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    }
    List unconditionList = new ArrayList();
    unconditionList.addAll(conditionList);
    unconditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("status", EntityOperator.EQUALS, "CASE_STATUS_ALLOCATED"),
                                                       EntityCondition.makeCondition("status", EntityOperator.EQUALS, "CASE_STATUS_BACKLOG")], EntityOperator.OR));
    List<GenericValue> caseTasUnfinishedList = EntityQuery.use(delegator).select().from("caseTaskList").where(unconditionList).orderBy("dueTime DESC").queryList();
    List<Map<String, Object>> unCaseList = searchDataResource(caseTasUnfinishedList);
    conditionList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "CASE_STATUS_FINISH"));
    List<GenericValue> caseTaskFinishList = EntityQuery.use(delegator).select().from("caseTaskList").where(conditionList).orderBy("dueTime DESC").queryList();
    List<Map<String, Object>> caseList = searchDataResource(caseTaskFinishList);
    Map<String, Object> map = new HashMap<>();
    map.put("personType", personType);
    map.put("caseTasUnfinishedList", unCaseList);
    map.put("caseTaskFinishList", caseList);
    Map<String, Object> success = ServiceUtil.returnSuccess();
    success.put("data", map);
    return success;
}

public List<Map<String, Object>> searchDataResource(List<GenericValue> caseTaskList) {
    List<Map<String, Object>> caseList = new ArrayList<>();
    for (GenericValue finishedCase : caseTaskList) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(finishedCase);
        String id = map.get("id");
        List<GenericValue> dataResourceList = EntityQuery.use(delegator).select().from("caseTaskDataResource").where(UtilMisc.toMap("taskId", id)).queryList();
        map.put("dataReourceList", dataResourceList);
        caseList.add(map);
    }
    return caseList;
}

/**
 * 增加任务
 * @return
 */
public Map<String, Object> saveCaseTask() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String caseTaskFile = (String) context.get("caseTaskFile");
    List<String> newFileIds = new ArrayList<>();
    if(UtilValidate.isNotEmpty(caseTaskFile)){
        newFileIds = UtilMisc.toListArray(caseTaskFile.split(","));
    }
    context.put("status", "CASE_STATUS_ALLOCATED");

    String dueTime = context.get("dueTime");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = sdf.parse(dueTime);
    context.put("dueTime", new Timestamp(date.getTime()))
    String castTaskId = context.get("id");
    if (UtilValidate.isEmpty(castTaskId)) {
        castTaskId = delegator.getNextSeqId("TblCaseTask");
        context.put("id", castTaskId);
        GenericValue castTask = delegator.makeValidValue("TblCaseTask", context);
        castTask.create();
    } else {
        GenericValue castTask = delegator.makeValidValue("TblCaseTask", context);
        List<GenericValue> list = EntityQuery.use(delegator).select("dataResourceId").from("TblCaseTaskAtta").where(UtilMisc.toMap("taskId", castTaskId)).queryList();
        List<String> existingFileIds = new ArrayList<>();
        for (GenericValue genericValue : list) {
            existingFileIds.add(genericValue.get("dataResourceId").toString());
        }

        for (String fileId : existingFileIds) {
            if(!newFileIds.contains(fileId)){
                delegator.removeByAnd("TblCaseTaskAtta", UtilMisc.toMap("taskId", castTaskId, "dataResourceId", fileId));
                GenericValue dataResourceInfo = EntityQuery.use(delegator).from("DataResource").where(UtilMisc.toMap("dataResourceId", fileId)).queryOne();
                if (dataResourceInfo != null) {
                    FileTypeManager fileManager = FileManagerFactory.getFileManager(dataResourceInfo.get("dataResourceTypeId").toString(), (GenericDelegator) delegator);
                    fileManager.delFile(fileId);
                }
            }else{
                newFileIds.remove(fileId);
            }
        }
        castTask.store();
    }
    //存储文件信息
    for (String dataResourceId : newFileIds) {
        context.put("dataResourceId", dataResourceId);
        context.put("taskId", castTaskId);
        GenericValue caseTaskAtta = delegator.makeValidValue("TblCaseTaskAtta", context);
        caseTaskAtta.create();
    }
    success.put("msg", "保存成功");
    return success;
}

public Map<String, Object> editCaseTask() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String id = context.get("caseTaskId");
    String status = context.get("caseTaskStatus")
    GenericValue caseTask = EntityQuery.use(delegator).from("TblCaseTask").where(UtilMisc.toMap("id", id)).queryOne();
    if (caseTask != null) {
        caseTask.put("status", status);
        if (status.equals("CASE_STATUS_FINISH")) {
            Date date = new Date();
            caseTask.put("completeTime", new Timestamp(date.getTime()));
        }
        caseTask.store();
    }
    success.put("msg", "修改成功");
    return success;
}

/**
 * 删除任务及文件
 * @return
 */
public Map<String, Object> delCaseTask() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseTaskId = context.get("caseTaskId");
    //查找文件信息，在删除任务之前先要把文件信息和文件删除，避免脏数据
    List<GenericValue> dataResourceList = EntityQuery.use(delegator).select().from("TblCaseTaskAtta").where(UtilMisc.toMap("taskId", caseTaskId)).queryList();
    for (GenericValue genericValue : dataResourceList) {
        String fileId = genericValue.get("dataResourceId");
        GenericValue dataResource = EntityQuery.use(delegator).select().from("DataResource").where(UtilMisc.toMap("dataResourceId", fileId)).queryOne();
        if (dataResource != null) {
            fileManager = FileManagerFactory.getFileManager(dataResource.get("dataResourceTypeId").toString(), (GenericDelegator) delegator);
            fileManager.delFile(fileId);
        }
    }
    delegator.removeByAnd("TblCaseTaskAtta", UtilMisc.toMap("taskId", caseTaskId))
    delegator.removeByAnd("TblCaseTask", UtilMisc.toMap("id", caseTaskId))
    success.put("msg", "删除成功");
    return success;
}

public Map<String, Object> openCaseTaskEdit() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String caseTaskId = context.get("caseTaskId");
    String caseId = context.get("caseId");
    String partyGroupId = context.get("partyGroupId");

    List memberConditionList = new ArrayList();
    memberConditionList.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
    memberConditionList.add(EntityCondition.makeCondition("groupPartyId", EntityOperator.EQUALS, partyGroupId));
    /*memberConditionList.add(EntityCondition.makeCondition("personPartyId", EntityOperator.NOT_EQUAL, partyId));*/
    List<GenericValue> memberInfoList = EntityQuery.use(delegator).select().from("casePartyMembers").where(memberConditionList).queryList();
    Map<String, Object> map = new HashMap<>();
    map.put("memberInfoList", memberInfoList);
    if (UtilValidate.isNotEmpty(caseTaskId)) {
        GenericValue caseTask = EntityQuery.use(delegator).select().from("TblCaseTask").where(UtilMisc.toMap("id", caseTaskId)).queryOne();
        List<GenericValue> dataResourceList = EntityQuery.use(delegator).select().from("TblCaseTaskAtta").where(UtilMisc.toMap("taskId", caseTaskId)).queryList();
        map.put("caseTask", caseTask);
        List<String> dsIds = new ArrayList<>();
        for (GenericValue node  : dataResourceList) {
            dsIds.add(node.getString("dataResourceId"));
        }
        map.put("dataResourceList", from("DataResource").where(EntityCondition.makeCondition("dataResourceId", EntityOperator.IN, dsIds)).queryList());
    }
    success.put("data", map);
    return success;
}

public Map<String, Object> saveCaseBaseTime() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    Map<String, String> baseTimes = context.get("baseTimes");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String caseId = context.get("caseId");

    List<GenericValue> oldTimes = EntityQuery.use(delegator).from("TblCaseBaseTime").where("caseId", caseId).queryList();
    for (GenericValue oldTime : oldTimes) {
        String newTime = baseTimes.get(oldTime.getString("baseTimeId"));
        if (UtilValidate.isNotEmpty(newTime)) {
            oldTime.set("baseTime", UtilDateTime.toSqlDate(newTime, "yyyy-MM-dd"));
        } else {
            oldTime.set("baseTime", null);
        }
    }
    delegator.storeAll(oldTimes);

    return success;
}

public Map<String, Object> getCasePrompt() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.get("partyId");
    List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
    partyId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : partyId;

    List<GenericValue> caseList = EntityQuery.use(delegator).from("CasePrompt").where(UtilMisc.toMap("partyId", partyId)).queryList();
    Map<String, Map<String, Object>> map = new HashMap<>();
    for (GenericValue caseInfo : caseList) {
        String caseId = caseInfo.getString("caseId");
        Map<String, Object> caseData = map.get(caseId);
        if(caseData == null){
            caseData = new HashMap<>();
            map.put(caseId, caseData);
        }
        caseData.put("progressId", caseInfo.getString("progressId"));
        String msgTitle = caseData.get("msgTitle");
        if (UtilValidate.isNotEmpty(msgTitle)) {
            caseData.put("msgTitle", msgTitle + "," + caseInfo.get("progressTitle"))
        } else {
            caseData.put("msgTitle", caseInfo.get("caseTitle") + ":" + caseInfo.get("progressTitle"))
        }
    }
    success.put("data", map);
    return success;
}
/**
 * @deprecated case进度任务通过查询获取
 * @return
 */
public Map<String, Object> delCasePrompt() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String caseId = context.get("caseId");
    String progressId = context.get("progressId");
    GenericValue genericValue = EntityQuery.use(delegator).from("TblCasePartyMember").where(UtilMisc.toMap("caseId", caseId, "partyId", partyId, "roleTypeId", "CASE_PERSON_ROLE_MANAGER")).queryOne();
    if (genericValue == null) {
        throw new RuntimeException("您已不是当前case的项目经理，请刷新页面！");
    }
    delegator.removeByAnd("TblCasePrompt", UtilMisc.toMap("caseId", caseId, "partyId", genericValue.getString("groupPartyId"), "progressId", progressId));
    success.put("msg", "删除成功")
    return success;
}

public Map<String, Object> updateCaseFiledStatus() {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    GenericValue caseInfo = EntityQuery.use(delegator).from("TblCase").where(UtilMisc.toMap("caseId", caseId)).queryOne();
    String msg = "归档成功";
    if (caseInfo != null) {
        caseInfo.put("completeDate",new java.sql.Date((new Date()).getTime()));
        caseInfo.put("status", "CASE_STATUS_ARCHIVED");
        caseInfo.store();
        //回收文档共享权限
        dispatcher.runSync("revokeAllCasePartyFolderPermission", UtilMisc.toMap("caseId", caseId, "userLogin", context.get("userLogin")));
    } else {
        msg = "case不存在，请刷新后重试";
    }
    success.put("msg", msg);
    return success;
}

/**
 * 初始化case合同
 */
public Map<String, Object> initCaseContract() {
    Map success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    /* //合同时间
     String time = "";
     List<GenericValue> contracts = new ArrayList<>();
     //从关系表中取出所有的合同
     List<GenericValue> contractList = EntityQuery.use(delegator).from("TblCaseRelatedContract").where("caseId",caseId).queryList();
     if(contractList!=null&&contractList.size()!=0) {
         for (GenericValue contraceRelated : contractList) {
             String contractId = contraceRelated.get("contractId");
             GenericValue contract = EntityQuery.use(delegator).from("TblContract").where("contractId", contractId).queryOne();
             if (contract != null) {
                 contracts.add(contract);
                 time = contract.get("startDate");
             }
         }
     }
     //取出case所有的参与方类型
     List roleList = new ArrayList();
     List<GenericValue> casePartyList = EntityQuery.use(delegator).from("TblCaseParty").where("caseId",caseId).queryList();
     if(casePartyList!=null&&casePartyList.size()!=0)
     {
         for(GenericValue caseParty:casePartyList)
         {
             String roleTypeId = caseParty.get("roleTypeId");
             if(!roleTypeId.equals("CASE_ROLE_OWNER"))
             {
                 GenericValue roleType = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",roleTypeId,"parentTypeId","CASE_ROLE").queryOne()
                 String description = roleType.get("description");
                 Map map = new HashMap();
                 map.put("roleTypeId",roleTypeId);
                 map.put("description",description);
                 roleList.add(map);
             }
         }
     }*/
    //1.需要找到企业的相关信息
    GenericValue caseGroup = EntityQuery.use(delegator).from("TblCaseParty").where("caseId", caseId, "roleTypeId", "CASE_ROLE_OWNER").queryOne();
    String partyId = caseGroup.get("partyId");
    GenericValue group = EntityQuery.use(delegator).from("BasicUnEnabledGroupInfo").where("partyId", partyId).queryOne();
    success.put("group", group);
    //2.获取参与方的相关信息
    List groups = new ArrayList();
    List hasContract = new ArrayList();
    List groupName = new ArrayList();
    List<EntityCondition> condition = [];
    condition.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId));
    condition.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "CASE_ROLE_OWNER"));
    List<GenericValue> caseMembers = EntityQuery.use(delegator).from("TblCaseParty").where(condition).queryList();
    for (GenericValue caseMember : caseMembers) {
        partyId = caseMember.get("partyId");
        GenericValue basicGroup = EntityQuery.use(delegator).from("BasicGroupInfo").where("partyId", partyId).queryOne();
        groups.add(basicGroup);
        groupName.add(basicGroup.get("groupName"));
    }
    //3.获取合同中和当前case相关的参与方
    List<GenericValue> caseContracts = EntityQuery.use(delegator).from("TblCaseRelatedContract").where("caseId", caseId).queryList();
    for (GenericValue caseContract : caseContracts) {
        String contractId = caseContract.get("contractId");
        GenericValue contract = EntityQuery.use(delegator).from("TblContract").where("contractId", contractId).queryOne();
        String secondPartyName = contract.get("secondPartyName");
        hasContract.add(secondPartyName);
        if (groupName.contains(secondPartyName)) {
            groupName.remove(secondPartyName);
        }

    }
    for (int i=0;i<groupName.size();i++)
    {
        if(hasContract.contains(groupName[i]))
        {
            groupName.remove(i);
        }
        else if(group.get("groupName").equals(groupName[i]))
        {
            groupName.remove(i);
        }
    }
    //未签订合同的所有机构
    success.put("groupName",groupName);
    //已经签订合同的所有机构
    success.put("hasContract",hasContract);
    success.put("caseId",caseId);
    return success;
}

public Map<String, Object> openContractInfo()
{
    String caseId = context.get("caseId");
    String groupName = context.get("groupName");
    List contracts = new ArrayList();
    //1.获取所有合同
    List<GenericValue> caseContracts = EntityQuery.use(delegator).from("TblCaseRelatedContract").where("caseId",caseId).queryList();
    for(GenericValue caseContract:caseContracts)
    {
        String contractId = caseContract.get("contractId");
        GenericValue contract = EntityQuery.use(delegator).from("TblContract").where("contractId",contractId).queryOne();
        if(contract!=null)
        {
            String secondPartyName = contract.get("secondPartyName");
            if(groupName.equals(secondPartyName))
            {
                contracts.add(contract);
            }
        }
    }
    Map success = ServiceUtil.returnSuccess();
    success.put("contracts",contracts);
    return success;
}

/**
 * 获取case的概述
 * @return
 */
public Map<String, Object> openCaseSummary(){
    Map success = ServiceUtil.returnSuccess();
    String caseId = context.get("caseId");
    GenericValue tblCase = EntityQuery.use(delegator).from("TblCase").where("caseId",caseId).queryOne();
    if(tblCase!=null) {
        String summary = tblCase.get("summary");
        success.put("summary",summary);
    }
    return success;
}
