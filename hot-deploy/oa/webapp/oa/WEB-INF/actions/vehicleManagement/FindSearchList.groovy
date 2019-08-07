import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue

String vehicleId = parameters.get("vehicleId");
GenericValue vehicle = delegator.findByPrimaryKey("TblVehicleManagement", UtilMisc.toMap("vehicleId", vehicleId));
String buyDate = vehicle.get("buyDate").toString().substring(0, 4);
int buyYear = Integer.parseInt(buyDate);
List<Map> yearList = new ArrayList<>();
int year = (new Date().getYear()+1900);
for(int i = buyYear; i <= year; i++){
    Map map = new HashMap();
    map.put("year", buyYear+"");
    yearList.add(map);
    buyYear++;
}
List<Map> monthList = new ArrayList<>();
int month = 1;
for(int i = 0; i < 12; i++){
    Map map = new HashMap();
    map.put("month", month+"");
    monthList.add(map);
    month++;
}
context.monthList = monthList;
context.yearList = yearList;