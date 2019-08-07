<#macro getChildWorkPlan workPlanId>
    <#local childWorkPlanList = delegator.findByAnd("TblChildWorkPlan",
    {"workPlanId" : workPlanId}, null, false)>
    <div>
        <span><label class="label">附件：</label>《已转至工作计划重要事项跟进表》</span>
        <table class="basic-table">
            <tr class="header-row-2">
                <td width="5%">序号</td>
                <td width="40%">简要标题/跟进人/时间要求</td>
                <td>明细要求</td>
            </tr>
            <#if childWorkPlanList?has_content>
            <#list childWorkPlanList as plan>
            <tbody>
                <tr>
                    <td >
                    ${plan_index+1}
                    </td>
                    <td>
                        <table>
                            <tr>
                                <td class="label">子标题:</td>
                                <td ${plan.title?default('')}></td>
                            </tr>
                            <tr>
                                <#local operator = delegator.findOne("Person",
                                {"partyId" : plan.operatorId?default('')},false)>
                                <td class="label">跟进人:</td>
                                <td>${operator.fullName?default('')}</td>
                            </tr>
                            <tr>
                                <td class="label">限时间:</td>
                                <td>${plan.startTime?string("yyyy-MM-dd hh:MM:ss")}<br>
                                至<br>
                                ${plan.completeTime?string("yyyy-MM-dd hh:MM:ss")}</td>
                            </tr>
                        </table>
                    </td>
                    <td >
                    ${plan.description?default('')}
                    </td>
                </tr>
            </tbody>
            </#list>

            </#if>
        </table>
    </div>
</#macro>