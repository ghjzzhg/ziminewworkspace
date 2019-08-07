<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/dropDownList.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if contractMap?has_content>
    <#assign partyId = contractMap.partyId?default("")>
    <#assign contractId = contractMap.contractId?default("")>
    <#assign workerSn = contractMap.workerSn?default("")>
    <#assign fullName = contractMap.fullName?default("")>
    <#assign groupName = contractMap.groupName?default("")>
    <#assign occupationName = contractMap.occupationName?default("")>
    <#assign contractNumber = contractMap.contractNumber?default("")>
    <#assign contractName = contractMap.contractName?default("")>
    <#assign contractType = contractMap.contractType?default("")>
    <#assign startDate = contractMap.startDate?default("")>
    <#assign endDate = contractMap.endDate?default("")>
    <#assign salary = contractMap.salary?default("")>
    <#assign signDate = contractMap.signDate?default("")>
    <#assign content = contractMap.content?default("")>
    <#assign remarks = contractMap.remarks?default("")>
    <#assign fileList = contractMap.fileList?default("")>
    <#assign fileId = contractMap.fileId?default("")>
</#if>
<script language="JavaScript">
    $(function(){
        $("#fileId").val('${fileId?default('')}');
        $('input[name="fullName"]').data("promptPosition", "bottomLeft");
    })
    function searchPartyInfo(){
        var partyId = $("input[name='fullName']").val();
        $.ajax({
            type: 'POST',
            url: "searchPartyInfo",
            async: false,
            dataType: 'json',
            data:{partyId:partyId},
            success: function (data) {
                $("#workerSn").html(data.data.workerSn);
                $("#groupName").html(data.data.groupName);
                $("#occupationName").html(data.data.occupationName);
                $("#jobState").val(data.data.jobState);
            }
        });
    }
    function changeContractType(id,name){
        $("#ContractRecordForSearch_contractType").append('<option value="'+id+'">'+name+'</option>');
    }
