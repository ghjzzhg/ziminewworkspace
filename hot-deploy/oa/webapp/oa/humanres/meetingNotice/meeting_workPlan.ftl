<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<tr>
    <td>
        ${index}
    </td>
    <td>
        <table>
            <tr>
                <td class="label">
                    <label for="personWork_executor"
                           id="personWork_executor_title">执行人:</label>
                </td>
                <td>
                <@htmlTemplate.lookupField value="" formName="meetingNoticeSummaryForm" name="executor_o_${index}" id="partyId" fieldFormName="LookupStaff" position="center"/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="personWork_startTime" id="personWork_startTime_title">开始时间:</label>
                </td>
                <td>
                <@htmlTemplate.renderDateTimeField name="personWorkStartTime_o_${index}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                value="" size="25" maxlength="30" id="personWorkStartTime_o_${index}" dateType="" shortDateInput=false timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="personWork_endTime" id="personWork_endTime_title">结束时间:</label>
                </td>
                <td>
                <@htmlTemplate.renderDateTimeField name="personWorkEndTime_o_${index}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                value="" size="25" maxlength="30" id="personWorkEndTime_o_${index}" dateType="" shortDateInput=false timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
            </tr>
        </table>
    </td>
    <td>
                                            <textarea name="personWorkDescription_o_${index}"
                                                      style="width: 100%;height: 100%"></textarea>
    </td>
    <td>
        <a class="smallSubmit" id="workPlan_delete" name="workPlan_delete_o_${index}">删除</a>
    </td>
</tr>