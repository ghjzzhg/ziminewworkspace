<#include "component://oa/webapp/oa/humanres/workPlan/foldersHeader.ftl"/>
<#macro ChildWorkPlanList valueList curPerson type personId=''>
<script type="text/javascript">
    function deleteChildPlan(childWorkPlanId,workPlanId,curPartyId,personId) {
        $.ajax({
            type: 'post',
            url: "deleteChildPlan",
            data:{childWorkPlanId:childWorkPlanId,workPlanId:workPlanId,curPartyId:curPartyId,personId:personId},
            async: true,
            dataType: 'html',
            success: function (data) {
                var valueHtml = $("<code></code>").append($(data));
                var refreshChildWorkPlanList = $("#ChildWorkPlanList_div",valueHtml).attr("class");
                $("#"+refreshChildWorkPlanList).html(data);
                $.workPlan.searchWorPlan();
            }
        });
    }
</script>
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">
                <#if type=='childWorkPlan'>
                    <#assign curPersonGen = delegator.findOne("Person",{"partyId":curPerson},false)>
                    <b style="color:green;"><#if curPersonGen?has_content>${curPersonGen.fullName?default('')}</#if></b>的子计划：
                <#else >
                    子计划列表：
                </#if>
            </li>
        </ul>
        <br class="clear">
    </div>
    <div>
        <form class="basic-form">
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tr class="header-row-2">
                    <td width="35%">子计划标题</td>
                    <#if curPerson?has_content>
                        <td width="5%">安排人</td>
                    <#else >
                        <td width="5%">执行人</td>
                    </#if>
                    <td width="30%">要求时间</td>
                    <td width="20%">最后反馈</td>
                    <td width="10%">状态</td>
                    <td>操作</td>
                </tr>
                <#list valueList as list>
                    <tr>
                        <td>
                        ${list.title?default('')}
                        </td>
                        <td>
                            <#if curPerson?has_content>
                                <#assign planPerson = delegator.findOne("Person"{"partyId" : list.planPerson},false)>
                                ${planPerson.fullName?default('')}
                            <#else >
                                <#assign planPerson = delegator.findOne("Person"{"partyId" : list.operatorId},false)>
                            ${planPerson.fullName?default('')}
                            </#if>
                        </td>
                        <td>
                            开始时间：${list.startTime?string("yyyy-MM-dd HH:mm:ss")}<br>
                            结束时间：${list.completeTime?string("yyyy-MM-dd HH:mm:ss")}
                        </td>
                        <td>
                            <#if list.lastFeedback?has_content>
                                <#assign feedback = delegator.findOne("TblFeedback",{"feedbackId":list.lastFeedback},false)>
                                <#assign feedbackPerson = delegator.findOne("Person",{"partyId":feedback.feedbackPerson},false)>
                                ${feedbackPerson.fullName}(${feedback.feedbackTime?string("yyyy-MM-dd HH:mm:ss")})
                            </#if>
                        </td>
                        <td>
                            <@WorkPlanProgressbar id="childWorkPlanListProgressbar_${type}_${list.childWorkPlanId}" value=list.childWorkPlanStatus?default(0) />
                        </td>
                        <td>
                            <#if userLogin.partyId == personId || userLogin.partyId == curPerson>
                                <a class="icon-trash" href="#" title="删除" onclick="deleteChildPlan('${list.childWorkPlanId}','${list.workPlanId}','${curPerson}','${personId}')"></a>&nbsp;&nbsp;
                            </#if>
                        </td>
                    </tr>
                </#list>
            </table>
        </form>
    </div>
</#macro>

<#if parameters.data?has_content&&parameters.data.curPartyId?has_content&&parameters.data.curPartyId != ''>
    <#assign childWorkPlanListType = "childWorkPlan">
    <#assign curPartyId = parameters.data.curPartyId>
    <#assign personId = parameters.data.personId>
    <div id="ChildWorkPlanList_div" class="executorChildWorkPlanList"></div>
<#else >
    <#assign childWorkPlanListType = "workPlan">
<div id="ChildWorkPlanList_div" class="workPlanChildWorkPlanList"></div>
</#if>
<#if parameters.data?has_content &&  parameters.data.childWorkPlanList?has_content>
    <@ChildWorkPlanList valueList=parameters.data.childWorkPlanList curPerson=curPartyId?default('') type=childWorkPlanListType personId=personId></@ChildWorkPlanList>
</#if>
