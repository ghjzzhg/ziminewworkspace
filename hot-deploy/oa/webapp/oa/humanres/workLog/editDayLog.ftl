<script language="javascript">
    var logContent1;
    $(function () {
        logContent1 = KindEditor.create('textarea[name="logContent1"]', {
            allowFileManager: true
        });
    });
    function saveWorkLog(){
        $("#logContent").val(logContent1.html());
        var content = logContent1.html();
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "json",
            success: function (data) {
                $("#workLogId").val(data.data);
                showInfo("保存成功")
                $.ajax({
                    type: 'GET',
                    url: "MyWorkLog",
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#column-container").html(content);
                    }
                });
            },
            url: "saveWorkLog", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#createWorkLogForm").ajaxSubmit(options);
    }
</script>
<#--<form name="createWorkLogForm" id="createWorkLogForm" class="basic-form">-->
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
               <#-- <td class="label" rowspan="3">
                    <label>当日计划:</label>
                </td>-->
                <td class="label">
                    <label for="workPlan_a"
                           id="workPlan_a_title">简述:</label>
                </td>
                <td>
                    <#if returnMap?has_content>
                        <input type="text"  name="logTitle" id="logTitle" value="${returnMap.logTitle}"><span class="tooltip">(显示在首页，建议在10个字以内)</span>
                        <#else>
                        <input type="text"  name="logTitle" id="logTitle" value=""><span class="tooltip">(显示在首页，建议在10个字以内)</span>
                    </#if>
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
            </tr>
           <#-- <tr>
                <td class="label">
                    <label for="workPlan_b"
                           id="LogSet_c_title">详情说明</label>
                </td>
                <td>
                    <textarea name="template" id="workPlan_b" style="width: 100%"></textarea>
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
            </tr>-->
            <tr>
                <td class="label">
                    <label for="editDayLog_a">详情说明:</label>
                </td>
                <td>
                    <table cellpadding="0" cellspacing="0" border="1" width="100%"
                           style="border-collapse: collapse">
                        <tbody>
                        <input type="hidden" id="logContent" name="logContent"/>
                        <#if returnMap?has_content>
                        <textarea name="logContent1" id="logContent1" style="width:100%;">${returnMap.logContent}</textarea>
                        <#else>
                        <textarea name="logContent1" id="logContent1" style="width:100%;">${resultMapFor.template}</textarea>
                        </#if>

                       <#-- <tr>
                            <td>

                            </td>
                            <td>
                                <input type="text" value="提交今日之前最近天的工作计划">
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                工作日志中：
                            </td>
                            <td>
                                <input type="text" value="允许提交今日之前最近天内的工作日志">
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                工作日志中的批示：
                            </td>
                            <td>
                                <input type="text" value="允许批示今日之前最近天内的工作日志">
                                </td>
                        </tr>-->
                        </tbody>
                    </table>
                </td>
            </tr>
            </tbody>
        </table>
        <div align="center">
            <a href="#" onclick="saveWorkLog();" type="submit" class="smallSubmit">保存</a>
        </div>
    </div>
<#--
</form>-->
