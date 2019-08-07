<#include "component://oa/webapp/oa/humanres/workPlan/feedbackListCommonCopy.ftl"/>
<#if parameters.returnValue?has_content>
<#assign feedbackMiddleId = parameters.feedbackMiddleId?default('')>
<#assign feedbackPerson = parameters.feedbackPerson?default('')>
<#assign childFeedbackId = parameters.childFeedbackId?default('')>
<#assign refreshId = parameters.refreshId?default('feedbackList')>
<#assign type = parameters.feedbackMiddleType?default('')>
    <@feedbackList  feedbackMiddleId=feedbackMiddleId feedbackPerson=feedbackPerson childFeedbackId=childFeedbackId
    type=type value=parameters targetId=refreshId></@feedbackList>
</#if>