<div class="screenlet-title-bar">
    <ul><li class="h3"><i class="glyphicon glyphicon-forward"></i><span>流程审批历史记录</span></li>
</ul><br class="clear"></div>
<table cellspacing="0" class="basic-table hover-bar">
    <tbody><tr class="header-row-2">
        <td>
            <label for="ProcessHistory_taskName" id="ProcessHistory_taskName_title">序号</label></td>
        <td>
            <label for="ProcessHistory_taskName" id="ProcessHistory_taskName_title">任务名</label></td>
        <td>
            <label for="ProcessHistory_staffName" id="ProcessHistory_staffName_title">执行人</label></td>
        <td>
            <label for="ProcessHistory_startTime" id="ProcessHistory_startTime_title">开始时间</label></td>
        <td>
            <label for="ProcessHistory_endTime" id="ProcessHistory_endTime_title">结束时间</label></td>
        <td>
            <label for="ProcessHistory_approveComment" id="ProcessHistory_approveComment_title">审批意见</label></td>
    </tr>
    <#assign alt_row = false>
    <#list processList as line>
        <tr<#if alt_row> class="alternate-row"</#if> <#if line.allotStatus?has_content> style="font-style: italic;color: #AAAAAA" </#if>>
            <td class="sn-column">
                <label>${line_index}</label>
            </td>
            <td>
                <label>${line.taskName?default('')}</label>
            </td>
            <td>
                <label>${line.staffName?default('')}</label>
            </td>
            <td>
                <label>${line.startTime?default('')}</label>
            </td>
            <td>
                <label>${line.endTime?default('')}</label>
            </td>
            <td>
                <label>${line.approveComment?default('')}</label>
            </td>
        </tr>
        <#assign alt_row = !alt_row>
    </#list>
    </tbody>
</table>