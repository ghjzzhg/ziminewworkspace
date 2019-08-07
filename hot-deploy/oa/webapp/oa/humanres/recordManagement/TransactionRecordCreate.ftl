<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if postId?has_content>
    <#assign partyId = resultMap.partyId?default("")>
    <#assign postId = resultMap.postId?default("")>
    <#assign number = resultMap.number?default("")>
    <#assign workerSn = resultMap.workerSn?default("")>
    <#assign fullName = resultMap.fullName?default("")>
    <#assign lastGroup = resultMap.lastGroup?default("")>
    <#assign lastPost = resultMap.lastPost?default("")>
    <#assign lastGroupPost = '[' + lastGroup + '] ' + lastPost>
    <#assign lastPosition = resultMap.lastPosition?default("")>
    <#assign newGroup = resultMap.newGroup?default("")>
    <#assign newPost = resultMap.newPost?default("")>
    <#assign newGroupPost = newGroup + ',' + newPost>
    <#assign newPosition = resultMap.newPosition?default("")>
    <#assign changeType = resultMap.changeType?default("")>
    <#assign changeDate = resultMap.changeDate?default("")>
    <#assign changeReason = resultMap.changeReason?default("")>
    <#assign remarks = resultMap.remarks?default("")>
<#else>
    <#assign lastGroup = ''>
    <#assign lastPost = ''>
    <#assign lastGroupPost = ''>
</#if>
<script language="JavaScript">
    console.log($("input[name='partyId']").val());
    $(function () {
        $('input[name="partyId"]').data("promptPosition", "bottomLeft");
    })
    function showLookupInfo() {
        var partyId = $("input[name='partyId']").val();
        $.ajax({
            type: 'POST',
            url: "TransactionRecord",
            async: true,
            data:{partyId: partyId},
            dataType: 'json',
            success: function (data) {
                if(data.data.flag == '1'){
                    $("#lastGroup").html('['+ data.data.lastGroup +'] '+data.data.lastPost);
                    /*$("#lastPost").html(data.data.lastPost);*/
                    $("#workerSn").html(data.data.workerSn);
                    $("select[name='lastPosition'] option").each(function () {
                        if(data.data.lastPosition == $(this).val()){
                            $(this).attr('selected',"true");
                        }
                    })
                }
            }
        });
    }
