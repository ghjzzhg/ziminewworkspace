import javolution.util.FastList
fixedAssetsBorrowList =  FastList.newInstance();

    fixedAssetsBorrowMap = [:];
    fixedAssetsBorrowMap.put("a",1);
    fixedAssetsBorrowMap.put("b","机床");
    fixedAssetsBorrowMap.put("c","生产设备");
    fixedAssetsBorrowMap.put("d","DHD1200");
    fixedAssetsBorrowMap.put("e",3);
    fixedAssetsBorrowMap.put("f",1);
    fixedAssetsBorrowMap.put("g","在库可用");

    /*if(i==3){
        fixedAssetsBorrowMap.put("h","a");
    }else{
        fixedAssetsBorrowMap.put("h","b");
    }*/
    fixedAssetsBorrowMap.put("h","a");
    fixedAssetsBorrowMap.put("i","");
    fixedAssetsBorrowMap.put("j","");
    fixedAssetsBorrowList.add(fixedAssetsBorrowMap);

fixedAssetsBorrowMap1 = [:];
fixedAssetsBorrowMap1.put("a",2);
fixedAssetsBorrowMap1.put("b","联想笔记本");
fixedAssetsBorrowMap1.put("c","办公设备");
fixedAssetsBorrowMap1.put("d","ThinkingPad E500");
fixedAssetsBorrowMap1.put("e",4);
fixedAssetsBorrowMap1.put("f",2);
fixedAssetsBorrowMap1.put("g","已预约待借用");

/*if(i==3){
    fixedAssetsBorrowMap.put("h","a");
}else{
    fixedAssetsBorrowMap.put("h","b");
}*/
fixedAssetsBorrowMap1.put("h","b");
fixedAssetsBorrowMap1.put("i","");
fixedAssetsBorrowMap1.put("j","test");
fixedAssetsBorrowList.add(fixedAssetsBorrowMap1);


context.fixedAssetsBorrowList = fixedAssetsBorrowList;

