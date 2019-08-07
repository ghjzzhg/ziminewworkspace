<#include "component://oa/webapp/oa/humanres/workPlan/feedbackListCommon.ftl"/>
<#include "component://oa/webapp/oa/humanres/workPlan/childWorkPlanList.ftl"/>
<#include "component://oa/webapp/oa/humanres/workPlan/foldersHeader.ftl"/>
<#assign userLoginInfo = delegator.findOne("Person",{"partyId":userLogin.partyId},false)>
<#if parameters.returnValue?has_content>
    <#assign returnValue = parameters.returnValue>
    <#assign childWorkPlan=''/>
    <#assign childWorkPlanId=''/>
    <#assign staffId = ''>
    <#if returnValue.childWorkPlan?has_content>
        <#assign childWorkPlan = returnValue.childWorkPlan?default('')>
        <#assign childWorkPlanId = childWorkPlan.childWorkPlanId?default('')>
    </#if>
    <#if returnValue.currentExecutor?has_content>
        <#assign staffId = returnValue.currentExecutor.staffId?default('')>
    </#if>
    <#assign milestoneList = returnValue.milestoneList>
<script language="javascript">
    $('#feedback').validationEngine();
    var template;
    var templateRemark;
    $(function () {
        template = KindEditor.create('textarea[name="feedbackContext"]', {
            allowFileManager: true
        });

        templateRemark = KindEditor.create('textarea[name="performanceRemark"]', {
            allowFileManager: true
        });
    });

    function saveFeedback() {
        $('textarea[name="feedbackContext"]').val(template.html());
        var options = {
            beforeSubmit: function () {
                var validation = $('#feedback').validationEngine('validate');
                if (!validation) {
                    showInfo("反馈不能为空！");
                }
                return validation;
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.msg);
                /*$.feedback.findFeedback('${returnValue.workPlanId}', '${childWorkPlanId}', '${staffId}', '#feedbackList');*/
                $.ajax({
                    type: 'POST',
                    url: "findFeedback",
                    async: true,
                    data: {workPlanId: '${returnValue.workPlanId}', childWorkPlanId: '${childWorkPlanId}', partyId: '${staffId}',refreshId: '#feedbackList'},
                    dataType: 'html',
                    success: function (content) {
                        $('#feedbackList').html(content);
                        template.html("");
                    }
                });
                $.workPlan.searchWorPlan();
            },
            url: "saveFeedback",
            type: 'post'
        };
        $("#feedback").ajaxSubmit(options);
    }

</script>
<#--子任务-->
    <#assign displayChild = " " />
    <#if returnValue.childWorkPlan?has_content>
        <#assign displayChild = "none" />
    <div>
        <div class="screenlet-title-bar">
            <ul>
                <li>子计划名称：${childWorkPlan.title?default('')}</li>
                <#if returnValue.currentExecutor?has_content>
                    <#assign currentExecutor = returnValue.currentExecutor>
                </#if>
                <li><a class="smallSubmit"
                                  onclick="lookExecutorJob()">
                    查看${currentExecutor.fullName?default('')}的任务</a>
                </li>
                <li><a class="smallSubmit"
                                  onclick="lookWorkPlan()">查看总任务</a>
                </li>
                <script>
                    function lookExecutorJob() {
                        displayInTab2("feedbackWorkPlan_executorJobs", "任务安排", {});
                    }
                </script>
            </ul>
            <br class="clear">
        </div>
        <div id="childWorkPlanInfo">
            <form class="basic-form">
                <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                    <tbody>
                    <tr>
                        <td class="label">
                            <label>安排部门:</label>
                        </td>
                        <td>
                        ${childWorkPlan.departmentName?default('')}/${childWorkPlan.fullName?default('')}
                        </td>

                        <td class="label">
                            <label>要求时间:</label>
                        </td>
                        <td>
                        ${childWorkPlan.startTime?string('yyyy-MM-dd HH:mm:ss')}
                            &nbsp;~&nbsp;${childWorkPlan.completeTime?string('yyyy-MM-dd HH:mm:ss')}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            <label>进度:</label></td>
                        <td>
                            <#if childWorkPlan.childWorkPlanStatus?has_content>
                            <@WorkPlanProgressbar id="childWorkPlanOfExecutorProgressbar_${childWorkPlan.childWorkPlanId}" value=childWorkPlan.childWorkPlanStatus?default(0) />
                        </#if>
                        </td>
                        <td class="label">
                            <label>执行人:</label>
                        </td>
                        <td>
                        ${childWorkPlan.executorName?default('')}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            <label>任务说明:</label>
                        </td>
                        <td colspan="3" id="childWorkPlan_description">
                        <#--${childWorkPlan.description?default('')}-->
                        </td>
                        <script>
                            $("#childWorkPlan_description").append(unescapeHtmlText('${childWorkPlan.description?default('')}'));
                        </script>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>

    </div>
    </#if>