</script>
<form method="post" action="" id="TransactionRecordCreate" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="TransactionRecordCreate">
    <input type="hidden" name="postId" value="${postId?default('')}" id="TransactionRecordCreate_postId">
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label for="TransactionRecordCreate_fullName" id="TransactionRecordCreate_fullName_title"><b class="requiredAsterisk">*</b>姓名：</label>  </td>
            <td width="40%"  class="jqv">
            <#if postId?has_content>
                <span>${fullName?default("")}</span>
                <input type="hidden" name ="partyId" value="${partyId?default('')}">
            <#else>
                <#if partyId?has_content>
                    <span>${fullName?default('')}</span>
                    <input type="hidden" name ="partyId" value="${partyId?default('')}">
                <#else>
                    <@chooser name="partyId" chooserValue="${partyId?default('')}" callback="showLookupInfo" required=true/>
                </#if>
            </#if>
            </td>
            <td class="label">
                <label for="TransactionRecordCreate_workerSn" id="TransactionRecordCreate_workerSn_title">工号：</label>
            </td>
            <td colspan="2">
                <span id="workerSn">${workerSn?default('')}</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TransactionRecordCreate_lastGroup" id="TransactionRecordCreate_lastGroup_title">原部门岗位：</label>  </td>
            <td>
                <span id="lastGroup">${lastGroupPost?default('')}</span>
            </td>
            <td class="label">
                <label for="TransactionRecordCreate_newGroup" id="TransactionRecordCreate_newGroup_title"><b class="requiredAsterisk">*</b>新部门岗位：</label>  </td>
            <td colspan="2"  class="jqv">
            <@chooser chooserType="LookupDepartment" name="newGroup" strTab="岗位选择器" chooserValue="${newGroup?default('')}" dropDown=true
                dropDownType="LookupOccupation" dropValue="${newGroupPost?default('')}" required=true secondary=true/>
                <#--<@chooser chooserType="LookupDepartment" name="newGroup" chooserValue="${newGroup?default('')}" required=true/>-->
            </td>
        </tr>
        <#--<tr>-->
            <#--<td class="label">-->
                <#--<label for="TransactionRecordCreate_lastPost" id="TransactionRecordCreate_lastPost_title">原岗位：</label>  </td>-->
            <#--<td>-->
                <#--<span id="lastPost">${lastPost?default('')}</span>-->
            <#--</td>-->
            <#--<td class="label">-->
                <#--<label for="TransactionRecordCreate_newPost" id="TransactionRecordCreate_newPost_title"><b class="requiredAsterisk">*</b>新岗位：</label>  </td>-->
            <#--<td colspan="2">-->
                <#--&lt;#&ndash;<@chooser chooserType="LookupOccupation" name="newPost" chooserValue="${newPost?default('')}" required=true/>&ndash;&gt;-->
            <#--</td>-->
        <#--</tr>-->
        <tr>
            <td class="label">
                <label for="TransactionRecordCreate_lastPosition" id="TransactionRecordCreate_lastPosition_title">原职级：</label>  </td>
            <td>
            <#assign lastPositionList = delegator.findByAnd("Enumeration", {"enumTypeId" : "POSITION_LEVEL"})>
                <select name="lastPosition" id="TransactionRecordCreate_lastPosition" size="1">
                <#if lastPositionList?has_content>
                    <option value="">--请选择--</option>
                    <#list lastPositionList as list>
                        <#if lastPosition?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>

                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="TransactionRecordCreate_lastPosition" id="TransactionRecordCreate_lastPosition_title"><b class="requiredAsterisk">*</b>新职级：</label>  </td>
            <td colspan="2" class="jqv">
            <#assign newPositionList = delegator.findByAnd("Enumeration", {"enumTypeId" : "POSITION_LEVEL"})>
                <select name="newPosition" id="TransactionRecordCreate_newPosition" size="1" class="validate[required]">
                <#if newPositionList?has_content>
                    <#list newPositionList as list>
                        <#if newPosition?default('') == list.enumId>
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
                <label for="TransactionRecordCreate_changeType" id="TransactionRecordCreate_changeType_title"><b class="requiredAsterisk">*</b>调动类型：</label>
            </td>
            <td  class="jqv">
            <#assign changeTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "TRANSFER"})>
                <select name="changeType" id="TransactionRecordCreate_changeType" size="1" class="validate[required]">
                    <option value="">--请选择--</option>
                <#if changeTypeList?has_content>
                    <#list changeTypeList as list>
                        <#if changeType?default('') == list.enumId>
                            <option value="${list.enumId}" selected>${list.description}</option>
                        <#else >
                            <option value="${list.enumId}">${list.description}</option>
                        </#if>
                    </#list>
                </#if>
                </select>
            </td>
            <td class="label">
                <label for="TransactionRecordCreate_changeDate" id="TransactionRecordCreate_changeDate_title"><b class="requiredAsterisk">*</b>调动时间：</label>  </td>
            <td colspan="2"  class="jqv">
            <@htmlTemplate.renderDateTimeField name="changeDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${changeDate?default(.now)}" size="25" maxlength="30" id="TransactionRecordCreate_changeDate" dateType="dateFmt:'yyyy-MM-dd HH:mm:ss'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TransactionRecordCreate_changeReason" id="TransactionRecordCreate_changeReason_title"><b class="requiredAsterisk">*</b>调动原因：</label>  </td>
            <td colspan="4"  class="jqv">
                <input type="text" name ="changeReason" value="${changeReason?default("")}" class="validate[required,maxSize[20]]">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TransactionRecordCreate_remarks" id="TransactionRecordCreate_remarks_title">备注：</label>  </td>
            <td colspan="4">
                <input type="text" name ="remarks" value="${remarks?default("")}">
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="TransactionRecordCreate_createButton" id="TransactionRecordCreate_createButton_title">操作</label>  </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.savePostChange('${resultMap.url}')" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>