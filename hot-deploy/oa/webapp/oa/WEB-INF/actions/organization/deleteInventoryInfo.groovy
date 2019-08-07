import javolution.util.FastList
import javolution.util.FastMap

id = parameters.get("warehouseId");

inventoryInfoList = FastList.newInstance();
for(int i=0;i<10;i++){
    if(i.toString().equals(id)){
        continue;
    }
    inventoryInfoMap = FastMap.newInstance();
    inventoryInfoMap.put("inventoryInfoId",i);
    inventoryInfoMap.put("warehouseCode","仓库编号"+i);
    inventoryInfoMap.put("warehouseName","仓库名称"+i);
    inventoryInfoMap.put("warehouseAddress","仓库地址"+i);
    inventoryInfoMap.put("linkman","联系人"+i);
    inventoryInfoMap.put("phone","电话"+i);
    inventoryInfoMap.put("inputPerson","录入人"+i);
    inventoryInfoMap.put("inputTime","录入时间"+i);
    inventoryInfoList.add(inventoryInfoMap);
}

context.inventoryInfoList = inventoryInfoList;