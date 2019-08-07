import javolution.util.FastList

depreciationList =  FastList.newInstance();

for (int i=0;i<10;i++){
    depreciationMap = [:];
    depreciationMap.put("q","test");
    depreciationMap.put("w","test");
    depreciationMap.put("e","test");
    depreciationMap.put("r","test");
    depreciationMap.put("t","test");
    depreciationList.add(depreciationMap);
}

context.depreciationList = depreciationList;

