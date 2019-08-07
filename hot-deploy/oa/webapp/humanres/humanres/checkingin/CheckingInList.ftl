<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if checkingInList?has_content>
    <#assign viewIndex = viewIndex>
    <#assign highIndex = highIndex>
    <#assign totalCount = totalCount>
    <#assign viewSize = viewSize>
    <#assign lowIndex = lowIndex>
    <#assign commonUrl = "SearchCheckingInInfoForm"/>
    <#assign sortParam = "&checkingInType="+parameters.checkingInType?default("")+"&checkingInStatus="+parameters.checkingInStatus?default("")
                        +"&startDate="+parameters.startDate?default("")+"&endDate="+parameters.endDate?default("")
                        +"&staff="+parameters.staff?default("")+"&weekDay="+parameters.weekDay?default("")/>
    <#assign param = sortParam + "&sortField=" + parameters.sortField?default("")/>
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
            <label>序号</label>
        </td>
        <td>
            <label>登记时间</label>
        </td>
        <td>
            <label>考勤时间</label>
        </td>
        <td>
            <label>登记人</label>
        </td>
        <td>
            <label>班次</label>
        </td>
        <td>
            <label>班次类型</label>
        </td>
        <td>
            <label>考勤状态</label>
        </td>
        <td>
            <label>星期</label>
        </td>
        <td>
            <label>上下班</label>
        </td>
        <td>
            <label>迟到或早退分钟</label>
        </td>
        <td>
            <label>迟到/早退原因</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if checkingInList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list checkingInList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                ${rowCount + 1}
            </td>
            <td>
                <label>${line.registerDate?string('yyyy-MM-dd HH:mm:ss')}</label>
            </td>
            <td>
                <label>${line.checkingInDate?string('yyyy-MM-dd ')}${line.checkingInTime?string('HH:mm:ss')}</label>
            </td>
            <td>
                <label>${line.staffName?default('')}</label>
            </td>
            <td>
                <label>${line.listOfWorkName?default('')}</label>
            </td>
            <td>
                <label>${line.listOfWorkTypeName?default('')}</label>
            </td>
            <td>
                <label>${line.checkingInStatusDesc?default('')}</label>
            </td>
            <td>
                <label>${line.weekDay?default('')}</label>
            </td>
            <td>
                <label>${line.checkingInTypeDesc?default('')}</label>
            </td>
            <td>
                <label>${line.minutes?default('')}</label>
            </td>
            <td>
                <label>${line.cause?default('')}</label>
            </td>
            <td>
                <a href="#nowhere" onclick="$.checkingIn.deleteCheckingIn('${line.checkingInDate?default('')}','${line.staff?default('')}','${line.listOfWorkId?default('')}','${line.checkingInType?default('')}')" title="删除" class="icon-trash"></a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if checkingInList?has_content>
    <@nextPrevAjax targetId="ListCheckingInSubOrgs" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>