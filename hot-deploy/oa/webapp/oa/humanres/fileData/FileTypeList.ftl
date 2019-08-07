<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if ListFileType?has_content>
    <#assign viewIndex = viewIndex>
    <#assign highIndex = highIndex>
    <#assign totalCount = totalCount>
    <#assign viewSize = viewSize>
    <#assign lowIndex = lowIndex>
    <#assign commonUrl = "ListFileType"/>
    <#assign sortParam = "&Id="+parameters.Id?default("")/>
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
            <label>文档类别名称</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if ListFileType?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list ListFileType as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                ${rowCount + 1}
                <input type="hidden" name="fileTypeId" value="${line.fileTypeId?default('')}">
            </td>
            <td>
                <label>${line.typeName?default('')}</label>
            </td>
            <td>
                <a href="#nowhere" onclick="javascript:$.FileData.modifyFileType('${line.fileTypeId}','修改');" title="修改" class="icon-edit"></a>
            </td>
            <td>
                <a href="#nowhere" onclick="javascript:$.FileData.deleteFileType('${line.fileTypeId}');" title="删除" class="icon-trash"></a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if ListFileType?has_content>
    <@nextPrevAjax targetId="ListFileType" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>