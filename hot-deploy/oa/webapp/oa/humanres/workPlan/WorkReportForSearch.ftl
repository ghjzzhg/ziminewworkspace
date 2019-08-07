<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="workReportSearchForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="workReportSearchForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="num" id="num_title">报告编号</label>
            </td>
            <td>
                <input type="text" name="a" size="25" id="num">
            </td>
            <td class="label">
                <label for="name" id="name_title">报告名称</label>
            </td>
            <td>
                <input type="text" name="b" size="25" id="name">
            </td>
            <td class="label">
                <label for="party" id="party_title">录入人</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="party" importability=true chooserValue="${party?default('')}" />
            </td>
            <td class="label">
                <label for="status" id="status_title">报告总状况</label>
            </td>
            <td>
                <select name="d" id="status">
                    <option value="">--请选择--</option>
                <#list statusList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="leader" id="leader_title">项目主管</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="leader" importability=true chooserValue="${leader?default('')}" />
            </td>
            <td class="label">
                <label for="type" id="type_title">报告类别</label>
            </td>
            <td>
                <select name="f" id="type">
                    <option value="">--请选择--</option>
                <#list typeList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="executor" id="executor_title">执行人</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="executor" importability=true chooserValue="${executor?default('')}" />
            </td>
            <td class="label">
                <label for="process" id="process_title">本周期进度</label>
            </td>
            <td>
                <select name="h" id="process">
                    <option value="">--请选择--</option>
                <#list processList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="WorkReportForSearch_findButton" id="WorkReportForSearch_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.workPlan.searchWorkReportList();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="WorkReportForSearch_create" id="WorkReportForSearch_create_title">操作</label>
            </td>
            <td colspan="5">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.workPlan.addWorkReport('新增报告任务',null);" title="操作">新增工作报告</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>