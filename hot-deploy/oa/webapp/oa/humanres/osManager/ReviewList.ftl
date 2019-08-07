<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "SearchListReceive "/>
    <#assign sortParam = "&warehouseId="+data.warehouseId?default("")+"&productTypeId="+data.productTypeId?default("")+"&receiveDepartment="+data.receiveDepartment?default("")
    +"&checkResult="+data.checkResult?default("")+"&receivePerson="+data.receivePerson?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<form method="post" action="" id="ListReceive" class="basic-form" name="ListReceive">
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
            <label>出库类型</label>
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
            <label>审核</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
            <tr id="ss">
                <td>
                    <a href="#" class="hyperLinkStyle" onclick="$.InventoryManagement.seeReceive(${line.receiveId?default('')})" title="领用单编号">${line.receiveCode?default("")}</a>
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
                    <#if (line.checkResult?default(""))=="RECEIVE_RESULT_ONE">
                        <label>审核完成</label>
                    <#elseif (line.checkResult?default(""))=="RECEIVE_RESULT_TWO">
                        <label>审核未通过</label>
                    <#elseif (line.checkResult?default(""))=="RECEIVE_RESULT_THREE">
                        <label>无需审核</label>
                    <#elseif (line.checkResult?default(""))=="RECEIVE_RESULT_FOUR">
                        <#if (line.checkJob?default(''))==(data.post?default(''))>
                            <a href="#" class="hyperLinkStyle" onclick="$.InventoryManagement.checkReceive(${line.receiveId?default('')})" title="审核">审核</a>
                        <#else>
                            <label>审核</label>
                        </#if>
                    </#if>

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
    <@nextPrevAjax targetId="ListReceiveResult" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
