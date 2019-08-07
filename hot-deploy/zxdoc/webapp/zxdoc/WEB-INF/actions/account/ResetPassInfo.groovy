package account

import org.ofbiz.base.util.StringUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/11/16.
 */
Map<String, Object> systemAccount = UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne());
String userLoginId = parameters.username;
String email = parameters.email;
/*GenericValue person = EntityQuery.use(delegator).from("Person").where(UtilMisc.toMap("fullName", fullName)).queryOne();*/
GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId",userLoginId).queryOne();
if(userLogin!=null) {
    String partyId = userLogin.get("partyId");
/*
GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where(UtilMisc.toMap("userLoginId", userLoginId)).queryOne();
String partyId = userLogin.get("partyId");*/
    GenericValue EmailAddressInfo = EntityQuery.use(delegator).from("EmailAddressInfo").where("partyId", partyId, "thruDate", null).queryOne();
    String emails = EmailAddressInfo.get("infoString");
    if (emails.equals(email)) {
        Random random = new Random();
        String newPassword = "";
        newPassword += (int) (Math.random() * 9 + 1);
        for (int i = 0; i < 5; i++) {
            newPassword += (int) (Math.random() * 10);
        }
//密码重置
        Map<String, Object> paramMap = UtilMisc.toMap("userLoginId", userLoginId, "newPassword", newPassword, "newPasswordVerify", newPassword);
        paramMap.putAll(systemAccount);
        runService("updatePassword", paramMap);
//发送邮件
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add(email);
        result = runService("sendEmailNotice", UtilMisc.toMap("userLogin", from("UserLogin").where("userLoginId", "system").queryOne(), "templateId", "ZXDOC_PASS_RESETPASSWORD", "toAddress", StringUtil.join(emailAddresses, ","), "dataResourceIds", null, "bodyParameters", UtilMisc.toMap("password", newPassword)));
        request.setAttribute("data", "重置成功");
    } else {
        request.setAttribute("data", "您的登录名和邮箱不匹配！");
    }
}else
{
    request.setAttribute("data", "当前联系人不存在！");
}