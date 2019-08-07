<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<script language="JavaScript">
</script>
<form method="post" action="" id="RewardAndPunishmentForSearch" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="RewardAndPunishmentForSearch">
    <table cellspacing="0" class="basic-table">
        <tbody>
            <tr>
                <td class="label">
                    <label for="RewardRecordForSearch_number" id="RewardRecordForSearch_number_title">奖惩编号</label>
                </td>
                <td>
                    <input type="text" name="number" size="25" id="RewardRecordForSearch_number">
                </td>
                <td class="label">
                    <label for="RewardRecordForSearch_name" id="RewardRecordForSearch_name_title">奖惩名称</label>
                </td>
                <td>
                    <input type="text" name="name" size="25" id="RewardRecordForSearch_name">
                </td>
                <td class="label">
                    <label for="RewardRecordForSearch_type" id="RewardRecordForSearch_type_title">奖惩类型</label>
                </td>
                <td>
                    <select name="type" id="RewardRecordForSearch_type">
                        <option value="">--请选择--</option>
                    <#list typeList as type >
                        <option value="${type.enumId}">${type.description}</option>
                    </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="RewardRecordForSearch_startDate" id="RewardRecordForSearch_startDate_title">奖惩日期</label></td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${startDate?default('')}" size="25" maxlength="30" id="RewardRecordForSearch_startDate"
                    dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'RewardRecordForSearch_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="RewardRecordForSearch_endDate" id="RewardRecordForSearch_endDate_title">至</label>
                </td>
                <td>
                    <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                    value="${endDate?default('')}" size="25" maxlength="30" id="RewardRecordForSearch_endDate"
                    dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'RewardRecordForSearch_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td class="label">
                    <label for="RewardRecordForSearch_partyId" id="RewardRecordForSearch_partyId_title">员工</label>
                </td>
                <td>
                    <@chooser chooserType="LookupEmployee" name="partyIdForSearch" importability=true chooserValue="${partyIdForSearch?default('')}" />
                </td>
            </tr>
            <tr>
                <td class="label">
                    <label for="RewardRecordForSearch_level" id="RewardRecordForSearch_level_title">奖惩等级</label>
                </td>
                <td>
                    <select name="level" id="level">
                        <option value="">--请选择--</option>
                    <#list levelList as type >
                        <option value="${type.enumId}">${type.description}</option>
                    </#list>
                    </select>
                </td>
                <td class="label">
                    <label for="RewardRecordForSearch_findButton" id="RewardRecordForSearch_findButton_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.searchReword();" title="操作">查询</a>
                </td>
                <td class="label">
                    <label for="RewardRecordForSearch_create" id="RewardRecordForSearch_create_title">操作</label>
                </td>
                <td>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.createReword('','rewordList');" title="操作">创建奖惩记录</a>
                </td>
            </tr>
        </tbody>
    </table>
</form>