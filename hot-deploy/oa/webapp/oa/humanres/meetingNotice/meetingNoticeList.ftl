<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchMeetingNotice"/>
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
            <label>通知日期</label>
        </td>
        <td>
            <label>发布纪要</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                <label>${line.meetingStartTime?default('')}~${line.meetingEndTime?default('')}</label>
            </td>
            <td>
                <label>${line.groupName?default('')}</label>
            </td>
            <td>
                <a name="showMeetingNotice" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingNoticeInfo('${line.meetingNoticeId}')" title="查看">
                    ${line.meetingName?default('')}
                </a>
            </td>
            <td>
                <a name="showMeetingNotice" href="#" class="hyperLinkStyle" onclick="javascript:$.meetingNotice.showMeetingNoticeInfo('${line.meetingNoticeId}')" title="查看">
                   ${line.meetingTheme?default('')}
                </a>
            </td>
            <td>
                <label>${line.releaseTime?default('')}</label>
            </td>
            <td>
                <a name="releaseSummary" class="smallSubmit" href="#" onclick="$.meetingNotice.summaryRelease(${line.meetingNoticeId},'create')" title="发布纪要">发布纪要</a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if data.list?has_content>
    <@nextPrevAjax targetId="MeetingNoticeList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>