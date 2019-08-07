<#assign overtimeId = parameters.overtimeId>
<#if overtimeId?has_content>
    <#assign overtime = delegator.findOne("TblOvertime",{"overtimeId":overtimeId},false)>
</#if>
<#if overtime?has_content>
    <#assign overtimeId = overtime.overtimeId?default('')>
    <#assign startTime = overtime.startTime?string('yyyy-MM-dd HH:mm:ss')>
    <#assign endTime = overtime.endTime?string('yyyy-MM-dd HH:mm:ss')>
    <#assign staff = overtime.staff?default('')>
    <#assign department = overtime.department?default('')>
    <#assign auditor = overtime.auditor?default('')>
    <#assign overtimeType = overtime.overtimeType?default('')>
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
    $("input[name = staff]").data("promptPosition", "bottomLeft");
    $(function(){
        if($("#OvertimeForm_overtimeFrom").val() == '2' || $("#OvertimeForm_overtimeFrom").html() == '组织'){
            $("#OvertimeForm_staff_title").css("display","none");
            $("#overtime_staff").css("display","none");
            $("#overtime_department").css("display","");
        }
    });
    function changStatus(obj){
        if($(obj).val()=='2'){
            $("#OvertimeForm_staff_title").css("display","none");
            $("#overtime_staff").css("display","none");
            $("#overtime_department").css("display","");
            $("input[name = staff]").removeAttr("class value");
            $("input[name = showstaff]").removeAttr("value");
        }else{
            $("#OvertimeForm_staff_title").css("display","");
            $("#overtime_staff").css("display","");
            $("#overtime_department").css("display","none");
            $("input[name = department]").removeAttr("class value");
            $("input[name = showdepartment]").removeAttr("value");
        }
    };
</script>
<form method="post" action="" id="OvertimeForm" class="basic-form" name="OvertimeForm">
    <input type="hidden" name="overtimeId" value="${overtimeId?default('')}">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="OvertimeForm_overtimeFrom" id="OvertimeForm_overtimeFrom_title"><b class="requiredAsterisk">*</b>加班所属：</label></td>
            <td>
                  <span class="ui-widget">
                      <#if overtimeId?has_content>
                          <#if type=='1'>
                              <label id="OvertimeForm_overtimeFrom">个人</label>
                          <#else>
                              <label id="OvertimeForm_overtimeFrom">组织</label>
                          </#if>
                      <#else>
                          <select name="holidayFrom" id="OvertimeForm_overtimeFrom" onchange="changStatus($(this))">
                              <#if type=='1'>
                                  <option value="1" selected>个人</option>
                              <#else >
                                  <option value="1">个人</option>
                              </#if>
                              <#if type=='2'>
                                  <option value="2" selected>组织</option>
                              <#else >
                                  <option value="2">组织</option>
                              </#if>
                          </select>
                      </#if>
                  </span>
            </td>
            <td class="label">
                <label for="OvertimeForm_staff" id="OvertimeForm_staff_title"><b class="requiredAsterisk">*</b>所属员工：</label>
            </td>
            <td >
                <span id="overtime_staff">
                <@chooser chooserType="LookupEmployee" name="staff" chooserValue="${staff?default('')}" required=false/>
                </span>
            </td>
        </tr>
        <tr id="overtime_department" style="display: none">
            <td class="label">
                <label for="OvertimeForm_department" id="OvertimeForm_department_title"><b style="color: red;">*</b>所属组织：</label>
            </td>
            <td colspan="3">
                 <span class="ui-widget">
                <@chooser chooserType="LookupDepartment" name="department" chooserValue="${department?default('')}" required=false/>
                 </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_overtimeType" id="OvertimeForm_overtimeType_title"><b class="requiredAsterisk">*</b>加班类型：</label></td>
            <td>
                <select name="overtimeType" id="OvertimeForm_overtimeType" class="validate[required]">
                    <option value="">--请选择--</option>
                <#list overtimeTypeList as type>
                    <option value="${type.enumId}" <#if overtimeType == type.enumId>selected="selected"</#if>>${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label" id="auditor">
                <label for="OvertimeForm_a" id="auditor"><b class="requiredAsterisk">*</b>审核人:</label>
            </td>
            <td id="auditorPersonLabel">
            <@chooser chooserType="LookupEmployee" name="auditor" chooserValue="${auditor?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_startDate" id="OvertimeForm_startDate_title"><b class="requiredAsterisk">*</b>加班开始时间：</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="validate[required,past[endTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${startTime?default('')}" size="25" maxlength="30" id="OvertimeForm_startDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'OvertimeForm_endDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="OvertimeForm_endDate" id="OvertimeForm_endDate_title"><b class="requiredAsterisk">*</b>加班结束时间：</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="endTime" event="" action="" className="validate[required,future[startTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${endTime?default('')}" size="25" maxlength="30" id="OvertimeForm_endDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'OvertimeForm_startDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_remark" id="OvertimeForm_remark_title">备注：</label></td>
            <td colspan="3">
                <textarea name="remark" cols="60" rows="3" id="OvertimeForm_remark">${remark}</textarea>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeForm_addLink" class="hide" id="OvertimeForm_addLink_title">保存</label>
            </td>
            <td colspan="4">
                <a class="smallSubmit" href="#" title="保存" onclick="$.checkingIn.saveOvertime()">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>