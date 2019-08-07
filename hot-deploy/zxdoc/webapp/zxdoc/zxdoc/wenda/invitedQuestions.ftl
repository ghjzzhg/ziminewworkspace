<style type="text/css">
    tr:hover button {
        display: block !important;
    }

    table tr td {
        height: 40px;
    }
    .btn-group.status-type label {
        margin-left: 10px !important;
    }
</style>

<#--查询问题-->
<script type="text/javascript">
    function searchQuestion(statusId) {
        location.href = "InvitedQuestions?iframe=true&statusId=" + statusId;
    }
</script>
<div class="portlet light">
    <div class="portlet-body">
        <div>
            <div class="btn-group status-type" data-toggle="buttons">
                <label class="btn blue active" onclick="searchQuestion('')">
                    <input type="radio" class="toggle"> 全部 </label>
                <#--<label class="btn blue" onclick="searchQuestion('QUESTION_STATUS_WAIT_AUDIT')">
                    <input type="radio" class="toggle"> 待审核 </label>-->
                <label class="btn blue" onclick="searchQuestion('QUESTION_STATUS_WAIT_ANSWER')">
                    <input type="radio" class="toggle"> 未解决 </label>
                <label class="btn blue" onclick="searchQuestion('QUESTION_STATUS_COMPLETE')">
                    <input type="radio" class="toggle"> 已解决 </label>
                <#--<label class="btn blue" onclick="searchQuestion('QUESTION_STATUS_REJECTED')">
                    <input type="radio" class="toggle">已驳回 </label>-->
            </div>
        </div>
        <div class="table-scrollable table-scrollable-borderless">
            <table class="table table-hover table-light">
            <#list answernum.keySet() as myKeys>
                <#if myKeys.rejected?has_content>
                    <tr>
                        <td colspan="3">
                            <a onclick="displayInside('${request.contextPath}/control/AnswerQuestion?questionId=${myKeys.questionId}')">${myKeys.questionOverview?default('')}</a>
                            <span class="label label-sm label-info ">${myKeys.statusId}</span>
                        </td>
                        <td>
                        ${myKeys.createdStamp}
                        </td>
                    </tr>
                    <tr>
                        <td style="white-space: nowrap">
                            ${myKeys.rejected?default('')}
                    </tr>
                <#else>
                    <tr>
                        <td colspan="3">
                            <a onclick="displayInside('${request.contextPath}/control/AnswerQuestion?questionId=${myKeys.questionId}')">${myKeys.questionOverview?default('')}</a>
                            <span class="label label-sm label-info ">
                            <#if "QUESTION_STATUS_WAIT_ANSWER" == myKeys.statusId>未解决<#elseif "QUESTION_STATUS_COMPLETE" == myKeys.statusId>已解决<#elseif "QUESTION_STATUS_DELETE" == myKeys.statusId>已删除</#if>
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <td style="white-space: nowrap">
                        ${myKeys.createdStamp?default('')?string("yyyy-MM-dd HH:mm:ss")}
                        </td>
                        <td style="white-space: nowrap;width:150px"> 他人回答:${answernum.get(myKeys)?default('')}</td>
                        <td style="white-space: nowrap;width:150px"> ${myKeys.browseNum?default('0')}人看过</td>
                        <td style="white-space: nowrap;width:150px"><i class="fa fa-tag font-blue"></i>
                            <#list search.get(myKeys) as list2>
                                <#list description as list>
                                    <#if list2.questionType?has_content&&list2.questionType=list.enumId>
                                    ${list.description}
                                    </#if>
                                </#list>
                                <#if list2.isStandard="N">
                                ${list2.questionType?default('')}
                                </#if>
                            </#list>
                        </td>
                    </tr>
                </#if>
            </#list>
            </table>
        </div>
    </div>
</div>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->



