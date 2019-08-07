import org.ofbiz.base.util.UtilMisc

fileId = parameters.fileId;
fileType = parameters.fileType;
caseId = parameters.moduleId;
moduleType = parameters.moduleType;
sharedRoles = parameters.sharedRoles;

runService("updateCasePartyFolderShare", UtilMisc.toMap("caseId", caseId, "fileId", fileId, "fileType", fileType, "sharedRoles", sharedRoles, "userLogin", context.get("userLogin")));
