import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc

context.exam = [];
examId = parameters.get("id")
if(examId){
    context.exam = delegator.findOne("TblPerfExam", UtilMisc.toMap("examId", examId), true);
}
context.perfExamTypes = from("TblPerfExamItemType").where(UtilMisc.toMap("parentTypeId", "1")).queryList();
loginPerson = delegator.findOne("Person", UtilMisc.toMap("partyId", userLogin.get("partyId")), true);
context.evaluatorName = loginPerson.get("fullName") ? loginPerson.get("fullName") : loginPerson.get("lastName") + loginPerson.get("firstName");
years = [];
yearNow = Calendar.getInstance().get(Calendar.YEAR);
monthNow = Calendar.getInstance().get(Calendar.MONTH);
i = 0;
while (i < 5){
    years.push(yearNow - i);
    i++;
}
context.years = years;
context.evaluateDate = UtilDateTime.nowDateString("yyyy-MM-dd HH:mm:ss");
context.monthNow = monthNow;