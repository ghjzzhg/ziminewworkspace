import javolution.util.FastList

fixedAssetsList =  FastList.newInstance();

for (int i=0;i<2;i++){
    fixedAssetsMap = [:];
    fixedAssetsMap.put("q",i);
    fixedAssetsMap.put("w","奥迪Q7");
    fixedAssetsMap.put("e","交通工具");
    fixedAssetsMap.put("r","Q7");
    fixedAssetsMap.put("t",20);
    fixedAssetsMap.put("y",10);
    fixedAssetsMap.put("u","销售部");
    fixedAssetsMap.put("i","陈国民");
    fixedAssetsMap.put("o","");
    fixedAssetsMap.put("p","");
    fixedAssetsMap.put("a","");
    fixedAssetsList.add(fixedAssetsMap);
}

context.fixedAssetsList = fixedAssetsList;