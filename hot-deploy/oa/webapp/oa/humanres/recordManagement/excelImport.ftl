<#include "component://content/webapp/content/uploadFileList.ftl"/>
<form id="excelHandleForm" name="excelHandleForm" method="post">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_createButton" id="ContractRecordCreate_createButton_title">操作</label>  </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.CreateEmployee();" title="操作">新建员工</a>
            </td>
            <td class="label">
                <label for="ContractRecordCreate_createButton" id="ContractRecordCreate_createButton_title">操作</label>  </td>
            <td>
                <a class="attachment-image" title="员工信息表.xls" style="color: #55a3e5;margin-left:5px" href="javascript:downloadServerAttachment('MASTERPLATE_PERSON_EXCEL')" id="file_10003">导入员工模板下载</a>
            </td>
            <td class="label">
                <label for="ContractRecordCreate_createButton" id="ContractRecordCreate_createButton_title">操作</label>  </td>
            <td ><input type="file" id="uploadExcel" name="uploadExcel"></td>
            <td>
                <a class="button" onclick="uploadExcel();">导入员工</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
<script language="JavaScript">
    function uploadExcel(){
        var options = {
            beforeSubmit: function () {
                var excelFileName = $("#uploadExcel").val();
                if(excelFileName == null || excelFileName == ""){
                    showInfo("请先选择文件");
                    return false;
                }else{
                    return true;
                }
            },
            dataType: 'json',
            success: function (data) {
                showSubMenuAjax('ViewEmployeeInformation');
                showInfo(data._EVENT_MESSAGE_);
            },
            url:"uploadExcels",
            type: 'GET'
        };
        $("#excelHandleForm").ajaxSubmit(options);
    }
</script>