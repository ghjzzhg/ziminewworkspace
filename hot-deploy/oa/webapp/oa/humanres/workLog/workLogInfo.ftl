<#include "component://oa/webapp/oa/humanres/workLog/ScheduleList.ftl"/>
<#if parameters.type?has_content>
    <#assign textName = 'reviewContent'>
<#else >
    <#assign textName = 'logContent'>
</#if>
<#if workLog?has_content>
    <#assign logContent = workLog.logContent?default('')>
    <#assign reviewContent = workLog.reviewContent?default('')>
    <#assign logTitle = workLog.logTitle?default('')>
    <#assign workLogId = workLog.workLogId>
    <#assign partyId = workLog.partyId>
</#if>
<script type="text/javascript">
    var template;
    var template1;
    $(function () {
        $("#logContentSpan").html(unescapeHtmlText('${logContent?default('${template}')}'));
        $("#logContentSpan1").html(unescapeHtmlText('${reviewContent?default('')}'));
    });
    function saveLog(){
        $('textarea[name="logContent"]').val(template.html());
        var options = {
            beforeSubmit: function () {
                return $("#createWorkLogForm").validationEngine('validate');
            },
            async: true,
            dataType:"json",
            success:function (data) {
                showInfo(data.data.message);
                $("#testCalendar").fullCalendar('refetchEvents');
                $("input[name='workLogId']").val(data.data.workLogId);
                $("#logContentSpan").html(data.data.logContent);
                $("#logDiv").css("display","");
                $("#logtext").css("display","none");
            },
            url:"saveWorkLog",
            type:'post'
        };
        $("#createWorkLogForm").ajaxSubmit(options);
    }
    function saveReviewContent(){
        $('textarea[name="reviewContent"]').val(template1.html());
        var options = {
            beforeSubmit: function () {
                return $("#createWorkLogForm").validationEngine('validate');
            },
            async: true,
            dataType:"json",
            success:function (data) {
                showInfo(data.data.message);
                $("#logContentSpan1").html(data.data.reviewContent);
                $("#logDiv1").css("display","");
                $("#logtext1").css("display","none");
            },
            url:"saveWorkLog",
            type:'post'
        };
        $("#createWorkLogForm").ajaxSubmit(options);
    }
    function showWorkPlan(obj){
        $.ajax({
            url:"followUpWorkPlan",
            type:"post",
            async:true,
            dataType:"html",
            success:function(data){
                $("#myselfWorkPlan").html(data);
            }
        });
        $("#myselfWorkPlan").css("display","");
        $(obj).attr("onclick","hiddenWorkPlan($(this))");
    }
    function hiddenWorkPlan(obj){
        $("#myselfWorkPlan").css("display","none");
        $(obj).attr("onclick","showWorkPlan($(this))");
    }

    function showEditPage(){
        $("#logDiv").css("border","1px gray solid");
        $("#logDiv1").css("border","1px gray solid");
    }

    function closeEditPage(){
        $("#logDiv").css("border","");
        $("#logDiv1").css("border","");
    }

    function showEditTextArea(){
        $("#logDiv").css("display","none");
        $("#logtext").css("display","");
        KindEditor.remove('textarea[name="logContent"]');
        template = KindEditor.create('textarea[name="logContent"]', {
            allowFileManager: true
        });
        $("#logDiv1").css("display","none");
        $("#logtext1").css("display","");
        KindEditor.remove('textarea[name="reviewContent"]');
        template1 = KindEditor.create('textarea[name="reviewContent"]', {
            allowFileManager: true
        });
    }


    function deleteSchedule(workLogId,scheduleId,workLogDate){
        if(confirm("您确定要删除吗？")){
            $.ajax({
                type: 'post',
                url: "deleteSchedule",
                data: {scheduleId:scheduleId,workLogId: workLogId,workLogDate:workLogDate},
                async: true,
                dataType: 'html',
                success: function (content) {
                    $("#scheduleList").html(content);
                    $("#testCalendar").fullCalendar('refetchEvents');
                    showInfo($("#workLog_msg").val());
                }
            });
        }
    }
    function addSchedule(){
        displayInTab3("AddScheduleTab", "添加日程", {requestUrl: "addSchedule", data: {scheduleDay:"${workLogDate?default('')}",workLogId:"${workLogId?default('')}"}, width: "600"});
    }
    function updateSchedule(scheduleId,scheduleDay,workLogId,scheduleTitle,scheduleStartDatetime,scheduleEndDatetime,scheduleContent){
        displayInTab3("AddScheduleTab", "修改日程", {
            requestUrl: "addSchedule",
            data:{scheduleId:scheduleId,scheduleDay:scheduleDay,workLogId:workLogId,scheduleTitle:scheduleTitle,scheduleStartDatetime:scheduleStartDatetime,scheduleEndDatetime:scheduleEndDatetime,scheduleContent:scheduleContent},
            width: "600px",
            position: "center"
        });
    }
