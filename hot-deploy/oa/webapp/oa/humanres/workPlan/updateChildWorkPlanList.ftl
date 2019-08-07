<#if parameters.data.childWorkPlanId?has_content>
<#assign childWorkPlan = parameters.data>
<form class="basic-form">
    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
        <tbody>
        <tr>
            <td class="label">
                <label>安排部门:</label>
            </td>
            <td>
                <#assign staff = delegator.findOne("TblStaff",{"partyId":childWorkPlan.operatorId},false)>
                <#assign executorGen = delegator.findOne("Person",{"partyId":childWorkPlan.operatorId},false)>
                <#assign department = delegator.findOne("PartyGroup",{"partyId":staff.department},false)>
                ${department.groupName}
            </td>
            <td class="label">
                <label>要求时间:</label>
            </td>
            <td>
            ${childWorkPlan.startTime?string('yyyy-MM-dd HH:mm:ss')}
                &nbsp;~&nbsp;${childWorkPlan.completeTime?string('yyyy-MM-dd HH:mm:ss')}
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>进度:</label></td>
            <td>
                <#if childWorkPlan.childWorkPlanStatus?has_content>
                            <@WorkPlanProgressbar id="childWorkPlanOfExecutorProgressbar_${childWorkPlan.childWorkPlanId}" value=childWorkPlan.childWorkPlanStatus?default(0) />
                        </#if>
            </td>
            <td class="label">
                <label>执行人:</label>
            </td>
            <td>
                ${executorGen.fullName}
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>任务说明:</label>
            </td>
            <td colspan="3" id="childWorkPlan_description">

            </td>
            <script>
                $("#childWorkPlan_description").append(unescapeHtmlText('${childWorkPlan.description?default('')}'));
            </script>
        </tr>
        </tbody>
    </table>
</form>
</#if>
