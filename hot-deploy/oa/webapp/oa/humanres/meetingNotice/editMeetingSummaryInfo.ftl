<#include "component://oa/webapp/oa/humanres/workPlan/feedbackListCommonCopy.ftl"/>
<#include "component://oa/webapp/oa/humanres/meetingNotice/signInRecord.ftl"/>
<#include "component://oa/webapp/oa/humanres/meetingNotice/MeetingToPersonWorkList.ftl"/>
<#assign summaryId = parameters.get("summaryId")>
<#if summaryId?has_content>
    <#assign meetingSummaryList = delegator.findByAnd("MeetingSummaryNotice",{"summaryId":summaryId})>
    <#if meetingSummaryList?has_content>
        <#assign meetingSummary = meetingSummaryList.get(0)>
        <#assign releaseDepartment = meetingSummary.releaseDepartment?default('')>
        <#if releaseDepartment?has_content>
            <#assign  reDepartmentName = delegator.findOne("PartyGroup",{"partyId":releaseDepartment?default('')},false)>
            <#if releaseDepartment?has_content>
                <#assign reGroupName = reDepartmentName.groupName?default('')>
            </#if>
        </#if>
        <#assign meetingNoticeNumber = meetingSummary.meetingNoticeNumber?default('')>
        <#assign releaseTime = meetingSummary.releaseTime?default('')>
        <#assign meetingName = meetingSummary.meetingName?default('')>
        <#assign meetingStartTime = valueMap.meetingStartTime?default('')>
        <#assign meetingEndTime = valueMap.meetingEndTime?default('')>
        <#assign meetingPlace = meetingSummary.meetingPlace?default('')>
        <#assign participantList = delegator.findByAnd("SignInPersonInfo",{"noticeId":summaryId?default(''),"signInPersonType":"TblMeetingNotice"})>
        <#assign participants = "">
        <#if participantList?has_content>
            <#list participantList as list>
                <#assign participants = participants + " " + list.fullName>
            </#list>
            <#assign participants = participants?trim>
        </#if>
        <#assign absentPerson = "">
        <#if absentPersonList?has_content>
            <#list absentPersonList as list1>
                <#assign absentPerson = absentPerson + " " + list1.fullName>
            </#list>
            <#assign absentPerson = absentPerson?trim>
        <#else>
            <#assign absentPerson = "无">
        </#if>
        <#assign latePerson = "">
        <#if latePersonList?has_content>
            <#list latePersonList as list2>
                <#assign latePerson = latePerson + " " + list2.fullName>
            </#list>
            <#assign latePerson = latePerson?trim>
        <#else>
            <#assign latePerson = "无">
        </#if>
        <#assign recordPerson = meetingSummary.recordPerson?default('')>
        <#assign presenter = meetingSummary.presenter?default('')>
        <#assign meetingTheme = meetingSummary.meetingTheme?default('')>
        <#assign summaryId = meetingSummary.summaryId?default('')>
        <#assign summaryContent = meetingSummary.summaryContent?default('')>
        <#assign signPerson = meetingSummary.signPerson?default('')>
        <#assign releasePerson = meetingSummary.releasePerson?default('')>
        <#assign releaseSummaryTime = valueMap.releaseSummaryTime?default('')>
        <#assign hasSignIn = meetingSummary.hasSignIn?default('')>
        <#assign workPlanId = meetingSummary.workPlanId?default('')>
    </#if>
    <#assign scopeData = dispatcher.runSync("getDataScope", Static["org.ofbiz.base.util.UtilMisc"].toMap("entityName", "TblMeetingNotice", "dataId", summaryId, "dataAttr", "", "userLogin", userLogin))/>
    <#if scopeData?has_content>
        <#assign description=scopeData.description?default("")/>
    </#if>
</#if>
<script language="javascript">
    var template;
    $(function() {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager : true
        });
        $("#summaryContentHtml").html(unescapeHtmlText('${summaryContent}'));

        $("textarea[name='checkPerson']").click(function(){
            displayInTab3("AddTransactionTab", "与会人员", {requestUrl: "checkPerson",data:{param:"person"},width: "600px", position:'top'});
        });
        $("textarea[name='range']").click(function(){
            displayInTab3("AddTransactionTab", "设置浏览权限", {requestUrl: "checkPerson",data:{param:"range"},width: "600px", position:'top'});
        });

        $("#follow_up").css("display","none");
        $("#meeting_summary_feedback").click(function(){
            if(template.html() == null || template.html() == ""){
                showInfo("请填写反馈后再提交！");
                return;
            }
            $.ajax({
                type: 'POST',
                url: "saveMeetingNoticeFeedback",
                async: true,
                dataType: 'json',
                data:{meetingNoticeId:'${summaryId?default('')}',feedbackContext1:template.html()},
                success: function (data) {
                    $.feedback.getFeedback('${summaryId?default('')}', '', '', 'TblMeetingNotice', "#feedbackList");
                    KindEditor.remove('textarea[name="template"]');
                    $('textarea[name="template"]').val("");
                    template = KindEditor.create('textarea[name="template"]', {
                        allowFileManager: true
                    });
                    showInfo(data.msg);
                    $.meetingNotice.searchMeetingSummary();
                }
            });
        });
    });
    function displayFollowUp(obj){
        if(obj.attr("checked")=='checked'){
            $("#follow_up").css("display"," ");
        }else{
            $("#follow_up").css("display","none");
        }
    }