</script>
<form method="post" action="" id="ContractRecordCreate" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="ContractRecordCreate">
    <input type="hidden" name="contractId" value="${contractId?default('')}" id="ContractRecordCreate_contractId">
    <table cellspacing="0" class="basic-table" id="yui_3_18_1_1_1443403767248_384">
        <tbody id="yui_3_18_1_1_1443403767248_383"><tr>
            <td class="label">
                <label for="ContractRecordCreate_fullName" id="ContractRecordCreate_fullName_title"><b class="requiredAsterisk">*</b>姓名：</label>  </td>
            <td class="jqv">
            <#if contractId?has_content>
                <span>${fullName?default("")}</span>
                <input type="hidden" name ="fullName" value="${partyId?default('')}">
            <#elseif type?has_content && type.equals("1")>
                <@chooser name="fullName" chooserValue="${resultMap.partyId?default('')}" callback="searchPartyInfo" required=true/>
               <#-- <@selectStaff id="fullName" name="fullName" value="${resultMap.partyId?default('')}" onchange = "searchPartyInfo" multiple=false required=true/>-->
            <#else>
                    <span>${resultMap.fullName?default('')}</span>
                    <input type="hidden" name ="fullName" value="${resultMap.partyId?default('')}">
            </#if>
            </td>
            <td class="label">
                <label for="ContractRecordCreate_workerSn" id="ContractRecordCreate_workerSn_title">工号：</label>
            </td>
            <td>
            <#if contractId?has_content>
                <span id="workerSn">${workerSn?default('')}</span>
            <#else>
                <span id="workerSn">${resultMap.workerSn?default('')}</span>
            </#if>

            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_groupName" id="ContractRecordCreate_groupName_title">部门：</label>  </td>
            <td>
            <#if contractId?has_content>
                <span id="groupName">${groupName?default('')}</span>
            <#else>
                <span id="groupName">${resultMap.groupName?default('')}</span>
            </#if>

            </td>
            <td class="label">
                <label for="ContractRecordCreate_occupationName" id="ContractRecordCreate_occupationName_title">岗位：</label>  </td>
            <td colspan="4">
            <#if contractId?has_content>
                <span id="occupationName">${occupationName?default('')}</span>
            <#else>
                <span id="occupationName">${resultMap.occupationName?default('')}</span>
            </#if>
            </td>
        </tr>
        <tr id="yui_3_18_1_1_1443403767248_382">
            <td class="label">
                <label for="ContractRecordCreate_contractNumber" id="ContractRecordCreate_contractNumber_title">合同编号：</label>  </td>
            <td>
            <#if contractId?has_content>
                <input type="hidden" name="contractNumber" size="25" value="${contractNumber?default('')}" id="ContractRecordCreate_contractNumber">${contractNumber?default('')}
            <#else>
                <input type="hidden" name="contractNumber" size="25" value="${number?default('')}" id="ContractRecordCreate_contractNumber">${number?default('')}
            </#if>
            </td>
            <td class="label">
                <label for="ContractRecordCreate_contractName" id="ContractRecordCreate_contractName_title"><b class="requiredAsterisk">*</b>合同名称：</label>  </td>
            <td colspan="4" id="yui_3_18_1_1_1443403767248_381" class="jqv">
                <input type="text" name="contractName" size="25" value="${contractName?default('')}" id="ContractRecordCreate_contractName" class="validate[required]" maxlength="20">
                <input type="hidden" id="jobState" name ="jobState" value="">
            </td>
        </tr>
        <tr id="yui_3_18_1_1_1443403767248_419">
            <td class="label">
                <label for="ContractRecordCreate_contractType" id="ContractRecordCreate_contractType_title"><b class="requiredAsterisk">*</b>合同类型：</label>  </td>
            <td  class="jqv">
                <span>
                    <#assign contractTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "CONTRACT_TYPE"})>
                    <@dropDownList dropDownValueList=contractTypeList enumTypeId="CONTRACT_TYPE" name="contractType" selectedValue="${contractType?default('')}" callback="changeContractType"/>
                  </span>
            </td>
            <td class="label" class="jqv">
                <label for="ContractRecordCreate_salary" id="ContractRecordCreate_salary_title"><b class="requiredAsterisk">*</b>合同工资：</label>  </td>
            <td id="tdsalary">
                <input type="text" name="salary" size="25" value="${salary?default('')}" id="ContractRecordCreate_salary" class="validate[required,custom[twoDecimalNumber]]" maxlength="20">
            </td>

        </tr>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_startDate" id="ContractRecordCreate_startDate_title"><b class="requiredAsterisk">*</b>有效期开始日期：</label>
            </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="editStartDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${startDate?default(.now)}" size="25" maxlength="30" id="ContractRecordCreate_startDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'ContractRecordCreate_endDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td class="label">
                <label for="ContractRecordCreate_startDate" id="ContractRecordCreate_startDate_title"><b class="requiredAsterisk">*</b>有效期结束日期：</label>
            </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="editEndDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${endDate?default(.now)}" size="25" maxlength="30" id="ContractRecordCreate_endDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\\'ContractRecordCreate_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>

        </tr>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_signDate" id="ContractRecordCreate_signDate_title"><b class="requiredAsterisk">*</b>签约时间：</label>  </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="signDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
            value="${signDate?default(.now)}" size="25" maxlength="30" id="ContractRecordCreate_signDate"
            dateType="dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\\'ContractRecordCreate_startDate\\')}'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_content" id="ContractRecordCreate_content_title"><b class="requiredAsterisk">*</b>合同内容：</label>  </td>
            <td colspan="4" class="jqv">
                <textarea name="content" cols="60" rows="3" id="ContractRecordCreate_content" maxlength="125" class="validate[required]">${content?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_remarks" id="ContractRecordCreate_remarks_title">备注：</label>  </td>
            <td colspan="4">
                <textarea name="remarks" cols="60" rows="3" maxlength="125" id="ContractRecordCreate_remarks">${remarks?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_accessory" id="ContractRecordCreate_accessory_title">附件</label>  </td>
            <td colspan="4">
                <#include "component://content/webapp/content/showUploadFileView.ftl"/>
                <div>
                    <@showFileList id="accessory_div" hiddenId="fileId" fileList=fileList?default('')/>
                </div>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="ContractRecordCreate_createButton" id="ContractRecordCreate_createButton_title">操作</label>  </td>
            <td colspan="4">
                <#if type?has_content && type == '2'>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.saveContract('searchContract','2');" title="操作">保存</a>
                <#else>
                    <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.saveContract('searchContract','1');" title="操作">保存</a>
                </#if>
            </td>
        </tr>
        </tbody>
    </table>
</form>