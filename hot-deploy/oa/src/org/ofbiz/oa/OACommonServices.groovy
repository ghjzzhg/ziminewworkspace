import javolution.util.FastList
import javolution.util.FastMap
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.StringUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.service.ServiceUtil

public Map<String, Object> getNextSequenceId(){
    String entityName = context.get("entityName");
    String prefix = context.get("prefix");
    prefix = StringUtils.stripToEmpty(prefix);
    String staggerMax = context.get("staggerMax")

    Map success = ServiceUtil.returnSuccess();
    String seq = prefix + delegator.getNextSeqId(entityName, staggerMax);
    success.put("seq", seq);
    return success;
}

public Map<String, Object> saveDataScope(){
    Map success = ServiceUtil.returnSuccess();
    String entityName = context.get("entityName");
    String dataId = context.get("dataId");
    String dataAttr = context.get("dataAttr");

    String deptOnly = context.get("deptOnly");
    String deptLike = context.get("deptLike");

    String levelOnly = context.get("levelOnly");
    String levelLike = context.get("levelLike");

    String positionOnly = context.get("positionOnly");
    String positionLike = context.get("positionLike");

    String user = context.get("userValue");

    //删除原有的数据范围
    Map<String, String> deleteMap = UtilMisc.toMap("entityName", entityName, "dataId", dataId);
    if(UtilValidate.isNotEmpty(dataAttr)){
        deleteMap.put("dataAttr", dataAttr);
    }
    delegator.removeByAnd("TblDataScope", deleteMap);

    if (UtilValidate.isNotEmpty(deptOnly) || UtilValidate.isNotEmpty(deptLike)
            || UtilValidate.isNotEmpty(levelOnly) || UtilValidate.isNotEmpty(levelLike)
            || UtilValidate.isNotEmpty(positionOnly) || UtilValidate.isNotEmpty(positionLike)
            || UtilValidate.isNotEmpty(user)) {
        List<GenericValue> scopeValues = FastList.newInstance();
        Map scopeData = UtilMisc.toMap("entityName", entityName, "dataId", dataId, "dataAttr", dataAttr);
        if(UtilValidate.isNotEmpty(deptOnly)){
            scopeData.put("scopeType", "SCOPE_DEPT_ONLY");
            String[] deptIds = deptOnly.split(",");
            for (String deptId: deptIds) {
                scopeData.put("scopeId", delegator.getNextSeqId("TblDataScope"));
                scopeData.put("scopeValue", deptId);
                scopeValues.add(delegator.makeValidValue("TblDataScope", scopeData));
            }
        }
        if(UtilValidate.isNotEmpty(deptLike)){
            scopeData.put("scopeType", "SCOPE_DEPT_LIKE");
            String[] deptIds = deptLike.split(",");
            for (String deptId: deptIds) {
                scopeData.put("scopeId", delegator.getNextSeqId("TblDataScope"));
                scopeData.put("scopeValue", deptId);
                scopeValues.add(delegator.makeValidValue("TblDataScope", scopeData));
            }
        }
        if(UtilValidate.isNotEmpty(levelOnly)){
            scopeData.put("scopeType", "SCOPE_LEVEL_ONLY");
            String[] levelIds = levelOnly.split(",");
            for (String levelId: levelIds) {
                scopeData.put("scopeId", delegator.getNextSeqId("TblDataScope"));
                scopeData.put("scopeValue", levelId);
                scopeValues.add(delegator.makeValidValue("TblDataScope", scopeData));
            }
        }
        if(UtilValidate.isNotEmpty(levelLike)){
            scopeData.put("scopeType", "SCOPE_LEVEL_LIKE");
            String[] levelIds = levelLike.split(",");
            for (String levelId: levelIds) {
                scopeData.put("scopeId", delegator.getNextSeqId("TblDataScope"));
                scopeData.put("scopeValue", levelId);
                scopeValues.add(delegator.makeValidValue("TblDataScope", scopeValue));
            }
        }
        if(UtilValidate.isNotEmpty(positionOnly)){
            scopeData.put("scopeType", "SCOPE_POSITION_ONLY");
            String[] positionIds = positionOnly.split(",");
            for (String positionId: positionIds) {
                scopeData.put("scopeId", delegator.getNextSeqId("TblDataScope"));
                scopeData.put("scopeValue", positionId);
                scopeValues.add(delegator.makeValidValue("TblDataScope", scopeData));
            }
        }
        if(UtilValidate.isNotEmpty(positionLike)){
            scopeData.put("scopeType", "SCOPE_POSITION_LIKE");
            String[] positionIds = positionLike.split(",");
            for (String positionId: positionIds) {
                scopeData.put("scopeId", delegator.getNextSeqId("TblDataScope"));
                scopeData.put("scopeValue", positionId);
                scopeValues.add(delegator.makeValidValue("TblDataScope", scopeData));
            }
        }
        if(UtilValidate.isNotEmpty(user)){
            scopeData.put("scopeType", "SCOPE_USER");
            String[] users = user.split(",");
            for (String userId: users) {
                scopeData.put("scopeId", delegator.getNextSeqId("TblDataScope"));
                scopeData.put("scopeValue", userId);
                scopeValues.add(delegator.makeValidValue("TblDataScope", scopeData));
            }
        }

        delegator.storeAll(scopeValues);
    }


    return success;
}

