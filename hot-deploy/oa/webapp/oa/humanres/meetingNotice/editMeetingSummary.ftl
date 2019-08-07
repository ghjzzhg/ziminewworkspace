<script language="javascript">
    var childPlanIndex = 1;
    var template;
    $(function () {
        template = KindEditor.create('#meetingSummary_summaryContent', {
            allowFileManager: true
        });

        $("#follow_up").css("display", "none");
        //$("#meetingSummary_recordPerson").attr("disabled","disabled");
    });

    function displayFollowUp(obj) {
        if (obj.attr("checked") == 'checked') {
            $("#follow_up").css("display", "    ");
            $("input[name = 'title']").attr("class","validate[required]");
            $("input[name = startTime]").attr("class","validate[required,past[completeTime:yyyy-MM-dd]]");
            $("input[name = completeTime]").attr("class","validate[required,future[startTime:yyyy-MM-dd]]");
            $("input[name = executor]").attr("class","validate[required]");
        } else {
            $("#follow_up").css("display", "none");
            $("input[name = title]").removeAttr("class","validate[required]");
            $("input[name = startTime]").removeAttr("class","validate[required,past[completeTime:yyyy-MM-dd]]");
            $("input[name = completeTime]").removeAttr("class","validate[required,future[startTime:yyyy-MM-dd]]");
            $("input[name = executor]").removeAttr("class","validate[required]");
        }
    }
    function saveMeetingWorkPlan() {
        $("textarea[name='summaryContent']").val(template.html());
        var options = {
            beforeSubmit: function () {
                $("input[name = 'title']").attr("class","validate[required]");
                $("input[name = startTime]").attr("class","validate[required,past[completeTime:yyyy-MM-dd]]");
                $("input[name = completeTime]").attr("class","validate[required,future[startTime:yyyy-MM-dd]]");
                $("input[name = executor]").attr("class","validate[required]");
                return $('#meetingNoticeSummaryForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                $("input[name='workPlanId']").attr("value",data.workPlanId);
                showInfo("计划保存成功");
                console.log(data);
            },
            url: "meetingWorkPlanCreate",
            type: 'post'
        };
        $("#meetingNoticeSummaryForm").ajaxSubmit(options);
    }
</script>
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>

<#if summaryId?has_content>
    <#assign hasWorkPlan = "N">
    <#assign isCreate = "N">
    <#assign meetingNoticeList = delegator.findByAnd("MeetingSummaryNotice",{"summaryId":summaryId})>
    <#assign meetingNoticeOrSummaryId = summaryId>
<#else >
    <#assign isCreate = "Y">
    <#assign meetingNoticeList = delegator.findByAnd("MeetingNoticeInfo",{"meetingNoticeId":meetingNoticeId})>
    <#assign meetingNoticeOrSummaryId = meetingNoticeId>
</#if>
<#if meetingNoticeList?has_content>
    <#assign meetingNoticeMap = meetingNoticeList.get(0)>
    <#assign signInPersonList = delegator.findByAnd("SignInPersonInfo",{"noticeId":meetingNoticeOrSummaryId,"signInPersonType":"TblMeetingNotice"})>
    <#assign meetingNoticeId = meetingNoticeMap.meetingNoticeId?default('')>
    <#assign releaseGroupName = meetingNoticeMap.groupName?default('')>
    <#assign releaseTime = meetingNoticeMap.releaseTime?default('')>
    <#assign meetingName = meetingNoticeMap.meetingName?default('')>
    <#assign meetingStartTime = meetingNoticeMap.meetingStartTime?default('')>
    <#assign meetingEndTime = meetingNoticeMap.meetingEndTime?default('')>
    <#assign meetingPlace = meetingNoticeMap.meetingPlace?default('')>
    <#assign presenter = meetingNoticeMap.presenter?default('')>
    <#assign hasSignIn = meetingNoticeMap.hasSignIn?default('')>
    <#assign meetingNoticeNumber = meetingNoticeMap.meetingNoticeNumber?default('')>
    <#assign meetingTheme = meetingNoticeMap.meetingTheme?default('')>
    <#assign extParticipants = meetingNoticeMap.extParticipants?default('')>
    <#assign participants = " ">
    <#assign scope = " ">
    <#if isCreate?has_content&&isCreate=='N'>
        <#assign summaryId = meetingNoticeMap.summaryId?default('')>
        <#assign recordPerson = meetingNoticeMap.recordPerson?default('')>
        <#assign signPerson = meetingNoticeMap.signPerson?default('')>
        <#assign absentPerson = meetingNoticeMap.absentPerson?default('')>
        <#assign latePerson = meetingNoticeMap.latePerson?default('')>
        <#assign summaryContent = meetingNoticeMap.summaryContent?default('')>
        <#assign releasePerson = meetingNoticeMap.releasePerson?default('')>
        <#assign workPlanId = meetingNoticeMap.workPlanId?default('')>
        <#assign releaseSummaryTime = meetingNoticeMap.releaseSummaryTime?default('')>
    <#else >
        <#assign summaryId = ''>
        <#assign recordPerson = ''>
        <#assign signPerson = ''>
        <#assign absentPerson = ''>
        <#assign latePerson = ''>
        <#assign summaryContent = meetingNoticeMap.content?default('')>
        <#assign releasePerson = ''>
        <#assign releaseSummaryTime = ''>
        <#assign workPlanId = ''>
    </#if>
</#if>

<#assign strSingIn ="">
<#if signInPersonList?has_content>
    <#list signInPersonList as signIn>
        <#assign strSingIn = strSingIn+","+signIn.staffId>
    </#list>
    <#assign strSingIn = strSingIn?substring(1)>
</#if>

<#assign strAbsent = "">
<#if absentPersonList?has_content>
    <#list absentPersonList as list1>
        <#assign strAbsent = strAbsent+","+list1.staffId>
    </#list>
    <#assign strAbsent = strAbsent?substring(1)>
</#if>

<#assign strLate = "">
<#if latePersonList?has_content>
    <#list latePersonList as list2>
        <#assign strLate = strLate+","+list2.staffId>
    </#list>
    <#assign strLate = strLate?substring(1)>
</#if>

<div style="padding: 20px">
    <div>
        <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
            <tbody>
            <tr>
                <td valign="top"><img
                        src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="meetingNoticeSummaryForm" id="meetingNoticeSummaryForm" class="basic-form">
        <input type="hidden" name="meetingNoticeId" value="${meetingNoticeId}">
        <input type="hidden" name="summaryId" value="${summaryId}">
        <input type="hidden" name="workPlanId" value="${workPlanId}">
        <div>
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                会 议 纪 要
            </div>
            <div>
                <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                    <tbody>
                    <tr>
                        <td colspan="6">
                            <table cellpadding="0" cellspacing="0" border="1" width="100%"
                                   style="border-collapse: collapse">
                                <tbody>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingSummary_meetingNoticeNumber"
                                               id="meetingSummary_meetingNoticeNumber_title"><b class="requiredAsterisk">*</b>会议编号：</label>
                                    </td>
                                    <td colspan="3">
                                        <label>${meetingNoticeNumber}</label>
                                        <#--<input id="meetingSummary_meetingNoticeNumber" type="text"-->
                                               <#--name="meetingNoticeNumber" value="${meetingNoticeNumber}" disabled>-->
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_meetingName" id="meetingSummary_meetingName_title"><b class="requiredAsterisk">*</b>会议名称：</label>
                                    </td>
                                    <td colspan="3" class="jqv">
                                        <input type="text" id="meetingSummary_meetingName" name="meetingName"
                                               value="${meetingName}" maxlength="125" class="validate[required,custom[onlyLetterNumberChinese]]">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_meetingTheme" id="meetingSummary_meetingTheme_title"><b class="requiredAsterisk">*</b>会议主题：</label>
                                    </td>
                                    <td colspan="3" class="jqv">
                                        <input type="text" id="meetingSummary_meetingTheme" name="meetingTheme"
                                               value="${meetingTheme}" class="validate[required]" maxlength="125">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_meetingTime" id="meetingSummary_meetingTime_title"><b class="requiredAsterisk">*</b>会议时间: </label>
                                    </td>
                                    <td colspan="3">
                                    <@htmlTemplate.renderDateTimeField name="meetingStartTime1" event="" action="" className="validate[required,past[meetingEndTime1:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                                    value="${meetingStartTime?default('')}" size="25" maxlength="30" id="meetingSummary_meetingStartTime1" dateType="" shortDateInput=false timeDropdownParamName="" defaultDateTimeString=""
                                    localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                       至
                                    <@htmlTemplate.renderDateTimeField name="meetingEndTime1" event="" action="" className="validate[required,future[meetingStartTime1:yyyy-MM-dd HH:mm:ss]]" alert="" title="Format: yyyy-MM-dd HH:mm:ss"
                                    value="${meetingEndTime?default('')}" size="25" maxlength="30" id="meetingSummary_meetingEndTime1" dateType="" shortDateInput=false timeDropdownParamName="" defaultDateTimeString=""
                                    localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                    </td>

                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_meetingPlace" id="meetingSummary_meetingPlace_title"><b class="requiredAsterisk">*</b>会议地点：</label>
                                    </td>
                                    <td class="jqv">
                                        <input type="text" id="meetingSummary_meetingPlace" name="meetingPlace"
                                               value="${meetingPlace}" class="validate[required]" maxlength="125">
                                    </td>
                                    <td class="label">
                                        <label for="meetingSummary_presenter" id="meetingSummary_presenter_title"><b class="requiredAsterisk">*</b>主 持 人：</label>
                                    </td>
                                    <td class="jqv">
                                        <input type="text" id="meetingSummary_presenter" name="presenter"
                                               value="${presenter}" class="validate[required]">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_recordPerson" id="meetingSummary_recordPerson_title"><b class="requiredAsterisk">*</b>记 录 员：</label>
                                    </td>
                                    <td class="jqv">
                                        <input type="text" id="meetingSummary_recordPerson" name="recordPerson" value="${recordPerson}" class="validate[required]">
                                    </td>
                                    <td class="label">
                                        <label for="meetingSummary_signPerson" id="meetingSummary_signPerson_title"><b class="requiredAsterisk">*</b>签 发 人：</label>
                                    </td>
                                    <td class="jqv">
                                        <input type="text" id="meetingSummary_signPerson" name="signPerson" value="${signPerson}" class="validate[required]">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingSummary_participants" id="meetingSummary_participants_title"><b class="requiredAsterisk">*</b>与会人员：</label>
                                    </td>
                                    <td colspan="2" class="jqv">
                                    <@selectStaff id="meetingSummary_participants" name="participants" value="${strSingIn}" multiple=true required=true/>
                                    </td>
                                    <td>
                                        <input type="checkbox">发送邮件通知<br>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingSummary_extParticipants"
                                               id="meetingSummary_extParticipants_title">系统外与会人员：</label>
                                    </td>
                                    <td colspan="3">
                                        <@meetingNoticeTextarea id="extParticipants" name="extParticipants" value=extParticipants></@meetingNoticeTextarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">　
                                        <label for="meetingSummary_absentPerson" id="meetingSummary_absentPerson_title">缺席人员：</label>
                                    </td>
                                    <td colspan="3">
                                    <@selectStaff id="meetingSummary_absentPerson" name="absentPerson" value="${strAbsent?default('')}" multiple=true/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_latePerson" id="meetingSummary_latePerson_title">迟到人员：</label>
                                    </td>
                                    <td colspan="3">
                                    <@selectStaff id="meetingSummary_latePerson" name="latePerson" value="${strLate?default('')}" multiple=true/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_noticeDataScope"
                                               id="meetingSummary_noticeDataScope_title">发放范围：</label>
                                    </td>
                                    <td colspan="3">
                                    <@dataScope id="meetingSummary_noticeDataScope" name="meetingSummaryDataScope" dataId="${meetingNoticeId?default('')}" entityName="TblMeetingNotice"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="meetingSummary_summaryContent"
                                               id="meetingSummary_summaryContent_title"><b class="requiredAsterisk">*</b>会议内容：</label>
                                    </td>
                                    <td colspan="3"></td>
                                </tr>
                                <tr>
                                    <td colspan="4" class="jqv">　
                                        <textarea id="meetingSummary_summaryContent" name="summaryContent" class="validate[required]">${summaryContent}</textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                <#if hasWorkPlan?has_content&&hasWorkPlan=="N">
                                    <td></td>
                                <#else >
                                    <td>
                                        <span style="color: #0000CC">重要事项转工作计划跟进 </span><input type="checkbox" onclick="displayFollowUp($(this))">
                                    </td>
                                </#if>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <div id="follow_up">
                                <table>
                                    <tr>
                                        <td class="label">
                                            <label for="workPlan_title"
                                                   id="workPlan_title_title"><b class="requiredAsterisk">*</b>简要标题:</label>
                                        </td>
                                        <td colspan="3">
                                            <input type="text" id="workPlan_title" name="title" value="${meetingName}">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="WorkPlan_startTime"
                                                   id="WorkPlan_startTime_title"><b class="requiredAsterisk">*</b>执行日期:</label>
                                        </td>
                                        <td>
                                        <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                                        value="${startTime?default('')}" size="25" maxlength="30" id="WorkPlan_startTime" dateType="" shortDateInput=true timeDropdownParamName=""
                                        defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                        timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                            <#--<script type="text/javascript">-->
                                                <#--$("#WorkPlan_startTime").attr("class","validate[required]");-->
                                            <#--</script>-->
                                        </td>
                                        <td class="label">
                                            <label for="WorkPlan_completeTime"
                                                   id="WorkPlan_completeTime_title"><b class="requiredAsterisk">*</b>完成日期:</label>
                                        </td>
                                        <td>
                                        <@htmlTemplate.renderDateTimeField name="completeTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                                        value="${completeTime?default('')}" size="25" maxlength="30" id="WorkPlan_completeTime" dateType="" shortDateInput=true timeDropdownParamName=""
                                        defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                        timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                            <#--<script type="text/javascript">-->
                                                <#--$("#WorkPlan_completeTime").attr("class","validate[required]");-->
                                            <#--</script>-->
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="WorkPlan_executor"
                                                   id="WorkPlan_executor_title"><b class="requiredAsterisk">*</b>执行人姓名:</label>
                                        </td>
                                        <td class="jqv">
                                            <div id="executorOptions" style="display: none">
                                            <#if executorList?has_content>
                                                <#list executorList as executor>
                                                    <option value="${executor.staffId}">${executor.fullName?default('')}</option>
                                                </#list>
                                            </#if>
                                            </div>
                                        <#assign executors=''>
                                        <#assign fullNames=''>
                                        <#if executorList?has_content>
                                            <div id="show_executor_value" style="display: none">
                                                <#list executorList as executor>
                                                    <#assign executors=(executors+','+executor.staffId)>
                                                    <#assign fullNames=(fullNames + executor.fullName)>
                                                    <span>${executor.fullName}</span>
                                                </#list>
                                            </div>
                                        </#if>
                                        <#if (executors?length > 1) >
                                            <#assign executors = executors?substring(1)>
                                        </#if>
                                        <#if (fullNames?length > 1) >
                                            <#assign fullNames = fullNames?substring(1)>
                                        </#if>
                                            <script>
                                                var executorValue = "${executors?default('')}";
                                                <#if personWorkList?has_content>
                                                var executorOptions = $("#executorOptions").html();
                                                <#else>
                                                var executorOptions = "";
                                                </#if>
                                                Array.prototype.in_array = function(e) {
                                                    for(i=0;i<this.length && this[i]!=e;i++);
                                                    return !(i==this.length);
                                                }
                                                function selectedProjectLeader() {
                                                    var executorOptionsStr = "";
                                                    var valueArray = $("#executor_SelectedTab li");
                                                    jobCount = 1;
                                                    var title = $("#workPlan_title").val();
                                                    var startTime = $("#WorkPlan_startTime").val();
                                                    var completeTime = $("#WorkPlan_completeTime").val();
                                                    var executorArray;
                                                    if(executorValue.length > 1){
                                                        executorArray = executorValue.split(",");
                                                    }
                                                    var nowExecutorList = new Array();
                                                    for (var i = 0; i < valueArray.length; i++) {
                                                        var nowExecutor = new Object();
                                                        var executor_partyId = $(valueArray[i]).attr("value_id");
                                                        var executor_fullName = $(valueArray[i]).text();
                                                        var nowExecutor_partyId = $("#jobOfExecutor_" + executor_partyId).val();
                                                        if(nowExecutor_partyId == null || nowExecutor_partyId == ''){
                                                            nowExecutor.description = title;
                                                            nowExecutor.startTime = startTime;
                                                            nowExecutor.endTime =completeTime;
                                                            nowExecutor.jobOfExecutor = executor_partyId;
                                                            nowExecutor.jobOfExecutorFullName = executor_fullName;
                                                            nowExecutorList.push(nowExecutor);
                                                        }else{
                                                            nowExecutor.description = $("#description_"+executor_partyId).val();
                                                            nowExecutor.startTime = $("#startTime_"+executor_partyId).val();
                                                            nowExecutor.endTime = $("#endTime_"+executor_partyId).val();
                                                            nowExecutor.jobOfExecutor = executor_partyId;
                                                            nowExecutor.jobOfExecutorFullName = executor_fullName;
                                                            nowExecutorList.push(nowExecutor);
                                                        }
                                                        executorOptionsStr = executorOptionsStr + '<option value="'+executor_partyId+'">'+executor_fullName+'</option>';
                                                    }
                                                    $(".jobContent").remove();
                                                    for (var i = 0; i < nowExecutorList.length; i++) {
                                                        addJob(nowExecutorList[i].jobOfExecutor,nowExecutorList[i].jobOfExecutorFullName,nowExecutorList[i].description,nowExecutorList[i].startTime,nowExecutorList[i].endTime);
                                                    }
                                                    executorOptions = executorOptionsStr;
                                                    $("#workPlan_projectLeader").children().remove();
                                                    $("#workPlan_projectLeader").append(executorOptions);
                                                }
                                                var jobCount = 1;
                                                function addJob(value,text,title,startTime,completeTime) {
                                                    $("#workPlan_addJob").append('<tr id="job_tr_'+ jobCount +'" class="jobContent">' +
                                                            '<td>' +
                                                            '<textarea name="description_o1_' + jobCount + '" id="description_'+ value +'">'+ title +'</textarea>' +
                                                            '</td>' +
                                                            '<td>' +
                                                            '<span style="position:relative"><label class="label">开始：</label>'+
                                                            '<input type="text" id="startTime_'+ value +'" name="startTime_o1_' + jobCount + '" class="form_datetime" value="'+ startTime +'">' +
                                                            '</span>'+
                                                            '<span style="position:relative"><label class="label">结束：</label>' +
                                                            '<input type="text" id="endTime_'+ value +'" name="endTime_o1_' + jobCount + '" value="'+ completeTime +'">' +
                                                            '</span>' +
                                                            '</td>' +
                                                            '<td>' +
                                                            '<input type="hidden" name="jobOfExecutor_o1_'+ jobCount + '" id="jobOfExecutor_'+ value +'" value="'+ value +'">' + text + '</td>');

                                                    initDatePicker({input: "#startTime_" + value, timeFormat: 'YYYY-MM-DD'});
                                                    initDatePicker({input: "#endTime_" + value, timeFormat: 'YYYY-MM-DD'});
                                                    jobCount++;
                                                }
                                                function creatHiddenInput(jobCount,obj){
                                                    $("#hidden_jobOfExecutor_" + jobCount).val($(obj).val());
                                                }
//                                                function checkStatus(checkbox){
//                                                    if ( checkbox.checked == true){
//                                                        var content1 = document.getElementById('meetingSummary_participants').innerHTML;
//                                                        var stringId = document.getElementById('meetingSummary_participants_participants').value;
//                                                        document.getElementById('executor').innerHTML = content1;
//                                                        document.getElementById('executor_executor').value = stringId;
//                                                        selectedProjectLeader();
//                                                    } else{
//                                                        document.getElementById('executor').innerHTML = "";
//                                                        document.getElementById('executor_executor').value = "";
//                                                        selectedProjectLeader();
//                                                    }
//                                                }
                                            </script>
                                        <@selectStaff id="executor" name="executor" value="${executors}" onchange = "selectedProjectLeader" multiple=true />
                                            <script type="text/javascript">
                                                $("#executor").html($("#show_executor_value").children());
                                            </script>
                                        </td>
                                        <#--<td>-->
                                            <#--<input type="checkbox" onclick="checkStatus(this)">仅与会人员<br>-->
                                        <#--</td>-->
                                        <td class="label">
                                            <label for="WorkPlan_projectLeader"
                                                   id="WorkPlan_projectLeader_title">项目主管:</label>
                                        </td>
                                        <td>
                                            <select id="workPlan_projectLeader" name="projectLeader">
                                            <#if projectLeader?has_content&&executorList?has_content>
                                                <#list executorList as executor>
                                                    <#if projectLeader == executor.staffId>
                                                        <option value="${executor.staffId}" selected>${executor.fullName}</option>
                                                    <#else >
                                                        <option value="${executor.staffId}">${executor.fullName}</option>
                                                    </#if>
                                                </#list>
                                            </#if>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="WorkPlan_job"
                                                   id="WorkPlan_job_title">任务分配:</label>
                                        </td>
                                        <td colspan="3">
                                            <input type="hidden" name="oldExecutorList" value="${executors?default('')}">
                                            <table class="basic-table" id="workPlan_addJob">
                                                <tr class="header-row-2">
                                                    <td width="50%">任务描述</td>
                                                    <td width="35%">完成时间</td>
                                                    <td width="10%">执行人</td>
                                                </tr>
                                            <#if personWorkList?has_content>
                                                <#assign personWorkIndex = 1>
                                                <#list personWorkList as list>
                                                    <#if list.startTime?has_content>
                                                        <tr id="job_tr_${list.personId?default('')}" class="jobContent">
                                                            <td>
                                                                <textarea name="description_o1_${personWorkIndex}" id="description_${list.personId}">${list.jobDescription}</textarea>
                                                            </td>
                                                            <td>
                                                <span>
                                                    <label class="label">开始：</label>
                                                    <@htmlTemplate.renderDateTimeField name="startTime_o1_${personWorkIndex}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                                                    value="${list.startTime?default('')}" size="25" maxlength="30" id="startTime_${list.personId}" dateType="" shortDateInput=true timeDropdownParamName=""
                                                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                                </span>
                                                                <br>
                                                <span>
                                                    <label class="label">结束：</label>
                                                    <@htmlTemplate.renderDateTimeField name="endTime_o1_${personWorkIndex}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                                                    value="${list.completeTime?default('')}" size="25" maxlength="30" id="endTime_${list.personId}" dateType="" shortDateInput=true timeDropdownParamName=""
                                                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                                </span>
                                                            </td>
                                                            <td>
                                                                <input type="hidden" name="jobOfExecutor_o1_${personWorkIndex}" id="jobOfExecutor_${list.personId}" value="${list.personId?default('')}">
                                                                <#assign person = delegator.findOne("Person",{"partyId":list.personId},false)>
                                                            ${person.fullName}
                                                            </td>
                                                        </tr>
                                                        <#assign personWorkIndex = personWorkIndex + 1>
                                                    </#if>
                                                </#list>
                                            </#if>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                                <div align="center">
                                    <a class="smallSubmit" onclick="saveMeetingWorkPlan()">保存计划</a>
                                </div>
                            </div>

                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </form>
</div>
<div align="center" style="margin-top: 20px">
    <a href="#" class="smallSubmit" onclick="$.meetingNotice.saveMeetingSummary(template)">发布纪要</a>
</div>

<#macro meetingNoticeTextarea id name value="">
    <div id="meetingNoticeTextarea_${id}">
        <div  style="float: right">
            <a class="smallSubmit" onclick="clearValue('${id}', '${name}')">清除</a>
        </div>
        <div style="overflow: auto">
            <textarea id="meetingSummary_${id}" name="${name}">${value}</textarea>
        </div>

    </div>
    <script>
        function clearValue(id, name){
            //$("#meetingNoticeTextarea_" + id).html("");
            document.getElementById('meetingSummary_${id}').value = "";
            //$("input[textarea^=" + name + "_]").val("");
            return false;
        }
    </script>
</#macro>