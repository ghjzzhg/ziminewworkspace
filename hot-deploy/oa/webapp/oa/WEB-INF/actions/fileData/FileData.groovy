import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityConditionList
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

import java.text.Format
import java.text.SimpleDateFormat
import java.util.Calendar;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
/**
 * 查询档案资料
 * @return
 */
public Map<String, Object> searchFileData(){
    Locale locale = (Locale) context.get("locale");
    GenericValue userLoginId = context.get("userLogin");
    String partyId=userLoginId.getString("partyId");
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    // 计算当前显示页的最小、最大索引(可能会超出实际条数)
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    String parentTypeName = context.get("parentTypeName");
    String sonTypeName = context.get("sonTypeName");
    String documentStatus = context.get("documentStatus");
    String documentNumber = context.get("documentNumber");
    String documentTitle = context.get("documentTitle");
    java.sql.Timestamp releaseDate = context.get("releaseDate");

    List conditionList = addConditionList(userLoginId,partyId,parentTypeName,sonTypeName,documentStatus,documentNumber,documentTitle,releaseDate);
    EntityConditionList condition = EntityCondition.makeCondition(UtilMisc.toList(conditionList));
    List<GenericValue> fileDataList = new ArrayList<>();
    EntityListIterator fileDataIterator = EntityQuery.use(delegator).select().from("TblFileData").where(condition).queryIterator();
    if(null != fileDataIterator && fileDataIterator.getResultsSizeAfterPartialList() > 0) {
        totalCount = fileDataIterator.getResultsSizeAfterPartialList();
        List<GenericValue> list = fileDataIterator.getPartialList(lowIndex, viewSize);
        Iterator<GenericValue> it = list.iterator();
        while (it.hasNext()) {
            GenericValue  fileData =it.next();

            Map map=new HashMap();
            String auditor=fileData.get("auditor");
            String audit="";
            if(auditor.equals(partyId)==true){
                audit="审核";
            }
            String releaseDepartment = fileData.get("releaseDepartment");
            java.sql.Timestamp rd = fileData.get("releaseDate");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(rd);
            fileData.put("releaseDate",time);
            String issuer = fileData.get("issuer");
            GenericValue issuerPerson = delegator.findOne("Person",UtilMisc.toMap("partyId", issuer),false);
            String issuerName = issuerPerson.get("fullName");
            String edit="";
            if(issuer.equals(partyId)==true){
                edit="编辑";
            }
            fileData.put("issuer",issuerName);
            GenericValue gw = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId", releaseDepartment),false);
            String groupName=gw.get("groupName");
            fileData.put("releaseDepartment",groupName);
            String parentId=fileData.get("parentTypeName");
            String sonId=fileData.get("sonTypeName");
            GenericValue genericValue = delegator.findOne("TblFileType",UtilMisc.toMap("fileTypeId",parentId),false);
            String parentName=genericValue.get("typeName");
            GenericValue genericValue1 = delegator.findOne("TblFileType",UtilMisc.toMap("fileTypeId",sonId),false);
            String sonName=genericValue1.get("typeName");
            fileData.put("parentTypeName",parentName);
            fileData.put("sonTypeName",sonName);
            map.putAll(fileData);
            map.put("audit",audit);
            map.put("edit",edit);
            fileDataList.add(map);
        }
    }
    fileDataIterator.close();
    Map<String,Object> viewData = new HashMap<String,Object>();
    viewData.put("viewIndex",viewIndex);
    viewData.put("highIndex",highIndex);
    viewData.put("totalCount",totalCount);
    viewData.put("viewSize",viewSize);
    viewData.put("parentTypeName",parentTypeName);
    viewData.put("sonTypeName",sonTypeName);
    viewData.put("documentStatus",documentStatus);
    viewData.put("documentNumber",documentNumber);
    viewData.put("documentTitle",documentTitle);
    viewData.put("releaseDate",releaseDate);
    viewData.put("fileDataList",fileDataList);
    successResult.put("data",viewData);
    return successResult;
}

/**
 * 查询档案版本号
 */
