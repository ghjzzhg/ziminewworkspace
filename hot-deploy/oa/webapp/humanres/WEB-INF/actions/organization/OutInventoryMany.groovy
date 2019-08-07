import javolution.util.FastList
import javolution.util.FastMap
outInventoryManyList = FastList.newInstance();

outInventoryManyMap = FastMap.newInstance();

outInventoryManyMap.put("productCode")
id = parameters.get("productCode");
editProductTypeMap = FastMap.newInstance();
editProductTypeMap.put("productCode",id);
editProductTypeMap.put("warehouseName","所属仓库");
editProductTypeMap.put("typeName","类别名称"+id);
editProductTypeMap.put("note","备注"+id);
context.editProductTypeMap =editProductTypeMap;