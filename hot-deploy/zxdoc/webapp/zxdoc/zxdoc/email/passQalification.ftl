
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<#assign hostUrl = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "host-url")>
<#assign contextPath = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "contextPath")>
<p>
    亲爱的资协网用户：<br/>
    您好！您的账户已通过实名认证，您可以通过下面的链接进入资协网进行进一步操作，例如添加子账户：<br/>
    ${hostUrl}<br/>
    如果以上链接无法点击，请将上面的地址复制到你的浏览器（如IE）的地址栏中打开。<br/>
    <br/>
    谢谢！<br/>
</p>
</body>
</html>
