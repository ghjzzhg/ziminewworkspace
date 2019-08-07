import javolution.util.FastList

fixedAssetsPartsList =  FastList.newInstance();

for (int i=0;i<1;i++){
    fixedAssetsPartsMap = [:];
    fixedAssetsPartsMap.put("q","打印机");
    fixedAssetsPartsMap.put("w","a507");
    fixedAssetsPartsMap.put("e","测试厂家");
    fixedAssetsPartsMap.put("r","台");
    fixedAssetsPartsMap.put("t","20");
    fixedAssetsPartsMap.put("y","");
    fixedAssetsPartsMap.put("u","是");
    fixedAssetsPartsMap.put("i","");
    fixedAssetsPartsList.add(fixedAssetsPartsMap);
}

context.fixedAssetsPartsList = fixedAssetsPartsList;

