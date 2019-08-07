<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.salaryPayOffList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchSalaryPayOffList"/>
    <#assign sortParam = ""/>
    <#assign param = sortParam/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<script language="javascript">
    $(function () {
        $("[name='moneh']").each(function () {
            $(this).html(unescapeHtmlText($(this).html()));
        });
    });
</script>
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <input id="allCheckbox" type="checkbox">
        </td>
        <td>
            <label>部门</label>
        </td>
        <td>
            <label>工号</label>
        </td>
        <td>
            <label>姓名</label>
        </td>
        <td>
            <label>岗位</label>
        </td>
        <td>
            <label>性别</label>
        </td>
    <#list [1,2,3,4,5,6,7,8,9,10,11,12] as index>
        <td>
            <label>${index}月</label>
        </td>
    </#list>
    </tr>
    <#if data.salaryPayOffList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.salaryPayOffList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
        <tr class="alternate-row" id="salaryList">
            <td>
                <input name="" type="checkbox">
            </td>
            <td>
            ${line.groupName?default("")}
            </td>
            <td>
            ${line.workerSn?default("")}
            </td>
            <td>
            ${line.name?default("")}
            </td>
            <td>
            ${line.postName?default("")}
            </td>
            <td>
            ${line.genderName?default("")}
            </td>
        <#--<#escape x as x?html>-->
            <td>
                <span name="moneh">${line.moneh1?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh2?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh3?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh4?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh5?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh6?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh7?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh8?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh9?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh10?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh11?default("")}</span>
            </td>
            <td>
                <span name="moneh">${line.moneh12?default("")}</span>
            </td>
        <#--</#escape>-->
        </tr>
            <#assign rowCount = rowCount + 1>
        <#--toggle the row color-->
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.salaryPayOffList?has_content>
    <@nextPrevAjax targetId="SalaryPayOffListDiv" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
</html>
