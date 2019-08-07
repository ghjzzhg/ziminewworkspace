import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

caseId = parameters.moduleId;
fileId = parameters.fileId;
moduleType = parameters.moduleType;
fileType = parameters.fileType;
sharedRoles = [];
context.roleTypeList = [];
List<GenericValue> caseParties = EntityQuery.use(delegator).from("CaseProviderAndOwner").where("caseId", caseId).queryList();
Map<String, String> providerRoleMap = new HashMap<>();
if(UtilValidate.isNotEmpty(caseParties)){
    for (GenericValue caseParty : caseParties) {
        String prividerRole = caseParty.getString("providerRole");
        if(UtilValidate.isEmpty(prividerRole)){
            continue;
        }
        GenericValue roleDesc = EntityQuery.use(delegator).from("RoleType").where("roleTypeId", prividerRole).queryOne();
        String groupName = caseParty.getString("groupName");
        String partnerGroupName = caseParty.getString("partnerGroupName");
        if(UtilValidate.isNotEmpty(partnerGroupName)){
            groupName = partnerGroupName;
        }
        context.roleTypeList.push(["roleTypeId":  prividerRole, "description": roleDesc.getString("description"), "groupName": groupName]);
        providerRoleMap.put(caseParty.getString("providerPartyId"), prividerRole);
    }
}

List<GenericValue> sharedFiles;
if("folder".equalsIgnoreCase(fileType)){//目录
    //找出已共享的
    sharedFiles = EntityQuery.use(delegator).from("TblDirectoryStructure").where(UtilMisc.toMap("folderType","2","folderId",fileId, "shareFromModule", moduleType, "shareFromModuleId", caseId)).queryList();

}else if("file".equalsIgnoreCase(fileType)){
    sharedFiles = EntityQuery.use(delegator).from("TblFileOwnership").where(UtilMisc.toMap("fileType","2","fileId",fileId, "shareFromModule", moduleType, "shareFromModuleId", caseId)).queryList();
}

if(UtilValidate.isNotEmpty(sharedFiles)){
    for (GenericValue sharedFile : sharedFiles) {
        String providerGroupId = sharedFile.getString("partyId");//case分享只分到机构一层
        String providerRole = providerRoleMap.get(providerGroupId);
        if (UtilValidate.isNotEmpty(providerRole)) {
            sharedRoles.push(providerRole);
        }
    }
}

context.sharedRoles = sharedRoles.join(",");
