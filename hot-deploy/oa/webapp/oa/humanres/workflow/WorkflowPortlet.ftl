<div>
    <div style="text-align: center" class="portlet-notice-title">
        现在有83条需审批的新工作流，请查阅
    </div>
<#list notices as notice>
    <div class="portlet-notice-item">
        [${notice.date}] ${notice.title}
    </div>
</#list>
</div>