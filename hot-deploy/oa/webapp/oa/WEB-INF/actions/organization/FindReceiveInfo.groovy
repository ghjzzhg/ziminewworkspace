import javolution.util.FastList
import javolution.util.FastMap
receiveInfoList = FastList.newInstance();
checkHistoryList = FastList.newInstance();
receiveInfoFormMap = FastMap.newInstance();
receiveInfoFormMap.put("receiveNum",1);
receiveInfoFormMap.put("warehouseName","办公用品仓库");
receiveInfoFormMap.put("receiveType","A4纸");
receiveInfoFormMap.put("receiveDepartment","财务部");
receiveInfoFormMap.put("receivePerson","何宏志");
receiveInfoFormMap.put("receiveDate","2015-02-15");
receiveInfoFormMap.put("checkResult","2015-05-12");
receiveInfoFormMap.put("checkPerson","程甜甜");
receiveInfoFormMap.put("makeInfoPerson","张伟林");
receiveInfoFormMap.put("description","");
receiveInfoFormMap.put("makeInfoTime","2015-05-12");
receiveInfoFormMap.put("nowStatus","审核通过待发货");
context.receiveInfoFormMap = receiveInfoFormMap;
for(int i=0;i<2;i++){
    if(i==0){
        receiveInfoMap = FastMap.newInstance();
        receiveInfoMap.put("productCode",i);
        receiveInfoMap.put("productName","打印机");
        receiveInfoMap.put("standard","250w");
        receiveInfoMap.put("type","电子器材");
        receiveInfoMap.put("unit","台");
        receiveInfoMap.put("inventoryNum",20);
        receiveInfoMap.put("enabledReceiveNum",3);
        receiveInfoMap.put("receiveNum",1);
        receiveInfoMap.put("num",0);
        receiveInfoList.add(receiveInfoMap);

        checkHistoryMap = FastMap.newInstance();
        checkHistoryMap.put("checkTime","");
        checkHistoryMap.put("checkJob","总经理");
        checkHistoryMap.put("checkPerson","");
        checkHistoryMap.put("checkOpinion","");
        checkHistoryMap.put("checkExplain","待审核");
        checkHistoryList.add(checkHistoryMap);
    }else {
        receiveInfoMap = FastMap.newInstance();
        receiveInfoMap.put("productCode",i);
        receiveInfoMap.put("productName","电脑");
        receiveInfoMap.put("standard","250w");
        receiveInfoMap.put("type","电子器材");
        receiveInfoMap.put("unit","台");
        receiveInfoMap.put("inventoryNum",20);
        receiveInfoMap.put("enabledReceiveNum",3);
        receiveInfoMap.put("receiveNum",1);
        receiveInfoMap.put("num",0);
        receiveInfoList.add(receiveInfoMap);

        checkHistoryMap = FastMap.newInstance();
        checkHistoryMap.put("checkTime","");
        checkHistoryMap.put("checkJob","财务总监");
        checkHistoryMap.put("checkPerson","");
        checkHistoryMap.put("checkOpinion","");
        checkHistoryMap.put("checkExplain","待审核");
        checkHistoryList.add(checkHistoryMap);
    }



}

context.receiveInfoList = receiveInfoList;
context.checkHistoryList = checkHistoryList;