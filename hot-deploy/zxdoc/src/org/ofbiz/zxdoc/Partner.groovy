import javolution.util.FastList
import javolution.util.FastSet
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.minilang.method.conditional.Conditional
import org.ofbiz.service.ModelService
import org.ofbiz.service.ServiceUtil

import java.nio.ByteBuffer

public Map<String, Object> addPartner() {
    Map<String, Object> data = ServiceUtil.returnSuccess();
    String customerName = context.get("customerName");
    String area = context.get("area");
    String address = context.get("address");
    String webAddress = context.get("webAddress");
    String customerLevel = context.get("customerLevel");
    String customerStatus = context.get("customerStatus");
    String remarks = context.get("remarks");
    String contactPerson = context.get("contactPerson");
    String contactNumber = context.get("contactNumber");
    String userLoginId = ((GenericValue) context.get("userLogin")).get("userLoginId");
//创建一个party-group类型的party
//创建一个party-group
//    GenericValue groupName = delegator.makeValidValue("PartyGroup", UtilMisc.toMap("groupName",customerName));
//    Map<String, Object> resultGroup = runService("createPartyGroup", dispatcher.getDispatchContext().getModelService("createPartyGroup").makeValid(groupName, ModelService.IN_PARAM));
    Map<String, Object> resultGroup = dispatcher.runSync("createPartyGroup", UtilMisc.toMap("groupName", customerName));
    String customerPartyId = resultGroup.get("partyId");
//创建客户的企业网址联系方式
    dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", customerPartyId, "contactMechTypeId", "WEB_ADDRESS", "infoString", webAddress==null?"":webAddress, "userLogin", userLogin));
//创建客户的区域地址联系方式
    dispatcher.runSync("createPartyPostalAddress", UtilMisc.toMap("partyId", customerPartyId, "contactMechTypeId", "POSTAL_ADDRESS",
            "city", "", "postalCode", area==null?"":area, "address1", address==null?"":address, "userLogin", userLogin));
//创建客户的状态、等级、备注
    Map<String, Object> partner = UtilMisc.toMap("partyId", customerPartyId, "customerLevel", customerLevel==null?"":customerLevel, "customerStatus", customerStatus==null?"":customerStatus, "remarks", remarks==null?"":remarks, "userLoginId", userLoginId, "status", "Y");
    String id = delegator.getNextSeqId("TblCustomer");
    partner.put("id",id);
    GenericValue customer = delegator.makeValidValue("TblCustomer", partner);
    GenericValue createdcustomer = delegator.create(customer);
//创建一个person类型的party
//创建一个person
    Map<String, Object> resultPerson = dispatcher.runSync("createPerson", UtilMisc.toMap("firstName", contactPerson));
    String contactPersonId = resultPerson.get("partyId");
//创建联系人的电话联系方式
    dispatcher.runSync("createPartyTelecomNumber", UtilMisc.toMap("partyId", contactPersonId, "contactNumber", contactNumber, "userLogin", userLogin));
//创建客户企业之间的联系
    dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("partyIdFrom", customerPartyId, "partyIdTo", contactPersonId, "partyRelationshipTypeId", "ORG_CONTACT", "userLogin", userLogin));
    return data;
}
//提供搜索条件的下拉框
public Map<String, Object> searchPartner() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    List<GenericValue> listLevel = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "CUSTOMER_LEVEL"), null, false);
    List<GenericValue> listStatus = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "CUSTOMER_STATUS"), null, false);
    Map map = new HashMap();
    map.put("mapList2", listLevel);
    map.put("mapList3", listStatus);
    successResult.put("data", map);
    return successResult;
}

