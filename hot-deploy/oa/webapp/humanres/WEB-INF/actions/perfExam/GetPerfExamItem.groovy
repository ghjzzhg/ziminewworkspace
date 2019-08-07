import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

id = parameters.get("id");

selectedType = parameters.get("parentType");
selectedSubType = parameters.get("type");

perfExamTypes = [];
itemTypeList = from("TblPerfExamItemType").where(UtilMisc.toMap("parentTypeId", "1")).queryList();
if(itemTypeList){
    for (GenericValue itemType : itemTypeList) {
        perfExamTypes.add([id: itemType.typeId, code: itemType.typeId, pId: itemType.parentTypeId, name: itemType.description]);
    }
    if(UtilValidate.isEmpty(selectedType) || "1".equals(selectedType)){
        selectedType = perfExamTypes[0].id;
    }
}
context.perfExamTypes = perfExamTypes;


perfExamSubTypes = [];
itemTypeList = from("TblPerfExamItemType").where(UtilMisc.toMap("parentTypeId", selectedType)).queryList();

if(itemTypeList) {
    for (GenericValue itemType : itemTypeList) {
        perfExamSubTypes.add([id: itemType.typeId, code: itemType.typeId, pId: itemType.parentTypeId, name: itemType.description]);
    }
    if(UtilValidate.isEmpty(selectedSubType) || "1".equals(selectedSubType)) {
        selectedSubType = perfExamSubTypes[0].id;
    }
}
context.perfExamSubTypes = perfExamSubTypes;

perfExamItem = [parentType: selectedType, type: selectedSubType];
Map map = new HashMap();
if(id){
    perfExamItem = delegator.findByPrimaryKeyCache("TblPerfExamItem", UtilMisc.toMap("itemId", id));
    map.putAll(perfExamItem);
    map.put("type",perfExamItem.get("typeId"))
    selectedType = perfExamItem.parentType;
}
context.perfExamItem = map;