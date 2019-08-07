<script language="javascript">
    function delDataResourceRole(dataResourceId,partyId,fileFlag){
        if(confirm("是否确认删除？")){
            $.ajax({
                type: 'post',
                url: "delDataResourceRole",
                data:{dataResourceId:dataResourceId,partyId:partyId,fileFlag:fileFlag},
                async: true,
                dataType: 'json',
                success: function (data) {
                    showInfo(data.msg);
                    showFileShareInfo(dataResourceId);
                }
            });
        }
    }

    function showFileShareInfo(dataResourceId){
        $.ajax({
            type: 'post',
            url: "showShareInfo",
            async: true,
            dataType: 'html',
            data:{fileId:dataResourceId,fileFlag:'${fileFlag?default('')}'},
            success: function (content) {
                $("#dataResourceList").replaceWith(content);
            }
        });
    }
</script>
<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if dataResourceMap.dataResourceList?has_content>
    <#assign viewIndex = dataResourceMap.viewIndex>
    <#assign highIndex = dataResourceMap.highIndex>
    <#assign totalCount = dataResourceMap.totalCount>
    <#assign viewSize = dataResourceMap.viewSize>
    <#assign commonUrl = "showShareInfo"/>
    <#assign sortParam = ""/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<table cellspacing="0" class="basic-table hover-bar table table-hover table-striped table-bordered">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>序号</label>
        </td>
        <td>
            <label>分享人</label>
        </td>
        <td>
            <label>分享时间</label>
        </td>
        <td>
            <label>删除</label>
        </td>
    </tr>
    <#if dataResourceMap.dataResourceList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list dataResourceMap.dataResourceList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td align="center">
            ${rowCount + 1}
            </td>
            <td align="center">
                ${line.partyName?default("")}
            </td>
            <td align="center">
                ${line.fromDate?default("")}
            </td>
            <td align="center">
                <a href="#" onclick="javascript:delDataResourceRole('${line.fileId?default("")}','${line.partyId?default("")}','${parameters.fileFlag}')" title="删除" class="icon-trash"></a>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
        <#--toggle the row color-->
            <#assign alt_row = !alt_row>
        </#list>
    <#else>
        <tr>
            <td colspan="5" style="text-align: center">
                无分享记录
            </td>
        </tr>
    </#if>
    </tbody>
</table>
<#if dataResourceMap.dataResourceList?has_content>
    <@nextPrevAjax targetId="dataResourceList" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>

