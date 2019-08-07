import javolution.util.FastMap

id = parameters.get("productCode");
if(id!=null && !"".equals(id)){
    editProductTypeMap = FastMap.newInstance();
    editProductTypeMap.put("productCode",id);
    editProductTypeMap.put("warehouseName","所属仓库");
    editProductTypeMap.put("typeName","类别名称"+id);
    editProductTypeMap.put("note","备注"+id);
    context.editProductTypeMap =editProductTypeMap;
}
