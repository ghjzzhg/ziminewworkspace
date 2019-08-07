<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<form method="post" action="" id="orderResourceForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="orderResourceForm">
    <table cellspacing="0" class="basic-table">
        <input name="orderId" id="orderResourceForm_orderId" type="hidden"/>
        <tbody>
        <tr>
            <td class="label">
                <label for="orderResourceForm_resourceId" id="orderResourceForm_resourceId_title"><span class="requiredAsterisk">*</span>预约资源</label>
            </td>
            <td>
                <select name="resourceId" id="orderResourceForm_resourceId">
                <#list data1.resource as type >
                    <option value="${type.resourceId}">${type.resourceName}</option>
                </#list>
                </select>
            </td>
            <td class="label">
                <label for="orderResourceForm_orderDepartment" id="orderResourceForm_orderDepartment_title"><span class="requiredAsterisk">*</span>预约部门</label></td>
            <td>
            <@chooser chooserType="LookupDepartment" name="orderDepartment" importability=true chooserValue="${defaultMap.orderDepartment?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderResourceForm_orderPerson" id="orderResourceForm_orderPerson_title"><span class="requiredAsterisk">*</span>预约人</label></td>
            <td>
            <@chooser chooserType="LookupEmployee" name="orderPerson" importability=true chooserValue="${defaultMap.orderPerson?default('')}"/>
            </td>
            <td class="label">
                <label for="orderResourceForm_register" id="orderResourceForm_register_title"><span class="requiredAsterisk">*</span>登记日期</label></td>
            <td>
            <@htmlTemplate.renderDateTimeField name="register" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${register?default('')}" size="25" maxlength="30" id="orderVehicleForm_register"
            dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'orderVehicleForm_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderResourceForm_auditPerson" id="orderResourceForm_auditPerson_title"><span class="requiredAsterisk">*</span>审核人</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupEmployee" name="auditPerson" importability=true chooserValue="${auditPerson?default('')}"/>
            </td>
            <td class="label">
                <label for="orderResourceForm_arrangePerson" id="orderResourceForm_arrangePerson_title"><span class="requiredAsterisk">*</span>安排人</label></td>
            <td class="jqv">
            <@chooser chooserType="LookupEmployee" name="arrangePerson" importability=true chooserValue="${arrangePerson?default('')}"/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderResourceForm_startDate" id="orderResourceForm_startDate_title"><span class="requiredAsterisk">*</span>开始使用时间</label></td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="startDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd mm:HH:ss"
            value="${startDate?default('')}" size="25" maxlength="30" id="orderVehicleForm_startDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'orderVehicleForm_endDate\\')}'" shortDateInput=false timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="orderResourceForm_endDate" id="orderResourceForm_endDate_title"><span class="requiredAsterisk">*</span>结束使用时间</label></td>
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
                <label for="orderResourceForm_contactInformation" id="orderResourceForm_contactInformation_title"><span class="requiredAsterisk">*</span>联系信息</label></td>
            <td class="jqv">
                <input type="text" name="contactInformation" class="validate[required,custom[onlyNumberSp],maxSize[20]]" size="25" id="orderResourceForm_contactInformation"><script language="JavaScript" type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
            </td>
            <td class="label">
                <label for="orderResourceForm_useReason" id="orderResourceForm_useReason_title"><span class="requiredAsterisk">*</span>使用事由</label></td>
            <td class="jqv">
                <input type="text" name="useReason" class="validate[required,custom[onlyLetterNumberChinese],maxSize[20]]" size="25" id="orderResourceForm_useReason"><script language="JavaScript" type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="orderResourceForm_reviewRemarks" id="orderResourceForm_reviewRemarks_title">备注</label></td>
            <td colspan="4">
                <textarea name="reviewRemarks" class="validate[maxSize[125]]" cols="60" rows="3" id="orderResourceForm_reviewRemarks"></textarea>
            </td>
        </tr>
        <tr>
            <td class="label">&nbsp;</td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.resourceManagement.saveOrderResource();" title=" ">预约资源</a
            </td>
        </tr>
        </tbody>
    </table>
</form>