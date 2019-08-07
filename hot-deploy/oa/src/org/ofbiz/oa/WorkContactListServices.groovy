package org.ofbiz.oa

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil

public Map<String, Object> saveWorkContactList() {
    String contactListId = context.get("contactListId");
    String number = context.get("number");
//    number = "MIS-YFZX-15-06-" + number
    context.put("number", number);
    Map<String, Object> successResult = ServiceUtil.returnSuccess();
    GenericValue userLogin = context.get("userLogin");
    String partyId = userLogin.getString("partyId");
    List<GenericValue> genericValueUserLogin = delegator.findByAnd("StaffInfo", UtilMisc.toMap("staffId", partyId));
    String departmentName = genericValueUserLogin.get(0).getString("departmentName");


    String reviewTheStatus = context.get("ifApprove");
    if (("on").equals(reviewTheStatus)) {
        context.put("reviewTheStatus", "LIAISON_STATUS_ONE");
        context.put("ifApprove", "yes");
    } else if (null == reviewTheStatus) {
        context.put("reviewTheStatus", "LIAISON_STATUS_TWO");
        context.put("ifApprove", "no");
        context.put("auditorPerson", "");
    }

    String ifResponse = context.get("ifResponse");
    if (("on").equals(ifResponse)) {
        context.put("ifResponse", "yes");
    } else if (null == ifResponse) {
        context.put("ifResponse", "no");
        context.put("responseTime", "");
    }


    boolean flag = true;
    String msg = "保存工作联络单成功";
    liaisonList = delegator.findAll("TblWorkContactList", false);
    for (Map map : liaisonList) {
        if (map.get("number").equals(number)) {
            msg = "当前联络单编号已存在"
            flag = false;
            break;
        }
    }
    if (flag) {
        if (UtilValidate.isNotEmpty(contactListId)) {
            msg = "更新工作联络单成功"
            genericValue = delegator.findByPrimaryKey("TblWorkContactList", UtilMisc.toMap("contactListId", contactListId));
            genericValue.put("departmentName", departmentName);
            genericValue.put("fullName", partyId);
            genericValue.setNonPKFields(context);
            genericValue.store();
        } else {
            contactListId = delegator.getNextSeqId("TblWorkContactList").toString();
            genericValue = delegator.makeValidValue("TblWorkContactList", UtilMisc.toMap("contactListId", contactListId));
            genericValue.put("departmentName", departmentName);
            genericValue.put("fullName", partyId);
            genericValue.setNonPKFields(context);
            genericValue.create();
        }
    }
    /**
     * 根据审核人将审核信息存入审核列表
     */
    String auditorPersonString = context.get("auditorPerson");
    if (null != auditorPersonString && !"".equals(auditorPersonString)) {
        String[] auditorPersons = auditorPersonString.split(",");
        for (int i = 0; i < auditorPersons.length; i++) {
            String auditorPerson = auditorPersons[i];
            auditorPersonList = delegator.findByAnd("StaffInfo", UtilMisc.toMap("staffId", auditorPerson));
            String departmentNameString = auditorPersonList.get(0).get("departmentName");
            String workSheetAuditInformationId = delegator.getNextSeqId("TblWorkSheetAuditInfor").toString();
            genericValue = delegator.makeValidValue("TblWorkSheetAuditInfor", UtilMisc.toMap("workSheetAuditInforId", workSheetAuditInformationId));
            genericValue.put("contactListId", contactListId);
            genericValue.put("departmentName", departmentNameString);
            genericValue.put("fullName", auditorPerson);
            genericValue.put("reviewTheStatus", "PERSON_ONE");
            genericValue.create();
        }
    }
    /**
     * 根据主送人将签收信息存入签收列表
     */
    String mainPersonString = context.get("mainPerson");
    if (null != mainPersonString && !"".equals(mainPersonString)) {
        String[] mainPersons = mainPersonString.split(",");
        for (int i = 0; i < mainPersons.length; i++) {
            String mainPerson = mainPersons[i];
            mainPersonList = delegator.findByAnd("StaffInfo", UtilMisc.toMap("staffId", mainPerson));
            String departmentNameString = mainPersonList.get(0).get("departmentName");
            String workSheetSignForInformationId = delegator.getNextSeqId("TblWorkSheetSignForInfor").toString();
            genericValue = delegator.makeValidValue("TblWorkSheetSignForInfor", UtilMisc.toMap("workSheetSignForId", workSheetSignForInformationId));
            genericValue.put("contactListId", contactListId);
            genericValue.put("departmentName", departmentNameString);
            genericValue.put("fullName", mainPerson);
            genericValue.put("reviewTheStatus", "SIGN_FOR_NO");
            genericValue.create();
        }
    }
    successResult.put("data", UtilMisc.toMap("msg", msg));
    return successResult;
}
