<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if salaryMouldList?has_content>
    <#assign viewIndex = viewIndex>
    <#assign highIndex = highIndex>
    <#assign totalCount = totalCount>
    <#assign viewSize = viewSize>
    <#assign lowIndex = lowIndex>
    <#assign commonUrl = "showTemplateManagement"/>
    <#assign sortParam = ''/>
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
            <label>模板名称</label>
        </td>
        <td>
            <label>创建人</label>
        </td>
        <td>
            <label>创建时间</label>
        </td>
        <td>
            <label>使用状态</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if salaryMouldList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list salaryMouldList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                ${rowCount + 1}
                <input type="hidden" name="mouldId" value="${line.mouldId?default('')}">
            </td>
            <td>
                <label>${line.mouldName?default('')}</label>
            </td>
            <td>
                <label>${line.createdPersonName?default('')}</label>
            </td>
            <td>
                <label>${line.createdTime?string('yyyy-MM-dd HH:mm:ss')}</label>
            </td>
            <td>
                <#if line.useState.equals("USING")>
                    <label>${line.useStateDesc?default('')}</label>
                <#else>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.salary.changeTemplateState('${line.mouldId?default('')}');" title="使用状态">选择使用</a>
                </#if>
            </td>
            <td>
                <a class="icon-edit" href="#nowhere" onclick="javascript:$.salary.addTemplateManagement('${line.mouldId?default('')}');" title="修改"></a>
            </td>
            <td>
                <a class="icon-trash" href="#nowhere" onclick="javascript:$.salary.deleteSalaryTemplate('${line.mouldId?default('')}');" title="删除"></a>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if salaryMouldList?has_content>
    <@nextPrevAjax targetId="showTemplateManagement" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>