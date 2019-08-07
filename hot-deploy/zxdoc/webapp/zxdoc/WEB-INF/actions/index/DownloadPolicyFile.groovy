import org.ofbiz.content.content.UploadFileService

String dataResourceId = parameters.dataResourceId;
long fileCount = from("TblPolicyFiles").where("id", dataResourceId).queryCount();
if(fileCount > 0){//只有政策文件可以不登录直接下载
    UploadFileService.downloadUploadFile(request, response);
}