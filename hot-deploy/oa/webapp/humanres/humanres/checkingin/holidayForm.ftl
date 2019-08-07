<#if holiday?has_content>
    <#assign holidayId = holiday.holidayId?default('')>
    <#assign startTime = holiday.startTime?string('yyyy-MM-dd HH:mm:ss')>
    <#assign endTime = holiday.endTime?string('yyyy-MM-dd HH:mm:ss')>
    <#assign staff = holiday.staff?default('')>
    <#assign department = holiday.department?default('')>
    <#assign holidayType = holiday.holidayType?default('')>
    <#assign description = holiday.description?default('')>
<#else>
    <#assign holidayId = ''>
    <#assign staff = ''>
    <#assign department = ''>
    <#assign holidayType= ''>
    <#assign description = ''>
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
    $("#AddHolidayInfoForm_holidayType").data("promptPosition", "bottomLeft");
    $("input[name = staff]").data("promptPosition", "bottomLeft");
    $(function(){
        if($("#AddHolidayInfoForm_holidayFrom").val() == '2' || $("#OvertimeForm_overtimeFrom").html() == '组织'){
            $("#AddHolidayInfoForm_staff_title").css("display","none");
            $("#holiday_staff").css("display","none");
            $("#holiday_department").css("display","");
        }
    })
    function changStatus(obj){
        if($(obj).val()=='2'){
            $("#AddHolidayInfoForm_staff_title").css("display","none");
            $("#holiday_staff").css("display","none");
            $("#holiday_department").css("display","");
            $("input[name = staff]").removeAttr("class value");
            $("input[name = showstaff]").removeAttr("value");
        }else{
            $("#AddHolidayInfoForm_staff_title").css("display","");
            $("#holiday_staff").css("display","");
            $("#holiday_department").css("display","none");
            $("input[name = department]").removeAttr("class value");
            $("input[name = showdepartment]").removeAttr("value");
        }
    }
</script>
<form method="post" action="" id="AddHolidayInfoForm" class="basic-form" name="AddHolidayInfoForm">
    <input type="hidden" name="holidayId" value="${holidayId?default('')}">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="AddHolidayInfoForm_holidayType" id="AddHolidayInfoForm_holidayType_title"><b class="requiredAsterisk">*</b>假期类型：</label></td>
            <td colspan="3">
                <select name="holidayType" id="AddHolidayInfoForm_holidayType" class="validate[required]">
                    <option value="">--请选择--</option>
                    <#list holidayTypeList as type>
                        <option value="${type.enumId}" <#if holidayType == type.enumId>selected="selected"</#if>>${type.description}</option>
                    </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="AddHolidayInfoForm_holidayFrom" id="AddHolidayInfoForm_holidayFrom_title"><b class="requiredAsterisk">*</b>假期所属：</label></td>
            <td>
                  <span class="ui-widget">
                      <#if holidayId?has_content>
                          <#if type=='1'>
                              <label id="AddHolidayInfoForm_holidayFrom">个人</label>
                          <#else>
                              <label id="AddHolidayInfoForm_holidayFrom">组织</label>
                          </#if>
                      <#else>
                          <select name="holidayFrom" id="AddHolidayInfoForm_holidayFrom" onchange="changStatus($(this))">
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
                <label for="AddHolidayInfoForm_staff" id="AddHolidayInfoForm_staff_title"><b class="requiredAsterisk">*</b>所属员工：</label>
            </td>
            <td >
                <span id="holiday_staff">
                <@chooser chooserType="LookupEmployee" name="staff" chooserValue="${staff?default('')}" required=false/>
                <#--<@htmlTemplate.lookupField title="选择员工" formName="AddHolidayInfoForm" name="staff" value="${staff}" id="staff" fieldFormName="LookupStaff" position="center"/>-->
                </span>

            </td>
        </tr>
        <tr id="holiday_department" style="display: none">
            <td class="label">
                <label for="AddHolidayInfoForm_department" id="AddHolidayInfoForm_department_title"><b style="color: red;">*</b>所属组织：</label>
            </td>
            <td colspan="3">
                 <span class="ui-widget">
                <@chooser chooserType="LookupDepartment" name="department" chooserValue="${department?default('')}" required=false/>
                 <#--<@htmlTemplate.lookupField value="${department?default('')}" formName="AddHolidayInfoForm" name="department" id="AddHolidayInfoForm_department" fieldFormName="LookupDepartment" position="center"/>-->
                 </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="AddHolidayInfoForm_startDate" id="AddHolidayInfoForm_startDate_title"><b class="requiredAsterisk">*</b>假期开始时间：</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="validate[required,past[endTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${startTime?default('')}" size="25" maxlength="30" id="AddHolidayInfoForm_startDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'AddHolidayInfoForm_endDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>

            </td>
            <td class="label">
                <label for="AddHolidayInfoForm_endDate" id="AddHolidayInfoForm_endDate_title"><b class="requiredAsterisk">*</b>假期结束时间：</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="endTime" event="" action="" className="validate[required,future[startTime:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${endTime?default('')}" size="25" maxlength="30" id="AddHolidayInfoForm_endDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'AddHolidayInfoForm_startDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>

            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="AddHolidayInfoForm_description" id="AddHolidayInfoForm_description_title">描述：</label></td>
            <td colspan="4">
                <textarea name="description" cols="60" rows="3" id="AddHolidayInfoForm_description">${description}</textarea>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="AddHolidayInfoForm_addLink" class="hide" id="AddHolidayInfoForm_addLink_title">保存</label>
            </td>
            <td colspan="4">
                <a class="smallSubmit" href="#" title="保存" onclick="$.checkingIn.saveHoliday()">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>