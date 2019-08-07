import javolution.util.FastList
import javolution.util.FastMap

productTypeList = FastList.newInstance();

for(int i=0;i<2;i++){
    if(i==0){
        productTypeMap = FastMap.newInstance();
        productTypeMap.put("productCode",i);
        productTypeMap.put("typeName","办公用品");
        productTypeMap.put("warehouseName","办公用品仓库");
        productTypeMap.put("inputPerson","赵爽");
        productTypeMap.put("inputTime","2015-5-25");
        productTypeMap.put("note","备注"+i);
        productTypeList.add(productTypeMap);
    }else{
        productTypeMap = FastMap.newInstance();
        productTypeMap.put("productCode",i);
        productTypeMap.put("typeName","电子器材");
        productTypeMap.put("warehouseName","材料库");
        productTypeMap.put("inputPerson","韩玲");
        productTypeMap.put("inputTime","2015-5-25");
        productTypeMap.put("note","备注"+i);
        productTypeList.add(productTypeMap);
    }

}

context.productTypeList = productTypeList;