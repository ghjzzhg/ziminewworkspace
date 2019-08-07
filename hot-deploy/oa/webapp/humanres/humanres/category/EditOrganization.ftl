<#include "component://widget/templates/chooser.ftl"/>
<#if organization?has_content>
    <#assign parentPartyId = parameters.parentPartyId?default('')>
    <#assign partyId = organization.partyId?default('')>
    <#assign groupName = organization.groupName?default('')>
    <#assign manager = organization.manager?default('')>
<#else>
    <#assign parentPartyId = parameters.parentPartyId?default('')>
    <#assign partyId = ''>
    <#assign groupName = ''>
    <#assign manager = ''>
</#if>
<form method="post" action="/hr/control/updateOrganization" id="EditOrganization" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="EditOrganization">
    <input type="hidden" name="parentPartyId" id="EditOrganization_parentPartyId" value="${parentPartyId}">
    <input type="hidden" name="partyId" value="${partyId}" id="EditOrganization_partyId">
    <input type="hidden" name="saveLink" id="EditOrganization_saveLink" onclick="javascript:$.organization.saveOrganization();">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr>
            <td class="label">
                <label for="EditOrganization_partyId" id="EditOrganization_partyId_title">组织ID</label></td>
            <td>
                <label>${partyId}</label>
                <span class="tooltip">唯一标识由系统生成</span>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="EditOrganization_groupName" id="EditOrganization_groupName_title"><span class="requiredAsterisk">*</span>组织名称</label></td>
            <td>
                <input type="text" name="groupName" class="validate[required]" value="${groupName}" size="25" maxlength="30" id="EditOrganization_groupName">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="EditOrganization_manager" id="EditOrganization_manager_title">负责人</label></td>
            <td>
            <@chooser chooserType="LookupEmployee" name="manager" importability=true chooserValue="${manager?default('')}" required=false/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="EditOrganization_saveLink" id="EditOrganization_saveLink_title">操作</label></td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.organization.saveOrganization();" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>