<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "Inbox"/>
    <#assign sortParam =""/>
    <#assign param = ""/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<form method="post" action="" id="Inbox" class="basic-form" name="Inbox">
    <table cellspacing="0" class="basic-table hover-bar" id="inboxTable">
        <tbody>
        <tr class="header-row-2">
            <td>
                <input type="checkbox" name="selectAll" onclick="javascript:toggleAll(this, 'Inbox');" value="Y">
            </td>
            <td>
                <label>状态</label>
            </td>
            <td>
                <label>发送人</label>
            </td>
            <td>
                <label>主题</label>
            </td>
            <td>
                <label>时间</label>
            </td>
            <td>
                <label>附件</label>
            </td>
        </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
            <tr id="ss">
                <td>
                    <input type="checkbox" id="Inbox__rowSubmit_o_${rowCount+1}" name="_rowSubmit_o_${rowCount+1}" value="${line.communicationEventId?default("")}">
                </td>
                <td>
                    <#if (line.statusId?default(""))=="COM_UNKNOWN_PARTY">
                        <span class="mail-icon mail-unread"></span>
                    <#elseif (line.statusId?default(""))=="COM_COMPLETE">
                        <span class="mail-icon mail-read"></span>
                    </#if>
                </td>
                <td>
                    <a href="#nowhere" onclick="javascript:$.email.viewEmail(${line.communicationEventId?default('')},'inbox')" title="发件人">
                        <label>${line.fromString?default("")}</label>
                    </a>
                </td>
                 <td>
                    <a href="#nowhere" onclick="javascript:$.email.viewEmail(${line.communicationEventId?default('')},'inbox')" title="主题">
                        <label>${line.subject?default("")}</label>
                    </a>
                 </td>
                <td>
                    <a href="#nowhere" title="时间">
                        <label>${line.datetimeStarted?default("")}</label>
                    </a>
                </td>
                <td>
                    <span class="mail-icon mail-attachment"></span>
                </td>
            </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
        </tbody>
    </table>
</form>
<#if data.list?has_content>
    <@nextPrevAjax targetId="mailContent" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>


