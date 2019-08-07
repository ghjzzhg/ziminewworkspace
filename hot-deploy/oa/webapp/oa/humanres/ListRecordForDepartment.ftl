<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.recordList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "ListRecordForDepartment"/>
    <#--<#assign sortParam = "partyId="+data.partyId?default("")/>-->
    <#assign param = "partyId="+data.partyId?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<input type="hidden" id="departmentId" value="${data.partyId?default('')}">
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>序号</label>
        </td>
        <td>
            <label>工号</label>
        </td>
        <td>
            <label>姓名</label>
        </td>
        <td>
            <label>性别</label>
        </td>
        <td>
            <label>年龄</label>
        </td>
        <td>
            <label>部门</label>
        </td>
        <td>
            <label>学历</label>
        </td>
        <td>
            <label>入职日期</label>
        </td>
        <td>
            <label>查看</label>
        </td>
    </tr>
    <#if data.recordList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.recordList as line>
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
                <label>${line.genderName?default('')}</label>
            </td>
            <td>
                <label>${line.age?default('')}</label>
            </td>
            <td>
                <label>${line.groupName?default('')}</label>
            </td>
            <td>
                <label>${line.diploma?default('')}</label>
            </td>
            <td>
                <label>${line.workDate?default('')}</label>
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.ViewEmployee('${line.partyId}')" title="查看" class="icon-eye-open"/>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.recordList?has_content>
    <@nextPrevAjax targetId="ListSubOrgs" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
<#--<@showFileList id="abc" hiddenId="fileListH"/>-->
</html>