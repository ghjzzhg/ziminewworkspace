
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://oa/webapp/oa/humanres/workPlan/planType.ftl"/>
<#include "component://oa/webapp/oa/humanres/workPlan/milestoneList.ftl"/>
<#if workPlan?has_content>
    <#assign workPlanId = workPlan.workPlanId?default('')>
    <#assign title = workPlan.title?default('')>
    <#assign planType = workPlan.planType?default('')>
    <#assign isPerformance = workPlan.isPerformance?default('')>
    <#assign startTime = workPlan.startTime?default('')>
    <#assign completeTime = workPlan.completeTime?default('')>
    <#assign planPerson = workPlan.planPerson?default('')>
    <#assign importanceDegree = workPlan.importanceDegree?default('')>
    <#assign difficultyDegree = workPlan.difficultyDegree?default('')>
    <#assign noticePerson = workPlan.noticePerson?default('')>
    <#assign canSeePerson = workPlan.canSeePerson?default('')>
    <#assign projectLeader = workPlan.projectLeader?default('')>
    <#assign planDescription = workPlan.planDescription?default('')>
    <#assign workPlanStatus = workPlan.workPlanStatus?default('')>
<#else >
    <#assign workPlanId = ''>
    <#assign title = ''>
    <#assign planType = ''>
    <#assign isPerformance = ''>
    <#assign startTime = ''>
    <#assign completeTime = ''>
    <#assign planPerson = ''>
    <#assign importanceDegree = ''>
    <#assign difficultyDegree = ''>
    <#assign noticePerson = ''>
    <#assign projectLeader = ''>
    <#assign planDescription = ''>
</#if>
<form name="WorkPlanForm" id="WorkPlanForm" class="basic-form">
<#if copyWorkPlan?has_content&&copyWorkPlan=='copy'>
    <input type="hidden" name="workPlanId" value="">
<#else >
    <input type="hidden" name="workPlanId" value="${workPlanId}">
    <input type="hidden" name="workPlanStatus" value="${workPlanStatus?default('')}">
