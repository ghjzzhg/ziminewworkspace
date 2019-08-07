import org.ofbiz.base.util.UtilMisc

TypeManagementList=delegator.findAll("TblTypeManagementList",false);
context.TypeManagementList = TypeManagementList;

TemplateManagementList=delegator.findAll("TblNoticeTemplate",false);
context.TemplateManagementList = TemplateManagementList;

Map map = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblWorkContactList","prefix","liasion","numName","number","userLogin",userLogin));;
String number = map.get("number");
context.number = number;



