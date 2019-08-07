<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<script language="javascript">
    function addShare(){
        var options = {
            beforeSubmit: function () {
                return $('#addShareForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                showInfo(data.msg);
                showFileShareInfo("${fileId?default('')}");
            },
            url: "addShare", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#addShareForm").ajaxSubmit(options);
    }
</script>
<div>
    <form name="addShareInfo" id="addShareForm" class="basic-form">
        <input type="hidden" id="fileId" name="fileId" value="${fileId?default('')}">
        <input type="hidden" id="fileFlag" name="fileFlag" value="${fileFlag?default('')}">
        <input type="hidden" id="folderId" name="folderId" value="${folderId?default('')}">
        <table class="basic-table hover-bar table table-hover table-striped table-bordered">
            <tr>
                <td>
                <@dataScope id="testDataScope" name="dataScope" dept=false level=false position=false required=true/>
                </td>
            </tr>
        </table>
    </form>
    <div align="center">
        <a href="#" onclick="javascript:addShare()" class="smallSubmit btn btn-primary">分享</a>
    </div>
</div>