</#if>
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td class="label">
                    <label for="WorkPlan_title"
                           id="WorkPlan_title_title"><span class="requiredAsterisk">*</span>简要标题:</label>
                </td>
                <td class="jqv">
                    <input type="text" size="25" id="WorkPlan_title" name="title" class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="20" data-prompt-position="centerRight"
                           value="<#if copyWorkPlan?has_content&&copyWorkPlan=='copy'>复制于${title?default('')}<#else >${title?default('')}</#if>">
                </td>
                <td class="label">
                    <label for="WorkPlan_planType"
                           id="WorkPlan_planType_title">任务类别:</label>
                </td>
                <td>
                    <@selectPlanType name="planType" optionEnumeTypeId = "WORK_PLAN_Type" defaultVale="${planType}"></@selectPlanType>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlan_isPerformance"
                           id="WorkPlan_isPerformance_title">纳入绩效考核:</label>
                </td>

                <td colspan="3">
                <#if workPlanRadio?has_content>
                    <#list workPlanRadio as list>
                        <#if isPerformance?has_content&&isPerformance == list.enumId>
                            <input type="radio" value="${list.enumId}" id="WorkPlan_isPerformance"
                                   name="isPerformance" checked>${list.description}
                        <#elseif "WP_N" == list.enumId>
                            <input type="radio" value="${list.enumId}" id="WorkPlan_isPerformance"
                                   name="isPerformance" checked>${list.description}
                        <#else >
                            <input type="radio" value="${list.enumId}" id="WorkPlan_isPerformance"
                                   name="isPerformance">${list.description}
                        </#if>
                    </#list>
                </#if>
                </td>
            </tr>

            <tr>
                <td class="label">
                    <label for="WorkPlan_startTime"
                           id="WorkPlan_startTime_title"><span class="requiredAsterisk">*</span>执行日期:</label>
                </td>
                <td class="jqv">
                <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                value="${startTime?default('')}" size="25" maxlength="30" id="WorkPlan_startTime" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'WorkPlan_completeTime\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="WorkPlan_completeTime"
                           id="WorkPlan_completeTime_title"><span class="requiredAsterisk">*</span>完成日期:</label>
                </td>
                <td class="jqv">
                <@htmlTemplate.renderDateTimeField name="completeTime" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                value="${completeTime?default('')}" size="25" maxlength="30" id="WorkPlan_completeTime" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'WorkPlan_startTime\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlan_planPerson"
                           id="WorkPlan_planPerson_title">安 排 人:</label>
                </td>
                <td>
                <#assign staffId = context.userLogin.partyId>
                <#assign  departmentMember = delegator.findByAnd("DepartmentMember", {"partyId" : staffId})[0]>

                    <select id="WorkPlan_planPerson" name="planPerson">

                    <#if departmentMember?has_content>
                        <#assign departmentId = departmentMember.departmentId?default('')>
                        <#assign groupName = departmentMember.groupName?default('')>
                        <#assign fullName = departmentMember.fullName?default('')>
                        <#if planPerson?has_content&&planPerson == departmentId>
                            <option value="${departmentId}"
                                    selected>${groupName}</option>
                        <#else >
                            <option value="${departmentId}">${groupName}</option>
                        </#if>
                        <#if planPerson?has_content&&planPerson == staffId>
                            <option value="${staffId}"
                                    selected>${fullName}</option>
                        <#else >
                            <option value="${staffId}">${fullName}</option>
                        </#if>
                    </#if>
                    </select>
                    <input type="hidden" name="departmentId" value="${departmentId?default('')}">
                </td>
                <td class="label">
                    <label for="WorkPlan_importanceDegree"
                           id="MemoAddForm_importanceDegree_title">重要程度:</label>
                </td>
                <td>
                    <select id="WorkPlan_importanceDegree" name="importanceDegree">
                    <#if workPlanDegree?has_content>
                        <#list workPlanDegree as list>
                            <#if importanceDegree?has_content&&importanceDegree==list.enumId>
                                <option value="${list.enumId}" selected>${list.description}</option>
                            <#elseif list.sequenceId == '02'>
                                <option value="${list.enumId}" selected>${list.description}</option>
                            <#else >
                                <option value="${list.enumId}">${list.description}</option>
                            </#if>
                        </#list>
                    </#if>

                    </select>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlan_difficultyDegree"
                           id="WorkReport_difficultyDegree_title">难度程度:</label>
                </td>
                <td>
                    <select id="WorkPlan_difficultyDegree" name="difficultyDegree">
                    <#if workPlanDegree?has_content>
                        <#list workPlanDegree as list>
                            <#if difficultyDegree?has_content&&difficultyDegree==list.enumId>
                                <option value="${list.enumId}" selected>${list.description}</option>
                            <#elseif list.sequenceId == '02'>
                                <option value="${list.enumId}" selected>${list.description}</option>
                            <#else >
                                <option value="${list.enumId}">${list.description}</option>
                            </#if>

                        </#list>
                    </#if>
                    </select>
                </td>

                <td class="label">
                    <label for="WorkPlan_noticePerson"
                           id="WorkPlan_noticePerson_title">新反馈通知:</label>
                </td>
                <td>
                <#if noticePerson?has_content&&noticePerson=='planPerson'>
                    <input type="checkbox" size="25" id="WorkPlan_noticePerson" name="noticePerson"
                           value="planPerson"
                           checked>安排人
                    <input type="checkbox" size="25" name="noticePerson" value="leader">项目主管<span class="tooltip">(新反馈通过Email通知)</span>
                <#elseif noticePerson?has_content&&noticePerson=='planPerson,leader'>
                    <input type="checkbox" size="25" id="WorkPlan_noticePerson" name="noticePerson"
                           value="planPerson"
                           checked>安排人
                    <input type="checkbox" size="25" name="noticePerson" value="leader" checked>项目主管<span
                        class="tooltip">(新反馈通过Email通知)</span>
                <#elseif noticePerson?has_content&&noticePerson=='leader'>
                    <input type="checkbox" size="25" id="WorkPlan_noticePerson" name="noticePerson"
                           value="planPerson">安排人
                    <input type="checkbox" size="25" name="noticePerson" value="leader" checked>项目主管<span
                        class="tooltip">(新反馈通过Email通知)</span>
                <#else >
                    <input type="checkbox" size="25" id="WorkPlan_noticePerson" name="noticePerson"
                           value="planPerson">安排人
                    <input type="checkbox" size="25" name="noticePerson" value="leader">项目主管<span
                        class="tooltip">(新反馈通过Email通知)</span>
                </#if>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlan_k"
                           id="WorkPlan_k_title">可查看人员:</label>
                </td>
                <td colspan="3">
                    <select name="canSeePerson" multiple="multiple" style="width:40%;">
                    <#if canSeePersonList?has_content>
                        <#list canSeePersonList as list>
                            <#if canSeePerson?has_content>
                                <#if canSeePerson?contains(list.enumId)>
                                    <option value="${list.enumId}" selected>${list.description}</option>
                                <#else >
                                    <option value="${list.enumId}">${list.description}</option>
                                </#if>
                            <#elseif list.enumId == 'WP_CS_ALL'>
                                <option value="${list.enumId}" selected>${list.description}</option>
                            <#else >
                                <option value="${list.enumId}">${list.description}</option>
                            </#if>
                        </#list>
                    </#if>
                    </select>
                    <span class="tooltip">Shift + 单击鼠标左键（连续多选） Ctrl + 单击鼠标左键（随机多选）</span>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlan_planDescription"
                           id="WorkPlan_planDescription_title"><span class="requiredAsterisk">*</span>描述:</label>
                </td>
                <td colspan="3" class="jqv">
                    <textarea name="planDescription"
                              style="width:100%;" maxlength="125" class="validate[required]">${planDescription?default('')}</textarea>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlan_executor"
                           id="WorkPlan_executor_title"><span class="requiredAsterisk">*</span>执行人姓名:</label>
                </td>
                <td>
                    <div id="executorOptions" style="display: none" class="jqv">
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
                            var title = $("#WorkPlan_title").val();
                            var startTime = $("#WorkPlan_startTime").val();
                            var completeTime = $("#WorkPlan_completeTime").val();
                            var executorArray
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
                    </script>
                <@selectStaff id="executor" name="executor" value="${executors}" onchange = "selectedProjectLeader" multiple=true required=true/>
                    <script type="text/javascript">
                        $("#executor").html($("#show_executor_value").children());
                    </script>
                </td>
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
                                                    value="${list.startTime?default('')}" size="25" maxlength="30" id="startTime_${list.personId}" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'startTime_${list.personId}\\')}'" shortDateInput=true timeDropdownParamName=""
                                                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                                </span>
                                        <br>
                                                <span>
                                                    <label class="label">结束：</label>
                                                    <@htmlTemplate.renderDateTimeField name="endTime_o1_${personWorkIndex}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                                                    value="${list.completeTime?default('')}" size="25" maxlength="30" id="endTime_${list.personId}" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'endTime_${list.personId}\\')}'" shortDateInput=true timeDropdownParamName=""
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
            <tr>
                <td colspan="4">
                <#--里程碑-->
                    <@showMilestoneList workPlanId = workPlanId></@showMilestoneList>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <#if workPlan?has_content&&!copyWorkPlan?has_content>
        <div align="center">
            <a href="#" class="smallSubmit" onclick="$.workPlan.workPlanCreate('1')">修改</a>
            <a href="#" class="smallSubmit" onclick="$.workPlan.workPlanCreate('WP_STATUS_DISCARDED')">作废</a>
            <a href="#" class="smallSubmit" onclick="$.workPlan.deleteWorkPlan(${workPlanId})">删除</a>
        </div>
    <#else >
        <div align="center">
            <a href="#" class="smallSubmit" onclick="$.workPlan.workPlanCreate()">保存</a>
        </div>
    </#if>

</form>
