<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if postList?has_content>
    <#assign viewIndex = viewIndex>
    <#assign highIndex = highIndex>
    <#assign totalCount = totalCount>
    <#assign viewSize = viewSize>
    <#assign lowIndex = lowIndex>
    <#assign commonUrl = "postChangeList"/>
    <#assign sortParam = "&changeType="+parameters.changeType?default("")+"&partyIdForSearch="+parameters.partyIdForSearch?default("")+"&partyId="+parameters.partyId?default("")
                        +"&startDate="+parameters.startDate?default("")+"&endDate="+parameters.endDate?default("")/>
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
            <label>工号</label>
        </td>
        <td>
            <label>姓名</label>
        </td>
        <td>
            <label>原部门</label>
        </td>
        <td>
            <label>原岗位</label>
        </td>
        <td>
            <label>新部门</label>
        </td>
        <td>
            <label>新岗位</label>
        </td>
        <td>
            <label>调动类型</label>
        </td>
        <td>
            <label>调动时间</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if postList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list postList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                ${rowCount + 1}
                <#--<input type="hidden" name="partyId" value="${line.partyId?default('')}">-->
            </td>
            <td>
                <label>${line.workerSn?default('')}</label>
            </td>
            <td>
                <label>${line.fullName?default('')}</label>
            </td>
            <td>
                <label>${line.groupName?default('')}</label>
            </td>
            <td>
                <label>${line.description?default('')}</label>
            </td>
            <td>
                <label>${line.groupName1?default('')}</label>
            </td>
            <td>
                <label>${line.description1?default('')}</label>
            </td>
            <td>
                <label>${line.changeTypeName?default('')}</label>
            </td>
            <td>
                <label>${line.changeDate?string('yyyy-MM-dd')}</label>
            </td>
            <td>
                <a href="#nowhere" onclick="javascript:$.recordManagement.editPostChange('${partyId?default('')}','${line.postId}','postChangeList');" title="修改" class="icon-edit"></a>
            </td>
            <td>
                <a href="#nowhere" onclick="javascript:$.recordManagement.removePostChange('${partyId?default('')}','${line.postId}','postChangeList');" title="添加" class="icon-trash"></a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if postList?has_content>
    <@nextPrevAjax targetId="ListTransferRecord_col" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>