public Map<String,Object> showFileDataVersion(){
    Map<String,Object> successResult = ServiceUtil.returnSuccess();
    String fileDataId = context.get("fileDataId");
    String oldFileDataId = context.get("oldFileDataId");
    List<EntityCondition> conditionList = FastList.newInstance();
    if(UtilValidate.isNotEmpty(fileDataId)){
        if (UtilValidate.isNotEmpty(oldFileDataId)){
            conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("fileDataId",EntityOperator.EQUALS,oldFileDataId),
                                                             EntityCondition.makeCondition("oldFileDataId",EntityOperator.EQUALS,oldFileDataId)],EntityOperator.OR));
        } else {
            conditionList.add(EntityCondition.makeCondition("fileDataId",EntityOperator.EQUALS,fileDataId));
        }
    }
    List<GenericValue> fileDataVersionList = EntityQuery.use(delegator)
            .select()
            .from("FileDataInfo")
            .where(conditionList)
            .orderBy("createdStamp")
            .queryList();
    Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    for (Map map : fileDataVersionList){
        map.put("createdStamp",format.format(map.get("createdStamp")));
    }
    Map map = new HashMap();
    map.put("list",fileDataVersionList);
    successResult.put("data",map);
    return successResult;
}


/**
 * 档案资料查看权限
 * @param userLoginId
 * @param partyId
 * @param parentTypeName
 * @param sonTypeName
 * @param documentStatus
 * @param documentNumber
 * @param documentTitle
 * @param releaseDate
 * @return
 */
public List addConditionList(GenericValue userLoginId, String partyId, String parentTypeName, String sonTypeName, String documentStatus, String documentNumber, String documentTitle, java.sql.Timestamp releaseDate){
    List<EntityCondition> conditionList = FastList.newInstance();
    //查询当前登陆人可查看的档案编号
    Map<String,Object> fileDataMap = dispatcher.runSync("verifyViewPermissions",UtilMisc.toMap("entityName","TblFileData","isSelect",false,"partyId",partyId,"userLogin",userLoginId));
    Map<String,Object> partyInfo = (HashMap<String,Object>)fileDataMap.get("data");
    List<Map<String,Object>> entityDataList = new ArrayList<Map<String,Object>>();
    if(null != partyInfo){
        entityDataList = (List<FastMap<String,Object>>)partyInfo.get("entityDataList");
    }
    List list = new ArrayList();
    for(Map<String ,Object> map : entityDataList){
        list.add(map.get("dataId"));
    }
    //查询需确认浏览人员是否存在当前登陆人
    List<GenericValue> partyList = EntityQuery.use(delegator).select().from("TblFileDataParty").where(UtilMisc.toMap("partyId",partyId)).queryList();
    for(GenericValue map : partyList){
        list.add(map.get("fileDataId"));
    }
    //查询发布人是否存在当前登陆人
    List<GenericValue> issuerList = EntityQuery.use(delegator).select().from("TblFileData").where(UtilMisc.toMap("issuer",partyId)).queryList();
    for(GenericValue map : issuerList){
        list.add(map.get("fileDataId"));
    }
    //查询审核人是否存在当前登陆人
    List<GenericValue> auditorList = EntityQuery.use(delegator).select().from("TblFileData").where(UtilMisc.toMap("auditor",partyId)).queryList();
    for(GenericValue map : auditorList){
        list.add(map.get("fileDataId"));
    }
    Set set = new HashSet();
    List newList = new ArrayList();
    Iterator it = list.iterator();
    while (it.hasNext()){
        String Id = it.next();
        if (set.add(Id)){
            newList.add(Id);
        }
    }
    list.clear();
    list.addAll(newList);
    if(UtilValidate.isNotEmpty(list)){
        conditionList.add(EntityCondition.makeCondition("fileDataId", EntityOperator.IN, list));
    }
    List<Object> timeCondition = FastList.newInstance();
    if(UtilValidate.isNotEmpty(parentTypeName)){
        conditionList.add(EntityCondition.makeCondition("parentTypeName",EntityOperator.EQUALS,parentTypeName));
    }
    if(UtilValidate.isNotEmpty(sonTypeName)){
        conditionList.add(EntityCondition.makeCondition("sonTypeName",EntityOperator.EQUALS,sonTypeName));
    }
    if(UtilValidate.isNotEmpty(documentStatus)){
        conditionList.add(EntityCondition.makeCondition("documentStatus",EntityOperator.EQUALS,documentStatus));
    }
    if(UtilValidate.isNotEmpty(documentNumber)){
        conditionList.add(EntityCondition.makeCondition("documentNumber",EntityOperator.LIKE,"%"+documentNumber+"%"));
    }
    if(UtilValidate.isNotEmpty(documentTitle)){
        conditionList.add(EntityCondition.makeCondition("documentTitle",EntityOperator.LIKE,"%"+documentTitle+"%"));
    }
    if(UtilValidate.isNotEmpty(releaseDate)){
        Map releaseDateMap =formatReleaseDate(releaseDate);
        java.sql.Timestamp startDate= releaseDateMap.get("startDate");
        java.sql.Timestamp endDate= releaseDateMap.get("endDate");
        timeCondition.add(startDate);
        timeCondition.add(endDate);
        conditionList.add(EntityCondition.makeCondition("releaseDate",EntityOperator.BETWEEN,timeCondition));
    }
    conditionList.add(EntityCondition.makeCondition("newVersion",EntityOperator.EQUALS,"Y"));
    return conditionList;
}
/**
 * 保存档案资料
 * @return
 */
