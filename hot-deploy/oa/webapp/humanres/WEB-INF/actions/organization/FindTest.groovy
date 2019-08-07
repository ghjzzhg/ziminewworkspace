import javolution.util.FastList
import javolution.util.FastMap

testList = FastList.newInstance();
testMap = FastMap.newInstance();
testMap.put("a",null);
testList.add(testMap);
context.testList = testList;



