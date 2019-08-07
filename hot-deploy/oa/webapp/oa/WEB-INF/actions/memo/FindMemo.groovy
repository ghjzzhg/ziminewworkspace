import javolution.util.FastList
import javolution.util.FastMap

memoList = FastList.newInstance();
memoMap = FastMap.newInstance();
memoMap.put("q","备忘");
memoMap.put("w","个人日记");
memoMap.put("e","旅游风景");
memoMap.put("r","2015-7-28");
memoMap.put("t","2015-5-28");
memoMap.put("y","2015-6-01 11:28");
memoList.add(memoMap);
context.memoList = memoList;