public Map<String, Object> saveFileData() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLoginId = context.get("userLogin");
    String fileDataId = context.get("fileDataId");
    String status = context.get("status");
    String msg = "";
    Format format = new SimpleDateFormat("yyyyMMddHHmmss");
    Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblFileData","numName","documentNumber","prefix","fileData","userLogin",userLogin));
    String documentNumber = uniqueNumber.get("number");
    try {
        if(UtilValidate.isNotEmpty(fileDataId)){
            Map map = new HashMap();
            map.put("fileDataId", fileDataId);
            map.put("parentTypeName", context.get("parentTypeName"));
            map.put("sonTypeName", context.get("sonTypeName"));
            map.put("documentTitle", context.get("documentTitle"));
            map.put("feedback", context.get("feedback"));
            map.put("documentStatus", context.get("documentStatus"));
            map.put("releaseDepartment", context.get("releaseDepartment"));
            map.put("releaseDate", context.get("releaseDate"));
            map.put("auditor", context.get("auditor"));
            map.put("remarks", context.get("remarks"));
            map.put("documentContent", context.get("documentContent"));
            if (UtilValidate.isNotEmpty(status) && status.equals("审核通过")){
                String browseStaff = context.get("browseStaff");
                String documentPublishingRange = context.get("documentPublishingRange_dept_only");
                Map editMap = new HashMap();
                editMap.put("fileDataId", fileDataId);
                editMap.put("newVersion","N");
                delegator.store(delegator.makeValidValue("TblFileData",editMap));
                map.put("issuer", userLoginId.getString("partyId"));
                map.put("documentNumber", documentNumber);
                map.put("status", "待审核");
                if (UtilValidate.isNotEmpty(context.get("oldFileDataId"))){
                    map.put("oldFileDataId",context.get("oldFileDataId"));
                } else {
                    map.put("oldFileDataId",fileDataId);
                }
                fileDataId = delegator.getNextSeqId("TblFileData").toString();//获取主键ID
                map.put("fileDataId",fileDataId)
                map.put("versionNumber",context.get("versionNumber"));
                map.put("newVersion","Y");
                delegator.create(delegator.makeValidValue("TblFileData",map));
                if(UtilValidate.isNotEmpty(browseStaff)){
                    saveBrowseStaff(browseStaff,fileDataId);
                }
                if(UtilValidate.isNotEmpty(documentPublishingRange)){
                    saveDocumentPublishingRange(documentPublishingRange,fileDataId);
                }
                Map documentPublishingRangeMap= searchDocumentPublishingRange(fileDataId);
                String documentPublishingRanges = documentPublishingRangeMap.get("documentPublishingRange");
                if(documentPublishingRanges.equals(documentPublishingRange)==false){
                    delegator.removeByAnd("TblDataScope",UtilMisc.toMap("dataId",fileDataId));
                    if(UtilValidate.isNotEmpty(documentPublishingRange)){
                        saveDocumentPublishingRange(documentPublishingRange,fileDataId);
                    }
                }
                Map browseStaffMap = searchBrowseStaff(fileDataId);
                String browseStaffs = browseStaffMap.get("browseStaff");
                if(browseStaffs.equals(browseStaff)==false){
                    delegator.removeByAnd("TblFileDataParty",UtilMisc.toMap("fileDataId",fileDataId));
                    if(UtilValidate.isNotEmpty(browseStaff)){
                        saveBrowseStaff(browseStaff,fileDataId);
                    }
                }
            }else {
                String browseStaff = context.get("browseStaff");
                String documentPublishingRange = context.get("documentPublishingRange_dept_only");
                map.put("documentNumber", context.get("documentNumber"));
                delegator.store(delegator.makeValidValue("TblFileData",map));
                Map documentPublishingRangeMap= searchDocumentPublishingRange(fileDataId);
                String documentPublishingRanges = documentPublishingRangeMap.get("documentPublishingRange");
                if(documentPublishingRanges.equals(documentPublishingRange)==false){
                    delegator.removeByAnd("TblDataScope",UtilMisc.toMap("dataId",fileDataId));
                    if(UtilValidate.isNotEmpty(documentPublishingRange)){
                        saveDocumentPublishingRange(documentPublishingRange,fileDataId);
                    }
                }
                Map browseStaffMap = searchBrowseStaff(fileDataId);
                String browseStaffs = browseStaffMap.get("browseStaff");
                if(browseStaffs.equals(browseStaff)==false){
                    delegator.removeByAnd("TblFileDataParty",UtilMisc.toMap("fileDataId",fileDataId));
                    if(UtilValidate.isNotEmpty(browseStaff)){
                        saveBrowseStaff(browseStaff,fileDataId);
                    }
                }
            }
            msg = "更新成功";
        }else{
            fileDataId = delegator.getNextSeqId("TblFileData").toString();//获取主键ID
            String browseStaff = context.get("browseStaff");
            String documentPublishingRange = context.get("documentPublishingRange_dept_only");
            Map map = new HashMap();
            map.put("fileDataId", fileDataId);
            map.put("issuer", userLoginId.getString("partyId"));
            map.put("parentTypeName", context.get("parentTypeName"));
            map.put("sonTypeName", context.get("sonTypeName"));
            map.put("documentTitle", context.get("documentTitle"));
            map.put("documentNumber", context.get("documentNumber"));
            map.put("feedback", context.get("feedback"));
            map.put("versionNumber",context.get("versionNumber"));
            map.put("documentStatus", context.get("documentStatus"));
            map.put("releaseDepartment", context.get("releaseDepartment"));
            map.put("releaseDate", context.get("releaseDate"));
            map.put("auditor", context.get("auditor"));
            map.put("remarks", context.get("remarks"));
            map.put("documentContent", context.get("documentContent"));
            map.put("status", "待审核");
            map.put("newVersion","Y");
            delegator.create(delegator.makeValidValue("TblFileData",map));
            if(UtilValidate.isNotEmpty(browseStaff)){
                saveBrowseStaff(browseStaff,fileDataId);
            }
            if(UtilValidate.isNotEmpty(documentPublishingRange)){
                saveDocumentPublishingRange(documentPublishingRange,fileDataId);
            }
            msg="保存成功"
        }
        String fileId = context.get("fileId");
        List<String> newFileList = new ArrayList<String>();
        if(null != fileId && !"".equals(fileId)){
            String[] files = fileId.split(",");
            for(String file : files){
                newFileList.add(file);
            }
        }
        List<String> oldFileList = new ArrayList<String>();
        List<GenericValue> workAccessFile = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId", fileDataId, "entityName", "tblFileData"))).queryList();
        if(null != workAccessFile && workAccessFile.size() > 0){
            for(GenericValue workAccess : workAccessFile){
                oldFileList.add(workAccess.get("accessoryId").toString());
            }
        }
        saveFile(fileDataId,oldFileList,newFileList);
    }catch (Exception e){
        msg="保存失败"
        return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                msg,
                UtilMisc.toMap("errMessage", e.getMessage()), locale));
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
public void saveFile(String fileDataId,List<String> oldFileList,List<String> newFileList){
    for(String fileId:newFileList) {
        if (!oldFileList.contains(fileId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            String fileScopeId = delegator.getNextSeqId("TblFileScope");
            map.put("fileScopeId", fileScopeId);
            map.put("entityName", "tblFileData");
            map.put("dataId", fileDataId);
            map.put("accessoryId", fileId);
            GenericValue accessory = delegator.makeValidValue("TblFileScope", map);
            accessory.create();
        }
    }
    for(String fileid:oldFileList){
        if(!newFileList.contains(fileid)) {
            delegator.removeByAnd("TblFileScope", UtilMisc.toMap("entityName", "tblFileData", "dataId", fileDataId, "accessoryId", fileid));
            GenericValue fileData = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId", fileid)).queryOne();
            if (null != fileData && "ATTACHMENT_FILE".equals(fileData.get("dataResourceTypeId").toString())) {
                File file = new File(fileData.get("parentObjectInfo").toString() + fileData.get("dataResourceName").toString());
                if (file.exists()) {
                    file.delete();
                }
                delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", fileid));
            }
        }
    }
}
/**
 * 保存审核
 * @return
 */
public Map<String, Object> saveAudit() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String fileDataId = context.get("fileDataId");
    String msg = "";
    try {
        if(UtilValidate.isNotEmpty(fileDataId)){
            Map map = new HashMap();
            map.put("fileDataId", fileDataId);
            map.put("status", context.get("status"));
            map.put("auditContent", context.get("auditContent"));
            delegator.store(delegator.makeValidValue("TblFileData",map));

            msg = "审核成功";
        }
    }catch (Exception e){
        msg="审核失败"
        return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                msg,
                UtilMisc.toMap("errMessage", e.getMessage()), locale));
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
/**
 * 删除档案资料
 * @return
 */
