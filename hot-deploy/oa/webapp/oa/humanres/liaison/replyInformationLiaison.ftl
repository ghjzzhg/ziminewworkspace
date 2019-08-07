<#macro ReplyInformationList value contactListId targetId = "ReplyInformationList_div">
    <#local description=""/>
    <#assign commonUrl = "findWorkSheetReplyInformation"/>
    <#--<#assign sortField = ''>-->
    <#assign sortParam = "contactListId=" + contactListId?default('')/>
    <#if value?has_content && value.viewIndex?has_content>
        <#assign viewIndex = value.viewIndex>
        <#assign highIndex = value.highIndex>
        <#assign totalCount = value.totalCount>
        <#assign viewSize = value.viewSize>
        <#assign lowIndex = value.lowIndex>
        <#assign replyInformationList = value.data>
        <#assign param = sortParam/>
        <#assign viewIndexFirst = 0/>
        <#assign viewIndexPrevious = viewIndex - 1/>
        <#assign viewIndexNext = viewIndex + 1/>
        <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
        <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
        <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
    <#else >
        <#local returnValue = dispatcher.runSync("findWorkSheetReplyInformation",{"contactListId":contactListId,"userLogin":userLogin})>
        <#if returnValue?has_content && returnValue.data?has_content>
            <#assign viewIndex = returnValue.viewIndex>
            <#assign highIndex = returnValue.highIndex>
            <#assign totalCount = returnValue.totalCount>
            <#assign viewSize = returnValue.viewSize>
            <#assign lowIndex = returnValue.lowIndex>
            <#assign replyInformationList = returnValue.data>
            <#assign param = sortParam/>
            <#assign viewIndexFirst = 0/>
            <#assign viewIndexPrevious = viewIndex - 1/>
            <#assign viewIndexNext = viewIndex + 1/>
            <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
            <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
            <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
        </#if>
    </#if>


<div style="OVERFLOW-X: scroll; scrollbar-face-color:#B3DDF7;scrollbar-shadow-color:#B3DDF7;scrollbar-highlight-color:#B3DDF7;
        scrollbar-3dlight-color:#EBEBE4;scrollbar-darkshadow-color:#EBEBE4;scrollbar-track-color:#F4F4F0;scrollbar-arrow-color:#000000;
        width:100%;HEIGHT: 500px;border: 1px solid #DDD;height: 200px;padding: 20px">
    <input type="hidden" id="ReplyInformationList_msg" value="${msg?default('')}">
    <form id="deleteFeedbackForm" name="deleteFeedbackForm" class="basic-form">
        <#if replyInformationList?has_content>
            <#list replyInformationList as list>
                <div style="border: 1px solid #DDD">
                    <table>
                        <tr>
                            <td class="label">回复部门:</td>
                            <td>
                                <b style="color: blue">${list.departmentName?default('')}</b>
                            </td>
                            <td class="label">回复者:</td>
                            <td>
                                <#assign person = delegator.findOne("Person",{"partyId":list.fullName},false)>
                                <b style="color: blue">${person.fullName?default('')}</b>
                            </td>
                            <td class="label">回复时间:</td>
                            <td>
                                <b style="color: blue">${list.replyTime}</b>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="7" id="replyContent_${list.workSheetReplyInforId}"></td>
                            <script>
                                $("#replyContent_${list.workSheetReplyInforId}").append(unescapeHtmlText('${list.replyContent?default('')}'));
                            </script>
                        </tr>
                        </tbody>
                    </table>
                </div>

            </#list>
        <#else >
            <div align="center">
                <span class="h1" style="color: red">暂时回复意见！</span>
            </div>
        </#if>
    </form>
</div>
<#if replyInformationList?has_content>
    <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
    <@nextPrevAjax targetId=targetId commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>


</#macro>

