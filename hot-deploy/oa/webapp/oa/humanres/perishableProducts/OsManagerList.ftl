<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "ListOsManager"/>
    <#assign sortParam = "&suppliesName="+data.suppliesName?default("")+"&productTypeId="+data.productTypeId?default("")
    +"&repertoryWarning="+data.repertoryWarning?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<form method="post" action="" id="ListOsManager" class="basic-form" name="ListOsManager">
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>物品名称</label>
        </td>
        <td>
            <label>计量单位</label>
        </td>
        <td>
            <label>库存总数</label>
        </td>
        <td>
            <label>所属分类</label>
        </td>
        <td>
            <label>是否启用库存警示</label>
        </td>
        <td>
            <label>规格</label>
        </td>
        <td>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
            <tr id="ss"
                <#if alt_row>
                class="alternate-row"
                </#if>
                <#if (line.repertoryWarning?default(""))=="Y" && (line.MaximumSafety?default("")) < (line.repertoryAmount?default(""))>
                    style="color:red"
                <#elseif (line.repertoryWarning?default(""))=="Y" && (line.repertoryAmount?default("")) < (line.MinimumSafety?default(""))>
                    style="color:green"
                </#if>
                >
                <td>
                    <label>${line.suppliesName?default('')}</label>
                </td>
                <td>
                    <label>${line.unitName?default('')}</label>
                </td>
                <td>
                    <label>${line.repertoryAmount?default('')}</label>
                </td>
                <td>
                    <label>${line.productTypeName?default('')}</label>
                </td>
                <td>
                    <#if (line.repertoryWarning?default(""))=="Y">
                        <label>是</label>
                    <#elseif (line.repertoryWarning?default(""))=="N">
                        <label>否</label>
                    </#if>
                </td>
                <td>
                    <label>${line.standardName?default('')}</label>
                </td>
                <td>
                    <a name="editLink" href="#" onclick="$.osManager.showUpdateForm('${line.osManagementId}')" title="更新" class="icon-edit"></a>
                </td>
            </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.list?has_content>
    <@nextPrevAjax targetId="ListOsManager" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
