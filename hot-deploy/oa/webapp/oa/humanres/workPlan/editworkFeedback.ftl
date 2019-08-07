<div class="screenlet">
<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if workReportMap.feedback?has_content>
    <#assign viewIndex = workReportMap.viewIndex>
    <#assign highIndex = workReportMap.highIndex>
    <#assign totalCount = workReportMap.totalCount>
    <#assign viewSize = workReportMap.viewSize>
    <#assign commonUrl = "searchFeedbackList"/>
    <#assign sortParam = "workReportId=" + workReportMap.workReportId?default('')/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
    <div style="height: 150px">
        <#if workReportMap.feedback?has_content>
            <#list workReportMap.feedback as list>
            <div id="feedList">
                <div style="background:darkgray;height: 50px" class="screenlet">
                    ${list.groupName?default('')} ${list.fullName?default('')} 发表于：${list.feedbackTime?default('')}  查看权限:${list.permissionName?default('')}
                </div>
                <script language="javascript">
                $(function () {
                $("#${list.feedbackId?default('')}").html(unescapeHtmlText('${list.feedbackContext?default('')}'));
                });
                </script>
            <div id="${list.feedbackId?default('')}">
            </div>

            </div>
            </#list>
        </#if>
    </div>
</div>
<#if workReportMap.feedback?has_content>
<@nextPrevAjax targetId="workFeedList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>

