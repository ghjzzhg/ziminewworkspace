<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<script language="JavaScript">
</script>
<form method="post" action="" id="DepartureManagementForSearch" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="DepartureManagementForSearch">
    <table cellspacing="0" class="basic-table">
        <tbody>
            <tr>
                <td class="label">
                    <label for="DepartureRecordForSearch_startDate" id="DepartureRecordForSearch_startDate_title">离职日期</label></td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${startDate?default('')}" size="25" maxlength="30" id="DepartureRecordForSearch_startDate"
                    dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'DepartureRecordForSearch_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="DepartureRecordForSearch_endDate" id="DepartureRecordForSearch_endDate_title">至</label>
                </td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${endDate?default('')}" size="25" maxlength="30" id="DepartureRecordForSearch_endDate"
                    dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'DepartureRecordForSearch_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="DepartureRecordForSearch_departureType" id="DepartureRecordForSearch_departureType_title">离职类型</label>
                </td>
                <td>
                    <select name="departureType" id="departureType">
                        <option value="">--请选择--</option>
                    <#list departureTypeList as type >
                        <option value="${type.enumId}">${type.description}</option>
                    </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="DepartureRecordForSearch_partyId" id="DepartureRecordForSearch_partyId_title">员工</label>
                </td>
                <td>
                    <@chooser chooserType="LookupEmployee" name="partyIdForSearch" importability=true chooserValue="${partyIdForSearch?default('')}" />
                </td>
                <td class="label">
                    <label for="DepartureRecordForSearch_findButton" id="DepartureRecordForSearch_findButton_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.searchDeparture();" title="操作">查询</a>
                </td>
                <td class="label">
                    <label for="DepartureRecordForSearch_create" id="DepartureRecordForSearch_create_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.editDeparture('','','1');" title="操作">创建离职记录</a>
                </td>
            </tr>
        </tbody>
    </table>
</form>