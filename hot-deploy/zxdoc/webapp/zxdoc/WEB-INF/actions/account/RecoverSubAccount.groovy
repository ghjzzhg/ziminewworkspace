package account

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue

GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", parameters.userLoginId), false);
userLogin.setString("enabled", "Y");
userLogin.set("disabledDateTime", null);
userLogin.set("successiveFailedLogins", 0L);
userLogin.store();
request.setAttribute("data", "解除成功");