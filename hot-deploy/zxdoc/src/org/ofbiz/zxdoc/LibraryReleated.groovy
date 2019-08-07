package org.ofbiz.zxdoc

import freemarker.ext.beans.HashAdapter
import org.apache.commons.collections.map.HashedMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.content.data.DataServices
import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityExpr
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtilProperties
import org.ofbiz.service.ServiceUtil

import java.security.acl.Group


public Map<String, Object> addLibrary() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String title = context.get("title");
    String type = context.get("type");
    String tags = context.get("tags");
    String introduce = context.get("introduce");
    String accessType = context.get("accessType");
    Integer score = 0;
    //只有当是付费文档时才存入付费积分
    if(accessType.equals("1") && UtilValidate.isNotEmpty(context.get("score"))){
        score = Integer.parseInt(context.get("score").toString());
    }
    String size = context.get("_uploadedFile_size");
    String fileSize = EntityUtilProperties.getPropertyValue("content.properties", "fileSize", "localhost", delegator);
    if(size != null && Integer.valueOf(size) < Integer.parseInt(fileSize) * 1024 * 1024 ) {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = userLogin.get("partyId");
        List<String> dataResouceIds = DataServices.storeAllDataResourceInMap(dispatcher, delegator as GenericDelegator, userLogin, context);
        String fileId = "";
        if(UtilValidate.isNotEmpty(dataResouceIds)){
            fileId = dataResouceIds.get(0);
        }
        String id = delegator.getNextSeqId("TblLibrary");
        Map<String, Object> libraryContext = UtilMisc.toMap("id", id, "title", title, "type", type, "tags", tags, "introduce", introduce, "score", score,
                "accessType", accessType, "status", "R", "dataResourceId", fileId, "userLoginId", userLoginId);
        GenericValue library = delegator.makeValidValue("TblLibrary", libraryContext);
        delegator.create(library);
        successResult.put("msg","success");
    }else if(size==null){
        successResult.put("msg","file");
    }else
    {
        successResult.put("msg","fileSize");
    }

    return successResult;
}

public Map<String, Object> ListFileUploads() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    List<GenericValue> fileUploads = delegator.findByAnd("libraryDataResourceEnum", UtilMisc.toMap("status", "R"));
    successResult.put("data", fileUploads);
    return successResult;
}

public Map<String, Object> ListFileUpload() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String dataResourceId = context.get("fileId");
    Map<String, Object> library = delegator.findByAnd("libraryDataResourceEnum", UtilMisc.toMap("dataResourceId", dataResourceId)).get(0);
    successResult.put("data", library);
    return successResult;
}

public Map<String, Object> changeLibraryStatus() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue)context.get("userLogin");
    String id = context.get("id");
    String status = context.get("status");
    String reason = context.get("reason");
    String msg = "审核成功";
    try {
        GenericValue library = EntityQuery.use(delegator).from("TblLibrary").where(UtilMisc.toMap("id", id)).queryOne();
        if(library != null){
            String partyId = library.get("userLoginId");
            String noticeTitle = "资料[" + library.getString("title") + "] "
            library.set("status", status);
            if (status.equals("N")) {
                library.set("reason", reason);
                noticeTitle += "审核不通过" + (UtilValidate.isNotEmpty(reason) ? ": " + reason : "");
            }else{
                noticeTitle += "审核通过";
                dispatcher.runAsync("sendScoreMessage2",UtilMisc.toMap("scoreTarget",partyId,"eventName","SCORE_RULE_3"));
            }
            library.store();

            String msgTitle = "资料审核结果通知";
            String titleClickAction = "showInfoSticky('" + noticeTitle + "')";
            runService("createSiteMsg", UtilMisc.toMap("partyId", partyId, "title", msgTitle, "titleClickAction", titleClickAction, "type", "notice"));
        }
    }
    catch (GenericEntityException ex) {
        msg = "审核失败";
    }
    successResult.put("msg", msg);
    return successResult;
}

