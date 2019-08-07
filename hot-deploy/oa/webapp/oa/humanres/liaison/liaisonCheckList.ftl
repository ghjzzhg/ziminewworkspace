<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchLiaison"/>
    <#assign sortParam = "&responseTimeStart="+data.responseTimeStart?default("")+"&responseTimeEnd="+data.responseTimeEnd?default("")
    +"&title="+data.title?default("")+"&contactListType="+data.contactListType?default("")
    +"&reviewTheStatus="+data.list.reviewTheStatus?default("")/>
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
            <label>工作联络单标题</label>
        </td>
        <td>
            <label>联络单类型</label>
        </td>
        <td>
            <label>发送者所在部门</label>
        </td>
        <td>
            <label>发送者</label>
        </td>
        <td>
            <label>所有审核人</label>
        </td>
        <td>
            <label>状态</label>
        </td>
        <td>
            <label>审核</label>
        </td>
        <td>
            <label>发送日期</label>
        </td>
        <td>

        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                <a name="title" href="#" class="hyperLinkStyle" onclick="$.liaison.showLiaisonInfo(${line.contactListId?default("")})" title="工作联络单标题">
                ${line.title?default("")}
                </a>
            </td>
            <td>
                <label>${line.contactListTypeName?default("")}</label>
            </td>
            <td>
               <label>${line.departmentName?default("")}</label>
            </td>
            <td>
                <label>${line.fullNameString?default("")}</label>
            </td>
            <td>
                <label>${line.auditorPersonName?default("")}</label>
            </td>
            <#if (line.auditorPerson?default("")) == (data.partyId?default(""))>
                <td>
                    <#if (line.reviewTheStatus?default(''))=="LIAISON_STATUS_FOUR" || (line.reviewTheStatus?default(''))=="LIAISON_STATUS_ONE">
                        <label>${line.reviewTheStatusString?default("")}</label>
                    <#else>
                        <a name="reviewTheStatus" href="#" class="hyperLinkStyle"
                           ondblclick="$.liaison.onClickStatus($(this),'${line.contactListId}','${line.reviewTheStatus}')" title="状态">
                            <label>${line.reviewTheStatusString?default("")}</label>
                        </a>
                    </#if>
                </td>
            <#else>
                <td>
                    <label>${line.reviewTheStatusString?default("")}</label>
                </td>

            </#if>
            <td>
                <#if (line.reviewTheStatus?default(''))=="LIAISON_STATUS_FOUR" || (line.auditorPerson?default("")) != (data.partyId?default(""))>
                    <label>审核</label>
                <#elseif (line.reviewTheStatus?default(''))=="LIAISON_STATUS_ONE">
                    <a name="audit" href="#" class="hyperLinkStyle" onclick="$.liaison.showLiaisonInfos(${line.contactListId?default("")})" title="审核">
                        审核
                    </a>
                <#else>
                    <label>审核</label>
                </#if>
            </td>
            <td>
                <label>${line.createdStamp?default("")}</label>
            </td>
            <td>
                <#if (line.reviewTheStatus?default(''))!="LIAISON_STATUS_FOUR">
                    <a name="delete" class="icon-trash" href="#" onclick="$.liaison.deleteLiaison(${line.contactListId?default("")})" title="作废"></a>
                </#if>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.list?has_content>
    <@nextPrevAjax targetId="LiaisonCheckList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>