<#include "component://widget/templates/chooser.ftl"/>
<script type="text/javascript">
    $("#EditOccupation").validationEngine("attach", {promptPosition: "topLeft"});
</script>
<form method="post" action="/hr/control/editOccupation" id="EditOccupation" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="EditOccupation">
    <input type="hidden" name="partyId" value="${partyId}" id="EditOccupation_partyId">
    <input type="hidden" name="oldOccupation" value="${roleTypeId}" id="EditOccupation_oldOccupation">
    <input type="hidden" name="saveLink" id="EditOccupation_saveLink" onclick="javascript:$.organization.saveOccupation();">
    <table cellspacing="0" class="basic-table hover-bar">
        <#--<label>${organization?default('')}</label>-->
        <tbody>
        <tr>
            <td class="label">
                <label for="EditOccupation_newOccupation" id="EditOccupation_newOccupation_title"><span class="requiredAsterisk">*</span>岗位名称</label></td>
            <td>
                <input type="text" name="newOccupation" value="${description?default('')}" size="25" maxlength="30" id="EditOccupation_newOccupation">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="EditOccupation_manager" id="EditOccupation_manager_title">岗位负责人</label></td>
            <td>
            <@chooser chooserType="LookupEmployee" name="managerForEditOccupation" importability=false chooserValue="${managerForEditOccupation?default('')}" required=false secondary=true/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="EditOccupation_newOccupationMasterForEdit" id="EditOccupation_newOccupationMasterForEdit_title"><span class="requiredAsterisk">*</span>岗位主管</label></td>
            <td>
                <@chooser chooserType="LookupOccupation" name="newOccupationMasterForEdit" importability=true chooserValue="${newOccupationMasterForEdit?default('')}" secondary=true/>
                <span class="tooltip">下级岗位需选择上级主管岗位</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="EditOccupation_saveLink" class="hide" id="EditOccupation_saveLink_title">更新</label></td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.organization.saveOccupation();" title="更新">更新</a>
            </td>
        </tr>
        </tbody></table>
</form>