public Map<String,Object> deleteFileData(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String fileDataId = context.get("fileDataId");
    if(UtilValidate.isNotEmpty(fileDataId)){
        List<GenericValue> list = EntityQuery.use(delegator).select().from("TblFileScope").where(EntityCondition.makeCondition(UtilMisc.toMap("dataId",fileDataId, "entityName", "tblFileData"))).queryList();
        delegator.removeByAnd("TblFileScope",UtilMisc.toMap("dataId",fileDataId, "entityName", "tblFileData"));
        delegator.removeByAnd("TblDataScope",UtilMisc.toMap("dataId",fileDataId));
        delegator.removeByAnd("TblFileDataParty",UtilMisc.toMap("fileDataId",fileDataId));
        delegator.makeValidValue("TblFileData","fileDataId",fileDataId).remove();
        Iterator it = list.iterator();
        while (it.hasNext()){
            GenericValue map = it.next();
            String accessoryId =map.get("accessoryId");
            GenericValue data = EntityQuery.use(delegator).select().from("DataResource").where(EntityCondition.makeCondition("dataResourceId",accessoryId)).queryOne();
            if("ATTACHMENT_FILE".equals(data.get("dataResourceTypeId").toString())){
                File file = new File(data.get("objectInfo").toString()+data.get("dataResourceName").toString());
                if(file.exists()){
                    file.delete();
                }
                delegator.removeByAnd("DataResource",UtilMisc.toMap("dataResourceId",data.get("dataResourceId")));
            }
        }
    }
    successResult.put("msg","删除成功！");
    return successResult;
}
/**
 * 修改档案资料时做显示作用
 * @return
 */
