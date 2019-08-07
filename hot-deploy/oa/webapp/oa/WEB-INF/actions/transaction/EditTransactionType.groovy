import javolution.util.FastMap
param = parameters.get("param");
if(param!=null&&param.equals("edit")){
    transactionTypeMap = FastMap.newInstance();
    transactionTypeMap.put("a","1");
    transactionTypeMap.put("b","电脑维护(系统跟新)");
    transactionTypeMap.put("c","2");
    transactionTypeMap.put("d","998");
    transactionTypeMap.put("e","网络部");
    context.transactionTypeMap = transactionTypeMap;
}



