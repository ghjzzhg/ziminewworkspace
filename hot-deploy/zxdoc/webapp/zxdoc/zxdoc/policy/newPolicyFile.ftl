<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript">

    $(function(){
        $("#policyForm").validationEngine("attach", {promptPosition: "bottomLeft",focusFirstField : false,scroll:false});
        formData = $("#caseTemplateForm").serialize();
    })

    function savePolicyFile() {
        var fileName = $("input[name='uploadedFile']").val();
        var validate = $("#policyForm").validationEngine("validate",{focusFirstField : false,scroll:false});
        if(!fileName){
            showError("请选择文件")
            return;
        }
        var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
        if(filesub.length > 50){
            showError("文件名称长度请小于50个字符");
            return;
        }
        if(fileName.indexOf(".pdf") < 0){
            showError("请选择PDF文件");
            return;
        }
        if (validate && fileName){
            var options = {
                beforeSubmit: function () {
                    return true;
                },
                dataType: "html",
                success: function (data) {
                    var msg = $(data).find("#msg").html();
                    showInfo(msg);
                    closeCurrentTab();
                },
                url: "savePolicyFile", // override for form's 'action' attribute
                type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            };
            $("#policyForm").ajaxSubmit(options);
        }
    }

    function searchPolicyTypeTwo(){
        var oneTypeId = $("#addPolicySearchOne").val();
        $.ajax({
            type: 'GET',
            url: "searchPolicyTypeTwo",
            async: true,
            data: {oneTypeId: oneTypeId},
            dataType: 'json',
            success: function (content) {
                var twoTypeList = content.data;
                var twoPolicyHtml = "<option>-请选择-</option>";
                for(var i = 0; i < twoTypeList.length; i++){
                    var type = twoTypeList[i];
                    twoPolicyHtml += "<option value='" + type.enumId + "'> " + type.description + " </option>";
                }
                var twoTypenum = twoTypeList.length;
                if(twoTypenum > 0){
                    $("#addPolicySearchTwo").prop("class", "form-control validate[required]");
                    $("#policyTypeTwo").show();
                }else{
                    $("#addPolicySearchTwo").prop("class", "form-control");
                    $("#policyTypeTwo").hide();
                }
                $("#addPolicySearchTwo").html(twoPolicyHtml);
            }
        });
    }
</script>
<div class="portlet light">
    <div class="portlet-body row">
        <div class="col-xs-12">
            <form id="policyForm">
                <table class="table table-hover table-striped table-bordered">
                    <tbody>
                    <tr>
                        <td style="width: 100px"><label class="control-label">一级分类<span class="required"> * </span></label></td>
                        <td>
                        <#assign policySearchOne = delegator.findByAnd("Enumeration", {"enumTypeId" : "POLICY_CATEGORY"}, null, false)/>
                            <select class="form-control validate[required]" onchange="searchPolicyTypeTwo()" name="addPolicySearchOne" id="addPolicySearchOne">
                                <option value="">-请选择-</option>
                            <#list policySearchOne as policyTypeOne>
                                <option value="${policyTypeOne.enumId?default('')}">${policyTypeOne.description?default('')}</option>
                            </#list>
                            </select>
                        </td>
                    </tr>
                    <tr id="policyTypeTwo" style="display: none">
                        <td><label class="control-label">二级分类<span class="required"> * </span></label></td>
                        <td>
                            <select class="form-control" id="addPolicySearchTwo" name="addPolicySearchTwo">
                                <option value="">-请选择-</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="control-label">上传文件<span class="required"> * </span></label></td>
                        <td>
                        <@fileinput name="uploadedFile"  multiple=false />
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div class="form-group col-md-12" style="text-align: center">
                    <div class="margiv-top-10">
                        <a href="javascript:savePolicyFile()"
                           class="btn green"> 保存 </a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="note note-info">
<pre>
    温馨提示
<#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
    1.<span style="color: red">上传文件的大小请不要超过${fileSize?default("50")}兆</span>
    2.<span style="color: red">文件名称不要超过50个字符。</span>
    3.一级分类选择后可能会有二级分类，两者都为必选
</pre>
</div>