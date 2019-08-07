<#macro ShowStaffList staffList action>
    <#if staffList?has_content>
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr class="header-row-2">
            <td>
                <label for="StaffList_item" id="StaffList_item_title">序号</label></td>
            <td>
                <label for="StaffList_workerSn" id="StaffList_workerSn_title">员工工号</label></td>
            <td>
                <label for="StaffList_fullName" id="StaffList_fullName_title">员工姓名</label></td>
            <td>
                <label for="StaffList_department" id="StaffList_department_title">当前部门</label></td>
            <td>
                <label for="StaffList_position" id="StaffList_position_title">当前岗位</label></td>
            <td>
                <label for="StaffList_f" id="StaffList_f_title">当前班组</label></td>
            <td>
                <label for="StaffList_laborType" id="StaffList_laborType_title">当前状态</label></td>
        </tr>

        <#assign item = 1>
        <#assign trClass = "">
            <#list staffList as list>
                <#if item%2 != 0>
                    <#assign trClass = "alternate-row">
                </#if>
                <#assign functionName = action + "('"+ list.partyId +"')">
            <tr class="${trClass}">
                <td>
                    ${item}
                </td>
                <td>
                    <a href="#nowhere" onclick="${functionName}" title="员工工号">${list.workerSn?default('')}</a>
                </td>
                <td>
                    ${list.fullName?default('')}
                </td>
                <td>
                    ${list.department?default('')}
                </td>
                <td>

                </td>
                <td>

                </td>
                <td>

                </td>
            </tr>
            <#assign item = item + 1>
            </#list>
        </tbody>
    </table>
    </#if>
</#macro>
<@ShowStaffList staffList=staffList action='${action}'></@ShowStaffList>
