<#include "component://widget/templates/chooser.ftl"/>
<#assign parentPartyId = parameters.parentPartyId?default('')>
<#if parentPartyId?has_content>
    <#assign parentPartyName = delegator.findOne("PartyGroup", {"partyId" : parentPartyId}, true).getString("groupName")>
</#if>

<#assign partyId = ''>
<#assign groupName = ''>
<#assign managerForSub = ''>
<form method="post" action="/hr/control/updateOrganization" id="EditSubOrganization" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="EditSubOrganization">
    <input type="hidden" name="parentPartyId" id="EditSubOrganization_parentPartyId" value="${parentPartyId}">
    <input type="hidden" name="partyId" value="${partyId}" id="EditSubOrganization_partyId">
    <input type="hidden" name="saveLink" id="EditSubOrganization_saveLink" onclick="javascript:$.organization.saveOrganization();">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <#if parentPartyName?has_content>
        <tr>
            <td class="label">
                <label for="EditSubOrganization_partyId">上级组织</label></td>
            <td>
                <label>${parentPartyName}</label>
            </td>
        </tr>
        </#if>
        <tr>
            <td class="label">
                <label for="EditSubOrganization_partyId" id="EditSubOrganization_partyId_title">组织ID</label></td>
            <td>
                <label>${partyId}</label>
                <span class="tooltip">唯一标识由系统生成</span>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="EditSubOrganization_groupName" id="EditSubOrganization_groupName_title"><span class="requiredAsterisk">*</span>组织名称</label></td>
            <td>
                <input type="text" name="groupName" class="validate[required]" value="${groupName}" size="25" maxlength="30" id="EditSubOrganization_groupName">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="EditSubOrganization_managerForSub" id="EditSubOrganization_managerForSub_title">负责人</label></td>
            <td>
            <@chooser chooserType="LookupEmployee" name="managerForSub" importability=true chooserValue="${managerForSub?default('')}" required=false/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="EditSubOrganization_saveLink" id="EditSubOrganization_saveLink_title">操作</label></td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.organization.saveSubOrganization($.organization.refreshSubOrgs);" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>