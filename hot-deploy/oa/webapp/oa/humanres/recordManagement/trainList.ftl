<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.trainList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "trainList"/>
    <#assign sortParam = "startDate="+data.startDate?default("")+"&endDate="+data.endDate?default("")+"&type="+data.type?default("")+"&name="+data.name?default("")+"&number="+data.number?default("")+"&partyIdForSearch="+data.partyIdForSearch?default("")/>
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
            <label>员工工号</label>
        </td>
        <td>
            <label>员工姓名</label>
        </td>
        <td>
            <label>培训编号</label>
        </td>
        <td>
            <label>培训课程名称</label>
        </td>
        <td>
            <label>培训类型</label>
        </td>
        <td>
            <label>培训日期</label>
        </td>
        <td>
            <label>培训讲师</label>
        </td>
        <td>
            <label>培训地址</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if data.trainList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.trainList as line>
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
                <label>${line.number?default('')}</label>
            </td>
            <td>
                <label>${line.name?default('')}</label>
            </td>
            <td>
                <label>${line.typeName?default('')}</label>
            </td>
            <td>
                <label>${line.date?default('')}</label>
            </td>
            <td>
                <label>${line.teacher?default('')}</label>
            </td>
            <td>
                <label>${line.address?default('')}</label>
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.editTrain('${line.trainId}','${line.partyId}','trainList')" title="修改" class="icon-edit"/>
            </td>
            <td>
                <a href="#" onclick="javascript:$.recordManagement.removeTrain('${line.trainId}','trainList','')" title="删除" class="icon-trash"/>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.trainList?has_content>
    <@nextPrevAjax targetId="staffInformation" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
</html>