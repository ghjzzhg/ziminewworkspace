import javolution.util.FastList
import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue

String noticeId = parameters.get("noticeId");
GenericValue userLogin = (GenericValue) context.get("userLogin");
String partyId = userLogin.get("partyId");
String logicDelete = "N";
TemplateManagementList = delegator.findByAnd("TblNoticeTemplate",
        UtilMisc.toMap("createPerson", partyId,"logicDelete",logicDelete));
List<GenericValue> templateListNew = new ArrayList<>();

if(UtilValidate.isNotEmpty(noticeId)){
    notice = delegator.findByPrimaryKey("TblNotice", UtilMisc.toMap("noticeId", noticeId));
    if(UtilValidate.isNotEmpty(notice.get("useTemplate"))){
        String useTemplate1 = notice.get("useTemplate").toString();
        noticeTemplate = delegator.findByPrimaryKey("TblNoticeTemplate", UtilMisc.toMap("noticeTemplateId", useTemplate1));
        templateListNew.add(noticeTemplate);
        if(UtilValidate.isNotEmpty(TemplateManagementList)){
            for(GenericValue g : TemplateManagementList){
                if(!g.get("noticeTemplateId").toString().equals(useTemplate1)){
                    templateListNew.add(g);
                }
            }
        }
    }
    if(UtilValidate.isEmpty(notice.get("useTemplate"))){
        if(UtilValidate.isNotEmpty(TemplateManagementList)){
            for(GenericValue g : TemplateManagementList){
                templateListNew.add(g);
            }
        }
    }
}
List<GenericValue> resultList = delegator.findByAnd("StaffDetail",UtilMisc.toMap("partyId",partyId));
Map<String,Object> resultMap = new HashMap<String,Object>();
if (resultList.size()!=0){
    Map<String,Object> map = resultList.get(0);
    resultMap.put("groupName",map.get("departmentName"));
    resultMap.put("departmentId",map.get("departmentId"));
    resultMap.put("partyId",partyId);
}
List<Map> noticeYearList = new ArrayList<>();
int year = (new Date().getYear()+1900);
for(int i = 0; i < 4; i++){
    Map map = new HashMap();
    map.put("noticeYear", year+"");
    noticeYearList.add(map);
    year++;
}
Map uniqueNumber = dispatcher.runSync("searchUniqueNumber",UtilMisc.toMap("entityName","TblNotice", "prefix","bumphNotice", "numName","noticeNumber", "userLogin",userLogin));
context.number = uniqueNumber.get("number");
context.noticeYearList = noticeYearList;
context.groupName = resultMap.get("groupName");
context.departmentId = resultMap.get("departmentId");
context.TemplateManagementList = TemplateManagementList;
context.templateListNew = templateListNew;


