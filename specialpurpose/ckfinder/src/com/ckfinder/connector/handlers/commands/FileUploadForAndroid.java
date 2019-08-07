package com.ckfinder.connector.handlers.commands;

import com.ckfinder.connector.configuration.ConfigurationFactory;
import com.ckfinder.connector.configuration.IConfiguration;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.event.EventUtil;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rextec-15-1 on 2016/1/16.
 */
public class FileUploadForAndroid {
    public static final String module = FileUploadForAndroid.class.getName();
    private static String fileName;
    private static String partyId;
    private static Delegator delegator;
    private static String type;
    private static IConfiguration configuration;
    private static String currentFolder;


    public static void uploadForAndroid(HttpServletRequest request, HttpServletResponse response) {
        delegator = (Delegator) request.getAttribute("delegator");
        configuration = getIConfigurationByType(delegator, request);
        currentFolder = (String) request.getAttribute("currentFolder");
        if (currentFolder == null){currentFolder = "/";}
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        partyId = userLogin.getString("partyId");
        String message = LoginWorker.login(request, response);
        if ("success".equals(message)) {
            try {
                request.setCharacterEncoding("utf-8");
                String ex_type = (String) request.getParameter("type");
                if (ex_type.equals("1")) {
                    type = "协作空间";
                } else if (ex_type.equals("2")){
                    type = "个人文档";
                }
                fileUpload(request);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String downloadForAndroid(HttpServletRequest request, HttpServletResponse response){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String message = LoginWorker.login(request, response);
        if ("success".equals(message)){
            String filePath = request.getParameter("downloadPath");
            File file = new File(filePath);
            try{
                FileInputStream fileInputStream = new FileInputStream(file);
                response.reset();
                response.setContentType("application/octet-stream");
                response.setContentType("application/OCTET-STREAM;charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
                response.addHeader("Content-Length", "" + file.length());
                byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = fileInputStream.read(bytes)) != -1){
                    response.getOutputStream().write(bytes,0,length);
                }
                fileInputStream.close();

            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return EventUtil.returnError(request, "error");
    }

    private static void fileUpload(final HttpServletRequest request) {

        String path = configuration.getTypes().get(type).getPath() + currentFolder;
        if (path.contains("ckfinder\\ckfinder")) {
            path = path.replace("ckfinder\\ckfinder", "ckfinder");
        }
        path = path.replace("\\","/");

        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
        upload.setHeaderEncoding("UTF-8");
        try{
        List<FileItem> list = upload.parseRequest(request);
        for(FileItem item : list){
            if(item.isFormField()){
                String name = item.getFieldName();
                String value = item.getString("UTF-8");
            }else{
                //得到上传的文件名称，
                BASE64Decoder decoder = new BASE64Decoder();//获取上传文件名,包括路径
                String filename = item.getName();
                if(filename==null || filename.trim().equals("")){
                    continue;
                }
                byte[] b = null;
                try {
                    b = decoder.decodeBuffer(filename);
                    filename = new String(b);
                } catch (Exception e) {
                   e.printStackTrace();
                }

                filename = filename.substring(filename.lastIndexOf(File.separator)+1);
                InputStream in = item.getInputStream();
                File fNew = new File(path, filename);
                int number = 0;
                String newName = "";
                while (true) {
                    if (fNew.exists()) {
                        number++;
                        newName = filename.substring(0,filename.lastIndexOf(".")) + "(" + number + ")" + filename.substring(filename.lastIndexOf("."),filename.length());
                        fNew = new File(path, newName);
                    } else {
                        if (newName.equals("")) {
                            newName = filename;
                        }
                        break;
                    }
                }
                fileName = newName;
                if (fNew.isFile() && !fNew.exists()){
                        fNew.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fNew);
                byte buffer[] = new byte[1024];
                int len = 0;
                while((len=in.read(buffer))>0){
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
                saveDataResource(path);//将上传文件信息保存到 数据库
            }
        }
        }catch (FileUploadBase.FileSizeLimitExceededException e) {
            e.printStackTrace();
        }catch (FileUploadBase.SizeLimitExceededException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
}



    public static void saveDataResource(String path) throws GenericEntityException {
        Map<String,Object> map = new HashMap<String,Object>();
        String resourceId = delegator.getNextSeqId("DataResource").toString();//��ȡ����ID
        map.put("dataResourceId",resourceId);
        map.put("dataResourceTypeId","LOCAL_FILE");
        map.put("dataResourceName",fileName);
        map.put("partyId",partyId);
        String newFolder = path.replace("\\","/") ;
        map.put("parentObjectInfo",newFolder);
        map.put("objectInfo",newFolder+fileName);
        GenericValue dataResoure = delegator.makeValidValue("DataResource", map);
        dataResoure.create();
        String dataScope = delegator.getNextSeqId("TblDataScope").toString();//��ȡ����ID
        Map<String,Object> dataScpoceMap = new HashMap<String,Object>();
        dataScpoceMap.put("scopeId",dataScope);
        dataScpoceMap.put("dataId",resourceId);
        dataScpoceMap.put("dataAttr","all");
        dataScpoceMap.put("scopeType","SCOPE_USER");
        dataScpoceMap.put("scopeValue",partyId);
        dataScpoceMap.put("entityName","DataResource");
        GenericValue executor = delegator.makeValidValue("TblDataScope", dataScpoceMap);
        executor.create();
    }


    private static IConfiguration getIConfigurationByType(Delegator delegator,HttpServletRequest request){
        IConfiguration configuration = null;
        try {
            Map loginMap = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", request.getParameter("USERNAME")));
            request.getSession().setAttribute("userLogin",loginMap);
            configuration = ConfigurationFactory.getInstace().getConfiguration(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configuration;
    }


    public static String returnJson(HttpServletRequest request, HttpServletResponse response, Object toJson,String module) {
        //转化为json数据
        String httpMethod = request.getMethod();
        Writer out = null;
        try {
            JSON json = JSON.from(toJson);
            String jsonStr = json.toString();
            if (jsonStr == null) {
                Debug.logError("JSON Object was empty; fatal error!", module);
                return EventUtil.returnError(request, "error");
            }
            if ("GET".equalsIgnoreCase(httpMethod)) {
                Debug.logWarning("for security reason (OFBIZ-5409) the the '//' prefix was added handling the JSON response.  "
                        + "Normally you simply have to access the data you want, so should not be annoyed by the '//' prefix."
                        + "You might need to remove it if you use Ajax GET responses (not recommended)."
                        + "In case, the util.js scrpt is there to help you", module);
            }
            response.setContentType("application/x-json");
            response.setContentLength(jsonStr.getBytes("UTF8").length);
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
            return EventUtil.returnSuccess(request,"true");
        } catch (IOException e) {
            Debug.logError(e, module);
            return EventUtil.returnError(request, "error");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
