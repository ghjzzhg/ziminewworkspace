<#include "component://oa/webapp/oa/humanres/workLog/ScheduleList.ftl"/>
<#if scheduleList?has_content>
    <@ScheduleList scheduleList=scheduleList workLogDate=workLogDate workLogId=workLogId/>
<#else>
<table>
    <tr>
        <td align="center" style="color: red">
            今日没有日程
        </td>
    </tr>
</table>
</#if>
<input type="hidden" id="workLog_msg" value="${msg?default('')}">


