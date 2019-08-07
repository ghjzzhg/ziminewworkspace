import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

context.fileSuffix = parameters.fileSuffix;
context.fileName = parameters.fileName;
context.fileFlag = parameters.fileFlag;
context.rootId = parameters.rootId;
context.folderIds = parameters.folderIds;

String fileType = parameters.fileType;
String fileId = parameters.fileId;
context.fileId = fileId;
if(fileType.equals("folder")){
    GenericValue genericValue = EntityQuery.use(delegator).from("TblDirectoryStructure").where("folderId": fileId, "folderType": "1").queryOne();
    context.folderRemarks = genericValue.get("foldeRemarks");
}
context.fileType = parameters.fileType;