public Map<String, Object> getDataScope() {
    Map success = ServiceUtil.returnSuccess();
    String entityName = context.get("entityName");
    String dataId = context.get("dataId");
    String dataAttr = context.get("dataAttr");
    List<String> deptOnly = FastList.newInstance();
    List<String> deptLike = FastList.newInstance();
    List<String> levelOnly = FastList.newInstance();
    List<String> levelLike = FastList.newInstance();
    List<String> positionOnly = FastList.newInstance();
    List<String> positionLike = FastList.newInstance();
    List<String> user = FastList.newInstance();
    List<Map> description = FastList.newInstance();

    Map conditionMap = UtilMisc.toMap("dataId", dataId, "entityName", entityName);
    if(UtilValidate.isNotEmpty(dataAttr)){
        conditionMap.put("dataAttr", dataAttr);
    }
    List<GenericValue> scopes = from("TblDataScope").where(conditionMap).queryList();
    if(UtilValidate.isNotEmpty(scopes)){
        for (GenericValue scope : scopes) {
            String scopeType = scope.get("scopeType");
            String scopeValue = scope.get("scopeValue");
            Map nameMap = FastMap.newInstance();
            description.add(nameMap);
            switch (scopeType){
                case "SCOPE_DEPT_ONLY":
                    deptOnly.add(scopeValue);
                    nameMap.put("name", delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", scopeValue), true).get("groupName"));
                    break;
                case "SCOPE_DEPT_LIKE":
                    deptLike.add(scopeValue);
                    nameMap.put("name", delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", scopeValue), true).get("groupName"));
                    nameMap.put("like", delegator.findOne("Enumeration", UtilMisc.toMap("enumId", "SCOPE_DEPT_LIKE"), true).get("description"));
                    break;
                case "SCOPE_LEVEL_ONLY":
                    levelOnly.add(scopeValue);
                    nameMap.put("name", delegator.findOne("Enumeration", UtilMisc.toMap("enumId", scopeValue), true).get("description"));
                    break;
                case "SCOPE_LEVEL_LIKE":
                    levelLike.add(scopeValue);
                    nameMap.put("name", delegator.findOne("Enumeration", UtilMisc.toMap("enumId", scopeValue), true).get("description"));
                    nameMap.put("like", delegator.findOne("Enumeration", UtilMisc.toMap("enumId", "SCOPE_LEVEL_LIKE"), true).get("description"));
                    break;
                case "SCOPE_POSITION_ONLY":
                    positionOnly.add(scopeValue);
                    nameMap.put("name", delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", scopeValue), true).get("description"));
                    break;
                case "SCOPE_POSITION_LIKE":
                    positionLike.add(scopeValue);
                    nameMap.put("name", delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", scopeValue), true).get("description"));
                    nameMap.put("like", delegator.findOne("Enumeration", UtilMisc.toMap("enumId", "SCOPE_POSITION_LIKE"), true).get("description"));
                    break;
                case "SCOPE_USER":
                    user.add(scopeValue);
                    GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", scopeValue), true)
                    nameMap.put("name", person.get("fullName"));
                    break;
                default:break;
            }
        }
        Map valueMap = FastMap.newInstance();
        valueMap.put("deptOnly", StringUtils.stripToEmpty(StringUtil.join(deptOnly, ",")));
        valueMap.put("deptLike", StringUtils.stripToEmpty(StringUtil.join(deptLike, ",")));
        valueMap.put("levelOnly", StringUtils.stripToEmpty(StringUtil.join(levelOnly, ",")));
        valueMap.put("levelLike", StringUtils.stripToEmpty(StringUtil.join(levelLike, ",")));
        valueMap.put("positionOnly", StringUtils.stripToEmpty(StringUtil.join(positionOnly, ",")));
        valueMap.put("positionLike", StringUtils.stripToEmpty(StringUtil.join(positionLike, ",")));
        valueMap.put("user", StringUtils.stripToEmpty(StringUtil.join(user, ",")));
        valueMap.put("entityName", entityName);
        success.put("value", valueMap);
        success.put("description", description);
    }
    return success
}


public Map<String, Object> getStaffNames() {
    Map success = ServiceUtil.returnSuccess();
    List<String> staffNames = FastList.newInstance();
    String partyIdStr = context.get("partyIds");
    if(UtilValidate.isNotEmpty(partyIdStr)){
        String[] partyIds = partyIdStr.split(",");
        List partyIdList = UtilMisc.toListArray(partyIds);
        List<GenericValue> persons = delegator.findList("Person", EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdList), UtilMisc.toSet("fullName", "firstName", "lastName"), null, null, true);
        if(UtilValidate.isNotEmpty(persons)){
            for (GenericValue person : persons) {
                String fullName = person.get("fullName");
                if(UtilValidate.isEmpty(fullName)){
                    fullName = person.get("lastName") + person.get("firstName");
                }
                staffNames.add(fullName);
            }
        }
    }
    success.put("nameList", staffNames);
    return success
}

public Map<String,Object> searchUniqueNumber(){
    Map success = ServiceUtil.returnSuccess();
    String entityName = context.get("entityName").toString();
    String numName = context.get("numName").toString();
    String prefix = context.get("prefix");
    String prefixion = EntityUtilProperties.getPropertyValue("oa.properties", prefix, "localhost", delegator);
    String number;
    int numLeng = 3;
    List<GenericValue> entityList = EntityQuery.use(delegator).select(numName).from(entityName).orderBy(numName + " DESC").queryList();
            if(UtilValidate.isNotEmpty(entityList)){
        GenericValue entity = entityList.get(0);
        String num = entity.get(numName).toString();
        if(num.indexOf(prefixion) >= 0){
            num = num.substring(prefixion.length(),num.length());
        }
        if(num.indexOf("localhost") >= 0){
            num = num.substring("localhost".length(),num.length());
        }
        number = (Integer.parseInt(num) + 1).toString();
        if(number.length() >= numLeng){
            numLeng += number.length() - numLeng;
        }
        while (number.length() < numLeng){
            number = "0" + number;
        }
    }else{
        number = "001";
    }
    success.put("number", prefixion + number);
    return success;
}

/**
 * 查找用户可查看的资源ID
 * @return
 */
public Map<String,Object> verifyViewPermissions(){
    Map success = ServiceUtil.returnSuccess();
    String entityName = context.get("entityName");
    String partyId = context.get("partyId");
    String dataenId = context.get("entityId");
    Boolean isSelect = (Boolean)context.get("isSelect");
    Map con = new HashMap();
    con.put("entityName", entityName);
    if(null != dataenId && dataenId.length() > 0){
        con.put("dataId",dataenId);
    }
    //根据实体名查找资源列表,dataId分组
    List<GenericValue> scopes = from("searchDataScopeGroupByDataId").where(con).queryList();
    List<Map<String,Object>> entityDataList = new ArrayList<Map<String,Object>>();
    //循环列表
    for(GenericValue dataScope : scopes){
        String dataId = dataScope.get("dataId");
        Map conditionMap = UtilMisc.toMap("dataId", dataId, "entityName", entityName);
        //根据dataID和entityName查找列表
        List<GenericValue> dataScopeList = from("TblDataScope").where(conditionMap).queryList();
        List<Map<String,Object>> dataIdList = new ArrayList<Map<String,Object>>();
        if(UtilValidate.isNotEmpty(dataScopeList)){
            //循环查找到的权限列表，区分用户权限
            for(GenericValue scope : dataScopeList){
                String scopeType = scope.get("scopeType");
                String scopeValue = scope.get("scopeValue");
                Map<String,Object> nameMap = new HashMap<String,Object>();
                List partyIdList = new ArrayList();
                List partyNameList = new ArrayList();
                List partyList = new ArrayList();
                String selectEntityName = "";
                String condition = "";
                //区分部门和岗位及职级之间查询的entityName和条件列
                if(scopeType.equals("SCOPE_DEPT_ONLY") || scopeType.equals("SCOPE_DEPT_LIKE")){
                    selectEntityName = "PersonByGroupId";
                    condition = "department";
                }else if(scopeType.equals("SCOPE_LEVEL_ONLY") || scopeType.equals("SCOPE_LEVEL_LIKE") || scopeType.equals("SCOPE_POSITION_ONLY") || scopeType.equals("SCOPE_POSITION_LIKE")){
                    selectEntityName = "PersonByRoleId";
                    condition = "roleTypeId";
                }

                if(!scopeType.equals("SCOPE_USER")){
                    List<GenericValue> entityList = EntityQuery.use(delegator).select().from(selectEntityName).where(UtilMisc.toMap(condition,scope.get("scopeValue"))).queryList();
                    for(GenericValue entity : entityList){
                        partyNameList.add(entity.get("fullName"));
                        partyIdList.add(entity.get("partyId"));
                        partyList.add(entity);
                    }
                    //查询下属情况
                    if(scopeType.indexOf("_LIKE") >= 0){
                        List<GenericValue> addList = new ArrayList<GenericValue>();
                        List<GenericValue> circulateList = new ArrayList<GenericValue>();
                        List<GenericValue> list;
                        if(scopeType.equals("SCOPE_DEPT_ONLY") || scopeType.equals("SCOPE_DEPT_LIKE")){
                            list = searchGroupBranch(addList,scopeValue,circulateList);
                        }else{
                            list = searchPositionBranch(addList,scopeValue,circulateList);
                        }
                        if(null != list){
                            for(GenericValue group : list){
                                List<GenericValue> entitys = EntityQuery.use(delegator).select().from(selectEntityName).where(UtilMisc.toMap(condition,group.get("partyIdTo"))).queryList();
                                for(GenericValue entity : entitys){
                                    partyNameList.add(entity.get("fullName"));
                                    partyIdList.add(entity.get("partyId"));
                                    partyList.add(entity)
                                }
                            }
                        }
                    }
                }else{
                    //如果为SCOPE_USER则单独查询
                    GenericValue genericValue = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",scope.get("scopeValue"))).queryOne();
                    partyNameList.add(genericValue.get("fullName"));
                    partyIdList.add(genericValue.get("partyId"));
                    partyList.add(genericValue)
                }
                if(partyIdList.size() > 0){
                    nameMap.put("partyIdList",partyIdList);
                    nameMap.put("partyNameList",partyNameList);
                    nameMap.put("partyList",partyList);
                    dataIdList.add(nameMap);
                }
            }
            for(Map<String,Object> map : dataIdList){
                List<String> partyIdList = (List<String>)map.get("partyIdList");
                //区分是查询所有人还是查询有权限的人员
                if(isSelect){
                    Map<String,Object> dataMap = new HashMap<String,Object>();
                    dataMap.put("partyIdList",map.get("partyIdList"));
                    dataMap.put("partyNameList",map.get("partyNameList"));
                    dataMap.put("partyList",map.get("partyList"));
                    dataMap.put("dataId",dataId);
                    entityDataList.add(dataMap);
                }else{
                    if(partyIdList.contains(partyId)){
                        Map<String,Object> dataMap = new HashMap<String,Object>();
                        dataMap.put("partyIdList",map.get("partyIdList"));
                        dataMap.put("partyNameList",map.get("partyNameList"));
                        dataMap.put("partyList",map.get("partyList"));
                        dataMap.put("dataId",dataId);
                        entityDataList.add(dataMap);
                    }
                }
            }
        }
    }
    Map<String ,Object> dataMap = new HashMap<String ,Object>()
    dataMap.put("entityDataList",entityDataList);
    success.put("data", dataMap);
    return success;
}

/**
 * 递归查询下属部门
 * @param addGroupList
 * @param groupId
 * @param circulateGroupList
 * @return
 */
public List<GenericValue> searchGroupBranch(List<GenericValue> addGroupList,String groupId,List<GenericValue> circulateGroupList){
    List<GenericValue> circulateGroups = from("PartyRelationship").where("partyIdFrom", groupId, "partyRelationshipTypeId", "GROUP_ROLLUP","roleTypeIdFrom","_NA_","roleTypeIdTo","_NA_").cache().filterByDate().queryList();
    circulateGroupList.addAll(circulateGroups);
    if(null != circulateGroupList && circulateGroupList.size() > 0){
        for(int i = 0; i<circulateGroupList.size(); i++){
            GenericValue partyRelationship = circulateGroupList.get(i);
            groupId = partyRelationship.get("partyIdTo");
            addGroupList.add(partyRelationship);
            List<GenericValue> addpartyRelationshipList = from("PartyRelationship").where("partyIdFrom",groupId , "partyRelationshipTypeId", "GROUP_ROLLUP","roleTypeIdFrom","_NA_","roleTypeIdTo","_NA_").cache().filterByDate().queryList();
            circulateGroupList.remove(partyRelationship);
            if(circulateGroupList.size() <= 0){
                break;
            }
            circulateGroupList.addAll(addpartyRelationshipList);
            searchGroupBranch(addGroupList,groupId,circulateGroupList);
        }
    }
    return addGroupList;
}

/**
 * 递归查询岗位或者职级
 * @param addPositionList
 * @param positionId
 * @param circulatePositionList
 * @return
 */
public List<GenericValue> searchPositionBranch(List<GenericValue> addPositionList,String positionId,List<GenericValue> circulatePositionList){
    positionCondition = [EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, "Company"),
                         EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, positionId),
                         EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "MASTER_POSITION")]
    List<GenericValue> parentPosition = select("partyIdFrom", "roleTypeIdFrom").from("PartyRelationship").where(positionCondition).cache().queryList();
    circulatePositionList.addAll(parentPosition);

    if(null != circulatePositionList && circulatePositionList.size() > 0){
        for(int i = 0; i<circulatePositionList.size(); i++){
            GenericValue partyRelationship = circulatePositionList.get(i);
            groupId = partyRelationship.get("partyIdTo");
            addPositionList.add(partyRelationship);
            List<GenericValue> addpartyRelationshipList = from("PartyRelationship").where("partyIdFrom",positionId , "partyRelationshipTypeId", "GROUP_ROLLUP","roleTypeIdFrom","_NA_","roleTypeIdTo","_NA_").cache().filterByDate().queryList();
            circulatePositionList.remove(partyRelationship);
            if(circulatePositionList.size() <= 0){
                break;
            }
            circulatePositionList.addAll(addpartyRelationshipList);
            searchPositionBranch(addPositionList,positionId,circulatePositionList);
        }
    }
    return addPositionList
}

