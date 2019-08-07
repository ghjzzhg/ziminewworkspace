package org.ofbiz.content.content;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.FileManagerFactory;
import org.ofbiz.content.data.FileTypeManager;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.RunningService;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 2015/7/30.
 */
public class UploadFileService {

    private static Set<String> supportImgType = new HashSet<String>();
    private static Set<String> supportFileType = new HashSet<String>();

    private static String savePath = "specialpurpose/ckfinder/webapp/ckfinder/uploadSendFile";

    static {
        supportImgType.add("jpg");
        supportImgType.add("png");
        supportImgType.add("jpeg");
        supportImgType.add("gif");
        supportImgType.add("bmp");

        supportFileType.add("xls");
        supportFileType.add("doc");
        supportFileType.add("pdf");
    }

    public static String getSavePath(){
        return savePath;
    }
    public static String uploadFileList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");
        String message = "";
        try{
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setSizeThreshold(1024*100);
            ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
            upload.setHeaderEncoding("UTF-8");
            List<FileItem> list = upload.parseRequest(request);
            for(FileItem item : list){
                if(item.isFormField()){
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                }else{
                    //得到上传的文件名称，
                    String filename = item.getName();
                    if(filename==null || filename.trim().equals("")){
                        continue;
                    }
                    filename = filename.substring(filename.lastIndexOf(File.separator)+1);
                    String fileExtName = filename.substring(filename.lastIndexOf(".")+1);
                    String fileId = saveFile(request,filename,savePath);
                    InputStream in = item.getInputStream();
                    String realSavePath = makePath(partyId, savePath);
                    FileOutputStream out = new FileOutputStream(realSavePath + "/" + filename);
                    byte buffer[] = new byte[1024];
                    int len = 0;
                    while((len=in.read(buffer))>0){
                        out.write(buffer, 0, len);
                    }
                    in.close();
                    out.close();

                    if(null != request.getAttribute("compressImgWidth")){
                        Integer compressImgWidth = Integer.getInteger(request.getAttribute("compressImgWidth").toString());
                        if(supportImgType.contains(filename.toLowerCase())) {
                            File resultFile = new File(realSavePath + "/" + filename);
                            BufferedImage image = ImageIO.read(resultFile);
                            int width = image.getWidth();
                            if (width > compressImgWidth) {
                                int height = image.getHeight();
                                int targetWidth = compressImgWidth;
                                int targetHeight = targetWidth * width / height;
                                image.getGraphics().drawImage(image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
                                ImageIO.write(image, fileExtName, resultFile);
                            }
                        }
                    }
                    message = "文件上传成功！";
                    request.setAttribute("fileId",fileId);
                    request.setAttribute("fileType",fileExtName);
                    request.setAttribute("fileName",fileId + "." + fileExtName);
                    String filePath = realSavePath + "/" + filename;
                    filePath = filePath.replace("\\","/");
                    filePath = filePath.substring(filePath.lastIndexOf("specialpurpose/ckfinder/webapp")+"specialpurpose/ckfinder/webapp".length(),filePath.length());
                    request.setAttribute("filePath",filePath);
                    //kindeditor文件上传所需返回值
                    request.setAttribute("url",filePath);
                    request.setAttribute("error", "0");
                }
            }
        }catch (FileUploadBase.FileSizeLimitExceededException e) {
            request.setAttribute("error", "1");
            request.setAttribute("message", "单个文件超出最大值！！！");
            request.getRequestDispatcher("/message.jsp").forward(request, response);
            Debug.logError(e, "文件上传错误", UploadFileService.class.getName());
            return "error";
        }catch (FileUploadBase.SizeLimitExceededException e) {
            request.setAttribute("error", "1");
            request.setAttribute("message", "上传文件的总的大小超出限制的最大值！！！");
            request.getRequestDispatcher("/message.jsp").forward(request, response);
            Debug.logError(e, "文件上传错误", UploadFileService.class.getName());
            return "error";
        }catch (Exception e) {
            message= "文件上传失败！";
            request.setAttribute("error", "1");
            request.setAttribute("message", "上传文件错误!");
            Debug.logError(e, "文件上传错误", UploadFileService.class.getName());
            return "error";
        }finally {
            request.setAttribute("messages",message);
        }
        return "success";
    }