public Map<String, Object> editPartner() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String partyId = context.get("partyId");
    String id = context.get("id");
    Map map = new HashMap();
    def postalCode;
    StringBuffer site = new StringBuffer();
    String partyRelationshipTypeId = "";
    if (id != null) {
        List<EntityCondition> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition("id", EntityOperator.EQUALS, id));
        //changed by galaxypan@2017-09-05:已有id过滤，下面条件多余
        /*conditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"ORG_CONTACT"),
                EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"ORG_LINK_CONTACT")), EntityOperator.OR));*/
        Map<String, Object> partner = delegator.findList("PartnerSearchInfo", EntityCondition.makeCondition(conditions), null, null, null, false).get(0);
        //通过level和status获取相关描述，该修改修复页面错误数据的展示，根本原因在于视图配置问题
        Map partnerMap = new HashMap();
        partnerMap.putAll(partner);
        String level = partner.get("customerLevel");
        if(level!=null&&level!=""){
            GenericValue levelEnum = EntityQuery.use(delegator).from("Enumeration").where("enumId",level,"enumTypeId","CUSTOMER_LEVEL").queryOne();
            partnerMap.put("descriptionLevel",levelEnum.get("description"));
        }else
        {
            partnerMap.put("descriptionLevel","");
        }
        String status = partner.get("customerStatus");
        if(status!=null&&status!=""){
            GenericValue levelEnum = EntityQuery.use(delegator).from("Enumeration").where("enumId",status,"enumTypeId","CUSTOMER_STATUS").queryOne();
            partnerMap.put("descriptionStatus",levelEnum.get("description"));
        }else
        {
            partnerMap.put("descriptionStatus","");
        }
        Set<String> fieldsToSelect = FastSet.newInstance();
        fieldsToSelect.add("partyIdTo");
        //这串代码用于控制合同生成的客户名不能编辑
        List<GenericValue> total = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdFrom",partyId).queryList();
        List<String> personIds = new ArrayList<>();
        for (GenericValue a : total) {
            String partyIdTo = a.get("partyIdTo");
            personIds.add(partyIdTo);
        }
        List<GenericValue> list = EntityQuery.use(delegator).select().from("PartyRelationship").
                where(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, personIds),EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"ORG_LINK_CONTACT")).queryList();
        if(list.size()!=0)
        {
            partyRelationshipTypeId = "ORG_LINK_CONTACT";
        }
        //获取客户信息
        List<GenericValue> List = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdFrom",partyId,"partyRelationshipTypeId","ORG_CONTACT").queryList();
        //把 List<GenericValue> List结果集遍历成List<String>类型
        List<String> personIdList = new ArrayList<>();
        for (GenericValue a : List) {
            String partyIdTo = a.get("partyIdTo");
            personIdList.add(partyIdTo);
        }
        List<GenericValue> personInfo = EntityQuery.use(delegator).select().from("CustomerContactTelephone").
                where(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, personIdList),EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"ORG_CONTACT")).distinct().queryList();
        //从PartyContactMech这张表中获取地址编码
        postalCode = EntityQuery.use(delegator).select("area").from("PostalAddressInfo").
                where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)).queryList().get(0).get("area");
        if(UtilValidate.isNotEmpty(postalCode)){
            GenericValue provincial = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",postalCode)).queryOne()
            String cityCode =  postalCode.substring(0,4) + "00";
            GenericValue city = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",cityCode)).queryOne()
            String areaCode =  postalCode.substring(0,2) + "0000";
            GenericValue area = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode",areaCode)).queryOne()
            if(area != null && city!= null && provincial!=null){
                site.append(area.get("geoName")).append("/");
                site.append(city.get("geoName")).append("/");
                site.append(provincial.get("geoName"));
            }
            if(city != null && area !=null && provincial==null){
                site.append(area.get("geoName")).append("/");
                site.append(city.get("geoName"));
            }
            if(provincial == null && area != null && city == null){
                site.append(area.get("geoName"));
            }
            //偏远地区省市区特例
            if(area != null && city == null && provincial!=null)
            {
                site.append(area.get("geoName")).append("/");
                site.append(provincial.get("geoName"));
            }
        }
        map.put("personInfo", personInfo);
        map.put("partner", partnerMap);
    }
//从Enumeration这张表中拿出CUSTOMER_LEVEL顾客等级；
    List<GenericValue> listLevel = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "CUSTOMER_LEVEL"), null, false);
//从Enumeration这张表中拿出CUSTOMER_STATUS顾客状态；
    List<GenericValue> listStatus = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "CUSTOMER_STATUS"), null, false);
    Map dataMap = new HashMap();
    dataMap.put("mapList2", listLevel);
    dataMap.put("mapList3", listStatus);
    dataMap.put("map", map);
    dataMap.put("id", partyId);
    dataMap.put("site", site);
    dataMap.put("partyRelationshipTypeId", partyRelationshipTypeId);
    dataMap.put("postalCode", postalCode);
    successResult.put("data", dataMap);
    return successResult;
}

