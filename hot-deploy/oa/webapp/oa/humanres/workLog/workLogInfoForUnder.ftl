<script language="javascript">
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true

        });
    });
    $(function () {
        planContent1 = KindEditor.create('textarea[name="planContent"]', {
            allowFileManager: true,
            items:[],
            readonlyMode:true,
            resizeType:0
    });
    });
    $(function () {
        logContent1 = KindEditor.create('textarea[name="logContent"]', {
            allowFileManager: true,
            items:[],
            readonlyMode:true,
            resizeType:0
        });
    });
    $(function () {
        $(".ke-toolbar").hide();
        $(".ke-statusbar").hide();
        $(".ke-container").css("border","none");
    });
    function hiddenPlanLog(obj) {
        if (obj.attr("checked") == "checked") {
            $('#myWorkLog_plan').css("display", "none");
        } else {
            $('#myWorkLog_plan').css("display", " ");
        }
    }
    function editWorkPlan(workLogId) {
        $.ajax({
            type: 'GET',
            url: "editWorkPlanForPersonal",
            async: true,
            data:{workLogId:workLogId},
            dataType: 'html',
            success: function (content) {
                $("#workPlanCreateDiv").html(content);
            }
        });
    }
    function saveLeadInstructions(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "json",
            success: function (data) {
                showInfo("保存成功")
            },
            url: "saveLeadInstructions", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#createWorkLogForm").ajaxSubmit(options);
    }
    function editDayLog(workLogId) {
        $.ajax({
            type: 'GET',
            url: "editDayLog",
            async: true,
            dataType: 'html',
            data:{workLogId:workLogId},
            success: function (content) {
                    document.getElementById('workLogCreateDiv').innerHTML = " "
                $("#workLogCreateDiv").html(content);
            }
        });
        /* displayInTab3("editWorkPlanTab", "维护本日工作日志", {requestUrl: "editDayLog", width: "800px", position: "center"});*/
    }
    function followUpWorkPlan() {
        $.ajax({
            type: 'GET',
            url: "followUpWorkPlan",
            async: true,
            dataType: 'html',
            success: function (content) {
                document.getElementById('followUpWorkPlan').innerHTML = " "
                $("#followUpWorkPlan").html(content);
            }
        });
        /* displayInTab3("editWorkPlanTab", "维护本日工作日志", {requestUrl: "editDayLog", width: "800px", position: "center"});*/
    }
</script>
<style>
    tr {
        border: 1px solid gainsboro;
    }

    td {
        border: 1px solid gainsboro;
    }