public Map<String, Object> libraryHome() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String type = context.get("type");
    String userLoginId = context.get("userLoginId");
    List<EntityCondition> condList = new ArrayList<>();
    if (UtilValidate.isNotEmpty(type)) {
        condList.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, type))
    }
    if (UtilValidate.isNotEmpty(userLoginId)) {
        condList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
    }
    condList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "Y"))

    List<GenericValue> list = delegator.findList("libraryDataResourceEnum", EntityCondition.makeCondition(condList), null, null, null, false);

    Map<String, List<GenericValue>> resultMap = list.groupBy({ x -> x.getString("userLoginId") });
    Set<String> userLoginIds = resultMap.keySet();
    List<GenericValue> partyInfos = EntityQuery.use(delegator).from("UserLoginAndPartyDetails").where(EntityCondition.makeCondition("partyId", EntityOperator.IN, userLoginIds)).queryList();
    Map<String, GenericValue> partyInfoByUserLogin = new HashMap<>();
    String loginId = context.get("userLogin").get("userLoginId");
    for (GenericValue info : partyInfos) {
        Map infos = new HashMap();
        infos.put("partyId",info.get("partyId"));
        infos.put("partyTypeId",info.get("partyTypeId"));
        infos.put("createdDate",info.get("createdDate"));
        infos.put("statusId",info.get("statusId"));
        infos.put("groupName",info.get("groupName"));
        infos.put("userLoginId",info.get("userLoginId"));
        infos.put("enabled",info.get("enabled"));
        infos.put("fullName",info.get("fullName"));
        if(info.get("userLoginId").equals(loginId) || loginId.equals("admin"))
        {
            infos.put("ishide","true");
        }else
        {
            GenericValue login = EntityQuery.use(delegator).from("UserLoginAndPartyDetails").where(UtilMisc.toMap("userLoginId",loginId)).queryOne();
            String roleType = login.get("partyTypeId");
            if(roleType.equals("PARTY_GROUP"))
            {
                infos.put("ishide","true");
            }else
            {
                infos.put("ishide","false");
            }
        }
        partyInfoByUserLogin.put(info.getString("partyId"), infos);
    }
    List<GenericValue> listEnum = delegator.findByAnd("Enumeration",UtilMisc.toMap("enumTypeId","LIBRARY_TYPE"));

    Map map = new HashMap();
    map.put("listEnum",listEnum);
    map.put("resultMap",resultMap);
    map.put("type",type);
    map.put("partyInfoByUserLogin",partyInfoByUserLogin);
    successResult.put("libraryData", map);
    return successResult;
}

public Map<String, Object> newFileUpload() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    List<GenericValue> list = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LIBRARY_TYPE"));
    successResult.put("data", list);
    String fileSize = EntityUtilProperties.getPropertyValue("content.properties", "fileSize", "localhost", delegator);
    successResult.put("fileSize", fileSize);
    return successResult;
}

/**
 * 搜索资料库文件信息
 * @return
 */
public Map<String, Object> searchLibraryFileListByName(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String isLibrary = context.get("isLibrary");
    String fileName = context.get("fileName");
    String dataType = context.get("libraryDataType");
    List<EntityExpr> conditionList = new ArrayList<>();
    conditionList.add(EntityCondition.makeCondition("dataResourceName", EntityOperator.LIKE, "%" + fileName + "%" ));
    conditionList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "Y" ));
    if(UtilValidate.isNotEmpty(dataType)){
        conditionList.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, dataType.trim()));
    }
    List<GenericValue> fileList = EntityQuery.use(delegator).from("libraryDataResourceEnum").where(conditionList).orderBy("userLoginId").queryList();
    List<Map<String,Object>> fileMapList = new ArrayList<>();
    for(GenericValue genericValue : fileList){
        Map<String, Object> map = new HashMap<>();
        map.put("fileId",genericValue.get("dataResourceId"));
        map.put("fileName",genericValue.get("dataResourceName"));
        map.put("createFullName",genericValue.get("fullName"));
        map.put("fileVersion","");
        map.put("filePath","");
        map.put("createTime",genericValue.get("createdStamp"));
        //下载检查积分用的id
        map.put("id",genericValue.get("id"));
        //标识，用于区分是资料库，还是文档管理
        map.put("isLibrary","Y");
        fileMapList.add(map);
    }
    successResult.put("data",fileMapList);
    return successResult;
}

