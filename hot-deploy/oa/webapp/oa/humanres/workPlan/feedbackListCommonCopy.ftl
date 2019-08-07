 <#macro feedbackList feedbackMiddleId feedbackPerson childFeedbackId value type targetId>
    <#local description=""/>
    <#assign commonUrl = "getFeedback"/>
    <#assign sortField = '-feedbackTime'>
    <#assign sortParam = "feedbackMiddleId=" + feedbackMiddleId?default('')+"&sortField="+sortField+"&feedbackPerson="+feedbackPerson+"&childFeedbackId="+childFeedbackId+"&feedbackMiddleType="+type/>
    <#if value?has_content>
        <#assign viewIndex = value.viewIndex>
        <#assign highIndex = value.highIndex>
        <#assign totalCount = value.totalCount>
        <#assign viewSize = value.viewSize>
        <#assign lowIndex = value.lowIndex>
        <#assign feedbackList = value.returnValue.feedbackList>
        <#assign showChild = value.returnValue.showChild>

        <#assign param = sortParam/>
        <#assign viewIndexFirst = 0/>
        <#assign viewIndexPrevious = viewIndex - 1/>
        <#assign viewIndexNext = viewIndex + 1/>
        <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
        <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
        <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
    <#else>
        <#local returnValue = dispatcher.runSync("getFeedback", Static["org.ofbiz.base.util.UtilMisc"].toMap("feedbackMiddleId",
        feedbackMiddleId,"feedbackPerson",feedbackPerson,"feedbackMiddleType",type,"childFeedbackId",childFeedbackId,"userLogin", userLogin))/>
        <#if returnValue?has_content>
            <#assign viewIndex = returnValue.viewIndex>
            <#assign highIndex = returnValue.highIndex>
            <#assign totalCount = returnValue.totalCount>
            <#assign viewSize = returnValue.viewSize>
            <#assign lowIndex = returnValue.lowIndex>
            <#assign feedbackList = returnValue.returnValue.feedbackList>
            <#assign showChild = returnValue.returnValue.showChild>
            <#assign param = sortParam/>
            <#assign viewIndexFirst = 0/>
            <#assign viewIndexPrevious = viewIndex - 1/>
            <#assign viewIndexNext = viewIndex + 1/>
            <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
            <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
            <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
        </#if>
    </#if>
<script type="text/javascript">
    $(function(){
        $("#deleteFeedback").click(function(){
            var deleteFeedbackIds = new Array();
            var number = 0;
            $('input:checkbox:checked').each(function(){
                deleteFeedbackIds.push($(this).attr("value"));
                number++;
            });
            if(number < 1 ){
                showInfo("请至少选择1个反馈删除！");
                return;
            }
            var deleteFeedbackIdStr = deleteFeedbackIds.join(",");
            $.ajax({
                type: 'POST',
                url: "deleteFeedback",
                async: true,
                dataType: 'json',
                data:{deleteFeedbackId:deleteFeedbackIdStr},
                success: function (data) {
                    showInfo(data.msg);
                    $.feedback.getFeedback('${feedbackMiddleId}', '', '', '${type}', "#feedbackList");
                    $.meetingNotice.searchMeetingSummary();
                }
            });
        });
    });

    function getWeekByDate(str) {
        var strWeek;
        var date = new Date(str);
        var weekOfDay = date.getDay();
        switch (weekOfDay) {
            case 0:
                strWeek = '日';
                break;
            case 1:
                strWeek = "一";
                break;
            case 2:
                strWeek = "二";
                break;
            case 3:
                strWeek = "三";
                break;
            case 4:
                strWeek = "四";
                break;
            case 5:
                strWeek = "五";
                break;
            case 6:
                strWeek = "六";
                break;
        }
        return strWeek;
    }
</script>
<div  id="" style="OVERFLOW-X: scroll; scrollbar-face-color:#B3DDF7;scrollbar-shadow-color:#B3DDF7;scrollbar-highlight-color:#B3DDF7;
        scrollbar-3dlight-color:#EBEBE4;scrollbar-darkshadow-color:#EBEBE4;scrollbar-track-color:#F4F4F0;scrollbar-arrow-color:#000000;
        width:100%;HEIGHT: 380px;border: 1px solid #DDD;height: 200px;padding: 20px">
        <#if feedbackList?has_content>
            <#list feedbackList as feedback>
                <div style="border: 1px solid #DDD">
                    <table>
                        <tr>
                            <td><input type="checkbox" name="deleteFeedbackId" value="${feedback.feedbackId}"></td>
                            <td class="label">反馈时间:</td><td><b style="color: blue">${feedback.feedbackTime?string('yyyy-MM-dd HH:mm:ss')}(星期<b class="feedback_week"></b>)</b></td>
                            <td class="label">反馈人:</td><td><b style="color: blue"><#if feedback.actualFeedbackPerson?has_content>${feedback.actualFeedbackPersonName?default('')}&nbsp;代&nbsp;${feedback.feedbackPersonName?default('')}&nbsp;反馈<#else>${feedback.feedbackPersonName?default('')}</#if></b></td>
                            <td class="label">所在部门:</td><td><b style="color: blue">${feedback.feedbackPersonDeptName?default('')}</b></td>
                        </tr>
                        <tbody>
                            <#if feedback.childWorkPlanId?has_content && showChild?default('') == 'true'>
                            <tr>
                                <td colspan="7">
                                    <table class="basic-table" cellspacing="0" cellpadding="0" align="center"
                                           style="height: 40px">
                                        <tbody>
                                        <tr class="header-row-2">
                                            <td>子计划标题</td>
                                            <td>要求时间</td>
                                            <td>最后反馈</td>
                                            <td width="10">状态</td>
                                        </tr>

                                        <tr onclick="$.workPlan.feedbackWorkPlan(${feedback.workPlanId},${feedback.actualFeedback},${feedback.childWorkPlanId})">
                                            <td>${feedback.title?default('')}</td>
                                            <td>${feedback.completeTime?default('')}</td>
                                            <td>暂无反馈</td>
                                            <td>
                                                <div style="background: red;width: 20px;height: 20px"></div>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>

                            </tr>
                            </#if>
                        <tr>
                            <td colspan="7">
                                <label id="feedbackContext_${feedback_index}"/>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <script type="text/javascript">
                    $(".feedback_week").each(function(){
                        var test = getWeekByDate('${feedback.feedbackTime?string("yyyy-MM-dd")}');
                        $(this).text(test);
                    });
                    $("#feedbackContext_${feedback_index}").html(unescapeHtmlText('${feedback.feedbackContext}'));
                </script>
            </#list>

            <div>
                <span><a class="smallSubmit" id="deleteFeedback">删除选中</a> </span>
            </div>
        <#else >
            <div align="center">
                <span class="h1" style="color: red">暂时无反馈！</span>
            </div>
        </#if>
</div>

    <#if feedbackList?has_content>
        <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
        <@nextPrevAjax targetId=targetId commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
        viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
        paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
        ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
        paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
    </#if>
</#macro>

