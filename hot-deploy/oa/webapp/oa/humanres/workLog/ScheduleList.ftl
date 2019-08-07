<#macro ScheduleList scheduleList workLogDate workLogId='' option=true>
    <#if scheduleList?has_content && scheduleList[0].scheduleStartDatetime?has_content>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tr class="header-row-2">

                <#if option>
                    <td width="20%">标题</td>
                    <td width="5%">开始时间</td>
                    <td width="5%">结束时间</td>
                    <td width="60%">内容</td>
                    <td width="5%"></td>
                    <td width="5%"></td>
                <#else >
                    <td width="20%">标题</td>
                    <td width="5%">开始时间</td>
                    <td width="5%">结束时间</td>
                    <td width="70%">内容</td>
                </#if>
            </tr>
            <#list scheduleList as list>
                <tr>
                    <td>${list.scheduleTitle?default('')}</td>
                    <td>${list.scheduleStartDatetime?string('HH:mm')}</td>
                    <td>${list.scheduleEndDatetime?string('HH:mm')}</td>
                    <td>${list.scheduleContent?default('')}</td>
                    <#if option>
                        <td>
                            <a class="icon-edit" href="#nowhere" onclick="updateSchedule('${list.scheduleId}','${list.scheduleStartDatetime?string('yyyy-MM-dd')}','${list.workLogId?default('')}','${list.scheduleTitle?default('')}','${list.scheduleStartDatetime}','${list.scheduleEndDatetime}','${list.scheduleContent?default('')}')" title="修改">
                            </a>
                        </td>
                        <td>
                            <a class="icon-trash" href="#nowhere" onclick="deleteSchedule('${list.workLogId}','${list.scheduleId}','${workLogDate}')" title="删除">
                            </a>
                        </td>
                    </#if>
                </tr>
            </#list>
        </table>
    </#if>
</#macro>
