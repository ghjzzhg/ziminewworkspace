import org.ofbiz.base.util.UtilHttp
import org.ofbiz.base.util.UtilMisc
String result = parameters.get("result");
String sendId = parameters.get("sendId");
if (result.equals("approve")){
    genericValue = delegator.findByPrimaryKey("TblSalarySend",UtilMisc.toMap("sendId",sendId));
    genericValue.setString("state","已审")
    genericValue.store();
}else if(result.equals("disapprove")){
    genericValue = delegator.findByPrimaryKey("TblSalarySend",UtilMisc.toMap("sendId",sendId));
    genericValue.setString("state","未通过")
    genericValue.store();
}else if (result.equals("confirmSent")){
    genericValue = delegator.findByPrimaryKey("TblSalarySend",UtilMisc.toMap("sendId",sendId));
    genericValue.setString("state","已发")
    genericValue.store();
}

