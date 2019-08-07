<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<script language="JavaScript">
</script>
<form method="post" action="" id="RewardTrainForSearch" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="RewardTrainForSearch">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="TrainRecordForSearch_number" id="TrainRecordForSearch_number_title">培训编号</label>
            </td>
            <td>
                <input type="text" name="number" size="25" id="TrainRecordForSearch_number">
            </td>
            <td class="label">
                <label for="TrainRecordForSearch_name" id="TrainRecordForSearch_name_title">培训课程名称</label>
            </td>
            <td>
                <input type="text" name="name" size="25" id="TrainRecordForSearch_name">
            </td>
            <td class="label">
                <label for="TrainRecordForSearch_type" id="TrainRecordForSearch_type_title">培训类型</label>
            </td>
            <td>
                <select name="type" id="TrainRecordForSearch_type">
                    <option value="">--请选择--</option>
                <#list typeList as type >
                    <option value="${type.enumId}">${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainRecordForSearch_startDate" id="TrainRecordForSearch_startDate_title">培训日期</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${startDate?default('')}" size="25" maxlength="30" id="TrainRecordForSearch_startDate"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'TrainRecordForSearch_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="TrainRecordForSearch_endDate" id="TrainRecordForSearch_endDate_title">至</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${endDate?default('')}" size="25" maxlength="30" id="TrainRecordForSearch_endDate"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'TrainRecordForSearch_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="TrainRecordForSearch_partyId" id="TrainRecordForSearch_partyId_title">员工</label>
            </td>
            <td>
            <@chooser chooserType="LookupEmployee" name="partyIdForSearch" importability=true chooserValue="${partyIdForSearch?default('')}" />
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainRecordForSearch_findButton" id="TrainRecordForSearch_findButton_title">操作</label>
            </td>
            <td>
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.searchTrain();" title="操作">查询</a>
            </td>
            <td class="label">
                <label for="TrainRecordForSearch_create" id="TrainRecordForSearch_create_title">操作</label>
            </td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.createTrain('','trainList');" title="操作">创建员工培训记录</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>