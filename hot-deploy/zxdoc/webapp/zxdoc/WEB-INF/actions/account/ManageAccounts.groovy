import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.UtilPagination

import java.text.SimpleDateFormat

String accountType = parameters.accountType;
String SearchGroupName = parameters.get("SearchGroupName");
String SearchUserLogin = parameters.get("SearchUserLogin");
String SearchCategorys = parameters.get("SearchCategorys");
String SearchContactNumber = parameters.get("SearchContactNumber");
String enabled = parameters.get("enabled");
String personName = parameters.get("personName");
if(enabled==null||enabled=="")
{
    enabled = "Y";
}
List data;
List<Map<String, Object>> resultList = new ArrayList<>();
Map table;
UtilPagination.PaginationResultDatatables result = null;
//查询所有已通过审核的账户
if (accountType.equals("group")) {//机构账户
    List<EntityCondition> conditions = [];
    //查询条件：类型
    if (UtilValidate.isNotEmpty(SearchCategorys)) {
        conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, SearchCategorys));
    }
    //查询条件：机构名
    if (UtilValidate.isNotEmpty(SearchGroupName)) {
        conditions.add(EntityCondition.makeCondition(EntityJoinOperator.OR, EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + SearchGroupName + "%"), EntityCondition.makeCondition("partnerGroupName", EntityOperator.LIKE, "%" + SearchGroupName + "%")));
        //changed by galaxypan@2017-08-03:查询机构型合伙人
    }
    //查询条件：用户名
    if (UtilValidate.isNotEmpty(SearchUserLogin)) {
        conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.LIKE, "%" + SearchUserLogin + "%"));
    }
    //查询条件：电话号码
    if (UtilValidate.isNotEmpty(SearchContactNumber)) {
        conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, "%" + SearchContactNumber + "%"));
    }
    /*conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["PARTY_IDENTIFIED", "PARTY_UNIDENTIFIED"]));*/
    if("Y".equals(enabled)){
        conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
    }else{//兼容N 、J
        conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.NOT_EQUAL, "Y"));
    }

    result = UtilPagination.queryPageDatatables(from("PartyGroupAndUserLoginAndContact").where(conditions).cache(), parameters);

    data = result.data;

    for (GenericValue outfit:data)
    {
        Map acc = new HashMap();
        //账户类型
        acc.put("roleTypeId",outfit.get("roleTypeId"));
        //详细地址
        acc.put("address1",outfit.get("address1")==null?"":outfit.get("address1"));
        //创建时间
        acc.put("createdStamp",outfit.get("createdStamp"));
        //描述
        acc.put("description",outfit.get("description")==null?"":outfit.get("description"));
        //权限
        acc.put("enabled",outfit.get("enabled"));
        //机构名
        acc.put("groupName",UtilValidate.isEmpty(outfit.get("partnerGroupName")) ? (outfit.get("groupName")==null ? "" : outfit.get("groupName")) : outfit.get("partnerGroupName"));
        acc.put("isPartner", UtilValidate.isNotEmpty(outfit.get("partnerType")));
        //状态
        acc.put("statusId",outfit.get("statusId"));
        //联系号码
        acc.put("contactNumber",outfit.get("contactNumber")==null?"":outfit.get("contactNumber"));
        //主键
        acc.put("partyId",outfit.get("partyId"));
        //联系人
        acc.put("fullName",outfit.get("fullName")==null?"":outfit.get("fullName"));
        //地址
        String postalCode = outfit.get("area");
        StringBuffer site = new StringBuffer();
        if(postalCode != null) {
            GenericValue provincial = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", postalCode)).queryOne()
            String cityCode = postalCode.substring(0, 4) + "00";
            GenericValue city = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", cityCode)).queryOne()
            String areaCode = postalCode.substring(0, 2) + "0000";
            GenericValue area = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", areaCode)).queryOne()
            if (area != null && city != null && provincial != null) {
                if(area.equals(city)&&city.equals(provincial))
                {
                    site.append(area.get("geoName"));
                }
                else if (city.equals(provincial))
                {
                    site.append(area.get("geoName")).append("/");
                    site.append(city.get("geoName"));
                }else {
                    site.append(area.get("geoName")).append("/");
                    site.append(city.get("geoName")).append("/");
                    site.append(provincial.get("geoName"));
                }
            }
            if (city != null && area != null && provincial == null) {
                if(city.equals(area))
                {
                    site.append(area.get("geoName"));
                }else {
                    site.append(area.get("geoName")).append("/");
                    site.append(city.get("geoName"));
                }
            }
            if (provincial == null && area != null && city == null) {
                site.append(area.get("geoName"));
            }
            //偏远地区省市区特例
            else if (area != null && city == null && provincial != null) {
                site.append(area.get("geoName")).append("/");
                site.append(provincial.get("geoName"));
            }
        }else
        {
            String tip = "未填写";
            site.append(tip);
        }
        acc.put("geoName",site);
        //用户名，如果是合伙人，名字应该为子账户的名字
        String partyId = outfit.get("partyId");
        String roleTypeId = outfit.get("roleTypeId");
        //changed by galaxypan@2017-08-03:合伙人主账户直接显示
        /*if(roleTypeId.equals("CASE_ROLE_PARTNER"))
        {
            String userLoginId = " ";
            //合伙人对应的子账户应该只有一个，
            List relationList = EntityQuery.use(delegator).from("PartyRelationship").where(UtilMisc.toMap("partyIdFrom",partyId,"partyRelationshipTypeId","ORG_SUB_ACCOUNT")).cache().queryList();
            if(relationList!=null && relationList.size()!=0) {
                GenericValue relation = relationList.get(0);
                String thisParty = relation.get("partyIdTo");
                GenericValue user = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("partyId", thisParty)).cache().queryOne();
                if (user != null) {
                    userLoginId = user.get("userLoginId")
                }
            }
            acc.put("userLoginId",userLoginId == null ? "":userLoginId);
        }else
        {
            acc.put("userLoginId",outfit.get("userLoginId"));
        }*/
        acc.put("userLoginId",outfit.get("userLoginId"));
        //时间处理
        String cueTime = outfit.get("CueTime");
        String remainTime = "";
        if(cueTime == null)
        {
            //到期时间为空，计算试用期时间
            String createdStamp = outfit.get("createdStamp");
            Calendar c = Calendar.getInstance();//获得一个日历的实例
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try{
                date = sdf.parse(createdStamp);//初始日期
            }catch(Exception e){

            }
            c.setTime(date);//设置日历时间
            c.add(Calendar.MONTH,6);//在日历的月份上增加6个月
            remainTime = sdf.format(c.getTime());//的到你想要得6个月后的日期
        }else
        {
            remainTime = cueTime;
        }
        acc.put("CueTime",remainTime);
        acc.put("area",outfit.get("area")==null?"":outfit.get("area"));
        resultList.add(acc);
    }

}
//子账户
else {
    List<EntityCondition> conditions = [];
    //查询条件：机构名
    if (UtilValidate.isNotEmpty(personName)) {
        conditions.add(EntityCondition.makeCondition("fullName", EntityOperator.LIKE, "%" + personName + "%"));
    }
    //查询条件：用户名
    if (UtilValidate.isNotEmpty(SearchUserLogin)) {
        conditions.add(EntityCondition.makeCondition("userLoginId", EntityOperator.LIKE, "%" + SearchUserLogin + "%"));
    }
    //查询条件：电话号码
    if (UtilValidate.isNotEmpty(SearchContactNumber)) {
        conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, "%" + SearchContactNumber + "%"));
    }
    /*conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["PARTY_IDENTIFIED", "PARTY_UNIDENTIFIED"]));*/
    if("Y".equals(enabled)){
        conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
    }else{//兼容N 、J
        conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.NOT_EQUAL, "Y"));
    }
