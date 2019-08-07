import javolution.util.FastList
import javolution.util.FastMap
receiveList = FastList.newInstance();
for(int i=0;i<5;i++){
    receiveMap = FastMap.newInstance();
    receiveMap.put("receiveNum",i);
    receiveMap.put("warehouseName","仓库"+i);
    receiveMap.put("receiveType","领用类型"+i);
    receiveMap.put("receiveDate","2015-02-15");
    receiveMap.put("receiveDepartment","领用部门"+i);
    receiveMap.put("receivePerson","领用人"+i);
    receiveMap.put("makeInfoPerson","制单人"+i);
    receiveMap.put("makeInfoTime","2015-05-12");
    receiveMap.put("checkResult","待审核");
    receiveMap.put("nowStatus","目前由总经理审批");
    receiveList.add(receiveMap);
}

context.receiveList = receiveList;