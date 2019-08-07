<#if parameters.startTime?has_content>
    <#assign startTime = parameters.startTime>
    <#assign endTime = parameters.endTime>
    <#assign year = startTime?substring(0,4)?number>
    <#assign month = startTime?substring(5,7)?number>
    <#assign searStartTime = startTime?date>
    <#assign searEndTime = endTime?date>
    <#assign searEndTimeStr = endTime?substring(8)?number>
    <#assign day = endTime?substring(8,10)?number>
</#if>
<style type="text/css">
    form table tr td {
        border-right: 1px solid #DDD;
    }
</style>
<script type="text/javascript">
    var strWeek;
    <#if parameters.startTime?has_content>
    var str = unescapeHtmlText('${parameters.startTime}');
    var strYearAndMonth = str.substr(0, 7);
    <#else >
    var strYearAndMonth = '${.now?string("yyyy-MM")}';
    </#if>
    function getWeekByDate(str) {
        if (str < 10) {
            var strDate = strYearAndMonth + "-0" + str;
        } else {
            var strDate = strYearAndMonth + "-" + str;
        }
        var date = new Date(strDate);
        var weekOfDay = date.getDay();
        switch (weekOfDay) {
            case 0:
                strWeek = '日';
                break;
            case 1:
                strWeek = "一";
                break;
            case 2:
                strWeek = "二";
                break;
            case 3:
                strWeek = "三";
                break;
            case 4:
                strWeek = "四";
                break;
            case 5:
                strWeek = "五";
                break;
            case 6:
                strWeek = "六";
                break;
        }
        return strWeek;
    }
</script>
<style type="text/css">
    .white_nowrap {
        white-space: nowrap;
    }

