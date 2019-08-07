import org.ofbiz.base.util.UtilMisc

String entryId = parameters.get("entryId");
entryMap = delegator.findByPrimaryKey("TblSalaryEntry",UtilMisc.toMap("entryId",entryId));
entryMap.setNonPKFields(parameters);
entryMap.store();
