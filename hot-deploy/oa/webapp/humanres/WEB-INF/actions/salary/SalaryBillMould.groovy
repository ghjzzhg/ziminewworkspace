import freemarker.template.Configuration
import freemarker.template.Template
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.oa.StringTemplateLoader

import java.text.SimpleDateFormat

public StringWriter printSalaryBill(String staffId,GenericValue sendValue,String month,String year){
    String sendId = sendValue.get("sendId");
    monthFor = Long.valueOf(month);
    yearFor = Long.valueOf(year);
    checkingList = delegator.findByAnd("TblCheckingFor",UtilMisc.toMap("staff",staffId,"checkingInMonth",monthFor,"checkingInYear",yearFor))
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date newDate = format.parse(year + "-" + month + "-01");
    Map<String,Object> successMap = dispatcher.runSync("getListOfWorkByStaffAndDate",UtilMisc.toMap("staffId",staffId,"date",newDate,"userLogin",userLogin));
    GenericValue listOfWork = (GenericValue)successMap.get("returnValue");//获得员工班次
    Integer workHowLongDay = 0;
    if(null != listOfWork && listOfWork.size() > 0){
        workHowLongDay = listOfWork.getInteger("workHowLongDay") - 1;//应出勤折算天数(天)
    }
    Map root = new HashMap();
    personMap = delegator.findByAnd("StaffInformationDetailView",UtilMisc.toMap("partyId",staffId))[0];
    List <GenericValue> personSalaryItems = delegator.findByAnd("TblSendSalaryInfo",UtilMisc.toMap("sendId",sendId));
    root.put("user_name",personMap.get("fullName")==null?"无":personMap.get("fullName"));
    root.put("user_dept",personMap.get("groupName")==null?"无":personMap.get("groupName"));
    root.put("user_code",personMap.get("workerSn")==null?"无":personMap.get("workerSn"));
    root.put("user_position",personMap.get("postName")==null?"无":personMap.get("postName"));
    root.put("s_year",year);
    root.put("s_month",month);
    root.put("actualDays",workHowLongDay);
    root.put("attendanceDays",checkingList.size()/2);
    root.put("actualSalary", sendValue.get("paySalary"));

    for(Map map1:personSalaryItems){
        if (map1.get("entryId").equals("10001")){
            root.put("salary",map1.get("amount")==null?"":map1.get("amount"));
        }
    }

    List<GenericValue> entryList = EntityQuery.use(delegator).select().from("TblSalaryEntry").where(UtilMisc.toMap("systemType",null)).queryList();
    for(GenericValue entry : entryList){
        String entryId = entry.get("entryId");
        if("10001".equals(entryId)){
            continue;
        }
        String value = "";
        for(GenericValue salaryEntry : personSalaryItems){
            String saEntryId = salaryEntry.get("entryId");
            if(entryId.equals(saEntryId)){
                value = salaryEntry.get("amount");
            }
        }
        root.put(entry.get("amount").toString(),value==""?"无":entry.get("amount").toString())
    }
    salaryMouldList = delegator.findAll("TblSalaryBillMould",false);
    String freeMarker = "";
    for(Map map:salaryMouldList){
        freeMarker = map.get("mouldContent");
    }
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(new StringTemplateLoader(freeMarker));
    cfg.setDefaultEncoding("GB-2312");
    Template template = cfg.getTemplate("");
    StringWriter writer = new StringWriter();
    template.process(root, writer);
    return  writer;
}
context.freeMarkerList = freeMarkerList;