</style>
<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://oa/webapp/oa/humanres/workPlan/foldersHeader.ftl"/>
<#if parameters.workPlanList?has_content>
    <#assign viewIndex = parameters.viewIndex>
    <#assign highIndex = parameters.highIndex>
    <#assign totalCount = parameters.totalCount>
    <#assign viewSize = parameters.viewSize>
    <#assign lowIndex = parameters.lowIndex>

    <#assign commonUrl = "searchWorkPlan"/>
    <#assign sortParam = "workPlanId=" + parameters.workPlanId?default('')+"&title="+parameters.title?default("")+"&planType="+parameters.planType?default("")
    +"&member="+parameters.member?default("")+"&startTime="+parameters.startTime?default("")+"&planDescription="+parameters.planDescription?default("")
    +"&departmentId="+parameters.departmentId?default("")+"&planPerson="+parameters.planPerson?default("")+"&workPlanStatus="+parameters.workPlanStatus?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<div align=center">
    <table class="basic-table" align="center">
        <tbody>
        <#--<tr> ${startTime}~~${endTime}~~${searEndTimeStr}</tr>-->
        <tr class="header-row-2">
            <td width="200px">待处理事宜</td>
            <td>安排人<br><span style="color: #0000CC">执行人</span></td>
            <td>任务明细</td>
            <td>安排日期<br><span style="color: #0000CC">反馈日期</span></td>
        <#list 1..31 as dateList>
            <td>${dateList}<br>
                <span name="date_span_${dateList}"></span>
            </td>
        </#list>
            <td colspan="2">状态</td>
            <td>绩效</td>
            <script type="text/javascript">
                for (var i = 1; i < 32; i++) {
                    $("span[name='date_span_" + i + "']").text(getWeekByDate(i));
                }
            </script>
        </tr>
        <#if parameters.workPlanList?has_content>
            <#list parameters.workPlanList as plan>

            <tr style="background: yellowgreen">
            <#--总计划-->
                <#assign size = plan.executorAndChildPlanList?size><#--执行人数-->
                <#list plan.executorAndChildPlanList as executor>
                    <#if executor.childWorkPlanList?has_content>
                        <#assign size = size + 1>
                    </#if>
                </#list>

                <td rowspan="${size+1}" style="background:whitesmoke;">
                    <div style="width: 200px;">
                        <div>
                            <span class="label" style="text-align: right">任务排序:</span>
                        </div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div>
                            <span class="label" style="text-align: right">类别:</span>${plan.ptDescription?default('无类别')}
                        </div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div>
                            <span class="label">任务名称:</span><b style="color: red;cursor:pointer"
                                                               onclick="$.workPlan.feedbackWorkPlan(${plan.workPlanId},null,null)">${plan.title}</b>
                        </div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div>
                            <span class="label">安排部门:</span>${plan.groupName?default('')}
                            (${plan.startTime?string("yyyy-MM-dd")}～${plan.completeTime?string("yyyy-MM-dd")})
                        </div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div>
                            <span class="label">总体进度:</span>
                            <#if plan.totalPersonWorkStatus?has_content>
                                <span style="width:40px;">
                                    <@WorkPlanProgressbar id="totalPersonWorkStatus_${plan.workPlanId}" value= plan.totalPersonWorkStatus />
                                </span>

                            </#if>
                        </div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div><span
                                class="label">重要级:</span><#if plan.importanceDegree?has_content>${plan.idDescription}<#else ></#if>
                        </div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div><span class="label">难度:</span>${plan.ddDescription?default('')}</div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div><span class="label">纳入绩效考核:</span>${plan.isDescription?default('')}</div>
                        <div style="background: gainsboro;height: 1px;width: 100%"></div
                        <div style="background: gainsboro;height: 1px;width: 100%"></div>
                        <div>
                            <span class="label">最后反馈:</span><b style="color: red;cursor:pointer"
                                                               onclick="$.workPlan.feedbackWorkPlan(${plan.workPlanId},null,null)"><#if plan.feedbackTime?has_content>${plan.feedbackPersonName?default('')}
                            /${plan.feedbackTime?string('yyyy-MM-dd HH:mm:ss')}<#else>暂无反馈</#if></b>
                        </div>
                    </div>

                </td>

                <td class="white_nowrap">
                ${plan.planPersonName}<#--安排人-->
                </td>
                <td>
                    <#--安排人可能是一个部门，部门的任务分配权限没做-->
                    <#if userLogin.partyId == plan.projectLeader || userLogin.partyId == plan.personId>
                        <a href="#" onclick="$.workPlan.addJobs('${plan.workPlanId}','','','${plan.personId}','${plan.projectLeader}','${plan.startTime}','${plan.completeTime}')">任务分配</a>
                    </#if>
                </td>
                <td class="white_nowrap">${plan.startTime?string("yyyy-MM-dd")}</td><#--安排时间-->
                <#assign startTimeDate = plan.startTime?date>
                <#assign startTimeSlipYear = plan.startTimeSlip.year?number>
                <#assign startTimeSlipMonth = plan.startTimeSlip.month?number>
                <#assign startTimeSlipDay = plan.startTimeSlip.day?number>
                <#assign completeTimeDate = plan.completeTime?date>
                <#assign completeTimeSlipYear = plan.completeTimeSlip.year?number>
                <#assign completeTimeSlipMonth = plan.completeTimeSlip.month?number>
                <#assign completeTimeSlipDay = plan.completeTimeSlip.day?number>
                <#list 1..31 as day>
                    <#if startTimeSlipMonth == completeTimeSlipMonth && startTimeSlipYear ==  completeTimeSlipYear>
                        <#if (day>=startTimeSlipDay&&day<=completeTimeSlipDay)>
                            <td style="padding-left: 0;padding-right: 0;">
                                <div style="background: green;height: 20px"></div>
                            </td>
                        <#else>
                            <td></td>
                        </#if>
                    <#elseif !(startTimeSlipMonth = completeTimeSlipMonth && startTimeSlipYear = completeTimeSlipYear)>
                        <#if month <= startTimeSlipMonth>
                            <#if (day >= startTimeSlipDay && day <= searEndTimeStr)>
                                <td style="padding-left: 0;padding-right: 0;">
                                    <div style="background: green;height: 20px"></div>
                                </td>
                            <#else>
                                <td></td>
                            </#if>
                        <#elseif (month > startTimeSlipMonth && month < completeTimeSlipMonth)>
                            <td style="padding-left: 0;padding-right: 0;">
                                <div style="background: green;height: 20px"></div>
                            </td>
                        <#elseif (month >= completeTimeSlipMonth)>
                            <#if (day <= completeTimeSlipDay)>
                                <td style="padding-left: 0;padding-right: 0;">
                                    <div style="background: green;height: 20px"></div>
                                </td>
                            <#else>
                                <td></td>
                            </#if>
                        </#if>
                    <#elseif (!(startTimeSlipMonth = completeTimeSlipMonth) && !(startTimeSlipYear = completeTimeSlipYear))>
                        <#if (day >= startTimeSlipDay && day <= searEndTimeStr)>
                            <td style="padding-left: 0;padding-right: 0;">
                                <div style="background: green;height: 20px"></div>
                            </td>
                        <#else>
                            <td></td>
                        </#if>
                    </#if>
                    <#--<#if (startTimeDate<=searStartTime&&completeTimeDate>=searEndTime)>-->
                        <#--&lt;#&ndash;<td style="padding-left: 0;padding-right: 0;">&ndash;&gt;-->
                            <#--&lt;#&ndash;<div style="background: green;height: 20px"></div>&ndash;&gt;-->
                        <#--&lt;#&ndash;</td>&ndash;&gt;-->
                        <#--<#if (day>=startTimeSlipDay&&day<=completeTimeSlipDay)>-->
                            <#--<td style="padding-left: 0;padding-right: 0;">-->
                                <#--<div style="background: green;height: 20px"></div>-->
                            <#--</td>-->
                        <#--<#else >-->
                            <#--<td></td>-->
                        <#--</#if>-->
                    <#--<#elseif (startTimeDate>=searStartTime&&startTimeDate<=searEndTime&&completeTimeDate>=searStartTime&&completeTimeDate<=searEndTime)>-->
                        <#--<#if (day>=startTimeSlipDay&&day<=completeTimeSlipDay)>-->
                            <#--<td style="padding-left: 0;padding-right: 0;">-->
                                <#--<div style="background: green;height: 20px"></div>-->
                            <#--</td>-->
                        <#--<#else >-->
                            <#--<td></td>-->
                        <#--</#if>-->
                    <#--<#elseif (startTimeDate>=searStartTime&&startTimeDate<=searEndTime)>-->
                        <#--<#if (day>=startTimeSlipDay)>-->
                            <#--<td style="padding-left: 0;padding-right: 0;">-->
                                <#--<div style="background: green;height: 20px"></div>-->
                            <#--</td>-->
                        <#--<#else>-->
                            <#--<td></td>-->
                        <#--</#if>-->
                    <#--<#elseif (completeTimeDate>=searStartTime&&completeTimeDate<=searEndTime)>-->
                        <#--<#if (day<=completeTimeSlipDay)>-->
                            <#--<td style="padding-left: 0;padding-right: 0;">-->
                                <#--<div style="background: green;height: 20px"></div>-->
                            <#--</td>-->
                        <#--<#else >-->
                            <#--<td></td>-->
                        <#--</#if>-->
                    <#--<#else >-->
                        <#--<td></td>-->
                    <#--</#if>-->
                </#list>

            <td colspan="3" align="center">
                <#assign completeTime = plan.completeTime>
                <#assign completeTime = Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(completeTime)>
                <#assign nowDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
                <#assign intervalInDays = Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(completeTime,nowDateTime)>
                <#assign test = Static["org.ofbiz.base.util.UtilDateTime"].getInterval(completeTime,nowDateTime)>
                <#if plan.workPlanStatus != 100.0 && (intervalInDays > 0)>
                    <b style="color: red">已逾期${-(intervalInDays-1)}天</b></td>
                <#else>
                    <b style="color: red">剩余${-(intervalInDays-1)}天</b></td>
                </#if>

            </td>

            </tr>
                <#--执行人及相关工作进展信息-->
                <#assign executorAndChildPlanList = plan.executorAndChildPlanList>
                <#list executorAndChildPlanList as executor>
                    <#if executor.childWorkPlanList?has_content>
                        <#assign size = size + 1>
                    </#if>
                    <#assign executorPerson = delegator.findOne("Person",{"partyId" : executor.partyId},false)>
                    <#--<#assign executorJobs = executor.executorJobs>-->
                <tr <#if plan.projectLeader == executor.partyId>style="background-color: #1790C0;"</#if>>
                    <#--执行人-->
                    <td class="white_nowrap" <#if executor.childWorkPlanList?has_content>rowspan="2"</#if>>
                        <a href="#" style="color: #0000CC;"
                           onclick="$.workPlan.feedbackWorkPlan(${plan.workPlanId},${executor.partyId},' ')">${executorPerson.fullName}</a>
                    </td>
                    <#--任务信息-->
                    <td <#if executor.childWorkPlanList?has_content>rowspan="2"</#if>>
                        <#if executor.startTime?has_content>
                            <a id="ele2" href="#" onclick="$.workPlan.addJobs('${plan.workPlanId}','${executor.partyId}','${executor.personWorkId}','${plan.personId}','${plan.projectLeader}')">点击查看</a>
                        <#elseif userLogin.partyId == plan.personId || userLogin.partyId == plan.projectLeader>
                            <a href="#" onclick="$.workPlan.addJobs('${plan.workPlanId}','${executor.partyId}','${executor.personWorkId}','${plan.personId}','${plan.projectLeader}')">分配任务</a>
                        </#if>
                    </td>
                <#--执行人最后反馈时间-->
                    <td class="white_nowrap" <#if executor.childWorkPlanList?has_content>rowspan="2"</#if>>
                        <#if executor.lastFeedback?has_content>
                            <#assign lastFeedbackGen = delegator.findOne("TblFeedback",{"feedbackId":executor.lastFeedback},false)>
                            ${lastFeedbackGen.feedbackTime?string('yyyy-MM-dd')}
                        </#if>
                    </td>
                <#--时间范围-->
                    <#if executor.startTime?has_content && executor.completeTime?has_content>
                        <#assign maxDateStr = executor.completeTime?string("yyyy-MM-dd")>
                        <#assign minDateStr = executor.startTime?string("yyyy-MM-dd")>
                        <#assign maxDate = maxDateStr?date>
                        <#assign minDate = minDateStr?date>
                        <#assign  maxYear = maxDateStr?substring(0,4)?eval>
                        <#assign  minYear = minDateStr?substring(0,4)?eval>
                        <#assign  maxMonth = maxDateStr?substring(5,7)?eval>
                        <#assign  minMonth = minDateStr?substring(5,7)?eval>
                        <#assign  maxDay = maxDateStr?substring(8)?eval>
                        <#assign  minDay = minDateStr?substring(8)?eval>
                    </#if>
                    <#list 1..31 as day>
                        <#if !(executor.startTime?has_content && executor.completeTime?has_content)>
                            <td></td>
                        <#elseif (maxMonth = minMonth && maxYear = minYear)>
                            <#if (day >= minDay && day <= maxDay && maxMonth = month)>
                                <td style="padding-left: 0;padding-right: 0;">
                                    <div style="background: green;height: 20px"></div>
                                </td>
                            <#else>
                                <td></td>
                            </#if>
                        <#elseif !(maxMonth = minMonth && maxYear = minYear)>
                            <#if month <= minMonth>
                                <#if (day >= minDay && day <= searEndTimeStr)>
                                    <td style="padding-left: 0;padding-right: 0;">
                                        <div style="background: green;height: 20px"></div>
                                    </td>
                                <#else>
                                    <td></td>
                                </#if>
                            <#elseif (month > minMonth && month < maxMonth)>
                                <td style="padding-left: 0;padding-right: 0;">
                                    <div style="background: green;height: 20px"></div>
                                </td>
                            <#elseif (month >= maxMonth)>
                                <#if (day <= maxDay)>
                                    <td style="padding-left: 0;padding-right: 0;">
                                        <div style="background: green;height: 20px"></div>
                                    </td>
                                <#else>
                                    <td></td>
                                </#if>
                            </#if>

                            <#--<#if (day >= minDay && day <= searEndTimeStr)>-->
                                <#--<td style="padding-left: 0;padding-right: 0;">-->
                                    <#--<div style="background: green;height: 20px"></div>-->
                                <#--</td>-->
                            <#--<#else >-->
                                <#--<td></td>-->
                            <#--</#if>-->
                        <#--<#elseif (minDate >= searStartTime && maxDate <= searEndTime)>-->
                            <#--<#if (day >= minDay && day <= maxDay)>-->
                                <#--<td style="padding-left: 0;padding-right: 0;">-->
                                    <#--<div style="background: green;height: 20px"></div>-->
                                <#--</td>-->
                            <#--<#else>-->
                                <#--<td></td>-->
                            <#--</#if>-->
                        <#--<#elseif (minDate>=searStartTime&&minDate<=searEndTime&&maxDate>=searEndTime)>-->
                            <#--<#if (day >= minDay)>-->
                                <#--<td style="padding-left: 0;padding-right: 0;">-->
                                    <#--<div style="background: green;height: 20px"></div>-->
                                <#--</td>-->
                            <#--<#else>-->
                                <#--<td></td>-->
                            <#--</#if>-->
                        <#--<#elseif (maxDate<=searStartTime)>-->
                                <#--<td></td>-->
                        <#--<#elseif (minDate>=searEndTime)>-->
                            <#--<td></td>-->
                        </#if>
                    </#list>

                    <#if plan.workPlanStatus?has_content&&plan.workPlanStatus==-1>
                        <td <#if executor.childWorkPlanList?has_content>rowspan="2"</#if>>
                            作废
                        </td>
                        <td colspan="2" <#if executor.childWorkPlanList?has_content>rowspan="2"</#if>>
                            <#if plan.workPlanStatus?has_content>
                            <span style="width: 50px">
                                <@WorkPlanProgressbar id="workPlanProgressbar_${plan.workPlanId}" value= plan.workPlanStatus(0) />
                                                </span>
                            </#if>
                        </td>
                    <#else >
                        <td <#if executor.childWorkPlanList?has_content>rowspan="2"</#if>>
                            <#assign progressValue = 10>
                            <#if executor.personWorkStatus?has_content>
                                <div style="width: 50px">
                                    <@WorkPlanProgressbar id="workPlanProgressbar_${executor.personWorkId}" value=executor.personWorkStatus?default(0) />
                                </div>
                            </#if>

                        </td>
                        <td colspan="2" <#if executor.childWorkPlanList?has_content>rowspan="2"</#if>>
                            ${executor.performanceScore?default('')}
                        </td>
                    </#if>
                </tr>
                    <#if executor.childWorkPlanList?has_content>
                    <#assign childWorkPlanList = executor.childWorkPlanList>
                    <tr>
                        <td colspan="31" align="center">
                            <a href="#" style="color: red" onclick="showChildWorkPlan('${plan.workPlanId}','${executor.partyId}',$(this))">有(${childWorkPlanList?size})个子计划(<span id="childWorkPlan_clickValue_${plan.workPlanId}_${executor.partyId}">点击查看</span>)</a>
                            <script>
                                function showChildWorkPlan(workPlanId,id,obj){
                                    $("#display_childWorkPlan_" + workPlanId +"_" +id).css("display","");
                                    $("#childWorkPlan_clickValue_" + workPlanId +"_" +id).text("点击隐藏");
                                    $(obj).attr("onclick","hiddenChildWorkPlan('"+ workPlanId +"','"+ id +"',$(this))");
                                }
                                function hiddenChildWorkPlan(workPlanId,id,obj){
                                    $("#display_childWorkPlan_" +  + workPlanId +"_" +id).css("display","none");
                                    $("#childWorkPlan_clickValue_" +  + workPlanId +"_" +id).text("点击查看");
                                    $(obj).attr("onclick","showChildWorkPlan('"+ workPlanId +"','"+ id +"',$(this))");
                                }

                            </script>
                            <div style="display: none" id="display_childWorkPlan_${plan.workPlanId}_${executor.partyId}">
                                <table class="basic-table" cellspacing="0" cellpadding="0" align="center"
                                       style="height: 50px">
                                    <tbody>
                                    <tr class="header-row-2">
                                        <td width="45%">子计划标题</td>
                                        <td width="30%">要求时间</td>
                                        <td width="15%">最后反馈</td>
                                        <td width="10">状态</td>
                                    </tr>
                                        <#list childWorkPlanList as childWorkPlan>
                                            <#assign childWorkPlanStatus=childWorkPlan.childWorkPlanStatus?default('')>
                                        <tr onclick="$.workPlan.feedbackWorkPlan(${plan.workPlanId},${executor.partyId},${childWorkPlan.childWorkPlanId})">
                                            <td>${childWorkPlan.title?default('')}</td>
                                            <td>${childWorkPlan.startTime?string('yyyy-MM-dd HH:mm:ss')}
                                                ~${childWorkPlan.completeTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                            <td>
                                                <#if childWorkPlan.feedbackTime?has_content>
                                                    ${childWorkPlan.feedbackPersonName?default('')}/
                                                   ${childWorkPlan.feedbackTime?string('yyyy-MM-dd HH:mm:ss')}
                                                </#if>
                                            </td>
                                            <td>
                                                <#if childWorkPlan.childWorkPlanStatus?has_content>
                                                    <#assign childWorkPlanStatus = childWorkPlan.childWorkPlanStatus>
                                                    <@WorkPlanProgressbar id="childWorkPlanProgressbar_${childWorkPlan.childWorkPlanId}" value=childWorkPlanStatus?default(0) />
                                                </#if>
                                            </td>
                                        </tr>
                                        </#list>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </tr>
                    </#if>
                </#list>

                <tr>
                    <td colspan="37"></td>
                </tr>
            </#list>
        </#if>
        </tbody>
    </table>
</div>
<div>
<#if parameters.workPlanList?has_content>
    <@nextPrevAjax targetId="workPlanSearchResult" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
</div>