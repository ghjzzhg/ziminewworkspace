import activiti.ActivitiProcessUtils
import org.ofbiz.base.util.UtilValidate

String viewType = request.getParameter("viewType");
if(UtilValidate.isEmpty(viewType)){
    viewType = context.get("viewType");
}
String view = request.getParameter("view");
if(UtilValidate.isEmpty(view)){
    view = context.get("view");
}
if("OFBIZ_SCREEN".equals(viewType)){//ofbiz view 的格式一般为 component://[name].....xml#screenName
    if(view.contains("#")){
        context.put("location", view.substring(0, view.indexOf("#")));
        context.put("screenName", view.substring(view.indexOf("#") + 1));
    }else{
        context.put("location", "");
        context.put("screenName", view);
    }
}else if("USER_SCREEN".equals(viewType)){
    context.put("customTaskFormId", view);
}

Map map = ActivitiProcessUtils.prepareTaskFormData(context, request.getParameter("taskId"));
context.putAll(map);
