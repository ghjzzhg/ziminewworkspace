<#if workReportMap?has_content>
    <#assign workReportJurisdiction = workReportMap.workReportJurisdiction?default('')>
    <#assign processingTypeId = workReportMap.processingTypeId?default('')>
    <#assign personProcessing = workReportMap.personProcessing?default('')>
</#if>
<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true
        });
        $("#descriptionLab").html(unescapeHtmlText('${workReportMap.description?default('')}'));
        var type = '${processingTypeId?default('')}';
        $("select[id='processing'] option").each(function () {
            if(type == $(this).val()){
                $(this).attr('selected',"true");
            }
        })
    });

    function verifyInfo(){
        $("#template").val(template.html());
        var content = template.html();
        if(content.length <= 0){
            showInfo("请输入反馈信息");
            return;
        }else{
            addReportFeedback();
        }
    }

    function addReportFeedback(){
        $("#template").val(template.html());
        var content = template.html();
        var reportId = $("#workReportId").val();
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "json",
            success: function (data) {
                showInfo("反馈成功");
//                刷新列表页
                $.ajax({
                    type: 'post',
                    url: "searchWorkReportList",
                    async: true,
                    dataType: 'html',
                    success: function (content) {
                        $("#workReportList").html(content);
                    }
                });
                closeCurrentTab2();
            },
            url: "addReportFeedback", // override for form's 'action' attribute
            type: 'post',        // 'get' or 'post', override for form's 'method' attribute
            data:"workReportId=" + reportId,
        };
        $("#workReportForm").ajaxSubmit(options);
    }

    function editType(){
        if($("#rule").attr("checked")=="checked"){
            $("#feedtype").val("FEEDBACK_TYPE_A");
            $("#processing").attr("disabled","disabled");
        }else{
            $("#feedtype").val("FEEDBACK_TYPE_B");
            $("#processing").removeAttr("disabled");
        }
    }
</script>
<form name="workReportForm" id="workReportForm" class="basic-form">
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">报告基本信息</li>
            </ul>
            <br class="clear">
        </div>
        <table class="basic-table">
            <tbody>
            <tr>
                <td class="label">
                    <label>报告编号:</label>
                </td>
                <td>
                    ${workReportMap.reportNumber?default('')}
                    <input type="hidden" id="workReportId" name="workReportId" value="${workReportMap.workReportId}">
                </td>
                <td class="label">
                    <label>报告类别:</label>
                </td>
                <td>
                    ${workReportMap.reportTypeName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>报告状态:</label></td>
                <td>
                    ${workReportMap.statusName?default('')}
                </td>
                <td class="label">
                    <label>录 入 人:</label></td>
                <td>
                    ${workReportMap.fullName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>报告名称:</label></td>
                <td>
                    ${workReportMap.reportTitle?default('')}
                </td>
                <td class="label">
                    <label>报告进度:</label></td>
                <td>
                    ${workReportMap.planName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>简要要求:</label></td>
                <td>
                    ${workReportMap.request?default('')}
                </td>
                <td class="label">
                    <label>项目主管:</label></td>
                <td>
                    ${workReportMap.leaderNames?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>生效时间:</label></td>
                <td>
                ${workReportMap.startDate?default('')}~${workReportMap.endDate?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>执行范围:</label></td>
                <td colspan="3">
                    ${workReportMap.allName?default('')}
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label>内容说明:</label></td>
                <td colspan="3">
                    <label id="descriptionLab"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <div class="screenlet">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">发布反馈</li>
            </ul>
            <br class="clear">
        </div>
        <div>
            <table class="basic-table">
                <tbody>
                <tr>
                    <td class="label">
                        <label for="workReportForm_a" id="workReportForm_a_title">发生日期:</label>
                    </td>
                    <td>
                    <@htmlTemplate.renderDateTimeField name="feedbackTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                    value="${dd?default(.now)}" size="25" maxlength="30" id="workReportForm_a" dateType="" shortDateInput=false timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    </td>
                    <td class="label">
                        <label for="workReportForm_b" id="workReportForm_b_title">查看人:</label>
                    </td>
                    <td>
                        <select id="workReportForm_b" name="permission">
                        <#if feedbackParty?has_content>
                            <#list feedbackParty as list>
                                <option value="${list.enumId?default('')}">${list.description?default('')}</option>
                            </#list>
                        </#if>
                        </select>
                    </td>
                    <td>
                        <input type="checkbox">Email通知<input type="text" name="email">
                    </td>
                </tr>
                <tr>
                    <td colspan="5">
                        <textarea name="template" id="template" style="width:100%;"></textarea>
                    </td>
                </tr>
                <tr>
                    <td>
                       <input type="radio" name="report" id="rule" onclick="editType()" checked>常规反馈
                       <input type="hidden" name = "feedtype" id="feedtype" value="FEEDBACK_TYPE_A"/>
                    </td>
                    <td colspan="2">
                        <#if workReportJurisdiction?has_content&&personProcessing?has_content>
                            <#if workReportJurisdiction.equals("0") && !personProcessing.equals("WORK_REPORT_PLAN_C")>
                                <input type="radio" name="report" id="report" onclick="editType()">提交报告-报告进度
                                <input type="hidden" name="processingTypeId" id="processingTypeId" value="${personProcessing?default('')}">
                                <select id="processing" name="processing" disabled>
                                    <#if workReportPlan?has_content>
                                        <#list workReportPlan as list>
                                            <option value="${list.enumId?default('')}">${list.description?default('')}</option>
                                        </#list>
                                    </#if>
                                </select>
                            </#if>
                        </#if>

                    </td>
                    <td colspan="2" style="text-align: center">
                        <a href="#"  onclick="verifyInfo()" class="smallSubmit">确认反馈</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</form>