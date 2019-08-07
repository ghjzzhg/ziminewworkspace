<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.fileDataList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign commonUrl = "searchFileData"/>
    <#assign sortParam = "parentTypeName="+data.parentTypeName?default("")+"&sonTypeName="+data.sonTypeName?default("")+"&documentStatus="+data.documentStatus?default("")+"&documentNumber="+data.documentNumber?default("")+"&documentTitle="+data.documentTitle?default("")+"&releaseDate="+data.releaseDate?default("")/>
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
            <label>文档类别</label>
        </td>
        <td>
            <label>文档子类别</label>
        </td>
        <td>
            <label>文档标题</label>
        </td>
        <td>
            <label>发布部门</label>
        </td>
        <td>
            <label>发布人</label>
        </td>
        <td>
            <label>发布日期</label>
        </td>
        <td>
            <label>状态</label>
        </td>
        <td>
            <label>审核</label>
        </td>
        <td>
            <label>编辑</label>
        </td>
        <td>
            <label>查看历史版本</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if data.fileDataList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.fileDataList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
            ${rowCount + 1}
            </td>
            <td>
                <label>${line.parentTypeName?default('')}</label>
            </td>
            <td>
                <label>${line.sonTypeName?default('')}</label>
            </td>
            <td>
                <a href="#" class="hyperLinkStyle" onclick="$.FileData.showFileData('${line.fileDataId}','查看档案')">
                ${line.documentTitle?default('')}
                </a>
                <label></label>
            </td>
            <td>
                <label>${line.releaseDepartment?default('')}</label>
            </td>
            <td>
                <label>${line.issuer?default('')}</label>
            </td>
            <td>
                <label>${line.releaseDate?default('')}</label>
            </td>
            <td>
                <label>${line.status?default('')}</label>
            </td>
            <td>
            <#if line.status.equals("待审核")>
                <#if line.audit?has_content>
                    <a href="#" class="smallSubmit" onclick="$.FileData.auditFileData('${line.fileDataId}','审核档案')">
                        审核
                    </a>
                <#else>
                    <label>审核</label>
                </#if>
            <#else>
                <label></label>
            </#if>

            </td>
            <td>
            <#if line.status.equals("审核通过")>
                <#if line.edit?has_content>
                    <a href="#" onclick="javascript:$.FileData.modifyFileData('${line.fileDataId}','修订')" title="修订" class="icon-edit"/>
                <#else>
                    <label></label>
                </#if>
            <#else>
                <#if line.edit?has_content>
                    <a href="#" onclick="javascript:$.FileData.modifyFileData('${line.fileDataId}','修改档案')" title="修改" class="icon-edit"/>
                <#else>
                    <label></label>
                </#if>
            </#if>
            </td>
            <td>
                <a href="#" class="hyperLinkStyle" onclick="$.FileData.showFileDataVersion('${line.fileDataId?default('')}','${line.oldFileDataId?default('')}')">历史版本</a>
            </td>
            <td>
            <#if line.status.equals("审核通过")>
                <label></label>
            <#else>
                <#if line.edit?has_content>
                    <a href="#" onclick="javascript:$.FileData.deleteFileData('${line.fileDataId}')" title="删除" class="icon-trash"/>
                <#else>
                    <label></label>
                </#if>
            </#if>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.fileDataList?has_content>
    <@nextPrevAjax targetId="fileDataList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
<#--<@showFileList id="abc" hiddenId="fileListH"/>-->
</html>