public Map<String,Object> modifyFileData(){
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String fileDataId = context.get("fileDataId");
    Map map = new HashMap();
    if(UtilValidate.isNotEmpty(fileDataId)){
        GenericValue genericValue = delegator.findOne("TblFileData",UtilMisc.toMap("fileDataId", fileDataId),false);
        String parentId=genericValue.get("parentTypeName");
        String sonId=genericValue.get("sonTypeName");
        GenericValue genericValue1 = delegator.findOne("TblFileType",UtilMisc.toMap("fileTypeId",parentId),false);
        String parentName=genericValue1.get("typeName");
        GenericValue genericValue2 = delegator.findOne("TblFileType",UtilMisc.toMap("fileTypeId",sonId),false);
        List<GenericValue> fileTypes = EntityQuery.use(delegator).select().from("TblFileType").where(EntityCondition.makeCondition("parentId",EntityOperator.EQUALS,parentId)).queryList();
        map.put("list",fileTypes);
        String sonName=genericValue2.get("typeName");
        map.put("parentName",parentName);
        map.put("sonName",sonName);
        String fileId = searchAccessory(fileDataId);
        if(!"".equals(fileId)){
            map.put("fileId",fileId);
        }else{
            map.put("fileId","");
        }
        java.sql.Timestamp rd = genericValue.get("releaseDate");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(rd);
        map.put("releaseDates",time);
        String auditor = genericValue.get("auditor");
        GenericValue auditorPerson = delegator.findOne("Person",UtilMisc.toMap("partyId", auditor),false);
        String auditorName = auditorPerson.get("fullName");
        map.put("auditorName",auditorName);
        Map documentPublishingRangeMap= searchDocumentPublishingRange(fileDataId);
        String documentPublishingRange = documentPublishingRangeMap.get("documentPublishingRange");
        String groupName=documentPublishingRangeMap.get("groupName");
        Map browseStaffMap = searchBrowseStaff(fileDataId);
        String browseStaff = browseStaffMap.get("browseStaff");
        String fullName = browseStaffMap.get("fullName");
        map.put("browseStaff",browseStaff);
        map.put("fullName",fullName);
        map.put("groupName",groupName);
        map.put("documentPublishingRange",documentPublishingRange);
        GenericValue gw = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",genericValue.get("releaseDepartment")),false);
        String groupNames=gw.get("groupName");
        map.put("groupNames",groupNames);
        String feedback1 ="";
        if(UtilValidate.isNotEmpty(genericValue.get("feedback"))){
            GenericValue gw1 =EntityQuery.use(delegator).from("Enumeration").where(UtilMisc.toMap("enumId",genericValue.get("feedback"),"enumTypeId","WHETHER_FEEDBACK")).queryOne();
            feedback1 =gw1.get("description");
        }
        String documentStatus1 ="";
        if(UtilValidate.isNotEmpty(genericValue.get("documentStatus"))){
            GenericValue gw2 = EntityQuery.use(delegator).from("Enumeration").where(UtilMisc.toMap("enumId",genericValue.get("documentStatus"),"enumTypeId","DOCUMENT_STATUS")).queryOne();
            documentStatus1 = gw2.get("description");
        }
        map.put("feedback1",feedback1);
        map.put("documentStatus1",documentStatus1);
        Format format1 = new SimpleDateFormat("yyyMMddHHmmss");
        genericValue.put("versionNumber",format1.format(new Date()));
        map.putAll(genericValue);
    }
    String fileId = map.get("fileId");
    if (null != fileId && !"".equals(fileId)) {
        context.put("files",fileId);

        Map data = runService("searchFileByIds", dispatcher.getDispatchContext().getModelService("searchFileByIds").makeValid(context, ModelService.IN_PARAM));
        Map filemap = data.get("data");
        map.put("fileId",filemap.get("fileIds"));
        map.put("fileList",filemap.get("fileList"));
    }else{
        map.put("fileList","");
    }
    successResult.put("fileDataMap",map);
    return successResult;
}
/**
 * 保存需确认浏览人员
 * @param browseStaff
 * @param fileDataId
 */
