<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<script language="javascript" xmlns="http://www.w3.org/1999/html">
    var description;
    $(function () {
        description = KindEditor.create('textarea[name="descriptionHtml"]', {
            allowFileManager: true
        });
        $("textarea[name='workPlan']").click(function () {
            displayInTab3("AddTransactionTab", "执行人范围", {
                requestUrl: "checkPerson",
                data: {param: "person"},
                width: "600px",
                position: "center"
            });
        });
        var id = '${flag}';
        if('update' == id){
            $("#workStatus").css("display", "block");
            if('${workReportMap.isPerson?default('')}' == 1){
                $("#inputUser").attr("checked","true");
            }else{
                $("#inputUser").removeAttr("checked");
            }
            if('${workReportMap.isLeader?default('')}' == 1){
                $("#supervisor").attr("checked","true");
            }else{
                $("#supervisor").removeAttr("checked");
            }
            $("#workReportFile").val('${workReportMap.fileId?default('')}');
            $("#fileId").val('${workReportMap.fileIds?default('')}');
            $("#workReportFile").val('${workReportMap.fileName?default('')}');
            var seePermission = '${workReportMap.seePermission?default('')}';
            if(seePermission == '1'){
                $("#oneselfPerson").attr("checked","true");
                $("#publicPerson").removeAttr("checked");
            }else{
                $("#oneselfPerson").removeAttr("checked");
                $("#publicPerson").attr("checked","true");
            }
            var workReportType = '${workReportMap.reportType?default('')}';
            $("select[id='WorkReportType'] option").each(function () {
                if(workReportType == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })

            var reportStatus = '${workReportMap.status?default('')}';
            $("select[id='WorkReportStatus'] option").each(function () {
                if(reportStatus == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
        }
        else{
            $("#workStatus").css("display", "none");
            $("#WorkReport_e").val(unescapeHtmlText('${inputPersonName?default("")}'));
            $("#setPartyId").val(unescapeHtmlText('${inputPersonId?default("")}'));
            $("#reportNumber").val(unescapeHtmlText('${reportNumber?default("")}'));
        }
    });

    function check(v){
        var r=/^[0-9]*$/;
        if('' != v && !r.test(v)){ //isNaN也行的,正则可以随意扩展
            $("#WorkReport_j").val('0');
            showInfo('只能输入数字');
        }
    }


    function editWorkReport(){
        $("#descriptionValue").val(description.html());
        var content = description.html();
        var options = {
            beforeSubmit: function () {
                return $('#childPlanForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab2();
                showInfo("保存成功");
                backRepoerList();
            },
            url: "editWorkReport", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#childPlanForm").ajaxSubmit(options);
    }

    function backRepoerList(){
        $.ajax({
            type: 'post',
            url: "searchWorkReportList",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#workReportList").html(content);
            }
        });
    }

    function setNoticePerson(){
        var noticePerson ="";
        if($("#inputUser").attr("checked")=="checked"){
            noticePerson = "1,0";
        }
        if($("#supervisor").attr("checked")=="checked"){
            noticePerson = "0,1";
        }
        if($("#inputUser").attr("checked")=="checked" && $("#supervisor").attr("checked")=="checked"){
            noticePerson = "1,1";
        }
        if($("#inputUser").attr("checked") != "checked" && $("#supervisor").attr("checked") != "checked"){
            noticePerson ="0,0";
        }
        jQuery('#noticePersonId').val(noticePerson);
    }

    function setWorkPerson(){
        var workPerson = "";
        if($("#oneselfPerson").attr("checked")=="checked"){
            workPerson = "1";
        }
        else{
            workPerson = "2";
        }
        jQuery('#seePermissionID').val(workPerson);
    }
    function changeEndTime(){
        var a = $("#WorkReportType").val();
        if(a=="WORK_REPORT_TYPE_A"){
            $("#endTimeDiv").show();
            $("#WorkReport_d_Div").show();
        }else if(a=="WORK_REPORT_TYPE_B"){
            $("#endTimeDiv").show();
            $("#WorkReport_d_Div").show();
        }else if(a=="WORK_REPORT_TYPE_C"){
            $("#endTimeDiv").show();
            $("#WorkReport_d_Div").show();
        }else if(a=="WORK_REPORT_TYPE_D"){
            $("#endTimeDiv").hide();
            $("#WorkReport_d_Div").hide();
        }else if(a=="WORK_REPORT_TYPE_E"){
            $("#endTimeDiv").hide();
            $("#WorkReport_d_Div").hide();
        }else if(a=="WORK_REPORT_TYPE_F"){
            $("#endTimeDiv").hide();
            $("#WorkReport_d_Div").hide();
        }else if(a=="WORK_REPORT_TYPE_G"){
            $("#endTimeDiv").hide();
            $("#WorkReport_d_Div").hide();
        }
    }
</script>
<form name="childPlanForm" id="childPlanForm" class="basic-form">
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td class="label">
                    <label for="WorkReport_a"
                           id="WorkReport_a_title">报告编号:</label>
                </td>
                <td>
                    <label id="WorkReport_a">${reportNumber?default('${workReportMap.reportNumber?default("")}')}</label>
                <#--<input type="text" size="25" id="WorkReport_a" name="reportNum" disabled="disabled" value="${workReportMap.reportNumber?default('')}">-->
                    <input type="hidden" name="reportNumber" id="reportNumber" value="${workReportMap.reportNumber?default('')}">
                    <input type="hidden" name="workReportId" id="workReportId" value="${workReportMap.workReportId?default('')}">
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
                <td class="label">
                    <label for="WorkReportType"
                           id="WorkReportType_title"><b class="requiredAsterisk">*</b>报告类别:</label></td>
                <td>
                    <select id="WorkReportType" name="reportType" onchange="/*changeEndTime()*/">
                    <#if workReportType?has_content>
                        <#list workReportType as list>
                            <option value="${list.enumId}">${list.description}</option>
                        </#list>
                    </#if>
                    </select>
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkReport_c"
                           id="WorkReport_c_title">开始日期:</label></td>
                <td>
                <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                value="${workReportMap.startTime?default(.now)}" size="25" maxlength="30" id="WorkReport_c" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'WorkReport_d\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>

                <td class="label">
                    <div id="WorkReport_d_Div">
                        <label for="WorkReport_d" id="WorkReport_d_title">结束日期:</label>
                    </div>
                </td>
                <td>
                    <div id="endTimeDiv">
                    <@htmlTemplate.renderDateTimeField name="endTime" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                    value="${workReportMap.endTime?default(.now)}" size="25" maxlength="30" id="WorkReport_d" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'WorkReport_c\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </div>
                </td>

            </tr>

            <tr>
                <td class="label">
                    <label for="WorkReport_e"
                           id="WorkReport_e_title">录  入  人:</label></td>
                <td>
                    <label>${workReportMap.fullName?default('${inputPersonName?default("")}')}</label>
                    <input type="hidden" id="setPartyId" name="inputPerson" value="${workReportMap.inputPerson?default('')}">
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
                <td class="label">
                    <label for="WorkReport_f" id="WorkReport_f_title"><b class="requiredAsterisk">*</b>项目主管:</label></td>
                <td class="jqv">
                <@selectStaff id="testStaffSelect" value="${workReportMap.leaderIds?default('')}" required=true name="testStaffSelect" multiple=true/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkReportName"
                           id="WorkReportName_title"><b class="requiredAsterisk">*</b>报告名称:</label></td>
                <td  class="jqv">
                    <input type="text" size="25" id="WorkReportName"  class="validate[required,custom[onlyLetterNumberChinese]]" maxlength="20" name="reportTitle" value="${workReportMap.reportTitle?default('')}">
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
                <td class="label">
                    <label for="WorkReport_gg"
                           id="MemoAddForm_gg_title">新反馈通知:</label></td>
                <td>
                    任务中有新的反馈立即Email通知
                    <input type="checkbox" size="25" id="inputUser" onclick="setNoticePerson(this,1)">录入人
                    <input type="checkbox" size="25" id="supervisor" onclick="setNoticePerson(this,2)">项目主管
                    <input type="hidden" name="noticePerson" id="noticePersonId" value="${workReportMap.noticePerson?default('0,0')}">
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="WorkReport_h"
                           id="WorkReport_h_title">简要要求:</label></td>
                <td>
                    <input type="text" size="25" id="WorkReport_h" name="request" maxlength="125" value="${workReportMap.request?default('')}">
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>
                <td class="label">
                    <label>上传文件:</label></td>
                <td>
                <@showFileList id="workReportFile" hiddenId="fileId" fileList=workReportMap.fileList?default('')></@showFileList>
                </td>

            </tr>
            <tr>
                <td class="label">
                    <label for="WorkReport_j"
                           id="MemoAddForm_j_title">交报告提醒:</label></td>
                <td colspan="3">
                    提前<input type="text" size="3" maxlength="3" id="WorkReport_j" value="${workReportMap.remind?default('0')}" onkeyup="check(this.value)" onafterpaste="check(this.value)" name="remind">天提醒执行人
                    <script language="JavaScript"
                            type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                </td>

            </tr>
            <tr>
                <td class="label">
                    <label for="WorkReport_k"
                           id="MemoAddForm_k_title"><b class="requiredAsterisk">*</b>执行人范围:</label>
                </td>
                <td colspan="3" class="jqv">
                <@dataScope id="testDataScope" name="dataScope" dataId="${workReportMap.workReportId?default('')}" entityName="TblWorkReport" required=true/>
                </td>
            </tr>
            <tr>
                <td class="label" rowspan="2">
                    <label for="WorkReport_m"
                           id="MemoAddForm_m_title">内容说明:</label>
                </td>
                <td colspan="3" id="ss">
                   <textarea name="descriptionHtml" id="descriptionValue">
                   ${workReportMap.description?default('')}
                   </textarea>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <span class="tooltip">
                        <p>1、你可以选择以上图标中的直接上传附件！</p>
                        <p>2、本模块可以方便的对一次性以及周期性（季度、月、周、日）工作报告进行精确的进度跟踪及历史查询。</p>
                        <p>3、如果本报告任务需要长期执行而无明确的结束日期，则需要将“结束日期”留空或选择为一个距离现在很遥远的日期即可。</p>
                        <p>4、提交日期即执行人需要提交报告的日期，“交报告提醒”将在此日期前若干天（用户设定的提前天数）每天提醒执行人一次。</p>
                        <p>5、录入人和项目主管可以修改和删除新增的报告任务；执行人不能修改和删除新增的报告任务，只能进行报告任务进度更新和报告任务反馈。</p>
                    </span>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div align="center" id="workStatus" style="display: none">
        <select id="WorkReportStatus" name="status">
        <#if workReportStatus?has_content>
            <#list workReportStatus as list>
                <option value="${list.enumId}">${list.description}</option>
            </#list>
        </#if>
        </select>
    </div>
    <div align="center">
        <a href="#" onclick="editWorkReport()" class="smallSubmit">保存</a>
    </div>
</form>