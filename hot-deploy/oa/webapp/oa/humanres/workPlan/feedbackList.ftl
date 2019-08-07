<#include "component://oa/webapp/oa/humanres/workPlan/feedbackListCommon.ftl"/>
<#if parameters.returnValue?has_content>
    <@feedbackList workPlanId="${parameters.workPlanId?default('')}" childWorkPlanId="${parameters.chilWorkPlanId?default('')}"
    partyId="${parameters.partyId?default('')}" value=parameters/>
</#if>