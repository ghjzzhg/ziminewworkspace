<#include "component://content/webapp/content/uploadFileList.ftl"/>
<form id="excelHandleForm" name="excelHandleForm" method="post">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
            <tr>
                <td class="label">
                    <input type="hidden" name="year" value="${year}">
                    <label>月份</label>
                </td>
                <td>
                    <select name="month" id="excelMonth">
                    <#list ["1","2","3","4","5","6","7","8","9","10","11","12"] as index>
                        <option value="${index}">${index}月</option>
                    </#list>
                    </select>
                </td>
                <td>
                    <a class="button" onclick="javascript:$.salary.excelLeadOut('${year}');">导出工资信息</a>
                </td>
            </tr>
            <tr class="alternate-row">
                <td colspan="2"><input type="file" id="uploadExcel" name="uploadExcel"></td>
                <td>
                    <a class="button" onclick="uploadExcel();">导入发放详情</a>
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
                showSubMenuAjax('SalaryPayOff');
                showInfo("导入成功");
            },
            url:"uploadExcel",
            type: 'GET'
        };
        $("#excelHandleForm").ajaxSubmit(options);
    }
</script>