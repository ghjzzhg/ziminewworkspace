import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

import java.text.SimpleDateFormat

List<GenericValue> caseProgressList = delegator.findByAnd("progressPartyGroupName", UtilMisc.toMap("caseId", parameters.caseId), UtilMisc.toList("progressIndex"), false);
List<Map<String, Object>> caseProgresses = new ArrayList<>();
for(GenericValue caseProgress: caseProgressList){
    Map<String, Object> map = new HashMap();
    map.putAll(caseProgress);
    String progressId = caseProgress.get("id");
    List<GenericValue> progressTemplateFileList = delegator.findByAnd("CaseProgressDataResource", UtilMisc.toMap("progressId", progressId, "template", "Y"), null, false);
    if(UtilValidate.isEmpty(progressTemplateFileList)){
        progressTemplateFileList = delegator.findByAnd("CaseProgressDataResource", UtilMisc.toMap("progressId", progressId,"template", null), null, false);
    }
    List<GenericValue> progressCompletedFileList = delegator.findByAnd("CaseProgressDataResource", UtilMisc.toMap("progressId", progressId, "template", "N"), null, false);
    map.put("progressTemplateFileList", progressTemplateFileList);
    map.put("progressCompletedFileList", progressCompletedFileList);
    //处理期限
    if(UtilValidate.isNotEmpty(map.get("dueBase"))){
        String dueBase = map.get("dueBase");
        String caseId = map.get("caseId");
        //截取前两位来判断类型是基准时间还是任务时间
        String type = dueBase.substring(0,2);
        Integer dueDay = 0;
        if(UtilValidate.isNotEmpty(map.get("dueDay"))){
            dueDay = Integer.parseInt(map.get("dueDay").toString());
        }
        String time = "";
        if(dueBase.equals("BT-start")){//case开始时间
            GenericValue caseInfo = EntityQuery.use(delegator).from("TblCase").where(UtilMisc.toMap("caseId", caseId)).queryOne()
            if(UtilValidate.isNotEmpty(caseInfo)) {
                time = caseInfo.get("startDate");
            }
        }else if(type.equals("BT")){//基准时间
            GenericValue baseTime = EntityQuery.use(delegator).from("TblCaseBaseTime").where(UtilMisc.toMap("caseId", caseId, "baseTimeId", dueBase)).queryOne()
            if(UtilValidate.isNotEmpty(baseTime)){
                time = baseTime.get("baseTime");
            }
        }else if(type.equals("NG")){//步骤完成时间
            if(UtilValidate.isNotEmpty(map.get("caseProgressGroupId"))){
                GenericValue caseProgressGroup = EntityQuery.use(delegator).from("TblCaseProgressGroup").where(UtilMisc.toMap("caseId", caseId, "id", map.get("caseProgressGroupId"))).queryOne()
                Integer seq = Integer.parseInt(caseProgressGroup.get("seq").toString());
                if(seq == 0){//如果步骤为0，说明步骤开始，得到case开始时间
                    GenericValue caseInfo = EntityQuery.use(delegator).from("TblCase").where(UtilMisc.toMap("caseId", caseId)).queryOne()
                    time = caseInfo.get("startDate");
                }else{
                    GenericValue caseProGroup = EntityQuery.use(delegator).from("TblCaseProgressGroup").where(UtilMisc.toMap("caseId", caseId, "templateGroupId", caseProgress.get("dueBase"))).queryOne();
                    if(UtilValidate.isNotEmpty(caseProgress.get("dueTask"))){
                        //如果基于步骤的任务，就指定指定步骤的信息
                        if(UtilValidate.isNotEmpty(caseProGroup)){
                            Integer dueTask = Integer.parseInt(caseProgress.get("dueTask").toString());
                            if(dueTask > 0){
                                dueTask = dueTask - 1;
                            }else{
                                dueTask = 0;
                            }
                            GenericValue caseProgressInfo = EntityQuery.use(delegator).from("TblCaseProgress").where(UtilMisc.toMap("caseId", caseId, "caseProgressGroupId", caseProGroup.get("id"), "progressIndex", dueTask)).orderBy("completeTime DESC").queryOne();
                            if(UtilValidate.isNotEmpty(caseProgressInfo)) {
                                time = caseProgressInfo.get("completeTime");
                            }
                        }
                    }else{
                        //如果没有基于步骤的任务，那么就得到上个步骤的信息
                        if(UtilValidate.isNotEmpty(caseProGroup)){
                            //得到上个任务的信息
                            List<GenericValue> caseProgressInfoList = EntityQuery.use(delegator).from("TblCaseProgress").where(UtilMisc.toMap("caseId", caseId, "caseProgressGroupId", caseProGroup.get("id"))).orderBy("completeTime DESC").queryList();
                            Boolean flag = false;
                            for(GenericValue genericValue : caseProgressInfoList){
                                if(UtilValidate.isEmpty(genericValue.get("completeTime"))){
                                    flag = true;
                                    break;
                                }
                            }
                            if(!flag){
                                if(UtilValidate.isNotEmpty(caseProgressInfoList)) {
                                    time = caseProgressInfoList.get(0).get("completeTime");
                                }
                            }

                        }
                    }
                }
            }
        }else{
            GenericValue caseProGroup = EntityQuery.use(delegator).from("TblCaseProgressGroup").where(UtilMisc.toMap("id", dueBase)).queryOne();
            if(UtilValidate.isNotEmpty(caseProgress.get("dueTask"))){
                //如果没有基于步骤的任务，就指定指定步骤的信息
                if(UtilValidate.isNotEmpty(caseProGroup)){
                    Integer dueTask = Integer.parseInt(caseProgress.get("dueTask").toString());
                    if(dueTask > 0){
                        dueTask = dueTask - 1;
                    }else{
                        dueTask = 0;
                    }
                    GenericValue caseProgressInfo = EntityQuery.use(delegator).from("TblCaseProgress").where(UtilMisc.toMap("caseId", caseId, "caseProgressGroupId", caseProGroup.get("id"), "progressIndex", dueTask)).orderBy("completeTime DESC").queryOne();
                    if(UtilValidate.isNotEmpty(caseProgressInfo)) {
                        time = caseProgressInfo.get("completeTime");
                    }
                }
            }else{
                //如果没有基于步骤的任务，那么就得到上个步骤的信息
                if(UtilValidate.isNotEmpty(caseProGroup)){
                    //得到上个任务的信息
                    List<GenericValue> caseProgressInfoList = EntityQuery.use(delegator).from("TblCaseProgress").where(UtilMisc.toMap("caseId", caseId, "caseProgressGroupId", caseProGroup.get("id"))).orderBy("completeTime DESC").queryList();
                    Boolean flag = false;
                    for(GenericValue genericValue : caseProgressInfoList){
                        if(UtilValidate.isEmpty(genericValue.get("completeTime"))){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        if(UtilValidate.isNotEmpty(caseProgressInfoList)) {
                            time = caseProgressInfoList.get(0).get("completeTime");
                        }
                    }

                }
            }
        }
        if(UtilValidate.isNotEmpty(time)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(time);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, dueDay);
            map.put("dueTime", sdf.format(c.getTime()))
        }
    }
    caseProgresses.add(map);
}
//request.setAttribute("caseProgresses", caseProgresses);
Map<String, List<Map>> progressMap = caseProgresses.groupBy {x -> x.get("caseProgressGroupId")}
List<GenericValue> caseProgressGroups = delegator.findByAnd("TblCaseProgressGroup", UtilMisc.toMap("caseId", parameters.caseId), UtilMisc.toList("seq"), true);
List<Map> caseProgressGroupList = new ArrayList<>();
for(GenericValue caseProgressGroup : caseProgressGroups){
    boolean isStepDone = false;
    boolean isPreDone = false;
    int seq = caseProgressGroup.get("seq");
    int doneAmount = 0;
    Map<String, Object> map = new HashMap();
    String groupId = caseProgressGroup.get("id");
    List childProgressList = progressMap.get(groupId);
    for(Map map1: childProgressList){
        if(!UtilValidate.isEmpty(map1.get("completeTime")) || "Y".equals(map1.get("skipped"))){
            doneAmount ++;
        }
    }
    if( UtilValidate.isNotEmpty(childProgressList) && doneAmount == childProgressList.size()){
        isStepDone = true;
    }
    if(seq == 0){
        isPreDone = true;
    }else if(caseProgressGroupList.get(seq - 1).get("isStepDone")){
        isPreDone = true;
    }
    map.put("isStepDone", isStepDone);
    map.put("isPreDone", isPreDone);
    map.put("childProgressList", childProgressList);
    map.putAll(caseProgressGroup);
    caseProgressGroupList.add(map);
}
request.setAttribute("caseProgresses", caseProgressGroupList);