<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchProposalList"/>
    <#assign sortParam = "&proposalType="+data.proposalType?default("")+"&submitDateStart="+data.submitDateStart?default("")
    +"&submitDateEnd="+data.submitDateEnd?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<form method="post" action="" id="listProposal" class="basic-form" name="listProposal">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr class="header-row-2">
            <td>
                <label>编号</label>
            </td>
            <td>
                <label>提案标题</label>
            </td>
            <td>
                <label>提案类别</label>
            </td>
            <td>
                <label>提交人</label>
            </td>
            <td>
                <label>现状描述</label>
            </td>
            <td>
                <label>预期效果</label>
            </td>
            <td>
                <label>跟进部门</label>
            </td>
            <td>
                <label>最后编辑人/日期</label>
            </td>
            <td>
                <label></label>
            </td>
            <td>
                <label></label>
            </td>
        </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
            <tr id="ss">
                <td>
                    <label>${line.proposalNumber?default('')}</label>
                </td>
                <td>
                    <label>${line.proposalTitle?default('')}</label>
                </td>
                <td>
                    <label>${line.proposalTypeName?default('')}</label>
                </td>
                <td>
                    <label>${line.submitPersonName?default('')}</label>
                </td>
                <td>
                    <label>${line.nowDescript?default('')}</label>
                </td>
                <td>
                    <label>${line.futureEffect?default('')}</label>
                </td>
                <td>
                    <label>${line.departmentName?default('')}</label>
                </td>
                <td>
                    <label>${line.lastEditPersonName?default("")}/${line.lastUpdatedStamp?default('')}</label>
                </td>
                <td>
                    <a href="#" onclick="$.proposal.editProposal(${line.proposalId?default('')})" class="icon-edit" title="更新"></a>
                </td>
                <td>
                    <a href="#" onclick="$.proposal.deleteProposal(${line.proposalId?default('')})" class="icon-trash" title="删除"></a>
                </td>
            </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
        </tbody>
    </table>
</form>
<#if data.list?has_content>
    <@nextPrevAjax targetId="ListProposal" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>


