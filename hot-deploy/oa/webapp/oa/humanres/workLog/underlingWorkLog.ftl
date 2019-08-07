<script language="javascript">
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true
        });
    });
    function weekPageFlip(weekPage) {
        $.ajax({
            type: 'POST',
            url: "underlingWorkLogForPage",
            async: true,
            dataType: 'html',
            data: {weekPage: weekPage, type: '${viewType}'},
            success: function (content) {
                document.getElementById('underlingWorkLogDiv').innerHTML = " "
                $("#underlingWorkLogDiv").html(content);
            }
        });
    }
    function viewAllUnderling() {
        $.ajax({
            type: 'POST',
            url: "underlingWorkLogForPage",
            async: true,
            dataType: 'html',
            data: {type: "viewAll"},
            success: function (content) {
                document.getElementById('underlingWorkLogDiv').innerHTML = " "
                $("#underlingWorkLogDiv").html(content);
            }
        });
    }
    function viewWorkLogByDate() {
        var dateTime = $(".dateTime").val();
        $.ajax({
            type: 'POST',
            url: "underlingWorkLogForPage",
            async: true,
            dataType: 'html',
            data: {dateTime: dateTime,search:"true"},
            success: function (content) {
                document.getElementById('underlingWorkLogDiv').innerHTML = " "
                $("#underlingWorkLogDiv").html(content);
            }
        });
    }
    function showCalendar(partyId) {
        displayInTab3("underlingWorkLog", "工作日志", {requestUrl: "WorkLogForUnder", data: {partyId: partyId}, width: "800px", height: 500, position: "center"});
    }
    function dateClick(workLogId, partyId) {
        displayInTab3("makeInstructions", "查看下属日志", {requestUrl: "viewWorkLog", data: {workLogId: workLogId, partyId: partyId}, width: "800px", height: 500, position: "center"});
    }
    function subordinates() {
        displayInTab3("subordinatesTab", "更新关注下属", {requestUrl: "subordinates", width: "800px", position: "center"});
    }
</script>
<form name="" id="" class="basic-form">
    <div class="underlingWorkLogDiv" id="underlingWorkLogDiv">
        <div>
            <span><a href="#" onclick="weekPageFlip('${weekPage+1}')" class="smallSubmit">上周</a></span>
            <span><a href="#" onclick="weekPageFlip('0')" class="smallSubmit">本周</a></span>
            <span><a href="#" onclick="weekPageFlip('${weekPage-1}')" class="smallSubmit">下周</a></span>
            <span><input class="dateTime" type="date"/></span>
            <span>
                 <a href="#" onclick="viewWorkLogByDate()" class="smallSubmit"">查询</a>
            </span>
            <span>
                 <a href="#" onclick="viewAllUnderling()" class="smallSubmit"">查看全部下属</a>
            </span>
            <span>
                 <a href="#" onclick="subordinates()" class="smallSubmit"">更新关注下属</a>
            </span>
        </div>
        <div>
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr class="header-row-2">
                    <td>序号</td>
                    <td>姓名</td>
                    <td>岗位</td>
                <#list weekList as list>
                    <td>${list.date}<br>${list.week}</td>
                </#list>
                    <td>按月历查看日志</td>
                </tr>
                <#assign item=1>
                <#list personListForView as list>
                <tr>
                    <td>${item}</td>
                    <td>${list.name}</td>
                    <td></td>
                    <td>
                        <#list resultListForClone as resultListForClone>

                            <#if resultListForClone.partyId == list+"Sun">
                                <#if resultListForClone?has_content>
                                    <a href="#" style="color: green" onclick="$.workLog.showWorkLog('${resultListForClone.workLogId}','${resultListForClone.workLogDate}','view')">
                                      ${resultListForClone.logTitle?default('')}
                                    </a>
                                </#if>
                            </#if>

                        </#list>
                    </td>
                    <td>
                        <#list resultListForClone as resultListForClone>
                            <#if resultListForClone.partyId == list.partyId+"Mon">
                                <#if resultListForClone?has_content>
                                    <a href="#" style="color: green" onclick="$.workLog.showWorkLog('${resultListForClone.workLogId}','${resultListForClone.workLogDate}','view')">
                                      ${resultListForClone.logTitle?default('')}
                                    </a>
                                </#if>
                            </#if>
                        </#list>
                    </td>
                    <td>
                        <#list resultListForClone as resultListForClone>
                            <#if resultListForClone.partyId == list.partyId+"Tues">
                                <#if resultListForClone?has_content>
                                    <a href="#" style="color: green" onclick="$.workLog.showWorkLog('${resultListForClone.workLogId}','${resultListForClone.workLogDate}','view')">
                                      ${resultListForClone.logTitle?default('')}
                                    </a>
                                </#if>
                            </#if>
                        </#list>
                    </td>
                    <td>

                        <#list resultListForClone as resultListForClone>
                            <#if resultListForClone.partyId == list.partyId+"Wed">
                                <#if resultListForClone?has_content>
                                <a href="#" style="color: green" onclick="$.workLog.showWorkLog('${resultListForClone.workLogId}','${resultListForClone.workLogDate}','view')">
                                  ${resultListForClone.logTitle?default('')}
                                </a>
                                </#if>
                            </#if>
                        </#list>
                    </td>
                    <td>
                        <#list resultListForClone as resultListForClone>
                            <#if resultListForClone.partyId == list.partyId+"Thur">
                                <#if resultListForClone?has_content>
                                    <a href="#" style="color: green" onclick="$.workLog.showWorkLog('${resultListForClone.workLogId}','${resultListForClone.workLogDate}','view')">
                                      ${resultListForClone.logTitle?default('')}
                                    </a>
                                </#if>
                            </#if>
                        </#list>
                    </td>
                    <td>
                        <#list resultListForClone as resultListForClone>
                            <#if resultListForClone.partyId == list.partyId+"Fri">
                                <#if resultListForClone?has_content>
                                    <a href="#" style="color: green" onclick="$.workLog.showWorkLog('${resultListForClone.workLogId}','${resultListForClone.workLogDate}','view')">
                                     ${resultListForClone.logTitle?default('')}
                                    </a>
                                </#if>
                            </#if>
                        </#list>
                    </td>
                    <td>
                        <#list resultListForClone as resultListForClone>
                            <#if resultListForClone.partyId == list.partyId+"Sat">
                                <#if resultListForClone?has_content>
                                    <a href="#" style="color: green" onclick="$.workLog.showWorkLog('${resultListForClone.workLogId}','${resultListForClone.workLogDate}','view')">
                                     ${resultListForClone.logTitle?default('')}
                                    </a>
                                </#if>
                            </#if>
                        </#list>
                    </td>
                    <td>
                        <a href="#" class="smallSubmit" onclick="showCalendar('${list.partyId}')">查看</a>
                    </td>
                </tr>
                    <#assign item=item+1>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
</form>