/**
 * 搜索用户积分和下载文件情况
 * @return
 */
public Map<String, Object> searchScoreByUserId(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = (GenericValue)context.get("userLogin");
    String partyId = userLogin.get("partyId");
    String libraryId = context.get("libraryId");
    GenericValue libraryInfo = EntityQuery.use(delegator).from("TblLibrary").where(UtilMisc.toMap("id", libraryId)).queryOne();
    Map<String, Object> map = new HashMap<>();
    //资料存在并且资料库中文件存在
    if(libraryInfo != null && UtilValidate.isNotEmpty(libraryInfo.get("dataResourceId"))){
        String dataResourceId = libraryInfo.get("dataResourceId");
            GenericValue dataResource = EntityQuery.use(delegator).from("DataResource").where(UtilMisc.toMap("dataResourceId",dataResourceId)).queryOne();
            if(UtilValidate.isNotEmpty(dataResource)){
                GenericValue genericValue = EntityQuery.use(delegator).from("TblScoreFileHistory").where(UtilMisc.toMap("partyId",partyId, "fileId",dataResourceId)).queryOne();
                if(UtilValidate.isEmpty(genericValue)){
                    Integer libraryScore = 0;//下载积分
                    Integer userScoreNo = 0;//用户积分
                    Boolean isDownLoad = false;//用户是否允许下载
                    String accessType = "";
                    if(UtilValidate.isNotEmpty(libraryInfo.get("accessType"))){
                        accessType = libraryInfo.get("accessType").toString();
                    }
                    //查找用户积分
                    GenericValue userScore = EntityQuery.use(delegator).from("TblUserScore").where(UtilMisc.toMap("userLoginId", partyId)).queryOne();
                    if(userScore != null){
                        userScoreNo = Integer.parseInt(userScore.get("scoreNow").toString());
                    }
                    //必须存在资料库类型,否则按照没有普通文档处理
                    if(UtilValidate.isNotEmpty(accessType) && accessType.equals("1") &&  UtilValidate.isNotEmpty(libraryInfo.get("score"))){
                        if(partyId.equals(libraryInfo.get("userLoginId"))){
                            map.put("downloadSelf", true);//用户是否允许下载
                            isDownLoad = true;
                        }else{
                            libraryScore = Integer.parseInt(libraryInfo.get("score").toString());
                            isDownLoad = (userScoreNo - libraryScore) >= 0;
                        }
                    }else{
                        isDownLoad = true;
                    }
                    map.put("isDownLoad", isDownLoad);//用户是否允许下载
                    map.put("libraryPartyId", libraryInfo.get("userLoginId"));//上传人
                    map.put("isDownLoad", isDownLoad);//用户是否允许下载
                    map.put("userScoreNo", userScoreNo);//用户积分
                    map.put("libraryScore", libraryScore);//下载所用积分
                    map.put("isyHistory", false);//是否存在该资料
                }else{
                    map.put("isyHistory", true);//是否存在该资料
                }
                map.put("fileName", dataResource.get("dataResourceName"));//文件名称
                map.put("fileId", dataResource.get("dataResourceId"));//用户是否允许下载
                map.put("libraryId", libraryId);//资源Id
                map.put("isyLibrary", true);//是否存在该资料
                map.put("isFile", true);//是否存在文件
            }else{
                map.put("isFile", false);
            }
    }else{
        map.put("isyLibrary", false);
    }
    successResult.put("data", map);
    return successResult;
}