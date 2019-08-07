<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchCustomerInfo"/>
    <#assign sortParam = "&customerType="+data.customerType?default("")+"&customerName="+data.customerName?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<form method="post" action="" id="ListCustomerInfo" class="basic-form" name="ListCustomerInfo">
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>客户名称</label>
        </td>
        <td>
            <label>客户类型</label>
        </td>
        <td>
            <label>电话号码</label>
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
            <tr id="ss">
                <td>
                    <a href="#" name="showCustomer" onclick="$.customer.showCustomerInfo('${line.customerInfoId}')"
                            title="" class="hyperLinkStyle">${line.customerName?default('')}</a>
                </td>
                <td>
                    <label>${line.customerTypeName?default('')}</label>
                </td>
                <td>
                    <label>${line.telephone?default('')}</label>
                </td>
                <td>
                    <a name="editLink" href="#" onclick="$.customer.showCustomerInfoForm('${line.customerInfoId}')" title="更新" class="icon-edit"></a>
                </td>
                <td>
                    <a name="deleteLink" href="#" onclick="$.customer.deleteCustomerInfo('${line.customerInfoId}')" title="删除" class="icon-trash"></a>
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
    <@nextPrevAjax targetId="ListCustomerInfo" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
