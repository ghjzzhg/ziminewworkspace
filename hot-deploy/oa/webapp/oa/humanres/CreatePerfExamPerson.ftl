<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if resultMap?has_content>
    <#assign planId = resultMap.planId?default("")>
    <#assign staff = resultMap.staff?default("")>
    <#assign perfExamType = resultMap.perfExamType?default("")>
    <#assign approver = resultMap.approver?default("")>
    <#assign reviewer = resultMap.reviewer?default("")>
    <#assign evaluator = resultMap.evaluator?default("")>
    <#assign finalizer = resultMap.finalizer?default("")>
    <#assign startDate = resultMap.startDate?default("")>
    <#assign department = resultMap.department?default("")>
    <#assign position = resultMap.position?default("")>
    <#assign level = resultMap.level?default("")>
    <#assign perfExamCycle = resultMap.perfExamCycle?default("")>
</#if>
<script language="JavaScript">

    $(function () {
        $("#staff").data("promptPosition", "bottomLeft");
        var planId = '${planId?default("")}';
        var perfExamType = '${perfExamType?default("")}';
        var perfExamCycle = '${perfExamCycle?default("")}';
        if('' != planId){
            $("select[id='perfExamType'] option").each(function () {
                if(perfExamType == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
            $("select[id='perfExamCycle'] option").each(function () {
                if(perfExamCycle == $(this).val()){
                    $(this).attr('selected',"true");
                }
            })
        };
    })

    function changeDPL(){
        var partyId = $("input[name='staff']").val();
        $.ajax({
            type: 'POST',
            url: "changeDPL",
            async: false,
            dataType: 'json',
            data:{staff:partyId},
            success: function (data) {
                $("#department").val(data.data.department);
                $("#position").val(data.data.position);
                $("#level").val(data.data.level);
            }
        });
    }
</script>
<form method="post" action="" id="CreatePerfExamPerson" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="CreatePerfExamPerson">
    <input type="hidden" name="planId" value="${planId?default('')}" id="CreatePerfExamPerson_planId">
    <input type="hidden" name="planState" value="1" id="CreatePerfExamPerson_planState">
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label for="CreatePerfExamPerson_staff" id="CreatePerfExamPerson_staff_title"><b class="requiredAsterisk">*</b>员工姓名</label>  </td>
            <td class="jqv">
            <@selectStaff id="staff" name="staff" value="${staff?default('')}" onchange = "changeDPL" multiple=false required=true/>
            </td>
            <td class="label">
                <label for="CreatePerfExamPerson_department" id="CreatePerfExamPerson_department_title">部门</label>
            </td>
            <td>
                <input type="text" value="${department?default('')}" disabled id="department">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreatePerfExamPerson_position" id="CreatePerfExamPerson_position_title">职位</label>
            </td>
            <td>
                <input type="text" value="${position?default('')}" disabled id="position">
            </td>
            <td class="label">
                <label for="CreatePerfExamPerson_level" id="CreatePerfExamPerson_level_title">职级</label>  </td>
            <td>
                <input type="text" value="${level?default('')}"  disabled id="level">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="CreatePerfExamPerson_perfExamType" id="CreatePerfExamPerson_perfExamType_title">考评类别</label>
            </td>
            <td>
                <select id="perfExamType" name="perfExamType">
                <#if typeList?has_content>
                    <#list typeList as status>
                        <option value="${status.typeId}">${status.description}</option>
                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="CreatePerfExamPerson_perfExamCycle" id="CreatePerfExamPerson_perfExamCycle_title">考评周期</label>
            </td>
            <td>
                <select id="perfExamCycle" name="perfExamCycle">
                <#if perfExamCycleList?has_content>
                    <#list perfExamCycleList as status>
                        <option value="${status.enumId}">${status.description}</option>
                    </#list>
                </#if>
                </select>
            </td>
        <tr>

            <td class="label">
                <label for="CreatePerfExamPerson_evaluator" id="CreatePerfExamPerson_evaluator_title"><b class="requiredAsterisk">*</b>评分人</label>  </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="evaluator" importability=true chooserValue="${evaluator?default('')}"/>
            </td>
            <td class="label">
                <label for="CreatePerfExamPerson_reviewer" id="CreatePerfExamPerson_reviewer_title"><b class="requiredAsterisk">*</b>初审人</label>  </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="reviewer" importability=true chooserValue="${reviewer?default('')}"/>
            </td>
        </tr>
        <tr>

            <td class="label">
                <label for="CreatePerfExamPerson_approver" id="CreatePerfExamPerson_approver_title"><b class="requiredAsterisk">*</b>终审人</label>  </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="approver" importability=true chooserValue="${approver?default('')}"/>
            </td>
            <td class="label">
                <label for="CreatePerfExamPerson_finalizer" id="CreatePerfExamPerson_finalizer_title"><b class="requiredAsterisk">*</b>归档人</label>  </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="finalizer" importability=true chooserValue="${finalizer?default('')}"/>
            </td>
        </tr>
        <tr>

            <td class="label">
                <label for="CreatePerfExamPerson_startDate" id="CreatePerfExamPerson_startDate">开始日期</label>  </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${startDate?default(.now)}" size="25" maxlength="30" id="PerfExamPerson_startDate" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>

        </tr>
        <tr>
            <td class="label">
                <label for="CreatePerfExamPerson__createButton" id="CreatePerfExamPerson_createButton_title">操作</label>  </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.perfExam.savePerfExamPerson()" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>