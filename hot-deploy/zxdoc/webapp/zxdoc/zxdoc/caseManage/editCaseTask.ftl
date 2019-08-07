<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#include "component://common/webcommon/includes/uiWidgets/fineuploader.ftl"/>
<script src="/images/lib/attachment.js?t=20171024" type="text/javascript"></script>
<#assign caseData = data.caseTask?default('')>
<#assign dataResourceList = data.dataResourceList?default('')>
<script type="text/javascript">
    $(function(){
        $("#caseTaskFile").attachment();
    })
    function saveCaseTask() {
        var fileFlag = false;
        $("input[name^='caseTaskFile']").each(function () {
            var fileName = $(this).val();
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                fileFlag = true;
                return;
            }
        })
        if(fileFlag){
            showError("文件名称长度请小于50个字符");
            return;
        }
        var options = {
            beforeSubmit: function () {
                return $("#caseTaskForm").validationEngine('validate');
            },
            dataType: "html",
            success: function (data) {
                var msg = $(data).find("#msg").html()
                showInfo(msg);
                initCaseTask();
            },
            url: "saveCaseTask",
            type: 'post'
        };
        $("#caseTaskForm").ajaxSubmit(options);
    }
</script>
<div align="center">
    <form id="caseTaskForm" style="width: 70%" name="caseTaskForm" class="basic-form">
        <input type="hidden" name="caseId" value="${caseId?default("")}">
        <input type="hidden" name="partyGroupId" value="${partyGroupId?default("")}">
        <input type="hidden" name="id" value="${parameters.caseTaskId?default("")}">
        <table class="table table-hover table-striped table-bordered">
            <tr>
                <td style="width: 150px">执行人</td>
                <td>
                    <select id="memberSelect" name="partyId" style="width: 238px;" class="form-control validate[required]">
                        <#list data.memberInfoList as member>
                            <option value="${member.personPartyId}" <#if caseData?has_content && caseData.partyId == member.personPartyId> selected </#if> >${member.fullName}</option>
                        </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <td>期限</td>
                <td>
                    <div class="input-group">
                        <span class="input-group-addon">
                        <i class="fa fa-calendar"></i>
                        </span>
                        <input type="text" name="dueTime"  style="width: 200px;" class="form-control validate[required]"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" <#if caseData?has_content> value="${caseData.dueTime?string("yyyy-MM-dd HH:mm:ss")}" </#if>/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>任务描述</td>
                <td>
                    <textarea style="width: 100%; height: 100%;resize: none;" name="title" rows="3" maxlength="1000" placeholder="请不要超过1000个字符"
                              class="form-control validate[required,custom[noSpecial]]"><#if caseData?has_content>${caseData.title?default('')}</#if></textarea>
                </td>
            </tr>
            <tr>
                <td>模版文件</td>
                <td>
                <#assign fileInfo = "">
                <#assign fileValue = "">
                <#if dataResourceList?has_content>
                    <#list dataResourceList as nodeFile>
                        <#assign fileInfo = fileInfo + ",{\"dataResourceId\": \"" +  nodeFile.dataResourceId + "\", \"dataResourceName\": \"" + nodeFile.dataResourceName + "\"}">
                        <#assign fileValue = fileValue + "," +  nodeFile.dataResourceId>
                    </#list>
                    <#assign fileInfo = fileInfo?substring(1)>
                    <#assign fileValue = fileValue?substring(1)>
                </#if>
                    <span style="float:right;">
                    <input type="text" name="caseTaskFile" id="caseTaskFile" data-size-limit="${fileSize}" value="${fileValue}" data-value='[${fileInfo}]'>
                        </span>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="form-group col-md-12" style="text-align: center">
                        <div class="margiv-top-10">
                            <a href="javascript:saveCaseTask();" class="btn green"> 保存 </a>
                            <a href="javascript:initCaseTask();" class="btn green"> 返回 </a>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>
<div class="note note-info">
<pre>
    温馨提示
    <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
    1.<span style="color: red">上传所有文件总大小请不要超过${fileSize?default("50")}兆。</span>
    2.<span style="color: red">文件名称不要超过50个字符。</span>
</pre>
</div>