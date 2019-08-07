<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchWarehouseInfo"/>

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
            <label>仓库编号</label>
        </td>
        <td>
            <label>仓库名称</label>
        </td>
        <td>
            <label>仓库地址</label>
        </td>
        <td>
            <label>联系人</label>
        </td>
        <td>
            <label>联系电话</label>
        </td>
        <td>
            <label>录入人</label>
        </td>
        <td>
            <label>录入时间</label>
        </td>
        <td>
            <label>最后编辑人/编辑日期</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                <label>${line.warehouseCode?default('')}</label>
            </td>
            <td>
                <label>${line.warehouseName?default('')}</label>
            </td>
            <td>
                <label>${line.warehouseAddress?default('')}</label>
            </td>
            <td>
                <label>${line.linkmanName?default('')}</label>
            </td>
            <td>
                <label>${line.phone?default('')}</label>
            </td>
            <td>
                <label>${line.inputPersonName?default('')}</label>
            </td>
            <td>
                <label>${line.inputTime?default('')}</label>
            </td>
            <td>
                <label>${line.lastEditPersonName?default('')}/${line.lastEditTime?default('')}</label>
            </td>
            <td>
                <a href="#" onclick="$.inventory.editWarehouseInfo('${line.warehouseId?default('')}')" title="修改" class="icon-edit"></a>
            </td>
            <td>
                <a href="#" onclick="$.inventory.deleteInventoryInfo('${line.warehouseId?default('')}')" title="删除" class="icon-trash"></a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.list?has_content>
    <@nextPrevAjax targetId="inventoryList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>