//    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "CASE_ROLE_PARTNER"));
    conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "CASE_ROLE_%"));
    EntityQuery.use(delegator).from("SubAccountAndContact").where(conditions).queryList();
    result = UtilPagination.queryPageDatatables(from("SubAccountAndContact").where(conditions).cache(), parameters);
    data = result.data;
    for (GenericValue outfit:data)
    {
        Map acc = new HashMap();
        //账户类型
        acc.put("roleTypeId",outfit.get("roleTypeId"));
        //详细地址
        acc.put("address1",outfit.get("address1")==null?"":outfit.get("address1"));
        //创建时间
        acc.put("createdStamp",outfit.get("createdStamp"));
        //描述
        acc.put("description","");
        //权限
        acc.put("enabled",outfit.get("enabled"));
        //机构名
        acc.put("groupName",outfit.get("groupName")==null?"":outfit.get("groupName"));
        //状态
        acc.put("statusId",outfit.get("statusId"));
        //联系号码
        acc.put("contactNumber",outfit.get("contactNumber")==null?"":outfit.get("contactNumber"));
        //主键
        acc.put("partyId",outfit.get("partyId"));
        //联系人
        acc.put("fullName",outfit.get("fullName")==null?"":outfit.get("fullName"));
        //地址
        String postalCode = outfit.get("postalCode");
        StringBuffer site = new StringBuffer();
        if(postalCode != null) {
            GenericValue provincial = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", postalCode)).queryOne()
            String cityCode = postalCode.substring(0, 4) + "00";
            GenericValue city = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", cityCode)).queryOne()
            String areaCode = postalCode.substring(0, 2) + "0000";
            GenericValue area = EntityQuery.use(delegator).from("Geo").where(UtilMisc.toMap("geoCode", areaCode)).queryOne()
            if (area != null && city != null && provincial != null) {
                if(area.equals(city)&&city.equals(provincial))
                {
                    site.append(area.get("geoName"));
                }
                else if (city.equals(provincial))
                {
                    site.append(area.get("geoName")).append("/");
                    site.append(city.get("geoName"));
                }else {
                    site.append(area.get("geoName")).append("/");
                    site.append(city.get("geoName")).append("/");
                    site.append(provincial.get("geoName"));
                }
            }
            if (city != null && area != null && provincial == null) {
                if(city.equals(area))
                {
                    site.append(area.get("geoName"));
                }else {
                    site.append(area.get("geoName")).append("/");
                    site.append(city.get("geoName"));
                }
            }
            if (provincial == null && area != null && city == null) {
                site.append(area.get("geoName"));
            }
        }else
        {
            String tip = "未填写";
            site.append(tip);
        }
        acc.put("geoName",site);
        //用户名，如果是合伙人，名字应该为子账户的名字
        String partyId = outfit.get("partyId");
        String roleTypeId = outfit.get("roleTypeId");
        acc.put("userLoginId",outfit.get("userLoginId"));
        //时间处理
        String cueTime = outfit.get("CueTime");
        String remainTime = "";
        if(cueTime == null)
        {
            //到期时间为空，计算试用期时间
            String createdStamp = outfit.get("createdStamp");
            Calendar c = Calendar.getInstance();//获得一个日历的实例
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try{
                date = sdf.parse(createdStamp);//初始日期
            }catch(Exception e){

            }
            c.setTime(date);//设置日历时间
            c.add(Calendar.MONTH,6);//在日历的月份上增加6个月
            remainTime = sdf.format(c.getTime());//的到你想要得6个月后的日期
        }else
        {
            remainTime = cueTime;
        }
        acc.put("CueTime",remainTime);
        acc.put("area",outfit.get("postalCode")==null?"":outfit.get("postalCode"));
        acc.put("type","subAccount");
        resultList.add(acc);
    }

}

if (result != null) {
    request.setAttribute("draw", result.draw);
    request.setAttribute("recordsTotal", result.recordsTotal);
    request.setAttribute("recordsFiltered", result.recordsFiltered);
    request.setAttribute("data", resultList);
}