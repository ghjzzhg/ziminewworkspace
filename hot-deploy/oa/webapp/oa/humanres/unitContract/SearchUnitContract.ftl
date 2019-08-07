<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>

<form method="post" action="" id="SearchUnitContract" class="basic-form" name="SearchUnitContract">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label>合同编号</label>
            </td>
            <td>
                <input type="text" name="contractNumber">
            </td>
            <td class="label">
                <label>合同名称</label>
            </td>
            <td>
                <input type="text" name="contractName">
            </td>
            <td class="label">
                <label>合同类型</label>
            </td>
            <td>
                <select name="contractType" id="contractType_Id">
                    <option value="">--请选择--</option>
                <#list data.contractTypeList as list>
                    <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>所属部门</label>
            </td>
            <td>
            <@chooser name="department1" chooserType="LookupDepartment"/>
            </td>
            <td class="label">
                <label>所属客户名称</label>
            </td>
            <td>
                <input type="text" name="customerName">
            </td>
            <td class="label">
                <label>收/支类型</label>
            </td>
            <td>
                <select class="validate[required]" name="balanceOfPaymentsType" >
                    <option value="">--请选择--</option>
                <#list data.paymentList as list>
                    <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>合同总金额</label>
            </td>
            <td>
                <input type="text" name="contractMoneyStart">
            </td>
            <td>
                至
            </td>
            <td>
                <input type="text" name="contractMoneyEnd">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>签约日期</label>
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="signContractStartDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="" size="25" maxlength="30" id="signContractStartDate" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'signContractEndDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td>
                至
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="signContractEndDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="" size="25" maxlength="30" id="signContractEndDate" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'signContractStartDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td >
                <a href="#" class="smallSubmit" onclick="$.UnitContract.searchContract()">查询</a>
            </td>
            <td >
                <a href="#" class="smallSubmit" onclick="$.UnitContract.createContract()">新建合同</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>
