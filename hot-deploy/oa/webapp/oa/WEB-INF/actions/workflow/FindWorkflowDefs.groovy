/*type = parameters.get("type");
perfExamItems = [];
if(type && !"1".equals(type)){
    perfExamItems = from("TblPerfExamItem").where(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("parentType", EntityOperator.EQUALS, type), EntityCondition.makeCondition("type", EntityOperator.EQUALS, type)), EntityOperator.OR)).orderBy("parentType", "type").queryList();
}else{
    perfExamItems = from("TblPerfExamItem").orderBy("parentType", "type").queryList();
}
context.perfExamItemsList = perfExamItems;

DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
dynamicViewEntity.addMemberEntity("T", "TblPerfExamItem");
dynamicViewEntity.addAlias("T", "count", "itemId", null, null, false, "count");
dynamicViewEntity.addAlias("T", "type", "type", null, null, true, null);
typeCounts = from(dynamicViewEntity).queryList();
typeCountMap = [:];
if(typeCounts){
    for (GenericValue count : typeCounts) {
        typeCountMap.put(count.get("type"), count.get("count"));
    }
}
context.typeCountMap = typeCountMap;*/

workflowDefs = [[key:"WF-150601001", name: '请假申请', type: '自由流程', description: '请假申请流程'],
             [key:"WF-150601002", name: '借款流程', type: '固定流程', description: '某公司借款流程'],
             [key:"WF-150601003", name: '公务报销', type: '固定流程', description: '报销流程'],
             [key:"WF-150601004", name: '加班申请', type: '固定流程', description: '加班申请'],
             [key:"WF-150601005", name: '调休申请', type: '固定流程', description: '调休申请'],
             [key:"WF-150601006", name: '印章申请', type: '固定流程', description: '印章申请']
];

context.workflowDefs = workflowDefs;