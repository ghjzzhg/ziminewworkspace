import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

perfExamTypes = [];
parentType = parameters.get("perfExamItemType");
if(UtilValidate.isEmpty(parentType)){
    itemTypeList = from("TblPerfExamItemType").queryList();
}else{
    itemTypeList = from("TblPerfExamItemType").where(UtilMisc.toMap("parentTypeId", parentType)).queryList();
}

if(itemTypeList){
    for (GenericValue itemType : itemTypeList) {
        perfExamTypes.add([id: itemType.typeId, code: itemType.typeId, pId: itemType.parentTypeId, name: itemType.description]);
    }
}

request.setAttribute("data", perfExamTypes);