public Map<String, Object> savePartner() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String id = context.get("id");
    String partyId = context.get("partyId");
    String customerName = context.get("customerName");
    String area = context.get("area");
    String address = context.get("address");
    String webAddress = context.get("webAddress");
    String customerLevel = context.get("customerLevel");
    String customerStatus = context.get("customerStatus");
    String remarks = context.get("remarks");
    String contactPerson = context.get("contactPerson");
    String contactNumber = context.get("contactNumber");
    String partyRelationshipTypeId = context.get("partyRelationshipTypeId");
    if(!partyRelationshipTypeId.equals("ORG_LINK_CONTACT")){
        //更新客户名称
        GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
        if(partyGroup!=null) {
            partyGroup.put("groupName", customerName);
            partyGroup.store();
        }
        //通过partyId拿出两个联系方式的Id
        String contactMechIdInfo = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId), null, false).get(0).get("contactMechId");
        String contactMechIdPA = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId), null, false).get(1).get("contactMechId");
        //更新客户的网址
        GenericValue contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", contactMechIdInfo));
        if(contactMech!=null) {
            contactMech.put("infoString", webAddress);
            contactMech.store();
        }
        //更新客户的区域和地址
        GenericValue postalAdress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", contactMechIdPA));
        if(postalAdress!=null) {
            postalAdress.put("postalCode", area);
            postalAdress.put("address1", address);
            postalAdress.store();
        }
        //设置一个查询，通过客户的partyId查找出联系人的id,在通过联系人的Id个更新联系人的名字和电话号码
        Set<String> fieldsToSelect = FastSet.newInstance();
        fieldsToSelect.add("partyIdTo");
        EntityCondition mainCond =
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId);
        List<GenericValue> personIdList = delegator.findList("PartyRelationship", mainCond, fieldsToSelect, null, null, false);
        for (GenericValue s : personIdList) {
            String personId = s.get("partyIdTo");
            GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", personId));
            if(person!=null) {
                person.put("firstName", contactPerson);
                person.store();
            }
            String contactMechId = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", personId), null, false).get(0).get("contactMechId");
            GenericValue telecomNumber = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId));
            if(telecomNumber!=null) {
                telecomNumber.put("contactNumber", contactNumber);
                telecomNumber.store();
            }
        }
    }
    //更新客户的等级，状态，备注
    GenericValue customer = delegator.findByPrimaryKey("TblCustomer", UtilMisc.toMap("id", id));
    if(customer!=null) {
        customer.put("customerLevel", customerLevel);
        customer.put("customerStatus", customerStatus);
        customer.put("remarks", remarks);
        customer.store();
    }
    return successResult;
}

public Map<String, Object> deletePartner() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String id = context.get("id");
    String partyId = context.get("userLogin").get("partyId");
    GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId",partyId).queryOne();
    String partyTypeId = party.get("partyTypeId");
    if(partyTypeId.equals("PERSON"))
    {
        GenericValue relation = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT").queryOne();
        partyId = relation.get("partyIdFrom");
    }
    GenericValue changeStatus = delegator.findByPrimaryKey("TblCustomer", UtilMisc.toMap("id", id));
    String cusomerId = changeStatus.get("partyId");
    changeStatus.put("status", "N");
    changeStatus.store();
    GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship").where("partyIdTo",partyId,"partyIdFrom",cusomerId,"partyRelationshipTypeId","ORG_LINK_CONTACT").queryOne();
    if(partyRelationship!=null) {
        partyRelationship.remove();
    }
    return successResult;
}

public Map<String, Object> chartPartners() {
    Map<String, Object> successRessult = ServiceUtil.returnSuccess();
    String userLoginId = ((GenericValue) context.get("userLogin")).get("userLoginId");
    List<EntityCondition> condList = new ArrayList<>();
    condList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
    condList.add(EntityCondition.makeCondition("status",EntityOperator.EQUALS,"Y"));
    //changed by galaxypan@2017-09-05:无用的条件
//    condList.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"ORG_CONTACT"));
    Map<String, Object> resultMap = new HashMap<>();
//客户等级维度
    List<GenericValue> list = delegator.findList("PartnerSearchInfo", EntityCondition.makeCondition(condList), null, null, null, false);
//    Map<String, List<GenericValue>> levelMap = list.groupBy ({x->x.getString("descriptionLevel")});
//    resultMap.put("levelMap",levelMap);
//    Map<String, List<GenericValue>> statusMap = list.groupBy ({x->x.getString("descriptionStatus")});
//    resultMap.put("statusMap",statusMap);
    Map<String, Integer> levelMap = new HashMap<>();
    Map<String, Integer> statusMap = new HashMap<>();

    for (int i = 0; i < list.size(); i++) {
        Map groupItem = list.get(i);
        String level = groupItem.get("customerLevel");
        if(level!=null&&level!=""){
            GenericValue levelEnum = EntityQuery.use(delegator).from("Enumeration").where("enumId",level,"enumTypeId","CUSTOMER_LEVEL").queryOne();
            level = levelEnum.get("description");
        }else
        {
            level = "未指定客户等级";
        }
        String status = groupItem.get("customerStatus");
        if(status!=null&&status!=""){
            GenericValue levelEnum = EntityQuery.use(delegator).from("Enumeration").where("enumId",status,"enumTypeId","CUSTOMER_STATUS").queryOne();
            status = levelEnum.get("description");
        }else
        {
            status = "未指定客户状态";
        }
        Integer levelCount = levelMap.get(level);
        if (levelCount == null) {
            levelMap.put(level, 1);
        } else {
            levelMap.put(level, levelCount + 1);
        }
        Integer statusCount = statusMap.get(status);
        if (statusCount == null) {
            statusMap.put(status, 1);
        } else {
            statusMap.put(status, statusCount + 1);
        }
    }
    resultMap.put("levelMap", levelMap);
    resultMap.put("statusMap", statusMap);
    if(levelMap.size() == 0 && statusMap.size() == 0){
        resultMap.put("hasContent", false);
    }else{
        resultMap.put("hasContent", true);
    }
    successRessult.put("data", resultMap);
    return successRessult;
}

