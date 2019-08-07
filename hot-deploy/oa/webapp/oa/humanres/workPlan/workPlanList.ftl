<script language="javascript">
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true
        });
    });

    function addWorkPlan() {
        displayInTab3("WorkPlanCreateTab", "添加员工计划", {
            requestUrl: "WorkPlanCreate",
            width: "800px",
            height: "500",
            position: 'center'
        });
    }
    function showPerformance() {
        displayInTab3("ShowPerformance", "工作计划绩效评分", {
            requestUrl: "WorkPerformance",
            width: "800px",
            position: 'center'
        });
    }
</script>
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#assign staffId = parameters.userLogin.partyId>
<#assign  departmentMembers = delegator.findByAnd("DepartmentMember", {"partyId" : staffId})>
<#if departmentMembers?has_content>
    <#assign  departmentMember = delegator.findByAnd("DepartmentMember", {"partyId" : staffId})[0]>
</#if>
<#if departmentMember?has_content>
    <#assign departmentId = departmentMember.departmentId>
</#if>
<form name="WorkPlanSearch" id="WorkPlanSearch" class="basic-form">
    <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
        历年历月工作计划
    </div>
    <div>
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td class="label">
                    <label for="WorkPlanSearchForm_title"
                           id="MemoAddForm_title_title">任务名称:</label></td>
                <td>
                    <input type="text" size="25" id="WorkPlanSearchForm_title" name="title">
                </td>

                <td class="label">
                    <label for="WorkPlanSearchForm_member"
                           id="MemoAddForm_member_title">任务成员:</label>
                </td>
                <td>
                <@chooser chooserType="LookupEmployee" name="member" importability=true chooserValue="${member?default('')}" />
                <#--<@htmlTemplate.lookupField title="选择员工" formName="WorkPlanSearch" name="member" value="" id="member" fieldFormName="LookupStaff" position="center"/>-->
                </td>
                <td class="label">
                    <label for="WorkPlanSearchForm_departmentId"
                           id="MemoAddForm_departmentId_title">所在部门:</label></td>
                <td>
                    <select name="departmentId" id="WorkPlanSearchForm_departmentId">
                        <#if departmentList?has_content>
                            <option value="">全部</option>
                            <#list departmentList as department>
                                <#if departmentId == department.partyId>
                                    <option value="${department.partyId}" selected>${department.groupName}</option>
                                <#else >
                                    <option value="${department.partyId}">${department.groupName}</option>
                                </#if>
                            </#list>
                        </#if>
                    </select>
                </td>
                <td class="label">
                    <label for="WorkPlanSearchForm_planPerson"
                           id="WorkPlanSearchForm_planPerson_title">安排人:</label></td>
                <td>
                <@chooser chooserType="LookupEmployee" name="planPerson" importability=true chooserValue="${planPerson?default('')}" />
                <#--<@htmlTemplate.lookupField title="选择员工" formName="WorkPlanSearch" name="planPerson" value="" id="planPerson" fieldFormName="LookupStaff" position="center"/>-->
                </td>

            </tr>
            <tr>
                <td class="label">
                    <label for="WorkPlanSearchForm_startTime" id="MemoAddForm_startTime_title">开始时间:</label>
                </td>
                <td>
                <@htmlTemplate.renderDateTimeField name="startTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                value="" size="25" maxlength="30" id="WorkPlanSearchForm_startTime" dateType="dateFmt:'yyyy-MM-dd HH:mm:ss'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="WorkPlanSearchForm_planDescription"
                           id="MemoAddForm_planDescription_title">任务内容:</label></td>
                <td>
                    <input type="text" size="25" id="WorkPlanSearchForm_planDescription" name="planDescription">
                </td>

                <td class="label">
                    <label for="WorkPlanSearchForm_planType"
                           id="MemoAddForm_planType_title">任务类别:</label></td>
                <td>
                    <select id="WorkPlanSearchForm_planType" name="planType">
                        <option value="" selected>全部</option>
                        <#if workPlanType?has_content>
                            <#list workPlanType as type>
                                <option value="${type.enumId}">${type.description}</option>
                            </#list>
                        </#if>
                    </select>
                </td>

                <td class="label">
                    <label for="WorkPlanSearchForm_workPlanStatus"
                           id="MemoAddForm_workPlanStatus_title">任务状态:</label></td>
                <td>
                    <select id="WorkPlanSearchForm_workPlanStatus" name="workPlanStatus">
                        <#if workPlanStatus?has_content>
                            <option value="" selected>--请选择--</option>
                            <#list workPlanStatus as status>
                                <option value="${status.enumId}">${status.description}</option>
                            </#list>
                        </#if>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <a class="smallSubmit" href="#" title="查询" onclick="$.workPlan.searchWorPlan()">查询</a>
                </td>
                <td colspan="9">
                    <a class="smallSubmit" href="#" title="本人任务查询" onclick="$.workPlan.searchWorPlan('${staffId}')">本人任务查询</a>
                    <a class="smallSubmit" href="#" title="添加员工计划" onclick="addWorkPlan()">添加员工计划</a>
                </td>
            </tr>
            <tr>
                <td colspan="8">
                    <div id="workPlanSearchResult">
                    <#include "workPlanTableList.ftl">
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>


</form>