public Map saveEnumeration(){
    Map success = ServiceUtil.returnSuccess();
    String enumTypeId = context.get("enumTypeId");
    String enumName = context.get("enumName");
    Map<String ,Object> dataMap = new HashMap<String ,Object>()
    GenericValue enumType = EntityQuery.use(delegator).select().from("EnumerationType").where(UtilMisc.toMap("enumTypeId",enumTypeId)).queryOne();
    if(UtilValidate.isNotEmpty(enumType)){
        List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId",enumTypeId)).orderBy("sequenceId DESC").queryList();
        String sequenceId = "01";
        if(UtilValidate.isNotEmpty(genericValueList)){
            GenericValue genericValue = genericValueList.get(0);
            String sequenceId1 = genericValue.get("sequenceId");
            if(UtilValidate.isNotEmpty(sequenceId1)){
                Integer num = Integer.parseInt(sequenceId1);
                sequenceId = ++num;
            }
        }
        String id = delegator.getNextSeqId("Enumeration").toString()
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("enumId",id);
        map.put("enumTypeId",enumTypeId);
        map.put("description",enumName);
        map.put("sequenceId",sequenceId);
        GenericValue genericValue = delegator.makeValidValue("Enumeration", map);
        dataMap.put("flag","1");
        dataMap.put("enumId",id);
        dataMap.put("description",enumName);
        genericValue.create();
    }else{
        dataMap.put("flag","0");
        dataMap.put("message","请联系管理员添加类别信息");
    }
    success.put("data", dataMap);
    return success;
}

