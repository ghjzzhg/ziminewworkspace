<#include "component://oa/webapp/oa/humanres/workPlan/feedbackListCommonCopy.ftl"/>
<#include "component://oa/webapp/oa/humanres/meetingNotice/signInRecord.ftl"/>
<#if parameters.returnValue?has_content>
    <#assign returnValue = parameters.returnValue>
    <#assign meetingNoticeId = parameters.meetingNoticeId?default('')>
    <#assign meetingNoticeNumber = returnValue.meetingNoticeNumber?default('')>
    <#assign releaseGroupName = returnValue.groupName?default('')>
    <#assign releaseTime = returnValue.releaseTime?default('')>
    <#assign meetingName = returnValue.meetingName?default('')>
    <#assign meetingStartTime = returnValue.meetingStartTime?default('')>
    <#assign meetingEndTime = returnValue.meetingEndTime?default('')>
    <#assign meetingPlace = returnValue.meetingPlace?default('')>
    <#assign presenter = returnValue.presenter?default('')>
    <#assign hasSignIn = returnValue.hasSignIn?default('')>
    <#assign participantList = delegator.findByAnd("SignInPersonInfo",{"noticeId":meetingNoticeId?default(''),"signInPersonType":"TblMeetingNotice"})>
    <#assign participants = "">
    <#if participantList?has_content>
        <#list participantList as list>
            <#assign participants = participants + " " + list.fullName>
        </#list>
        <#assign participants = participants?trim>
    </#if>
    <#assign scopeData = dispatcher.runSync("getDataScope", Static["org.ofbiz.base.util.UtilMisc"].toMap("entityName", "TblMeetingNotice", "dataId", meetingNoticeId, "dataAttr", "", "userLogin", userLogin))/>
    <#if scopeData?has_content>
        <#assign description=scopeData.description?default("")/>
    </#if>
    <#assign content = returnValue.content?default('')>
<#else >
    <#assign returnValue = ''>
    <#assign meetingNoticeId = ''>
    <#assign releaseGroupName = ''>
    <#assign releaseTime = ''>
    <#assign meetingName = ''>
    <#assign meetingStartTime = ''>
    <#assign meetingEndTime = ''>
    <#assign meetingPlace = ''>
    <#assign presenter = ''>
    <#assign hasSignIn = ''>
    <#assign participants = " ">
    <#assign scope = " ">
    <#assign content = ''>
</#if>
<script language="javascript">
    var template;
    $(function() {
        template = KindEditor.create('textarea[name="meetingNoticeFeedback"]', {
            allowFileManager : true
        });
        $("#meetingNoticeContent").html(unescapeHtmlText('${content}'));
        $("#meeting_notice_feedback_submit").click(function(){
            if(template.html() == null || template.html() == ""){
                showInfo("请填写反馈后再提交！");
                return;
            }
            $.ajax({
                type: 'POST',
                url: "saveMeetingNoticeFeedback",
                async: true,
                dataType: 'json',
                data:{meetingNoticeId:'${meetingNoticeId}',feedbackContext1:template.html()},
                success: function (data) {
                    $.feedback.getFeedback('${meetingNoticeId?default('')}', '', '', 'TblMeetingNotice', "#feedbackList");
                    KindEditor.remove('textarea[name="meetingNoticeFeedback"]');
                    $('textarea[name="meetingNoticeFeedback"]').val("");
                    template = KindEditor.create('textarea[name="meetingNoticeFeedback"]', {
                        allowFileManager: true
                    });
                    showInfo(data.msg);
                    $.meetingNotice.searchMeetingSummary();
                }
            });
        });
    });

</script>
<div>
    <form name="" id="" style="margin-left: 20px;margin-right: 20px;margin-bottom: 20px" class="basic-form">
        <div id="meetingSummary_context">
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                会 议 通 知
            </div>
            <div>
                <div >
                    <div>
                        <div style="float: left"><b>发布部门：<span style="color:blue;">${releaseGroupName}</span></b></div>
                        <div style="float: right">
                            <b>会议编号：<span style="color:blue;">${meetingNoticeNumber?default('')}</span></b><br><b>通知日期：<span style="color:blue;">${releaseTime}</span></b>
                        </div>
                    </div>
                    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tr>
                            <td class="label">会议名称</td>
                            <td colspan="3">${meetingName}</td>
                        </tr>
                        <tr>
                            <td class="label">会议时间</td>
                            <td colspan="3">${meetingStartTime}~${meetingEndTime}</td>
                        </tr>
                        <tr>
                            <td class="label">会议地点</td>
                            <td>${meetingPlace}</td>

                            <td class="label">主 持 人</td>
                            <td>${presenter}</td>
                        </tr>
                        <tr>
                            <td class="label">参加人员</td>
                            <td colspan="3">${participants}</td>
                        </tr>
                        <tr>
                            <td class="label">发布范围</td>
                            <td colspan="3">
                            <#if description?has_content>
                                <#list description as desc>
                                    <span>${desc.name}
                                        <#if desc.like?has_content>
                                            <i style="color: lightskyblue">${desc.like}</i>
                                        </#if>
                                                </span>
                                </#list>
                            <#else>
                                全体员工
                            </#if>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">会议主要讨论事宜</td>
                            <td colspan="3">
                                <label id="meetingNoticeContent"></label>
                            </td>
                        </tr>
                    </table>
                    <div >
                        <div align="center">
                            <div align="center" style="margin-top: 20px">
                                <a href="#" class="smallSubmit">打印纪要</a>
                                <a href="#" class="smallSubmit">浏览日志</a>
                                <a href="#" class="smallSubmit">导出word</a>
                            </div>
                        </div>
                    </div>
                    <div style="margin-top: 20px;">
                        <div class="screenlet-title-bar">
                            <span style="color: black">会议通知中责任人(部门)跟进结果反馈</span>
                        </div>
                        <div id="feedbackList">
                        <@feedbackList  feedbackMiddleId='${meetingNoticeId}' feedbackPerson='' childFeedbackId="" type='TblMeetingNotice' value='' targetId='feedbackList'></@feedbackList>
                        </div>
                    </div>
                    <div style="margin-top: 20px" id="signRecordList">
                        <#if hasSignIn?has_content&&hasSignIn=="Y">
                            <@signInRecord noticeId='${meetingNoticeId}' type='TblMeetingNotice' param=parameters hasSignInList=hasSignInList statusList=statusList></@signInRecord>
                        </#if>
                    </div>

                    <div style="margin-top: 20px;" align="center">
                        <b>相关责任人(部门)<b style="color: blue">录入</b>会议纪要跟进结果</b><br>
                        <span style="color:green;font-size: 8px">(由会议纪要中安排的相关人员在完成任务跟进后填写)</span>
                    </div>
                    <div>
                        <textarea name="meetingNoticeFeedback" id="meetingNoticeFeedback"></textarea>
                    </div>
                </div>
            </div>
        </div>
        <div align="center" style="margin-top: 20px">
            <a href="#" class="smallSubmit" id="meeting_notice_feedback_submit">提交反馈</a>
        </div>
    </form>
</div>
