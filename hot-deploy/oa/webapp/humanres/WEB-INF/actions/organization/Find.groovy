import javolution.util.FastList

workNumList =  FastList.newInstance();

workNumMap = [:];
workNumMap.put("a",0);
workNumMap.put("b","早班");
workNumMap.put("c","正常班次");
workNumMap.put("d","00:30");
workNumMap.put("e","01:00");
workNumMap.put("e","01:30");
workNumMap.put("f","08:30");
workNumMap.put("g","09:00");
workNumMap.put("h","09:30");
workNumList.add(workNumMap);

workNumMap = [:];
workNumMap.put("a",1);
workNumMap.put("b","中班");
workNumMap.put("c","正常班次");
workNumMap.put("e","正常班次");
workNumMap.put("d","09:00");
workNumMap.put("f","09:30");
workNumMap.put("e","16:30");
workNumMap.put("g","17:00");
workNumMap.put("h","17:30");
workNumList.add(workNumMap);

workNumMap = [:];
workNumMap.put("a",1);
workNumMap.put("b","晚班");
workNumMap.put("c","正常班次");
workNumMap.put("e","16:30");
workNumMap.put("d","17:00");
workNumMap.put("f","17:30");
workNumMap.put("e","00:30");
workNumMap.put("g","01:00");
workNumMap.put("h","01:30");
workNumList.add(workNumMap);

context.workNumList = workNumList;

