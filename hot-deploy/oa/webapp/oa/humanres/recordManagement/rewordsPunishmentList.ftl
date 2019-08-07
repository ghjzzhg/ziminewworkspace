<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.rewordsPunishmentList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "rewordList"/>
    <#assign sortParam = "startDate="+data.startDate?default("")+"&endDate="+data.endDate?default("")+"&number="+data.number?default("")+"&name="+data.name?default("")+"&type="+data.type?default("")+"&level="+data.level?default("")+"&partyIdForSearch="+data.partyIdForSearch?default("")/>
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
            <label>序号</label>
        </td>
        <td>
            <label>员工工号</label>
        </td>
        <td>
            <label>员工姓名</label>
        </td>
        <td>
            <label>奖惩编号</label>
        </td>
        <td>
            <label>奖惩名称</label>
        </td>
        <td>
            <label>奖惩类型</label>
        </td>
        <td>
            <label>奖惩等级</label>
        </td>
        <td>
            <label>奖惩金额</label>
        </td>
        <td>
            <label>奖惩时间</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if data.rewordsPunishmentList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.rewordsPunishmentList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
            ${rowCount + 1}
            </td>
            <td>
                <label>${line.workerSn?default('')}</label>
            </td>
            <td>
                <label>${line.fullName?default('')}</label>
            </td>
            <td>
                <label>${line.number?default('')}</label>
            </td>
            <td>
                <label>${line.name?default('')}</label>
            </td>
            <td>
                <label>${line.typeName?default('')}</label>
            </td>
            <td>
                <label>${line.levelName?default('')}</label>
            </td>
            <td>
                <label>${line.money?default('')}</label>
            </td>
            <td>
                <label>${line.date?default('')}</label>
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.editReword('${line.rewordId}','${line.partyId}','rewordList')" title="修改" class="icon-edit"/>
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.removeReword('${line.rewordId}','','rewordList')" title="删除" class="icon-trash"/>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.rewordsPunishmentList?has_content>
    <@nextPrevAjax targetId="staffInformation" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
</html>