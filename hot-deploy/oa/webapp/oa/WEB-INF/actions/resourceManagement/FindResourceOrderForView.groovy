import org.ofbiz.base.util.UtilMisc
orderId = parameters.get("orderId");
resourceForOrderMap =  delegator.findByPrimaryKey("ResourceOrderDetail",UtilMisc.toMap("orderId",orderId));
context.orderResourceView = resourceForOrderMap;
