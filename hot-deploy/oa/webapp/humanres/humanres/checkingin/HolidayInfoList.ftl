<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#assign returnValue = parameters.get("returnValue")>
<#if holidayInfoList?has_content>
    <#assign viewIndex = returnValue.viewIndex>
    <#assign highIndex = returnValue.highIndex>
    <#assign totalCount = returnValue.totalCount>
    <#assign viewSize = returnValue.viewSize>
    <#assign lowIndex = returnValue.lowIndex>
    <#assign commonUrl = "searchHoliday"/>
    <#assign sortParam = "&holidayStartDate="+returnValue.holidayStartDate?default("")+"&holidayEndDate="+returnValue.holidayEndDate?default("")
                        +"&staff="+returnValue.staff?default("")+"&department="+returnValue.department?default("")/>
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
            <label>假期类型</label>
        </td>
        <td>
            <label>所属员工或部门</label>
        </td>
        <td>
            <label>查看</label>
        </td>
        <td>
            <label>更新</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if holidayInfoList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list holidayInfoList as line>
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
                    <#list holidayTypeList as type><#if type.enumId == line.holidayType>${type.description}</#if></#list>
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
                <a href="#nowhere" onclick="$.checkingIn.lookHolidayInfo('${line.holidayId}')" title="查看" class="icon-eye-open"></a>
            </td>
            <td>
                <a href="#nowhere" onclick="$.checkingIn.addHolidayInfo('${line.holidayId}')" title="更新" class="icon-edit"></a>
            </td>
            <td>
                <a href="#nowhere" onclick="$.checkingIn.deleteHoliday('${line.holidayId}')" title="删除" class="icon-trash"></a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if holidayInfoList?has_content>
    <@nextPrevAjax targetId="ListHolidayInfo" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>