import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
String blankCaseSessionKey = parameters.blankCaseSessionKey;
if(UtilValidate.isNotEmpty(blankCaseSessionKey)){
    Map<String, Object> blankCaseData = request.getSession().getAttribute(blankCaseSessionKey);
    context.nodes = blankCaseData.get("nodes");
    if(context.nodes == null){
        context.nodes = new ArrayList<>();
    }
    context.caseName = blankCaseData.get("case").get("title");
    context.isFromCase = "true";
}else{
    context.template = from("TblCaseTemplate").where("id", parameters.templateId).cache().queryOne();
    context.nodes = from("TblCaseTemplateNodeGroup").where("templateId", parameters.templateId).cache().orderBy("seq").queryList();
    if(parameters.isFromCase == "true"){
        //更新case的 模板 信息
        long mesc = 24 * 3600 * 1000;
        Date date = new Date();
        List<GenericValue> templateNodes = delegator.findByAnd("TblCaseTemplateNode", UtilMisc.toMap("templateId", parameters.templateId), null, false);
        int dueDays = 0;
        for (GenericValue templateNode : templateNodes) {
            dueDays += Integer.parseInt(templateNode.get("dueDay") == null ? "0" : templateNode.get("dueDay") + "");
        }
        Date completeDate = new Date(date.getTime() + dueDays * mesc);
        //将模板信息存入(更新)case
        String caseCategory = delegator.findOne("TblCaseTemplate", UtilMisc.toMap("id", parameters.templateId), false).get("templateKey");
        GenericValue caseInfo = from("TblCase").where("caseId", parameters.caseId).queryOne();
        caseInfo.put("caseCategory", caseCategory);
        caseInfo.put("startDate", new java.sql.Date(date.getTime()));
        caseInfo.put("completeDate", new java.sql.Date(completeDate.getTime()));
        caseInfo.put("caseTemplate", parameters.templateId);
        caseInfo.store();
        context.isFromCase = "true"
        context.caseId = parameters.caseId;
    }else {

        if(UtilValidate.isNotEmpty(parameters.templateId)){
            context.isFromCase = "false"
        }else{
            context.nodes = from("TblCaseProgressGroup").where("caseId", parameters.caseId).cache().orderBy("seq").queryList();
            context.isFromCase = "true"
            context.caseName = delegator.findByAnd("TblCase", UtilMisc.toMap("caseId", parameters.caseId), null, false).get(0).get("title");
            context.caseId = parameters.caseId
        }
    }
}