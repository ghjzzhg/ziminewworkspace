<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if meetingSummaryList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchMeetingSummary"/>
    <#assign sortParam = "&meetingName="+data.meetingName?default("")+"&meetingTheme="+data.meetingTheme?default("")
                        +"&startTime="+data.meetingStartTime?default("")+"&endTime="+data.meetingEndTime?default("")
                        +"&releaseDepartment="+data.releaseDepartment?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>会议时间</label>
        </td>
        <td>
            <label>发布部门</label>
        </td>
        <td>
            <label>会议名称</label>
        </td>
        <td>
            <label>会议主题</label>
        </td>
        <td>
            <label>查看内容</label>
        </td>
        <td>
            <label>发布日期</label>
        </td>
        <td>
            <label>最后反馈人（反馈日期）</label>
        </td>
        <td>
            <label>纪要发布人</label>
        </td>
        <td>
            <label>纪要最后编辑人(编辑日期)</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if meetingSummaryList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list meetingSummaryList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                <label>${line.meetingStartTime?default('')}~${line.meetingEndTime?default('')}</label>
            </td>
            <td>
                <label>${line.groupName?default('')}</label>
            </td>
            <td>
                <#if line.summaryId?has_content>
                    <a name="editSummary" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},${line.summaryId})" title="查看">
                        ${line.meetingName?default('')}
                    </a>
                <#else>
                    <a name="editMeetingNotice" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},null)" title="查看">
                        ${line.meetingName?default('')}
                    </a>
                </#if>
            </td>
            <td>
                <#if line.summaryId?has_content>
                    <a name="editSummary" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},${line.summaryId})" title="查看">
                        ${line.meetingTheme?default('')}
                    </a>
                <#else>
                    <a name="editMeetingNotice" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},null)" title="查看">
                        ${line.meetingTheme?default('')}
                    </a>
                </#if>
            </td>
            <td>
                <#if line.summaryId?has_content>
                    <a name="editSummary"  href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},${line.summaryId})" title="查看">
                        ${line.hasMeetingStart}(${line.hasReleaseSummary})
                    </a>
                <#else>
                    <a name="editMeetingNotice" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},null)" title="查看">
                        ${line.hasMeetingStart}(${line.hasReleaseSummary})
                    </a>
                </#if>
            </td>
            <td>
                <label>${line.releaseTime?default('')}</label>
            </td>
            <td>
                <#if line.summaryId?has_content>
                    <a name="editSummary" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},${line.summaryId})" title="查看">
                        ${line.lastFeedbackPerson?default('')}${line.lastFeedbackTime?default('')}
                    </a>
                <#else>
                    <a name="editMeetingNotice" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingInfo(${line.meetingNoticeId},null)" title="查看">
                        ${line.lastFeedbackPerson?default('')}${line.lastFeedbackTime?default('')}
                    </a>
                </#if>
            </td>
            <td>
                <label>${line.releasePersonName?default('')}</label>
            </td>
            <td>
                <label>${line.lastEditPersonName?default('')}${line.lastEditTime?default('')}</label>
            </td>
            <td>
                <#if line.summaryId?has_content>
                    <a name="editSummary" class="icon-edit" href="#" onclick="javascript:$.meetingNotice.summaryRelease(${line.summaryId},'edit')" title="修改"></a>
                <#else>
                    <a name="editMeetingNotice" class="icon-edit" href="#" onclick="javascript:$.meetingNotice.addMeetingNotice(${line.meetingNoticeId})" title="修改"></a>
                </#if>
            </td>
            <td>
                <#if line.summaryId?has_content>
                    <a name="deleteSummary" class="icon-trash" href="#" onclick="javascript:$.meetingNotice.deleteSummary(${line.summaryId})" title="删除"></a>
                <#else>
                    <a name="editMeetingNotice" class="icon-trash" href="#" onclick="javascript:$.meetingNotice.deleteMeetingNotice(${line.meetingNoticeId})" title="删除"></a>
                </#if>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if meetingSummaryList?has_content>
    <@nextPrevAjax targetId="MeetingSummaryList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>