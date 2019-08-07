import javolution.util.FastList
import javolution.util.FastMap

transactionTypeList = FastList.newInstance();
transactionTypeMap = FastMap.newInstance();
//transactionTrackMap.put("q",1);
transactionTypeMap.put("a","公用");
transactionTypeMap.put("b","100001");
transactionTypeMap.put("c","电脑维护(系统跟新)");
transactionTypeMap.put("d","按年重复");
transactionTypeMap.put("e","998");
transactionTypeMap.put("f","网络部");
transactionTypeList.add(transactionTypeMap);
context.transactionTypeList = transactionTypeList;


