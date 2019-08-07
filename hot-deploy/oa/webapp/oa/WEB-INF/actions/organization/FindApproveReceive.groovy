import javolution.util.FastList
import javolution.util.FastMap
approveReceiveList = FastList.newInstance();
for(int i=0;i<2;i++){
    if(i==0){
        approveReceiveMap = FastMap.newInstance();
        approveReceiveMap.put("receiveNum",i);
        approveReceiveMap.put("warehouseName","办公用品仓库");
        approveReceiveMap.put("receiveType","A4纸");
        approveReceiveMap.put("receiveDate","2015-02-15");
        approveReceiveMap.put("receiveDepartment","财务部");
        approveReceiveMap.put("receivePerson","何宏志");
        approveReceiveMap.put("makeInfoPerson","张伟林");
        approveReceiveMap.put("makeInfoTime","2015-05-12");
        approveReceiveMap.put("checkResult","审核通过");
        approveReceiveMap.put("nowStatus","待发货");
        approveReceiveList.add(approveReceiveMap);
    }else {
        approveReceiveMap = FastMap.newInstance();
        approveReceiveMap.put("receiveNum",i);
        approveReceiveMap.put("warehouseName","办公用品仓库");
        approveReceiveMap.put("receiveType","2B铅笔");
        approveReceiveMap.put("receiveDate","2015-02-15");
        approveReceiveMap.put("receiveDepartment","设计部");
        approveReceiveMap.put("receivePerson","王腾");
        approveReceiveMap.put("makeInfoPerson","赵鼎");
        approveReceiveMap.put("makeInfoTime","2015-05-12");
        approveReceiveMap.put("checkResult","审核通过");
        approveReceiveMap.put("nowStatus","待发货");
        approveReceiveList.add(approveReceiveMap);
    }

}
context.approveReceiveList = approveReceiveList;