</script>


<form name="createWorkLogForm" id="createWorkLogForm" class="basic-form">
    <div class="screenlet-title-bar">
        <ul>
            <li style="float: left">日程列表(<b style="color:green;">${parameters.workLogDate?default('')}</b>)</li>
            <#if canSchedule&&(!partyId?has_content ||userLogin.partyId == partyId)>
                <li style="float: right"><a href="#" class="smallSubmit" onclick="addSchedule()">添加日程</a></li>
            </#if>
            <#if !parameters.type?has_content>
                <li style="float: right"><a href="#" class="smallSubmit" onclick="showWorkPlan($(this))">待跟进计划</a></li>
            </#if>
        </ul>
        <br class="clear">
    </div>

    <div id="scheduleList">
        <#include "newScheduleList.ftl"/>
    </div>

    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">工作日志</li>
        </ul>
        <br class="clear">
    </div>

    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
        <#if parameters.type?has_content>
            <tr>
                <td>
                    <#if logContent?has_content>
                        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                            <td class="label">
                                <label for="logTitle">日志标题：</label>
                            </td>
                            <td colspan="2">
                                ${logTitle?default('')}
                            </td>
                            <tr>
                                <td class="label">
                                    <label for="logContent">日志内容：</label>
                                </td>
                                <td colspan="10">
                                    <div id="aaa">
                                    </div>
                                    <script>
                                        $("#aaa").append("<label>"+ unescapeHtmlText('${logContent}') +"</label>");
                                    </script>
                                </td>
                            </tr>
                        </table>


                    <#else >
                        <p style="color: red">暂无日志录入</p>
                    </#if>
                </td>
            </tr>
        <#elseif (canLog?has_content && canLog)>
            <tr>
                <td>
                    <input type="hidden" name="workLogId" value="${workLogId?default('')}">
                    <input type="hidden" name="workLogDate" value="${parameters.workLogDate?default('')}">
                    <div id="logDiv"  style="min-height: 60px;" onclick="showEditTextArea()" onmousemove="showEditPage()" onmouseout="closeEditPage()">
                        <label id="logContentSpan"/>
                    </div>
                    <div id="logtext" style="display: none;">
                        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                            <td class="label">
                                <label for="logTitle"><b class="requiredAsterisk">*</b>日志标题：</label>
                            </td>
                            <td colspan="2" class="jqv">
                                <input type="text" name="logTitle" maxlength="10" class="validate[required,custom[onlyLetterNumberChinese]]" value="${logTitle?default('')}"/><br/>
                            </td>
                            <tr>
                                <td class="label">
                                    <label for="logContent"><b class="requiredAsterisk">*</b>日志内容：</label>
                                </td>
                                <td colspan="10" class="jqv">
                                    <textarea name="logContent" class="validate[required]">${logContent?default('${template}')}</textarea>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="7" align="center">
                                    <a href="#" class="smallSubmit" onclick="saveLog()">确定</a>
                                </td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
        <#else >
            <tr>
                <td>
                    <div style="color: red;font-size: 12px;text-align: center">
                        该时间段不可录入日志！
                    </div>
                </td>
            </tr>
        </#if>
       </table>
    </div>
    <div id="myselfWorkPlan" style="display: none"/>
<#if parameters.type?has_content>
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">领导批示</li>
        </ul>
        <br class="clear">
    </div>
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td>
                    <input type="hidden" name="workLogDate" value="${workLog.workLogDate?default('')}">
                    <input type="hidden" name="workLogId" value="${workLog.workLogId?default('')}">
                    <div id="logDiv1"  style=" height: 60px" onclick="showEditTextArea()" onmousemove="showEditPage()" onmouseout="closeEditPage()">
                        <label id="logContentSpan1"/>
                    </div>
                    <div id="logtext1" style="display: none;">
                        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                            <tr>
                                <td class="label">
                                    <label for="logContent"><b class="requiredAsterisk">*</b>领导批示内容：</label>
                                </td>
                                <td colspan="10" class="jqv">
                                    <textarea name="reviewContent" class="validate[required]">${reviewContent?default('')}</textarea>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="7" align="center">
                                    <a href="#" class="smallSubmit" onclick="saveReviewContent()">确定</a>
                                </td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</#if>
</form>