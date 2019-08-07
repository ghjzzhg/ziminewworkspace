<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="SalaryPayOffFindOptions" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="SalaryPayOffFindOptions">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="SalaryPayOffFindOptions_year" id="SalaryPayOffFindOptions_year_title">年份</label>
            </td>
            <td>
                <select name="year" id="PerfExamFindOptions_year">
                <#list dateList as type >
                    <option value="${type.yearValue}" <#if year == '${type.yearValue}'>selected="selected"</#if>>${type.yearValue}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="SalaryPayOffFindOptions_partyId" id="SalaryPayOffFindOptions_partyId_title">员工</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="partyId" importability=true chooserValue="${partyId?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="SalaryPayOffFindOptions_department" id="SalaryPayOffFindOptions_department_title">部门</label>
            </td>
            <td>
            <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}" required=false/>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="SalaryPayOffFindOptions_position" id="SalaryPayOffFindOptions_position_title">岗位</label>
            </td>
            <td>
            <@chooser chooserType="LookupOccupation" name="position" importability=true chooserValue="${position?default('')}" required=false/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="SalaryPayOffFindOptions_searchLink" id="SalaryPayOffFindOptions_searchLink_title">操作</label>
            </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.salary.searchByYear();" title="操作">查询</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>