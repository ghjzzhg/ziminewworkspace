<#macro getPersonWorkList workPlanId>
    <#local personWorkList = delegator.findByAnd("TblPersonWork",
    {"workPlanId" : workPlanId}, null, false)>
    <div>
        <#--<span><label class="label">附件：</label>《已转至工作计划重要事项跟进表》</span>-->
        <table class="basic-table">
            <tr class="header-row-2">
                <td width="5%">序号</td>
                <td width="40%">执行人/时间要求</td>
                <td>个人任务描述</td>
            </tr>
            <#if personWorkList?has_content>
            <#list personWorkList as plan>
            <tbody>
                <tr>
                    <td >
                    ${plan_index+1}
                    </td>
                    <td>
                        <table>
                            <tr>
                                <#local executor = delegator.findOne("Person",
                                {"partyId" : plan.personId?default('')},false)>
                                <td class="label">执行人:</td>
                                <td>${executor.fullName?default('')}</td>
                            </tr>
                            <tr>
                                <td class="label">限时间:</td>
                                <td>${plan.startTime?string("yyyy-MM-dd")} - ${plan.completeTime?string("yyyy-MM-dd")}</td>
                            </tr>
                        </table>
                    </td>
                    <td >
                    ${plan.jobDescription?default('')}
                    </td>
                </tr>
            </tbody>
            </#list>

            </#if>
        </table>
    </div>
</#macro>