<#--执行人的任务安排-->
    <#if returnValue.executorJobs?has_content && returnValue.executorJobs.startTime?has_content>
        <#assign displayWorkPlan = "none">
        <#assign executorJobs = returnValue.executorJobs>
    <div style="display:${displayChild};">
        <div class="screenlet-title-bar">
            <ul>
                <li><b
                        style="color:green;"><#if returnValue.currentExecutor?has_content>${returnValue.currentExecutor.fullName?default('')}</#if></b>的任务安排：
                </li>
                <li><a class="smallSubmit"
                                  onclick="lookWorkPlan()">查看总任务</a>
                </li>
                <script>
                    function lookWorkPlan() {
                        displayInTab2("feedbackWorkPlan_workPlan", "总任务", {height:200});
                    }
                </script>
            </ul>
            <br class="clear">
        </div>
        <form class="basic-form" id="feedbackWorkPlan_executorJobs">
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tr class="header-row-2">
                    <td width="60%">任务描述</td>
                    <td width="20%">开始时间</td>
                    <td width="20%">结束时间</td>
                </tr>

                <tr>
                    <td>${executorJobs.jobDescription?default('')}</td>
                    <td>${executorJobs.startTime?string('yyyy-MM-dd')}</td>
                    <td>${executorJobs.completeTime?string('yyyy-MM-dd')}</td>
                </tr>

            </table>
        </form>
    </div>
    <#else >
        <#assign displayWorkPlan = " ">
    </#if>
<#--执行人的子任务-->
    <div style="display:${displayChild};">
        <div id="executorChildWorkPlanList">
            <#if returnValue.childWorkPlanOfExecutorList?has_content>
                <#assign childWorkPlanOfExecutorList = returnValue.childWorkPlanOfExecutorList>
                <@ChildWorkPlanList valueList=childWorkPlanOfExecutorList curPerson='${returnValue.currentExecutor.staffId}' type="childWorkPlan" personId='${returnValue.personId}'></@ChildWorkPlanList>
            </#if>
        </div>
    </div>


