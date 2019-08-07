<#include "component://widget/templates/chooser.ftl"/>
<script language="JavaScript">
    function searchLookupEmployee${parameters.name}(){
        var options = {
            beforeSubmit: function () {
                return true;
            },
            dataType: "html",
            data:{},
            success: function (data) {
                $("#LookupEmployeeList_col").html(data);
            },
            url: "searchLookupEmployee",
            type: 'post',
        };
        $("#lookEmplyee").ajaxSubmit(options);
    }
</script>
<div class="screenlet-title-bar">
    <ul>
        <li class="h3">查询条件</li>
    </ul>
    <br class="clear"></div>
<div class="screenlet-body no-padding">
    <form method="post" action="" id="lookEmplyee" class="basic-form" name="lookEmplyee">
    <input type="hidden" name="name" value="${parameters.name}"/>
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label>员工姓名</label>
            </td>
            <td>
                <input type="text" name="fullName" size="25">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>部门名称</label></td>
            <td>
                <@chooser name="lookUpGroupName" importability=false strTab="选择部门" chooserType="LookupDepartment" secondary="true"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>岗位名称</label></td>
            <td>
                <@chooser  name="LookupOccupation" importability=false strTab="选择岗位" chooserType="LookupOccupation" secondary="true"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <a href="#" class="smallSubmit" onclick="searchLookupEmployee${parameters.name}()">查找</a>
        </tr>
        </tbody>
    </table>
    </form>
</div>
