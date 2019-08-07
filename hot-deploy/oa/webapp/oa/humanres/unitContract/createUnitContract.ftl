<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<script language="javascript">
    var remark;
    var abstract;
    $(function () {
        remark = KindEditor.create('textarea[name="remark"]', {
            allowFileManager: true
        });
        abstract =KindEditor.create('textarea[name="abstract"]', {
            allowFileManager: true,
        });
        $("#fileId").val('${data.fileIds?default('')}');
    });
    function addItem(id,name){
        $("#contractType_Id").append('<option value="'+id+'">'+name+'</option>');
    }
    function changeValue(){
        if($("input[id='isYes']").attr("checked")=="checked"){
            $("input[name='expiredReminder']").val("yes")
        } else if($("input[id='isNo']").attr("checked")=="checked"){
            $("input[name='expiredReminder']").val("no")
        }
    }
</script>
<form name="unitContractCreate" id="unitContractCreate" method="post" action="" class="basic-form">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr >
            <td class="label">
                　<label><b class="requiredAsterisk">*</b>合同编号：</label></td>
            <td class="jqv">
                <input type="text" name="contractNumber" class="validate[required]" data-prompt-position="centerRight" value="${data.contractNumber?default('')}"/>
            </td>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>合同类型：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                <@dropDownList dropDownValueList=data.contractTypeList enumTypeId="UNIT_CONTRACT_TYPE" inputSize="15" name="contractType"
                selectedValue="${data.unitContract.contractType?default('')}" callback="addItem"/>
            <#else>
                <@dropDownList dropDownValueList=data.contractTypeList enumTypeId="UNIT_CONTRACT_TYPE" inputSize="15" name="contractType" callback="addItem"/>
            </#if>

                    <#--<select class="validate[required]" name="contractType" data-prompt-position="centerRight">
                        <option value="">--请选择--</option>
                        <#if data.unitContract?has_content>
                            <#list data.contractTypeList as list>
                                <#if (list.enumId)==(data.unitContract.contractType)>
                                    <option value="${list.enumId?default("")}" selected>${list.description?default("")}</option>
                                <#else>
                                    <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                                </#if>
                            </#list>
                        <#else>
                            <#list data.contractTypeList as list>
                                <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                            </#list>
                        </#if>

                    </select>-->
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>合同名称：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                <input type="text" name="contractName" class="validate[required,custom[userName]]" value="${data.unitContract.contractName?default('')}">
            <#else>
                <input type="text" name="contractName" class="validate[required,custom[userName]]">
            </#if>
            </td>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>归属部门：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                    <@chooser name="department" chooserType="LookupDepartment" chooserValue="${data.unitContract.department?default('')}"/>
                <#else>
                <@chooser name="department" chooserType="LookupDepartment"/>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>客户名称：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                <input name="customerName" type="text" class="validate[required,custom[userName]]" value="${data.unitContract.customerName?default('')}"/>
            <#else>
                <input name="customerName" type="text" class="validate[required,custom[userName]]"/>
            </#if>
            </td>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>收/支类型：</label>
            </td>
            <td class="jqv">
                <select class="validate[required]" name="balanceOfPaymentsType" >
                    <option value="">--请选择--</option>
                <#if data.unitContract?has_content>
                    <#list data.paymentList as list>
                        <#if (list.enumId)==(data.unitContract.balanceOfPaymentsType)>
                            <option value="${list.enumId?default('')}" selected>${list.description?default("")}</option>
                        <#else>
                            <option value="${list.enumId?default('')}">${list.description?default("")}</option>
                        </#if>
                    </#list>
                <#else>
                    <#list data.paymentList as list>
                        <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                    </#list>
                </#if>

                </select>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>合同总金额：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                <input name="contractMoney" type="text" class="validate[required,custom[twoDecimalNumber]]" value="${data.unitContract.contractMoney?default('')}"/>元
            <#else>
                <input name="contractMoney" type="text" class="validate[required,custom[twoDecimalNumber]]"/>元
            </#if>
            </td>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>累计已收/支金额：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                <input name="totalMoney" type="text" class="validate[required,custom[twoDecimalNumber]]" value="${data.unitContract.totalMoney?default('')}"/>元
            <#else>
                <input name="totalMoney" type="text" class="validate[required,custom[twoDecimalNumber]]"/>元
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>合同开始日期：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                         <@htmlTemplate.renderDateTimeField name="contractStartDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${data.unitContract.contractStartDate?default('')}" size="25" maxlength="30" id="contractStartDate" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'contractEndDate\\')}'"
            shortDateInput=true timeDropdownParamName=""defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    <#else>
                <@htmlTemplate.renderDateTimeField name="contractStartDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                value="" size="25" maxlength="30" id="contractStartDate" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'contractEndDate\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </#if>
            </td>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>合同结束日期：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                        <@htmlTemplate.renderDateTimeField name="contractEndDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${data.unitContract.contractEndDate?default('')}" size="25" maxlength="30" id="contractEndDate" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'contractStartDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    <#else>
                <@htmlTemplate.renderDateTimeField name="contractEndDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                value="" size="25" maxlength="30" id="contractEndDate" dateType="dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\\'contractStartDate\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>过期提醒：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                <#if (data.unitContract.expiredReminder?default(''))=="yes">
                    <input type="radio" id="isYes" name="radio" checked onclick="changeValue()">是&nbsp;&nbsp;
                    <input type="radio" id="isNo" name="radio" onclick="changeValue()">否
                    <input type="hidden" name="expiredReminder" value="yes">
                <#elseif (data.unitContract.expiredReminder?default(''))=="no">
                    <input type="radio" id="isYes" name="radio" onclick="changeValue()">是&nbsp;&nbsp;
                    <input type="radio" id="isNo" name="radio" checked onclick="changeValue()">否
                    <input type="hidden" name="expiredReminder" value="no">
                </#if>
            <#else>
                <input type="radio" id="isYes" name="radio" checked onclick="changeValue()">是&nbsp;&nbsp;
                <input type="radio" id="isNo" name="radio" onclick="changeValue()">否
                <input type="hidden" name="expiredReminder" value="yes">
            </#if>

            </td>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>签约日期：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                        <@htmlTemplate.renderDateTimeField name="signDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${data.unitContract.signDate?default('')}" size="25" maxlength="30" id="signDate" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'contractStartDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    <#else>
                <@htmlTemplate.renderDateTimeField name="signDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                value="" size="25" maxlength="30" id="signDate" dateType="dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\\'contractStartDate\\')}'" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>我方签约代表：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                        <@chooser name="mineSignRepresentative" chooserType="LookupEmployee" chooserValue="${data.unitContract.mineSignRepresentative?default('')}"/>
                    <#else>
                <@chooser name="mineSignRepresentative" chooserType="LookupEmployee"/>
            </#if>

            </td>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>对方签约代表：</label>
            </td>
            <td class="jqv">
            <#if data.unitContract?has_content>
                <input type="text" name="theirSignRepresentative" class="validate[required,custom[userName]]" value="${data.unitContract.theirSignRepresentative?default('')}"/>
            <#else>
                <input type="text" name="theirSignRepresentative" class="validate[required,custom[userName]]"/>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>合同概要：</label>
            </td>
        </tr>
        <tr>
            <td class="jqv" colspan="4">
            <#if data.unitContract?has_content>
                <textarea name="abstract" class="validate[required]">${data.unitContract.abstract?default("")}</textarea>
            <#else>
                <textarea name="abstract" class="validate[required]"/>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label><b class="requiredAsterisk">*</b>备注说明：</label>
            </td>
        </tr>
        <tr>
            <td class="jqv" colspan="4">
            <#if data.unitContract?has_content>
                <textarea name="remark" class="validate[required]">${data.unitContract.remark?default("")}</textarea>
            <#else>
                <textarea name="remark" class="validate[required]"/>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label>附件：</label>
            </td>
            <td class="jqv">
            <@showFileList id="unitContractFile" hiddenId="fileId" fileList=data.fileList?default('')></@showFileList>
            </td>
        </tr>
        <tr>
            <td align="center">
            <#if data.unitContract?has_content>
                <a href="#" class="smallSubmit" onclick="$.UnitContract.saveUnitContract(${data.unitContract.unitContractId?default('')})">保存</a>
            <#else>
                <a href="#" class="smallSubmit" onclick="$.UnitContract.saveUnitContract()">保存</a>
            </#if>

            </td>
        </tr>
        </tbody>
    </table>
</form>