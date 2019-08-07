<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#include "component://widget/templates/dropDownList.ftl"/>
<script language="JavaScript">
    var remarks;
    $(function () {
        remarks = KindEditor.create('#createVehicleForm_remarks', {
            allowFileManager: true
        });
        $("#vehicleBrandhidden").data("promptPosition", "centerRight:200,0");
    });
</script>
<#if vehicleMap?has_content>
    <#assign vehicleId = vehicleMap.vehicleId?default('')>
    <#assign vehicleName = vehicleMap.vehicleName?default('')>
    <#assign vehicleType = vehicleMap.vehicleType?default('')>
    <#assign vehicleBrand = vehicleMap.vehicleBrand?default('')>
    <#assign busload = vehicleMap.busload?default('')>
    <#assign buyInsuranceDate = vehicleMap.buyInsuranceDate?default('')>
    <#assign InsuranceDueDate = vehicleMap.InsuranceDueDate?default('')>
    <#assign buyDate = vehicleMap.buyDate?default('')>
    <#assign annualDate = vehicleMap.annualDate?default('')>
    <#assign plateNumber = vehicleMap.plateNumber?default('')>
    <#assign vehicleManager = vehicleMap.vehicleManager?default('')>
    <#assign readyState = vehicleMap.readyState?default('')>
    <#assign remarks = vehicleMap.remarks?default('')>
<#else>
    <#assign vehicleId = ''>
    <#assign vehicleName = ''>
    <#assign vehicleType = ''>
    <#assign vehicleBrand = ''>
    <#assign busload = ''>
    <#assign buyInsuranceDate = ''>
    <#assign InsuranceDueDate = ''>
    <#assign buyDate = ''>
    <#assign annualDate = ''>
    <#assign plateNumber = ''>
    <#assign vehicleManager = ''>
    <#assign readyState = ''>
    <#assign remarks = ''>
</#if>
<form method="post" action="" id="createVehicleForm" class="basic-form"  name="createVehicleForm">
    <table cellspacing="0" class="basic-table">
        <input type="hidden" name="vehicleId" value="${vehicleId?default('')}" id="createVehicleForm_vehicleId"/>
        <tbody>
        <tr>
            <td class="label">
                <label for="createVehicleForm_vehicleName" id="createVehicleForm_vehicleName_title"><span class="requiredAsterisk">*</span>车辆名称</label></td>
            <td class="jqv">
                <input type="text" name="vehicleName" value="${vehicleName?default('')}" class="validate[required,maxSize[20],custom[onlyLetterNumberChinese]]" data-prompt-position="centerRight" size="25" id="createVehicleForm_vehicleName"></td>
            <td class="label">
                <label for="createVehicleForm_vehicleBrand" id="createVehicleForm_vehicleBrand_title"><span class="requiredAsterisk">*</span>车辆品牌</label></td>
            <td class="jqv">
            <@dropDownList dropDownValueList=vehicleBrandList enumTypeId="VEHICLE_BRAND" inputSize="10" name="vehicleBrand"  selectedValue="${vehicleBrand?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="createVehicleForm_vehicleType" id="createVehicleForm_vehicleType_title"><span class="requiredAsterisk">*</span>车辆类型</label></td>
            <td class="jqv">
                <select name="vehicleType" id="createVehicleForm_vehicleType">
                <#list vehicleTypeList as type >
                    <option value="${type.enumId}" <#if vehicleType == '${type.enumId}'>selected="selected"</#if>>${type.description}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="createVehicleForm_busload" id="createVehicleForm_busload_title"><span class="requiredAsterisk">*</span>入载客量</label></td>
            <td class="jqv">
                <input type="text" name="busload" value="${busload?default('')}" class="validate[required,maxSize[3],custom[onlyNumberSp]]" size="25" id="createVehicleForm_busload"></td>
        </tr>
        <tr>
            <td class="label">
                <label for="createVehicleForm_buyInsuranceDate" id="createVehicleForm_buyInsuranceDate_title"><span class="requiredAsterisk">*</span>购买保险日期</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="buyInsuranceDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${buyInsuranceDate?default('')}" size="25" maxlength="30" id="createVehicleForm_buyInsuranceDate"
            dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'createVehicleForm_InsuranceDueDate\\')}'" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'createVehicleForm_InsuranceDueDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="createVehicleForm_InsuranceDueDate" id="createVehicleForm_InsuranceDueDate_title"><span class="requiredAsterisk">*</span>保险到期日期</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="InsuranceDueDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${InsuranceDueDate?default('')}" size="25" maxlength="30" id="createVehicleForm_InsuranceDueDate"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'createVehicleForm_buyInsuranceDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="createVehicleForm_buyDate" id="createVehicleForm_buyDate_title"><span class="requiredAsterisk">*</span>购买日期</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="buyDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${buyDate?default('')}" size="25" maxlength="30" id="createVehicleForm_buyDate"
            dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'createVehicleForm_annualDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="createVehicleForm_annualDate" id="createVehicleForm_annualDate_title"><span class="requiredAsterisk">*</span>年审日期</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="annualDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${annualDate?default('')}" size="25" maxlength="30" id="createVehicleForm_annualDate"
            dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'createVehicleForm_buyDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="createVehicleForm_plateNumber" id="createVehicleForm_plateNumber_title"><span class="requiredAsterisk">*</span>车牌号</label></td>
            <td class="jqv">
                <input type="text" name="plateNumber" value="${plateNumber?default('')}" class="validate[required,maxSize[7],custom[onlyLetterNumberChinese]]" size="25" id="createVehicleForm_plateNumber">
            </td>
            <td class="label">
                <label for="createVehicleForm_vehicleManager" id="createVehicleForm_vehicleManager_title"><span class="requiredAsterisk">*</span>车辆管理员</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupEmployee" name="vehicleManager" importability=true chooserValue="${vehicleManager?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="createVehicleForm_readyState" id="createVehicleForm_readyState_title"><span class="requiredAsterisk">*</span>可用状态</label></td>
            <td colspan="3" class="jqv">
                <select name="readyState" id="createVehicleForm_readyState">
                <#list readyStateList as type >
                    <option value="${type.enumId}" <#if readyState == '${type.enumId}'>selected="selected"</#if>>${type.description}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="createVehicleForm_remarks" id="createVehicleForm_remarks_title"><span class="requiredAsterisk">*</span>备注</label></td>
            <td colspan="3" class="jqv">
                <textarea name="remarks" class="validate[required]" id="createVehicleForm_remarks" style="width: inherit">${remarks?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="3">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.vehicleManagement.saveVehicle();" title=" ">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>