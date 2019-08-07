import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.webapp.control.LoginWorker

//移除session中的登录账户
LoginWorker.userSessions.remove(parameters.userLoginId);
GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", parameters.userLoginId), false);
userLogin.setString("enabled", "J");
//清空禁用时间，防止系统恢复该账户
userLogin.setString("disabledDateTime",null);
userLogin.store();
request.setAttribute("data", "禁用成功");