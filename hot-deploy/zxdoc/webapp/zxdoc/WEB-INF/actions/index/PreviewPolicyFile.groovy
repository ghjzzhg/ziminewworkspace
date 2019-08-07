import org.ofbiz.content.content.UploadFileService

String dataResourceId = parameters.dataResourceId;
long fileCount = from("TblPolicyFiles").where("file", dataResourceId.replace("-r", "")).queryCount();
if(fileCount > 0){//只有政策文件可以不登录直接预览
    UploadFileService.imageView(request, response);
}