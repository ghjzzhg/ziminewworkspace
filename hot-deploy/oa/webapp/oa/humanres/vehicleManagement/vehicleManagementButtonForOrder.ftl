<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="orderVehicleForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="orderVehicleForm">
    <table cellspacing="0" class="basic-table">
        <input name="orderId" id="orderVehicleForm_orderId" type="hidden"/>
        <tbody>
        <tr>
            <td class="label">
                <label for="orderVehicleForm_vehicleId" id="orderVehicleForm_vehicleId_title"><span class="requiredAsterisk">*</span>预约车辆</label></td>
            <td class="jqv">
                <select name="vehicleId" id="orderVehicleForm_vehicleId">
                    <#list vehicleShow as type >
                        <option value="${type.vehicleId}">${type.vehicleName}</option>
                    </#list>
                </select>
            </td>
            <td class="label">
                <label for="orderVehicleForm_driver" id="orderVehicleForm_driver_title"><span class="requiredAsterisk">*</span>驾驶员</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupEmployee" name="driver" importability=true chooserValue="${driver?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderVehicleForm_orderDepartment" id="orderVehicleForm_orderDepartment_title"><span class="requiredAsterisk">*</span>预约部门</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupDepartment" name="orderDepartment" importability=true chooserValue="${orderDepartment?default('')}"/>
            </td>
            <td class="label">
                <label for="orderVehicleForm_orderPerson" id="orderVehicleForm_orderPerson_title"><span class="requiredAsterisk">*</span>预约人</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupEmployee" name="orderPerson" importability=true chooserValue="${orderPerson?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderVehicleForm_reviewPerson" id="orderVehicleForm_reviewPerson_title"><span class="requiredAsterisk">*</span>审核人</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupEmployee" name="reviewPerson" importability=true chooserValue="${reviewPerson?default('')}"/>
            </td>
            <td class="label">
                <label for="orderVehicleForm_arrangePerson" id="orderVehicleForm_arrangePerson_title"><span class="requiredAsterisk">*</span>安排人</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupEmployee" name="arrangePerson" importability=true chooserValue="${arrangePerson?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderVehicleForm_register" id="orderVehicleForm_register_title"><span class="requiredAsterisk">*</span>登记日期</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="register" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${register?default('')}" size="25" maxlength="30" id="orderVehicleForm_register"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'orderVehicleForm_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="orderVehicleForm_destination" id="orderVehicleForm_destination_title"><span class="requiredAsterisk">*</span>目的地</label></td>
            <td class="jqv">
                <input type="text" name="destination" class="validate[required,custom[onlyLetterNumberChinese],maxSize[20]]" size="25" id="orderVehicleForm_destination"><script language="JavaScript" type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderVehicleForm_startDate" id="orderVehicleForm_startDate_title"><span class="requiredAsterisk">*</span>开始使用时间</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd mm:HH:ss"
            value="${startDate?default('')}" size="25" maxlength="30" id="orderVehicleForm_startDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'orderVehicleForm_endDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="orderVehicleForm_endDate" id="orderVehicleForm_endDate_title"><span class="requiredAsterisk">*</span>结束使用时间</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="endDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd mm:HH:ss"
            value="${endDate?default('')}" size="25" maxlength="30" id="orderVehicleForm_endDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'orderVehicleForm_startDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderVehicleForm_peers" id="orderVehicleForm_peers_title">同行人员</label></td>
            <td colspan="3">
                <input type="text" name="peers" class="validate[custom[onlyLetterNumberChinese],maxSize[20]]" size="25" id="orderVehicleForm_peers"><script language="JavaScript" type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderVehicleForm_contactInformation" id="orderVehicleForm_contactInformation_title"><span class="requiredAsterisk">*</span>联系信息</label></td>
            <td class="jqv">
                <input type="text" name="contactInformation" class="validate[required,custom[isMobile],maxSize[20]]" size="25" id="orderVehicleForm_contactInformation"><script language="JavaScript" type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
            </td>
            <td class="label">
                <label for="orderVehicleForm_useReason" id="orderVehicleForm_useReason_title">使用事由</label></td>
            <td>
                <input type="text" name="useReason" class="validate[custom[onlyLetterNumberChinese],maxSize[20]]" size="25" id="orderVehicleForm_useReason"><script language="JavaScript" type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
            </td>
        </tr>
        <tr>
            <td class="label">&nbsp;</td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.vehicleManagement.saveOrderVehicle();" title=" ">预约车辆</a>
            </td>
        </tr>
        </tbody>

    </table>
</form>