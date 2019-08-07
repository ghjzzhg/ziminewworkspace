import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue

String partyId = parameters.get("partyId");
String familyId = parameters.get("familyId");
//ȡ��Ա����ϸ��Ϣ
List<GenericValue> resultList = delegator.findByAnd("StaffDetails",UtilMisc.toMap("partyId",partyId));
Map<String,Object> resultMap = new HashMap<String,Object>();
if (resultList.size()!=0){
    Map<String,Object> map = resultList[0];
    resultMap.put("groupName",map.get("departmentName"));
    resultMap.put("fullName",map.get("fullName"));
    resultMap.put("workerSn",map.get("workerSn"));
    resultMap.put("occupationName",map.get("occupationName"));
    resultMap.put("partyId",partyId);
}
if(familyId!=null){
       //��ѯ��ͥ���
       GenericValue map = delegator.findByPrimaryKey("TblFamilyInformation",UtilMisc.toMap("familyId",familyId));
       resultMap.put("familyId",familyId);
       resultMap.put("name",map.get("name"));
       resultMap.put("relationship",map.get("relationship"));
       resultMap.put("birthDate",map.get("birthDate"));
       resultMap.put("workCompany",map.get("workCompany"));
       resultMap.put("address",map.get("address"));
       resultMap.put("phone",map.get("phone"));
       resultMap.put("remarks",map.get("remarks"));
}
context.resultMap = resultMap;
