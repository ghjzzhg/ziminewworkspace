<#include "component://oa/webapp/oa/humanres/workPlan/foldersHeader.ftl"/>
<div class="screenlet-title-bar">
    <ul>
        <li class="h3">待跟进计划</li>
    </ul>
    <br class="clear">
</div>
<table class="basic-table" cellspacing="0" cellpadding="0" align="center">
    <tr class="header-row-2">
        <td width="20%">计划标题</td>
        <td width="25%">时间要求</td>
        <td width="10%">完成状态</td>
        <td width="45%">我的任务</td>
    </tr>
    <#if workPlanList?has_content>
        <#list workPlanList as list>
            <tr>
                <td>${list.title}</td>
                <td>${list.startTime}~${list.completeTime}</td>
                <td><@WorkPlanProgressbar id="mySelfWorkPlan_${list.workPlanId}" value= list.personWorkStatus /></td>
                <td>${list.jobDescription}</td>
            </tr>
        </#list>
    </#if>
</table>