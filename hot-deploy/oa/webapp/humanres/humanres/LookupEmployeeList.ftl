<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.employeeList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchLookupEmployee"/>
    <#assign sortParam = "&name="+data.name?default("")+"&fullName="+data.fullName?default("")
                        +"&lookUpGroupName="+data.lookUpGroupName?default("")+"&lookUpOccupation="+data.lookUpOccupation?default("")
                        +"&showlookUpGroupName="+data.showlookUpGroupName?default("")+"&showlookUpOccupation="+data.showlookUpOccupation?default("")/>
    <#assign param = sortParam + ""/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<table cellspacing="0" class="basic-table hover-bar">
    <tr class="header-row">
        <td>
            <label>名称</label></td>
        <td>
            <label>部门</label></td>
        <td>
            <label>岗位</label></td>
    </tr>
    <#if data.employeeList?has_content>
        <#list data.employeeList as line>
            <tr>
                <td>
                    <label><a href="#" onclick="setLookUpInfo${parameters.name}('${line.partyId}',true)" class="smallSubmit">${line.fullName?default("")}</a><#if line.jobState?has_content &&  line.jobState != "WORKING">[离职]</#if></label></td>
                <td>
                    <label>${line.groupName?default("")}</label></td>
                <td>
                    <label>${line.postName?default("")}</label></td>
            </tr>
        </#list>
    </#if>
</table>
<#if data.employeeList?has_content>
    <@nextPrevAjax targetId="LookupEmployeeList_col" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>