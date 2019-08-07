package org.ofbiz.oa.android;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.event.EventUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
*Author 邓贝贝
*created 2016/11/7 15:24
*function 文件管理
*/
public class FileListAndroidServices {
    public static final String module = FileListAndroidServices.class.getName();

    public static String searchFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String userName = request.getParameter("USERNAME");
        List<Map<String, Object>> list = null;
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String message = LoginWorker.login(request, response);
        if("success".equals(message)){
            String fileId = request.getParameter("fileId");
            try{
                if (UtilValidate.isNotEmpty(fileId)) {
                    String path;
                    if (fileId.contains("/")) {
                         path =fileId;
                    } else {
                        path = fileId + "/" + userName+"/";
                    }
                    list = getExplore(delegator, path);
                } else {
                    list = searchXmlType();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
            return CommonReturnJson.returnJson(request, response, list, module);
        }else {
            return EventUtil.returnError(request, "error");
        }
    }

    public static List<Map<String,Object>> searchXmlType(){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File("specialpurpose/ckfinder/webapp/ckfinder/WEB-INF/config.xml");
            Document doc =  db.parse(file.getAbsolutePath());
            NodeList nl_url = doc.getElementsByTagName("url");
            NodeList nl_name = doc.getElementsByTagName("name");
            int size1 = nl_url.getLength();
            for (int i = 0; i < size1; i++) {
                Map<String,Object> map = new HashMap();
                Node node_url = (Node)nl_url.item(i);
                Node node_name = (Node)nl_name.item(i);
                String url = node_url.getFirstChild().getNodeValue();
                url = url.substring("%BASE_URL%".length(),url.length()-1);
                String name = node_name.getFirstChild().getNodeValue();
                if(!"_thumbs".equals(url)){
                    map.put("url",url);
                    map.put("name",name);
                    list.add(map);
                }
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
        return list;
    }

  /*
  *Author 邓贝贝
  *created 2016/11/6 15:40
  *function 获取服务器本地文件内的数据
  */
    public static List<Map<String, Object>> getExplore(Delegator delegator, String url) throws GenericEntityException {
        List<GenericValue> dataResourceList = new ArrayList<GenericValue>();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
       GenericValue folder = new GenericValue();
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };
        String path ="D:/workspace/oa-ofbiz/specialpurpose/ckfinder/webapp/ckfinder/uploadFiles/" + url;
        File dir = new File(path);
        File[] subDirsList = dir.listFiles();
        if(null != subDirsList){
            for (File file : subDirsList){
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("dataResourceName",file.getName());
                map.put("objectInfo",path);
                folder = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition(map)).distinct().queryOne();
                if(null != folder && folder.size() > 0){
                    Map<String,Object> fileMap = new HashMap<String,Object>();
                    //判断是文件夹还是文件
                    if (folder.get("dataResourceTypeId").equals("LOCAL_FOLDER")){
                        fileMap.put("isFolder", true);
                    } else {
                        fileMap.put("isFolder", false);
                    }
                    fileMap.put("dataResourceId",folder.get("dataResourceId"));
                    fileMap.put("dataResourceTypeId",folder.get("dataResourceTypeId"));
                    fileMap.put("url",url);
                    fileMap.put("name",folder.get("dataResourceName"));
                    list.add(fileMap);
                    dataResourceList.add(folder);
                }
            }
        }
        return list;
    }

}