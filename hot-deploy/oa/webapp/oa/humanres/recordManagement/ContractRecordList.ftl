<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchContract"/>
    <#assign sortParam = "&contractNumber="+data.contractNumber?default("")+"&contractName="+data.contractName?default("")
                        +"&contractType="+data.contractType?default("")+"&partyIdForSearch="+data.partyIdForSearch?default("")
                        +"&state="+data.state?default("")+"&startDate="+data.startDate?default("")+"&endDate="+data.endDate?default("")/>
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
            <label>员工编号</label>
        </td>
        <td>
            <label>员工名称</label>
        </td>
        <td>
            <label>合同编号</label>
        </td>
        <td>
            <label>合同名称</label>
        </td>
        <td>
            <label>合同类型</label>
        </td>
        <td>
            <label>签约时间</label>
        </td>
        <td>
            <label>生效时间</label>
        </td>
        <td>
            <label>失效时间</label>
        </td>
        <td>
            <label>编辑</label>
        </td>
        <td>
            <label>作废</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
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
                <label>${line.contractNumber?default('')}</label>
            </td>
            <td>
                <label>${line.contractName?default('')}</label>
            </td>
            <td>
                <label>${line.description?default('')}</label>
            </td>
            <td>
                <label>${line.signDate?default('')}</label>
            </td>
            <td>
                <label>${line.startDate?default('')}</label>
            </td>
            <td>
                <label>${line.endDate?default('')}</label>
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.editContract('${line.contractId}')" title="修改" class="icon-edit"></a>
            </td>
            <td>

                <#if line.contractStatus?has_content>
                    <#if line.contractStatus !="CONTRACT_STATUS_D">
                        <a id="#cancel" class="smallSubmit" href="#" onclick="javascript:$.recordManagement.cancelContract('${line.contractId}','','1')" title="操作">作废</a>
                    </#if>
                </#if>
            </td>
        </tr>

        <#assign rowCount = rowCount + 1>
        <#--toggle the row color-->
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if data.list?has_content>
    <@nextPrevAjax targetId="staffInformation1" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>