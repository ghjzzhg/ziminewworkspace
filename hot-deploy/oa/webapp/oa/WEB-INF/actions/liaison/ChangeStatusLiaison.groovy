import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

public Map<String, Object> changeLiaisonStatus() {
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    String msg = "";
    msg = "联络单状态更改失败！"
    String contactListId = parameters.get("contactListId");
    String reviewTheStatus = parameters.get("reviewTheStatus");
    if (UtilValidate.isNotEmpty(reviewTheStatus)) {
        GenericValue genericValue = delegator.findByPrimaryKey("TblWorkContactList", UtilMisc.toMap("contactListId", contactListId));
        genericValue.put("reviewTheStatus", reviewTheStatus);
        genericValue.store();
        msg = "联络单状态更改成功！"
    }

    reviewTheStatusMap = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", reviewTheStatus));
    String reviewTheStatusString = reviewTheStatusMap.get("description");

    successResult.put("data",UtilMisc.toMap("statusName",reviewTheStatusString,"msg",msg,"statusId",reviewTheStatus));
    return successResult;
}