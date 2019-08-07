import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.entity.model.ModelUtil

outInventoryManyList = FastList.newInstance();
outInventoryMany = FastMap.newInstance();
outInventoryMany.put("productCode",parameters.get("productCode"));
outInventoryMany.put("productName",parameters.get("productName"));
outInventoryMany.put("standard",parameters.get("standard"));
outInventoryMany.put("type",parameters.get("type"));
outInventoryMany.put("unit","个");
outInventoryMany.put("inventoryNum",3435);
outInventoryMany.put("enabledReceiveNum",234);
outInventoryMany.put("receiveNum",parameters.get("receiveNum"));
outInventoryManyList.add(outInventoryMany);
context.outInventoryManyList = outInventoryManyList;
parameters.put("listsize",outInventoryManyList.size());

