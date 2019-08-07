import org.apache.commons.io.FilenameUtils
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.content.data.DocConverter
import org.ofbiz.content.data.FileManagerFactory
import org.ofbiz.content.data.FileTypeManager
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericValue

String dataResourceId = parameters.dataResourceId;
String idConversion = context.get("idConversion");
if(UtilValidate.isEmpty(dataResourceId)){
    dataResourceId = context.dataResourceId;
    parameters.dataResourceId = dataResourceId;
}
//TODO:文件查看权限检查

GenericValue file = from("DataResource").where("dataResourceId", dataResourceId).queryOne();
String fileName = file.getString("dataResourceName");
String fileExt = FilenameUtils.getExtension(fileName);
if(file != null){
    String type = file.getString("mimeTypeId");
    if(UtilValidate.isNotEmpty(type)){
        if(type.contains("pdf")){
            context.fileType = "pdf";
        }else if(type.contains("image")){
            context.fileType = "image";
        }else if(type.contains("text/plain") || fileExt.contains("txt")){//文本文件
            String fileType = file.getString("dataResourceTypeId");
            FileTypeManager fileManager = FileManagerFactory.getFileManager(fileType, delegator as GenericDelegator);
            byte[] content = fileManager.getFileContent(dataResourceId);
            String code = "gb2312";
            String contentString = "";
            if(UtilValidate.isNotEmpty(content) && content.length > 0){
                if (content[0] == -1 && content[1] == -2 )
                    code = "UTF-16";
                if (content[0] == -2 && content[1] == -1 )
                    code = "Unicode";
                if(content[0]==-17 && content[1]==-69 && content[2] ==-65)
                    code = "UTF-8";
                contentString  = new String(content, code);
            }
            context.contentString = contentString;
            context.fileType = "txt";
        }else if(fileExt.contains("xls") || fileExt.contains("doc") || fileExt.contains("ppt")){
            if(Boolean.parseBoolean(idConversion)){
                context.fileType = "pdf";
                //office文件生成pdf文件
                String pdfFileName = fileName + ".pdf";//TODO:文件名重复问题
                String dataResourceType = file.getString("dataResourceTypeId");
                file = from("DataResource").where("dataResourceName", pdfFileName).queryOne();
                if(file == null){
                    File pdfFile = new File(DocConverter.getInstance().doc2pdf(FileManagerFactory.getFileManager(dataResourceType, delegator).getFileAsStream(dataResourceId), fileName));
                    //采用绝对路径文件方式存储
                    if(pdfFile.exists()){
                        Map<String, Object> createDrResult = dispatcher.runSync("createDataResource", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "dataResourceTypeId", "ZXDOC_FILE", "dataResourceName", pdfFileName, "mimeTypeId", "application/pdf", "objectInfo", pdfFile.getName(), "resourceSize", pdfFile.length()));
                        parameters.dataResourceId = createDrResult.get("dataResourceId");
                    }else{
                        throw new RuntimeException("文件读取错误");
                    }
                }else{
                    parameters.dataResourceId = file.getString("dataResourceId");
                }
            }else{
                context.fileType = "office";
                parameters.fileId = file.getString("dataResourceId");
            }
        }else{
            context.fileType = "other";
            context.fileId = file.getString("dataResourceId");
        }
    }
}