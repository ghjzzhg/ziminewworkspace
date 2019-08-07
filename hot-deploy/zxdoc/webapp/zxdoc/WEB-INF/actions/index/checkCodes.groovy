package index

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

/**
 * Created by Administrator on 2016/10/24.
 */
String code1 = request.getParameter("code1");
String phoneNum = request.getParameter("phoneNum");
String verificationCode = request.getParameter("verificationCode");
int captchaCode = 1;
String code = request.getSession().getAttribute("_CAPTCHA_CODE_");
if(code!=null) {
    String imgCode = "";
    for (int i = 2; i < 8; i++) {
        imgCode = imgCode + "" + code[i];
    }
    if (code1.equals(imgCode)) {
        captchaCode = 0;
    }
}

int phoneCode = 1;
List<GenericValue> phoneNumList = EntityQuery.use(delegator).from("TblPhoneCode").where(UtilMisc.toMap("phoneNum",phoneNum)).orderBy("createCodeDate DESC").queryList();
if(UtilValidate.isNotEmpty(phoneNumList)){
    GenericValue phoneInfo = phoneNumList.get(0);
    if(phoneInfo.get("code").equals(verificationCode)){
        phoneCode = 0;
    }
}
//result：0正常，1图片验证码出错，2手机验证码出错，3图片和手机验证码都出错
String result = "";
if(captchaCode == 1 && phoneCode == 1){
    result = "短信验证码和图片验证码均出错，请检查！";
}else if(captchaCode == 0 && phoneCode == 1){
    result = "短信验证码出错，请检查！";
}else if(captchaCode == 1 && phoneCode == 0){
    result = "图片验证码出错，请检查！";
}

request.setAttribute("result",result);