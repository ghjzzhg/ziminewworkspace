<%@ page import="org.ofbiz.base.util.UtilCodec" %>
<html>
<body>
<div>
<%
    String errorMsg = (String) request.getAttribute("_ERROR_MESSAGE_");
    System.out.println(UtilCodec.getDecoder("html").decode(errorMsg));
    try {
        if (errorMsg != null && errorMsg.contains("Error in Service")) {
            errorMsg = errorMsg.substring(errorMsg.indexOf("Error in Service"));
            errorMsg = UtilCodec.getDecoder("html").decode(errorMsg);
            errorMsg = errorMsg.substring(errorMsg.indexOf("]: ") + 3);
        }
    }catch (Exception e){
        e.printStackTrace();
    }
%>
<div id="result" style="display: none">false</div>
<div id="msg"><%=errorMsg%></div>
</div>
</body>
</html>