<div>
    <div class="portlet-notice-title" style="text-align: center">
        <#if noNum == 0>
            今日没有新公文\通知
        </#if>
    </div>
    <#list notices as notice>
        <div class="portlet-notice-item">
            <a href="#" onclick="$.bumphNotice.showBumphNoticeInfo(${notice.noticeId})">[${notice.releaseTime}] ${notice.title}</a>
        </div>
    </#list>
</div>