    public static String saveFile(HttpServletRequest request, String newFileName, String path) throws GenericEntityException {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");
        Map<String,Object> map = new HashMap<String,Object>();
        String resourceId = delegator.getNextSeqId("DataResource").toString();//获取主键ID
        map.put("dataResourceId",resourceId);
        map.put("dataResourceTypeId","ATTACHMENT_FILE");
        map.put("dataResourceName",newFileName);
        map.put("partyId",partyId);
        String newFolder = path.replace("\\", "/") + "/" + partyId + "/";
        map.put("parentObjectInfo",newFolder);
        map.put("objectInfo",newFolder + newFileName);
        GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
        dataResoure.create();
        return resourceId;
    }

    public static String saveDoc(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        HttpSession session = request.getSession();
        String newFileName = request.getParameter("newFileName").toString();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");
        Map<String,Object> map = new HashMap<String,Object>();
        String resourceId = newFileName;
        map.put("dataResourceId",resourceId);
        map.put("dataResourceTypeId","ATTACHMENT_FILE");
        map.put("dataResourceName",newFileName + ".doc");
        map.put("partyId",partyId);
        String newFolder = UtilProperties.getPropertyValue("zxdoc", "filePath");
        map.put("parentObjectInfo",newFolder);
        map.put("objectInfo",newFolder + newFileName);
        GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
        dataResoure.create();
        return "success";
    }

    /**
     * 为防止一个目录下面出现太多文件，要使用hash算法打散存储
     * @Method: makePath
     * @Description:
     *
     * @param savePath 文件存储路径
     * @return 新的存储目录
     */
    private static String makePath(String partyId, String savePath){
        //构造新的保存目录
        String dir = (savePath +"/"+ partyId).replace("\\","/");  //upload\2\3  upload\3\5
        //File既可以代表文件也可以代表目录
        File file = new File(dir);
        //如果目录不存在
        if(!file.exists()){
            //创建目录
            file.mkdirs();
        }
        return dir;
    }

