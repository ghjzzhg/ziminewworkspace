<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "ApproveReceive "/>
    <#assign param = "0"/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<form method="post" action="" id="ApproveReceive" class="basic-form" name="ApproveReceive">
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>领用单编号</label>
        </td>
        <td>
            <label>仓库名称</label>
        </td>
        <td>
            <label>领用类型</label>
        </td>
        <td>
            <label>预计领用日期</label>
        </td>
        <td>
            <label>领用部门</label>
        </td>
        <td>
            <label>领用人</label>
        </td>
        <td>
            <label>制单人</label>
        </td>
        <td>

        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
            <tr id="ss">
                <td>
                    <label>${line.receiveCode?default("")}</label>
                </td>
                <td>
                    <label>${line.warehouseName?default("")}</label>
                </td>
                <td>
                    <label>${line.outInventoryTypeName?default("")}</label>
                </td>
                <td>
                    <label>${line.receiveDate?default("")}</label>
                </td>
                <td>
                    <label>${line.receiveDepartmentName?default("")}</label>
                </td>
                <td>
                    <label>${line.receivePersonName?default("")}</label>
                </td>
                <td>
                    <label>${line.makeInfoPersonName?default("")}</label>
                </td>
                <td>
                    <a href="#" name="out" class="smallSubmit" onclick="$.InventoryManagement.delivery(${line.receiveId?default('')})">发货</a>
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
    <@nextPrevAjax targetId="ApproveReceive" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