public void saveBrowseStaff(String browseStaff,String fileDataId){
    String[] partyIdArray = browseStaff.split(",");
    for(String partyId : partyIdArray){
        Map fileDataPartyMap = new HashMap();
        fileDataPartyMap.put("fileDataId", fileDataId);
        fileDataPartyMap.put("partyId",partyId);
        delegator.create(delegator.makeValidValue("TblFileDataParty",fileDataPartyMap));
    }
}
/**
 * 保存附件
 * @param fileId
 * @param fileDataId
 */
public void saveAccessory(String fileId,String fileDataId){
    String[] fileArray = fileId.split(",");
    for(String Id : fileArray){
        Map fileDatafileAccessoryMap = new HashMap();
        String scopeId = delegator.getNextSeqId("TblFileScope").toString();//获取主键ID
        fileDatafileAccessoryMap.put("fileScopeId", scopeId);
        fileDatafileAccessoryMap.put("dataId", fileDataId);
        fileDatafileAccessoryMap.put("accessoryId",Id);
        fileDatafileAccessoryMap.put("entityName","tblFileData")
        delegator.create(delegator.makeValidValue("TblFileScope",fileDatafileAccessoryMap));
    }
}
/**
 * 保存文档发布范围
 * @param documentPublishingRange
 * @param fileDataId
 */