    public static String downloadUploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String message = "";
        String fileId = request.getParameter("dataResourceId");
        if (UtilValidate.isEmpty(fileId)) {
            fileId = request.getParameter("fileName");
        }
        return UploadFileService.downloadFile(request,response,delegator,fileId) ;
    }

    public static String  downloadFile(HttpServletRequest request, HttpServletResponse response ,Delegator delegator, String fileId) throws IOException, GenericEntityException {
        GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
        if(null != fileData){
            FileTypeManager fileManager = FileManagerFactory.getFileManager(fileData.getString("dataResourceTypeId"), (GenericDelegator) delegator);
            String fileName = fileData.getString("dataResourceName");
            Long fileSize = fileData.getLong("resourceSize");
            String isPublic = fileData.getString("isPublic");
            if((UtilValidate.isEmpty(isPublic) || "N".equals(isPublic)) && request.getSession().getAttribute("userLogin") == null){
                //非公开资源必须验证用户登录
                request.setAttribute("message","无权限获取文件");
                return "error";
            }
            InputStream fileStream = fileManager.getFileAsStream(fileId);
            if(fileStream == null){
                request.setAttribute("message","文件不存在");
                return "error";
            }

            String downloadFileName = fileName;
            downloadFileName = URLEncoder.encode(downloadFileName, "UTF-8");
            if(downloadFileName.length()>150){//解决IE 6.0 bug
                downloadFileName = new String(downloadFileName.getBytes("GBK"),"ISO-8859-1");
            }
            response.setHeader("Content-disposition", "attachment; filename="
                    + downloadFileName);

            exportFileToResponse(fileStream, fileName, response);
            return "success";
        }else{
            return "error";
        }
    }

    /**
     * 下载积分资源
     * @return 成功
     */
    public static String downloadScoreFile(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, IOException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String modelId = request.getParameter("modelId");//模块Id
        String scoreId = request.getParameter("scoreId");//模块内容id
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String scoreSource = userLogin.get("partyId").toString();
        String scoreTarget = "";
        int bizScoreOnSource = 0;
        int bizScoreOnTarget = 0;
        if(modelId.equals("library")){
            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            Map<String, Object> map = null;
            try {
                //再次验证积分，得到资源和积分数据
                map = dispatcher.runSync("searchScoreByUserId", UtilMisc.toMap("libraryId",scoreId, "userLogin", userLogin));
            } catch (GenericServiceException e) {
                e.printStackTrace();
            }
            if(UtilValidate.isNotEmpty(map.get("data"))){
                Map<String,Object> data = (Map<String, Object>) map.get("data");
                Boolean isyLibrary = (Boolean) data.get("isyLibrary");
                //判断资源是否存在
                if(isyLibrary){
                    Boolean isFile = (Boolean) data.get("isFile");
                    //判断文件是否存在
                    if(isFile){
                        Boolean isyHistory = (Boolean) data.get("isyHistory");
                        //如果已经存在历史信息，则不需要再次扣除增加积分
                        if(!isyHistory){
                            Boolean isDownLoad = (Boolean) data.get("isDownLoad");
                            //判读是否允许下载
                            if(isDownLoad){
                                scoreTarget = data.get("libraryPartyId").toString();//积分给与目标party
                                //如果资源积分等于0,那么积分不需要执行 下载人员和目标人员相同，不需要积分执行
                                if(!data.get("libraryScore").toString().equals("0") && !scoreSource.equals(scoreTarget)){
                                    bizScoreOnSource = Integer.parseInt(data.get("libraryScore").toString()) * -1;//扣除积分
                                    bizScoreOnTarget = Integer.parseInt(data.get("libraryScore").toString());//增加积分
                                    try {
                                        dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreSource",scoreSource,"scoreTarget",scoreTarget, "bizScoreOnSource", bizScoreOnSource, "bizScoreOnTarget", bizScoreOnTarget, "eventName", "SCORE_RULE_ADDANDSUBTRACT"));
                                    } catch (GenericServiceException e) {
                                        Debug.logError(e, "积分下载错误");
                                    }
                                }
                                GenericValue genericValue = EntityQuery.use(delegator).from("TblScoreFileHistory").where(UtilMisc.toMap("partyId", map.get("fileId"), "fileId",map.get("fileId"))).queryOne();
                                if(genericValue == null){
                                    Map<String, Object> scoreMap = new HashMap<>();
                                    scoreMap.put("partyId", scoreSource);
                                    scoreMap.put("fileId",data.get("fileId"));
                                    scoreMap.put("createTime",new Timestamp((new Date()).getTime()));
                                    GenericValue scoreFile = delegator.makeValidValue("TblScoreFileHistory", scoreMap);
                                    scoreFile.create();
                                }
                                downloadFile(request, response, delegator, data.get("fileId").toString());
                            }else{
                                throw new RuntimeException("积分不足！");
                            }
                        }else{
                            downloadFile(request, response, delegator, data.get("fileId").toString());
                        }
                    }else{
                        throw new RuntimeException("文件已被删除！");
                    }
                }else{
                    throw new RuntimeException("资源已删除，请刷新");
                }
            }
        }
        return "success";
    }

    public static String imageView(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String message = "";

        String fileId = request.getParameter("dataResourceId");
        if (UtilValidate.isEmpty(fileId)) {
            fileId = request.getParameter("fileName");
        }

        GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
        if(null != fileData){
            FileTypeManager fileManager = FileManagerFactory.getFileManager(fileData.getString("dataResourceTypeId"), (GenericDelegator) delegator);
            String fileName = fileData.getString("dataResourceName");

            Long fileSize = fileData.getLong("resourceSize");
            /*if(fileSize > 5120000){//大于5M的从静态文件获取

            }*/
            String isPublic = fileData.getString("isPublic");
            if((UtilValidate.isEmpty(isPublic) || "N".equals(isPublic)) && request.getSession().getAttribute("userLogin") == null){
                //非公开资源必须验证用户登录
                request.setAttribute("message","无权限获取文件");
                return "error";
            }
            InputStream fileStream = fileManager.getFileAsStream(fileId);
            if(fileStream == null){
                request.setAttribute("message","文件不存在");
                return "error";
            }else {
                //changed by galaxypan@2017-10-31:文件存在时直接通过nginx静态读取
                fileStream.close();
                String objectInfo = fileData.getString("objectInfo");
                if(objectInfo.indexOf("\\") > -1){
                    objectInfo = objectInfo.substring(objectInfo.lastIndexOf("\\") + 1);
                }
                String hostUrl = UtilProperties.getPropertyValue("general", "host-url");
                if(!hostUrl.endsWith("/")){
                    hostUrl += "/";
                }
                response.sendRedirect(hostUrl + "zmwfile/" + objectInfo + "?t=" + System.currentTimeMillis());
            }
//            exportFileToResponse(fileStream, fileName, response);
            return "success";
        }else{
            return "error";
        }
    }


    public static String partyAvatar(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String message = "";

        String partyId = request.getParameter("partyId");
        if (UtilValidate.isEmpty(partyId)) {
            partyId = ((GenericValue)request.getSession().getAttribute("userLogin")).getString("partyId");
        }
        GenericValue partyObj = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
        String fileId = partyObj.getString("avatar");
        if(UtilValidate.isNotEmpty(fileId)){
            GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
            if(null != fileData){
                FileTypeManager fileManager = FileManagerFactory.getFileManager(fileData.getString("dataResourceTypeId"), (GenericDelegator) delegator);
                String fileName = fileData.getString("dataResourceName");
                Long fileSize = fileData.getLong("resourceSize");
                InputStream fileStream = fileManager.getFileAsStream(fileId);
                if(fileStream != null) {
                    exportFileToResponse(fileStream, fileName, response);
                    return "success";
                }
            }
        }
        //默认头像
        File defaultAvatar = new File(request.getRealPath("/"), "images/avatar.png");
        if(defaultAvatar.exists()){
            exportFileToResponse(new FileInputStream(defaultAvatar), "avatar", response);
        }

        return "success";
    }

    private static void exportFileToResponse(File file, HttpServletResponse response) throws IOException {
        String message = "";
        if(file.exists()){
            InputStream inputStream = new FileInputStream(file);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            String extension = FilenameUtils.getExtension(file.getName());
            if(supportImgType.contains(extension)){
                response.setContentType("image/" + extension);
            }else{
                if("pdf".equals(extension)){
                    response.setContentType("application/pdf");
                }else if("xls".equals(extension)){
                    response.setContentType("application/vnd.ms-excel");
                }else if("doc".equals(extension)){
                    response.setContentType("application/rtf");
                }
            }
            ServletOutputStream sos = null;
            try{
                sos = response.getOutputStream();
                IOUtils.copy(inputStream, sos);
            }finally {
                if (sos != null) {
                    try {
                        sos.flush();
                        sos.close();
                    } catch (IOException e) {
                        message = "关闭文件流错误";
                    }
                }
                inputStream.close();
            }
        }else{
            message = "文件不存在";
        }
    }

    private static void exportFileToResponse(InputStream inputStream, String fileName, HttpServletResponse response) throws IOException {

        String extension = FilenameUtils.getExtension(fileName);
        if(supportImgType.contains(extension)){
            response.setContentType("image/" + extension);
        }else{
            if("pdf".equalsIgnoreCase(extension)){
                response.setContentType("application/pdf");
            }else if("xls".equalsIgnoreCase(extension) || "xlsx".equalsIgnoreCase(extension)){
                response.setContentType("application/vnd.ms-excel");
            }else if("doc".equalsIgnoreCase(extension) || "docx".equalsIgnoreCase(extension)){
                response.setContentType("application/rtf");
            }
        }
        ServletOutputStream sos = null;
        try{
            sos = response.getOutputStream();
            long length = IOUtils.copyLarge(inputStream, sos, new byte[409600]);
            response.setHeader("Content-Length", String.valueOf(length));
        }catch (Exception e){
            Debug.logError(e, "UploadFileService");
        }finally {
            if (sos != null) {
                try {
                    sos.flush();
                    sos.close();
                } catch (IOException e) {
                    Debug.logError(e, UploadFileService.class.getName());
                }
            }
            inputStream.close();
        }
    }

    public static  String deleteUplodeFile(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String message = "";
        String flag;
        String fileId = request.getParameter("fileId");
        GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();

        message = "删除成功";
        flag = "success";
        try {
            FileTypeManager fileManager = FileManagerFactory.getFileManager(fileData.get("dataResourceTypeId").toString(), (GenericDelegator) delegator);
            fileManager.delFile(fileId);
        }catch (Exception e){
            e.printStackTrace();
            message = "删除失败";
            flag = "error";
        }
        request.setAttribute("message",message);
        return flag;
    }

    public static  String selectUploadFileList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String fileList = request.getParameter("fileList");
        List<GenericValue> fileModelList = new ArrayList<GenericValue>();
        if(null != fileList && fileList.length()  > 0){
            String[] files = fileList.split(",");
            for(String fileId :files){
                GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileId)).queryOne();
                fileModelList.add(fileData);
            }
        }
        request.setAttribute("fileModelList",fileModelList);
        return "success";
    }

}

