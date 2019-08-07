<script language="javascript">
    $(function () {
        planContent1 = KindEditor.create('textarea[name="planContent1"]', {
            allowFileManager: true,
        });
    });
    function saveWorkPlan(){
        $("#planContent").val(planContent1.html());
        var content = planContent1.html();
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "json",
            success: function (data) {
                console.log(data);
                if(data.data==null){
                    showInfo("保存失败")
                }else{
                    $(".workLogIdForHidden").val(data.data);
                    showInfo("保存成功")
                }
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
            url: "saveWorkPlan", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#createWorkLogForm").ajaxSubmit(options);
    }
</script>
<#--<form name="createWorkPlanForm" id="createWorkPlanForm" class="basic-form">-->
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <#--<td class="label" rowspan="3">
                    <label>当日计划:</label>
                </td>-->
                <td class="label">
                    <label for="workPlan_a"
                           id="workPlan_a_title">简述:</label>
                </td>
                <td>
                <#if returnMap?has_content>
                    <input type="text" name="planTitle" id="planTitle" value="${returnMap.planTitle}"><span class="tooltip">(显示在首页，建议在10个字以内)</span>
                <#else>
                    <input type="text" name="planTitle" id="planTitle" value=""><span class="tooltip">(显示在首页，建议在10个字以内)</span>
                </#if>

                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="workPlan_b"
                           id="LogSet_c_title">详情说明</label>
                </td>
                <td>
                    <input type="hidden" id="planContent" name="planContent"/>
                <#if returnMap?has_content>
                    <textarea name="planContent1" id="planContent1" style="width: 100%">${returnMap.planContent}</textarea>
                <#else>
                    <textarea name="planContent1" id="planContent1" style="width: 100%"></textarea>
                </#if>


                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
            </tr>
            </tbody>
        </table>
        <div align="center">
            <a href="#" onclick="saveWorkPlan();" class="smallSubmit">保存</a>
        </div>
    </div>
<#--
</form>-->