public void saveDocumentPublishingRange(String documentPublishingRange,String fileDataId){
    String[] documentPublishingRangeArray = documentPublishingRange.split(",");
    for(String Id : documentPublishingRangeArray){
        String scopeId = delegator.getNextSeqId("TblDataScope").toString();//获取主键ID
        Map documentPublishingRangeMap = new HashMap();
        documentPublishingRangeMap.put("scopeId", scopeId);
        documentPublishingRangeMap.put("dataId", fileDataId);
        documentPublishingRangeMap.put("dataAttr","view");
        documentPublishingRangeMap.put("scopeType","SCOPE_DEPT_ONLY");
        documentPublishingRangeMap.put("scopeValue",Id);
        documentPublishingRangeMap.put("entityName","tblFileData")
        delegator.create(delegator.makeValidValue("TblDataScope",documentPublishingRangeMap));
    }
}
/**
 * 通过fileDataId查找附件
 * @param fileDataId
 * @return
 */
public String searchAccessory(String fileDataId){
    List<GenericValue> list =new ArrayList<>();
    list =delegator.findByAnd("TblFileScope",UtilMisc.toMap("dataId", fileDataId,"entityName","tblFileData"));
    Iterator<GenericValue> it = list.iterator();
    String fileId ="";
    while (it.hasNext()) {
        GenericValue  accessory =it.next();
        String accessoryId =accessory.get("accessoryId");
        fileId =fileId+accessoryId+",";
    }
    if(fileId.length()>0){
        return fileId.substring(0,fileId.length()-1);
    }else{
        return fileId;
    }

}
/**
 * 通过fileDataId查找文档发布范围
 * @param fileDataId
 * @return
 */
public Map searchDocumentPublishingRange(String fileDataId){
    List<GenericValue> list =new ArrayList<>();
    list =delegator.findByAnd("TblDataScope",UtilMisc.toMap("dataId", fileDataId,"entityName","tblFileData"));
    Iterator<GenericValue> it = list.iterator();
    Map map = new HashMap();
    String documentPublishingRange ="";
    String groupName="";
    while (it.hasNext()) {
        GenericValue gv =it.next();
        String scopeValue=gv.get("scopeValue");
        documentPublishingRange =documentPublishingRange+scopeValue+",";
        GenericValue gw = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId", scopeValue),false);
        String name=gw.get("groupName");
        groupName=groupName+name+" ";
    }
    map.put("documentPublishingRange",documentPublishingRange.substring(0,documentPublishingRange.length()-1));
    map.put("groupName",groupName.trim());
    return map;
}
/**
 * 通过fileDataId查找需确认浏览人员
 * @param fileDataId
 * @return
 */
public Map searchBrowseStaff(String fileDataId){
    List<GenericValue> list =new ArrayList<>();
    list =delegator.findByAnd("TblFileDataParty",UtilMisc.toMap("fileDataId", fileDataId));
    Iterator<GenericValue> it = list.iterator();
    Map map = new HashMap();
    String browseStaff ="";
    String fullName="";
    while (it.hasNext()) {
        GenericValue gv =it.next();
        String partyId=gv.get("partyId");
        browseStaff =browseStaff+partyId+",";
        GenericValue gw = delegator.findOne("Person",UtilMisc.toMap("partyId", partyId),false);
        String name=gw.get("fullName");
        fullName=fullName+name+" ";
    }
    map.put("browseStaff",browseStaff.substring(0,browseStaff.length()-1));
    map.put("fullName",fullName.trim());
    return map;
}
public Map formatReleaseDate(java.sql.Timestamp releaseDate){
    Map map = new HashMap();
    java.util.Date date = new java.util.Date(releaseDate.getTime())
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.MILLISECOND, 001);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date);
    cal2.set(Calendar.HOUR_OF_DAY, 23);
    cal2.set(Calendar.SECOND, 59);
    cal2.set(Calendar.MINUTE, 59);
    cal2.set(Calendar.MILLISECOND, 999);
    map.put("startDate",new Timestamp(cal.getTimeInMillis()));
    map.put("endDate",new Timestamp(cal2.getTimeInMillis()));
    return map;
}



