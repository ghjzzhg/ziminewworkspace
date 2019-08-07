<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<script language="javascript">
    function setShareInfo(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "json",
            success: function (data) {
                showInfo("设置成功");
                closeCurrentTab();
            },
            url: "setShareInfo", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#setShareForm").ajaxSubmit(options);
    }

    function clickRadio(){
        var radioType = "";
        if($("#readOnly").attr("checked")=="checked"){
            radioType = "view";
        }
        else{
            radioType = "update";
        }
        jQuery('#typeName').val(radioType);
    }
</script>
<div>
    <form name="setShare" id="setShareForm" class="basic-form">
        <input type="hidden" id="fileId" name="fileId" value="${fileId?default('')}">
        <table class="basic-table hover-bar">
            <tr>
                <td>
					请选择范围：
				<@dataScope id="testDataScope" name="dataScope" level=false position=false/>
                </td>
            </tr>
            <tr>
                <td>
					上述人员将：
					<input type="radio" name="type" id="readOnly" value="view" onclick="clickRadio()" checked>只读
                    <input type="radio" name="type" id="team" value="update" onclick="clickRadio()">协作
					<input type="hidden" name="typeName" id="typeName" value="view">
                </td>
            </tr>
        </table>
    </form>
    <div align="center">
        <a href="#" onclick="javascript:setShareInfo()" class="smallSubmit">设置</a>
    </div>
</div>