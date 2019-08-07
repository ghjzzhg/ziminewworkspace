<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if workCycleScheduleId?has_content>
    <#assign  workCycleSchedule = delegator.findByAnd("WorkCycleScheduleInfo",
        Static["org.ofbiz.base.util.UtilMisc"].toMap("dataValue",workCycleScheduleId,"type",type))[0]>
<#assign wcScheduleWeekList = delegator.findByAnd("TblWorkCycleScheduleWorkWeek",{"workCycleScheduleId":workCycleScheduleId},null,false)>
<#assign startDate = workCycleSchedule.startDate?string("yyyy-MM-dd")>
<#assign endDate = workCycleSchedule.endDate?string("yyyy-MM-dd")>
<#assign staff = workCycleSchedule.staff?default('')>
<#assign cycleSize = workCycleSchedule.cycleSize?default('')>
<#assign cycleSizeUnit = workCycleSchedule.cycleSizeUnit?default('')>
<#assign department = workCycleSchedule.department?default('')>
    <#if staff?has_content>
        <#assign belongTo = 'WST_PERSONAL'>
        <#assign department = "">
    <#else >
        <#assign belongTo = 'WST_DEPARTMENT'>
        <#assign staff = "">
    </#if>
<#else >
    <#assign listOfWorkByWeekId = "">
    <#assign startDate = "">
    <#assign endDate = "">
    <#assign department = "">
    <#assign staff = "">
    <#assign belongTo = ''>
</#if>
<script language="JavaScript">
    $(function () {
        $("input[name='startDate']").data("promptPosition", "bottomLeft:-150,0");
        $("input[name='endDate']").data("promptPosition", "bottomLeft");
    });
    <#if department?has_content && (department != '')>
        $("#WorkScheduleAddForm_staff_title").css("display","none");
        $("#work_schedule_staff").css("display","none");
        $("#work_schedule_department").css("display","");
         $($("input[name='staff']")[1]).val('');
    </#if>

    function showDepartment(obj){
        if($(obj).val()=='WST_DEPARTMENT'){
            $("#WorkScheduleAddForm_staff_title").css("display","none");
            $("#work_schedule_staff").css("display","none");
            $("#WorkScheduleAddForm").find("input[name='staff']").attr( "disabled","disabled");
            $("#work_schedule_department").css("display","");
            $("#WorkScheduleAddForm_department").removeAttr("disabled");
        }else{
            $("#WorkScheduleAddForm_staff_title").css("display","");
            $("#work_schedule_staff").css("display","");
            $("#work_schedule_department").css("display","none");
            $("#WorkScheduleAddForm_department").attr("disabled","disabled");
            $("#WorkScheduleAddForm").find("input[name='staff']").removeAttr("disabled");
        }
    }
