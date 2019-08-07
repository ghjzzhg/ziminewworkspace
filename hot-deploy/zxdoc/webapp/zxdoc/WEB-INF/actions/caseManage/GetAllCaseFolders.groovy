import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery


List<GenericValue> partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", userLogin.partyId, "partyRelationshipTypeId", "ORG_SUB_ACCOUNT"), null, false);
String groupId = partyRelationships.size() > 0 ? partyRelationships.get(0).get("partyIdFrom") : userLogin.partyId;

GenericValue groupInfo = from("BasicGroupInfo").where("partyId", groupId).queryOne()
boolean isCompany = groupInfo != null && "CASE_ROLE_OWNER".equalsIgnoreCase(groupInfo.getString("roleTypeId"));
context.isCompany = isCompany;
String templateId = parameters.templateId;
Map<String, List<String>> existingFolders = new HashMap<>();
if(UtilValidate.isEmpty(parameters.caseId) && UtilValidate.isNotEmpty(templateId)) {//设置CASE模板
    GenericValue template = from("TblCaseTemplate").where("id", templateId).queryOne();
    context.template = template;
    /*String id = template.getString("templateKey");
    if(UtilValidate.isEmpty(id)){
        id = templateId;
    }*/
    List<GenericValue> categoryFolders = from("TblCaseCategoryFolder").where("caseCategory", templateId).queryList();
    for (GenericValue roleFolder : categoryFolders) {
        String folderPath = roleFolder.getString("folderPath");
        String roles = roleFolder.getString("roles");
        if(UtilValidate.isNotEmpty(roles)){
            String[] roleArray = roles.split(",");
            for (String role : roleArray) {
                List<String> roleAssign = existingFolders.get(role);
                if(roleAssign == null){
                    roleAssign = new ArrayList<>();
                    existingFolders.put(role, roleAssign);
                }
                roleAssign.add(folderPath + "_" + roleFolder.getString("fileType"));
            }
        }
    }

}else if(UtilValidate.isNotEmpty(parameters.caseId)){
    Map<String, String> providerRoleMap = new HashMap<>();
    List<GenericValue> caseParties = EntityQuery.use(delegator).from("CaseProviderAndOwner").where("caseId", parameters.caseId).queryList();
    if(UtilValidate.isNotEmpty(caseParties)){
        for (GenericValue caseParty : caseParties) {
            String prividerRole = caseParty.getString("providerRole");
            providerRoleMap.put(caseParty.getString("providerPartyId"), prividerRole);
        }
    }

    List<GenericValue> shareFolders = EntityQuery.use(delegator).from("TblDirectoryStructure").where("shareFromModule", "case", "shareFromModuleId", parameters.caseId, "folderType", "2").queryList();
    if(UtilValidate.isNotEmpty(shareFolders)){
        for (GenericValue shareFolder : shareFolders) {
            String partyId = shareFolder.getString("partyId");
            String caseRole = providerRoleMap.get(partyId);
            if (UtilValidate.isNotEmpty(caseRole)) {
                List<String> roleAssign = existingFolders.get(caseRole);
                if(roleAssign == null){
                    roleAssign = new ArrayList<>();
                    existingFolders.put(caseRole, roleAssign);
                }
                roleAssign.add(shareFolder.getString("folderId"));
            }
        }
    }

    List<GenericValue> shareFiles = EntityQuery.use(delegator).from("TblFileOwnership").where("shareFromModule", "case", "shareFromModuleId", parameters.caseId, "fileType", "2").queryList();
    if(UtilValidate.isNotEmpty(shareFiles)){
        for (GenericValue shareFile : shareFiles) {
            String partyId = shareFile.getString("partyId");
            String caseRole = providerRoleMap.get(partyId);
            if (UtilValidate.isNotEmpty(caseRole)) {
                List<String> roleAssign = existingFolders.get(caseRole);
                if(roleAssign == null){
                    roleAssign = new ArrayList<>();
                    existingFolders.put(caseRole, roleAssign);
                }
                roleAssign.add(shareFile.getString("fileId"));
            }
        }
    }
}

ObjectMapper objectMapper = new ObjectMapper();
context.existingFolders = objectMapper.writer().writeValueAsString(existingFolders);

if (UtilValidate.isNotEmpty(parameters.caseId)) {
    List<GenericValue> caseRoles = from("TblCaseParty").where(UtilMisc.toMap("caseId", parameters.caseId)).queryList();
    List<Map> roleType = new ArrayList<>();
    if(caseRoles!=null)
    {
        for (GenericValue caseRole:caseRoles)
        {
            Map roleMap = new HashMap();
            String type = caseRole.get("roleTypeId");
            //changed by galaxypan@2017-08-03:合伙人不再单独一个CASE ROLE
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
}else if(!isCompany){//管理员模板设置时
    String allCaseFolders = UtilProperties.getPropertyValue("zxdoc.properties", "allCaseFolders");
    JsonParser parser = new JsonParser();
    JsonElement elem = parser.parse(allCaseFolders);
    JsonArray folders = elem.getAsJsonArray();
    List<String> folderPath = new ArrayList<>();
    for (JsonElement ele : folders) {
        folderPath.add(ele.getAsString());
    }

    context.folderPath = folderPath;
}

if(UtilValidate.isNotEmpty(templateId)) {
    GenericValue caseTemplate = from("TblCaseTemplate").where("id", parameters.templateId).cache().queryOne();
    String roles = caseTemplate == null ? "" : caseTemplate.get("roles");
    List role = new ArrayList();
    if (roles != null && roles != "") {
        if (roles.substring(0, 1) == "[") {
            String roleMember = roles.substring(1, roles.length() - 1);
            role = roleMember.split(",");
        } else {
            role.add(roles);
        }
    }
    List list = new ArrayList();
    for (int i = 0; i < role.size(); i++) {
        Map map = new HashMap();
        String roleTypeId = role.get(i);
        //处理非第一个元素会首字符为空格的情况
        if (i != 0) {
            roleTypeId = roleTypeId.substring(1, roleTypeId.length());
        }
        GenericValue rt = EntityQuery.use(delegator).from("RoleType").where("roleTypeId", roleTypeId, "parentTypeId", "CASE_ROLE").queryOne();
        String description = rt.get("description");
        map.put("roleTypeId", roleTypeId);
        map.put("description", description);
        list.add(map);
    }
    context.roleTypeList = list;
}
//移除企业
for(int i = 0; i < context.roleTypeList.size(); i ++){
    if("CASE_ROLE_OWNER".equalsIgnoreCase(context.roleTypeList[i].get("roleTypeId"))){
        context.roleTypeList.remove(i);
        break;
    }
}
