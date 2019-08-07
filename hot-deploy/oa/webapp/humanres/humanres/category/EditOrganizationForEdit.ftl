<#include "component://widget/templates/chooser.ftl"/>
<#assign parentPartyId = parameters.parentPartyId?default('')>
<#if organization?has_content>
    <#assign partyId = organization.partyId?default('')>
    <#assign groupName = organization.groupName?default('')>
    <#assign managerForEdit = organization.manager?default('')>
<#else>
    <#assign partyId = ''>
    <#assign groupName = ''>
    <#assign managerForEdit = ''>
</#if>
<form method="post" action="/hr/control/updateOrganization" id="EditOrganization1" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="EditOrganization1">
    <input type="hidden" name="parentPartyId" id="EditOrganization1_parentPartyId" value="${parentPartyId}">
    <input type="hidden" name="partyId" value="${partyId}" id="EditOrganization1_partyId">
    <input type="hidden" name="saveLink" id="EditOrganization1_saveLink" onclick="javascript:$.organization.saveOrganization();">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody><tr>
            <td class="label">
                <label for="EditOrganization1_partyId" id="EditOrganization1_partyId_title">组织ID</label></td>
            <td>
                <label>${partyId}</label>
                <span class="tooltip">唯一标识由系统生成</span>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="EditOrganization1_groupName" id="EditOrganization1_groupName_title"><span class="requiredAsterisk">*</span>组织名称</label></td>
            <td>
                <input type="text" name="groupName" class="validate[required]" value="${groupName}" size="25" maxlength="30" id="EditOrganization1_groupName">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="EditOrganization1_managerForEdit" id="EditOrganization1_managerForEdit_title">负责人</label></td>
            <td>
            <@chooser chooserType="LookupEmployee" name="managerForEdit" importability=true chooserValue="${managerForEdit?default('')}" required=false/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="EditOrganization1_saveLink" id="EditOrganization1_saveLink_title">操作</label></td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.organization.saveOrganization1();" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>