import org.ofbiz.base.util.UtilMisc

String content = parameters.get("content");
mouldId = delegator.getNextSeqId("TblSalaryBillMould").toString();
genericValue = delegator.makeValidValue("TblSalaryBillMould",UtilMisc.toMap("mouldId",mouldId));
genericValue.setString("mouldContent",content);
genericValue.create();


