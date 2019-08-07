
<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if recordList?has_content>
    <#assign commonUrl = "ListDepartmentMembers"/>
    <#assign sortParam = "partyId=" + partyId/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
        <tr class="header-row-2">
            <td width="50px">
                <label>序号</label>
            </td>
            <td width="150px">
                <label>工号</label>
            </td>
            <td width="200px">
                <label>部门</label>
            </td>
            <td>
                <label>
                <@sortFieldAjax targetId="PersonList" headerName="姓名" currentSort=sortField?default("") sortField="fullName" commonUrl=commonUrl param=sortParam/>
                </label>
            </td>
            <td width="80px">
                <label>性别</label>
            </td>
            <td>
                <label>
                    <@sortFieldAjax targetId="PersonList" headerName="岗位" currentSort=sortField?default("") sortField="memberRoleTypeId" commonUrl=commonUrl param=sortParam/>
                </label>
            </td>
            <td width="80px">
                修改
            </td>
            <td width="100px">
                登录账户
            </td>
        </tr>
    <#if recordList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list recordList as line>
        <tr<#if alt_row> class="alternate-row"</#if>>
            <td>
                ${rowCount + 1}
            </td>
            <td>
                ${line.employeeJobNumber?default("")}
            </td>
            <td>
                ${line.department?default("")}
            </td>
            <td>
                ${line.employeeName?default("")}
            </td>
            <td>
                ${line.gender?default("")}
            </td>
            <td>
                ${line.post?default("")}
            </td>
            <td style="text-align: center">
                <a href="#nowhere" onclick="javascript:$.recordManagement.editEmployee('${line.partyId?default('')}');" title="修改" class="icon-edit"></a>
            </td>
            <td>
                <table>
                    <tr>
                <#if line.loginIds?has_content>
                <td>
                    <#list line.loginIds as loginId>
                        <div>
                            <a href="#nowhere" onclick="javascript:$.recordManagement.editUserLogin('${loginId}');" title="修改">${loginId}</a>
                        </div>
                    </#list>
                </td>
                </#if>
                        <td>
                            <a href="#nowhere" onclick="javascript:$.recordManagement.createUserLogin('${line.partyId?default('')}');" title="增加" class="icon-plus"></a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
        <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if recordList?has_content>
    <@nextPrevAjax targetId="PersonList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>