<div class="screenlet" id="feeds">
    <div id="screenlet_2_col" class="screenlet-body">
        <table cellspacing="0" class="basic-table hover-bar">
            <tbody>
            <tr class="header-row-2">
                <td>
                    <label >执行人</label></td>
                <td>
                    <label>当前进度</label></td>
                <td>
                    <label>最后反馈人/时间</label></td>
                <#--<td>
                    <label>最后反馈内容</label></td>-->
                <td>操作</td>
            </tr>
            <#if workReportMap.executionPlanList?has_content>
                <#list workReportMap.executionPlanList as list>
                <tr>
                    <td>${list.fullName}</td>
                    <td>${list.description}</td>
                    <td>${list.personAndTime}</td>
                    <#--<td>${list.feedbackInfo}</td>-->
                    <td><a href="#" onclick="individualFeedback('${list.reportPerson}','${list.workReportId}')" class="smallSubmit">查看更多...</a></td>
                </tr>
                </#list>
            </#if>
            </tbody>
        </table>
        </div>
        <div>
            <p>说明：</p>
            <p>1、周期重复报告可在选定“时间范围”后点击“查询”显示报告历史反馈信息；</p>
            <p>2、“时间范围”需要指定开始时间，结束时间没有指定则默认为当天。</p>
        </div>
    </div>
</div>