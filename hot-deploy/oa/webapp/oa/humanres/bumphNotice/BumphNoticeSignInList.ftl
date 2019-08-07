<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchSignInRecord"/>
    <#assign sortParam = "&department="+data.department?default("")+"&releaseTimeStart="+data.releaseTimeStart?default("")
                        +"&releaseTimeEnd="+data.releaseTimeEnd?default("")+"&title="+data.title?default("")
                        +"&noticeType="+data.noticeType?default("")+"&feedbackDepartment="+data.feedbackDepartment?default("")
                        +"&hasSignIn="+data.hasSignIn?default("")+"&status="+data.status?default("")/>
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
            <label>发布日期</label>
        </td>
        <td>
            <label>发布部门</label>
        </td>
        <td>
            <label>文档编号</label>
        </td>
        <td>
            <label>类型</label>
        </td>
        <td>
            <label>公文/通知标题</label>
        </td>
        <td>
            <label>签收进度</label>
        </td>
        <td>
            <label>浏览进度</label>
        </td>
        <#--<td>-->
            <#--<label>岗位</label>-->
        <#--</td>-->
        <#--<td>-->
            <#--<label>签收日期</label>-->
        <#--</td>-->
        <#--<td>-->
            <#--<label>签收状态</label>-->
        <#--</td>-->
        <#--<td>-->
            <#--<label>当前进度</label>-->
        <#--</td>-->
        <td>
            <label>操作</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                <label>${line.releaseTime?default('')}</label>
            </td>
            <td>
                <label>${line.groupName?default('')}</label>
            </td>
            <td>
                <label>RECTEC${line.noticeYear?default('')}CWB${line.noticeNumber?default('')}</label>
            </td>
            <td>
                <label>${line.noticeTypeDesc?default('')}</label>
            </td>
            <td>
                <a href="#" class="hyperLinkStyle" onclick="javascript:$.bumphNotice.showBumphNoticeInfo('${line.noticeId}')" title="查看">
                    ${line.title?default('')}
                </a>
            </td>
            <td>
                <label>未签收${line.noSignInCount?default('')}，已签收${line.signInCount?default('')}</label>
            </td>
            <td>
                <label>未浏览${line.noSeeCount?default('')}，了解中${line.seeCount?default('')}，已熟悉${line.understandCount?default('')}</label>
            </td>
            <#--<td>-->
                <#--<label>${line.positionDesc?default('')}</label>-->
            <#--</td>-->
            <#--<td>-->
                <#--<label>${line.signInTime?default('')}</label>-->
            <#--</td>-->
            <#--<td>-->
                <#--<label>${line.hasSignInDesc?default('')}</label>-->
            <#--</td>-->
            <#--<td>-->
                <#--<label>${line.stautsDesc?default('')}</label>-->
            <#--</td>-->
            <td>
                <a href="#" onclick="javascript:$.bumphNotice.BumphNoticeSignIn('${line.noticeId}')" title="查看" class="smallSubmit">查看</a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if data.list?has_content>
    <@nextPrevAjax targetId="BumphNoticeSignInList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>