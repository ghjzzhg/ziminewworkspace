import javolution.util.FastList
import javolution.util.FastMap

workPerformanceList = FastList.newInstance();
workPerformanceMap = FastMap.newInstance();
workPerformanceMap.put("w","开发部");
workPerformanceMap.put("e","郭靖");
workPerformanceMap.put("r","未完成");
workPerformanceMap.put("t","");
workPerformanceMap.put("y","");
workPerformanceMap.put("u","");

workPerformanceMap = FastMap.newInstance();
workPerformanceMap.put("w","开发部");
workPerformanceMap.put("e","张扬");
workPerformanceMap.put("r","已完成");
workPerformanceMap.put("t","2015-6-6");
workPerformanceMap.put("y","70");
workPerformanceMap.put("u","速度慢");
workPerformanceList.add(workPerformanceMap);

workPerformanceMap = FastMap.newInstance();
workPerformanceMap.put("w","开发部");
workPerformanceMap.put("e","张娴");
workPerformanceMap.put("r","已完成");
workPerformanceMap.put("t","2015-6-6");
workPerformanceMap.put("y","80");
workPerformanceMap.put("u","干的不错");
workPerformanceList.add(workPerformanceMap);
context.workPerformanceList = workPerformanceList;


