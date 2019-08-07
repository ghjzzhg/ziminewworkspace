import javolution.util.FastMap
import org.ofbiz.base.util.UtilHttp
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.ModelService;

java.util.Enumeration<java.lang.String> parameterNames = request.getParameterNames();
Iterator ite = parameterNames.iterator();
Map itemValue = FastMap.newInstance();
while (ite.hasNext()){
    String name = ite.next();
    if(name.endsWith("_value")){
        itemValue.put(name.substring(0, name.indexOf("_value")), request.getParameter(name));
    }
}
context.itemScores = itemValue;
ModelService model = dispatcher.getDispatchContext().getModelService("savePerfExam");
serviceContext = model.makeValid(context, ModelService.IN_PARAM);
serviceContext1 = model.makeValid(UtilHttp.getCombinedMap(request), ModelService.IN_PARAM);
serviceContext.putAll(serviceContext1);
result = runService("savePerfExam", serviceContext);

Map data = result.get("data");
if(data){
    examId = data.get("examId");
    scopeContext = UtilMisc.toMap("userLogin", userLogin);
    scopeContext.put("entityName", "TblPerfExam");
    scopeContext.put("dataId", examId);
    scopeContext.put("deptOnly", parameters.get("viewScope_dept_only"));
    scopeContext.put("deptLike", parameters.get("viewScope_dept_like"));
    scopeContext.put("positionOnly", parameters.get("viewScope_position_only"));
    scopeContext.put("positionLike", parameters.get("viewScope_positionLike"));
    scopeContext.put("levelOnly", parameters.get("viewScope_level_only"));
    scopeContext.put("levelLike", parameters.get("viewScope_level_like"));
    scopeContext.put("userValue", parameters.get("viewScope_user"));

    runService("saveDataScope", scopeContext);
}