enum chooserType {
    LookupDepartment, LookupOccupation, LookupStaff
}
public Map<String,Object> lookUpChooserInfo(){
    // todo 员工选择器
    Map success = ServiceUtil.returnSuccess();
    Map<String,Object> map = new HashMap<String,Object>();
    String chooserType = context.get("chooserType");
    String chooserId = context.get("chooserId");
    String masterPartyId;
    if(chooserId.indexOf(",") >= 0){
        String[] position = chooserId.split(",");
        masterPartyId = position[0];//一个岗位对应的partyRole
        chooserId = position[1];
    }
    String dropDown = context.get("dropDown");
    String dropDownType = context.get("dropDownType");
    String entityName;
    String condition;
    String value;
    if("LookupEmployee".equals(chooserType)){
        entityName = "Person";
        condition = "partyId";
        value = "fullName";
    }else if("LookupOccupation".equals(chooserType)){
        entityName = "RoleType";
        condition = "roleTypeId";
        value = "description";
    }else if("LookupDepartment".equals(chooserType)){
        entityName = "PartyGroup";
        condition = "partyId";
        value = "groupName";
    }else if("WorkNumAjax".equals(chooserType)){
        entityName = "TblListOfWork";
        condition = "listOfWorkId";
        value = "listOfWorkName";
    }else{
        map.put("flag",0);
        map.put("msg","能暂未开发！");
        success.put("data",map);
        return success;
    }
    GenericValue genericValue = EntityQuery.use(delegator).select().from(entityName).where(UtilMisc.toMap(condition,chooserId)).queryOne();
    if(UtilValidate.isNotEmpty(genericValue)){
        if("LookupOccupation".equals(chooserType) && UtilValidate.isNotEmpty(masterPartyId)){
            map.put("id", masterPartyId + "," + chooserId);
        }else {
            map.put("id",chooserId);
        }
        map.put("name",genericValue.get(value));
        if(UtilValidate.isNotEmpty(dropDown) && "true".equals(dropDown)){
            List<Map<String,Object>> genericValueList = new ArrayList<Map<String,Object>>();
            if("LookupEmployee".equals(dropDownType)){
                if("LookupDepartment".equals(chooserType)){
                    selectEntityName = "PersonByGroupId";
                    condition = "department";
                }else if("LookupOccupation".equals(chooserType)){
                    selectEntityName = "PersonByRoleId";
                    condition = "roleTypeId";
                }
                List<GenericValue> entityList = EntityQuery.use(delegator).select().from(selectEntityName).where(UtilMisc.toMap(condition,chooserId)).queryList();
                for(GenericValue entity : entityList){
                    Map<String,Object> persoMap = new HashMap<String,Object>()
                    persoMap.put("id",entity.get("partyId"));
                    persoMap.put("name",entity.get("fullName"));
                    genericValueList.add(persoMap);
                }
            }else if("LookupOccupation".equals(dropDownType) && "LookupOccupation".equals(chooserType)){
                 List positionCondition = [EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, masterPartyId),
                                     EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, chooserId),
                                     EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "MASTER_POSITION")]
                List<GenericValue> occupationList = select("partyIdTo", "roleTypeIdTo").from("PartyRelationship").where(positionCondition).cache().queryList();
                for(GenericValue genericValue1 : occupationList){
                    Map<String,Object> occupationMap = new HashMap<String,Object>();
                    GenericValue occupation = EntityQuery.use(delegator).select().from("RoleType").where(UtilMisc.toMap("roleTypeId",genericValue1.get("roleTypeIdTo"))).queryOne();
                    occupationMap.put("id",occupationMap.get("roleTypeId"));
                    occupationMap.put("name",occupation.get("description"));
                    genericValueList.add(occupationMap);
                }
            }else if("LookupDepartment".equals(dropDownType) && "LookupDepartment".equals(chooserType)){
                List<GenericValue> departmentList = from("PartyRelationship").where("partyIdFrom", chooserId, "partyRelationshipTypeId", "GROUP_ROLLUP","roleTypeIdFrom","_NA_","roleTypeIdTo","_NA_").cache().filterByDate().queryList();
                for(GenericValue genericValue1 : departmentList){
                    Map<String,Object> occupationMap = new HashMap<String,Object>();
                    GenericValue occupation = EntityQuery.use(delegator).select().from("PartyGroup").where(UtilMisc.toMap("partyId",genericValue1.get("partyIdTo"))).queryOne();
                    occupationMap.put("id",occupation.get("partyId"));
                    occupationMap.put("name",occupation.get("groupName"));
                    genericValueList.add(occupationMap);
                }
            }else if("LookupOccupation".equals(dropDownType) && "LookupDepartment".equals(chooserType)){
                List<GenericValue> occupationList = EntityQuery.use(delegator).select().from("DepartmentPositionView").where(UtilMisc.toMap("partyId",chooserId)).queryList();
                for(GenericValue genericValue1 : occupationList){
                    Map<String,Object> occupationMap = new HashMap<String,Object>();
                    occupationMap.put("id", chooserId + "," + genericValue1.get("roleTypeId"));
                    occupationMap.put("name", genericValue1.get("description"));
                    genericValueList.add(occupationMap);
                }
            }else if("LookupManager".equals(dropDownType) && "LookupDepartment".equals(chooserType)){
                List positionCondition = [EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, chooserId),
                                          EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"),
                                          EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "MANAGER")]
                List<GenericValue> managerList = EntityQuery.use(delegator).select().from("PartyRelationship").where(positionCondition).orderBy("fromDate DESC").queryList();
                GenericValue manager = managerList.get(0);
                GenericValue manager1 = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",manager.get("partyIdTo"))).queryOne();
                Map<String,Object> occupationMap = new HashMap<String,Object>();
                occupationMap.put("id", manager.get("partyIdTo"));
                occupationMap.put("name", manager1.get("fullName"));
                genericValueList.add(occupationMap);
            }else if("LookupManager".equals(dropDownType) && "LookupOccupation".equals(chooserType)){
                List positionCondition = [EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, masterPartyId),
                                          EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, chooserId),
                                          EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "MANAGER")]
                List<GenericValue> managerList = EntityQuery.use(delegator).select().from("PartyRelationship").where(positionCondition).orderBy("fromDate DESC").queryList();
                GenericValue manager = managerList.get(0);
                GenericValue manager1 = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("partyId",manager.get("partyIdTo"))).queryOne();
                Map<String,Object> occupationMap = new HashMap<String,Object>();
                occupationMap.put("id", manager.get("partyIdTo"));
                occupationMap.put("name", manager1.get("fullName"));
                genericValueList.add(occupationMap);
            }else{
                map.put("flag",0);
                map.put("msg","能暂未开发！");
                success.put("data",map);
                return success;
            }
            map.put("valueList",genericValueList);
        }
    }
    success.put("data",map);
    return success;
}