<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if resultMap?has_content>
    <#assign partyId = resultMap.partyId?default("")>
    <#assign trainId = resultMap.trainId?default("")>
    <#assign number = resultMap.number?default("")>
    <#assign workerSn = resultMap.workerSn?default("")>
    <#assign fullName = resultMap.fullName?default("")>
    <#assign groupName = resultMap.groupName?default("")>
    <#assign occupationName = resultMap.occupationName?default("")>
    <#assign name = resultMap.name?default("")>
    <#assign type = resultMap.type?default("")>
    <#assign address = resultMap.address?default("")>
    <#assign content = resultMap.content?default("")>
    <#assign remarks = resultMap.remarks?default("")>
    <#assign teacher = resultMap.teacher?default("")>
    <#assign money = resultMap.money?default("")>
    <#assign date = resultMap.date?default("")>
</#if>
<script language="JavaScript">
    $(function () {
        $('input[name="partyId"]').data("promptPosition", "bottomLeft");
    });
    function searchPartyInfo(){
        var partyId = $("input[name='partyId']").val();
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
</script>
<form method="post" action="" id="TrainManagementCreate" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="TrainManagementCreate">
    <input type="hidden" name="trainId" value="${trainId?default('')}" id="TrainManagementCreate_trainId">
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label for="TrainManagementCreate_fullName" id="TrainManagementCreate_fullName_title"><b class="requiredAsterisk">*</b>姓名：</label>  </td>
            <td width="40%" class="jqv">
            <#if trainId?has_content>
                <span>${fullName?default("")}</span>
                <input type="hidden" name ="partyId" value="${partyId?default('')}">
            <#else>
                <#if partyId?has_content>
                    <span>${fullName?default('')}</span>
                    <input type="hidden" name ="partyId" value="${partyId?default('')}">
                <#else>
                    <@chooser name="partyId" chooserValue="${partyId?default('')}" callback="searchPartyInfo" required=true/>
                </#if>
            </#if>
            </td>
            <td class="label">
                <label for="TrainManagementCreate_workerSn" id="TrainManagementCreate_workerSn_title">工号：</label>
            </td>
            <td>
                <span id="workerSn">${workerSn?default('')}</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainManagementCreate_groupName" id="TrainManagementCreate_groupName_title">部门：</label>  </td>
            <td>
                <span id="groupName">${groupName?default('')}</span>
            </td>
            <td class="label">
                <label for="TrainManagementCreate_occupationName" id="TrainManagementCreate_occupationName_title">岗位：</label>  </td>
            <td colspan="4">
                <span id="occupationName">${occupationName?default('')}</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainManagementCreate_number" id="TrainManagementCreate_number_title">培训编号：</label>  </td>
            <td>
                <span class="ui-widget">
                    ${number?default("")}
                        <input type="hidden" name ="number" value="${number?default("")}">
                </span>
            </td>
            <td class="label">
                <label for="TrainManagementCreate_name" id="TrainManagementCreate_name_title"><b class="requiredAsterisk">*</b>培训名称：</label>  </td>
            <td class="jqv">
                <span class="ui-widget">
                    <input type="text" name ="name" value="${name?default("")}" class="validate[required,custom[onlyLetterNumberChinese],maxSize[20]]">
                </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainManagementCreate_type" id="TrainManagementCreate_type_title"><b class="requiredAsterisk">*</b>培训类型：</label>  </td>
            <td class="jqv">
            <#assign trainManagementTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "TRAIN_TYPE"})>
                <select name="type" id="TrainManagementCreate_type" size="1">
                <#if trainManagementTypeList?has_content>
                    <#list trainManagementTypeList as list>
                        <#if type?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="TrainManagementCreate_date" id="TrainManagementCreate_date_title"><b class="requiredAsterisk">*</b>培训日期：</label>  </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="date" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${date?default(.now)}" size="25" maxlength="30" id="TrainManagementCreate_date" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainManagementCreate_teacher" id="TrainManagementCreate_teacher_title"><b class="requiredAsterisk">*</b>培训讲师：</label>  </td>
            <td class="jqv">
                <input type="text" name ="teacher" value="${teacher?default("")}" class="validate[required,maxSize[20],cudtom[onlyLetterChinese]]">
            </td>
            <td class="label">
                <label for="TrainManagementCreate_address" id="TrainManagementCreate_address_title"><b class="requiredAsterisk">*</b>培训地址：</label>  </td>
            <td class="jqv">
                <input type="text" name ="address" value="${address?default("")}" class="validate[required,custom[onlyLetterNumberChinese],maxSize[20]]">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainManagementCreate_content" id="TrainManagementCreate_content_title"><b class="requiredAsterisk">*</b>培训内容：</label>  </td>
            <td class="jqv">
                <input type="text" name ="content" value="${content?default("")}" class="validate[required,maxSize[20]]">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainManagementCreate_remarks" id="TrainManagementCreate_remarks_title">备注：</label>  </td>
            <td>
                <input type="text" name ="remarks" value="${remarks?default("")}">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TrainManagementCreate_createButton" id="TrainManagementCreate_createButton_title">操作</label>  </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.saveTrain('${resultMap.url}')" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>