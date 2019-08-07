<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="searchFixedAssetsForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="searchFixedAssetsForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="FixedAssetsForSearch_fixedAssetsCode" id="FixedAssetsForSearch_fixedAssetsCode_title">资产编码</label>
            </td>
            <td>
                <input type="text" name="fixedAssetsCode" size="25" id="FixedAssetsForSearch_fixedAssetsCode">
            </td>
            <td class="label">
                <label for="FixedAssetsForSearch_fixedAssetsName" id="FixedAssetsForSearch_fixedAssetsName_title">资产名称</label>
            </td>
            <td>
                <input type="text" name="fixedAssetsName" size="25" id="FixedAssetsForSearch_fixedAssetsName">
            </td>
            <td class="label">
                <label for="FixedAssetsForSearch_assetType" id="FixedAssetsForSearch_assetType_title">资产类别</label>
            </td>
            <td>
                <select name="assetType" id="FixedAssetsForSearch_assetType">
                    <option value="">--请选择--</option>
                <#list fixedAssetsTypeList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="FixedAssetsForSearch_departmentForSearch" id="FixedAssetsForSearch_departmentForSearch_title">归属部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="departmentForSearch" importability=true chooserValue="${department?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForSearch_usePersonForSearch" id="FixedAssetsForSearch_usePersonForSearch_title">使用人</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="usePersonForSearch" importability=true chooserValue="${usePerson?default('')}" />
            </td>
            <td class="label">
                <label for="FixedAssetsForSearch_useDepartmentForSearch" id="FixedAssetsForSearch_useDepartmentForSearch_title">使用部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="useDepartmentForSearch" importability=true chooserValue="${useDepartment?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="FixedAssetsForSearch_assetStatus" id="FixedAssetsForSearch_assetStatus_title">状态</label>
            </td>
            <td>
                <select name="assetStatus" id="FixedAssetsForSearch_assetStatus">
                    <option value="">--请选择--</option>
                <#list assetStatusList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="FixedAssetsForSearch_canLendOut" id="FixedAssetsForSearch_canLendOut_title">可否外借</label>
            </td>
            <td>
                <select name="canLendOut" id="FixedAssetsForSearch_canLendOut">
                    <option value="">--请选择--</option>
                    <option value="Y">可外借</option>
                    <option value="N">不可外借</option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsForSearch_findButton" id="FixedAssetsForSearch_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.fixedAssets.searchFixedAssets();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="FixedAssetsForSearch_create" id="FixedAssetsForSearch_create_title">操作</label>
            </td>
            <td colspan="5">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.fixedAssets.addFixedAssets();" title="操作">新增固定资产</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>