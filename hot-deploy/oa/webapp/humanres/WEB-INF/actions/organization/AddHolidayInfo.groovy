import javolution.util.FastMap

param = parameters.get("param");

if("2".equals(param)){
    addHolidayInfoMap = FastMap.newInstance()
/*    addHolidayInfoMap.put("a","2015-05-12");
    addHolidayInfoMap.put("b","2015-05-12");*/
    addHolidayInfoMap.put("c","1");
    addHolidayInfoMap.put("d","test");
    addHolidayInfoMap.put("e","test");
    context.addHolidayInfoMap = addHolidayInfoMap;
}
context.param = param;