</script>
<form method="post" action="" id="WorkScheduleAddForm" class="basic-form" name="WorkScheduleAddForm">
    <input type="hidden" name="workCycleScheduleId" value="${workCycleScheduleId?default('')}">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="WorkScheduleAddForm_startDate" id="WorkScheduleAddForm_startDate_title"><b class="requiredAsterisk">*</b>开始日期</label>
            </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="validate[required]]" alert="" title="Format: yyyy-MM-dd"
            value="${startDate?default('')}" size="25" maxlength="30" id="WorkScheduleAddForm_startDate"
            dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'WorkScheduleAddForm_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="WorkScheduleAddForm_endDate" id="WorkScheduleAddForm_endDate_title"><b class="requiredAsterisk">*</b>结束日期</label>
            </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${endDate?default('')}" size="25" maxlength="30" id="WorkScheduleAddForm_endDate"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'WorkScheduleAddForm_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1=""
            hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="WorkScheduleAddForm_belongTo" id="WorkScheduleAddForm_belongTo_title">排班所属</label></td>
            <td>
                <#if workCycleScheduleId?has_content>
                    <label>
                        <#if belongTo == 'WST_PERSONAL'>
                            <input type="hidden" id="WorkScheduleAddForm_belongTo" value="WST_PERSONAL">
                            个人
                        <#else>
                            <input type="hidden" id="WorkScheduleAddForm_belongTo" value="WST_DEPARTMENT">
                            部门
                        </#if>
                    </label>
                <#else>
                    <#assign listOfWorkByWeekType =  delegator.findByAnd("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","WORK_SCHEDULE_TYPE"),Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"))>
                    <span class="ui-widget">
                    <select name="belongTo" id="WorkScheduleAddForm_belongTo" onclick="showDepartment($(this))"
                            size="1">
                        <#if listOfWorkByWeekType?has_content>
                            <#list listOfWorkByWeekType as list>
                            <#--${list.enumId}-->
                                <#if belongTo == list.enumId>
                                    <option value="${list.enumId}" selected>${list.description}</option>
                                <#else >
                                    <option value="${list.enumId}">${list.description}</option>
                                </#if>
                            </#list>
                        </#if>
                    </select>
                    </span>
                </#if>
            </td>
            <td class="label">
                <label for="WorkScheduleAddForm_staff" id="WorkScheduleAddForm_staff_title"><b class="requiredAsterisk">*</b>所属员工</label>
            </td>
            <td >
                <span id="work_schedule_staff">
                    <@chooser chooserType="LookupEmployee" name="staff" importability=true chooserValue="${staff?default('')}" required=false/>
                    <#--<@htmlTemplate.lookupField title="选择员工" formName="WorkScheduleAddForm" name="staff" value="${staff}" id="staff" fieldFormName="LookupStaff" position="center"/>-->
                </span>

            </td>
        </tr>
        <tr id="work_schedule_department" style="display: none">
            <td class="label">
                <label for="WorkScheduleAddForm_department" id="WorkScheduleAddForm_department_title"><b style="color: red;">*</b>所属组织</label>
            </td>
            <td colspan="3">
                 <span class="ui-widget">
                    <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}" required=false/>
                 <#--<@htmlTemplate.lookupField value="${department}" formName="WorkScheduleAddForm" name="department" id="releaseDepartment" fieldFormName="LookupDepartment" position="center"/>-->
                 </span>
            </td>
        </tr>
        <tbody>
            <tr>
                <td colspan="4">
                    <table>
                        <tr>
                            <td class="label">
                                <label for="WorkScheduleAddForm_listOfWorkByWeek"
                                       id="WorkScheduleAddForm_listOfWorkByWeek_title">班制</label></td>
                            <td width="50%">

                                <script>
                                    $(document).ready(function() {
                                        $("#listOfWorkByWeekId").multiSelect({
                                            keepOrder: true
                                        });
                                    });

                                    function clickSelect(obj){
                                        var optionList = $(obj).find("option:selected");
                                        if(optionList.length > 1){
                                            $(".workSchedule_cycle").css("display","");
                                            $(".cycle_disabled").removeAttr("disabled");
                                        }else{
                                            $(".workSchedule_cycle").css("display","none");
                                            $(".cycle_disabled").attr("disabled","cycle_disabled");
                                        }
                                    }
                                </script>
                            <#assign listOfWorkByWeek = delegator.findAll("TblListOfWorkByWeek",false)>
                            <#if listOfWorkByWeek?has_content>
                            <select name="listOfWorkByWeekId" id="listOfWorkByWeekId" multiple style="width:60%;" onclick="clickSelect($(this))" class="validate[required]">
                                <#list listOfWorkByWeek as list>
                                    <#if wcScheduleWeekList?has_content>
                                        <#list wcScheduleWeekList as weekList>
                                            <#global isSelected = false>
                                            <#if weekList.listOfWorkByWeekId == list.listOfWorkByWeekId>
                                                <option value="${list.listOfWorkByWeekId}" selected>${list.listOfWorkByWeekName}</option>
                                                <#global isSelected = true>
                                                <#break >
                                            </#if>
                                        </#list>
                                        <#if !isSelected>
                                            <option value="${list.listOfWorkByWeekId}">${list.listOfWorkByWeekName}</option>
                                        </#if>
                                    <#else >
                                        <option value="${list.listOfWorkByWeekId}">${list.listOfWorkByWeekName}</option>
                                    </#if>
                                </#list>
                            </select>
                            </#if>
                            </td>
                            <td class="label workSchedule_cycle">
                                <label for="WorkScheduleAddForm_listOfWorkByWeek"
                                       id="WorkScheduleAddForm_listOfWorkByWeek_title"><b class="requiredAsterisk">*</b>周期性</label></td>
                            <td class="workSchedule_cycle">
                            <#assign cycleTypeList = delegator.findByAnd("Enumeration",{"enumTypeId":"CYCLE_SCHEDULE_TYPE"},null,false)>
                                每<input name="cycleSize" value="${cycleSize?default(2)}"
                                        class="cycle_disabled validate[required,custom[onlyNumberSp],min[2]]" size="8">
                                <select name="cycleSizeUnit" class="cycle_disabled validate[required]">
                                <#list cycleTypeList as list>
                                    <#if list.enumId == cycleSizeUnit?default('')>
                                        <option value="${list.enumId}" selected>${list.description}</option>
                                    <#else>
                                        <option value="${list.enumId}">${list.description}</option>
                                    </#if>

                                </#list>
                                </select>
                                轮换一次
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </tbody>


        <tr>
            <td class="label">
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="$.checkingIn.saveWorkSchedule()" title="保存">保存</a>
            </td>
        </tr>

        </tbody>
    </table>
</form>
