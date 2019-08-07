import javolution.util.FastList
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityListIterator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.service.ServiceUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

public Map<String,Object> searchLiaison(){
    int viewIndex = 0;
    try {
        viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
    } catch (Exception e) {
        viewIndex = 0;
    }
    int totalCount = 0;
    int viewSize = 5;
    try {
        viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
    } catch (Exception e) {
        viewSize = 5;
    }
    int lowIndex = viewIndex * viewSize + 1;
    int highIndex = (viewIndex + 1) * viewSize;

    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String responseTimeStart = context.get("responseTimeStart");
    String responseTimeEnd = context.get("responseTimeEnd");
    String title = context.get("title");
    String contactListType = context.get("contactListType");
    String reviewTheStatus = context.get("reviewTheStatus");
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.get("partyId").toString();
    List<EntityCondition> conditionList = FastList.newInstance();
    if (UtilValidate.isNotEmpty(responseTimeStart) && UtilValidate.isNotEmpty(responseTimeEnd)) {
        conditionList.add(
                EntityCondition.makeCondition("createdStamp", EntityOperator.BETWEEN,
                        UtilMisc.toList(
                                new Timestamp(format.parse(responseTimeStart).getTime()),
                                new Timestamp(format.parse(responseTimeEnd).getTime())
                        )
                ));
    }
    if (UtilValidate.isNotEmpty(partyId)){//�鿴Ȩ��
        List<String> partyIds = new ArrayList<String>();
        partyIds.add(partyId);
        conditionList.add( EntityCondition.makeCondition([EntityCondition.makeCondition("auditorPerson",EntityOperator.IN,partyIds),
                                                          EntityCondition.makeCondition("mainPerson",EntityOperator.IN,partyIds),
                                                          EntityCondition.makeCondition("copyPerson",EntityOperator.IN,partyIds),
                                                          EntityCondition.makeCondition("fullName",EntityOperator.EQUALS,partyId)],EntityOperator.OR));
    }
    if(UtilValidate.isNotEmpty(title)){
        conditionList.add(EntityCondition.makeCondition("title",EntityOperator.LIKE,"%"+title+"%"));
    }
    if(UtilValidate.isNotEmpty(contactListType) && !contactListType.equals("1")){
        conditionList.add(EntityCondition.makeCondition("contactListType",EntityOperator.EQUALS,contactListType));
    }
    if(UtilValidate.isNotEmpty(reviewTheStatus) && !reviewTheStatus.equals("1")){
        conditionList.add(EntityCondition.makeCondition("reviewTheStatus",EntityOperator.EQUALS,reviewTheStatus));
    } else if (!reviewTheStatus.equals("1")){
        conditionList.add(EntityCondition.makeCondition("reviewTheStatus",EntityOperator.NOT_EQUAL,"LIAISON_STATUS_FOUR"));
    }
    List<Map<String,Object>> pageList = new ArrayList<Map<String,Object>>();
    EntityListIterator liaisonCheckList;
    if (conditionList.size() > 0) {
        liaisonCheckList = EntityQuery.use(delegator).from("TblWorkContactList").where(conditionList).queryIterator();
    } else {
        liaisonCheckList = EntityQuery.use(delegator).from("TblWorkContactList").queryIterator();
    }
    if(null != liaisonCheckList && liaisonCheckList.getResultsSizeAfterPartialList() > 0){
        totalCount = liaisonCheckList.getResultsSizeAfterPartialList();
        pageList = liaisonCheckList.getPartialList(lowIndex, viewSize);
    }
    liaisonCheckList.close();
    List<Map<String,Object>> liaisonList = new ArrayList<Map<String,Object>>();
    for (Map map : pageList) {

        String auditorPersonNameString = "";
        Map<String,Object> liaisonMap = new HashMap<String,Object>();
        liaisonMap.putAll(map);
        String contactList = map.get("contactListType");
        if (null != contactList && !"".equals(contactList)) {
            contactListTypeMap = delegator.findByPrimaryKey("TblTypeManagementList", UtilMisc.toMap("typeManagementListId", contactList));
            contactListTypeName = contactListTypeMap.get("typeManagement");
        }
        auditorPerson = map.get("auditorPerson");
        if (null != auditorPerson && !"".equals(auditorPerson)) {
            String[] auditorPersonIds = auditorPerson.split(",");
            for (int i = 0; i < auditorPersonIds.length; i++) {
                String auditorPersonId = auditorPersonIds[i];
                personMap = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", auditorPersonId));
                String auditorPersonName = personMap.get("fullName");
                auditorPersonNameString = auditorPersonNameString + auditorPersonName + " ";
            }
        } else if (null == auditorPerson) {
            auditorPersonNameString = "";
        }

        String fullName = map.get("fullName");
        personMap = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", fullName));
        String fullNameString = personMap.get("fullName");
        liaisonMap.put("fullNameString", fullNameString);


        String reviewTheStatusName = map.get("reviewTheStatus");
        reviewTheStatusMap = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", reviewTheStatusName));
        String reviewTheStatusString = reviewTheStatusMap.get("description");
        liaisonMap.put("reviewTheStatusString", reviewTheStatusString);

        String createdStamp = map.get("createdStamp");
        liaisonMap.put("createdStamp", createdStamp.substring(0, createdStamp.length() - 2));

        liaisonMap.put("auditorPersonName", auditorPersonNameString);
        liaisonMap.put("contactListTypeName", contactListTypeName);
        liaisonList.add(liaisonMap);
    }
    Map<String,Object> map = new HashMap<String,Object>()
    map.put("list",liaisonList);
    map.put("viewIndex",viewIndex);
    map.put("highIndex",highIndex);
    map.put("totalCount",totalCount);
    map.put("viewSize",viewSize);
    map.put("lowIndex",lowIndex);
    map.put("partyId",partyId);
    map.put("responseTimeStart",responseTimeStart);
    map.put("responseTimeEnd",responseTimeEnd);
    map.put("title",title);
    map.put("contactListType",contactListType);
    map.put("reviewTheStatus",reviewTheStatus);
    successResult.put("data",map);
    return successResult;
}

