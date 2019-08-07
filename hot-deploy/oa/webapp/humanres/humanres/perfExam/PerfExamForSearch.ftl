<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="PerfExamFindOptions" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="PerfExamFindOptions">
    <table cellspacing="0" class="basic-table">
        <tbody id="yui_3_18_1_1_1461117190005_332">
        <tr>
            <td class="label">
                <label for="PerfExamFindOptions_nameForSearch" id="PerfExamFindOptions_nameForSearch_title">姓名</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="nameForSearch" importability=true chooserValue="${nameForSearch?default('')}" required=false/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="PerfExamFindOptions_departmentForSearch" id="PerfExamFindOptions_departmentForSearch_title">部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="departmentForSearch" importability=true chooserValue="${departmentForSearch?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="PerfExamFindOptions_postForSearch" id="PerfExamFindOptions_postForSearch_title">岗位</label>
            </td>
            <td>
            <@chooser chooserType="LookupOccupation" name="postForSearch" importability=true chooserValue="${postForSearch?default('')}" required=false/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="PerfExamFindOptions_evaluateYear" id="PerfExamFindOptions_evaluateYear_title">考评年份</label>
            </td>
            <td>
                <select name="evaluateYear" id="PerfExamFindOptions_evaluateYear" onchange="javascript:$.perfExam.changeEvaluateMonth(this)">
                    <#list years as type >
                        <option value="${type.value}">${type.label}</option>
                    </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="PerfExamFindOptions_evaluateMonth" id="PerfExamFindOptions_evaluateMonth_title">考评月份</label>
            </td>
            <td>
                <select name="evaluateMonth" id="PerfExamFindOptions_evaluateMonth">
                    <#list months as type >
                        <option value="${type.value}">${type.label}</option>
                    </#list>
                </select>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="PerfExamFindOptions_searchLink" class="hide" id="PerfExamFindOptions_searchLink_title">操作</label></td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.perfExam.searchPerfExam();" title="操作">查询</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>