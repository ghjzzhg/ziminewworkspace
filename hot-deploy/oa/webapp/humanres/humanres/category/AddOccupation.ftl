<#include "component://widget/templates/chooser.ftl"/>
<script type="text/javascript">
    $("#AddOccupation").validationEngine("attach", {promptPosition: "topLeft"});
</script>
<form id="AddOccupation" class="basic-form" name="AddOccupation" action="/hr/control/addOccupation">
    <input type="hidden" name="partyId" value="${partyId}" id="AddOccupation_partyId">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr>
            <td class="label">
                <label for="AddOccupation_partyId" id="AddOccupation_partyId_title">所属组织</label></td>
            <td>
            <#assign department = delegator.findOne("PartyGroup",{"partyId" : partyId?default('')},false)>
            ${department.groupName?default('')}
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="AddOccupation_newOccupationMaster" id="AddOccupation_newOccupationMaster_title"><span class="requiredAsterisk">*</span>选择主管岗位</label></td>
            <td>
            <#--<@chooser chooserType="LookupDepartment" name="newOccupationMaster" strTab="岗位选择器" chooserValue="${newOccupationMaster?default('')}" dropDown=true-->
            <#--dropDownType="LookupOccupation" secondary=true/>-->
            <@chooser chooserType="LookupOccupation" name="newOccupationMaster" strTab="岗位选择器" chooserValue="${newOccupationMaster?default('')}" dropDown=false
            dropDownType="LookupOccupation" secondary=true/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="AddOccupation_newOccupation" id="AddOccupation_newOccupation_title"><span class="requiredAsterisk">*</span>新岗位名称</label></td>
            <td>
                <input type="text" name="newOccupation" class="validate[required]" size="20" maxlength="30" id="AddOccupation_newOccupation">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="AddOccupation_manager" id="AddOccupation_manager_title">新岗位负责人</label></td>
            <td>
                <@chooser chooserType="LookupEmployee" name="managerForOccupation" importability=false chooserValue="${managerForOccupation?default('')}" required=false secondary=true/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="AddOccupation_saveLink" class="hide" id="AddOccupation_saveLink_title">添加</label></td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.organization.addOccupation();" title="添加">添加</a>
            </td>
        </tr>
        </tbody>
    </table>

</form>