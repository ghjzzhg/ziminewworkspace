<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if assetsUserInfo?has_content>
    <#assign assetsUseInfoId = assetsUserInfo.assetsUseInfoId?default('')>
    <#assign usePerson = assetsUserInfo.usePerson?default('')>
    <#assign useDepartment = assetsUserInfo.useDepartment?default('')>
    <#assign startDate = assetsUserInfo.startDate?default('')>
    <#assign endDate = assetsUserInfo.endDate?default('')>
<#else>
    <#assign assetsUseInfoId = ''>
    <#assign usePerson = ''>
    <#assign useDepartment = ''>
    <#assign startDate = ''>
    <#assign endDate = ''>
</#if>
<form method="post" action="" id="AssetsUseInfoForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="AssetsUseInfoForm">
    <table cellspacing="0" class="basic-table">
        <input type="hidden" name="fixedAssetsId" value="${fixedAssetsId?default('')}"/>
        <input type="hidden" name="assetsUseInfoId" value="${assetsUseInfoId}"/>
        <input type="hidden" name="fixedAssetsCode" value="${fixedAssetsCode?default('')}"/>
        <tbody>
        <tr>
            <td class="label">
                <label for="FixedAssets_fixedAssetsCode" id="FixedAssets_fixedAssetsCode_title">资产编号</label>
            </td>
            <td colspan="3">
                <label>${fixedAssetsCode?default('')}</label>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssets_usePersonForUseInfo" id="FixedAssets_usePersonForUseInfo_title"><b class="requiredAsterisk">*</b>使用人</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="usePersonForUseInfo" importability=true chooserValue="${usePerson?default('')}" />
            </td>
            <td class="label">
                <label for="FixedAssets_useDepartmentForUseInfo" id="FixedAssets_useDepartmentForUseInfo_title">使用部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="useDepartmentForUseInfo" importability=true chooserValue="${useDepartment?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssets_startDate" id="FixedAssets_startDate_title"><b class="requiredAsterisk">*</b>开始日期</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="validate[required,past[endDate:yyyy-MM-dd]]" alert="" title="Format: yyyy-MM-dd"
            value="${startDate?default('')}" size="25" maxlength="30" id="FixedAssets_startDate" dateType="signContractStartDate" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'FixedAssets_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="FixedAssets_endDate" id="FixedAssets_endDate_title"><b class="requiredAsterisk">*</b>结束日期</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${endDate?default('')}" size="25" maxlength="30" id="FixedAssets_endDate" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'FixedAssets_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.fixedAssets.saveUseInfo();" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>