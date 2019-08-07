import org.ofbiz.base.util.UtilMisc

vehicleList = delegator.findByAnd("VehicleDetail",UtilMisc.toMap("logicDelete", "N"));
context.vehicleList = vehicleList;