import org.ofbiz.base.util.StringUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

/*
Map<String, Object> systemAccount = UtilMisc.toMap("userLogin", context.get("userLogin"));
String password = UtilProperties.getPropertyValue("zxdoc", "password");
Map<String,Object> paramMap = UtilMisc.toMap("userLoginId", parameters.userLoginId, "newPassword", password, "newPasswordVerify", password);
paramMap.putAll(systemAccount);
runService("updatePassword", paramMap);
request.setAttribute("password", password);*/
Map<String, Object> systemAccount = UtilMisc.toMap("userLogin", context.get("userLogin"));
String userLoginId = parameters.userLoginId;
GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId", userLoginId)).queryOne();
String partyId = userLogin.get("partyId");
GenericValue EmailAddressInfo = EntityQuery.use(delegator).from("EmailAddressInfo").where("partyId", partyId, "thruDate", null).queryOne();
String email = EmailAddressInfo.get("infoString");
Random random = new Random();
String newPassword = "";
newPassword += (int) (Math.random() * 9 + 1);
for (int i = 0; i < 5; i++) {
    newPassword += (int) (Math.random() * 10);
}
//密码重置
Map<String,Object> paramMap = UtilMisc.toMap("userLoginId", userLoginId, "newPassword", newPassword, "newPasswordVerify", newPassword);
paramMap.putAll(systemAccount);
runService("updatePassword", paramMap);
//发送邮件
List<String> emailAddresses = new ArrayList<>();
emailAddresses.add(email);
result = runService("sendEmailNotice", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_PASS_RESETPASSWORD", "toAddress", StringUtil.join(emailAddresses, ","), "dataResourceIds", null,"bodyParameters", UtilMisc.toMap("password",newPassword)));
request.setAttribute("data","重置成功");


