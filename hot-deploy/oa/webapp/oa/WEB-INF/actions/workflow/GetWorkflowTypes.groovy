/*

if(itemTypeList){
    for (GenericValue itemType : itemTypeList) {
        perfExamTypes.add([id: itemType.typeId, code: itemType.typeId, pId: itemType.parentTypeId, name: itemType.description]);
    }
}
*/

workflowTypes = [[id: "1", code: "1", pId: "", name: "工作流类型"],
                 [id: "11", code: "11", pId: "1", name: "财务"],
                 [id: "12", code: "12", pId: "1", name: "采购"],
                 [id: "13", code: "13", pId: "1", name: "差旅"],
                 [id: "14", code: "14", pId: "1", name: "公文"],
                 [id: "15", code: "15", pId: "1", name: "人事"],
                 [id: "111", code: "111", pId: "11", name: "报销"],
                 [id: "112", code: "112", pId: "11", name: "借款"],
                 [id: "121", code: "121", pId: "12", name: "办公用品采购"],
                 [id: "122", code: "122", pId: "12", name: "原材料采购"],
                 [id: "151", code: "151", pId: "15", name: "考勤"],
                 [id: "152", code: "152", pId: "15", name: "员工管理"]
];

request.setAttribute("data", workflowTypes);