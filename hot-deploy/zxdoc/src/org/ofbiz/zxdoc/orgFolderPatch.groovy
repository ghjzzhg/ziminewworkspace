package org.ofbiz.zxdoc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang.StringUtils
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties

String companyInitFolder = EntityUtilProperties.getPropertyValue("zxdoc.properties", "companyInitFolder",delegator);

List<Map<String, Object>> configuredFolders = new ArrayList<>();
if (UtilValidate.isNotEmpty(companyInitFolder)) {
    ObjectMapper jsonParser = new ObjectMapper();
    TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
    configuredFolders = jsonParser.readValue(companyInitFolder, typeRef);
}

List<String> topFolderNames = new ArrayList<>();
Map<String, Map<String,Object>> confFolderMap = new HashMap<>();
for (Map<String, Object> ele : configuredFolders) {
    String name = ele.get("name");
    topFolderNames.add(name);
    confFolderMap.put(name, ele);
}
List<String> folderNameList = new ArrayList<>();
////得到企业文件目录列表
List<GenericValue> orgInfoList = EntityQuery.use(delegator).from("PartyRole").where(UtilMisc.toMap("roleTypeId","CASE_ROLE_OWNER")).queryList();
List<String> orgIds = new ArrayList<>();
Map<String, List<String>> topFolderMap = new HashMap<>();
for (GenericValue orgInfo : orgInfoList) {
    String partyId = orgInfo.getString("partyId")
    orgIds.add(partyId);
    topFolderMap.put(partyId, new ArrayList<String>(topFolderNames));
}

List condition = new ArrayList<>();
condition.add(EntityCondition.makeCondition("parentFolderId",EntityOperator.EQUALS,null));
condition.add(EntityCondition.makeCondition("folderType",EntityOperator.EQUALS, '1'));
condition.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, orgIds));
List<GenericValue> folderInfos = EntityQuery.use(delegator).from("TblDirectoryStructure").where(condition).queryList();
for (GenericValue folderInfo : folderInfos) {
    String ownerId = folderInfo.getString("partyId");
    String folderName = folderInfo.getString("folderName");
    topFolderMap.get(ownerId).remove(folderName);
}

for (String partyId  : topFolderMap.keySet()) {
    List<String> missingFolders = topFolderMap.get(partyId);
    for (String missingFolder : missingFolders) {
        Map<String, Object> confFolder = confFolderMap.get(missingFolder);
        String permission = StringUtils.defaultString(confFolder.get("permission"));
        String description = StringUtils.defaultString(confFolder.get("description"));
        //调用文件管理模块服务创建目录
        Map<String, Object> result = runService("addUserFolder", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "partyId", partyId, "folderName", missingFolder, "parentFolderId", null, "permissionStr", permission, "description", description))
        String folderId = result.get("folderId");
        //如果存在子文件夹则查找子文件夹信息
        List<Map<String, Object>> subFolders = confFolder.get("child");
        if (subFolders != null) {
            for (Map<String, Object> elea : subFolders) {
                String subName = elea.get("name");
                String subPermission = StringUtils.defaultString(elea.get("permission"));
                String subDesc = StringUtils.defaultString(elea.get("description"));
                //调用文件管理模块服务创建目录
                Map<String, Object> aresult = runService("addUserFolder", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "partyId", partyId, "folderName", subName, "parentFolderId", folderId, "permissionStr", subPermission, "description", subDesc));
            }
        }
    }
}



