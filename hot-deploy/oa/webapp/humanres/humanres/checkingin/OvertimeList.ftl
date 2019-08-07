<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#assign returnValue = parameters.get("returnValue")>
<#assign user = parameters.get("userLogin")>
<#if overtimeList?has_content>
    <#assign viewIndex = returnValue.viewIndex>
    <#assign highIndex = returnValue.highIndex>
    <#assign totalCount = returnValue.totalCount>
    <#assign viewSize = returnValue.viewSize>
    <#assign lowIndex = returnValue.lowIndex>
    <#assign commonUrl = "searchOvertime"/>
    <#assign sortParam = "&overtimeStartDate="+returnValue.overtimeStartDate?default("")+"&overtimeEndDate="+returnValue.overtimeEndDate?default("")
                        +"&overtimeStaff="+returnValue.overtimeStaff?default("")+"&overtimeDepartment="+returnValue.overtimeDepartment?default("")
                        +"&overTimeType="+returnValue.overTimeType?default("")/>
    <#assign param = sortParam + "&sortField=" + returnValue.sortField?default("")/>
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
            <label>开始时间</label>
        </td>
        <td>
            <label>结束时间</label>
        </td>
        <td>
            <label>加班类型</label>
        </td>
        <td>
            <label>所属员工或部门</label>
        </td>
        <td>
            <label>审核人</label>
        </td>
        <td>
            <label>审核</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if overtimeList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list overtimeList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                ${rowCount + 1}
            </td>
            <td>
                <label>${line.startDate?default('')}</label>
            </td>
            <td>
                <label>${line.endDate?default('')}</label>
            </td>
            <td>
                <label>
                    <#list overtimeTypeList as type><#if type.enumId == line.overtimeType>${type.description}</#if></#list>
                </label>
            </td>
            <td>
                <#if line.type.equals("个人")>
                    <label>${line.fullName?default('')}</label>
                <#elseif line.type.equals("组织")>
                    <label>${line.groupName?default('')}</label>
                </#if>
            </td>
            <td>
                <label>${line.auditorName?default('')}</label>
            </td>
            <td>
                <#if line.overtimeState == "PERSON_ONE" && line.auditor == user.partyId>
                    <a href="#nowhere" onclick="$.checkingIn.auditorOvertime('${line.overtimeId}')" title="审核" class="icon-eye-open"></a>
                <#elseif line.overtimeState == "PERSON_ONE" && line.auditor != user.partyId>
                    <label>待审核人审核</label>
                <#elseif line.overtimeState == "PERSON_TWO">
                    <label>审核通过</label>
                <#elseif line.overtimeState == "PERSON_THREE">
                    <label>审核驳回</label>
                </#if>
            </td>
            <td>
                <a href="#nowhere" onclick="$.checkingIn.addOvertime('${line.overtimeId}')" title="更新" class="icon-edit"></a>
            </td>
            <td>
                <a href="#nowhere" onclick="$.checkingIn.deleteOvertime('${line.overtimeId}')" title="删除" class="icon-trash"></a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if overtimeList?has_content>
    <@nextPrevAjax targetId="OvertimeList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>