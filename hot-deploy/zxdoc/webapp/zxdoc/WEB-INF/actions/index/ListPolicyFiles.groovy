import org.ofbiz.base.util.StringUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.UtilPagination

import java.text.SimpleDateFormat

String category = parameters.category;
String searchKey = parameters.searchKey;
context.searchKey = searchKey;
List<EntityCondition> cons = new ArrayList<>();
if(UtilValidate.isNotEmpty(category)){
    GenericValue type = from("Enumeration").where("enumId", category).queryOne();
    String parentTypeId = type.getString("enumTypeId");
    GenericValue parentType = from("Enumeration").where("enumId", parentTypeId).queryOne();
    context.categoryName = (parentType == null ? "" : (parentType.getString("description") + "-")) + type.getString("description");
    cons.add(EntityCondition.makeCondition("category", EntityOperator.EQUALS, category));
}

if(UtilValidate.isNotEmpty(searchKey)){
    cons.add(EntityCondition.makeCondition("dataResourceName", EntityOperator.LIKE, "%" + searchKey.trim() + "%"));
}

UtilPagination.PaginationResultDatatables result = UtilPagination.queryPageDatatables(from("PolicyFileList").where(cons).orderBy("-publishDate", "dataResourceName").cache(), parameters);
request.setAttribute("draw", result.draw);
request.setAttribute("recordsTotal", result.recordsTotal);
request.setAttribute("recordsFiltered", result.recordsFiltered);
List<GenericValue> files = result.data;
List<Map<String, Object>> fileData = new ArrayList<>();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
for (GenericValue file : files) {
    Map data = new HashMap();
    Date publishDate = file.get("publishDate")
    publishDate = publishDate == null ? file.get("createdDate") : publishDate;
    data.put("publishDate", sdf.format(publishDate));
    data.put("fileId", file.getString("id"));
    String fileName = file.getString("dataResourceName")
    data.put("fileName", fileName);
    int length = StringUtil.xmlDecodedLength(fileName);
//    if(length > 20){
//        data.put("fileShortName", StringUtil.xmlDecodedSubstring(fileName, 0, 20) + "...")
//    }else{
        data.put("fileShortName", fileName);
//    }
    fileData.add(data);
}

request.setAttribute("data", fileData);