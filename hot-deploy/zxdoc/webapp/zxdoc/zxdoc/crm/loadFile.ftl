<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript">
    function loadUpFile() {
        var options = {
            beforeSubmit: function () {
                var validation = $('#loadFile').validationEngine('validate');
                return validation;
            },
            type: 'post',
            dataType: 'json',
            url: 'loadUpFile',
            success: function () {
                showInfo("导入成功");
                closeCurrentTab();
            },
        };
        $("#loadFile").ajaxSubmit(options);
    }
</script>
<div class="portlet-body">
    <form id="loadFile">
        <div  style="text-align: center;padding: 10px">
                <div class="portlet-body form">
                <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
                    <span style="color: red">(上传所有文件总大小请不要超过${fileSize?default("50")}兆)</span>
                <@fileinput name="file" multiple=false thumbnail=true thumbnailWidth="200"/>
                    <div class="form-actions" style="padding: 20px;text-align: center">
                        <button type="button" class="btn blue" onclick="loadUpFile()">
                            <i class="fa fa-check"></i> 确定
                        </button>
                    </div>
                </div>
    </form>
</div>