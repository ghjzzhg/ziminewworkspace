import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate

import java.text.SimpleDateFormat

orderId = parameters.get("orderId");
SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
orderVehicleDetailMap = delegator.findByPrimaryKey("VehicleOrderDetail",UtilMisc.toMap("orderId",orderId));
if(UtilValidate.isNotEmpty(orderVehicleDetailMap)){
    orderVehicleDetailMap.put("startDate",format.format(orderVehicleDetailMap.get("startDate")));
    orderVehicleDetailMap.put("endDate",format.format(orderVehicleDetailMap.get("endDate")));
    if(UtilValidate.isNotEmpty(orderVehicleDetailMap.get("reviewDate"))){
        orderVehicleDetailMap.put("reviewDate",format.format(orderVehicleDetailMap.get("reviewDate")));
    }
}
context.orderVehicleDetailMap = orderVehicleDetailMap;
