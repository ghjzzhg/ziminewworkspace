<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.workReportList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign commonUrl = "searchWorkReportList"/>
    <#assign sortParam = "num="+data.num?default("")+"&name="+data.name?default("")+"&party="+data.party?default("")+"&status="+data.status?default("")+"&type="+data.type?default("")+"&process="+data.process?default("")+"&executor="+data.executor?default("")+"&leader="+data.leader?default("")/>
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
            <label>编号</label>
        </td>
        <td>
            <label>报告类型</label>
        </td>
        <td>
            <label>报告名称</label>
        </td>
        <td>
            <label>简要要求</label>
        </td>
        <td>
            <label>录入人</label>
        </td>
        <td>
            <label>有效期</label>
        </td>
        <td>
            <label>发生频率</label>
        </td>
        <td>
            <label>报告状态</label>
        </td>
        <td>
            <label>当前进度</label>
        </td>
        <td>
            <label>最后反馈人/时间</label>
        </td>
        <td>
            <label>提交报告</label>
        </td>
        <td>
            <label>编辑</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if data.workReportList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.workReportList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
            ${rowCount + 1}
            </td>
            <td>
                <a href="#" class="hyperLinkStyle" onclick="javascript:$.workPlan.browseReport(${line.workReportId})">${line.reportNumber?default('')}</a>
                <label></label>
            </td>
            <td>
                <label>${line.typeName?default('')}</label>
            </td>
            <td>
                <label>${line.reportTitle?default('')}</label>
            </td>
            <td>
                <label>${line.request?default('')}</label>
            </td>
            <td>
                <label>${line.fullName?default('')}</label>
            </td>
            <td width="15%">
                <label>${line.startDate?default('')}</label>
                <br/>
                <label>~</label>
                <br/>
                <label>${line.endDate?default('')}</label>
            </td>
            <td>
                <label>${line.frequencyName?default('')}</label>
            </td>
            <td>
                <label>${line.statusName?default('')}</label>
            </td>
            <td>
                <label>未提交${line.noSubmit?default('0')}</label>
                <label>已提交${line.submit?default('0')}</label>
            </td>
            <td>
                <label>${line.feedbackInfo?default('')}</label>
            </td>
            <td class="function-column">
            <#if line.flag==1>
                <a href="#" onclick="javascript:$.workPlan.commitReport(${line.workReportId})" title="提交报告" class="smallSubmit">提交报告</a>
            </#if>
                <#if line.flag==0>
                    已过期
                </#if>
                <#if line.flag==2>
                    未开始
                </#if>
            </td>
            <td>
                <a href="#" onclick="javascript:$.workPlan.addWorkReport('修改报告',${line.workReportId})" title="修改" class="icon-edit"/>
            </td>
            <td>
                <a href="#" onclick="javascript:$.workPlan.delWorkReport(${line.workReportId})" title="删除" class="icon-trash"/>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
         <#--toggle the row color-->
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.workReportList?has_content>
    <@nextPrevAjax targetId="workReportList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
<#--<@showFileList id="abc" hiddenId="fileListH"/>-->
</html>
