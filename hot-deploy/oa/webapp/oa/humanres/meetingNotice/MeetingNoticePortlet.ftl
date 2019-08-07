<div>
    <div style="text-align: center" class="portlet-notice-title">
        ${noNum}
        <#if noNum == 0>
            您今日没有要参加的会议！
        </#if>
    </div>
    <#list notices as notice>
        <div class="portlet-notice-item">
            <a href="#" onclick="$.meetingNotice.showMeetingNoticeInfo(${notice.meetingNoticeId})">[${notice.meetingStartTime}] ${notice.meetingName}</a>
        </div>
    </#list>
</div>