<#include "component://oa/webapp/oa/humanres/workPlan/feedbackListCommonCopy.ftl"/>
<#include "component://oa/webapp/oa/humanres/meetingNotice/signInRecord.ftl"/>
<#if parameters.returnValue?has_content>
<#assign noticeId = parameters.noticeId?default('')>
<#assign type = parameters.type?default('')>
<#--<#assign childFeedbackId = parameters.childFeedbackId?default('')>-->
<#--<#assign refreshId = parameters.refreshId?default('feedbackList')>-->
<#--<#assign type = parameters.feedbackMiddleType?default('')>-->
    <#--<@feedbackList  feedbackMiddleId=feedbackMiddleId feedbackPerson=feedbackPerson childFeedbackId=childFeedbackId-->
    <#--type=type value=parameters targetId=refreshId></@feedbackList>-->
    <@signInRecord noticeId=noticeId type=type param=parameters hasSignInList=hasSignInList statusList=statusList></@signInRecord>
    <#--<@signInRecord noticeId='${meetingNoticeId}' type='TblMeetingNotice' param=parameters hasSignInList=hasSignInList statusList=statusList></@signInRecord>-->
</#if>