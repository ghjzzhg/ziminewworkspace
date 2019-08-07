<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<script language="JavaScript">
</script>
<form method="post" action="" id="TransferRecordForSearch" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="TransferRecordForSearch">
    <table cellspacing="0" class="basic-table">
        <tbody>
            <tr>
                <td class="label">
                    <label for="TransactionRecordForSearch_startDate" id="TransactionRecordForSearch_startDate_title">调动日期</label></td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${startDate?default('')}" size="25" maxlength="30" id="TransactionRecordForSearch_startDate"
                    dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'TransactionRecordForSearch_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="TransactionRecordForSearch_endDate" id="TransactionRecordForSearch_endDate_title">至</label>
                </td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${endDate?default('')}" size="25" maxlength="30" id="TransactionRecordForSearch_endDate"
                    dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'TransactionRecordForSearch_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="TransactionRecordForSearch_changeType" id="TransactionRecordForSearch_changeType_title">调动类型</label>
                </td>
                <td>
                    <select name="changeType" id="changeType">
                        <option value="">--请选择--</option>
                    <#list changeTypeList as type >
                        <option value="${type.enumId}">${type.description}</option>
                    </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="TransactionRecordForSearch_partyId" id="TransactionRecordForSearch_partyId_title">员工</label>
                </td>
                <td>
                    <@chooser chooserType="LookupEmployee" name="partyIdForSearch" importability=true chooserValue="${partyIdForSearch?default('')}" />
                </td>
                <td class="label">
                    <label for="TransactionRecordForSearch_findButton" id="TransactionRecordForSearch_findButton_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.searchPostChanges();" title="操作">查询</a>
                </td>
                <td class="label">
                    <label for="TransactionRecordForSearch_create" id="TransactionRecordForSearch_create_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.createPostChange('','postChangeList');" title="操作">创建岗位调动记录</a>
                </td>
            </tr>
        </tbody>
    </table>
</form>