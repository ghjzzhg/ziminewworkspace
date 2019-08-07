import javolution.util.FastMap

param = parameters.get("param");
if(param == "2"){
    memoMap1 = FastMap.newInstance();
    memoMap1.put("a","工作日志");
    context.memoMap1 = memoMap1;
}




