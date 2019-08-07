<#include "component://widget/templates/chooser.ftl"/>
<div>
    <form name="setAllotForm" id="setAllotForm" class="basic-form">
        <input type="hidden" name="taskId" value="${parameters.processInstanceId}">
        <table class="basic-table hover-bar">
            <tr>
                <td>
                <@chooser chooserType="LookupEmployee" name="userId" importability=true chooserValue="${department?default('')}" required=true/>
                </td>
                <td><a href="#" onclick="javascript:$.workflow.setAllot();" class="smallSubmit">转办</a></td>
            </tr>
        </table>
    </form>
</div>