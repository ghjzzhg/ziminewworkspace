package index

/**
 * 解析错误信息，值得注意的是，只有密码修改错误才会进入这个groovy
 * gf
 */
String msg = parameters._ERROR_MESSAGE_;
msg = msg.indexOf("密码不正确") ? "密码错误" : "其他错误";
request.setAttribute("msg",msg);