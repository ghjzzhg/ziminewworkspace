import javolution.util.FastList
import javolution.util.FastMap
selected = parameters.get("selected");
if(selected=="1"){
    transactionProgressList = FastList.newInstance();
    transactionProgressMap = FastMap.newInstance();
    transactionProgressMap.put("a","100001");
    transactionProgressMap.put("b","电脑维护(系统跟新)");
    transactionProgressMap.put("c","未开始");
    transactionProgressMap.put("d","998");
    transactionProgressList.add(transactionProgressMap);

    transactionProgressMap = FastMap.newInstance();
    transactionProgressMap.put("a","100001");
    transactionProgressMap.put("b","电脑维护(系统跟新)");
    transactionProgressMap.put("c","进行中");
    transactionProgressMap.put("d","998");
    transactionProgressList.add(transactionProgressMap);

    transactionProgressMap = FastMap.newInstance();
    transactionProgressMap.put("a","100001");
    transactionProgressMap.put("b","电脑维护(系统跟新)");
    transactionProgressMap.put("c","已完成");
    transactionProgressMap.put("d","998");
    transactionProgressList.add(transactionProgressMap);
    context.transactionProgressList = transactionProgressList;
}else if(selected=="2"){
    transactionProgressList = FastList.newInstance();
    transactionProgressMap1 = FastMap.newInstance();
    transactionProgressMap1.put("a","100002");
    transactionProgressMap1.put("b","设备跟新");
    transactionProgressMap1.put("c","未开始跟新");
    transactionProgressMap1.put("d","998");
    transactionProgressList.add(transactionProgressMap1);

    transactionProgressMap2 = FastMap.newInstance();
    transactionProgressMap2.put("a","100002");
    transactionProgressMap2.put("b","设备跟新");
    transactionProgressMap2.put("c","购买器材中");
    transactionProgressMap2.put("d","998");
    transactionProgressList.add(transactionProgressMap2);

    transactionProgressMap3 = FastMap.newInstance();
    transactionProgressMap3.put("a","100002");
    transactionProgressMap3.put("b","设备跟新");
    transactionProgressMap3.put("c","购买结束");
    transactionProgressMap3.put("d","998");
    transactionProgressList.add(transactionProgressMap3);

    transactionProgressMap4 = FastMap.newInstance();
    transactionProgressMap4.put("a","100002");
    transactionProgressMap4.put("b","设备跟新");
    transactionProgressMap4.put("c","跟新设备中");
    transactionProgressMap4.put("d","998");
    transactionProgressList.add(transactionProgressMap4);

    transactionProgressMap5 = FastMap.newInstance();
    transactionProgressMap5.put("a","100002");
    transactionProgressMap5.put("b","设备跟新");
    transactionProgressMap5.put("c","跟新完成");
    transactionProgressMap5.put("d","998");
    transactionProgressList.add(transactionProgressMap5);
    context.transactionProgressList = transactionProgressList;
}



