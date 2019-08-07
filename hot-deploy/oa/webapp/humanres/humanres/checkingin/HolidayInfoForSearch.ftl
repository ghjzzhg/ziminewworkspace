<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="HolidayInfoForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="HolidayInfoForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="HolidayInfoForSearch_holidayStartDate" id="HolidayInfoForSearch_holidayStartDate_title">假期开始时间</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="holidayStartDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${holidayStartDate?default('')}" size="25" maxlength="30" id="HolidayInfoForSearch_holidayStartDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'HolidayInfoForSearch_holidayEndDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="HolidayInfoForSearch_holidayEndDate" id="HolidayInfoForSearch_holidayEndDate_title">假期结束时间</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="holidayEndDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${holidayEndDate?default('')}" size="25" maxlength="30" id="HolidayInfoForSearch_holidayEndDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'HolidayInfoForSearch_holidayStartDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="HolidayInfoForSearch_holidayStaff" id="HolidayInfoForSearch_holidayStaff_title">所属员工</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="holidayStaff" importability=true chooserValue="${holidayStaff?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="HolidayInfoForSearch_holidayDepartment" id="HolidayInfoForSearch_holidayDepartment_title">所属部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="holidayDepartment" importability=true chooserValue="${holidayDepartment?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="HolidayInfoForSearch_findButton" id="HolidayInfoForSearch_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.searchHoliday();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="HolidayInfoForSearch_create" id="HolidayInfoForSearch_create_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.addHolidayInfo('');" title="操作">添加假期信息</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>