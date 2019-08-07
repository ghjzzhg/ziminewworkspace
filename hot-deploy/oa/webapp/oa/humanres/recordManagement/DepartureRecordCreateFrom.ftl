<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<#if resultMap?has_content>
    <#assign partyId = resultMap.partyId?default("")>
    <#assign jobState = resultMap.jobState?default("")>
    <#assign departureId = resultMap.departureId?default("")>
    <#assign workerSn = resultMap.workerSn?default("")>
    <#assign fullName = resultMap.fullName?default("")>
    <#assign groupName = resultMap.groupName?default("")>
    <#assign occupationName = resultMap.occupationName?default("")>
    <#assign departureDate = resultMap.departureDate?default("")>
    <#assign departureType = resultMap.departureType?default("")>
    <#assign departureReason = resultMap.departureReason?default("")>
    <#assign transferContent = resultMap.transferContent?default("")>
    <#assign remarks = resultMap.remarks?default("")>
    <#assign fileList = resultMap.fileList?default("")>
    <#assign fileId = resultMap.fileId?default("")>
    <#assign url = resultMap.url?default("")>
</#if>
<script language="JavaScript">
    $(function(){
        $("#fileId").val('${fileId?default('')}');
        $('input[name="fullName1"]').data("promptPosition", "bottomLeft");
    })
    function searchPartyInfo(){
        var partyId = $("input[name='fullName1']").val();
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
<form method="post" action="" id="DepartureRecordCreate" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="DepartureRecordCreate">
    <input type="hidden" name="departureId" value="${departureId?default('')}" id="DepartureRecordCreate_departureId">
    <table cellspacing="0" class="basic-table">
        <tbody><tr>
            <td class="label">
                <label for="DepartureRecordCreate_fullName" id="DepartureRecordCreate_fullName_title"><b class="requiredAsterisk">*</b>姓名：</label>  </td>
            <td width="40%" class="jqv">
            <#if departureId?has_content>
                <span>${fullName?default("")}</span>
                <input type="hidden" name ="fullName1" value="${partyId?default('')}">
            <#elseif type?has_content && type.equals("1")>
                <@chooser name="fullName1" chooserValue="${partyId?default('')}" callback="searchPartyInfo" required=true/>
            <#else>
                <span>${fullName?default('')}</span>
                <input type="hidden" name ="fullName1" value="${partyId?default('')}">
            </#if>
            </td>
            <td class="label">
                <label for="DepartureRecordCreate_workerSn" id="DepartureRecordCreate_workerSn_title">工号：</label>
            </td>
            <td>
                <span id="workerSn">${workerSn?default('')}</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="DepartureRecordCreate_groupName" id="DepartureRecordCreate_groupName_title">部门：</label>  </td>
            <td>
                <span id="groupName">${groupName?default('')}</span>
                <input type="hidden" id="jobState" name ="jobState" value="">
            </td>
            <td class="label">
                <label for="DepartureRecordCreate_occupationName" id="DepartureRecordCreate_occupationName_title">岗位：</label>  </td>
            <td colspan="4">
                <span id="occupationName">${occupationName?default('')}</span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="DepartureRecordCreate_departureType" id="DepartureRecordCreate_departureType_title">离职类型：</label>  </td>
            <td>
                <span class="ui-widget">
                  <#assign departureTypeList = delegator.findByAnd("Enumeration", {"enumTypeId" : "DEPARTURE_TYPE"})>
                      <select name="departureType" id="DepartureRecordCreate_departureType" size="1">
                      <#if departureTypeList?has_content>
                          <#list departureTypeList as list>
                              <#if departureType?default('') == list.enumId>
                                  <option value="${list.enumId}" selected>${list.description}</option>
                              <#else >
                                  <option value="${list.enumId}">${list.description}</option>
                              </#if>

                          </#list>
                      </#if>
                      </select>
                </span>
            </td>
            <td class="label">
                <label for="DepartureRecordCreate_departureDate" id="DepartureRecordCreate_departureDate_title"><b class="requiredAsterisk">*</b>离职日期：</label>  </td>
            <td class="jqv">
            <@htmlTemplate.renderDateTimeField name="departureDate" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
            value="${departureDate?default(.now)}" size="25" maxlength="30" id="DepartureRecordCreate_departureDate" dateType="" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
            timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="DepartureRecordCreate_departureReason" id="DepartureRecordCreate_departureReason_title"><b class="requiredAsterisk">*</b>离职原因：</label>  </td>
            <td colspan="7" class="jqv">
                <textarea name="departureReason" cols="60" rows="3" id="DepartureRecordCreate_departureReason" maxlength="20" class="validate[required]">${departureReason?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="DepartureRecordCreate_transferContent" id="DepartureRecordCreate_transferContent_title"><b class="requiredAsterisk">*</b>工作交接内容：</label>  </td>
            <td colspan="7" class="jqv">
                <textarea name="transferContent" cols="60" rows="3" id="DepartureRecordCreate_transferContent" maxlength="125" class="validate[required]">${transferContent?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="DepartureRecordCreate_remarks" id="DepartureRecordCreate_remarks_title">备注：</label>  </td>
            <td colspan="7">
                <textarea name="remarks" cols="60" rows="3" id="DepartureRecordCreate_remarks">${remarks?default('')}</textarea>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="DepartureRecordCreate_accessory" id="DepartureRecordCreate_accessory_title">附件</label>  </td>
            <td colspan="7">
                <#include "component://content/webapp/content/showUploadFileView.ftl"/>
                <div>
                    <@showFileList id="accessory_div" hiddenId="fileId" fileList=fileList?default('')/>
                </div>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="DepartureRecordCreate_createButton" id="DepartureRecordCreate_createButton_title">操作</label>  </td>
            <td colspan="4">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:$.recordManagement.saveDeparture('${url?default('')}');" title="操作">保存</a>
            </td>
        </tr>
        </tbody>
    </table>
</form>