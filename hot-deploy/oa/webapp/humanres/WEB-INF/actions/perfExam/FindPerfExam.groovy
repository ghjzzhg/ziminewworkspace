import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.model.ModelReader
import org.ofbiz.oa.OAUtil

years = [];
yearNow = Calendar.getInstance().get(Calendar.YEAR);
monthNow = Calendar.getInstance().get(Calendar.MONTH);
i = 0;
while (i < 5){
    years.push([value: yearNow - i, label: (yearNow-i) + '年']);
    i++;
}
context.years = years;
context.evaluateDate = UtilDateTime.nowDateString("yyyy-MM-dd HH:mm:ss");
context.evaluateMonth = monthNow;
context.evaluateYear = yearNow;

searchYear = parameters.get("evaluateYear");
searchMonth = parameters.get("evaluateMonth");
if(!searchYear){
    searchYear = yearNow;
}
if(!searchMonth){
    searchMonth = monthNow;
}

//根据表名、范围属性、当前登录人partyId查询可见的记录id
scopedIds = OAUtil.getScopedValuesByParty(delegator, "TblPerfExam", null, userLogin.get("partyId"));


DynamicViewEntity viewEntity = new DynamicViewEntity();
viewEntity.addMemberEntity("T", "TblPerfExam");//目标表
viewEntity.addMemberEntity("D", "TblDataScope");//范围限定表
ModelKeyMap keyMap = new ModelKeyMap("examId", "dataId");//限定表中数据id与目标表的主键对应
viewEntity.addViewLink("T", "D", true, UtilMisc.toList(keyMap));
viewEntity.setEntityName("PerfExamByScope");//可以不设置，设置名称是为了可以将其作为一个viewentity与其他表进一步关联
viewEntity.addAliasAll("T", null);//包括目标表的所有字段
viewEntity.addAlias("D", "dataId");//包括限定表的数据id字段，用于下面的条件过滤。

//正常查询条件
searchCon = EntityCondition.makeCondition(
[EntityCondition.makeCondition("evaluateYear", EntityOperator.EQUALS, new Long(searchYear)),
 EntityCondition.makeCondition("evaluateMonth", EntityOperator.EQUALS, new Long(searchMonth)),
]
)

//通用范围限定条件
scopeCon = EntityCondition.makeCondition([EntityCondition.makeCondition("dataId", EntityOperator.EQUALS, null), EntityCondition.makeCondition("dataId", EntityOperator.IN, scopedIds)], EntityOperator.OR)

//因目标表与范围限定表关联存在重复的记录，查询时需要将范围限定表的列排除在外进而用distinct去重
perfExamModel = ModelReader.getModelReader(delegator.getDelegatorName()).getModelEntity("TblPerfExam");
Set fieldNames = UtilMisc.toSet(perfExamModel.getPkFieldNames())
fieldNames.addAll(perfExamModel.getNoPkFieldNames())
fieldNames.remove("createdTxStamp");
fieldNames.remove("createdStamp");
fieldNames.remove("lastUpdatedStamp");
fieldNames.remove("lastUpdatedTxStamp");

//只查询目标表的所有字段，使用distinct去重
perfExamList = select(fieldNames).from(viewEntity).where(EntityCondition.makeCondition(searchCon, scopeCon)).distinct().queryList();

pIte = perfExamList.iterator();
existExams = [];
while(pIte.hasNext()){
    exam = pIte.next();
    existExams.push(exam.get("staff") + exam.get("perfExamType"));
}

//查询已计划的考核人员
perfExamPersons = from("TblPerfExamPerson").queryList();
for(person in perfExamPersons){
    if(!existExams.contains(person.get("staff") + person.get("perfExamType"))){
        perfExamList.add(person);
    }
}


//perfExamList = FastList.newInstance();
//names = ['徐德柱', '陆爱心', '吕银龙', '何维涛', '刘学晓']
//departments = ['研发部', '人事部', '行政部门']
//positions = ['开发工程师', '项目经理', '行政主管']
//for(i in 0 .. 4){
//    perfExam = [:];
//    perfExam.put("name", names[i]);
//    perfExam.put("department", departments[i%3]);
//    perfExam.put("position", positions[i%3]);
//    perfExam.put("period", '2015年6月');
//    perfExam.put("initScore", 80);
//    perfExam.put("additionalScore", 3);
//    perfExam.put("finalScore", "");
//    perfExam.put("perfExamResult", "B+");
//    perfExam.put("examSponsor", "考评人");
//    perfExam.put("examDate", new Date());
//    perfExam.put("examReviewer", "初审人");
//    perfExam.put("reviewDate", new Date());
//    perfExam.put("examApprover", "终审人");
//    perfExam.put("approveDate", new Date());
//    perfExam.put("examFinalizer", "归档人");
//    perfExam.put("finalizeDate", new Date());
//    perfExamList.add(perfExam);
//}

context.perfExamList = perfExamList;