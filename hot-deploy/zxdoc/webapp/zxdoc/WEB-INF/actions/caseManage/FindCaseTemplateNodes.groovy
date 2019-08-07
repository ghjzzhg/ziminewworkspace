import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
String blankCaseSessionKey = parameters.blankCaseSessionKey;
boolean isFromCase = Boolean.parseBoolean(parameters.isFromCase);
if(UtilValidate.isNotEmpty(blankCaseSessionKey)){
    Map<String, Object> blankCaseData = request.getSession().getAttribute(blankCaseSessionKey);
    context.caseName = blankCaseData.get("case").get("title");
    context.isFromCase = "true";
    context.isEmptyFlag = "true";
    List<GenericValue> groupList = blankCaseData.get("nodes");
    Map<String, List<GenericValue>> nodeTasks = blankCaseData.get("nodeTasks");//每个大步骤的小任务集合
    if(nodeTasks == null){
        nodeTasks = new HashMap<>();
        blankCaseData.put("nodeTasks", nodeTasks);
    }
    Map<String, List<GenericValue>> progressAttas = blankCaseData.get("progressAttas");//每个小任务的附件集合
    if(progressAttas == null){
        progressAttas = new HashMap<>();
        blankCaseData.put("progressAttas", progressAttas);
    }
    Map<String,Object> map = new HashMap<>();
    for(GenericValue group : groupList){
        List<GenericValue> progresses = nodeTasks.get(group.get("id"));
        List<Map<String, Object>> groupMap = new ArrayList<>();
        if (UtilValidate.isNotEmpty(progresses)) {
            for(GenericValue progress : progresses){
                Map<String, Object> groupInfoMap = new HashMap<>();
                groupInfoMap.putAll(progress);
                groupInfoMap.put("name",groupInfoMap.get("title"));
                String dueBase = groupInfoMap.get("dueBase");
                if(UtilValidate.isNotEmpty(dueBase) && dueBase.indexOf("BT") < 0){
                    String dueTask = groupInfoMap.get("dueTask");
                    List<GenericValue> dueTasks = nodeTasks.get(dueBase);
                    for (GenericValue o : dueTasks) {
                        if(Integer.parseInt(dueTask).equals(o.get("progressIndex"))){
                            groupInfoMap.put("dueBase", o.get("id"));
                        }
                    }
                }
                List list = new ArrayList();
                list.add(EntityCondition.makeCondition("dataResourceId",EntityOperator.NOT_EQUAL,"on"));
                list.add(EntityCondition.makeCondition("progressId",EntityOperator.EQUALS, progress.get("id")))

                List<GenericValue> nodeFiles = progressAttas.get(progress.get("id"));
                List<String> dsIds = new ArrayList<>();
                for (GenericValue node  : nodeFiles) {
                    dsIds.add(node.getString("dataResourceId"));
                }
                groupInfoMap.put("nodeFiles", from("DataResource").where(EntityCondition.makeCondition("dataResourceId", EntityOperator.IN, dsIds)).queryList());
                //当前节点的序号
                Integer nodeSeq = Integer.parseInt(groupInfoMap.get("progressIndex").toString());
                //当前节点的父节点
                String groupId = groupInfoMap.get("caseProgressGroupId").toString();
                Integer groupSeq = Integer.parseInt(group.getString("seq"));
                String caseId = group.get("caseId");
                List<Map<String, Object>> nodeMapList = new ArrayList<>();
                //拿上级的任务
                List<GenericValue> caseGroupList = new ArrayList<>();
                for (GenericValue otherGroup : groupList) {
                    if(otherGroup.getInteger("seq") <= groupSeq){
                        caseGroupList.add(otherGroup);
                    }
                }
                //上级父节点的信息
                for(GenericValue prevGroup : caseGroupList){
                    List<GenericValue> nodeList = new ArrayList<>();
                    List<GenericValue> prevGroupProgresses = nodeTasks.get(prevGroup.get("id"));
                    for (GenericValue prevGroupProgress : prevGroupProgresses) {
                        if(!prevGroup.get("id").equals(groupId) || (prevGroup.get("id").equals(groupId) && prevGroupProgress.getInteger("progressIndex") < nodeSeq)){
                            nodeList.add(prevGroupProgress);
                        }
                    }
                    Collections.sort(nodeList, new Comparator<GenericValue>() {
                        @Override
                        int compare(GenericValue o1, GenericValue o2) {
                            return o1.getInteger("progressIndex").compareTo(o2.getInteger("progressIndex"));
                        }
                    })
                    if(UtilValidate.isNotEmpty(nodeList)){
                        Map<String, Object> nodeMap = new HashMap<>();
                        nodeMap.put("id", prevGroup.get("id"));
                        nodeMap.put("name", prevGroup.get("name"))
                        nodeMap.put("flag", true)
                        nodeMapList.add(nodeMap);
                        for(GenericValue node : nodeList){
                            nodeMap = new HashMap<>();
                            nodeMap.put("id", node.getString("id"));
                            nodeMap.put("name",node.getString("title"))
                            nodeMap.put("flag", false)
                            nodeMapList.add(nodeMap);
                        }
                    }
                    groupInfoMap.put("nodeMapList", nodeMapList);
                }
                groupMap.add(groupInfoMap);
            }
        }
        map.put(group.getString("id"),groupMap);
    }
    context.groups = groupList;
    context.nodesMap = map;

    List<GenericValue> caseRoles = blankCaseData.get("caseParties");
    List<Map> roleType = new ArrayList<>();
    if(caseRoles!=null)
    {
        for (GenericValue caseRole:caseRoles)
        {
            Map roleMap = new HashMap();
            String type = caseRole.get("roleTypeId");
            //changed by galaxypan@2017-08-03:合伙人参与CASE时直接以挂靠单位的role参与，不再独立处理合伙人角色
            /*if(type.equals("CASE_ROLE_PARTNER"))
            {
                String partyId = caseRole.get("partyId");
                type = EntityQuery.use(delegator).from("PartyRole").where(EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%"),EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)).queryOne().get("roleTypeId");
                if(type.equals("CASE_ROLE_PARTNER")){
                    type = EntityQuery.use(delegator).from("TblPartnerType").where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)).queryOne().get("roleTypeId");
                }
            }*/
            String description = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",type).queryOne().get("description");
            roleMap.put("roleTypeId",type);
            roleMap.put("description",description);
            roleType.add(roleMap);
        }
        context.roleTypeList = roleType;
    }
    String partyId = context.get("userLogin").get("partyId");
    GenericValue user = EntityQuery.use(delegator).from("Party").where("partyId",partyId).queryOne();
    String accountType = user.get("partyTypeId");
    if(accountType.equals("PERSON"))
    {
        partyId = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT").queryOne().get("partyIdFrom");
    }
    String caseOwnerPartyId = "";
    for (GenericValue caseParty : caseRoles) {
        if("CASE_ROLE_OWNER".equals(caseParty.getString("roleTypeId"))){
            caseOwnerPartyId = caseParty.getString("partyId");
        }
    }
    if(partyId.equals(caseOwnerPartyId))
    {
        context.msg = "Y";
    }
}else if(isFromCase){
    String templateId = delegator.findOne("TblCase", UtilMisc.toMap("caseId", parameters.caseId), false).get("caseTemplate");
    context.caseName = parameters.caseName;
    List<GenericValue> groupList = from("TblCaseProgressGroup").where("caseId", parameters.caseId).cache().orderBy("seq").queryList();
    Map<String,Object> map = new HashMap<>();
    for(GenericValue group : groupList){
        List<GenericValue> groupInfo = EntityQuery.use(delegator).select().from("TblCaseProgress").where(UtilMisc.toMap("caseId",parameters.caseId,"caseProgressGroupId",group.get("id"))).queryList();
        List<Map<String, Object>> groupMap = new ArrayList<>();
        for(GenericValue genericValue : groupInfo){
            Map<String, Object> groupInfoMap = new HashMap<>();
            groupInfoMap.putAll(genericValue);
            groupInfoMap.put("name",groupInfoMap.get("title"));
            String dueBase = groupInfoMap.get("dueBase");
            if(UtilValidate.isNotEmpty(dueBase) && dueBase.indexOf("BT") < 0){
                String dueTask = groupInfoMap.get("dueTask");
                GenericValue casepro = EntityQuery.use(delegator).select().from("TblCaseProgress").where(UtilMisc.toMap("caseId",parameters.caseId,"caseProgressGroupId",dueBase, "progressIndex", Integer.parseInt(dueTask))).queryOne();
                groupInfoMap.put("dueBase", casepro.get("id"));
            }
            List list = new ArrayList();
            list.add(EntityCondition.makeCondition("dataResourceId",EntityOperator.NOT_EQUAL,"on"));
            list.add(EntityCondition.makeCondition("progressId",EntityOperator.EQUALS, genericValue.get("id")))

            List<GenericValue> nodeFiles = from("TblCaseProgressAtta").where(list).cache().queryList();
            List<String> dsIds = new ArrayList<>();
            for (GenericValue node  : nodeFiles) {
                dsIds.add(node.getString("dataResourceId"));
            }
            groupInfoMap.put("nodeFiles", from("DataResource").where(EntityCondition.makeCondition("dataResourceId", EntityOperator.IN, dsIds)).queryList());
            //当前节点的序号
            Integer nodeSeq = Integer.parseInt(groupInfoMap.get("progressIndex").toString());
            //当前节点的父节点
            String groupId = groupInfoMap.get("caseProgressGroupId").toString();
            GenericValue groupNode = from("TblCaseProgressGroup").where("id", groupId).cache().queryOne();
            Integer groupSeq = Integer.parseInt(groupNode.getString("seq"));
            String caseId = groupNode.get("caseId");
            List<Map<String, Object>> nodeMapList = new ArrayList<>();
            List listc = new ArrayList();
            listc.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId))
            listc.add(EntityCondition.makeCondition("seq", EntityOperator.LESS_THAN_EQUAL_TO, groupSeq))
            //拿上级的任务
            List<GenericValue> caseGroupList = from("TblCaseProgressGroup").where(listc).cache().queryList();
            //上级父节点的信息
            for(GenericValue genericValue1 : caseGroupList){
                listc.clear();
                listc.add(EntityCondition.makeCondition("caseId", EntityOperator.EQUALS, caseId))
                listc.add(EntityCondition.makeCondition("caseProgressGroupId", EntityOperator.EQUALS, genericValue1.get("id")))
                if(genericValue1.get("id").equals(groupId)){
                    listc.add(EntityCondition.makeCondition("progressIndex", EntityOperator.LESS_THAN, nodeSeq))
                }
                List<GenericValue> nodeList = from("TblCaseProgress").where(EntityCondition.makeCondition(listc)).cache().orderBy("progressIndex").queryList();
                if(UtilValidate.isNotEmpty(nodeList)){
                    Map<String, Object> nodeMap = new HashMap<>();
                    nodeMap.put("id", genericValue1.get("id"));
                    nodeMap.put("name", genericValue1.get("name"))
                    nodeMap.put("flag", true)
                    nodeMapList.add(nodeMap);
                    for(GenericValue node : nodeList){
                        nodeMap = new HashMap<>();
                        nodeMap.put("id", node.getString("id"));
                        nodeMap.put("name",node.getString("title"))
                        nodeMap.put("flag", false)
                        nodeMapList.add(nodeMap);
                    }
                }
                groupInfoMap.put("nodeMapList", nodeMapList);
            }

            groupMap.add(groupInfoMap);


        }
        map.put(group.getString("id"),groupMap);
    }
    context.groups = groupList;
    context.nodesMap = map;
    context.isFromCase = "true";
    context.isEmptyFlag = "true";
    context.caseId = parameters.caseId;
    List<GenericValue> caseRoles = from("TblCaseParty").where(UtilMisc.toMap("caseId", parameters.caseId)).queryList();
    List<Map> roleType = new ArrayList<>();
    if(caseRoles!=null)
    {
        for (GenericValue caseRole:caseRoles)
        {
            Map roleMap = new HashMap();
            String type = caseRole.get("roleTypeId");
            //changed by galaxypan@2017-08-03:合伙人参与CASE时直接以挂靠单位的role参与，不再独立处理合伙人角色
            /*if(type.equals("CASE_ROLE_PARTNER"))
            {
                String partyId = caseRole.get("partyId");
                type = EntityQuery.use(delegator).from("PartyRole").where(EntityCondition.makeCondition("roleTypeId",EntityOperator.LIKE,"CASE_ROLE_%"),EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)).queryOne().get("roleTypeId");
                if(type.equals("CASE_ROLE_PARTNER")){
                    type = EntityQuery.use(delegator).from("TblPartnerType").where(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)).queryOne().get("roleTypeId");
                }
            }*/
            String description = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",type).queryOne().get("description");
            roleMap.put("roleTypeId",type);
            roleMap.put("description",description);
            roleType.add(roleMap);
        }
        context.roleTypeList = roleType;
    }
    String partyId = context.get("userLogin").get("partyId");
    GenericValue user = EntityQuery.use(delegator).from("Party").where("partyId",partyId).queryOne();
    String accountType = user.get("partyTypeId");
    if(accountType.equals("PERSON"))
    {
        partyId = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT").queryOne().get("partyIdFrom");
    }
    GenericValue caseMember = EntityQuery.use(delegator).from("casePartyMembers").where("caseId",parameters.caseId,"roleTypeId","CASE_PERSON_ROLE_MANAGER","groupRoleTypeId","CASE_ROLE_OWNER").queryOne();
    String groupId = caseMember.get("groupPartyId");
    if(partyId.equals(groupId))
    {
        context.msg = "Y";
    }
}else {
    context.template = from("TblCaseTemplate").where("id", parameters.templateId).cache().queryOne();
    context.groups = from("TblCaseTemplateNodeGroup").where("templateId", parameters.templateId).cache().orderBy("seq").queryList();
    context.baseTimes = from("TblCaseTemplateBaseTime").where("templateId", parameters.templateId).cache().orderBy("seq").queryList();
    List<GenericValue> nodes = from("TblCaseTemplateNode").where(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, parameters.templateId), EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, null)).cache().orderBy("seq").queryList();
    List<Map<String, Object>> mapList = new ArrayList<>();
    for(GenericValue genericValue : nodes){
        Map<String , Object> map = new HashMap<>();
        map.putAll(genericValue);
        //当前节点的序号
        Integer nodeSeq = Integer.parseInt(map.get("seq").toString());
        //当前节点的父节点
        String groupId = map.get("groupId").toString()
        GenericValue groupNode = from("TblCaseTemplateNodeGroup").where("id", groupId).cache().queryOne();
        Integer groupSeq = Integer.parseInt(groupNode.getString("seq"));
        String templateId = groupNode.get("templateId");
        List<Map<String, Object>> nodeMapList = new ArrayList<>();
        List list = new ArrayList();
        list.add(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId))
        list.add(EntityCondition.makeCondition("seq", EntityOperator.LESS_THAN_EQUAL_TO, groupSeq))
        //拿上级的任务
        List<GenericValue> groupList = from("TblCaseTemplateNodeGroup").where(list).cache().queryList();
        //上级父节点的信息
        for(GenericValue genericValue1 : groupList){
            list.clear();
            list.add(EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId))
            list.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, genericValue1.get("id")))
            if(genericValue1.get("id").equals(groupId)){
                list.add(EntityCondition.makeCondition("seq", EntityOperator.LESS_THAN, nodeSeq))
            }
            List<GenericValue> nodeList = from("TblCaseTemplateNode").where(EntityCondition.makeCondition(list)).cache().orderBy("seq").queryList();
            if(UtilValidate.isNotEmpty(nodeList)){
                Map<String, Object> nodeMap = new HashMap<>();
                nodeMap.put("id", genericValue1.get("id"));
                nodeMap.put("name", genericValue1.get("name"))
                nodeMap.put("flag", true)
                nodeMapList.add(nodeMap);
                for(GenericValue node : nodeList){
                    nodeMap = new HashMap<>();
                    nodeMap.put("id", node.getString("id"));
                    nodeMap.put("name",node.getString("name"))
                    nodeMap.put("flag", false)
                    nodeMapList.add(nodeMap);
                }
            }
            map.put("nodeMapList", nodeMapList);
        }
        List<GenericValue> nodeFiles = from("TblCaseTemplateNodeAtta").where("templateNodeId", genericValue.get("id")).cache().queryList();
        List<String> dsIds = new ArrayList<>();
        for (GenericValue node  : nodeFiles) {
            dsIds.add(node.getString("dataResourceId"));
        }
        map.put("nodeFiles", from("DataResource").where(EntityCondition.makeCondition("dataResourceId", EntityOperator.IN, dsIds)).queryList());
        mapList.add(map);
    }
    context.nodesMap = mapList.groupBy {x -> x.get("groupId")};
    context.isFromCase = "false";
    context.isEmptyFlag = "true";
    //参与方
    GenericValue caseTemplate = from("TblCaseTemplate").where("id", parameters.templateId).cache().queryOne();
    String roles = caseTemplate==null?"":caseTemplate.get("roles");
    List role = new ArrayList();
    if(roles!=null && roles!="")
    {
        if(roles.substring(0,1)=="[") {
            String roleMember = roles.substring(1, roles.length() - 1);
            role = roleMember.split(",");
        }else
        {
            role.add(roles);
        }
    }
    List list = new ArrayList();
    for (int i = 0; i < role.size();i++)
    {
        Map map = new HashMap();
        String roleTypeId = role.get(i);
        //处理非第一个元素会首字符为空格的情况
        if(i!=0)
        {
            roleTypeId = roleTypeId.substring(1,roleTypeId.length());
        }
        GenericValue roleType = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",roleTypeId,"parentTypeId","CASE_ROLE").queryOne();
        String description = roleType.get("description");
        map.put("roleTypeId",roleTypeId);
        map.put("description",description);
        list.add(map);
    }
    context.roleTypeList = list;
}