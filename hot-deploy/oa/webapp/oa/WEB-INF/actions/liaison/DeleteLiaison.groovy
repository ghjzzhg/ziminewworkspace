import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

public Map<String, Object> deleteLiaison() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String contactListId = parameters.get("contactListId");
    GenericValue genericValue = delegator.findByPrimaryKey("TblWorkContactList", UtilMisc.toMap("contactListId", contactListId));
    genericValue.put("reviewTheStatus", "LIAISON_STATUS_FOUR");
    genericValue.store();
    msg = "作废成功！"
    successResult.put("data",msg);
    return successResult;
}