<#--总任务-->
<div id="feedbackWorkPlan_workPlan" style="display: ${displayWorkPlan}">
    <form class="basic-form">
        <div class="screenlet-title-bar">
            <ul style="float: left">
                <li>总任务名称:${returnValue.title?default('')}</li>
            </ul>
            <br class="clear">
        </div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td class="label">
                    <label>安排部门:</label>
                </td>
                <td>
                ${returnValue.groupName?default('')}
                </td>

                <td class="label">
                    <label>预定时间:</label>
                </td>
                <td>
                ${returnValue.startTime?default('')?string("yyyy-MM-dd")}
                    &nbsp;~&nbsp;${returnValue.completeTime?default('')?string("yyyy-MM-dd")}
                </td>
            </tr>

            <tr>
                <td class="label">
                    <label>总体进度:</label></td>
                <td>
                ${returnValue.wpsDescription?default('')}
                </td>

                <td class="label">
                    <label>重要级:</label></td>
                <td>
                ${returnValue.idDescription?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>难度:</label></td>
                <td>
                ${returnValue.ddDescription?default('')}
                </td>

                <td class="label">
                    <label>安 排 人:</label></td>
                <td>
                ${returnValue.planPersonName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>执行人:</label>
                </td>
                <td colspan="3">
                    <#if returnValue.executorList?has_content>
                        <#list returnValue.executorList as executor>
                            <#assign executorPerson = delegator.findOne("Person",{"partyId":executor.partyId},false)>
                            ${executorPerson.fullName},
                        </#list>
                    </#if>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>任务说明:</label>
                </td>
                <td colspan="4">
                ${returnValue.planDescription?default('')}
                </td>
            </tr>
                <#if milestoneList?has_content>
                <tr>
                    <td class="label">
                        <label>里程碑：</label>
                    </td>
                    <td colspan="4">
                        <table>
                            <tr class="header-row-2">
                                <td>序号</td>
                                <td>时间</td>
                                <td>要求</td>
                            </tr>
                            <#assign milestoneItem = 1>
                            <#list milestoneList as list>
                                <tr>
                                    <td>${milestoneItem}</td>
                                    <td>${list.milestoneTime?default('')}</td>
                                    <td>${list.milestoneDescription?default('')}</td>
                                </tr>
                                <#assign milestoneItem = milestoneItem + 1>
                            </#list>

                        </table>
                    </td>
                </tr>
                </#if>

            </tbody>
        </table>
        <div id="workPlanChildWorkPlanList">
            <#if returnValue.childPlanList?has_content>
                 <@ChildWorkPlanList valueList=returnValue.childPlanList curPerson='' type='workPlan' personId='${returnValue.personId}'></@ChildWorkPlanList>
            </#if>
        </div>
    </form>
</div>


    <#if isGrade?has_content && isGrade=="true">
    <div class="screenlet-title-bar">
        <ul style="float: left">
            <li style="float: left">
                <b style="color:green;"><#if returnValue.currentExecutor?has_content>${returnValue.currentExecutor.fullName?default('')}</#if></b>任务反馈：
            </li>
        </ul>
        <br class="clear">
    </div>

    <div id="feedbackList_grade">
        <#if returnValue.childWorkPlan?has_content>
                <@feedbackList workPlanId='${returnValue.workPlanId}' childWorkPlanId='${returnValue.childWorkPlan.childWorkPlanId}' partyId='${returnValue.currentExecutor.staffId}' value='' targetId='feedbackList_grade'/>
            <#else >
            <@feedbackList workPlanId='${returnValue.workPlanId}' childWorkPlanId='' partyId='${returnValue.currentExecutor.staffId}' value='' targetId='feedbackList_grade'/>
        </#if>
    </div>
    <div>
        <form id="workPlanGradeForm" name="workPlanGradeForm">
            <#if returnValue.curPersonWork?has_content>
                <#assign curPersonWork = returnValue.curPersonWork>
                <#assign curPersonWorkId = curPersonWork.personWorkId>
                <#assign curPersonWorkStatus = curPersonWork.personWorkStatus?default('')>
                <#assign curActualCompleteTime = curPersonWork.completeTime?default(.now)>
                <#assign curPerformanceRemark = curPersonWork.performanceRemark?default('')>
                <#assign curPerformanceScore = curPersonWork.performanceScore?default('')>
            </#if>
            <input type="hidden" name="personId" value="${returnValue.currentExecutor.staffId}">
            <input type="hidden" name="workPlanId" value="${returnValue.workPlanId}">
            <input type="hidden" name="personWorkId" value="${curPersonWorkId?default('')}">
            <div class="screenlet-title-bar">
                <ul>
                    <li>对<b
                            style="color:green;"><#if returnValue.currentExecutor?has_content>${returnValue.currentExecutor.fullName?default('')}</#if></b>任务评定：
                    </li>
                    <li>评定人：<b style="color:green;">${userLoginInfo.fullName?default('')}</b></li>
                    <li>评定时间：<b style="color:green;">${.now?string('yyyy-MM-dd hh:mm:ss')}</b></li>
                </ul>
                <br class="clear">
            </div>
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr>
                    <td class="label">
                        <label for="WorkPlan_grade_personWorkStatus" id="WorkPlan_grade_personWorkStatus_title">任务状态：</label>
                    </td>
                    <td>
                        <select id="WorkPlan_grade_personWorkStatus" name="personWorkStatus">
                            <#if curPersonWorkStatus?has_content>
                                <#if curPersonWorkStatus == 100>
                                    <option value="100" selected>已完成</option>
                                <#else >
                                    <option value="100">已完成</option>
                                </#if>
                                <option value="">执行中</option>
                                <#if curPersonWorkStatus == -1>
                                    <option value="-1" selected>作废</option>
                                <#else >
                                    <option value="-1">作废</option>
                                </#if>
                            <#else >
                                <option value="100">已完成</option>
                                <option value="">执行中</option>
                                <option value="-1">作废</option>
                            </#if>
                        </select>
                    </td>
                    <td class="label">
                        <label for="WorkPlan_grade_performanceScore"
                               id="WorkPlan_grade_performanceScore_title">任务评分：</label>
                    </td>
                    <td>
                        <select id="WorkPlan_grade_performanceScore" name="performanceScore">
                            <#if workPlanGrade?has_content>
                                <#list workPlanGrade as enum>
                                    <option value="${enum.enumCode}" <#if enum.enumCode == curPerformanceScore?default('')>selected</#if>>${enum.enumCode}</option>
                                </#list>
                            </#if>
                        </select>
                    </td>
                    <td class="label">
                        <label for="WorkPlan_grade_completeTime" id="WorkPlan_grade_completeTime_title">完成时间</label>
                    </td>
                    <td>
                        <@htmlTemplate.renderDateTimeField name="actualCompleteTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                        value="${curActualCompleteTime?default(.now)}" size="25" maxlength="30" id="WorkPlan_grade_completeTime" dateType="" shortDateInput=true timeDropdownParamName=""
                        defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                        timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    </td>
                </tr>
                <tr>
                    <td class="label">评语</td>
                    <td colspan="5">
                        <textarea name="performanceRemark">${curPerformanceRemark?default('')}</textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <div style="width:100%;" align="center">
                            <a class="smallSubmit" onclick="$.workPlan.saveGrade(${returnValue.workPlanId})">提交</a>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
    <#else >
        <#if returnValue.currentExecutor?has_content>
            <#assign curPartyId = returnValue.currentExecutor.staffId?default('')>
        </#if>
    <div class="screenlet-title-bar">
        <ul>
            <#if returnValue.childWorkPlan?has_content>
                <#assign childWorkPlan = returnValue.childWorkPlan>
                <li><b
                        style="color:green;">${returnValue.currentExecutor.fullName?default('')}</b>子计划反馈：
                </li>

                <#if returnValue.personId == userLogin.partyId || curPartyId?default('') == userLogin.partyId>
                    <li>
                        <a class="smallSubmit"
                           onclick="$.workPlan.addChildWorkPlan('${returnValue.workPlanId}','${returnValue.currentExecutor.staffId?default('')}','${childWorkPlan.childWorkPlanId}','')">编辑子计划</a>
                    </li>
                </#if>
            <#else >
                <li style="float: left"><b
                        style="color:green;"><#if returnValue.currentExecutor?has_content>${returnValue.currentExecutor.fullName?default('')}</#if></b>任务反馈：
                </li>
                <#if returnValue.personId == userLogin.partyId || curPartyId?default('') == userLogin.partyId>
                    <li>
                        <a class="smallSubmit"
                           onclick="$.workPlan.addChildWorkPlan('${returnValue.workPlanId}','${curPartyId?default('')}','','${curPartyId?default('')}')">增加子计划</a>
                    </li>
                </#if>

                <#if !(curPartyId?has_content)>
                    <#if userLogin.partyId == returnValue.personId>
                        <li>
                            <a class="smallSubmit" onclick="$.workPlan.editWorkPlan(${returnValue.workPlanId})">编辑计划</a>
                        </li>
                    </#if>

                <li><a class="smallSubmit"
                                  onclick="$.workPlan.copyWorkPlan(${returnValue.workPlanId})">复制计划</a></li>
                <li><a class="smallSubmit" onclick="$.workPlan.editPerformance(${returnValue.workPlanId})">绩效评分</a>
                </li>
                </#if>
            </#if>

        </ul>
        <br class="clear">
    </div>
    <div id="feedbackList">
        <#if returnValue.childWorkPlan?has_content>
            <@feedbackList workPlanId='${returnValue.workPlanId}' childWorkPlanId='${returnValue.childWorkPlan.childWorkPlanId}' partyId=staffId value=''/>
        <#else>
            <@feedbackList workPlanId='${returnValue.workPlanId}' childWorkPlanId='' partyId=staffId value=''/>
        </#if>
    </div>
    <form name="feedback" id="feedback">
        <input type="hidden" name="workPlanId" value="${returnValue.workPlanId}" form="feedback">
        <#if returnValue.childWorkPlan?has_content>
            <input type="hidden" name="childWorkPlanId" value="${returnValue.childWorkPlan.childWorkPlanId}"
                   form="feedback">
        </#if>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <#if !staffId?has_content>
                    <td class="label">
                        代反馈
                    </td>
                    <td>
                        <select name="feedbackPerson" form="feedback">
                            <#if parameters.returnValue.executorList?has_content>
                                <#if returnValue.currentExecutor?has_content>
                                    <#assign currentExecutor = returnValue.currentExecutor.staffId>
                                <#else>
                                    <#assign currentExecutor = "">
                                </#if>

                                <#list returnValue.executorList as executor>
                                    <#if currentExecutor == executor.partyId>
                                        <option value="${executor.partyId}" selected>${executor.fullName}</option>
                                    <#elseif parameters.userLogin.partyId == executor.partyId>
                                        <option value="${executor.partyId}" selected>${executor.fullName}</option>
                                    <#else>
                                        <option value="${executor.partyId}">${executor.fullName}</option>
                                    </#if>
                                </#list>
                            </#if>
                        </select>
                    </td>
                <#else >
                    <input type="hidden" name="feedbackPerson" value="${staffId}">
                </#if>

                <td class="label">
                    进度
                </td>
                <td class="jqv">
                    <script type="text/javascript">
                        function personWorkStatusChange(){
                            $("input[name='personWorkStatus']").val($("select[name='personWorkStatus_select']").val());
                        }
                    </script>
                    <input type="text" size="5" name="personWorkStatus" class="validate[required]"
                           <#if returnValue.childWorkPlan?has_content>
                                value="${returnValue.childWorkPlan.childWorkPlanStatus?default(0)}"
                           <#elseif executorJobs?has_content>
                                value="${executorJobs.personWorkStatus?default('')}"
                           </#if>>
                    <select name="personWorkStatus_select" form="feedback" onchange="personWorkStatusChange()">
                        <option value="0">0</option>
                        <#list 1..10 as list>
                            <option value="${list}0">${list}0</option>
                        </#list>
                    </select>%
                </td>

                <td class="label">
                    查看人
                </td>
                <td>
                    <select name="permission" form="feedback">
                        <#if feedbackpermission?has_content>
                            <#list feedbackpermission as permission>
                                <option value="${permission.enumId}">${permission.description}</option>
                            </#list>
                        </#if>
                    </select>
                </td>
                <td class="label">
                    <input type="checkbox">Email通知<input type="text" value="" disabled="disabled">
                </td>
            </tr>
            <tr>
                <td colspan="7">
                    <textarea name="feedbackContext" style="width:100%;" form="feedback"
                              class="validate[required]"></textarea>
                </td>
            </tr>
            </tbody>
        </table>
    </form>
    <div align="center">
        <a href="#" class="smallSubmit" onclick="saveFeedback()">提交</a>
    </div>
    <div>
            <span class="label" style="margin-right: 20px">本次登录时间：<b
                    style="color: blue">${parameters.returnValue.nowDate}</b></span><span class="label">上次反馈时间：
        <b style="color: blue"><#if parameters.returnValue.feedbackTime?has_content> ${parameters.returnValue.feedbackTime?string('yy-MM-dd HH:mm:ss')}</#if></b></span>
    </div>
    </#if>
</#if>
