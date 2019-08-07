<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="description"]', {
            allowFileManager: true
        });
    });

    function saveChildPlan(personId) {
        $('textarea[name="description"]').val(template.html());
        var options = {
            beforeSubmit: function () {
                return $('#childPlanForm').validationEngine('validate');
            },
            dataType: "html",
            success: function (data) {
                closeCurrentTab();
                if(childWorkPlanId != null){
                    $("#childPlanForm").html(data);
                }else{
                    var valueHtml = $("<code></code>").append($(data));
                    var refreshChildWorkPlanList = $("#ChildWorkPlanList_div",valueHtml).attr("class");
                    $("#"+refreshChildWorkPlanList).html(data);
                }
                $.workPlan.searchWorPlan();
            },
            data:{personId:personId},
            url: "saveChildPlan",
            type: 'post'
        };
        var childWorkPlanId = $("input[name='childWorkPlanId']").val();
        if(childWorkPlanId != null){
            options.url = "updateChildPlan";
        }
        $("#childPlanForm").ajaxSubmit(options);
    }


</script>
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://oa/webapp/oa/humanres/workPlan/foldersHeader.ftl"/>
<form name="childPlanForm" id="childPlanForm" class="basic-form">
    <input type="hidden" name="workPlanId" value="${parameters.workPlan.workPlanId?default('')}">
    <input type="hidden" name="curPartyId" value="${parameters.workPlan.curPartyId?default('')}">
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td class="label">
                    <label>安排部门:</label>
                </td>
                <td>
                ${parameters.workPlan.groupName}
                </td>

                <td class="label">
                    <label>预定时间:</label>
                </td>
                <td>
                ${parameters.workPlan.startTime}
                </td>
                <td class="label">
                    <label>总体进度:</label></td>
                <td >
                     <span style="width:40px;">
                     <@WorkPlanProgressbar id="workPlanProgressbar_aaaa" value= parameters.workPlan.workPlanStatus />
                     </span>
               <#-- ${parameters.workPlan.wpsDescription}-->
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>重要级:</label>
                </td>
                <td>
                ${parameters.workPlan.idDescription}
                </td>
                <td class="label">
                    <label>难度:</label>
                </td>
                <td>
                ${parameters.workPlan.ddDescription}
                </td>
                <td class="label">
                    <label>安 排 人:</label>
                </td>
                <td>
                     ${parameters.workPlan.planPersonName}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>执 行 人:</label>
                </td>
                <td colspan="5">
                <#if parameters.workPlan.executorList?has_content>
                    <#list parameters.workPlan.executorList as executor>
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
                <td colspan="5">
                <#if parameters.workPlan.planDescription?has_content>
                    ${parameters.workPlan.planDescription}
                </#if>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <div>
    <#if parameters.workPlan.childPlanList?has_content>
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">子计划列表</li>

            </ul>
            <br class="clear">
        </div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr class="header-row-2">
                <td width="5%">执行人</td>
                <td width="30%">子计划标题</td>
                <td width="30%">要求时间</td>
                <td width="25%">最后反馈</td>
                <td width="10%">状态</td>
            </tr>
                <#list parameters.workPlan.childPlanList as childPlan>
                <tr>
                    <td>${childPlan.executorName?default(' ')}</td>
                    <td>${childPlan.title?default(' ')}</td>
                    <td>${childPlan.startTime?string("yyyy-MM-dd HH:mm")}~${childPlan.completeTime?string("yyyy-MM-dd HH:mm")}</td>
                    <#assign lastFeedback = delegator.findByAnd("ChildWorkPlanInfo",{"lastFeedback":childPlan.lastFeedback?default('')})>
                    <td><#if lastFeedback?has_content>${lastFeedback[0].feedbackPersonName?default('')}(${lastFeedback[0].feedbackTime?string('yyyy-MM-dd HH:mm')})<#else>暂无反馈</#if></td>
                    <td>
                    ${childPlan.status?default(' ')}
                        <@WorkPlanProgressbar id="ChildWorkPlanProgressbar_${childPlan.childWorkPlanId?default('')}" value=childPlan.childWorkPlanStatus?default(0) />
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>
    </#if>

        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">添加子计划</li>

            </ul>
            <br class="clear">
        </div>

    <#if parameters.workPlan.childWorkPlan?has_content>
        <#assign childWorkPlan =  parameters.workPlan.childWorkPlan>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <input type="hidden" name="childWorkPlanId" value="${childWorkPlan.childWorkPlanId}">
            <tbody>
            <tr>
                <td class="label">
                    <label for="addChildPlan_title"
                           id="MemoAddForm_title_title"><b style="color: red">*</b>子计划标题:</label></td>
                <td class="jqv">
                    <input type="text" size="25" id="addChildPlan_title" class="validate[required,custom[onlyLetterNumberChinese],maxSize[20]]" name="title" value="${childWorkPlan.title}">
                </td>

                <td class="label">
                    <label for="addChildPlan_personId"
                           id="addChildPlan_personId_title"><b style="color: red">*</b>跟进人:</label></td>
                <td>
                    <#assign updateExecutor = delegator.findOne("Person",{"partyId":childWorkPlan.operatorId},false)>
                    <input type="hidden" name="operatorId" value="${updateExecutor.partyId}">
                     ${updateExecutor.fullName}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="addChildPlan_startTime"
                           id="addChildPlan_startTime_title"><b style="color: red">*</b>开始日期:</label></td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="" alert=""
                    title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${childWorkPlan.startTime?default('')}" size="25"
                    maxlength="30" id="addChildPlan_startTime" dateType="" shortDateInput=false timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                    hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour=""
                    ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="addChildPlan_completeTime"
                           id="addChildPlan_completeTime_title"><b style="color: red">*</b>完成日期:</label></td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="completeTime" event="" action="" className="" alert=""
                title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${childWorkPlan.completeTime?default('')}" size="25"
                maxlength="30" id="addChildPlan_completeTime" dateType="" shortDateInput=false timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour=""
                ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="addChildPlan_description"
                           id="addChildPlan_description_title">详细说明：</label>
                </td>
                <td colspan="3">
                    <textarea name="description"
                              id="addChildPlan_description">${childWorkPlan.description?default('')}</textarea>
                </td>
            </tr>
            </tbody>
        </table>
        <div align="center">
            <a href="#" class="smallSubmit" onclick="saveChildPlan()">提交</a>
            <a href="#" class="smallSubmit" onclick="deleteChildPlan(${childWorkPlan.childWorkPlanId})">删除</a>
        </div>
    <#else >
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td class="label">
                    <label for="addChildPlan_title"
                           id="MemoAddForm_title_title"><b style="color: red">*</b>子计划标题:</label></td>
                <td class="jqv">
                    <input type="text" size="25" id="addChildPlan_title"  class="validate[required,custom[onlyLetterNumberChinese],maxSize[20]]" name="title" value="">
                </td>

                <td class="label">
                    <label for="addChildPlan_personId"
                           id="addChildPlan_personId_title"><b style="color: red">*</b>跟进人:</label></td>
                <td>
                    <#if parameters.workPlan.curPartyId?has_content>
                        <#assign curPerson = delegator.findOne("Person",{"partyId":parameters.workPlan.curPartyId},false)>
                        <input type="hidden" name="operatorId" value="${curPerson.partyId}">
                        ${curPerson.fullName}
                    <#else >
                        <#assign executorPersonList = delegator.findByAnd("TblPersonWork",{"workPlanId" : parameters.workPlan.workPlanId},null,false)/>
                        <select name="operatorId">
                            <#list executorPersonList as list>
                                <#assign executorPersonGen = delegator.findOne("Person",{"partyId" : list.personId},false)>
                                <option value="${list.personId}">${executorPersonGen.fullName?default('')}</option>
                            </#list>
                        </select>
                    </#if>

                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="addChildPlan_startTime"
                           id="addChildPlan_startTime_title"><b style="color: red">*</b>开始日期:</label></td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="" alert=""
                    title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${parameters.workPlan.startTime}" size="25"
                    maxlength="30" id="addChildPlan_startTime" dateType="" shortDateInput=false timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                    hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour=""
                    ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="addChildPlan_completeTime"
                           id="addChildPlan_completeTime_title"><b style="color: red">*</b>完成日期:</label></td>
                <td>
                <#--<input type="date" size="25" id="addChildPlan_c">-->
                    <@htmlTemplate.renderDateTimeField name="completeTime" event="" action="" className="" alert=""
                title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${parameters.workPlan.completeTime}" size="25"
                maxlength="30" id="addChildPlan_completeTime" dateType="" shortDateInput=false timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour=""
                ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="addChildPlan_description"
                           id="addChildPlan_description_title">详细说明：</label>
                </td>
                <td colspan="3">
                    <textarea name="description" id="addChildPlan_description"></textarea>
                </td>
            </tr>
            </tbody>
        </table>
        <div align="center">
            <a href="#" class="smallSubmit" onclick="saveChildPlan('${parameters.workPlan.personId}')">提交</a>
        </div>
    </#if>
    </div>
</form>