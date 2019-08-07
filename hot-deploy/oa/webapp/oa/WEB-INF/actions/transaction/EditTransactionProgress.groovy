import javolution.util.FastMap
param = parameters.get("param");
if(param!=null&&param.equals("edit")){
    transactionProgressMap = FastMap.newInstance();
    transactionProgressMap.put("a","1");
    transactionProgressMap.put("b","电脑维护(系统跟新)");
    transactionProgressMap.put("c","2");
    transactionProgressMap.put("d","998");
    transactionProgressMap.put("e","网络部");
    context.transactionProgressMap = transactionProgressMap;
}



