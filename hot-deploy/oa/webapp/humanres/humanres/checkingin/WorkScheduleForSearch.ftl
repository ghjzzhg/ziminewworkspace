<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="SearchScheduleOfWorkForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="SearchScheduleOfWorkForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="WorkScheduleForSearch_type" id="WorkScheduleForSearch_type_title">排班选择</label>
            </td>
            <td>
                <select name="type" id="WorkScheduleForSearch_type">
                <#list workScheduleTypeList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="WorkScheduleForSearch_workStartDate" id="WorkScheduleForSearch_workStartDate_title">排班开始时间</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="workStartDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${workStartDate?default('')}" size="25" maxlength="30" id="WorkScheduleForSearch_workStartDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'WorkScheduleForSearch_workEndDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="WorkScheduleForSearch_workEndDate" id="WorkScheduleForSearch_workEndDate_title">排班结束时间</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="workEndDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${workEndDate?default('')}" size="25" maxlength="30" id="WorkScheduleForSearch_workEndDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'WorkScheduleForSearch_workStartDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="WorkScheduleForSearch_workStaff" id="WorkScheduleForSearch_workStaff_title">所属员工</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="workStaff" importability=true chooserValue="${workStaff?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="WorkScheduleForSearch_workDepartment" id="WorkScheduleForSearch_workDepartment_title">所属部门</label>
            </td>
            <td colspan="3">
            <@chooser chooserType="LookupDepartment" name="workDepartment" importability=true chooserValue="${workDepartment?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="WorkScheduleForSearch_findButton" id="WorkScheduleForSearch_findButton_title">操作</label>
            </td>
            <td colspan="1">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.searchScheduleOfWork();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="WorkScheduleForSearch_create" id="WorkScheduleForSearch_create_title">新增</label>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.editWorkSchedule();" title="操作">添加周期排班</a>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.autoScheduling();" title="操作">添加自动排班</a>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.showPerson();" title="操作">添加个人排班</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>