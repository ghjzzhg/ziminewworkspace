<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if resultMap?has_content>
    <#assign partyId = resultMap.partyId?default("")>
    <#assign rewordId = resultMap.rewordId?default("")>
    <#assign number = resultMap.number?default("")>
    <#assign workerSn = resultMap.workerSn?default("")>
    <#assign fullName = resultMap.fullName?default("")>
    <#assign groupName = resultMap.groupName?default("")>
    <#assign occupationName = resultMap.occupationName?default("")>
    <#assign name = resultMap.name?default("")>
    <#assign type = resultMap.type?default("")>
    <#assign level = resultMap.level?default("")>
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
<form method="post" action="" id="RewardsAndPunishmentRecordCreate" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="RewardsAndPunishmentRecordCreate">
    <input type="hidden" name="rewordId" value="${rewordId?default('')}" id="RewardsAndPunishmentRecordCreate_rewordId">
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_fullName" id="RewardsAndPunishmentRecordCreate_fullName_title"><b class="requiredAsterisk">*</b>姓名：</label>  </td>
            <td width="40%" class="jqv">
            <#if rewordId?has_content>
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
                <label for="RewardsAndPunishmentRecordCreate_workerSn" id="RewardsAndPunishmentRecordCreate_workerSn_title">工号：</label>
            </td>
            <td>
                <span id="workerSn">${workerSn?default('')}</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_groupName" id="RewardsAndPunishmentRecordCreate_groupName_title">部门：</label>  </td>
            <td>
                <span id="groupName">${groupName?default('')}</span>
            </td>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_occupationName" id="RewardsAndPunishmentRecordCreate_occupationName_title">岗位：</label>  </td>
            <td colspan="4">
                <span id="occupationName">${occupationName?default('')}</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_number" id="RewardsAndPunishmentRecordCreate_number_title">奖惩编号：</label>  </td>
            <td>
                <span class="ui-widget">
                    ${number?default("")}
                    <input type="hidden" name ="number" value="${number?default("")}">
                </span>
            </td>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_name" id="RewardsAndPunishmentRecordCreate_name_title"><b class="requiredAsterisk">*</b>奖惩名称：</label>  </td>
            <td  class="jqv">
                <span class="ui-widget">
                    <input type="text" name ="name" value="${name?default("")}" class="validate[required,custom[onlyLetterNumberChinese],maxSize[20]]">
                </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_type" id="RewardsAndPunishmentRecordCreate_type_title"><b class="requiredAsterisk">*</b>培训类型：</label>  </td>
            <td class="jqv">
            <#assign rewardsAndPunishmentTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "REWARD_TYPE"})>
                <select name="type" id="RewardsAndPunishmentRecordCreate_type" size="1">
                <#if rewardsAndPunishmentTypeList?has_content>
                    <#list rewardsAndPunishmentTypeList as list>
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
                <label for="RewardsAndPunishmentRecordCreate_level" id="RewardsAndPunishmentRecordCreate_level_title"><b class="requiredAsterisk">*</b>奖惩等级：</label>  </td>
            <td class="jqv">
                    <#assign rewardsAndPunishmentLevelList = delegator.findByAnd("Enumeration", {"enumTypeId" : "REWARD_LEVEL"})>
                    <select name="level" id="RewardsAndPunishmentRecordCreate_level" size="1">
                        <#if rewardsAndPunishmentLevelList?has_content>
                        <#list rewardsAndPunishmentLevelList as list>
                        <#if level?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_money" id="RewardsAndPunishmentRecordCreate_money_title"><b class="requiredAsterisk">*</b>奖惩金额：</label>  </td>
            <td class="jqv">
                <input type="text" name ="money" value="${money?default("")}" class="validate[required,custom[twoDecimalNumber],maxSize[20]]">
            </td>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_date" id="RewardsAndPunishmentRecordCreate_date_title"><b class="requiredAsterisk">*</b>奖惩时间：</label>  </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="date" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${date?default(.now)}" size="25" maxlength="30" id="RewardsAndPunishmentRecordCreate_date" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="RewardsAndPunishmentRecordCreate_createButton" id="RewardsAndPunishmentRecordCreate_createButton_title">操作</label>  </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.saveReword('${resultMap.url}')" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>