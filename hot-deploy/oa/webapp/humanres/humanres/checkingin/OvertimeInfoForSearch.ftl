<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="OvertimeStatisticsForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="OvertimeStatisticsForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="OvertimeStatisticsForm_overtimeStartDate" id="OvertimeStatisticsForm_overtimeStartDate_title">加班开始时间</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="overtimeStartDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${overtimeStartDate?default('')}" size="25" maxlength="30" id="overtimeStatisticsForm_overtimeStartDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'OvertimeStatisticsForm_overtimeEndDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="OvertimeStatisticsForm_overtimeEndDate" id="OvertimeStatisticsForm_overtimeEndDate_title">加班结束时间</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="overtimeEndDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${overtimeEndDate?default('')}" size="25" maxlength="30" id="OvertimeStatisticsForm_overtimeEndDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'OvertimeStatisticsForm_OvertimeStartDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="OvertimeStatisticsForm_overtimeStaff" id="OvertimeStatisticsForm_overtimeStaff_title">员工</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="overtimeStaff" importability=true chooserValue="${overtimeStaff?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="OvertimeStatisticsForm_overtimeDepartment" id="OvertimeStatisticsForm_overtimeDepartment_title">部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="overtimeDepartment" importability=true chooserValue="${overtimeDepartment?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="OvertimeStatisticsForm_overTimeType" id="OvertimeStatisticsForm_overTimeType_title">加班类型</label>
            </td>
            <td>
                <select name="overTimeType" id="OvertimeStatisticsForm_overTimeType" >
                    <option value="">--请选择--</option>
                    <option value="OVERTIME_NORMAL">正常加班</option>
                    <option value="OVERTIME_TEMPORARY">临时加班</option>
                </select>
            </td>
            <td class="label">
                <label for="OvertimeStatisticsForm_findButton" id="OvertimeStatisticsForm_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.searchOvertime();" title="操作">查询</a>
            </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.addOvertime('');" title="操作">新增加班信息</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>