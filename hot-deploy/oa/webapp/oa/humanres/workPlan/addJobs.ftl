<#macro JobForm workPlanId='' planPerson='' projectLeader='' partyId='' personWorkId='' startTime='' completeTime='' jobDescription='' startTime='' completeTime='' fullName=''>
    <script>
        function saveJobs() {
            $("#WorkPlanJobsForm").ajaxSubmit({
                beforeSubmit: function () {
                    var startTime = new Date('${startTime}');
                    console.log(startTime);
                },
                dataType: "json",
                success: function (data) {
                    showInfo(data.returnValue);
                    closeCurrentTab();
                    $.workPlan.searchWorPlan();
                },
                url: "saveJobs",
                type: 'post'
            });
        }
    </script>
    <#include "component://widget/templates/chooser.ftl"/>
<#if userLogin.partyId == planPerson || userLogin.partyId == projectLeader>
    <form class="basic-form" name="WorkPlanJobsForm" id="WorkPlanJobsForm">
        <input type="hidden" name="workPlanId" value="${workPlanId?default('')}">
        <input type="hidden" name="personWorkId" value="${personWorkId?default('')}">
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td class="label">
                    <label for="WorkPlanJobsForm_personId" id="WorkPlanJobsForm_personId_title">执行人:</label>
                </td>
                <td>
                    <#if !personWorkId?has_content>
                        <@chooser chooserType="LookupEmployee" name="personId" importability=true chooserValue="${personId?default('')}" />
                    <#--<@htmlTemplate.lookupField value="${partyId}" formName="WorkPlanJobsForm" name="personId" id="partyId" fieldFormName="LookupStaff" position="center"/>-->
                    <script>
                        $("input[name='personId']").attr("class","validate[required] " + $("input[name='personId']").attr("class"));
                    </script>
                    <#else>
                        ${fullName?default('')}
                    </#if>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlanJobsForm_startTime" id="WorkPlanJobsForm_startTime_title">开始时间:</label>
                </td>
                <td class="jqv">
                    <#if !personWorkId?has_content>
                        <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH"
                        value="${startTime?default('')}" size="25" maxlength="30" id="WorkPlanJobsForm_startTime" dateType="" shortDateInput=true timeDropdownParamName=""
                        defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                        timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <script>
                            $("input[name='startTime']").attr("class","validate[required] " + $("input[name='personId']").attr("class"));
                        </script>
                    <#else>
                        ${startTime?default('')}
                    </#if>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlanSearchForm_title" id="MemoAddForm_title_title">结束时间:</label>
                </td>
                <td class="jqv">
                    <#if !personWorkId?has_content>
                        <@htmlTemplate.renderDateTimeField name="completeTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH"
                        value="${completeTime?default('')}" size="25" maxlength="30" id="WorkPlanJobsForm_completeTime" dateType="" shortDateInput=true timeDropdownParamName=""
                        defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                        timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <script>
                            $("input[name='completeTime']").attr("class","validate[required] " + $("input[name='personId']").attr("class"));
                        </script>
                    <#else>
                        ${completeTime?default('')}
                    </#if>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlanSearchForm_title" id="MemoAddForm_title_title">任务描述:</label>
                </td>
                <td>
                    <#if !personWorkId?has_content>
                        <textarea name="jobDescription" class="validate[required]">${jobDescription?default('')}</textarea>
                    <#else>
                        <span id="jobDescription"/>
                        <script>
                            $("#jobDescription").html("${jobDescription?default('')}");
                        </script>
                    </#if>
                </td>
            </tr>
            <#if !personWorkId?has_content>
                <tr>
                    <td>
                        <a href="#" class="smallSubmit" onclick="saveJobs()">分配</a>
                    </td>
                </tr>
            </#if>
        </table>
    </form>
<#else>
    <form class="basic-form" name="WorkPlanJobsForm" id="WorkPlanJobsForm">
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td class="label">
                    <label for="WorkPlanJobsForm_personId" id="WorkPlanJobsForm_personId_title">执行人:</label>
                </td>
                <td>
                    <#assign executorGen = delegator.findOne("Person",{"partyId":partyId},false)>
                    ${executorGen.fullName}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlanJobsForm_startTime" id="WorkPlanJobsForm_startTime_title">开始时间:</label>
                </td>
                <td>
                    ${startTime}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlanSearchForm_title" id="MemoAddForm_title_title">结束时间:</label>
                </td>
                <td>
                    ${completeTime}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlanSearchForm_title" id="MemoAddForm_title_title">任务描述:</label>
                </td>
                <td>
                    ${jobDescription?default('')}
                </td>
            </tr>
        </table>
    </form>
</#if>

</#macro>
<#assign workPlanId = parameters.workPlanId?default('')>
<#assign partyId = parameters.partyId?default('')>
<#assign planPerson = parameters.planPerson?default('')>
<#assign projectLeader = parameters.projectLeader?default('')>
<#assign startTime = parameters.startTime?default('')>
<#assign completeTime = parameters.completeTime?default('')>
<#if parameters.personWorkId?has_content>
    <#assign personWorkId = parameters.personWorkId>
    <#assign personWork = delegator.findOne("TblPersonWork",{"personWorkId":personWorkId},false)>
    <#if personWork.startTime?has_content>
        <#assign startTime = personWork.startTime?string("yyyy-MM-dd")>
        <#assign completeTime = personWork.completeTime?string("yyyy-MM-dd")>
    </#if>
    <#assign jobDescription = personWork.jobDescription?default('')>
</#if>
<#if parameters.partyId?has_content>
    <#assign partyId = parameters.partyId>
    <#assign Person = delegator.findOne("Person",{"partyId":partyId},false)>
    <#if Person.fullName?has_content>
        <#assign fullName = Person.fullName?default('')>
    </#if>
</#if>
<@JobForm workPlanId=workPlanId?default('') planPerson=planPerson projectLeader=projectLeader partyId=partyId?default('')
    personWorkId=personWorkId?default('') startTime=startTime?default('') completeTime=completeTime?default('')
jobDescription=jobDescription?default('') startTime=startTime completeTime=completeTime fullName=fullName?default('')></@JobForm>

