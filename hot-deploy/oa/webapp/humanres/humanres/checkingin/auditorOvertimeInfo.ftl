
<#if overtime?has_content>
    <#assign overtimeId = overtime.overtimeId?default('')>
    <#assign startTime = overtime.startTime?string('yyyy-MM-dd HH:mm:ss')>
    <#assign endTime = overtime.endTime?string('yyyy-MM-dd HH:mm:ss')>
    <#assign staff = overtime.staff?default('')>
    <#assign fullName = overtime.fullName?default('')>
    <#assign department = overtime.department?default('')>
    <#assign groupName = overtime.groupName?default('')>
    <#assign auditorName = overtime.auditorName?default('')>
    <#assign overtimeType = overtime.overtimeType?default('')>
    <#assign auditRemarks = overtime.auditRemarks?default('')>
    <#assign remark = overtime.remark?default('')>
<#else>
    <#assign overtimeId = ''>
    <#assign staff = ''>
    <#assign department = ''>
    <#assign overtimeType= ''>
    <#assign remark = ''>
</#if>
<#if staff?has_content&&staff!=''>
    <#assign type='1'>
<#elseif department?has_content && department!=''>
    <#assign type='2'>
<#else >
    <#assign type='1'>
</#if>
<#include "component://widget/templates/chooser.ftl"/>
<script type="text/javascript">
    $(function(){
        if($("#OvertimeForm_overtimeFrom").val() == '2' || $("#OvertimeForm_overtimeFrom").html() == '组织'){
            $("#OvertimeForm_staff_title").css("display","none");
            $("#overtime_staff").css("display","none");
            $("#overtime_department").css("display","");
        }
    });
</script>
<form method="post" action="" id="auditOvertimeForm" class="basic-form" name="auditOvertimeForm">
    <input type="hidden" name="overtimeId" value="${overtimeId?default('')}">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="OvertimeForm_overtimeFrom" id="OvertimeForm_overtimeFrom_title">加班所属：</label></td>
            <td>
                  <span class="ui-widget">
                      <#if type=='1'>
                          <label id="OvertimeForm_overtimeFrom">个人</label>
                      <#else>
                          <label id="OvertimeForm_overtimeFrom">组织</label>
                      </#if>
                  </span>
            </td>
            <td class="label">
                <label for="OvertimeForm_staff" id="OvertimeForm_staff_title">所属员工：</label>
            </td>
            <td >
                <span id="overtime_staff">
                    <label>${fullName?default('')}</label>
                </span>
            </td>
        </tr>
        <tr id="overtime_department" style="display: none">
            <td class="label">
                <label for="OvertimeForm_department" id="OvertimeForm_department_title">所属组织：</label>
            </td>
            <td colspan="3">
                 <span class="ui-widget">
                     <label>${groupName?default('')}</label>
                 </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_overtimeType" id="OvertimeForm_overtimeType_title">加班类型：</label></td>
            <td>
                <label id="OvertimeForm_overtimeType">
                    <#list overtimeTypeList as type>
                        <#if overtimeType == type.enumId>${type.description?default('')}</#if>
                    </#list>
                </label>
            </td>
            <td class="label" id="auditor">
                <label id="auditor">审核人:</label>
            </td>
            <td id="auditorPersonLabel">
                <label>${auditorName?default('')}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_startDate" id="OvertimeForm_startDate_title">加班开始时间：</label></td>
            <td class="jqv">
                <label>${startTime?default('')}</label>
            </td>
            <td class="label">
                <label for="OvertimeForm_endDate" id="OvertimeForm_endDate_title">加班结束时间：</label></td>
            <td class="jqv">
                <label>${endTime?default('')}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_remark" id="OvertimeForm_remark_title">备注：</label></td>
            <td colspan="3">
                <label>${remark}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_startDate" id="OvertimeForm_startDate_title"><b class="requiredAsterisk">*</b>确认加班开始时间：</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="auditStartTime" event="" action="" className="validate[required,past[auditEndTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="" size="25" maxlength="30" id="OvertimeForm_startDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'OvertimeForm_endDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="OvertimeForm_endDate" id="OvertimeForm_endDate_title"><b class="requiredAsterisk">*</b>确认加班结束时间：</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="auditEndTime" event="" action="" className="validate[required,future[auditStartTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="" size="25" maxlength="30" id="OvertimeForm_endDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'OvertimeForm_startDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_auditRemarks" id="OvertimeForm_auditRemarks_title">审核意见：</label></td>
            <td colspan="3">
                <textarea name="auditRemarks" cols="60" rows="3" id="OvertimeForm_auditRemarks">${auditRemarks?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td colspan="4">
                <a class="smallSubmit" href="#" title="通过" onclick="$.checkingIn.saveAuditOvertime('PERSON_TWO')">通过</a>
                <a class="smallSubmit" href="#" title="驳回" onclick="$.checkingIn.saveAuditOvertime('PERSON_THREE')">驳回</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>