<div>
    <div style="text-align: center" class="portlet-notice-title">
        <#if noNum == 0>
            您今天没有新下达的任务！
        </#if>
    </div>
    <#list notices as notice>
        <div class="portlet-notice-item">
            <a href="#" onclick="$.workPlan.feedbackWorkPlan(${notice.workPlanId})">[${notice.startTime}] ${notice.title}</a>
        </div>
    </#list>
</div>