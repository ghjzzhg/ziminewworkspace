<#macro feedbackList workPlanId partyId childWorkPlanId value targetId='feedbackList'>
    <#local description=""/>
    <#assign commonUrl = "findFeedback"/>
    <#assign sortField = 'feedbackTime'>
    <#assign sortParam = "workPlanId=" + workPlanId?default('')+"&sortField="+sortField+"&partyId="+partyId+"&childWorkPlanId="+childWorkPlanId/>
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
        <#local returnValue = dispatcher.runSync("findFeedback", Static["org.ofbiz.base.util.UtilMisc"].toMap("workPlanId",
        workPlanId,"partyId",partyId,"childWorkPlanId",childWorkPlanId,"userLogin", userLogin))/>
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
            var options = {
                beforeSubmit:function () {
                    return true;
                },
                dataType:"json",
                data:{deleteFeedbackId:deleteFeedbackIdStr},
                success:function (data) {
                    showInfo(data.msg);
                    $.feedback.findFeedback('${workPlanId}', '${childWorkPlanId}', '${partyId}', '#feedbackList');
                },
                url:"deleteFeedback",
                type:'post'
            };
            $("#deleteFeedbackForm").ajaxSubmit(options);
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
<#include "component://oa/webapp/oa/humanres/workPlan/foldersHeader.ftl"/>
<div style="OVERFLOW-X: scroll; scrollbar-face-color:#B3DDF7;scrollbar-shadow-color:#B3DDF7;scrollbar-highlight-color:#B3DDF7;
        scrollbar-3dlight-color:#EBEBE4;scrollbar-darkshadow-color:#EBEBE4;scrollbar-track-color:#F4F4F0;scrollbar-arrow-color:#000000;
        width:100%;HEIGHT: 500px;border: 1px solid #DDD;height: 200px;padding: 20px">
    <form id="deleteFeedbackForm" name="deleteFeedbackForm" class="basic-form">
        <#if feedbackList?has_content>
            <#list feedbackList as feedback>
                <div style="border: 1px solid #DDD">
                    <table>
                        <tr>
                            <td><input type="checkbox" name="deletefeedbackId" value="${feedback.feedbackId}" form="deleteFeedbackForm"></td>
                            <td class="label">反馈时间:</td><td><b style="color: blue">${feedback.feedbackTime?string('yyyy-MM-dd HH:mm:ss')}(星期<b class="feedback_week"></b>)</b></td>
                            <td class="label">反馈人:</td><td><b style="color: blue"><#if feedback.actualFeedbackPerson?has_content>${feedback.actualFeedbackPersonName?default('')}&nbsp;代&nbsp;${feedback.feedbackPersonName?default('')}&nbsp;反馈<#else>${feedback.feedbackPersonName?default('')}</#if></b></td>
                            <td class="label">所在部门</td><td><b style="color: blue">${feedback.actualFeedbackPersonDeptName?default('')}</b></td>
                        </tr>
                        <tbody>
                            <#if feedback.childWorkPlanId?has_content && showChild?default('') == 'true'>
                            <tr>
                                <td colspan="7">
                                    <table class="basic-table" cellspacing="0" cellpadding="0" align="center"
                                           style="height: 40px">
                                        <tbody>
                                        <tr class="header-row-2">
                                            <td width="25%">子计划标题</td>
                                            <td width="25%">要求时间</td>
                                            <td width="20%">最后反馈</td>
                                            <td width="10%">状态</td>
                                        </tr>
                                        <tr>
                                            <#assign lastFeedback = delegator.findByAnd("ChildWorkPlanInfo",{"lastFeedback":feedback.lastFeedback?default('')})>
                                            <td>${feedback.title?default('')}</td>
                                            <td>${feedback.startTime?string("yyyy-MM-dd HH:mm")}~${feedback.completeTime?string("yyyy-MM-dd HH:mm")}</td>
                                            <td><#if lastFeedback?has_content>${lastFeedback[0].feedbackPersonName?default('')}(${lastFeedback[0].feedbackTime?string('yyyy-MM-dd HH:mm')})<#else>暂无反馈</#if></td>
                                            <td>
                                                <@WorkPlanProgressbar id="feedbackChildWorkPlanProgressbar_${feedback.feedbackId?default('')}" value=feedback.childWorkPlanStatus?default(0) />
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>

                            </tr>
                            </#if>
                        <tr>
                            <td colspan="7" id="feedbackListCommon_feedbackContext_${feedback.feedbackId}"></td>
                            <script type="text/javascript">
                                $("#feedbackListCommon_feedbackContext_"+${feedback.feedbackId}).html(unescapeHtmlText('${feedback.feedbackContext}'));
                            </script>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <script type="text/javascript">
                    $(".feedback_week").each(function(){
                        var test = getWeekByDate('${feedback.feedbackTime?string("yyyy-MM-dd")}');
                        $(this).text(test);
                    });
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
    </form>
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

