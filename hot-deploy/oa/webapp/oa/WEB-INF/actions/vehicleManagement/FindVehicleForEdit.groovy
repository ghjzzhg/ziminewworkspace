import org.ofbiz.base.util.UtilMisc

vehicleMap = delegator.findByPrimaryKey("TblVehicleManagement",UtilMisc.toMap("vehicleId",parameters.get("vehicleId")));
context.vehicleMap = vehicleMap;