<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if assetsRepair?has_content>
    <#assign assetsRepairId = assetsRepair.assetsRepairId?default('')>
    <#assign repairPerson = assetsRepair.repairPerson?default('')>
    <#assign repairDepartment = assetsRepair.repairDepartment?default('')>
    <#assign repairDate = assetsRepair.repairDate?default('')>
    <#assign repairContent = assetsRepair.repairContent?default('')>
<#else>
    <#assign assetsRepairId = ''>
    <#assign repairPerson = ''>
    <#assign repairDepartment = ''>
    <#assign repairDate = ''>
    <#assign repairContent = ''>
</#if>
<form method="post" action="" id="AssetsRepairForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="AssetsRepairForm">
    <table cellspacing="0" class="basic-table">
        <input type="hidden" name="fixedAssetsId" value="${fixedAssetsId?default('')}"/>
        <input type="hidden" name="assetsRepairId" value="${assetsRepairId}"/>
        <input type="hidden" name="fixedAssetsCode" value="${fixedAssetsCode?default('')}"/>
        <tbody>
        <tr>
            <td class="label">
                <label for="FixedAssets_fixedAssetsCode" id="FixedAssets_fixedAssetsCode_title">资产编号</label>
            </td>
            <td>
                <label>${fixedAssetsCode?default('')}</label>
            </td>
            <td class="label">
                <label for="FixedAssets_repairPerson" id="FixedAssets_repairPerson_title"><b class="requiredAsterisk">*</b>检修人</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="repairPerson" importability=true chooserValue="${repairPerson?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssets_repairDepartment" id="FixedAssets_repairDepartment_title">检修部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="repairDepartment" importability=true chooserValue="${repairDepartment?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="FixedAssets_repairDate" id="FixedAssets_repairDate_title"><b class="requiredAsterisk">*</b>检修日期</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="repairDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${repairDate?default('')}" size="25" maxlength="30" id="FixedAssets_repairDate" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssets_repairContent" id="FixedAssets_repairContent_title"><b class="requiredAsterisk">*</b>检修内容</label>
            </td>
            <td colspan="3">
                <textarea name="repairContent" rows="3" class="validate[required]">${repairContent}</textarea>
            </td>
        </tr>
        <tr>
            <td>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.fixedAssets.saveAssetsRepair();" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>