</style>
<form name="createWorkLogForm" id="createWorkLogForm" class="basic-form">
    <input type="hidden" name="workDate" value="${workDate}"/>
    <input type="hidden" name="partyId" value="${partyId}"/>
    <div>
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">工作日志(${workDate})</li>
                <li><#--<input type="checkbox" name="hidePlan" onchange="hiddenPlanLog($(this));">隐藏当前待跟进工作计划-->
                    <#if !reviewedById?has_content>
                        <a class="smallSubmit" href="#" onclick="followUpWorkPlan()">待跟进工作计划</a>
                    </#if>
                </li>
            </ul>
            <br class="clear">
        </div>
    </div>
    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
        <tbody>

        <tr>
            <td colspan="6">
                <table cellpadding="0" cellspacing="0" border="1" width="100%"
                       style="border-collapse: collapse">
                <#assign workLogId=""/>

                <#if returnMap?has_content>
                    <#assign workLogId="${returnMap.workLogId}"/>
                    <input type="hidden" name="workLogId" value="${workLogId}"/>
                <#else>
                    <input type="hidden" name="workLogId" id="workLogIdForHidden" class="workLogIdForHidden" value=""/>
                </#if>
                    <tbody>
                    <tr>
                        <td class="label">
                            <label for="Liaison_a"
                                   id="Liaison_a_title">当日计划:</label>
                        <#if returnMap.planTitle?has_content&&!reviewedById?has_content>
                            <a class="smallSubmit" href="#" onclick="editWorkPlan('${workLogId}')">维护</a>
                        </#if>
                        </td>
                        <td colspan="2">
                            <table cellpadding="0" cellspacing="0" border="1" width="100%"
                                   style="border-collapse: collapse">
                                <tbody id="workPlanCreateDiv">
                                <tr>

                                    <td class="label">
                                        <label>简述:</label>
                                    </td>
                                <#if returnMap.planTitle?has_content>
                                    <td>
                                        <input type="hidden" name="planTitle" value="${returnMap.planTitle}">
                                        <label>${returnMap.planTitle}</label>
                                    </td>
                                <#else>
                                    <td><label id="titleLabel">暂时没有工作计划</label></td>
                                </#if>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label>详情说明:</label>
                                    </td>
                                    <td class="plan">
                                    <#if returnMap.planContent?has_content>
                                        <textarea name="planContent" id="planContent" style="width: 100%">${returnMap.planContent}</textarea>
                                    <#elseif !reviewedById?has_content&&flagForPlan == "true">
                                        <a href="#" class="smallSubmit" onclick="editWorkPlan()">录入我的工作计划</a>
                                    </#if>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            <label>当日日志:</label>
                        <#if returnMap.logTitle?has_content&&!reviewedById?has_content>
                            <a class="smallSubmit" href="#" onclick="editDayLog('${workLogId}')">维护</a>
                        </#if>
                        </td>
                        <td colspan="2">
                            <table cellpadding="0" cellspacing="0" border="1" width="100%"
                                   style="border-collapse: collapse">
                                <tbody id="workLogCreateDiv">
                                <tr>
                                    <td class="label">
                                        <label>简述:</label>
                                    </td>
                                <#if returnMap.logTitle?has_content>
                                    <td>
                                        <input type="hidden" name="logTitle" value="${returnMap.logTitle}">
                                        <label>${returnMap.logTitle}</label>
                                    </td>
                                <#else>
                                    <td><label id="titleLabel">暂时没有工作日志</label></td>
                                </#if>
                                </tr>
                                <tr>
                                    <td class="label" rowspan="2">
                                        <label>详情说明:</label>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="log">
                                    <#--<#if workLog?has_content>
                                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                                               style="border-collapse: collapse">
                                            <tbody>
                                            <tr>
                                                <td>
                                                </td>
                                                <td>提交今日之前最近天的工作计划</td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    工作日志中：
                                                </td>
                                                <td>允许提交今日之前最近天内的工作日志</td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    工作日志中的批示：
                                                </td>
                                                <td>允许批示今日之前最近天内的工作日志</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    <#else >-->
                                        <#if returnMap.logContent?has_content>
                                         <#--   <input type="hidden" name="logContent" value="${returnMap.logContent}">-->
                                            <textarea name="logContent" id="logContent" style="width: 100%">${returnMap.logContent}</textarea>
                                        <#elseif !reviewedById?has_content&&flagForLog == "true">
                                            <a href="#" class="smallSubmit" onclick="editDayLog()">录入我的工作日志</a>
                                        </#if>

                                    <#--</#if>-->

                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <#if reviewedById?has_content>
                    <tr>

                        <td class="label">
                            <label for="Liaison_a"
                                   id="Liaison_a_title">领导批示:</label>
                        </td>
                        <td>
                            <textarea name="reviewContent" style="width:100%;"></textarea>
                             <input type="hidden" name="reviewedBy" value="${reviewedById}"
                        </td>
                        <td>
                            <a class="smallSubmit" onclick="saveLeadInstructions()">确认批示</a>
                        </td>
                    </tr>
                    </#if>
                    </tbody>
                </table>
            </td>
        </tr>

        </tbody>
    </table>
    </div>
    </div>

    <#--<div id="myWorkLog_plan">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3" style="color: red;">当前待跟进工作计划</li>
            </ul>
            <br class="clear">
        </div>
        <div>
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr>
                    <td></td>
                    <td class="label">
                        <label>计划执行人:</label>
                    </td>
                    <td></td>
                    <td class="label">
                        <label>完成情况:</label>
                    </td>
                    <td></td>
                    <td class="label">
                        <label>委派任务者:</label>
                    </td>
                    <td></td>
                    <td class="label">
                        <label>执行时间:</label>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td class="label">
                        <label>简要标题</label>
                    </td>
                    <td colspan="8"></td>
                </tr>
                <tr>
                    <td class="label">
                        <label>详细说明</label>
                    </td>
                    <td colspan="8"></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>-->
</form>