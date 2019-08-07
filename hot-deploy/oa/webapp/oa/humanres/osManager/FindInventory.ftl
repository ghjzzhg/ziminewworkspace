<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "InventoryListInfo"/>
    <#assign sortParam = "&warehouseId="+data.warehouseId?default("")+"&productTypeId="+data.productTypeId?default("")
    +"&productName="+data.suppliesName?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<form method="post" action="" id="InventoryList" class="basic-form" name="InventoryList">
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>仓库名称</label>
        </td>
        <td>
            <label>货品类别</label>
        </td>
        <td>
            <label>易耗品名称</label>
        </td>
        <td>
            <label>易耗品规格</label>
        </td>
        <td>
            <label>单位</label>
        </td>
        <td>
            <label>库存总数</label>
        </td>
        <td>
            <label>库存数量开单未领数量</label>
        </td>
        <td>
            <label>可用数量</label>
        </td>
        <td>
            <label>货位</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
            <tr id="ss" onclick="$.InventoryManagement.moveValue('${parameters.id}','${line.suppliesName?default('')}','${line.standardName?default('')}','${line.typeName?default('')}',
                    '${line.unitName?default('')}','${line.repertoryAmount?default('')}','${line.canUseAmount?default('')}','${line.osManagementId?default('')}')">
                <td>
                    <label>${line.warehouseName?default('')}</label>
                </td>
                <td>
                    <label>${line.typeName?default('')}</label>
                </td>
                <td>
                    <label>${line.suppliesName?default('')}</label>
                </td>
                <td>
                    <label>${line.standardName?default('')}</label>
                </td>
                <td>
                    <label>${line.unitName?default('')}</label>
                </td>
                <td>
                    <label>${line.repertoryAmount?default('')}</label>
                </td>
                <td>
                    <label>${line.notReceiveAmount?default('')}</label>
                </td>
                <td>
                    <label>${line.canUseAmount?default('')}</label>
                </td>
                <td>
                    <label>${line.storageLocationName?default('')}</label>
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
    <@nextPrevAjax targetId="InventoryList2" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
