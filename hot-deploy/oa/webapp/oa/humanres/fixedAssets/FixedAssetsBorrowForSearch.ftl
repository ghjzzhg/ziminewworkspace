<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="searchFixedAssetsBorrow" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="searchFixedAssetsBorrow">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_fixedAssetsCode" id="FixedAssetsBorrowForSearch_fixedAssetsCode_title">资产编码</label>
            </td>
            <td>
                <input type="text" name="fixedAssetsCode" size="25" id="FixedAssetsBorrowForSearch_fixedAssetsCode">
            </td>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_fixedAssetsName" id="FixedAssetsBorrowForSearch_fixedAssetsName_title">资产名称</label>
            </td>
            <td>
                <input type="text" name="fixedAssetsName" size="25" id="FixedAssetsBorrowForSearch_fixedAssetsName">
            </td>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_assetType" id="FixedAssetsBorrowForSearch_assetType_title">资产类别</label>
            </td>
            <td>
                <select name="assetType" id="FixedAssetsBorrowForSearch_assetType">
                    <option value="">--请选择--</option>
                <#list assetTypeList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_fixedAssetsManager" id="FixedAssetsBorrowForSearch_fixedAssetsManager_title">管理者</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="fixedAssetsManager" chooserValue="${fixedAssetsManager?default('')}" />
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_applyPerson" id="FixedAssetsBorrowForSearch_applyPerson_title">借用人</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="applyPerson" importability=true chooserValue="${applyPerson?default('')}" />
            </td>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_department" id="FixedAssetsBorrowForSearch_department_title">归属部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}" required=false/>
            </td>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_assetStatus" id="FixedAssetsBorrowForSearch_assetStatus_title">资产状况</label>
            </td>
            <td>
                <select name="assetStatus" id="FixedAssetsBorrowForSearch_assetStatus">
                    <option value="">--请选择--</option>
                <#list assetStatusList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_lendStatus" id="FixedAssetsBorrowForSearch_lendStatus_title">借用状态</label>
            </td>
            <td>
                <select name="lendStatus" id="FixedAssetsBorrowForSearch_lendStatus">
                    <option value="">--请选择--</option>
                <#list assetLendStatusList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="FixedAssetsBorrowForSearch_findButton" id="FixedAssetsBorrowForSearch_findButton_title">操作</label>
            </td>
            <td colspan="7">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.fixedAssets.searchAssetsBorrow();" title="操作">查询</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>