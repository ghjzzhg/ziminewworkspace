<div class="portlet light">
    <div class="portlet-body">
        <div class="table-scrollable table-scrollable-borderless">
            <table class="table table-hover table-light">
            <#list search.keySet() as myKeys>
                <tr>
                    <td>
                        <a href="#nowhere"
                           onclick="displayInside('${request.contextPath}/control/ApproveQuestion?questionId=${myKeys.questionId}')">${myKeys.questionOverview?default('')}
                            </a>
                            <span class="label label-sm label-info ">
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
                            </span>
                    </td>
                    <td style="white-space: nowrap"> ${myKeys.createdStamp}</td>
                </tr>
            <#--<tr>-->
            <#--<td>-->
            <#--<a href="#nowhere" onclick="displayInside('${request.contextPath}/control/ApproveQuestion?questionId=')">学而思教育集团准备上市用了大概多长时间.</a>-->
            <#--<span class="label label-sm label-info "> 法律 </span>-->
            <#--</td>-->
            <#--<td style="white-space: nowrap"> 20秒前 </td>-->
            <#--</tr>-->
            <#--<tr>-->
            <#--<td>-->
            <#--<a href="#nowhere" onclick="displayInside('${request.contextPath}/control/ApproveQuestion?questionId=')">如果公司借壳上市,那原公司会停盘吗,如果一直持有那个公司的股票呢，会不会在新公司上市后猛涨.</a>-->
            <#--<span class="label label-sm label-info "> 法律 </span>-->
            <#--</td>-->
            <#--<td style="white-space: nowrap"> 10分钟前 </td>-->
            <#--</tr>-->
            </#list>
            </table>
        </div>
    </div>
</div>