import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.util.EntityQuery

resourceMap = delegator.findByPrimaryKey("TblResourceManagement",UtilMisc.toMap("resourceId",parameters.get("resourceId")));
userSate = EntityQuery.use(delegator).select().from("Enumeration").where(UtilMisc.toMap("enumTypeId","USE_STATE")).queryList();
context.resourceMap = resourceMap;
context.userSate = userSate;