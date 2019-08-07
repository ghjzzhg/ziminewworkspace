<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<script language="JavaScript">
</script>
<form method="post" action="" id="ContractForEmployeeForSearch1" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="ContractForEmployeeForSearch1">
    <table cellspacing="0" class="basic-table">
        <tbody>
            <tr>
                <td class="label">
                    <label for="ContractRecordForSearch_contractNumber" id="ContractRecordForSearch_contractNumber_title">合同编号</label>
                </td>
                <td>
                    <input type="text" name="contractNumber" size="25" id="ContractRecordForSearch_contractNumber">
                </td>
                <td class="label">
                    <label for="ContractRecordForSearch_contractName" id="ContractRecordForSearch_contractName_title">合同名称</label>
                </td>
                <td>
                    <input type="text" name="contractName" size="25" id="ContractRecordForSearch_contractName">
                </td>
                <td class="label">
                    <label for="ContractRecordForSearch_contractType" id="ContractRecordForSearch_contractType_title">合同类型</label>
                </td>
                <td>
                    <select name="contractType" id="ContractRecordForSearch_contractType">
                        <option value="">--请选择--</option>
                        <#list contractTypeList as type >
                            <option value="${type.enumId}">${type.description}</option>
                        </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="ContractRecordForSearch_startDate" id="ContractRecordForSearch_startDate_title">签约日期</label></td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${startDate?default('')}" size="25" maxlength="30" id="ContractRecordForSearch_startDate"
                    dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'ContractRecordForSearch_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="ContractRecordForSearch_endDate" id="ContractRecordForSearch_endDate_title">至</label>
                </td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${endDate?default('')}" size="25" maxlength="30" id="ContractRecordForSearch_endDate"
                    dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'ContractRecordForSearch_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="ContractRecordForSearch_partyId" id="ContractRecordForSearch_partyId_title">员工</label>
                </td>
                <td>
                    <@chooser chooserType="LookupEmployee" name="partyIdForSearch" importability=true chooserValue="${partyIdForSearch?default('')}" />
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="ContractRecordForSearch_stateType" id="ContractRecordForSearch_stateType_title">合同状态</label>
                </td>
                <td>
                    <select name="stateType" id="stateType">
                        <option value="1">全部</option>
                    <#list stateTypeList as type >
                        <option value="${type.enumId}">${type.description}</option>
                    </#list>
                    </select>
                </td>
                <td class="label">
                    <label for="ContractRecordForSearch_findButton" id="ContractRecordForSearch_findButton_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.searchContract();" title="操作">查询</a>
                </td>
                <td class="label">
                    <label for="ContractRecordForSearch_create" id="ContractRecordForSearch_create_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.ContractRecordCreate('','contractList','1');" title="操作">创建员工合同</a>
                </td>
            </tr>
        </tbody>
    </table>
</form>