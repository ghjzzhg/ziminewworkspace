<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchNotice"/>
    <#assign sortParam = "&department="+data.department?default("")+"&releaseTimeStart="+data.releaseTimeStart?default("")
                        +"&releaseTimeEnd="+data.releaseTimeEnd?default("")+"&title="+data.title?default("")
                        +"&noticeType="+data.noticeType?default("")/>
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
            <label>公文/通知标题</label>
        </td>
        <td>
            <label>类型</label>
        </td>
        <td>
            <label>文档编号</label>
        </td>
        <td>
            <label>发布部门</label>
        </td>
        <td>
            <label>发布日期</label>
        </td>
        <td>
            <label>模板</label>
        </td>
        <td>
            <label>发布人</label>
        </td>
        <td>
            <label>最后编辑人/签收日期</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                <a href="#" class="hyperLinkStyle" onclick="javascript:$.bumphNotice.showBumphNoticeInfo('${line.noticeId}')" title="查看">
                    ${line.title?default('')}
                </a>
            </td>
            <td>
                <label>${line.noticeTypeDesc?default('')}</label>
            </td>
            <td>
                <label>RECTEC${line.noticeYear?default('')}CWB${line.noticeNumber?default('')}</label>
            </td>
            <td>
                <label>${line.groupName?default('')}</label>
            </td>
            <td>
                <label>${line.releaseTime?default('')}</label>
            </td>
            <td>
                <label>${line.noticeTemplateDesc?default('无')}</label>
            </td>
            <td>
                <label>${line.releasePersonName?default('')}</label>
            </td>
            <td>
                <label>${line.lastEditPersonAndDate?default('')}</label>
            </td>
            <td>
                <a href="#" onclick="javascript:$. bumphNotice.addBumphNotice('${line.noticeId}','修改通知')" title="修改" class="icon-edit"></a>
            </td>
            <td>
                <a href="#" onclick="javascript:$.bumphNotice.deleteNotice('${line.noticeId}')" title="删除" class="icon-trash"></a>
            </td>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if data.list?has_content>
    <@nextPrevAjax targetId="BumphNoticeList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>