<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchUnitContract"/>
    <#assign sortParam = "&contractType="+data.contractType?default("")+"&contractNumber="+data.contractNumber?default("")
    +"&contractName="+data.contractName?default("")+"&department1="+data.department1?default("")+"&customerName="+data.customerName?default("")
    +"&balanceOfPaymentsType="+data.balanceOfPaymentsType?default("")+"&contractMoneyStart="+data.contractMoneyStart?default("")+"&contractMoneyEnd="+data.contractMoneyEnd?default("")
    +"&signContractStartDate="+data.signContractStartDate?default("")+"&signContractEndDate="+data.signContractEndDate?default("")/>
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
                <label>合同编号</label>
            </td>
            <td>
                <label>合同名称</label>
            </td>
            <td>
                <label>所属部门</label>
            </td>
            <td>
                <label>所属客户名称</label>
            </td>
            <td>
                <label>收/支类型</label>
            </td>
            <td>
                <label>合同总金额</label>
            </td>
            <td>
                <label>合同开始日期</label>
            </td>
            <td>
                <label>合同结束日期</label>
            </td>
            <td>
                <label>过期提醒</label>
            </td>
            <td>
                <label>合同状态</label>
            </td>
            <td>
                <label></label>
            </td>
            <td>
                <label></label>
            </td>
        </tr>
        <#if data.list?has_content>
            <#assign alt_row = false>
            <#assign rowCount = viewIndex * viewSize>
            <#list data.list as line>
            <tr id="ss"
                <#if (line.expiredReminder?default(''))=="yes" && (line.contractState?default('')=="1")>
                    style="background-color: orange"
                </#if>
                    >
                <td>
                    <label>${line.contractNumber?default('')}</label>
                </td>
                <td>
                    <label>${line.contractName?default('')}</label>
                </td>
                <td>
                    <label>${line.departmentName?default('')}</label>
                </td>
                <td>
                    <label>${line.customerName?default('')}</label>
                </td>
                <td>
                    <label>${line.balanceOfPaymentsTypeName?default('')}</label>
                </td>
                <td>
                    <label>${line.contractMoney?default('')}</label>
                </td>
                <td>
                    <label>${line.contractStartDate?default('')}</label>
                </td>
                <td>
                    <label>${line.contractEndDate?default('')}</label>
                </td>
                <td>
                    <#if (line.expiredReminder?default(''))=="yes">
                        <label>是</label>
                    <#elseif (line.expiredReminder?default(''))=="no">
                        <label>否</label>
                    </#if>

                </td>
                <td>
                    <#if (line.contractState?default(''))=="0">
                        <label>未生效</label>
                    <#elseif (line.contractState?default(''))=="1">
                        <label>已过期</label>
                    <#elseif (line.contractState?default(''))=="2">
                        <label>已生效</label>
                    </#if>
                </td>
                <td>
                    <a href="#" onclick="$.UnitContract.editUnitContract(${line.unitContractId?default('')})" class="icon-edit" title="更新"></a>
                </td>
                <td>
                    <a href="#" onclick="$.UnitContract.deleteUnitContract(${line.unitContractId?default('')})" class="icon-trash" title="删除"></a>
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
    <@nextPrevAjax targetId="UnitContractListId" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>


