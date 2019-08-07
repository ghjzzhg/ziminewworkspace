<div>
    <div style="text-align: center" class="portlet-notice-title">
        当前有1个需要处理的绩效考核
    </div>
<#list notices as notice>
    <div class="portlet-notice-item">
        [${notice.date}] ${notice.title}
    </div>
</#list>
</div>