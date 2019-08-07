import javolution.util.FastList
holidayInfoList = FastList.newInstance();
checkingStatisticsMap = [:];
checkingStatisticsMap.put("q", 0);
checkingStatisticsMap.put("w", "2014-07-18");
checkingStatisticsMap.put("e", "2014-07-20");
checkingStatisticsMap.put("r", "个人");
checkingStatisticsMap.put("t", "陆天");
checkingStatisticsMap.put("y", "");
holidayInfoList.add(checkingStatisticsMap);

checkingStatisticsMap = [:];
checkingStatisticsMap.put("q", 1);
checkingStatisticsMap.put("w", "2013-09-17");
checkingStatisticsMap.put("e", "2013-09-21");
checkingStatisticsMap.put("r", "个人");
checkingStatisticsMap.put("t", "何晨");
checkingStatisticsMap.put("y", "");
holidayInfoList.add(checkingStatisticsMap);


context.holidayInfoList = holidayInfoList;

