<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="SearchCheckingInInfoForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="SearchCheckingInInfoForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="CheckingInForSearch_startDate" id="CheckingInForSearch_startDate_title">考勤开始日期</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${startDate?default('')}" size="25" maxlength="30" id="CheckingInForSearch_startDate"
            dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'CheckingInForSearch_endDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="CheckingInForSearch_endDate" id="CheckingInForSearch_endDate_title">考勤结束日期</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
            value="${endDate?default('')}" size="25" maxlength="30" id="CheckingInForSearch_endDate"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'CheckingInForSearch_startDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="CheckingInForSearch_staff" id="CheckingInForSearch_staff_title">考勤员工</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="staff" importability=true chooserValue="${staff?default('')}" />
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CheckingInForSearch_checkingInType" id="CheckingInForSearch_checkingInType_title">考勤类型</label>
            </td>
            <td>
                <select name="checkingInType" id="CheckingInForSearch_checkingInType">
                    <option value="">--请选择--</option>
                    <#list checkingInTypeList as type >
                        <option value="${type.enumId}">${type.description}</option>
                    </#list>
                </select>
            </td>
            <td class="label">
                <label for="CheckingInForSearch_checkingInStatus" id="CheckingInForSearch_checkingInStatus_title">考勤状态</label>
            </td>
            <td>
                <select name="checkingInStatus" id="CheckingInForSearch_checkingInStatus">
                    <option value="">--请选择--</option>
                    <#list checkingInStatusList as type >
                        <option value="${type.enumId}">${type.description}</option>
                    </#list>
                </select>
            </td>
            <td class="label">
                <label for="CheckingInForSearch_weekDay" id="CheckingInForSearch_weekDay_title">考勤星期</label>
            </td>
            <td>
                <select name="weekDay" id="CheckingInForSearch_weekDay">
                    <option value="">--请选择--</option>
                    <option value="7">周日</option>
                    <option value="1">周一</option>
                    <option value="2">周二</option>
                    <option value="3">周三</option>
                    <option value="4">周四</option>
                    <option value="5">周五</option>
                    <option value="6">周六</option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CheckingInForSearch_findButton" id="CheckingInForSearch_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.searchCheckingIn();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="CheckingInForSearch_create" id="CheckingInForSearch_create_title">操作</label>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.checkingIn.addChekingInTab();" title="操作">添加考勤信息</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>