</script>
<div>
    <form name="" id="" class="basic-form">
        <div id="meetingSummary_context">
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                会 议 纪 要
            </div>
            <div>
                <div >
                    <div>
                        <div style="float: left"><b>发布部门：<span style="color:blue;">${reGroupName?default('')}</span></b></div>
                        <div style="float: right">
                            <b>编号：<span style="color:blue;">${meetingNoticeNumber?default('')}</span></b><br><b>日期：<span style="color:blue;">${releaseTime?default('')}</span></b>
                        </div>
                    </div>
                    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tbody>
                        <tr>
                            <td colspan="6">
                                <table cellpadding="0" cellspacing="0" border="1" width="100%"
                                       style="border-collapse: collapse">
                                    <tr>
                                        <td width="15%"></td>
                                        <td></td>
                                        <td width="15%"></td>
                                        <td></td>
                                    </tr>
                                    <tr>
                                        <td><b>会议名称</b></td>
                                        <td colspan="3">${meetingName?default('')}</td>
                                    </tr>
                                    <tr>
                                        <td><b>会议时间</b></td>
                                        <td colspan="3">${meetingStartTime?default('')}~${meetingEndTime?default('')}</td>
                                    </tr>
                                    <tr>
                                        <td><b>会议地点</b></td>
                                        <td colspan="3">${meetingPlace?default('')}</td>
                                    </tr>
                                    <tr>
                                        <td><b>参加人员</b></td>
                                        <td colspan="3">${participants?default('')}</td>
                                    </tr>
                                    <tr>
                                        <td><b>缺席人员</b></td>
                                        <td>${absentPerson?default('')}</td>
                                        <td><b>迟到人员</b></td>
                                        <td>${latePerson?default('')}</td>
                                    </tr>
                                    <tr>
                                        <td><b>主 持 人</b></td>
                                        <td>${presenter?default('')}</td>
                                        <td><b>记 录 员</b></td>
                                        <td>${recordPerson?default('')}</td>
                                    </tr>
                                    <tr>
                                        <td><b>主要议题</b></td>
                                        <td colspan="3">${meetingTheme?default('')}</td>
                                    </tr>
                                    <tr>
                                        <td><b>发布范围</b></td>
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
                                </table>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <div >
                        <div style="height: 300px;overflow:auto; ">
                            <span><b>会议内容：</b></span><br><br><br>
                            <#if summaryId?has_content>
                            <div id="summaryContentHtml"></div>
                            <#else >
                                <span><b style="color: red">会议纪要待上传，会议主要拟讨论事宜：</b></span>
                            </#if>
                        </div>

                        <#if workPlanId?has_content>
                        <div>
                            <@getPersonWorkList workPlanId='${workPlanId}'></@getPersonWorkList>
                        </div>
                        </#if>
                        <div style="width:100%;height: 1px;background: black;border: 1px solid"></div>
                        <div align="center">
                            <span style="margin-left: 40px"><b>签发人：<b style="color: blue">${signPerson?default('')}</b></b></span>
                            <#assign personInfo = delegator.findOne("Person", {"partyId" : releasePerson?default('')},false)>
                            <span style="margin-left: 40px"><b>存档人:<b style="color: blue">${personInfo.fullName?default('')}</b></b></span>
                            <span style="margin-left: 40px"><b><b>存档时间：<b style="color: blue">${releaseSummaryTime?default('')}</b></b></span>
                            <div align="center" style="margin-top: 20px">
                                <a href="#" class="smallSubmit">打印纪要</a>
                                <a href="#" class="smallSubmit">浏览日志</a>
                                <a href="#" class="smallSubmit">导出word</a>
                            </div>
                        </div>
                    </div>
                    <div style="margin-top: 20px" id="signRecordList">
                    <#if hasSignIn?has_content && hasSignIn=="Y">
                          <@signInRecord noticeId='${summaryId}' type='TblMeetingNotice' param=parameters?default('') hasSignInList=hasSignInList statusList=statusList></@signInRecord>
                    </#if>
                    </div>
                    <div style="margin-top: 20px;">
                        <div class="screenlet-title-bar">
                            <span style="color: black">反馈信息</span>
                        </div>
                        <div id="feedbackList">
                            <@feedbackList  feedbackMiddleId='${summaryId}' feedbackPerson='' childFeedbackId="" type='TblMeetingNotice' value='' targetId='feedbackList'></@feedbackList>
                        </div>
                    </div>
                    <div style="margin-top: 20px;" align="center">
                        <b>相关责任人(部门)<b style="color: blue">录入</b>会议纪要跟进结果</b><br>
                        <span style="color:green;font-size: 8px">(由会议纪要中安排的相关人员在完成任务跟进后填写)</span>
                    </div>
                    <div>
                        <textarea name="template" id="template"></textarea>
                    </div>
                </div>
            </div>
        </div>
        <div align="center" style="margin-top: 20px">
            <a href="#" class="smallSubmit" id="meeting_summary_feedback">提交反馈</a>
        </div>
    </form>
</div>