public Map<String, Object> loadFile() {
    Map<String, Object> successRessult = ServiceUtil.returnSuccess();
    ByteBuffer file = context.get("file");
    //字节流-->输入流
    ByteArrayInputStream os = new ByteArrayInputStream(file.array());
    //创建excel表工作簿对象
    HSSFWorkbook workBook = new HSSFWorkbook(os);
    //创建Excel工作表对象
    HSSFSheet sheet = workBook.getSheetAt(0);
    //取得有效的行数
//    int rowcount = sheet.getLastRowNum();
    int i = 1;
    Map<String, String> levelGroupMap = new HashMap();
    Map<String, String> statusGroupMap = new HashMap();
    //根据指定的条件查出状态和等级数据，通过enumTypeId判断，放在两个Map里,key为description,Value为enumId
    List<GenericValue> list = EntityQuery.use(delegator).select("enumId", "description","enumTypeId").from("Enumeration").where(EntityCondition.makeCondition(EntityCondition.makeCondition("enumTypeId", "CUSTOMER_LEVEL"),EntityOperator.OR,EntityCondition.makeCondition("enumTypeId", "CUSTOMER_STATUS"))).queryList();
    for (int n = 0; n < list.size(); n++) {
        Map groupMap = list.get(n);
        if("CUSTOMER_LEVEL".equals(groupMap.get("enumTypeId"))){
            String enumId = groupMap.get("enumId");
            String descriptionLevel = groupMap.get("description");
            levelGroupMap.put(descriptionLevel, enumId);
        }else {
            String enumId = groupMap.get("enumId");
            String descriptionStatus = groupMap.get("description");
            statusGroupMap.put(descriptionStatus,enumId);
        }
    }
    while (true) {
        HSSFRow row = sheet.getRow(i);
        HSSFCell customerNameCell;
        //判断拿到的行是不是为null，如果不为null的话，再判断这一行的第一个cell是不是为空，为空跳出循环。
        if (row == null) {
            break;
        } else {
            customerNameCell = row.getCell(0);
            if (customerNameCell == null || UtilValidate.isEmpty(customerNameCell.getStringCellValue())) {
                break;
            }
        }
        String customerName = customerNameCell.getStringCellValue();
        String area = row.getCell(1).getStringCellValue();
        String address = row.getCell(2).getStringCellValue();
        String webAddress = row.getCell(3).getStringCellValue();
        String customerLevel = row.getCell(4).getStringCellValue();
        //通过levelGroupMap的key直接拿到value
        customerLevel = levelGroupMap.get(customerLevel);
        String customerStatus = row.getCell(5).getStringCellValue();
        customerStatus = statusGroupMap.get(customerStatus);
        String remarks = row.getCell(6).getStringCellValue();
        String contactPerson = row.getCell(7).getStringCellValue();
        HSSFCell cell = row.getCell(8);
        String contactNumber;
        if (Cell.CELL_TYPE_NUMERIC == cell.cellType) {
            //拿出来的数据为double类型，先转换成long，再转换成string类型
            contactNumber = Long.toString(cell.getNumericCellValue().longValue());
        } else {
            contactNumber = cell.getStringCellValue();
        }
        Map<String, Object> map = UtilMisc.toMap("customerName", customerName, "area", area, "address", address, "webAddress", webAddress,
                "customerLevel", customerLevel, "customerStatus", customerStatus, "remarks", remarks, "contactPerson", contactPerson, "contactNumber", contactNumber);
        runService("addPartner", map);
        i++;
    }
    return successRessult;
}