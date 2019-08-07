<#macro signInRecord noticeId type hasSignInList statusList param=""  targetId="signRecordList">
    <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
    <#assign currentPartyId = param.userLogin.partyId>
    <#assign commonUrl = "findRecordList"/>
    <#assign sortField = ''>
    <#assign sortParam = "&noticeId="+noticeId+"&type="+type/>
    <#if param.returnValue?has_content && param.viewIndex?has_content && param.viewSize?has_content>
        <#assign viewIndex = param.viewIndex>
        <#assign highIndex = param.highIndex>
        <#assign totalCount = param.totalCount>
        <#assign viewSize = param.viewSize>
        <#assign lowIndex = param.lowIndex>
        <#assign signInRecordList = param.returnValue>
        <#assign param = sortParam/>
        <#assign viewIndexFirst = 0/>
        <#assign viewIndexPrevious = viewIndex - 1/>
        <#assign viewIndexNext = viewIndex + 1/>
        <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
        <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
        <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
    <#elseif noticeId?has_content && type?has_content>
        <#local returnValue = dispatcher.runSync("findRecord", Static["org.ofbiz.base.util.UtilMisc"].toMap("noticeId",
        noticeId,"type",type,"userLogin", userLogin))/>
        <#if returnValue?has_content>
            <#assign viewIndex = returnValue.viewIndex>
            <#assign highIndex = returnValue.highIndex>
            <#assign totalCount = returnValue.totalCount>
            <#assign viewSize = returnValue.viewSize>
            <#assign lowIndex = returnValue.lowIndex>
            <#assign signInRecordList = returnValue.returnValue>
            <#assign param = sortParam/>
            <#assign viewIndexFirst = 0/>
            <#assign viewIndexPrevious = viewIndex - 1/>
            <#assign viewIndexNext = viewIndex + 1/>
            <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
            <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
            <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
        </#if>
    </#if>
<script language="javascript">
    function confirmButton(id,noticeId,staffId,signInStatus,status,remark) {
        $.ajax({
            type: 'POST',
            url: "updateSignInRecord",
            async: true,
            dataType: 'json',
            data:{singnInPersonId:id,noticeId:noticeId,staffId:staffId,signInStatus:signInStatus,signInPersonStatus:status,remark:remark},
            success: function (content) {
                $.ajax({
                    type: 'POST',
                    url: "findRecordList",
                    async: true,
                    dataType: 'html',
                    data:{noticeId:'${noticeId}',VIEW_INDEX:'${viewIndex}',VIEW_SIZE:'${viewSize}',type:'${type}'},
                    success: function (data) {
                        $("#signRecordList").html(data);
                    }
                });
                console.log(content);
                showInfo(content.msg);
            }
        });
    }
</script>
<div style="margin-top: 20px">
    <div>
        <b style="color: blue"><b style="color: red">注意：</b>本会议通知需要以下员工签收确认，请列表中员工在下面直接签收确认：</b>
    </div>
    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
        <tbody>
        <tr class="header-row-2">
            <td>签收者部门</td>
            <td>姓名</td>
            <td>岗位</td>
            <td>签收时间</td>
            <td>签收否</td>
            <td>当前状态</td>
            <td>备 注</td>
            <td>签收状态</td>
        </tr>
            <#if signInRecordList?has_content>
                <#assign item=0>
                <#list signInRecordList as sipList>
                <#--<#if sipList.staffId == currentPartyId>-->
                <tr>
                    <td>${sipList.departmentName}</td>
                    <td>${sipList.fullName}</td>
                    <td>${sipList.positionDesc?default('')}</td>
                    <td><#if sipList.signInTime?has_content>${sipList.signInTime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
                    <td>
                        <#if sipList.hasSignIn!="NS_Y">
                            <select name="signInStatus" id="signInStatus${item}">
                                <#list hasSignInList as hasSignIn>
                                    <option value="${hasSignIn.enumId}">${hasSignIn.description}</option>
                                </#list>
                            </select>
                        <#else >
                            <#assign hasSignInEnum  = delegator.findOne("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId",sipList.hasSignIn), false)/>
                        ${hasSignInEnum.description}
                        </#if>
                    </td>
                    <td>
                        <#if sipList.signInPersonStatus!="NS_UNDERSTAND">
                            <select name="signInPersonStatus" id="status${item}">
                                <#list statusList as status>
                                    <#if status.enumId==sipList.signInPersonStatus>
                                        <option value="${status.enumId}" selected>${status.description}</option>
                                    <#else >
                                        <option value="${status.enumId}">${status.description}</option>
                                    </#if>
                                </#list>
                            </select>
                        <#else >
                            <#assign statusEnum  = delegator.findOne("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId",sipList.signInPersonStatus), false)/>
                        ${statusEnum.description}
                        </#if>
                    </td>
                    <td><input type="text" name="remark" id="remark${item}" value="${sipList.remark?default('')}"></td>
                    <td>
                        <#if currentPartyId == sipList.staffId >
                            <#if sipList.signInPersonStatus!="NS_UNDERSTAND">
                                <a href="#" class="smallSubmit" name="submit_${item}"
                                   onclick="confirmButton('${sipList.singnInPersonId}','${sipList.noticeId}','${sipList.staffId}',$('#signInStatus'+${item}).val(),$('#status'+${item}).val(),$('#remark'+${item}).val())">确认</a>
                            </#if>
                        <#else>
                            <#assign hasSignInEnum  = delegator.findOne("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId",sipList.hasSignIn), false)/>
                            ${hasSignInEnum.description}
                        </#if>
                    </td>
                </tr>
                    <#assign item = item+1>
                    <#--</#if>-->
                </#list>
            </#if>
        </tbody>
    </table>
</div>
<div>
    <#if signInRecordList?has_content>
        <@nextPrevAjax targetId=targetId commonUrl=commonUrl param=sortParam paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
    </#if>
</div>
</#macro>