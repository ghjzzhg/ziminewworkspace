package org.ofbiz.content.content;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.event.EventUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lxx on 2016/3/21.
 */
public class ExcelImport {
    public static final String module = ExcelImport.class.getName();
    public static String uploadExcels(HttpServletRequest request, HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        String savePath = "excel";
        File file = new File(savePath);
        if (!file.exists() && !file.isDirectory()) {
            //创建目录
            file.mkdir();
        }
        String message = "";
        try{
            //使用Apache文件上传组件处理文件上传步骤：
            //1、创建一个DiskFileItemFactory工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //2、创建一个文件上传解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解决上传文件名的中文乱码
            upload.setHeaderEncoding("UTF-8");
            //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = upload.parseRequest(request);
            for(FileItem item : list){
                //如果fileitem中封装的是普通输入项的数据
                if(item.isFormField()){
                    String name = item.getFieldName();
                    //解决普通输入项的数据的中文乱码问题
                    String value = item.getString("UTF-8");
                }else{//如果fileitem中封装的是上传文件
                    //得到上传的文件名称，
                    String filename = item.getName();
                    if(filename==null || filename.trim().equals("")){
                        continue;
                    }
                    //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                    //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                    filename = filename.substring(filename.lastIndexOf("/")+1);
                    //获取item中的上传文件的输入流
                    InputStream in = item.getInputStream();
                    //创建一个文件输出流
                    FileOutputStream out = new FileOutputStream(savePath + "/" + filename);
                    //创建一个缓冲区
                    byte buffer[] = new byte[1024];
                    //判断输入流中的数据是否已经读完的标识
                    int len = 0;
                    //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                    while((len=in.read(buffer))>0){
                        out.write(buffer, 0, len);
                    }
                    //关闭输入流
                    in.close();
                    //关闭输出流
                    out.close();
                    //删除处理文件上传时生成的临时文件
                    item.delete();
                    message = "文件上传成功！";
                    Map<String,Object> staffMap = setStaffListByArray(new File(savePath + "/" + filename), 0,delegator);
                    List<Map<String,Object>> infoList =(List<Map<String,Object>>)staffMap.get("staffList");
                    List<Map<String,Object>> errorList =(List<Map<String,Object>>)staffMap.get("errorList");
                    if(UtilValidate.isNotEmpty(infoList)){
                        for(Map<String,Object> map : infoList){
                            String partyId = delegator.getNextSeqId("Party");//获取主键ID
                            map.put("partyId",partyId);
                            map.put("statusId","PARTY_ENABLED");
                            map.put("partyTypeId","PERSON");
                            map.put("createdDate",new java.sql.Timestamp(System.currentTimeMillis()));
                            map.put("createdByUserLogin",userLogin.get("partyId"));
                            delegator.create(delegator.makeValidValue("Party",map));
                            String gender = map.get("gender").toString();
                            map.remove("gender");//person中gender类型不一致
                            delegator.create(delegator.makeValidValue("Person",map));
                            map.put("jobState", "WORKING");
                            map.put("gender", gender);
                            Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblStaff","numName","workerSn","prefix","employee","userLogin",userLogin));
                            map.put("workerSn",uniqueNumber.get("number"));
                            delegator.create(delegator.makeValidValue("TblStaff",map));
                            Map<String,Object> maps = new HashMap<String,Object>();
                            maps.put("partyId",partyId);
                            maps.put("roleTypeId","_NA_");
                            delegator.create(delegator.makeValidValue("PartyRole",maps));
                        }
                    }
                    if(UtilValidate.isNotEmpty(errorList)){
                        message = "导入出错的原因：";
                        Iterator it = errorList.iterator();
                        while (it.hasNext()) {
                            Map errorMap = (Map)it.next();
                            String errorStr = errorMap.get("errorStr").toString();
                            message=message+errorStr+";";
                        }
                    }
                }
            }
        }catch (Exception e) {
            message= "文件上传失败！";
            e.printStackTrace();

        }
        return EventUtil.returnSuccess(request, message);
    }
    /**
     * 取得excel文件数据
     * @param file
     * @param ignoreRows
     * @return
     */
    public static Map<String,Object> setStaffListByArray(File file, int ignoreRows,Delegator delegator){
        Map<String,Object> staffMap = new HashMap();
        List<Map<String,Object>> staffList = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> errorList = new ArrayList<Map<String,Object>>();
        try {
            String[][] result = ExcelManipulate.getData(file,ignoreRows);
            if(null != result){
                int rowLength = result.length;
                for(int i=1;i<rowLength;i++) {
                    Map<String,Object> map = new HashMap();
                    Map<String,Object> errorMap = new HashMap();
                    String errorStr="";
                    String fullName = result[i][0];
                    String birthDay = result[i][1];
                    String gender = result[i][2];
                    String ifMarried = result[i][3];
                    String age = result[i][4];
                    String salary = result[i][5];
                    String politicalStatus = result[i][6];
                    String phoneNumber = result[i][7];
                    String familyAddress = result[i][8];
                    String domicileType = result[i][9];
                    String placeOfOrigin = result[i][10];
                    String cardId = result[i][11];
                    String department = result[i][12];
                    String post = result[i][13];
                    String workDate = result[i][14];
                    String position = result[i][15];
                    String laborType = result[i][16];
                    String degree = result[i][17];
                    String diploma = result[i][18];
                    if(UtilValidate.isNotEmpty(fullName)){
                        map.put("fullName",fullName);
                    }
                    if(UtilValidate.isNotEmpty(birthDay)){
                        map.put("birthDay", UtilDateTime.toSqlDate(birthDay,"yyyy-MM-dd"));
                    }
                    if(UtilValidate.isNotEmpty(gender)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",gender)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("gender",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(ifMarried)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",ifMarried)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("ifMarried",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(age)){
                        map.put("age",age);
                    }
                    if(UtilValidate.isNotEmpty(salary)){
                        map.put("salary",salary);
                    }
                    if(UtilValidate.isNotEmpty(politicalStatus)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",politicalStatus)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("politicalStatus",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(phoneNumber)){
                        map.put("phoneNumber",phoneNumber);
                    }
                    if(UtilValidate.isNotEmpty(familyAddress)){
                        map.put("familyAddress",familyAddress);
                    }
                    if(UtilValidate.isNotEmpty(domicileType)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",domicileType)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("domicileType",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(placeOfOrigin)){
                        map.put("placeOfOrigin",placeOfOrigin);
                    }
                    if(UtilValidate.isNotEmpty(cardId)){
                        map.put("cardId",cardId);
                    }
                    if(UtilValidate.isNotEmpty(department)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectPartyGroup").where(UtilMisc.toMap("selectId",department)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("department",genericValue.get("partyId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(post)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectRoleType").where(UtilMisc.toMap("selectId",post)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("post",genericValue.get("roleTypeId"));
                            }else{
                                map.put("post",post);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(workDate)){
                        map.put("workDate",UtilDateTime.toSqlDate(workDate,"yyyy-MM-dd"));
                    }
                    if(UtilValidate.isNotEmpty(position)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",position)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("(position",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(laborType)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",laborType)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("laborType",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(degree)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",degree)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("degree",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(UtilValidate.isNotEmpty(diploma)){
                        try{
                            GenericValue genericValue = EntityQuery.use(delegator).select().from("TblSelectMaintenance").where(UtilMisc.toMap("selectId",diploma)).queryOne();
                            if(UtilValidate.isNotEmpty(genericValue)){
                                map.put("diploma",genericValue.get("enumId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(isMobile(phoneNumber)==true&&isIdCardNo(cardId)==true){
                        try{
                            List<GenericValue> genericValueList = EntityQuery.use(delegator).select().from("Person").where(UtilMisc.toMap("cardId",map.get("cardId"))).queryList();
                            if(genericValueList.size()==0){
                                staffList.add(map);
                            }else{
                                errorStr = fullName+"的的记录里面身份号与数据库中的记录有重复，请核对后再操作";
                                errorMap.put("errorStr",errorStr);
                                errorList.add(errorMap);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(isMobile(phoneNumber)==false&&isIdCardNo(cardId)==true){
                        errorStr = fullName+"的记录里面手机号是错误的，请修正并手动添加";
                        errorMap.put("errorStr",errorStr);
                        errorList.add(errorMap);
                    } else if(isMobile(phoneNumber)==true&&isIdCardNo(cardId)==false){
                        errorStr = fullName+"的记录里面身份号是错误的，请修正并手动添加";
                        errorMap.put("errorStr",errorStr);
                        errorList.add(errorMap);
                    }else if(isMobile(phoneNumber)==false&&isIdCardNo(cardId)==false){
                        errorStr = fullName+"的记录里面手机号和身份号都是错误的，请修正并手动添加";
                        errorMap.put("errorStr",errorStr);
                        errorList.add(errorMap);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(UtilValidate.isNotEmpty(staffList)){
            staffMap.put("staffList",staffList);
        }
        if(UtilValidate.isNotEmpty(errorList)){
            staffMap.put("errorList",errorList);
        }
        return staffMap;
    }

    /**
     * 验证手机号是否正确
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^(((1[3-5]{1}[0-9]{1})|(18[0-9]{1}))+\\d{8})$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
    /**
     * 验证身份证号是否正确
     * @param str
     * @return
     */
    public static boolean isIdCardNo(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$"); // 验证身份证号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
}

