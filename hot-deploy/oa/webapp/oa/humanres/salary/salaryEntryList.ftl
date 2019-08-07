<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.salaryMapList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "SalaryManagementList"/>
    <#assign sortParam = ""/>
    <#assign param = sortParam + "partyId="+'${data.partyId}'/>
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
            <label>调整日期</label>
        </td>
        <td>
            <label>生效日期</label>
        </td>
        <td>
            <label>基本工资</label>
        </td>
        <td>
            <label>备注</label>
        </td>
        <td>
            <label>录入人</label>
        </td>
        <td>
            <label>编辑</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if data.salaryMapList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.salaryMapList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
            ${rowCount + 1}
            </td>
            <td>
            ${line.adjustmentTime?default("")}
            </td>
            <td>
            ${line.startTime?default("")}
            </td>
            <td>
            ${line.basePay?default("")}
            </td>
            <td>
            ${line.remarks?default("")}
            </td>
            <td>
            ${line.inputId?default("")}
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.SalaryManagementCreate('${data.partyId?default("")}','${line.salaryId?default("")}')" title="修改" class="icon-edit"/>
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.removeSalaryInfo('${line.salaryId?default("")}','${data.partyId?default("")}')" title="删除" class="icon-trash"/>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
        <#--toggle the row color-->
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.salaryMapList?has_content>
    <@nextPrevAjax targetId="SalaryManagementList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
</html>
